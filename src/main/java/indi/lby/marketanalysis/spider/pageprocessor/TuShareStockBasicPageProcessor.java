package indi.lby.marketanalysis.spider.pageprocessor;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import indi.lby.marketanalysis.entity.StockBasic;
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
public class TuShareStockBasicPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(120).setDomain("api.tushare.pro").setCharset("utf-8");

    DateTimeFormatter dateTimeFormatter;
    @Value("${tushare.dateformat}")
    public void setDateTimeFormatter(String dateformat){
        dateTimeFormatter=DateTimeFormatter.ofPattern(dateformat);
    }

    @Override
    public void process(Page page) {
        //log.info(page.getJson().toString());
        ReadContext readContext=JsonPath.parse(page.getJson().toString());
        int code=readContext.read("$.code");
        if(code==0){
            ArrayList<String> fields=readContext.read("$.data.fields");
            ArrayList<ArrayList<Object>> items=readContext.read("$.data.items.*");
            ArrayList<StockBasic> stockBasicArrayList=new ArrayList<>();
            for (ArrayList<Object> item:items) {
                StockBasic stockBasic=new StockBasic();
                stockBasic.setTscode(item.get(fields.indexOf("ts_code")).toString());
                stockBasic.setSymbol(item.get(fields.indexOf("symbol")).toString());
                stockBasic.setName(item.get(fields.indexOf("name")).toString());
                stockBasic.setMarket(item.get(fields.indexOf("market")).toString());
                stockBasic.setListdate(Date.valueOf(LocalDate.parse(item.get(fields.indexOf("list_date")).toString(),
                        dateTimeFormatter)));
                stockBasicArrayList.add(stockBasic);
            }
            if(!stockBasicArrayList.isEmpty()){
                page.putField("items",stockBasicArrayList);
            }
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
