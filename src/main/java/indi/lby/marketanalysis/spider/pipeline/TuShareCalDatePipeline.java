package indi.lby.marketanalysis.spider.pipeline;


import indi.lby.marketanalysis.entity.TradeCal;
import indi.lby.marketanalysis.repository.JpaTradeCalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

@Component
@ConditionalOnProperty(name = "function.datamantance")
public class TuShareCalDatePipeline implements Pipeline {

    @Autowired
    JpaTradeCalRepository jpaTradeCalRepository;


    @Override
    public void process(ResultItems resultItems, Task task) {
        List<TradeCal> tradeCalList=resultItems.get("items");
        jpaTradeCalRepository.saveAll(tradeCalList);
    }
}
