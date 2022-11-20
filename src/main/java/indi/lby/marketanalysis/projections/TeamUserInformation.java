package indi.lby.marketanalysis.projections;

import indi.lby.marketanalysis.entity.MyUser;
import indi.lby.marketanalysis.entity.TeamMember;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamUserInformation extends UserInformation{
    boolean accepted;
    public TeamUserInformation(TeamMember teamMember) {
        this.userid=teamMember.getUserid().getId();
        this.nickname=teamMember.getUserid().getNickname();
        this.comment=teamMember.getUserid().getComment();
        this.contact=teamMember.getUserid().getContact();
        this.accepted=teamMember.isAccepted();
    }
}
