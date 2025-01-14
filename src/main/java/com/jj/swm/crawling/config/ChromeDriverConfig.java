package com.jj.swm.crawling.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChromeDriverConfig {
    @PostConstruct
    public void init() {
        WebDriverManager.chromedriver().setup();
    }
}
