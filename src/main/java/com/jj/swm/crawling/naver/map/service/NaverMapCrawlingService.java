package com.jj.swm.crawling.naver.map.service;

import com.jj.swm.crawling.naver.map.constants.KoreaRegion;
import com.jj.swm.crawling.naver.map.entity.ExternalStudyRoom;
import com.jj.swm.crawling.naver.map.repository.ExternalStudyRoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class NaverMapCrawlingService implements DisposableBean {
    private final ExternalStudyRoomRepository externalStudyRoomRepository;
    private ChromeDriver driver;

    private static final String BASE_URL = "https://map.naver.com/p/search";

    public NaverMapCrawlingService(ExternalStudyRoomRepository externalStudyRoomRepository) {
        this.externalStudyRoomRepository = externalStudyRoomRepository;
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    public void crawl() {
        Arrays.stream(KoreaRegion.values()).toList().forEach(region -> {
            driver = new ChromeDriver();
            driver.get(BASE_URL);

            // 검색 입력 필드 찾기
            WebElement inputBox = driver.findElement(By.className("input_search"));

            // 검색어 입력
            inputBox.sendKeys(Keys.CONTROL + "a");
            inputBox.sendKeys(Keys.BACK_SPACE);
            inputBox.sendKeys(region.getKorName() + " 스터디룸");
            inputBox.sendKeys(Keys.ENTER);

            // 대기 - 페이지 로딩
            sleep(1000);

            driver.switchTo().frame(driver.findElement(By.cssSelector("iframe#searchIframe")));

            sleep(1000);
            // 스크롤 다운

            var scrollableElement = driver.findElement(By.className("Ryr1F"));
            Long lastHeight = (Long) driver.executeScript("return arguments[0].scrollHeight", scrollableElement);

            while (true) {
                driver.executeScript("arguments[0].scrollTop += 600", scrollableElement);
                sleep(1500);
                Long newHeight = (Long) driver.executeScript("return arguments[0].scrollHeight", scrollableElement);
                if (Objects.equals(newHeight, lastHeight)) {
                    break;
                }
                lastHeight = newHeight;
            }

            List<WebElement> studyRooms = driver.findElements(By.cssSelector(".VLTHu.OW9LQ"));

            for (WebElement room : studyRooms) {
                String thumbnail = getAttributeSafely(By.cssSelector(".place_thumb img"), room, "src");
                room.findElement(By.cssSelector(".place_bluelink.C6RjW")).click();
                sleep(1500);
                driver.switchTo().parentFrame();
                driver.switchTo().frame(driver.findElement(By.cssSelector("iframe#entryIframe")));

                String roomName = getElementTextSafely(By.className("GHAhO"), driver);
                String address = getElementTextSafely(By.className("LDgIH"), driver);

                // 내용 확장
                try {
                    driver.findElement(By.className("xHaT3")).click();
                } catch (NoSuchElementException ignored) {
                }

                String description = getElementTextSafely(By.className("zPfVt"), driver);
                String number = getElementTextSafely(By.className("xlx7Q"), driver);
                String link = getElementTextSafely(By.className("jO09N"), driver);

                String url = driver.getCurrentUrl();
                String id = parsePlaceId(url);

                externalStudyRoomRepository.save(ExternalStudyRoom.builder()
                        .id(Long.valueOf(id))
                        .address(address)
                        .url(link)
                        .naverMapUrl(url)
                        .name(roomName)
                        .thumbnail(thumbnail)
                        .number(number)
                        .description(description)
                        .koreaRegion(region)
                        .build());


                driver.switchTo().parentFrame();
                driver.switchTo().frame(driver.findElement(By.cssSelector("iframe#searchIframe")));
                sleep(1000);
            }

            driver.switchTo().parentFrame();
        });
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getElementTextSafely(By selector, ChromeDriver context) {
        try {
            return context.findElement(selector).getText();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private String getAttributeSafely(By selector, WebElement context, String attribute) {
        try {
            return context.findElement(selector).getAttribute(attribute);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public static String parsePlaceId(String url) {
        String regex = "place/(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    @Override
    public void destroy() {
        driver.quit();
    }
}
