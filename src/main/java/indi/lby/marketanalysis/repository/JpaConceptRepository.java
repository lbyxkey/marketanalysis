package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.Concept;
import indi.lby.marketanalysis.projections.ConceptConceptsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaConceptRepository extends JpaRepository<Concept, String> {
    List<ConceptConceptsProjection> findAllByOrderByName();

    Concept findConceptByCode(String code);
}
