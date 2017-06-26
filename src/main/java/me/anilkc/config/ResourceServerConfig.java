package me.anilkc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

import me.anilkc.config.handler.CustomAccessDeniedHandler;

@Configuration
@EnableResourceServer
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER - 1)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

  @Autowired
  private CustomAccessDeniedHandler accessDeniedHandler;
  
  @Override
  public void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http
    .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
    .and()
    .requestMatchers().antMatchers("/secure/**")    
    .and()
    .authorizeRequests()
    .antMatchers("/secure/admin").access("#oauth2.hasScope('READ') and hasRole('ROLE_ADMIN')")
    .antMatchers("/secure/**").authenticated();
  // @formatter:on
}
}
