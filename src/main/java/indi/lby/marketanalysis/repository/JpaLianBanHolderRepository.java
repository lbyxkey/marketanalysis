package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.LianBanHolder;
import indi.lby.marketanalysis.entity.LianBanStatistics;
import indi.lby.marketanalysis.entity.StockBasic;
import indi.lby.marketanalysis.entity.TradeCal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;


@Repository
public interface JpaLianBanHolderRepository extends JpaRepository<LianBanHolder, Long> {
    void deleteByStatisticsid(LianBanStatistics lianBanStatistics);
}
