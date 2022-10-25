package indi.lby.marketanalysis.spider.pipeline;


import indi.lby.marketanalysis.entity.Daily;
import indi.lby.marketanalysis.repository.JpaDailyRepository;
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
public class TuShareDailyPipeline implements Pipeline {

    @Autowired
    JpaDailyRepository jpaDailyRepository;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<Daily> dailies=resultItems.get("items");
        if(dailies!=null){
            log.info("写入日线:"+dailies.size());
            jpaDailyRepository.saveAll(dailies);
        }
    }
}
