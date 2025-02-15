package llh.studyflink.randomdata.model;

import io.github.yindz.random.RandomSource;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(
        name = "t_order",
        indexes = {
                @Index(name = "t_order_create_time", columnList = "createdTime DESC"),
        }
)
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime createdTime;

    public static Order newRandom() {
        LocalDateTime pastTime = RandomSource.dateTimeSource().randomPastTime(1999);
        double v = RandomSource.numberSource().randomDouble(0.00d, 1_0000_0000.d);
        Order order = new Order();
        order.setCreatedTime(pastTime);
        order.setAmount(BigDecimal.valueOf(v));
        return order;
    }


    /**
     * 生成过去指定秒内的随机时间
     *
     * @param pastSeconds 过去多少秒内
     */
    public static Order newRandom(long pastSeconds) {
        LocalDateTime pastTime = RandomSource.dateTimeSource().randomPastTime(LocalDateTime.now(), pastSeconds);
        double v = RandomSource.numberSource().randomDouble(0.00d, 1_0000_0000.d);
        Order order = new Order();
        order.setCreatedTime(pastTime);
        order.setAmount(BigDecimal.valueOf(v));
        return order;
    }
}
