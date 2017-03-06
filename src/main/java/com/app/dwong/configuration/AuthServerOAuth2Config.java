package com.app.dwong.configuration;

import com.app.dwong.CustomTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.AuthorizationServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;


@Configuration
@EnableAuthorizationServer
@EnableConfigurationProperties({AuthorizationServerProperties.class})
@EnableWebSecurity
public class AuthServerOAuth2Config extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {

        OAuth2AuthenticationManager authenticationManager = new OAuth2AuthenticationManager();
        authenticationManager.setTokenServices(tokenServices());
        OAuth2AuthenticationProcessingFilter oAuth2AuthenticationProcessingFilter = new OAuth2AuthenticationProcessingFilter();
        oAuth2AuthenticationProcessingFilter.setAuthenticationManager(authenticationManager);

        http
                .csrf().disable()
                .authorizeRequests()
                .and().formLogin().loginPage("/login").successForwardUrl("/oauth/confirm_access")
                .permitAll()
                .and()
                .addFilterBefore(oAuth2AuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class);

    }

    @Configuration
    @Order(-20)
    static class LoginConfig extends WebSecurityConfigurerAdapter {



        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .formLogin().loginPage("/login").permitAll()
                    .and()
                    .requestMatchers()
                    .antMatchers("/", "/login", "/oauth/authorize", "/oauth/confirm_access")
                    .and()
                    .authorizeRequests()
                    .anyRequest().authenticated();
        }
    }

    @Bean
    public AuthorizationServerConfigurer authorizationServerConfigurer() {
        return new AuthorizationServerConfigurer() {

            @Autowired
            ResourceServerTokenServices tokenServices;


            @Override
            public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
                security
                        .tokenKeyAccess("permitAll()")
                        .checkTokenAccess("isAuthenticated()");
            }

            @Override
            public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
                clients.inMemory()
                        .withClient("acme")
                        .secret("acmesecret")
                        .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
                        .scopes("write", "read");
            }

            @Override
            public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

                TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
                tokenEnhancerChain.setTokenEnhancers(
                        Arrays.asList(tokenEnhancer(), accessTokenConverter()));

                endpoints.tokenStore(tokenStore())
                        .tokenEnhancer(tokenEnhancerChain)
                        .accessTokenConverter(accessTokenConverter())
                        .authenticationManager(authenticationManager());
            }
        };
    }


    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("123");
        return converter;
    }

    @Bean
    @Primary
    public ResourceServerTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
    }
}