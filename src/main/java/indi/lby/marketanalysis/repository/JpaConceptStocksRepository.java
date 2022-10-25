package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.ConceptStocks;
import indi.lby.marketanalysis.entity.ConceptStocksUPK;
import indi.lby.marketanalysis.projections.ConceptStocksProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaConceptStocksRepository extends JpaRepository<ConceptStocks, ConceptStocksUPK> {
    public List<ConceptStocksProjection> findByConceptCode(@Param("code") String code);

}
