package indi.lby.marketanalysis.controller;


import indi.lby.marketanalysis.projections.StocksSymbolAndName;
import indi.lby.marketanalysis.projections.SymbolAndAmount;
import indi.lby.marketanalysis.projections.SymbolAndTime;
import indi.lby.marketanalysis.service.PriceService;
import indi.lby.marketanalysis.service.StockBasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stocks")
public class StockListController {

    @Autowired
    StockBasicService stockBasicService;

    @Autowired
    PriceService priceService;
    /**
     * 获取所有股票列表
     * @return 股票symbol和名称列表
     */
    @GetMapping("/all")
    @ResponseBody
    public List<StocksSymbolAndName> stocksList(){
        return stockBasicService.basic();
    }

    /**
     * 获取涨停的股票列表
     * @return 股票symbol列表
     */
    @GetMapping("/limitup")
    @ResponseBody
    public List<SymbolAndTime> stocksLimitUp(){
        //priceService.computePriceCache();
        return priceService.getLimitUpStocksCache();
    }

    /**
     * 获取涨停开板的股票列表
     * @return 股票symbol列表
     */
    @GetMapping("/limitupbreak")
    @ResponseBody
    public List<SymbolAndTime> stocksLimitUpBreak(){
        //priceService.computePriceCache();
        return priceService.getLimitUpBreakStocksCache();
    }

    /**
     * 获取跌停的股票列表
     * @return 股票symbol列表
     */
    @GetMapping("/limitdown")
    @ResponseBody
    public List<SymbolAndTime> stocksLimitDown(){
        //priceService.computePriceCache();
        return priceService.getLimitDownStocksCache();
    }

    /**
     * 获取跌停开板的股票列表
     * @return 股票symbol列表
     */
    @GetMapping("/limitdownbreak")
    @ResponseBody
    public List<SymbolAndTime> stocksLimitDownBreak(){
        //priceService.computePriceCache();
        return priceService.getLimitDownBreakStocksCache();
    }

    @GetMapping("/fast5")
    @ResponseBody
    public List<SymbolAndAmount> fast5(){
        //priceService.computePriceCache();
        return priceService.getFast5StocksCache();//fast5StocksCache
    }
}
