package indi.lby.marketanalysis.repository;


import indi.lby.marketanalysis.entity.PreMa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface JpaPreMaRepository extends JpaRepository<PreMa, Integer> {

}
