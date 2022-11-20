package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends JpaRepository<MyUser,Integer> {
    MyUser findUserByUsername(String username);

    MyUser findMyUserById(int userid);

    MyUser findMyUserByNickname(String nickname);
}
