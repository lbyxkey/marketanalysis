package indi.lby.marketanalysis.spider.pipeline;


import indi.lby.marketanalysis.model.EastMoneyFastPriceModel;
import indi.lby.marketanalysis.redisrepository.RedisEastMoneyFastPriceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.PageModelPipeline;

import javax.annotation.PostConstruct;


@Slf4j
@Component
@ConditionalOnProperty(name = "function.datamantance")
public class EastMoneyPricePipeline implements PageModelPipeline {

    @Autowired
    RedisEastMoneyFastPriceRepository redisEastMoneyFastPriceRepository;

    @PostConstruct
    public void init(){

    }

    @Override
    public void process(Object o, Task task) {
        EastMoneyFastPriceModel eastMoneyFastPriceModel= (EastMoneyFastPriceModel)o;
        redisEastMoneyFastPriceRepository.save(eastMoneyFastPriceModel);
    }
}
