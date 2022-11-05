package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.ConceptStocks;
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
    public List<ConceptStocksProjection> findByConceptCode(@Param("code") String code);

    @Transactional
    @Modifying
    @Query(value = "TRUNCATE conceptstocks RESTART IDENTITY ",nativeQuery =true)
    void mytruncate();
}
