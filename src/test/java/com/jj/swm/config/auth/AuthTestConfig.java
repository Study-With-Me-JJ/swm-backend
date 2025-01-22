package com.jj.swm.config.auth;

import com.jj.swm.domain.auth.service.AuthService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class AuthTestConfig {

    @Autowired
    private AuthService authService;

    @Bean
    @Primary
    public AuthService testAuthService(){
        return authService;
    }

    @Bean
    public AuthService mockAuthService(){
        return Mockito.mock(AuthService.class);
    }

    @Bean
    public AuthService spyAuthService(){
        return Mockito.spy(authService);
    }

}
