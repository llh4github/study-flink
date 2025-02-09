package demo01;

import llh.studyflink.WordCount;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


public class WordCountExample {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        var filePath = "./word-count.txt";
        if(args.length > 0){
            filePath = args[0];
        }
        Path path = new Path(filePath);
        FileSource<String> fileSource = FileSource
                .forRecordStreamFormat(new TextLineInputFormat(), path)
                .build();
        var textStream = environment.fromSource(fileSource, WatermarkStrategy.noWatermarks(), "file-source");

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
