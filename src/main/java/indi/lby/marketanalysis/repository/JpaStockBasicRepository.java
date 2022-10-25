package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.StockBasic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaStockBasicRepository extends JpaRepository<StockBasic,String> {
    StockBasic findStockBasicBySymbol(String symbol);
    StockBasic findStockBasicByTscode(String tscode);
}
