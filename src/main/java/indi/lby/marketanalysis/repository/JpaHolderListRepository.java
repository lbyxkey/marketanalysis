package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.FloatHolder;
import indi.lby.marketanalysis.entity.HolderList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaHolderListRepository extends JpaRepository<HolderList, Integer> {
    HolderList findHolderListByHoldername(String holdername);
}
