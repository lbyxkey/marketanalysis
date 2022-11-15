package indi.lby.marketanalysis.service;

import indi.lby.marketanalysis.entity.ConceptStocks;
import indi.lby.marketanalysis.entity.Daily;
import indi.lby.marketanalysis.entity.StockBasic;
import indi.lby.marketanalysis.entity.TradeCal;
import indi.lby.marketanalysis.model.EastMoneyFastPriceModel;
import indi.lby.marketanalysis.model.StockPriceState;
import indi.lby.marketanalysis.projections.ConceptResultStatistics;
import indi.lby.marketanalysis.projections.SymbolAndAmount;
import indi.lby.marketanalysis.projections.SymbolAndTime;
import indi.lby.marketanalysis.redisrepository.RedisEastMoneyFastPriceRepository;
import indi.lby.marketanalysis.redisrepository.RedisStockPriceStateRepository;
import indi.lby.marketanalysis.repository.JpaConceptStocksRepository;
import indi.lby.marketanalysis.repository.JpaDailyRepository;
import indi.lby.marketanalysis.repository.JpaStockBasicRepository;
import indi.lby.marketanalysis.repository.JpaTradeCalRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@Getter
public class PriceService {
//    final String updatetime = "EastUpdateTime";
    @Autowired
    JpaStockBasicRepository jpaStockBasicRepository;
    @Autowired
    JpaDailyRepository jpaDailyRepository;
    @Autowired
    JpaTradeCalRepository jpaTradeCalRepository;
    @Autowired
    JpaConceptStocksRepository jpaConceptStocksRepository;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisEastMoneyFastPriceRepository redisEastMoneyFastPriceRepository;

    @Autowired
    RedisStockPriceStateRepository redisStockPriceStateRepository;
    List<StockBasic> stockBasicList = new ArrayList<>();

    @PostConstruct
    public void init() {
        initStockBasicList();
    }

    public void initStockBasicList() {
        stockBasicList = jpaStockBasicRepository.findAll();
    }

//    @NoArgsConstructor
//    @Data
//    class LimitUpAndDownTime{
//        LocalTime limitUpTime=null;
//        LocalTime firstLimitUpTime=null;
//        LocalTime limitDownTime=null;
//        LocalTime firstLimitDownTime=null;
//        LocalTime limitUpBreakTime=null;
//        LocalTime limitDownBreakTime=null;
//        boolean isUp=false;
//        boolean isDown=false;
//    }
//
//    Map<String,LimitUpAndDownTime> limitUpAndDownTimeMap=new HashMap<>();
    @Value("${price.fastup5}")
    double fastup5;
    List<StockBasic> limitUp = new ArrayList<>();
    List<StockBasic> limitDown = new ArrayList<>();
    List<StockBasic> limitUpBreak = new ArrayList<>();
    List<StockBasic> limitDownBreak = new ArrayList<>();
    List<StockBasic> fast5List = new ArrayList<>();
    List<ConceptResultStatistics> limitUpConceptCache=new ArrayList<>();
    List<ConceptResultStatistics> limitDownConceptCache=new ArrayList<>();
    List<ConceptResultStatistics> limitUpBreakConceptCache=new ArrayList<>();
    List<ConceptResultStatistics> limitDownBreakConceptCache=new ArrayList<>();
    List<ConceptResultStatistics> fast5ConceptCache=new ArrayList<>();
    List<SymbolAndAmount> fast5StocksCache=new ArrayList<>();
    List<SymbolAndTime> limitUpStocksCache=new ArrayList<>();
    List<SymbolAndTime> limitDownStocksCache=new ArrayList<>();
    List<SymbolAndTime> limitUpBreakStocksCache=new ArrayList<>();
    List<SymbolAndTime> limitDownBreakStocksCache=new ArrayList<>();
    Map<String,Double> lastAmount=new HashMap<>();


    public void newDayStart(){
        lastAmount.clear();
        for (StockBasic stockBasic : stockBasicList) {
            String symbol = stockBasic.getSymbol();
            StockPriceState stockPriceState=redisStockPriceStateRepository.findBySymbol(symbol);
            if(stockPriceState!=null){
                redisStockPriceStateRepository.delete(stockPriceState);
            }
        }
        for (StockBasic stockBasic : stockBasicList) {
            Daily daily=jpaDailyRepository.findFirstBySymbolOrderByTradedateDesc(stockBasic);
            if(daily!=null){
                lastAmount.put(stockBasic.getSymbol(),daily.getAmount());
            }
        }
    }

