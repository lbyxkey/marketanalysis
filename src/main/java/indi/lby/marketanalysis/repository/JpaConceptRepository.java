package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.Concept;
import indi.lby.marketanalysis.projections.ConceptConceptsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface JpaConceptRepository extends JpaRepository<Concept, Integer> {
    List<ConceptConceptsProjection> findAllByOrderByName();


    Concept findConceptByCode(String code);

    @Transactional
    @Modifying
    @Query(value = "TRUNCATE concept RESTART IDENTITY CASCADE ",nativeQuery =true)
    void mytruncate();
}
