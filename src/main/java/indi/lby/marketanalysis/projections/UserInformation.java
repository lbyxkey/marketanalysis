package indi.lby.marketanalysis.projections;

import indi.lby.marketanalysis.entity.MyUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInformation implements Serializable {
    int userid;

    String nickname;

    String comment;

    String contact;

    public UserInformation(MyUser myUser) {
        this.userid=myUser.getId();
        this.nickname=myUser.getNickname();
        this.comment=myUser.getComment();
        this.contact=myUser.getContact();
    }
}
