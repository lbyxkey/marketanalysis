package indi.lby.marketanalysis.service;

import indi.lby.marketanalysis.entity.MyUser;
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
        if(Objects.equals(myUser.getUsername(), "admin")){
            return new User(myUser.getUsername(),myUser.getPassword(),
                    AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
        }else{
            return new User(myUser.getUsername(),myUser.getPassword(),
                    AuthorityUtils.commaSeparatedStringToAuthorityList("user"));
        }
    }
}
