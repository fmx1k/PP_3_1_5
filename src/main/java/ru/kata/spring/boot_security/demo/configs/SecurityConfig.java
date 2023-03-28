package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import ru.kata.spring.boot_security.demo.service.UserService;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final SuccessUserHandler successUserHandler;

    @Autowired
    public SecurityConfig(SuccessUserHandler successUserHandler, UserService userService) {
        this.successUserHandler = successUserHandler;
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests() // начало настройки авторизации запросов
                .antMatchers("/").permitAll() // доступ к главной странице разрешен всем
                .antMatchers("/user/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN") // доступ к страницам пользователям и админам
                .antMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN") // доступ к страницам администраторам
                .anyRequest().authenticated() // все другие запросы должны быть аутентифицированы
                .and()
                .formLogin() // начало настройки формы для входа
                .loginPage("/login") // страница для входа на сайт
                .successHandler(successUserHandler) // обработчик успешной авторизации
                .permitAll() // доступ к странице входа всем разрешен
                .and()
                .logout() // начало настройки выхода пользователя
                .permitAll() // доступ к выходу пользователя всем разрешен
                .logoutRequestMatcher((new AntPathRequestMatcher("/logout"))) // URL для выхода пользователя
                .logoutSuccessUrl("/login") // URL для перенаправления после успешного выхода
                .and().csrf().disable(); // отключение защиты от CSRF атак
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService((UserDetailsService) userService);
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        return authProvider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}