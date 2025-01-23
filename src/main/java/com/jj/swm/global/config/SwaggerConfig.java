package com.jj.swm.global.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    static {
        io.swagger.v3.core.jackson.ModelResolver.enumsAsRef = true;
    }
}
