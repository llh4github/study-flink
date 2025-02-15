package llh.studyflink.demo02.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@NoArgsConstructor
public class Order {
    private Integer id;

    private BigDecimal amount;

    private LocalDateTime createdTime;

    public static WatermarkStrategy<Order> defaultWatermark() {
        return WatermarkStrategy.<Order>forBoundedOutOfOrderness(Duration.ofMinutes(10))
                .withTimestampAssigner((event, timestamp) -> event.getCreatedTime().toEpochSecond(ZoneOffset.UTC));
    }
}
