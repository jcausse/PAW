package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.model.Role;
import ar.edu.itba.paw.webapp.auth.AuthUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@ComponentScan("ar.edu.itba.paw.webapp.auth")

public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    private final AuthUserDetailsService userDetailsService;

    @Autowired
    public WebAuthConfig(final AuthUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.userDetailsService(userDetailsService).sessionManagement()

            /* Invalid Sessions */
            .invalidSessionUrl("/login")

            /* Access Control */
            .and().authorizeRequests()
                .antMatchers("/login", "/register").anonymous()
                .antMatchers("/logout").authenticated()
                .antMatchers("/admin/**").hasRole(Role.ADMIN.getRoleName())
                .antMatchers("/listing/**").permitAll()
                .anyRequest().authenticated()

            /* Login */
            .and().formLogin()
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/", false)

            /* Logout */
            .and().logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")

            /* Remember Me */
            .and().rememberMe()
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30))

            /* Exceptions */
            .and().exceptionHandling()
                .accessDeniedPage("/403")

            /* Miscellaneous */
            .and().csrf().disable();

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/css/**",
                "/js/**",
                "/image/**",
                "/static-image/**",
                "/favicon.ico"
        );
    }
}
