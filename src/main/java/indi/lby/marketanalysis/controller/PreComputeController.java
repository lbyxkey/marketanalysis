package indi.lby.marketanalysis.controller;

import indi.lby.marketanalysis.entity.StockBasic;
import indi.lby.marketanalysis.entity.TradeCal;
import indi.lby.marketanalysis.service.LianBanComputeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
@Slf4j
public class PreComputeController {
    @Autowired
    LianBanComputeService lianBanComputeService;

    int cpuCount;

    @PostConstruct
    public void init() throws InterruptedException {
        cpuCount=Runtime.getRuntime().availableProcessors();
    }

    //@Scheduled(cron = "0 30 2 * * 2-6")
    public void computeLianBanStatistics() {
        lianBanComputeService.deleteOldStatics();
        TradeCal startDate=lianBanComputeService.getUpdateStartDate();
        List<StockBasic> stockBasicList=lianBanComputeService.getStockList();
        for(int i=0;i<cpuCount;i++){
            lianBanComputeService.computeLianBanStatistics(startDate,stockBasicList,i,cpuCount);
        }
    }



}
