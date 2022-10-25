package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.FloatHolder;
import indi.lby.marketanalysis.entity.StockBasic;
import indi.lby.marketanalysis.entity.TradeCal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JpaFloatHolderRepository extends JpaRepository<FloatHolder, Long> {
    FloatHolder findFirstByOrderByAnndateDesc();
    @Query(value = "SELECT MAX(enddate) FROM floatholder WHERE symbol=?1 AND enddate<=?2",nativeQuery =
            true)
    LocalDate getDateByStockBasicAndDate(String symbol,LocalDate startdate);

    List<FloatHolder> findFloatHolderBySymbolAndEnddateEquals(StockBasic symbol,TradeCal enddate);
}
