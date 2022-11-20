package indi.lby.marketanalysis.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import indi.lby.marketanalysis.entity.TradeCal;
import indi.lby.marketanalysis.repository.JpaTradeCalRepository;
import indi.lby.marketanalysis.service.PriceService;
import indi.lby.marketanalysis.service.SpiderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/spider")
@ConditionalOnProperty(name = "function.datamantance")
public class SpiderController {
    @Autowired
    SpiderService spiderService;

    @Autowired
    PreComputeController preComputeController;
    @Autowired
    PriceService priceService;


    @PostConstruct
    public void runOnStart() throws JsonProcessingException {
        //TradeCal tradeCal=jpaTradeCalRepository.findByCaldate(LocalDate.now());
        spiderService.runOnStart();
    }

    @Async
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateCal() throws JsonProcessingException{
        spiderService.updateCal();
    }


    @Async
    @Scheduled(cron = "0 0 16 * * 1-5")
    public void doAfterNoon() throws JsonProcessingException {
        spiderService.updateStockBasic();
        spiderService.updateDaily();
        priceService.initStockBasicList();
        //priceService.initPriceCache();
    }

//    @Scheduled(cron = "0 0 16 * * 1-5")
//    public void updateStockBasic() throws JsonProcessingException {
//        spiderService.updateStockBasic();
//    }
//
//    @Scheduled(cron = "0 5 16 * * 1-5")
//    public void updateDaily() throws JsonProcessingException {
//        spiderService.updateDaily();
//    }
    @Async
    @Scheduled(cron = "0 0 2 * * ?")
    public void doNight() throws JsonProcessingException {
        spiderService.updateTHSConcept();
        spiderService.updateTop10FloatHolder();
        preComputeController.computeLianBanStatistics();
    }

//    @Scheduled(cron = "0 0 2 * * ?")
//    public void updateTop10FloatHolder() throws JsonProcessingException {
//        spiderService.updateTop10FloatHolder();
//    }
//    @Scheduled(cron = "0 0 2 * * ?")
//    public void updateTHSConcept(){
//        spiderService.updateTHSConcept();
//    }
    @Autowired
    JpaTradeCalRepository jpaTradeCalRepository;
    /**
     * 周一到五早上9点30发动早盘刷新
     */
    @Scheduled(cron = "0 25 9 * * 1-5")
    public void updateEastMoneyPriceForeNoon0(){
        TradeCal tradeCal=jpaTradeCalRepository.findByCaldate(LocalDate.now());
        if(tradeCal.isIsopen()){
            priceService.newDayStart();
        }
        spiderService.updateEastMoneyPrice();
    }

    @Scheduled(cron = "*/3 30-59 9 * * 1-5")
    public void updateEastMoneyPriceForeNoon1() { spiderService.updateEastMoneyPrice(); }

    @Scheduled(cron = "*/3 * 10 * * 1-5")
    public void updateEastMoneyPriceForeNoon2(){
        spiderService.updateEastMoneyPrice();
    }

    @Scheduled(cron = "*/3 0-30 11 * * 1-5")
    public void updateEastMoneyPriceForeNoon3(){
        spiderService.updateEastMoneyPrice();
    }

    /**
     * 周一到五下午1点发动下午盘刷新
     */
    @Scheduled(cron = "*/3 * 13-14 * * 1-5")
    public void updateEastMoneyPriceAfterNoon1(){
        spiderService.updateEastMoneyPrice();
    }

    @Scheduled(cron = "0 0 15 * * 1-5")
    public void updateEastMoneyPriceAfterNoon2(){
        spiderService.updateEastMoneyPrice();
    }

//    @Autowired
//    SpiderService spiderService;
    @GetMapping("/addconcept/{conceptcode}")
    //@ResponseStatus(HttpStatus.ACCEPTED)
    public void addPool(@PathVariable("conceptcode") String conceptcode){
        //stockPoolService.add(principal.getName(),params);
        spiderService.addTHSConcept(conceptcode);
    }

}
