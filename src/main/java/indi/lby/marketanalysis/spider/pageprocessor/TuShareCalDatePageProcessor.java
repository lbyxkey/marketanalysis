package indi.lby.marketanalysis.spider.pageprocessor;


import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import indi.lby.marketanalysis.entity.TradeCal;
import lombok.extern.slf4j.Slf4j;
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
public class TuShareCalDatePageProcessor  implements PageProcessor {
    DateTimeFormatter dateTimeFormatter;
    @Value("${tushare.dateformat}")
    public void setDateTimeFormatter(String dateformat){
        dateTimeFormatter=DateTimeFormatter.ofPattern(dateformat);
    }

    private Site site = Site.me().setRetryTimes(3).setSleepTime(500).setDomain("api.tushare.pro").setCharset("utf-8");
    @Override
    public void process(Page page) {
        ReadContext readContext=JsonPath.parse(page.getJson().toString());
        int code=readContext.read("$.code");
        if(code==0){
            ArrayList<String> fields=readContext.read("$.data.fields");
            ArrayList<ArrayList<Object>> items=readContext.read("$.data.items.*");
            ArrayList<TradeCal> tradeCalList=new ArrayList<>();
            for (ArrayList<Object> item:items) {
                TradeCal tradeCal=new TradeCal();
                tradeCal.setCaldate(LocalDate.parse(item.get(fields.indexOf("cal_date")).toString(),
                        dateTimeFormatter));
                int is_openint= (int) item.get(fields.indexOf("is_open"));
                tradeCal.setIsopen(is_openint==1);
                if(item.get(fields.indexOf("pretrade_date"))==null){
                    tradeCal.setPretradedate(null);
                }else{
                    tradeCal.setPretradedate(LocalDate.parse(item.get(fields.indexOf("pretrade_date")).toString(),
                            dateTimeFormatter));
                }
                tradeCalList.add(tradeCal);
            }
            page.putField("items",tradeCalList);
            //boolean hasnext=readContext.read("$.data.has_more");
        }else{
            String msg=page.getJson().jsonPath("$.msg").toString();
            log.warn("更新日历出错，代码:"+code+",信息:"+msg);
        }
        //log.info(page.getJson().toString());
    }

    @Override
    public Site getSite() {
        return site;
    }
}
