package com.jj.swm.crawling.naver.map.service;

import com.jj.swm.crawling.naver.map.constants.KoreaRegion;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class NaverMapCrawlingService implements DisposableBean {
    private ChromeDriver driver;

    private static final String BASE_URL = "https://map.naver.com/p/search";

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    public void crawl() throws InterruptedException {
        // 1. 각 지역 이름에 "스터디룸"을 붙인 검색어 리스트 생성
        List<String> searchKeywords = Arrays.stream(KoreaRegion.values())
                .map(region -> region.getKorName() + " 스터디룸")
                .toList(); // 결과를 리스트로 수집

        // 2. ChromeDriver 초기화 및 URL로 이동
        driver = new ChromeDriver();
        driver.get(BASE_URL);

        // 3. 검색어마다 크롤링을 수행
        for (String keyword : searchKeywords) {
            // 검색 입력 필드 찾기
            WebElement inputBox = driver.findElement(By.className("input_search"));

            // 검색어 입력
            inputBox.sendKeys(Keys.CONTROL + "a");
            inputBox.sendKeys(Keys.BACK_SPACE);
            inputBox.sendKeys(keyword);
            inputBox.sendKeys(Keys.ENTER);

            // 대기 - 페이지 로딩
            Thread.sleep(1000);

            driver.switchTo().frame(driver.findElement(By.cssSelector("iframe#searchIframe")));

            List<WebElement> studyRooms = driver.findElements(By.cssSelector("VLTHu.OW9LQ"));

            for (WebElement room : studyRooms) {
                System.out.println(room.getText());
                Thread.sleep(1000);
            }

            driver.switchTo().parentFrame();
        }
    }


    @Override
    public void destroy() {
        driver.quit();
    }
}
