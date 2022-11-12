package indi.lby.marketanalysis.spider.pipeline;


import indi.lby.marketanalysis.entity.StockBasic;
import indi.lby.marketanalysis.repository.JpaStockBasicRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.HashSet;
import java.util.List;

@Component
@Slf4j
@ConditionalOnProperty(name = "function.datamantance")
public class TuShareStockBasicPipeline implements Pipeline {

    @Autowired
    JpaStockBasicRepository jpaStockBasicRepository;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<StockBasic> stockBasics=resultItems.get("items");
//        List<StockBasic> dbStockBasics= jpaStockBasicRepository.findAll();
//        HashSet<StockBasic> stockBasicSet=new HashSet<>(stockBasics);
//        HashSet<StockBasic> dbStockBasicSet=new HashSet<>(dbStockBasics);
//        HashSet<StockBasic> needDelete=new HashSet<>(dbStockBasicSet);
//        needDelete.removeAll(stockBasicSet);
//        log.info(needDelete.toString());
        if(stockBasics==null)return;
        log.info("刷新股票列表-股票数量:"+stockBasics.size());
        jpaStockBasicRepository.saveAll(stockBasics);
    }
}
