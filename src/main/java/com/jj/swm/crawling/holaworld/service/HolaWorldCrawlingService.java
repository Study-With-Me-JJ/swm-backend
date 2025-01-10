package com.jj.swm.crawling.holaworld.service;

import com.jj.swm.domain.external.study.entity.ExternalStudy;
import com.jj.swm.domain.external.study.repository.ExternalStudyRepository;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@Profile("!test")
public class HolaWorldCrawlingService implements DisposableBean {
    private final ExternalStudyRepository externalStudyRepository;
    private ChromeDriver driver;

    private static final String BASE_URL = "https://holaworld.io/";
    private static final int MAX_PAGES = 10; // 최대 페이지 개수 설정

    public HolaWorldCrawlingService(ExternalStudyRepository externalStudyRepository) {
        this.externalStudyRepository = externalStudyRepository;
    }

    private ChromeDriver initializeChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // GUI 없는 환경에서 실행
        options.addArguments("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        return new ChromeDriver(options);
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    public void crawl() {
        log.info("Starting HolaWorld crawling...");
        driver = initializeChromeDriver();

        try {
            driver.get(BASE_URL);

            for (int page = 0; page < MAX_PAGES; page++) {
                log.info("Processing page: {}", page + 1);

                // 페이지에서 학습 데이터 가져오기
                extractStudiesFromPage(driver);

                // 다음 페이지로 이동
                if (!goToNextPage(driver)) {
                    log.info("No more pages to process. Stopping.");
                    break;
                }

                // 페이지 이동 후 대기
                sleep(3000);
            }
        } catch (Exception e) {
            log.error("Error during HolaWorld crawling: {}", e.getMessage(), e);
        } finally {
            driver.quit();
        }
    }

    private void extractStudiesFromPage(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // 학습 데이터 요소를 찾음
            List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@id=\"root\"]/main/ul/a")));
            List<ExternalStudy> externalStudies = new ArrayList<>();
            for (WebElement element : elements) {
                try {
                    // 학습 데이터 정보 추출
                    String title = element.findElement(By.tagName("h1")).getText();

                    // 역할(role) 정보
                    List<WebElement> positionElements = element.findElements(By.className("studyItem_position__2sRRD"));
                    String roles = String.join(", ", positionElements.stream().map(WebElement::getText).toArray(String[]::new));

                    // 기술 스택(technologies) 정보
                    List<WebElement> technologyElements = element.findElement(By.className("studyItem_content__1mJ9M"))
                            .findElements(By.className("studyItem_language__20yqw"));
                    String technologies = String.join(", ", technologyElements.stream()
                            .map(e -> e.findElement(By.tagName("img")).getAttribute("title"))
                            .toArray(String[]::new));

                    String deadlineDateString = element.findElement(By.cssSelector(".studyItem_schedule__3oAnA p:nth-child(2)")).getText();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
                    LocalDate deadlineDate = LocalDate.parse(deadlineDateString, formatter);
                    // 학습 고유 ID 추출
                    String link = element.getAttribute("href");
                    String studyId = link.replace(BASE_URL + "study/", "");
                    externalStudies.add(ExternalStudy.builder()
                            .id(studyId)
                            .title(title)
                            .link(link)
                            .technologies(technologies)
                            .roles(roles)
                            .deadlineDate(deadlineDate)
                            .build());
                } catch (NoSuchElementException | TimeoutException e) {
                    log.warn("Error extracting study data: {}", e.getMessage());
                }
            }
            externalStudyRepository.saveAll(externalStudies);
        } catch (TimeoutException e) {
            log.error("Timeout while waiting for study elements: {}", e.getMessage());
        }
    }

    private boolean goToNextPage(WebDriver driver) {
        try {
            WebElement nextPageButton = driver.findElement(By.xpath("//*[@id=\"root\"]/main/div[2]/nav/ul/li[10]/button"));
            if (nextPageButton.isEnabled()) {
                nextPageButton.click();
                return true;
            }
        } catch (NoSuchElementException e) {
            log.info("Next page button not found. Stopping.");
        }
        return false;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void destroy() {
        if (driver != null) {
            driver.quit();
        }
    }
}
