package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.TradeCal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface JpaTradeCalRepository extends JpaRepository<TradeCal, Date> {
    TradeCal findTopByOrderByCaldateDesc();

    List<TradeCal> findAllByCaldateBetweenAndIsopenIsTrue(LocalDate startDate, LocalDate endDate);

    TradeCal findByCaldate(LocalDate caldate);
}
