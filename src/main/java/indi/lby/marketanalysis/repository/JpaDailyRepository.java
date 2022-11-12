package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.Concept;
import indi.lby.marketanalysis.entity.Daily;
import indi.lby.marketanalysis.entity.StockBasic;
import indi.lby.marketanalysis.entity.TradeCal;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface JpaDailyRepository extends JpaRepository<Daily, Long> {
//    @Query(value = "SELECT trade_date,sum(amount) FROM daily WHERE ts_code IN (SELECT ts_code FROM stock_basic WHERE stock_basic.symbol IN (SELECT symbol FROM conceptstocks WHERE conceptcode=?1)) AND trade_date BETWEEN '2022-09-03' AND '2022-09-22'  GROUP BY trade_date",nativeQuery = true)
//    List<ConceptAmountProjections> getConceptAmount(String conceptcode,Date startDate, Date endDate);
    @Transactional
    @Query(value = "SELECT tradedate,sum(amount) FROM daily WHERE stockid IN (SELECT stockid FROM conceptstocks WHERE" +
            "  conceptid=:#{#concept.id}) AND tradedate BETWEEN :#{#startDate.id} AND :#{#endDate.id} GROUP BY " +
            "tradedate ORDER " +
            "BY tradedate",
            nativeQuery = true)
    List<Map<String,Object>> getConceptAmount(Concept concept,TradeCal startDate,TradeCal endDate);

    @Transactional
    @Query(value = "SELECT tradedate,sum(amount) FROM daily WHERE tradedate BETWEEN ?1 AND ?2  GROUP BY tradedate  ORDER BY tradedate",
            nativeQuery = true)
    List<Map<String,Object>> getAmountAll(TradeCal startDate,TradeCal endDate);

    Daily findFirstByOrderByTradedateDesc();

    List<Daily> findDailiesBySymbolAndTradedateAfterOrderByTradedateAsc(StockBasic symbol, TradeCal tradeCal);

    Daily findBySymbolAndTradedate(StockBasic symbol,TradeCal tradeCal);

    List<Daily> findAllByTradedate(TradeCal tradeCal);
}
