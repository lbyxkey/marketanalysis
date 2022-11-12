package indi.lby.marketanalysis.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import us.codecraft.webmagic.model.annotation.ExtractBy;

import java.io.Serializable;

@Data
@RedisHash("realtimeprice")
public class EastMoneyFastPriceModel implements Serializable {
    @ExtractBy(value = "$.data.f43",type = ExtractBy.Type.JsonPath)
    int now;
    @ExtractBy(value = "$.data.f44",type = ExtractBy.Type.JsonPath)
    int high;
    @ExtractBy(value = "$.data.f45",type = ExtractBy.Type.JsonPath)
    int low;
    @ExtractBy(value = "$.data.f46",type = ExtractBy.Type.JsonPath)
    int open;
    @ExtractBy(value = "$.data.f48",type = ExtractBy.Type.JsonPath)
    String amount;
    @ExtractBy(value = "$.data.f51",type = ExtractBy.Type.JsonPath)
    int limitup;
    @ExtractBy(value = "$.data.f52",type = ExtractBy.Type.JsonPath)
    int limitdown;
    @Id
    @Indexed
    @ExtractBy(value = "$.data.f57",type = ExtractBy.Type.JsonPath)
    String symbol;
    @ExtractBy(value = "$.data.f58",type = ExtractBy.Type.JsonPath)
    String name;
}
