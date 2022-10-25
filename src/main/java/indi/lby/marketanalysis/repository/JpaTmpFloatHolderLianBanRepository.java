package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.TmpFloatHolderLianBan;
import indi.lby.marketanalysis.entity.TmpFloatHolderLianBanUPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTmpFloatHolderLianBanRepository extends JpaRepository<TmpFloatHolderLianBan, TmpFloatHolderLianBanUPK> {
}
