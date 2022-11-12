package indi.lby.marketanalysis.repository;


import indi.lby.marketanalysis.entity.Ma;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface JpaMaRepository extends JpaRepository<Ma, Long> {

}
