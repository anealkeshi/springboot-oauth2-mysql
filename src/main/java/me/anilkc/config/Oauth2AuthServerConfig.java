package me.anilkc.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import me.anilkc.service.CustomUserDetailsService;

@Configuration
@EnableAuthorizationServer
public class Oauth2AuthServerConfig extends AuthorizationServerConfigurerAdapter {

  @Autowired
  @Qualifier("authenticationManagerBean")
  private AuthenticationManager auth;

  @Autowired
  private DataSource dataSource;

  @Resource(name = "customUserDetailsService")
  private CustomUserDetailsService userDetailsService;

  @Bean
  public JdbcTokenStore tokenStore() {
    return new JdbcTokenStore(dataSource);
  }

  @Bean
  protected AuthorizationCodeServices authorizationCodeServices() {
    return new JdbcAuthorizationCodeServices(dataSource);
  }

  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
 // @formatter:off
    endpoints
          .authorizationCodeServices(authorizationCodeServices())
          .authenticationManager(auth)
          .userDetailsService(userDetailsService)
          .tokenStore(tokenStore())
          .approvalStoreDisabled();
 // @formatter:off
  }

  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    // @formatter:off
    clients.jdbc(dataSource)
           .withClient("clientA")
           .secret("secreta")
           .scopes("READ")
           .autoApprove(true)
           .accessTokenValiditySeconds(600)
           .refreshTokenValiditySeconds(600)
           .authorizedGrantTypes("implicit", "refresh_token", "password", "authorization_code", "client_credentials")
           .and()
           .withClient("clientB")
           .secret("secretb")
           .scopes("WRITE")
           .autoApprove(true)
           .accessTokenValiditySeconds(600)
           .refreshTokenValiditySeconds(600)
           .authorizedGrantTypes("implicit", "refresh_token", "password", "authorization_code", "client_credentials");
  // @formatter:on
}
}
