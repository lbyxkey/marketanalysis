package indi.lby.marketanalysis.spider.pipeline;

import indi.lby.marketanalysis.entity.ConceptStocks;
import indi.lby.marketanalysis.repository.JpaConceptStocksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

@Component
@ConditionalOnProperty(name = "function.datamantance")
public class THSConceptStockPipeline implements Pipeline {
    @Autowired
    JpaConceptStocksRepository jpaConceptStocksRepository;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<ConceptStocks> conceptStocksList=resultItems.get("conceptStocksList");
        jpaConceptStocksRepository.saveAll(conceptStocksList);
    }
}
