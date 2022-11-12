package indi.lby.marketanalysis.spider.pipeline;


import indi.lby.marketanalysis.model.EastMoneyFastPriceModel;
import indi.lby.marketanalysis.redisrepository.RedisEastMoneyFastPriceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.PageModelPipeline;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Slf4j
@Component
@ConditionalOnProperty(name = "function.datamantance")
public class EastMoneyPricePipeline implements PageModelPipeline {

    @Autowired
    RedisEastMoneyFastPriceRepository redisEastMoneyFastPriceRepository;

    @PostConstruct
    public void init(){

    }
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    final String updatetime="EastUpdateTime";

    @Override
    public void process(Object o, Task task) {
        EastMoneyFastPriceModel eastMoneyFastPriceModel= (EastMoneyFastPriceModel)o;
        redisEastMoneyFastPriceRepository.save(eastMoneyFastPriceModel);
        stringRedisTemplate.opsForValue().set(updatetime, LocalDateTime.now().format(formatter));
    }
}
