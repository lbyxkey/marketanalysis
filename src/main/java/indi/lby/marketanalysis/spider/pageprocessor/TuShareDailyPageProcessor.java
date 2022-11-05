package indi.lby.marketanalysis.spider.pageprocessor;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import indi.lby.marketanalysis.entity.Daily;
import indi.lby.marketanalysis.entity.StockBasic;
import indi.lby.marketanalysis.entity.TradeCal;
import indi.lby.marketanalysis.repository.JpaDailyRepository;
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

    private Site site =
            Site.me().setRetryTimes(3).setSleepTime(120).setDomain("api.tushare.pro").setCharset("utf-8").setTimeOut(10000);
    @Autowired
    JpaStockBasicRepository jpaStockBasicRepository;

    @Autowired
    JpaTradeCalRepository jpaTradeCalRepository;

    @Autowired
    JpaDailyRepository jpaDailyRepository;
    DateTimeFormatter dateTimeFormatter;
    @Value("${tushare.dateformat}")
    public void setDateTimeFormatter(String dateformat){
        dateTimeFormatter=DateTimeFormatter.ofPattern(dateformat);
    }

    @Override
    public void process(Page page) {
        ReadContext readContext=JsonPath.parse(page.getJson().toString());
        int code=readContext.read("$.code");
        ArrayList<String> wrongList=new ArrayList<>();
        ArrayList<String> wrongamountList=new ArrayList<>();
        if(code==0){
            ArrayList<String> fields=readContext.read("$.data.fields");
            ArrayList<ArrayList<Object>> items=readContext.read("$.data.items.*");
            ArrayList<Daily> dailyArrayList=new ArrayList<>();
            for (ArrayList<Object> item:items) {
                String symbolStr=item.get(fields.indexOf("ts_code")).toString();
                StockBasic symbol=
                        jpaStockBasicRepository.findStockBasicByTscode(symbolStr);
                if(symbol==null) {
                    wrongList.add(symbolStr);
                    continue;
                }
                LocalDate date=LocalDate.parse(item.get(fields.indexOf("trade_date")).toString(),
                        dateTimeFormatter);
                TradeCal tradeCal= jpaTradeCalRepository.findByCaldate(date);
                Daily daily=jpaDailyRepository.findBySymbolAndTradedate(symbol,tradeCal);
                if(daily!=null)continue;
                daily=new Daily();
                daily.setSymbol(symbol);
                daily.setTradedate(tradeCal);
                daily.setOpen((Double) item.get(fields.indexOf("open")));
                daily.setHigh((Double) item.get(fields.indexOf("high")));
                daily.setLow((Double) item.get(fields.indexOf("low")));
                daily.setClose((Double) item.get(fields.indexOf("close")));
                daily.setPreclose((Double) item.get(fields.indexOf("pre_close")));
                daily.setChange((Double) item.get(fields.indexOf("change")));
                daily.setPctchg((Double) item.get(fields.indexOf("pct_chg")));
                daily.setVol((Double) item.get(fields.indexOf("vol")));
                Object amountObject=item.get(fields.indexOf("amount"));
                if(amountObject==null){
                    wrongamountList.add(symbolStr);
                    continue;
                }
                daily.setAmount((Double) amountObject);
                dailyArrayList.add(daily);
            }
            log.warn("wrong symbol :"+wrongList);
            log.warn("wrong amount:"+wrongamountList);
            log.warn("write to DB:"+dailyArrayList.size());
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