    public void computePriceCache() {
        this.newDayStart();
        limitUp.clear();
        limitDown.clear();
        limitUpBreak.clear();
        limitDownBreak.clear();
        fast5List.clear();
        for (StockBasic stockBasic : stockBasicList) {
            String symbol = stockBasic.getSymbol();
            EastMoneyFastPriceModel eastMoneyFastPriceModel = redisEastMoneyFastPriceRepository.findBySymbol(symbol);
            if (eastMoneyFastPriceModel == null||eastMoneyFastPriceModel.getNow()==0) continue;
            if (eastMoneyFastPriceModel.getNow() == eastMoneyFastPriceModel.getLimitup()) {
                limitUp.add(stockBasic);
            } else if (eastMoneyFastPriceModel.getNow() == eastMoneyFastPriceModel.getLimitdown()) {
                limitDown.add(stockBasic);
            }
            if (eastMoneyFastPriceModel.getHigh() == eastMoneyFastPriceModel.getLimitup() && eastMoneyFastPriceModel.getNow() < eastMoneyFastPriceModel.getLimitup()) {
                limitUpBreak.add(stockBasic);
            }
            if (eastMoneyFastPriceModel.getLow() == eastMoneyFastPriceModel.getLimitdown() && eastMoneyFastPriceModel.getNow() > eastMoneyFastPriceModel.getLimitdown()) {
                limitDownBreak.add(stockBasic);
            }
            if(1.0*eastMoneyFastPriceModel.getNow()>=fastup5*eastMoneyFastPriceModel.getPreclose()){
                fast5List.add(stockBasic);
            }
        }
    }

    private void computeLimitUpAndDownTime(){
        LocalTime now=LocalTime.now();
        //计算涨停
        for (StockBasic stockBasic : limitUp){
            String symbol=stockBasic.getSymbol();
            StockPriceState stockPriceState=redisStockPriceStateRepository.findBySymbol(symbol);
            if(stockPriceState==null){
                stockPriceState=new StockPriceState();
                stockPriceState.setSymbol(symbol);
                stockPriceState.setLimitUpTime(now);
                stockPriceState.setFirstLimitUpTime(now);
                stockPriceState.setUp(true);
                stockPriceState.setDown(false);
            }else{
                if(!stockPriceState.isUp()){
                    stockPriceState.setUp(true);
                    stockPriceState.setLimitUpTime(now);
                }
            }
            redisStockPriceStateRepository.save(stockPriceState);
        }
        //计算跌停时间
        for (StockBasic stockBasic : limitDown){
            String symbol=stockBasic.getSymbol();
            StockPriceState stockPriceState=redisStockPriceStateRepository.findBySymbol(symbol);
            if(stockPriceState==null){
                stockPriceState=new StockPriceState();
                stockPriceState.setSymbol(symbol);
                stockPriceState.setFirstLimitDownTime(now);
                stockPriceState.setLimitDownTime(now);
                stockPriceState.setUp(false);
                stockPriceState.setDown(true);
            }else{
                if(!stockPriceState.isDown()){
                    stockPriceState.setDown(true);
                    stockPriceState.setLimitDownTime(now);
                }
            }
            redisStockPriceStateRepository.save(stockPriceState);
        }
        //记录破板
        for (StockBasic stockBasic : limitUpBreak){
            String symbol=stockBasic.getSymbol();
            StockPriceState stockPriceState=redisStockPriceStateRepository.findBySymbol(symbol);
            if(stockPriceState==null){
                stockPriceState=new StockPriceState();
                stockPriceState.setSymbol(symbol);
                stockPriceState.setFirstLimitUpTime(now);
                stockPriceState.setLimitUpBreakTime(now);
                stockPriceState.setUp(false);
                stockPriceState.setDown(false);
            }else{
                if(stockPriceState.isUp()){
                    stockPriceState.setUp(false);
                    stockPriceState.setLimitUpBreakTime(now);
                }
            }
            redisStockPriceStateRepository.save(stockPriceState);
        }
        //记录破板
        for (StockBasic stockBasic : limitDownBreak){
            String symbol=stockBasic.getSymbol();
            StockPriceState stockPriceState=redisStockPriceStateRepository.findBySymbol(symbol);
            if(stockPriceState==null){
                stockPriceState=new StockPriceState();
                stockPriceState.setSymbol(symbol);
                stockPriceState.setFirstLimitDownTime(now);
                stockPriceState.setLimitDownBreakTime(now);
                stockPriceState.setUp(false);
                stockPriceState.setDown(false);
            }else{
                if(stockPriceState.isDown()){
                    stockPriceState.setDown(false);
                    stockPriceState.setLimitDownBreakTime(now);
                }
            }
            redisStockPriceStateRepository.save(stockPriceState);
        }
    }

    public void computeResultCache(){
        computeLimitUpAndDownTime();
        limitUpConceptCache=getConceptCore(limitUp);
        limitDownConceptCache=getConceptCore(limitDown);
        limitUpBreakConceptCache=getConceptCore(limitUpBreak);
        limitDownBreakConceptCache=getConceptCore(limitDownBreak);
        fast5ConceptCache=getConceptCore(fast5List);
        limitUpStocksCache=getSymbolCoreUp();
        limitDownStocksCache=getSymbolCoreDown();
        limitUpBreakStocksCache=getSymbolCoreUpBreak();
        limitDownBreakStocksCache=getSymbolCoreDownBreak();
        fast5StocksCache=computeSymbolAndAmount();
    }

