package indi.lby.marketanalysis.projections;


import indi.lby.marketanalysis.entity.Team;
import indi.lby.marketanalysis.entity.TeamMember;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamInformation  implements Serializable {
    public TeamInformation(Team team){
        this.id=team.getId();
        this.leader=team.getLeader().getId();
        this.name= team.getName();
        this.description=team.getDescription();
        this.accepted=true;
    }

    public TeamInformation(TeamMember teamMember){
        this.id=teamMember.getTeamid().getId();
        this.leader=teamMember.getTeamid().getLeader().getId();
        this.name= teamMember.getTeamid().getName();
        this.description=teamMember.getTeamid().getDescription();
        this.accepted=teamMember.isAccepted();
    }
    int id;
    int leader;
    String name;
    String description;

    Boolean accepted;

}
