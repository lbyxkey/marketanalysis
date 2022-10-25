package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.ConceptType;
import indi.lby.marketanalysis.projections.ConceptTypeShowProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;


@Repository
public interface JpaConceptTypeRepository extends JpaRepository<ConceptType, Integer> {
    List<ConceptTypeShowProjection> findAllByOrderByTypeAsc();
}
