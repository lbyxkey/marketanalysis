package indi.lby.marketanalysis.repository;

import indi.lby.marketanalysis.entity.MyUser;
import indi.lby.marketanalysis.entity.Team;
import indi.lby.marketanalysis.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaTeamMemberRepository extends JpaRepository<TeamMember,Integer> {
    TeamMember findTeamMemberById(int id);

    TeamMember findTeamMemberByUseridAndTeamid(MyUser user,Team team);

    List<TeamMember> findTeamMembersByUserid(MyUser user);
}
