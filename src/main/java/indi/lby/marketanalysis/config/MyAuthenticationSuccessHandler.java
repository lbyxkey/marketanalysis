package indi.lby.marketanalysis.config;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
//        CsrfToken csrfToken= (CsrfToken) request.getSession().getAttribute("HttpSessionCsrfTokenRepository.CSRF_TOKEN");
//        log.info(csrfToken.toString());
        response.getWriter().write(JSON.toJSON(authentication).toString());
        log.info(authentication.getName()+" login sucess "+authentication.getDetails().toString());
    }
}

