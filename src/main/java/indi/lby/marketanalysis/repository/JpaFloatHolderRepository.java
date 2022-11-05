package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.FloatHolder;
import indi.lby.marketanalysis.entity.HolderList;
import indi.lby.marketanalysis.entity.StockBasic;
import indi.lby.marketanalysis.entity.TradeCal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Repository
public interface JpaFloatHolderRepository extends JpaRepository<FloatHolder, Long> {
    FloatHolder findFirstByOrderByAnndateDesc();
    @Query(value = "SELECT MAX(enddate) FROM floatholder WHERE stockid=:#{#stockBasic.id} AND enddate<=:#{#startdate.id}",
            nativeQuery =
            true)
    Object getDateByStockBasicAndDate(StockBasic stockBasic,TradeCal startdate);

    List<FloatHolder> findFloatHolderBySymbolAndEnddateEquals(StockBasic symbol,TradeCal enddate);

    FloatHolder findFloatHolderBySymbolAndEnddateAndHoldername(StockBasic symbol, TradeCal enddate,
                                                                     HolderList holdername);
//    @Transactional
//    @Modifying
//    @Query(value = "INSERT INTO floatholder(holdamount, stockid, holderid, anndate, enddate) VALUES " +
//            "(#{#floatHolder.holdamount}, #{#floatHolder.symbol.id}, #{#floatHolder.holdername.id}," +
//            "#{#floatHolder.anndate.id}, #{#floatHolder.enddate.id}) ON CONFLICT DO NOTHING",nativeQuery = true)
//    void mysave(FloatHolder floatHolder);
}
