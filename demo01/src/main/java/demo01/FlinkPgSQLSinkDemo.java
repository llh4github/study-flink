package demo01;

import demo01.models.User;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcInputFormat;
import org.apache.flink.connector.jdbc.source.JdbcSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.LongValue;
import org.apache.flink.util.LongValueSequenceIterator;

public class FlinkPgSQLSinkDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<User> source = env.fromSource(jdbcSource(), WatermarkStrategy.noWatermarks(), "pgsql-source");
        source.print();
        env.execute();
    }

    private static JdbcSource<User> jdbcSource() {
        return JdbcSource.<User>builder()
                .setDBUrl("jdbc:postgresql://localhost:5432/flink")
                .setPassword("postgres")
                .setUsername("postgres")
                .setDriverName("org.postgresql.Driver")
                .setSql("select * from users")
                .setResultExtractor(rs -> new User(
                          rs.getInt("id"),
                          rs.getString("name")
                  ))
                .setTypeInformation(TypeInformation.of(User.class))
                .build();
    }
}
