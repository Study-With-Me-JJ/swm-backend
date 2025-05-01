package com.jj.swm.config.user;

import com.jj.swm.domain.user.core.service.UserCommandService;
import com.jj.swm.domain.user.core.service.UserQueryService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class UserTestConfig {

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private UserQueryService userQueryService;

    @Bean
    @Primary
    public UserCommandService testUserCommandService(){
        return userCommandService;
    }

    @Bean
    public UserCommandService mockUserCommandService(){
        return Mockito.mock(UserCommandService.class);
    }

    @Bean
    public UserCommandService spyUserCommandService(){
        return Mockito.spy(userCommandService);
    }

    @Bean
    @Primary
    public UserQueryService testUserQueryService(){
        return userQueryService;
    }

    @Bean
    public UserQueryService mockUserQueryService(){
        return Mockito.mock(UserQueryService.class);
    }

    @Bean
    public UserQueryService spyUserQueryService(){
        return Mockito.spy(userQueryService);
    }
}
