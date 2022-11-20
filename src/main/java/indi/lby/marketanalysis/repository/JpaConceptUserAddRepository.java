package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.Concept;
import indi.lby.marketanalysis.entity.ConceptUserAdd;
import indi.lby.marketanalysis.projections.ConceptConceptsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface JpaConceptUserAddRepository extends JpaRepository<ConceptUserAdd, Integer> {

    ConceptUserAdd findConceptUserAddByCode(String code);

    ConceptUserAdd findConceptUserAddByName(String name);

    List<ConceptUserAdd> findAllByOrderByName();
}
