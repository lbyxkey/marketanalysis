package indi.lby.marketanalysis.service;

import indi.lby.marketanalysis.entity.ConceptStocks;
import indi.lby.marketanalysis.entity.StockBasic;
import indi.lby.marketanalysis.model.EastMoneyFastPriceModel;
import indi.lby.marketanalysis.projections.ConceptResultStatistics;
import indi.lby.marketanalysis.redisrepository.RedisEastMoneyFastPriceRepository;
import indi.lby.marketanalysis.repository.JpaConceptStocksRepository;
import indi.lby.marketanalysis.repository.JpaDailyRepository;
import indi.lby.marketanalysis.repository.JpaStockBasicRepository;
import indi.lby.marketanalysis.repository.JpaTradeCalRepository;
import indi.lby.marketanalysis.tools.ValueComparator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@Getter
public class PriceService {
    final String updatetime = "EastUpdateTime";
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
    List<StockBasic> stockBasicList = new ArrayList<>();
    List<StockBasic> limitUp = new ArrayList<>();
    List<StockBasic> limitUpBreak = new ArrayList<>();
    List<StockBasic> limitDown = new ArrayList<>();
    List<StockBasic> limitDownBreak = new ArrayList<>();

    @PostConstruct
    public void init() {
        initStockBasicList();
    }

    public void initStockBasicList() {
        stockBasicList = jpaStockBasicRepository.findAll();
    }


    /**
     *
     * @param t 缓存时间
     * @return 真则不需要更新，假需要更新
     */
    private boolean checkCacheTime(LocalDateTime t){
        String conceptUpdateDatetimeStr = stringRedisTemplate.opsForValue().get(updatetime);
        if (conceptUpdateDatetimeStr != null) {
            LocalDateTime updateDatetime = LocalDateTime.parse(conceptUpdateDatetimeStr);
            return t==updateDatetime;
        } else {
            return true;
        }
    }

    LocalDateTime computedTime=LocalDateTime.now();
    public void computePriceCache() {
        if(checkCacheTime(computedTime))return;
        String conceptUpdateDatetimeStr = stringRedisTemplate.opsForValue().get(updatetime);
        limitUp.clear();
        limitUpBreak.clear();
        limitDown.clear();
        limitDownBreak.clear();
        for (StockBasic stockBasic : stockBasicList) {
            String symbol = stockBasic.getSymbol();
            EastMoneyFastPriceModel eastMoneyFastPriceModel = redisEastMoneyFastPriceRepository.findBySymbol(symbol);
            if (eastMoneyFastPriceModel == null) continue;
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
        }
        if (conceptUpdateDatetimeStr != null) computedTime = LocalDateTime.parse(conceptUpdateDatetimeStr);
    }

    public List<String> getLimitUp() {
        List<String> result = new ArrayList<>();
        for (StockBasic stockBasic : limitUp) {
            result.add(stockBasic.getSymbol());
        }
        return result;
    }

    public List<String> getLimitDown() {
        List<String> result = new ArrayList<>();
        for (StockBasic stockBasic : limitDown) {
            result.add(stockBasic.getSymbol());
        }
        return result;
    }

    public List<String> getLimitUpBreak() {
        List<String> result = new ArrayList<>();
        for (StockBasic stockBasic : limitUpBreak) {
            result.add(stockBasic.getSymbol());
        }
        return result;
    }

    public List<String> getLimitDownBreak() {
        List<String> result = new ArrayList<>();
        for (StockBasic stockBasic : limitDownBreak) {
            result.add(stockBasic.getSymbol());
        }
        return result;
    }

    LocalDateTime limitUpConceptDateTime;
    List<ConceptResultStatistics> limitUpConceptCache=new ArrayList<>();
    public List<ConceptResultStatistics> getLimitUpConcept() {
        if(limitUpConceptDateTime==null||limitUpConceptDateTime!=computedTime){
            limitUpConceptCache=getConceptCore(limitUp);
            limitUpConceptDateTime=computedTime;
        }
        return limitUpConceptCache;
    }

    LocalDateTime limitDownConceptDateTime;
    List<ConceptResultStatistics> limitDownConceptCache=new ArrayList<>();
    public List<ConceptResultStatistics> getLimitDownConcept() {
        if(limitDownConceptDateTime==null||limitDownConceptDateTime!=computedTime){
            limitDownConceptCache=getConceptCore(limitDown);
            limitDownConceptDateTime=computedTime;
        }
        return limitDownConceptCache;
    }
    LocalDateTime limitUpBreakConceptDateTime;
    List<ConceptResultStatistics> limitUpBreakConceptCache=new ArrayList<>();
    public List<ConceptResultStatistics> getLimitUpBreakConcept() {
        if(limitUpBreakConceptDateTime==null||limitUpBreakConceptDateTime!=computedTime){
            limitUpBreakConceptCache=getConceptCore(limitUpBreak);
            limitUpBreakConceptDateTime=computedTime;
        }
        return limitUpBreakConceptCache;
    }

    LocalDateTime limitDownBreakConceptDateTime;
    List<ConceptResultStatistics> limitDownBreakConceptCache=new ArrayList<>();
    public List<ConceptResultStatistics> getLimitDownBreakConcept() {
        if(limitDownBreakConceptDateTime==null||limitDownBreakConceptDateTime!=computedTime){
            limitDownBreakConceptCache=getConceptCore(limitDownBreak);
            limitDownBreakConceptDateTime=computedTime;
        }
        return limitDownBreakConceptCache;
    }

    public List<ConceptResultStatistics> getConceptCore(List<StockBasic> stockBasicList){
        TreeMap<String, Integer> result = new TreeMap<>();
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
}
