package indi.lby.marketanalysis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import indi.lby.marketanalysis.entity.*;
import indi.lby.marketanalysis.repository.*;
import indi.lby.marketanalysis.model.EastMoneyFastPriceModel;
import indi.lby.marketanalysis.spider.model.THSConceptsModel;
import indi.lby.marketanalysis.spider.pageprocessor.*;
import indi.lby.marketanalysis.spider.pipeline.*;
import indi.lby.marketanalysis.spider.service.TuShareJsonMakerService;
import indi.lby.marketanalysis.tools.THSCookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.OOSpider;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@ConditionalOnProperty(name = "function.datamantance")
public class SpiderService {

    final String conceptUpdateDatetimeName = "conceptUpdateDatetime";
    final String stockbasicUpdateDatetimeName = "stockbasicUpdateDatetime";
    //final String dailyUpdateDatetimeName = "dailyUpdateDatetime";
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    THSCookie thsCookie;
    @Autowired
    THSConceptsPipeline thsConceptsPipeline;
    @Autowired
    THSConceptStockPipeline thsConceptStockPipeline;
    @Autowired
    JpaConceptRepository jpaConceptRepository;

    @Autowired
    JpaConceptStocksRepository jpaConceptStocksRepository;
    @Autowired
    THSConceptStockPageProcessor thsConceptStockPageProcessor;
    @Autowired
    JpaDailyRepository jpaDailyRepository;
    @Autowired
    JpaTradeCalRepository jpaTradeCalRepository;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired
    TuShareCalDatePageProcessor tuShareCalDatePageProcessor;

    @Autowired
    TuShareCalDatePipeline tuShareCalDatePipeline;

    @Autowired
    TuShareStockBasicPageProcessor tuShareStockBasicPageProcessor;

    @Autowired
    TuShareStockBasicPipeline tuShareStockBasicPipeline;

    @Autowired
    TuShareDailyPipeline tuShareDailyPipeline;
    @Autowired
    TuShareDailyPageProcessor tuShareDailyPageProcessor;

    @Autowired
    TuShareFloatHolderPageProcessor tuShareFloatHolderPageProcessor;

    @Autowired
    TuShareFloatHolderPipeline tuShareFloatHolderPipeline;

    @Autowired
    TuShareJsonMakerService tuShareJsonMakerService;

    @Autowired
    JpaFloatHolderRepository jpaFloatHolderRepository;

    @Value("${tushare.url}")
    String tuShareURL;

    @Value("${eastmoney.threadnum}")
    int eastMoneyThreadNum;

    @Value("${function.datamantance.refreshconceptonstart}")
    boolean refreshconceptonstart;
    @Autowired
    EastMoneyPricePipeline eastMoneyPricePipeline;
    @Autowired
    JpaStockBasicRepository jpaStockBasicRepository;
    List<StockBasic> stockBasicList;

    @Async
    public void runOnStart() throws JsonProcessingException {
        log.info("Spider Start runOnStart");
        updateCal();
        updateStockBasic();
        updateDaily();
        //如果上次距离本次时间大于24小时，刷新
        if(refreshconceptonstart){
            updateTHSConcept();
        }
//        LocalDateTime now=LocalDateTime.now();
//        String conceptUpdateDatetimeStr=stringRedisTemplate.opsForValue().get(conceptUpdateDatetimeName);
//        if(conceptUpdateDatetimeStr!=null){
//            LocalDateTime conceptUpdateDatetime=LocalDateTime.parse(conceptUpdateDatetimeStr);
//            if(Duration.between(conceptUpdateDatetime,now).toHours()>=24){
//                updateTHSConcept();
//            }
//        }else{
//            updateTHSConcept();
//        }
        updateTop10FloatHolder();
        updateEastMoneyPriceCore();
    }

    //刷新交易日历
    public void updateCal() throws JsonProcessingException {
        TradeCal tradeCal = jpaTradeCalRepository.findTopByOrderByCaldateDesc();
        //Date now1 = Date.valueOf(LocalDate.now().plusDays(-1));
        if (tradeCal == null) {
            updateCalCore(null);
        } else if (!tradeCal.getCaldate().isAfter(LocalDate.now().plusDays(-1))) {
            updateCalCore(tradeCal.getCaldate());
        }
        //updateCalCore(null);
    }

    private void updateCalCore(LocalDate date) throws JsonProcessingException {
        log.info("刷新交易日历");
        Spider spider = Spider.create(tuShareCalDatePageProcessor).addPipeline(tuShareCalDatePipeline);
        Request request = tuShareJsonMakerService.getRequest(tuShareJsonMakerService.getCalDateJson(date));
        spider.addRequest(request);
        spider.run();
        log.info("刷新交易日历-完成");
    }

