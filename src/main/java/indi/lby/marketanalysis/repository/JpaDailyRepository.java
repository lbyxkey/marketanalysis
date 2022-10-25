package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.Daily;
import indi.lby.marketanalysis.entity.DailyUPK;
import indi.lby.marketanalysis.entity.StockBasic;
import indi.lby.marketanalysis.entity.TradeCal;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface JpaDailyRepository extends JpaRepository<Daily, DailyUPK> {
//    @Query(value = "SELECT trade_date,sum(amount) FROM daily WHERE ts_code IN (SELECT ts_code FROM stock_basic WHERE stock_basic.symbol IN (SELECT symbol FROM conceptstocks WHERE conceptcode=?1)) AND trade_date BETWEEN '2022-09-03' AND '2022-09-22'  GROUP BY trade_date",nativeQuery = true)
//    List<ConceptAmountProjections> getConceptAmount(String conceptcode,Date startDate, Date endDate);

    @Query(value = "SELECT tradedate,sum(amount) FROM daily WHERE symbol IN (SELECT symbol FROM conceptstocks WHERE " +
            "conceptcode=?1) AND tradedate BETWEEN ?2 AND ?3 GROUP BY tradedate ORDER BY tradedate ASC ",nativeQuery =
            true)
    List<Map<String,Object>> getConceptAmount(String conceptcode,LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT tradedate,sum(amount) FROM daily WHERE tradedate BETWEEN ?1 AND ?2  GROUP BY tradedate  ORDER BY tradedate ASC",
            nativeQuery = true)
    List<Map<String,Object>> getAmountAll(LocalDate startDate, LocalDate endDate);

    Daily findFirstByOrderByTradedateDesc();

    List<Daily> findDailiesBySymbolAndTradedateAfterOrderByTradedateAsc(StockBasic symbol, TradeCal tradeCal);


}
