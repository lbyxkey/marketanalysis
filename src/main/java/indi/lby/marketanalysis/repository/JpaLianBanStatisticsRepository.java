package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.LianBanStatistics;
import indi.lby.marketanalysis.entity.StockBasic;
import indi.lby.marketanalysis.entity.TradeCal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface JpaLianBanStatisticsRepository extends JpaRepository<LianBanStatistics, Integer> {
    @Query(value = "SELECT MIN(startdate) FROM lianbanstatistics WHERE symbol IN (SELECT symbol FROM " +
            "lianbanstatistics WHERE enddate=(SELECT MAX(enddate) FROM lianbanstatistics))",
            nativeQuery = true)
    LocalDate getStartComputeLianBan();

    LianBanStatistics findLianBanStatisticsByStartdateEqualsAndSymbol(TradeCal startdate, StockBasic symbol);

    List<LianBanStatistics> findLianBanStatisticsByStartdateAfterAndSymbol(TradeCal startdate, StockBasic symbol);
    void deleteByEnddateBefore(TradeCal tradeCal);
//    @Query(value = "INSERT INTO public.lianbanstatistics(symbol, startdate, count, enddate) VALUES (?1,?2,?3,?4)",
//            nativeQuery = true)
//    Object mysave(String symbol, LocalDate startDate,int count,LocalDate endDate);

    @Transactional
    @Modifying
    @Query(value = "TRUNCATE lianbanstatistics RESTART IDENTITY CASCADE ",nativeQuery =true)
    void mytruncate();
}
