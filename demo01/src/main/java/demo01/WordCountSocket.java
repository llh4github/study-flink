package demo01;

import llh.studyflink.WordCount;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


/**
 * 从socket端口读取数据进行计词。
 * nc -lvp 9999
 * nc -lk 9999
 */
public class WordCountSocket {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> textStream = environment.socketTextStream("0.0.0.0", 9999);
        var words = textStream.flatMap((FlatMapFunction<String, String>) (sentence, collector) -> {
            String[] split = sentence.split("\\s+");
            for (String word : split) {
                collector.collect(word);
            }
        }).returns(TypeInformation.of(String.class)); // 显式指定返回类型为 String
        var wordCounts = words.map((MapFunction<String, WordCount>) s -> new WordCount(s, 1))
                .keyBy((KeySelector<WordCount, String>) WordCount::getWord)
                .sum("count");
        wordCounts.print();
        environment.execute("Word Count Job");
    }

}