    //刷新股票列表
    public void updateStockBasic() throws JsonProcessingException {
        log.info("刷新股票列表");
        Spider spider = Spider.create(tuShareStockBasicPageProcessor).addPipeline(tuShareStockBasicPipeline);
        Request request = tuShareJsonMakerService.getRequest(tuShareJsonMakerService.getStockBasicJson());
        spider.addRequest(request);
        spider.run();
        log.info("刷新股票列表-完成");
        stockBasicList= jpaStockBasicRepository.findAll();
    }

    /**
     * 刷新日线信息，每天16点5分运行
     */
    public void updateDaily() throws JsonProcessingException {
        log.info("刷新日线");
        Daily daily = jpaDailyRepository.findFirstByOrderByTradedateDesc();
        List<TradeCal> tradeCalList=new ArrayList<>();
        if(daily!=null){
            LocalDate maxDate = daily.getTradedate().getCaldate();
            tradeCalList = jpaTradeCalRepository.findAllByCaldateBetweenAndIsopenIsTrue(maxDate,
                    LocalDate.now());
        }else{
            tradeCalList=jpaTradeCalRepository.findAllByCaldateBeforeAndIsopenIsTrue(LocalDate.now());
        }
        Spider spider = Spider.create(tuShareDailyPageProcessor).addPipeline(tuShareDailyPipeline);
        for (TradeCal tradeCal : tradeCalList) {
            Request request =
                    tuShareJsonMakerService.getRequest(tuShareJsonMakerService.getDailyJson(tradeCal.getCaldate()));
            spider.addRequest(request);
        }
        spider.run();
        log.info("刷新日线-完成");
        //stringRedisTemplate.opsForValue().set(dailyUpdateDatetimeName, LocalDateTime.now().format(formatter));
    }

    /**
     * 每天凌晨2点刷新十大股东
     */
    public void updateTop10FloatHolder() throws JsonProcessingException {
        log.info("刷新十大股东");
        FloatHolder floatHolder = jpaFloatHolderRepository.findFirstByOrderByAnndateDesc();
        LocalDate maxDate;
        if(floatHolder!=null){
            maxDate = floatHolder.getAnndate().getCaldate().plusDays(1);
        }else{
            maxDate=jpaTradeCalRepository.findFirstByOrderByCaldate().getCaldate();
        }
//        Calendar calendar=new GregorianCalendar();
//        calendar.setTimeInMillis(maxDate.getTime());
//        calendar.add(Calendar.DATE,1);
//        maxDate.setTime(calendar.getTimeInMillis());
        tuShareFloatHolderPageProcessor.setMaxdate(maxDate);
        tuShareFloatHolderPageProcessor.setOffset(0);
        Spider spider = Spider.create(tuShareFloatHolderPageProcessor).addPipeline(tuShareFloatHolderPipeline);
        Request request = tuShareJsonMakerService.getRequest(tuShareJsonMakerService.getFloatHolderJson(maxDate,0));
        spider.addRequest(request);
        spider.run();
        log.info("刷新十大股东-完成");
    }

    /**
     * 刷新同花顺概念,每天凌晨2点执行
     */
    public void updateTHSConcept() {
        log.info("刷新同花顺概念");
        log.info("1、刷新同花顺概念-清空数据库");
        //清空概念数据库
        jpaConceptRepository.mytruncate();
        //刷新概念列表
        log.info("2、刷新同花顺概念-刷新概念列表");
        OOSpider.create(Site.me(), thsConceptsPipeline, THSConceptsModel.class).addUrl(
                "http://q.10jqka.com.cn/gn").thread(5).run();
        addUserAddTHSConcept();
        //刷新概念
        log.info("3、刷新同花顺概念-刷新概念");
        Spider spider =
                Spider.create(thsConceptStockPageProcessor).addPipeline(thsConceptStockPipeline).thread(1);
        List<Concept> conceptList = jpaConceptRepository.findAll();
        for (Concept concept : conceptList) {
            String newurl = "http://q.10jqka.com.cn/" + concept.getConceptType().getShortname() + "/detail/field/" + concept.getConceptType().getCode() +
                    "/order/desc/page/1/ajax/1/code/" + concept.getCode();
            Request request = new Request(newurl);
            String cookie = thsCookie.getCookie();
            request.addCookie("v", cookie);
            spider.addRequest(request);
        }
        spider.run();
        log.info("刷新同花顺概念-完成");
        stringRedisTemplate.opsForValue().set(conceptUpdateDatetimeName, LocalDateTime.now().format(formatter));
    }

