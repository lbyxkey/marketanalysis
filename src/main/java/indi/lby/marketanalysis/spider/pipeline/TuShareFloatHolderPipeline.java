package indi.lby.marketanalysis.spider.pipeline;


import indi.lby.marketanalysis.entity.*;
import indi.lby.marketanalysis.repository.*;
import indi.lby.marketanalysis.service.LianBanComputeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

@Component
@Slf4j
@ConditionalOnProperty(name = "function.datamantance")
public class TuShareFloatHolderPipeline implements Pipeline {

    @Autowired
    JpaFloatHolderRepository jpaFloatHolderRepository;

    @Autowired
    LianBanComputeService lianBanComputeService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<FloatHolder> floatHolderList=resultItems.get("items");
        if(floatHolderList!=null){
            log.info("写入十大股东-数量:"+floatHolderList.size());
            jpaFloatHolderRepository.saveAll(floatHolderList);
            lianBanComputeService.computeLianBanHolderNewHolder(floatHolderList);
        }
    }
}
