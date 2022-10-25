package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserTokenRepository extends JpaRepository<UserToken,Integer> {
}
