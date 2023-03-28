package ru.kata.spring.boot_security.demo.configs;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (roles.contains("ROLE_ADMIN")) {
            httpServletResponse.sendRedirect("/admin");
        } else if (roles.contains("ROLE_USER")) {
            httpServletResponse.sendRedirect("/user");
        } else {
            httpServletResponse.sendRedirect("/login");
        }
    }

    /*Метод onAuthenticationSuccess вызывается после успешной аутентификации пользователя и перенаправляет
     его на соответствующую страницу в зависимости от его роли.

@param httpServletRequest: объект запроса HTTP
@param httpServletResponse: объект ответа HTTP
@param authentication: объект аутентификации, содержащий информацию о пользователе

@throws IOException: исключение в случае ошибки при перенаправлении пользователя на другую страницу

Множество roles содержит все роли, которые были присвоены пользователю.
 Если у пользователя есть роль "ROLE_ADMIN", то он будет перенаправлен на страницу администратора (/admin).
  Если у пользователя есть роль "ROLE_USER", то он будет перенаправлен на страницу пользователя (/user).
   В противном случае пользователь будет перенаправлен на страницу входа (/login).*/
}