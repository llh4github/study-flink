package llh.studyflink.demo02.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Integer id;

    private String name;

    private BigDecimal amount;

    private java.sql.Timestamp createdTime;

    public static WatermarkStrategy<Order> defaultWatermark() {
        return WatermarkStrategy.<Order>forBoundedOutOfOrderness(Duration.ofMinutes(10))
                .withTimestampAssigner((event, timestamp) -> event.getCreatedTime().getTime());
    }
}
