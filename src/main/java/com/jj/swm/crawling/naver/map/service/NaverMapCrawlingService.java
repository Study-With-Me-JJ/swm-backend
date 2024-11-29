package com.jj.swm.crawling.naver.map.service;

import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

@Service
public class NaverMapCrawlingService implements DisposableBean {
    private ChromeDriver driver;

    private static final String BASE_URL = "https://map.naver.com/p";



    @Override
    public void destroy() throws Exception {
        driver.quit();
    }
}
