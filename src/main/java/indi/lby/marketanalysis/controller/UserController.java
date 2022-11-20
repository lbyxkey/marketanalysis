package indi.lby.marketanalysis.controller;

import indi.lby.marketanalysis.entity.MyUser;
import indi.lby.marketanalysis.projections.UserInformation;
import indi.lby.marketanalysis.repository.JpaUserRepository;
import indi.lby.marketanalysis.service.UserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    UserDetailService userDetailService;

    @PostMapping("/changepassword")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String,Boolean> addPool(Principal principal, @RequestBody Map<String,String> params){
        return userDetailService.changePassword(principal.getName(), params);
    }

    @GetMapping("/getmyuserinformation")
    public UserInformation getmyuserinformation(Principal principal){
        return userDetailService.getMyUserInformation(principal.getName());
    }

    @GetMapping("/getuserinformation/{nickname}")
    public UserInformation getuserinformation(@PathVariable("nickname") String nickname){
        return userDetailService.getUserInformation(nickname);
    }

    @GetMapping("/getuserinformationbyid/{userid}")
    public UserInformation getuserinformationbyid(@PathVariable("userid") int userid){
        return userDetailService.getuserinformationbyid(userid);
    }
    @GetMapping("/isNicknameDuplicate/{nickname}")
    public Boolean isNicknameDuplicate(Principal principal, @PathVariable("nickname") String nickname){
        return userDetailService.isNicknameDuplicate(principal.getName(), nickname);
    }

    @PostMapping("/updateuserinformation")
    public Boolean updateUserInformation(Principal principal, @RequestBody UserInformation userInformation){
        return userDetailService.updateUserInformation(principal.getName(), userInformation);
    }

}
