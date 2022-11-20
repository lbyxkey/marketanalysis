package indi.lby.marketanalysis.service;

import indi.lby.marketanalysis.entity.MyUser;
import indi.lby.marketanalysis.projections.UserInformation;
import indi.lby.marketanalysis.repository.JpaUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class UserDetailService implements UserDetailsService {

    @Autowired
    JpaUserRepository jpaUserRepository;

    @Autowired
    PasswordEncoder pw;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUser myUser = jpaUserRepository.findUserByUsername(username);
        if(myUser ==null){
            throw new UsernameNotFoundException("用户名不存在");
        }
        User user;
        if(Objects.equals(myUser.getUsername(), "admin")){
            user= new User(myUser.getUsername(),myUser.getPassword(),
                    AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
        }else{
            user= new User(myUser.getUsername(),myUser.getPassword(),
                    AuthorityUtils.commaSeparatedStringToAuthorityList("user"));
        }
        return user;
    }

    public Map<String,Boolean> changePassword(String name,Map<String,String> map){
        Map<String,Boolean> result=new HashMap<>();
        MyUser user=jpaUserRepository.findUserByUsername(name);
        if(user==null){
            result.put("result",false);
        }else{
            String oldpassword=map.getOrDefault("oldpassword","");
            String newpassword=map.getOrDefault("newpassword","");
            if(!oldpassword.isEmpty()&&!newpassword.isEmpty()&&pw.matches(oldpassword, user.getPassword())){
                user.setPassword(pw.encode(newpassword));
                jpaUserRepository.save(user);
                result.put("result",true);
            }else{
                result.put("result",false);
            }
        }
        return result;
    }

    public UserInformation getUserInformation(String nickname){
        MyUser user=jpaUserRepository.findMyUserByNickname(nickname);
        if(user==null){
            return new UserInformation();
        }else{
            return new UserInformation(user);
        }
    }

    public UserInformation getuserinformationbyid(int userid){
        MyUser user=jpaUserRepository.findMyUserById(userid);
        if(user==null){
            return new UserInformation();
        }else{
            return new UserInformation(user);
        }
    }

    public UserInformation getMyUserInformation(String username){
        UserInformation userInformation=new UserInformation();
        MyUser user=jpaUserRepository.findUserByUsername(username);
        if(user==null)return userInformation;
        userInformation.setUserid(user.getId());
        userInformation.setNickname(user.getNickname());
        userInformation.setContact(user.getContact());
        userInformation.setComment(user.getComment());
        return userInformation;
    }

    public boolean isNicknameDuplicate(String username,String nickname){
        MyUser user=jpaUserRepository.findMyUserByNickname(nickname);
        if(user==null){
            return false;
        }else if(user.getUsername().equals(username)){
            return false;
        }else{
            return true;
        }
    }

    public boolean updateUserInformation(String username,UserInformation userInformation){
        if(isNicknameDuplicate(username,userInformation.getNickname())){
            return false;
        }
        MyUser user=jpaUserRepository.findUserByUsername(username);
        user.setNickname(userInformation.getNickname());
        user.setContact(userInformation.getContact());
        user.setComment(userInformation.getComment());
        jpaUserRepository.save(user);
        return true;
    }
}
