package indi.lby.marketanalysis.spider.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import indi.lby.marketanalysis.spider.tusharejson.CalDateJsonTemplete;
import indi.lby.marketanalysis.spider.tusharejson.DailyJsonTemplete;
import indi.lby.marketanalysis.spider.tusharejson.FloatHolderJsonTemplete;
import indi.lby.marketanalysis.spider.tusharejson.StockBasicJsonTemplete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Service
@ConditionalOnProperty(name = "function.datamantance")
public class TuShareJsonMakerService {

    @Value("${tushare.url}")
    String tuShareURL;

    @Value("${tushare.token}")
    String token;

    DateTimeFormatter dateTimeFormatter;
    @Value("${tushare.dateformat}")
    public void setDateTimeFormatter(String dateformat){
        dateTimeFormatter=DateTimeFormatter.ofPattern(dateformat);
    }

    @Autowired
    ObjectMapper objectMapper;
    public String getCalDateJson(LocalDate startdate) throws JsonProcessingException {
        CalDateJsonTemplete calDateJsonTemplete=new CalDateJsonTemplete();
        calDateJsonTemplete.setToken(token);
        if(startdate!=null){
            calDateJsonTemplete.getParams().setStart_date(dateTimeFormatter.format(startdate));
        }
        return objectMapper.writeValueAsString(calDateJsonTemplete);
    }

    public String getStockBasicJson() throws JsonProcessingException {
        StockBasicJsonTemplete stockBasicJsonTemplete=new StockBasicJsonTemplete();
        stockBasicJsonTemplete.setToken(token);
        return objectMapper.writeValueAsString(stockBasicJsonTemplete);
    }

    public String getDailyJson(@NonNull LocalDate tradedate) throws JsonProcessingException {
        DailyJsonTemplete dailyJsonTemplete=new DailyJsonTemplete();
        dailyJsonTemplete.setToken(token);
        dailyJsonTemplete.getParams().setTrade_date(dateTimeFormatter.format(tradedate));
        return objectMapper.writeValueAsString(dailyJsonTemplete);
    }

    public String getFloatHolderJson(@NonNull LocalDate startDate,int offset) throws JsonProcessingException {
        FloatHolderJsonTemplete floatHolderJsonTemplete=new FloatHolderJsonTemplete();
        floatHolderJsonTemplete.setToken(token);
        floatHolderJsonTemplete.getParams().setStart_date(dateTimeFormatter.format(startDate));
        floatHolderJsonTemplete.getParams().setOffset(String.valueOf(offset));
        return objectMapper.writeValueAsString(floatHolderJsonTemplete);
    }

    public Request getRequest(String requestBody){
        Request request = new Request(tuShareURL);
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.json(requestBody, "utf-8"));
        return request;
    }
}
