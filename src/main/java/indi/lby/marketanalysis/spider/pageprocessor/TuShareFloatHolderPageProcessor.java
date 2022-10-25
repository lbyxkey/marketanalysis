package indi.lby.marketanalysis.spider.pageprocessor;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import indi.lby.marketanalysis.entity.FloatHolder;
import indi.lby.marketanalysis.repository.JpaStockBasicRepository;
import indi.lby.marketanalysis.repository.JpaTradeCalRepository;
import indi.lby.marketanalysis.spider.service.TuShareJsonMakerService;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Component
@Slf4j
@Setter
@ConditionalOnProperty(name = "function.datamantance")
public class TuShareFloatHolderPageProcessor implements PageProcessor {

    @Autowired
    JpaStockBasicRepository jpaStockBasicRepository;
    private Site site = Site.me().setRetryTimes(3).setSleepTime(6000).setDomain("api.tushare.pro").setCharset("utf-8");

    @Autowired
    TuShareJsonMakerService tuShareJsonMakerService;
    LocalDate maxdate;
    int offset;
    DateTimeFormatter dateTimeFormatter;
    @Autowired
    JpaTradeCalRepository jpaTradeCalRepository;
    @Value("${tushare.dateformat}")
    public void setDateTimeFormatter(String dateformat){
        dateTimeFormatter=DateTimeFormatter.ofPattern(dateformat);
    }

    @SneakyThrows
    @Override
    public void process(Page page) {
        //log.info(page.getJson().toString());
        ReadContext readContext=JsonPath.parse(page.getJson().toString());
        int code=readContext.read("$.code");
        if(code==0){
            ArrayList<String> fields=readContext.read("$.data.fields");
            ArrayList<ArrayList<Object>> items=readContext.read("$.data.items.*");
            ArrayList<FloatHolder> floatHolderArrayList=new ArrayList<>();
            for (ArrayList<Object> item:items) {
                FloatHolder floatHolder=new FloatHolder();
                floatHolder.setSymbol(jpaStockBasicRepository.findStockBasicByTscode(item.get(fields.indexOf("ts_code")).toString()));
                floatHolder.setHoldername(item.get(fields.indexOf("holder_name")).toString());
                Double hold_amount=(double)item.get(fields.indexOf("hold_amount"));
                floatHolder.setHoldamount(hold_amount.longValue());
                floatHolder.setAnndate(jpaTradeCalRepository.findByCaldate(LocalDate.parse(item.get(fields.indexOf("ann_date")).toString(),
                        dateTimeFormatter)));
                floatHolder.setEnddate(jpaTradeCalRepository.findByCaldate(LocalDate.parse(item.get(fields.indexOf("end_date")).toString(),
                        dateTimeFormatter)));
                floatHolderArrayList.add(floatHolder);
            }
            if(!floatHolderArrayList.isEmpty()){
                page.putField("items",floatHolderArrayList);
            }
            boolean hasnext=readContext.read("$.data.has_more");
            if(hasnext){
                offset+=floatHolderArrayList.size();
                Request request=tuShareJsonMakerService.getRequest(tuShareJsonMakerService.getFloatHolderJson(maxdate
                        ,offset));
                page.addTargetRequest(request);
            }
        }else{
            String msg=page.getJson().jsonPath("$.msg").toString();
            log.warn("更新股票列表出错，代码:"+code+",信息:"+msg);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}
