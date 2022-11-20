package indi.lby.marketanalysis.controller;

import indi.lby.marketanalysis.projections.TeamInformation;
import indi.lby.marketanalysis.projections.TeamUserInformation;
import indi.lby.marketanalysis.projections.UserInformation;
import indi.lby.marketanalysis.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {
    @Autowired
    TeamService teamService;

    @GetMapping("/getmyteam")
    public List<TeamInformation> getmyteam(Principal principal){
        return teamService.getMyTermInformation(principal.getName());
    }

    @GetMapping("/getjoinedteams")
    public List<TeamInformation> getjoinedteams(Principal principal){
        return teamService.getjoinedteams(principal.getName());
    }

    @GetMapping("/getmember/{teamid}")
    public List<TeamUserInformation> getmember(Principal principal, @PathVariable("teamid") int teamid){
        return teamService.getmember(principal.getName(), teamid);
    }

    @PostMapping("/teamadd")
    public boolean teamadd(Principal principal,@RequestBody TeamInformation teamInformation){
        return teamService.teamadd(principal.getName(),teamInformation);
    }

    @PostMapping("/teammodify")
    public boolean teammodify(Principal principal,@RequestBody TeamInformation teamInformation){
        return teamService.teammodify(principal.getName(),teamInformation);
    }

    @GetMapping("/teamdelete/{teamid}")
    public boolean teamdelete(Principal principal,@PathVariable("teamid") int teamid){
        return teamService.teamdelete(principal.getName(),teamid);
    }

    @GetMapping("/addmember/{teamid}/{nickname}")
    public boolean addmember(Principal principal,@PathVariable("teamid") int teamid,@PathVariable("nickname") String nickname){
        return teamService.addmember(principal.getName(),teamid,nickname);
    }

    @GetMapping("/deletemember/{teamid}/{userid}")
    public boolean deletemember(Principal principal,@PathVariable("teamid") int teamid,
                                @PathVariable("userid") int  userid){
        return teamService.deletemember(principal.getName(),teamid,userid);
    }

    @GetMapping("/memberaccept/{teamid}")
    public boolean memberaccept(Principal principal,@PathVariable("teamid") int teamid){
        return teamService.memberaccept(principal.getName(),teamid);
    }

    @GetMapping("/memberexit/{teamid}")
    public boolean memberexit(Principal principal,@PathVariable("teamid") int teamid){
        return teamService.memberexit(principal.getName(),teamid);
    }

}
