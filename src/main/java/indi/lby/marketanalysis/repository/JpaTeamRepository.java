package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.MyUser;
import indi.lby.marketanalysis.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaTeamRepository extends JpaRepository<Team,Integer> {
    Team findTeamById(int id);

    //Team findTeamByLeader(MyUser leader);
    List<Team> findTeamsByLeader(MyUser leader);

    Team findTeamByLeaderAndName(MyUser leader,String name);

    Team findTeamByLeaderAndId(MyUser leader,int id);

}
