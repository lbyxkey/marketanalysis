package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.Concept;
import indi.lby.marketanalysis.entity.ConceptStocks;
import indi.lby.marketanalysis.entity.StockBasic;
import indi.lby.marketanalysis.projections.ConceptStocksProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface JpaConceptStocksRepository extends JpaRepository<ConceptStocks, Integer> {
    List<ConceptStocksProjection> findByConceptCode(@Param("code") String code);

    @Modifying
    @Query(value = "TRUNCATE conceptstocks RESTART IDENTITY ",nativeQuery =true)
    void mytruncate();

    List<ConceptStocks> findAllByStockBasic(StockBasic stockBasic);

    ConceptStocks findConceptStocksByConceptAndStockBasic(Concept concept,StockBasic stockBasic);
}
