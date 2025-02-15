package llh.studyflink.randomdata.gen;

import llh.studyflink.randomdata.dao.OrderDao;
import llh.studyflink.randomdata.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest
public class OrderDataGenTest {
    @Autowired
    private OrderDao dao;

    @Test
    void genOldTimeData(){
        dao.saveAll(randomData());
    }

    @Test
    void genJustNowData(){
        List<Order> list = Stream.generate(()->Order.newRandom(10L))
                .limit(1_000)
                .toList();
        dao.saveAll(list);
    }



    List<Order> randomData(){
        dao.save(Order.newRandom());
        return Stream.generate(Order::newRandom)
                .limit(1_000)
                .sorted(Comparator.comparing(Order::getCreatedTime))
                .toList();

    }
}
