package com.jj.swm.crawling.naver.map.service;

import com.jj.swm.domain.common.KoreaRegion;
import com.jj.swm.domain.external.study.entity.ExternalStudyRoom;
import com.jj.swm.domain.external.study.repository.ExternalStudyRoomRepository;
import com.jj.swm.infra.s3.S3ClientWrapper;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
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

    private final S3ClientWrapper s3ClientWrapper;

    @Value("${spring.profiles.active:local}") // 현재 활성화된 프로파일 (기본값은 local)
    private String activeProfile;

    public NaverMapCrawlingService(ExternalStudyRoomRepository externalStudyRoomRepository, S3ClientWrapper s3ClientWrapper) {
        this.externalStudyRoomRepository = externalStudyRoomRepository;
        this.s3ClientWrapper = s3ClientWrapper;
    }

    private ChromeDriver initializeChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        if (!"local".equals(activeProfile)) {
            options.addArguments("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/237.84.2.178 Safari/537.36");
        }
        return new ChromeDriver(options);
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    public void crawl() {
        Arrays.stream(KoreaRegion.values()).toList().forEach(region -> {
            log.info("Starting Study Room crawling for region: {}", region.getKorName());
            driver = initializeChromeDriver();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // WebDriverWait 인스턴스 생성
            driver.get(BASE_URL);

            try {
                // 검색 입력 필드 찾기
                WebElement inputBox = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("input_search"))); // 요소가 로드될 때까지 기다림

                // 검색어 입력
                inputBox.sendKeys(Keys.CONTROL + "a");
                inputBox.sendKeys(Keys.BACK_SPACE);
                inputBox.sendKeys(region.getKorName() + " 스터디룸");
                inputBox.sendKeys(Keys.ENTER);

                // 페이지 로딩 후, 검색 결과 iframe으로 전환
                WebElement iframe = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("iframe#searchIframe"))); // iframe이 로드될 때까지 기다림
                driver.switchTo().frame(iframe);

                // 스크롤 다운
                WebElement scrollableElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("Ryr1F"))); // 스크롤 가능 div 대기
                Long lastHeight = (Long) driver.executeScript("return arguments[0].scrollHeight", scrollableElement);

                while (true) {
                    driver.executeScript("arguments[0].scrollTop += 1000", scrollableElement);
                    sleep(3000); // 스크롤 후 약간 대기
                    Long newHeight = (Long) driver.executeScript("return arguments[0].scrollHeight", scrollableElement);
                    if (Objects.equals(newHeight, lastHeight)) {
                        // scroll to top
                        driver.executeScript("arguments[0].scrollTop = 0", scrollableElement);
                        break;
                    }
                    lastHeight = newHeight;
                }

                sleep(1000);

                // 스터디룸 리스트 검색
                List<WebElement> studyRooms = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".VLTHu.OW9LQ"))); // 스터디룸 리스트 대기
                sleep(2000);
                log.info("Found {} study rooms for region: {}", studyRooms.size(), region.getKorName());
                for (WebElement room : studyRooms) {
                    try {
                        String thumbnail = getAttributeSafely(By.cssSelector(".place_thumb img"), room, "src");

                        // 클릭하기 전에 클릭 가능 상태인지 확인
                        WebElement clickableElement = wait.until(ExpectedConditions.elementToBeClickable(room.findElement(By.cssSelector(".place_bluelink.C6RjW"))));
                        driver.executeScript("arguments[0].click()", clickableElement);
                        sleep(1500);

                        // 기본 프레임으로 전환하고 상세 페이지 iframe으로 이동
                        driver.switchTo().defaultContent();
                        WebElement detailIframe = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("iframe#entryIframe")));
                        driver.switchTo().frame(detailIframe);

                        // 상세 정보 추출
                        String roomName = getElementTextSafely(By.className("GHAhO"), driver);
                        String address = getElementTextSafely(By.className("LDgIH"), driver);

                        // 내용 확장
                        try {
                            WebElement descriptionExpandButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("xHaT3")));
                            descriptionExpandButton.click();
                        } catch (NoSuchElementException | TimeoutException ignored) {
                            // 내용 확장 버튼이 없는 경우 무시
                        }

                        String description = getElementTextSafely(By.className("zPfVt"), driver);
                        String number = getElementTextSafely(By.className("xlx7Q"), driver);
                        String link = getElementTextSafely(By.className("jO09N"), driver);

                        String url = driver.getCurrentUrl();
                        String id = parsePlaceId(url);
                        if (thumbnail != null) {
                            thumbnail = s3ClientWrapper.putImageFromUrl(true, thumbnail);
                        }
                        // 데이터베이스에 저장
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

                        // 검색 결과 iframe으로 복귀
                        driver.switchTo().defaultContent();
                        driver.switchTo().frame(iframe);
                        sleep(1000);
                    } catch (Exception e) {
                        log.error("Error while processing study room: {}", e.getMessage());
                    }
                }

            } catch (Exception e) {
                log.error("Error while processing region {}: {}", region, e.getMessage());
            } finally {
                driver.quit(); // 드라이버 정리
            }
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
