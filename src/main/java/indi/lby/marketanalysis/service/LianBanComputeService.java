package indi.lby.marketanalysis.service;

import indi.lby.marketanalysis.entity.*;
import indi.lby.marketanalysis.repository.*;
import indi.lby.marketanalysis.tools.PriceTools;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class LianBanComputeService {

    @Autowired
    JpaStockBasicRepository jpaStockBasicRepository;

    @Autowired
    JpaDailyRepository jpaDailyRepository;

    @Autowired
    JpaTradeCalRepository jpaTradeCalRepository;

    @Autowired
    JpaLianBanStatisticsRepository jpaLianBanStatisticsRepository;

    @Autowired
    JpaFloatHolderRepository jpaFloatHolderRepository;

    @Autowired
    JpaLianBanHolderRepository jpaLianBanHolderRepository;

//    @Async
//    public void testFuction(List<Integer> intlist,int index,int count) throws InterruptedException {
//        for(int i=index;i<intlist.size();i+=count){
//            log.info("thread:"+index+" i="+i+" print:"+intlist.get(i));
//            Thread.sleep(100);
//        }
//    }
    @Value("${lianban.mincount}")
    int mincount;

    @Value("${lianban.mindays}")
    int mindays;

    @Value("${lianban.years}")
    int years;

    public TradeCal getUpdateStartDate(){
        //LocalDate startDate=jpaLianBanStatisticsRepository.getStartComputeLianBan();
        return jpaTradeCalRepository.findByCaldate(LocalDate.now().minusYears(years));
    }

    public void deleteOldStatics(){
//        TradeCal tradeCal=jpaTradeCalRepository.findByCaldate(LocalDate.now().minusYears(years));
//        jpaLianBanStatisticsRepository.deleteByEnddateBefore(tradeCal);
        jpaLianBanStatisticsRepository.mytruncate();
    }

    public List<StockBasic> getStockList(){
        return jpaStockBasicRepository.findAll();
    }

    @Getter
    @NoArgsConstructor
    class Lbcounter{
        boolean isLianban=false;
        int count=0;
        int finalcount=0;
        TradeCal startDate;
        TradeCal endDate;

        /**
         *
         * @param daily
         * @param last 是否是最后一个数据
         * @return
         */
        boolean isOver(Daily daily,boolean last){
            if(daily.getClose()>= PriceTools.getLimitUpPrice(daily.getSymbol(),daily.getPreclose())){
                if(isLianban){
                    count++;
                    endDate=daily.getTradedate();
                }else{
                    isLianban=true;
                    count=1;
                    startDate=daily.getTradedate();
                    endDate=daily.getTradedate();
                }
                finalcount=count;
                return last && count >= mincount;
            }else{
                if(isLianban&&count>=mincount){
                    finalcount=count;
                    count=0;
                    isLianban=false;
                    return true;
                }else{
                    return false;
                }
            }
        }
    }
    @Async
    public void computeLianBanStatistics(TradeCal startDate,List<StockBasic> stockList,int index,int count) {
        for(int i=index;i< stockList.size();i+=count){
            StockBasic stockBasic=stockList.get(i);
            List<Daily> dailies=jpaDailyRepository.findDailiesBySymbolAndTradedateAfterOrderByTradedateAsc(stockBasic
                    ,startDate);
            if(dailies.size()<=mindays){
                //10日内新股不考虑
                continue;
            }
            log.info(index+"/"+count+"计算连板"+i+" 股票名"+stockBasic.getSymbol()+stockBasic.getName()+" 日线数"+dailies.size());
            Lbcounter lbcounter=new Lbcounter();
            for(int j=0;j<dailies.size();j++){
                Daily daily=dailies.get(j);
                if(lbcounter.isOver(daily,j+1==dailies.size())){
                    LianBanStatistics lianBanStatistics=new LianBanStatistics();
                    lianBanStatistics.setSymbol(stockBasic);
                    lianBanStatistics.setStartdate(lbcounter.getStartDate());
                    lianBanStatistics.setCount(lbcounter.getFinalcount());
                    lianBanStatistics.setEnddate(lbcounter.getEndDate());
                    jpaLianBanStatisticsRepository.save(lianBanStatistics);
                    addLianBanHolderName(lianBanStatistics);
                }
            }
            log.info(index+"/"+count+"计算连班 "+i+" 股票名"+stockBasic.getSymbol()+stockBasic.getName()+"完毕");
        }
    }

    private  void addLianBanHolderName(LianBanStatistics lianBanStatistics){
        Object enddateid=
                jpaFloatHolderRepository.getDateByStockBasicAndDate(lianBanStatistics.getSymbol(),
                        lianBanStatistics.getStartdate());
        if(enddateid==null)return;
        TradeCal endCal=jpaTradeCalRepository.findById((int)enddateid);
        List<FloatHolder> floatHolderList=
                jpaFloatHolderRepository.findFloatHolderBySymbolAndEnddateEquals(lianBanStatistics.getSymbol(),endCal);
        for (FloatHolder floatHolder:floatHolderList) {
            LianBanHolder lianBanHolder=new LianBanHolder();
            lianBanHolder.setFloatholderid(floatHolder);
            lianBanHolder.setStatisticsid(lianBanStatistics);
            jpaLianBanHolderRepository.save(lianBanHolder);
        }
    }

    /**
     * 遇到新的股东处理方法，首先删除所有在结束日期后的holder表，然后添加
     * @param floatHolderList
     */
    public void computeLianBanHolderNewHolder(List<FloatHolder> floatHolderList){
        Set<LianBanStatistics> lianBanStatisticsSet=new HashSet<>();
        for (FloatHolder floatHolder:floatHolderList){
            List<LianBanStatistics> lianBanStatisticsList=
                    jpaLianBanStatisticsRepository.findLianBanStatisticsByStartdateAfterAndSymbol(floatHolder.getEnddate(),floatHolder.getSymbol());
            lianBanStatisticsSet.addAll(lianBanStatisticsList);
        }
        for(LianBanStatistics lianBanStatistics:lianBanStatisticsSet){
            jpaLianBanHolderRepository.deleteByStatisticsid(lianBanStatistics);
        }
        for (LianBanStatistics lianBanStatistics:lianBanStatisticsSet) {
            addLianBanHolderName(lianBanStatistics);
        }
    }
}
