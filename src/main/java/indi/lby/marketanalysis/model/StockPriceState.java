package indi.lby.marketanalysis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalTime;

@Data
@RedisHash("stockprice")
@NoArgsConstructor
@AllArgsConstructor
public class StockPriceState  implements Serializable {
    @Id
    @Indexed
    String symbol;

    LocalTime limitUpTime;
    LocalTime firstLimitUpTime;
    LocalTime limitDownTime;
    LocalTime firstLimitDownTime;
    LocalTime limitUpBreakTime;
    LocalTime limitDownBreakTime;
    boolean isUp;
    boolean isDown;
}
