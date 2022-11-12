package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.StockBasic;
import indi.lby.marketanalysis.projections.StocksSymbolAndName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaStockBasicRepository extends JpaRepository<StockBasic,Integer> {
    StockBasic findStockBasicBySymbol(String symbol);
    StockBasic findStockBasicByTscode(String tscode);

    List<StocksSymbolAndName> findAllByOrderBySymbol();
}