    private List<SymbolAndAmount> computeSymbolAndAmount(){
        List<SymbolAndAmount> result=new ArrayList<>();
        for(StockBasic stockBasic:fast5List){
            String symbol=stockBasic.getSymbol();
                        SymbolAndAmount symbolAndAmount=new SymbolAndAmount();

            symbolAndAmount.setSymbol(symbol);
            if(lastAmount.containsKey(symbol)){
                EastMoneyFastPriceModel eastMoneyFastPriceModel = redisEastMoneyFastPriceRepository.findBySymbol(symbol);
                double last=lastAmount.getOrDefault(symbol,1.0);
                BigDecimal lastdeciaml=new BigDecimal(last*10);
                String amountstr= eastMoneyFastPriceModel.getAmount();
                BigDecimal nowdeciaml=new BigDecimal(amountstr);
                int r=nowdeciaml.divide(lastdeciaml, RoundingMode.HALF_UP).toBigInteger().intValue();
                symbolAndAmount.setAmount(r);
            }else{
                symbolAndAmount.setAmount(0);
            }
            result.add(symbolAndAmount);
        }
        Collections.sort(result);
        return result;
    }


    private List<ConceptResultStatistics> getConceptCore(List<StockBasic> stockBasicList){
        Map<String, Integer> result = new HashMap<>();
        for (StockBasic stockBasic : stockBasicList) {
            List<ConceptStocks> conceptStocksList = stockBasic.getConceptStocksList();
            for (ConceptStocks conceptStocks : conceptStocksList) {
                String conceptcode = conceptStocks.getConcept().getCode();
                int tmp = result.getOrDefault(conceptcode, 0) + 1;
                result.put(conceptcode, tmp);
            }
        }
        List<ConceptResultStatistics> r=new ArrayList<>();
        for(String key:result.keySet()){
            r.add(new ConceptResultStatistics(key,result.get(key)));
        }
        Collections.sort(r);
        Collections.reverse(r);
        return r;
    }

    private List<SymbolAndTime> getSymbolCoreUp(){
        List<SymbolAndTime> r=new ArrayList<>();
        for (StockBasic stockBasic : limitUp) {
            String symbol=stockBasic.getSymbol();
            StockPriceState stockPriceState=redisStockPriceStateRepository.findBySymbol(symbol);
            SymbolAndTime symbolAndTime=new SymbolAndTime();
            symbolAndTime.setSymbol(stockBasic.getSymbol());
            symbolAndTime.setTime1(stockPriceState.getFirstLimitUpTime());
            symbolAndTime.setTime2(stockPriceState.getLimitUpTime());
            r.add(symbolAndTime);
        }
        Collections.sort(r);
        return r;
    }

    private List<SymbolAndTime> getSymbolCoreDown(){
        List<SymbolAndTime> r=new ArrayList<>();
        for (StockBasic stockBasic : limitDown) {
            String symbol=stockBasic.getSymbol();
            StockPriceState stockPriceState=redisStockPriceStateRepository.findBySymbol(symbol);
            SymbolAndTime symbolAndTime=new SymbolAndTime();
            symbolAndTime.setSymbol(stockBasic.getSymbol());
            symbolAndTime.setTime1(stockPriceState.getFirstLimitDownTime());
            symbolAndTime.setTime2(stockPriceState.getLimitDownTime());
            r.add(symbolAndTime);
        }
        Collections.sort(r);
        return r;
    }

    private List<SymbolAndTime> getSymbolCoreUpBreak(){
        List<SymbolAndTime> r=new ArrayList<>();
        for (StockBasic stockBasic : limitUpBreak) {
            String symbol=stockBasic.getSymbol();
            StockPriceState stockPriceState=redisStockPriceStateRepository.findBySymbol(symbol);
            SymbolAndTime symbolAndTime=new SymbolAndTime();
            symbolAndTime.setSymbol(stockBasic.getSymbol());
            symbolAndTime.setTime1(stockPriceState.getFirstLimitUpTime());
            symbolAndTime.setTime2(stockPriceState.getLimitUpBreakTime());
            r.add(symbolAndTime);
        }
        Collections.sort(r);
        return r;
    }

    private List<SymbolAndTime> getSymbolCoreDownBreak(){
        List<SymbolAndTime> r=new ArrayList<>();
        for (StockBasic stockBasic : limitDownBreak) {
            String symbol=stockBasic.getSymbol();
            StockPriceState stockPriceState=redisStockPriceStateRepository.findBySymbol(symbol);
            SymbolAndTime symbolAndTime=new SymbolAndTime();
            symbolAndTime.setSymbol(stockBasic.getSymbol());
            symbolAndTime.setTime1(stockPriceState.getFirstLimitDownTime());
            symbolAndTime.setTime2(stockPriceState.getLimitDownBreakTime());
            r.add(symbolAndTime);
        }
        Collections.sort(r);
        return r;
    }
}
