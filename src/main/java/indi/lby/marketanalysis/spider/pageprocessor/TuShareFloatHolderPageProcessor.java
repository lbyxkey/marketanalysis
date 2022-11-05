package indi.lby.marketanalysis.spider.pageprocessor;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import indi.lby.marketanalysis.entity.FloatHolder;
import indi.lby.marketanalysis.entity.HolderList;
import indi.lby.marketanalysis.entity.StockBasic;
import indi.lby.marketanalysis.entity.TradeCal;
import indi.lby.marketanalysis.repository.JpaFloatHolderRepository;
import indi.lby.marketanalysis.repository.JpaHolderListRepository;
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
    private Site site = Site.me().setRetryTimes(10).setSleepTime(6000).setDomain("api.tushare.pro").setCharset("utf-8").setTimeOut(30000);

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

    @Autowired
    JpaHolderListRepository jpaHolderListRepository;

    @Autowired
    JpaFloatHolderRepository jpaFloatHolderRepository;
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
                StockBasic symbol=
                        jpaStockBasicRepository.findStockBasicByTscode(item.get(fields.indexOf("ts_code")).toString());
                if(symbol==null)continue;
                String holdername=item.get(fields.indexOf("holder_name")).toString();
                HolderList holderList=jpaHolderListRepository.findHolderListByHoldername(holdername);
                if(holderList==null){
                    holderList=new HolderList();
                    holderList.setHoldername(holdername);
                    jpaHolderListRepository.save(holderList);
                }
                HolderList newholderList=jpaHolderListRepository.findHolderListByHoldername(holdername);
                TradeCal endDate=jpaTradeCalRepository.findByCaldate(LocalDate.parse(item.get(fields.indexOf("end_date")).toString(),
                        dateTimeFormatter));
                FloatHolder floatHolder=
                        jpaFloatHolderRepository.findFloatHolderBySymbolAndEnddateAndHoldername(symbol,endDate,
                                newholderList);
                if(floatHolder!=null){
                    continue;
                }
                floatHolder=new FloatHolder();
                floatHolder.setSymbol(symbol);
                floatHolder.setHoldername(newholderList);
                floatHolder.setEnddate(endDate);
                Double hold_amount=(Double)item.get(fields.indexOf("hold_amount"));
                floatHolder.setHoldamount(hold_amount.longValue());
                floatHolder.setAnndate(jpaTradeCalRepository.findByCaldate(LocalDate.parse(item.get(fields.indexOf("ann_date")).toString(),
                        dateTimeFormatter)));
                floatHolderArrayList.add(floatHolder);
            }
            if(!floatHolderArrayList.isEmpty()){
                page.putField("items",floatHolderArrayList);
            }
            //boolean hasnext=readContext.read("$.data.has_more");
            if(items.size()==5000){
                offset+=items.size();
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
