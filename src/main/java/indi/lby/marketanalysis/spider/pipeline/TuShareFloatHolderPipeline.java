package indi.lby.marketanalysis.spider.pipeline;


import indi.lby.marketanalysis.entity.*;
import indi.lby.marketanalysis.repository.*;
import indi.lby.marketanalysis.service.LianBanComputeService;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.misc.MultiMap;
import org.antlr.v4.runtime.misc.Triple;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.ArrayList;
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
        if(floatHolderList==null)return;
        ArrayList<Triple<Integer,Integer,Integer>> tmplist=new ArrayList<>();
        ArrayList<FloatHolder> floatHolderArrayList=new ArrayList<>();
        //找内部重复的
        for(int i=floatHolderList.size()-1;i>=0;i--){
            FloatHolder floatHolder=floatHolderList.get(i);
            Integer a=floatHolder.getSymbol().getId();
            Integer b=floatHolder.getHoldername().getId();
            Integer c=floatHolder.getEnddate().getId();
            Triple<Integer,Integer,Integer> myUnion=new Triple<>(a,b,c);
            if(tmplist.contains(myUnion)){
                floatHolderArrayList.add(floatHolder);
            }else{
                tmplist.add(myUnion);
            }
        }
        floatHolderList.removeAll(floatHolderArrayList);
        log.info("写入十大股东-数量:"+floatHolderList.size());
        jpaFloatHolderRepository.saveAll(floatHolderList);
//            for (FloatHolder floatHolder:floatHolderList) {
//                jpaFloatHolderRepository.mysave(floatHolder);
//            }
            //lianBanComputeService.computeLianBanHolderNewHolder(floatHolderList);
    }
}