    @Autowired
    JpaConceptUserAddRepository jpaConceptUserAddRepository;

    @Autowired
    JpaConceptTypeRepository jpaConceptTypeRepository;

    private void addUserAddTHSConcept(){
        List<ConceptUserAdd> conceptUserAddList=jpaConceptUserAddRepository.findAllByOrderByName();
        int deletecount=0;
        int addcount=0;
        ConceptType conceptType=jpaConceptTypeRepository.findConceptTypeByType(5);
        for (ConceptUserAdd conceptUserAdd:conceptUserAddList) {
            String name=conceptUserAdd.getName();
            Concept concept=jpaConceptRepository.findConceptByName(name);
            if(concept!=null){
                jpaConceptUserAddRepository.delete(conceptUserAdd);
                deletecount++;
            }else{
                Concept newconcept=new Concept();
                newconcept.setConceptType(conceptType);
                newconcept.setName(name);
                newconcept.setCode(conceptUserAdd.getCode());
                jpaConceptRepository.save(newconcept);
                addcount++;
            }
        }
        log.info("处理手工概念，删除"+deletecount+"个，添加"+addcount+"个");
    }

    @Autowired
    THSUserAddConceptsPipeline thsUserAddConceptsPipeline;
    @Autowired
    THSUserAddConceptPageProcessor thsUserAddConceptPageProcessor;
    @Async
    public void addTHSConcept(String code){
        log.info("手工添加同花顺概念"+code);
        Spider.create(thsUserAddConceptPageProcessor).addPipeline(thsUserAddConceptsPipeline).addUrl(
                "http://q.10jqka.com.cn/thshy/detail/code/"+code).thread(1).run();
        Concept concept = jpaConceptRepository.findConceptByCode(code);
        if(concept==null){
            log.warn("手工添加失败同花顺概念"+code);
        }else{
            Spider spider =
                    Spider.create(thsConceptStockPageProcessor).addPipeline(thsConceptStockPipeline).thread(1);
            String newurl = "http://q.10jqka.com.cn/" + concept.getConceptType().getShortname() + "/detail/field/" + concept.getConceptType().getCode() +
                        "/order/desc/page/1/ajax/1/code/" + concept.getCode();
            Request request = new Request(newurl);
            String cookie = thsCookie.getCookie();
            request.addCookie("v", cookie);
            spider.addRequest(request);
            spider.run();
            log.info("手工添加同花顺概念-完成");
        }

    }

    boolean spiderRunning=false;

    private synchronized void setSpiderRunningState(boolean state){
        this.spiderRunning=state;
    }
    //@Scheduled(cron = "*/10 * * * * 1-5")
    public void updateEastMoneyPrice() {
        if(spiderRunning) {
            log.warn("Spider is Running");
            return;
        }
        setSpiderRunningState(true);
        //判断今天开盘
        TradeCal tradeCal= jpaTradeCalRepository.findByCaldate(LocalDate.now());
        if(tradeCal.isIsopen()) {
            updateEastMoneyPriceCore();
        }
    }

    @Autowired
    PriceService priceService;
    private void updateEastMoneyPriceCore(){
        //判断股票列表非空
        if(stockBasicList==null){
            stockBasicList= jpaStockBasicRepository.findAll();
        }
        StopWatch stopWatch=new StopWatch("EastMoneyPrice");
        stopWatch.start();
        String useragent="Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:105.0) Gecko/20100101 Firefox/105.0";
        Spider spider=
                OOSpider.create(Site.me().setUserAgent(useragent).setSleepTime(1).setRetryTimes(3),
                        eastMoneyPricePipeline,
                        EastMoneyFastPriceModel.class).thread(eastMoneyThreadNum);
        for (StockBasic stockBasic:stockBasicList) {
            String symbol=stockBasic.getSymbol();
            String prefix = "0";
            if (symbol.startsWith("6"))
                prefix = "1";
            String url =
                    "http://push2.eastmoney.com/api/qt/stock/get?fields=f43,f44,f45,f46,f48,f51,f52,f57,f58,f60&secid="+prefix+"."+symbol;
            spider.addUrl(url);
        }
        spider.run();
        stopWatch.stop();
        priceService.computePriceCache();
        priceService.computeResultCache();
        setSpiderRunningState(false);
        log.info("EastMoney总数量"+spider.getPageCount()+"/"+stockBasicList.size()+"个，总运行时间"+stopWatch.getTotalTimeSeconds()+"秒");
    }


}
