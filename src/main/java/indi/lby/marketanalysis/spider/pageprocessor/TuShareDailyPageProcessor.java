package indi.lby.marketanalysis.spider.pageprocessor;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import indi.lby.marketanalysis.entity.Daily;
import indi.lby.marketanalysis.entity.TradeCal;
import indi.lby.marketanalysis.repository.JpaStockBasicRepository;
import indi.lby.marketanalysis.repository.JpaTradeCalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


@Component
@Slf4j
@ConditionalOnProperty(name = "function.datamantance")
public class TuShareDailyPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(500).setDomain("api.tushare.pro").setCharset("utf-8");
    @Autowired
    JpaStockBasicRepository jpaStockBasicRepository;

    @Autowired
    JpaTradeCalRepository jpaTradeCalRepository;
    DateTimeFormatter dateTimeFormatter;
    @Value("${tushare.dateformat}")
    public void setDateTimeFormatter(String dateformat){
        dateTimeFormatter=DateTimeFormatter.ofPattern(dateformat);
    }

    @Override
    public void process(Page page) {
        ReadContext readContext=JsonPath.parse(page.getJson().toString());
        int code=readContext.read("$.code");
        if(code==0){
            ArrayList<String> fields=readContext.read("$.data.fields");
            ArrayList<ArrayList<Object>> items=readContext.read("$.data.items.*");
            ArrayList<Daily> dailyArrayList=new ArrayList<>();
            for (ArrayList<Object> item:items) {
                Daily daily=new Daily();
                daily.setSymbol(jpaStockBasicRepository.findStockBasicByTscode(item.get(fields.indexOf("ts_code")).toString()));
                LocalDate date=LocalDate.parse(item.get(fields.indexOf("trade_date")).toString(),
                        dateTimeFormatter);
                TradeCal tradeCal= jpaTradeCalRepository.findByCaldate(date);
                daily.setTradedate(tradeCal);
                daily.setOpen((Double) item.get(fields.indexOf("open")));
                daily.setHigh((Double) item.get(fields.indexOf("high")));
                daily.setLow((Double) item.get(fields.indexOf("low")));
                daily.setClose((Double) item.get(fields.indexOf("close")));
                daily.setPreclose((Double) item.get(fields.indexOf("pre_close")));
                daily.setChange((Double) item.get(fields.indexOf("change")));
                daily.setPctchg((Double) item.get(fields.indexOf("pct_chg")));
                daily.setVol((Double) item.get(fields.indexOf("vol")));
                daily.setAmount((Double) item.get(fields.indexOf("amount")));
                dailyArrayList.add(daily);
            }
            page.putField("items",dailyArrayList);
            //boolean hasnext=readContext.read("$.data.has_more");
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
