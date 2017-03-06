package com.app.dwong.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

/**
 * Created by dwong on 3/3/17.
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration{

    @Bean
    public ResourceServerConfigurer resourceServerConfigurer(ResourceServerTokenServices tokenServices) {
        return new ResourceServerConfigurer() {

            @Override
            public void configure(ResourceServerSecurityConfigurer config) {
                config.tokenServices(tokenServices);
            }

            @Override
            public void configure(HttpSecurity http) throws Exception {

                http
                        .authorizeRequests()
                        .antMatchers("/me",  "/user").authenticated()
                        .and().exceptionHandling().authenticationEntryPoint(oAuth2AuthenticationEntryPoint());
            }
        };
    }

    private OAuth2AuthenticationEntryPoint oAuth2AuthenticationEntryPoint () {
        return new OAuth2AuthenticationEntryPoint();
    }
}
