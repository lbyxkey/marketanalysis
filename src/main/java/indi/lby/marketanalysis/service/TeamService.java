package indi.lby.marketanalysis.service;

import indi.lby.marketanalysis.entity.MyUser;
import indi.lby.marketanalysis.entity.Team;
import indi.lby.marketanalysis.entity.TeamMember;
import indi.lby.marketanalysis.projections.TeamInformation;
import indi.lby.marketanalysis.projections.TeamUserInformation;
import indi.lby.marketanalysis.projections.UserInformation;
import indi.lby.marketanalysis.repository.JpaTeamMemberRepository;
import indi.lby.marketanalysis.repository.JpaTeamRepository;
import indi.lby.marketanalysis.repository.JpaUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TeamService {

    @Autowired
    JpaTeamRepository jpaTeamRepository;

    @Autowired
    JpaUserRepository jpaUserRepository;

    @Autowired
    JpaTeamMemberRepository jpaTeamMemberRepository;

    private boolean checkTeamLeader(String name, int teamid){
        MyUser user=jpaUserRepository.findUserByUsername(name);
        if(user==null)return false;
        Team team=jpaTeamRepository.findTeamById(teamid);
        if(team==null)return false;
        return user.equals(team.getLeader());
    }

    private boolean checkTeamMember(String name, int teamid){
        MyUser user=jpaUserRepository.findUserByUsername(name);
        if(user==null)return false;
        Team team=jpaTeamRepository.findTeamById(teamid);
        if(team==null)return false;
        TeamMember teamMember=jpaTeamMemberRepository.findTeamMemberByUseridAndTeamid(user,team);
        return teamMember!=null;
    }

//    private boolean checkUser(String name){
//        MyUser user=jpaUserRepository.findUserByUsername(name);
//        return user!=null;
//    }

    public List<TeamInformation> getMyTermInformation(String name){
        List<TeamInformation> result=new ArrayList<>();
        MyUser user=jpaUserRepository.findUserByUsername(name);
        List<Team> teams=jpaTeamRepository.findTeamsByLeader(user);
        for(Team team:teams){
            TeamInformation teamInformation=new TeamInformation(team);
            result.add(teamInformation);
        }
        return result;
    }


    public List<TeamInformation> getjoinedteams(String name){
        List<TeamInformation> result=new ArrayList<>();
        MyUser user=jpaUserRepository.findUserByUsername(name);
        List<TeamMember> teamMembers=jpaTeamMemberRepository.findTeamMembersByUserid(user);
        for(TeamMember team:teamMembers){
            if(!team.getTeamid().getLeader().equals(user)){
                TeamInformation teamInformation=new TeamInformation(team);
                result.add(teamInformation);
            }

        }
        return result;
    }


    public List<TeamUserInformation> getmember(String name, int teamid){
        List<TeamUserInformation> result=new ArrayList<>();
        MyUser user=jpaUserRepository.findUserByUsername(name);
        Team team=jpaTeamRepository.findTeamById(teamid);
        List<TeamMember> teamMembers=team.getTeamMemberList();
        for(TeamMember teamMember:teamMembers){
            if(teamMember.getUserid().equals(user))continue;
            TeamUserInformation userInformation=new TeamUserInformation(teamMember);
            result.add(userInformation);
        }
        return result;
    }


    public boolean teamadd(String name,TeamInformation teamInformation){
        MyUser user=jpaUserRepository.findUserByUsername(name);
        Team team=jpaTeamRepository.findTeamByLeaderAndName(user, teamInformation.getName());
        if(team==null){
            team=new Team();
            team.setName(teamInformation.getName());
            team.setDescription(teamInformation.getDescription());
            team.setLeader(user);
            jpaTeamRepository.save(team);
            team=jpaTeamRepository.findTeamByLeaderAndName(user, teamInformation.getName());
            TeamMember teamMember=new TeamMember();
            teamMember.setTeamid(team);
            teamMember.setUserid(user);
            teamMember.setAccepted(true);
            jpaTeamMemberRepository.save(teamMember);
            return true;
        }else{
            return false;
        }
    }

    public boolean teammodify(String name,TeamInformation teamInformation){
        if(!checkTeamLeader(name,teamInformation.getId()))return false;
        MyUser user=jpaUserRepository.findUserByUsername(name);
        Team team=jpaTeamRepository.findTeamById(teamInformation.getId());
        if(team!=null) {
            if(!teamInformation.getName().equals(team.getName())){
                Team team1=jpaTeamRepository.findTeamByLeaderAndName(user,teamInformation.getName());
                if(team1==null){
                    team.setName(teamInformation.getName());
                    team.setDescription(teamInformation.getDescription());
                    jpaTeamRepository.save(team);
                    return true;
                }else{
                    return false;
                }
            }else{
                team.setName(teamInformation.getName());
                team.setDescription(teamInformation.getDescription());
                jpaTeamRepository.save(team);
                return true;
            }
        }else{
            return false;
        }
    }


    public boolean teamdelete(String name,int teamid){
        if(!checkTeamLeader(name,teamid))return false;
        MyUser user=jpaUserRepository.findUserByUsername(name);
        Team team=jpaTeamRepository.findTeamByLeaderAndId(user,teamid);
        if(team==null){
            return false;
        }else{
            //jpaTeamMemberRepository.deleteAll(team.getTeamMemberList());
            jpaTeamRepository.delete(team);
            return true;
        }
    }


    public boolean addmember(String name,int teamid,String nickname){
        if(!checkTeamLeader(name,teamid))return false;
        MyUser user=jpaUserRepository.findUserByUsername(name);
        Team team=jpaTeamRepository.findTeamByLeaderAndId(user,teamid);
        MyUser memberuser=jpaUserRepository.findMyUserByNickname(nickname);
        if(team==null||memberuser==null){
            return false;
        }else{
            TeamMember teamMember=jpaTeamMemberRepository.findTeamMemberByUseridAndTeamid(memberuser,team);
            if(teamMember!=null)return false;
            teamMember=new TeamMember();
            teamMember.setAccepted(false);
            teamMember.setTeamid(team);
            teamMember.setUserid(memberuser);
            jpaTeamMemberRepository.save(teamMember);
            return true;
        }
    }

    public boolean deletemember(String name,int teamid,int userid){
        if(!checkTeamLeader(name,teamid))return false;
        MyUser user=jpaUserRepository.findUserByUsername(name);
        Team team=jpaTeamRepository.findTeamByLeaderAndId(user,teamid);
        MyUser memberuser=jpaUserRepository.findMyUserById(userid);
        if(team==null||memberuser==null||!team.getLeader().equals(user)) {
            return false;
        }else{
            TeamMember teamMember=jpaTeamMemberRepository.findTeamMemberByUseridAndTeamid(memberuser,team);
            jpaTeamMemberRepository.delete(teamMember);
            return true;
        }
    }


    public boolean memberaccept(String name,int teamid){
        if(!checkTeamMember(name,teamid))return false;
        MyUser user=jpaUserRepository.findUserByUsername(name);
        Team team=jpaTeamRepository.findTeamById(teamid);
        if(user==null||team==null){
            return false;
        }else{
            TeamMember teamMember=jpaTeamMemberRepository.findTeamMemberByUseridAndTeamid(user,team);
            if(teamMember==null){
                return false;
            }else {
                teamMember.setAccepted(true);
                jpaTeamMemberRepository.save(teamMember);
                return true;
            }
        }
    }


    public boolean memberexit(String name,int teamid){
        if(!checkTeamMember(name,teamid))return false;
        MyUser user=jpaUserRepository.findUserByUsername(name);
        Team team=jpaTeamRepository.findTeamById(teamid);
        if(user==null||team==null){
            return false;
        }else{
            TeamMember teamMember=jpaTeamMemberRepository.findTeamMemberByUseridAndTeamid(user,team);
            if(teamMember==null){
                return false;
            }else {
                jpaTeamMemberRepository.delete(teamMember);
                return true;
            }

        }
    }
}
