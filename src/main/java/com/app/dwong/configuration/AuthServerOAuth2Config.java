package com.app.dwong.configuration;

import com.app.dwong.CustomTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;


@Configuration
@EnableAuthorizationServer
public class AuthServerOAuth2Config {

    @Autowired
    private AuthenticationManager authenticationManager;


    @Bean
    public AuthorizationServerConfigurer authorizationServerConfigurer() {
        return new AuthorizationServerConfigurer() {

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
                        .scopes("write");
            }

            @Override
            public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

                TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
                tokenEnhancerChain.setTokenEnhancers(
                        Arrays.asList(tokenEnhancer(), accessTokenConverter()));

                endpoints.tokenStore(tokenStore())
                        .tokenEnhancer(tokenEnhancerChain)
                        .accessTokenConverter(accessTokenConverter())
                        .authenticationManager(authenticationManager);
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
    public DefaultTokenServices tokenServices() {
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