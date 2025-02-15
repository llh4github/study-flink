package llh.studyflink.demo02;

import llh.studyflink.demo02.model.Order;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.jdbc.source.JdbcSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class PgsqlTaskDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Order> source = env.fromSource(jdbcSource(), Order.defaultWatermark(), "order-source");

        source.keyBy(ele -> true)
                .window(TumblingEventTimeWindows.of(Time.minutes(1L)))
                .reduce((a, b) -> {
                    Order order = new Order();
                    BigDecimal add = a.getAmount().add(b.getAmount());
                    order.setAmount(add);
                    order.setName("SUM");
                    order.setCreatedTime(a.getCreatedTime());
                    return order;
                })
                .print();
        env.execute("Watermark Demo");
    }


    private static JdbcSource<Order> jdbcSource() {
        return JdbcSource.<Order>builder()
                .setDBUrl("jdbc:postgresql://localhost:5432/flink")
                .setPassword("postgres")
                .setUsername("postgres")
                .setDriverName("org.postgresql.Driver")
                .setSql("select * from t_order ")
                .setResultExtractor(rs -> new Order(
                        rs.getInt("id"),
                        "",
                        rs.getBigDecimal("amount"),
                        rs.getTimestamp("created_time")
                ))
                .setTypeInformation(TypeInformation.of(Order.class))
                .build();
    }
}