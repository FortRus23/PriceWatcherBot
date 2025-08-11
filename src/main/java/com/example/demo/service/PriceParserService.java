package com.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;

@Service
public class PriceParserService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static WebDriver getDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/139.0.0.0 Safari/537.36");
        options.addArguments("--lang=ru-RU");

        return new ChromeDriver(options);
    }

    public record PriceInfo(String title, BigDecimal price, BigDecimal originalPrice) {}

    public PriceInfo parseprice(String url) {
        System.setProperty("webdriver.chrome.driver", "driver/chromedriver.exe");

        WebDriver driver = getDriver();

        BigDecimal price = null;
        String title = null;
        BigDecimal originalPrice = null;

        try {
            driver.get("https://www.ozon.ru");
            Thread.sleep(5000);
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("h1.tsHeadline550Medium")
            ));

            title = driver.findElement(By.cssSelector("h1.tsHeadline550Medium")).getText();

            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[data-widget='webProductHeading']")));

            WebElement dataStateDiv = driver.findElement(By.cssSelector("div[id^='state-webPrice-']"));

            String dataStateJson = dataStateDiv.getAttribute("data-state");

            if (dataStateJson != null && !dataStateJson.isEmpty()) {
                JsonNode rootNode = objectMapper.readTree(dataStateJson);
                String priceStr = rootNode.path("price").asText(null);
                String originalPriceStr = rootNode.path("originalPrice").asText(null);

                price = parsePriceToBigDecimal(priceStr);
                originalPrice = parsePriceToBigDecimal(originalPriceStr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        } return new PriceInfo(title, price, originalPrice);
    }

    private BigDecimal parsePriceToBigDecimal(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) return BigDecimal.ZERO;
        String clean = priceStr.replaceAll("[^\\d.]", "");
        if (clean.isEmpty()) return BigDecimal.ZERO;
        return new BigDecimal(clean);
    }
}



