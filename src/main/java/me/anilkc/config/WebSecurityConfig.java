package me.anilkc.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import me.anilkc.config.handler.CustomAccessDeniedHandler;
import me.anilkc.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity(debug = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private DataSource jdbcDatasource;

  @Resource(name = "customUserDetailsService")
  private CustomUserDetailsService userDetailsService;

  @Autowired
  private CustomAccessDeniedHandler accessDeniedHandler;

  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
  // @formatter:off
      auth.userDetailsService(userDetailsService)
        .and().jdbcAuthentication().dataSource(jdbcDatasource);
  // @formatter:on

  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
  // @formatter:off
    http.csrf().disable()
    .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
    .and()
    .authorizeRequests().antMatchers("/oauth/authorize").authenticated()
    .and()
    .formLogin().permitAll()
    .and()
    .logout()
    .and()
    .authorizeRequests().anyRequest().authenticated();
  // @formatter:on
  }

}
