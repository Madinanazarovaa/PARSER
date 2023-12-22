package kz.kaspi;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class Parser {
    private WebDriver driver;
    private FileWriter writer;
    private boolean hasNextPage = true;
    public Parser() {
        driver = new EdgeDriver();
    }
    private static final Logger logger = LoggerFactory.getLogger(Parser.class);

    /**
     * Инициализирует WebDriver, открывает указанный URL, и закрывает всплывающее окно, если оно присутствует.
     *
     * @param url URL для открытия веб-страницы.
     */
    public void initialize(String url) {
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".dialog__close")));
        WebElement popupElement = driver.findElement(By.cssSelector(".dialog__close"));
        popupElement.click();
    }

    /**
     * Создает новый файл с заданным именем и инициализирует FileWriter.
     * @param fileName Имя файла для создания.
     */
    public void createFile(String fileName){
        try {
            writer = new FileWriter(fileName);
        } catch (IOException e) {
            logger.error("Ошибка при работе с FileWriter", e);
        }
    }

    /**
     * Метод выполняет парсинг страницы товаров, извлекая информацию о названии и цене каждого товара.
     * Парсинг продолжается до достижения конца списка страниц.
     */
    public void parse() {
        int pageCounter = 1;
        logger.info("Страница: {}", pageCounter);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            while (hasNextPage) {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".item-card__info")));

                List<WebElement> productElements = driver.findElements(By.cssSelector(".item-card__info"));

                for (WebElement productElement : productElements) {
                    String name = productElement.findElement(By.cssSelector(".item-card__name-link")).getText();
                    String price = productElement.findElement(By.cssSelector(".item-card__prices-price")).getText();

                    writeInfo(name + " " + price + "\n");
                }
                List<WebElement> nextButtonElements = driver.findElements(By.xpath("//li[contains(@class,'pagination__el') and contains(text(), 'Следующая')]"));

                if (!nextButtonElements.isEmpty() && !nextButtonElements.get(0).getAttribute("class").contains("_disabled")) {
                    pageCounter++;
                    logger.info("Страница: {}", pageCounter);
                    nextButtonElements.get(0).click();
                    Thread.sleep(500);
                    wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".item-card__info"))));
                } else {
                    logger.info("Достигнут конец списка страниц");
                    hasNextPage = false;
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            closeResources();
        }
    }

    /**
     * Метод записывает переданную информацию в файл или другой механизм хранения данных.
     * @param info Информация, которую необходимо записать.
     */
    private void writeInfo(String info) {
        try {
            writer.write(info);
        } catch (IOException e) {
            logger.error("Ошибка при записи в файл", e);
        }
    }

    /**
     * Метод закрывает ресурсы, такие как FileWriter и WebDriver.
     * В случае ошибок при закрытии FileWriter, метод записывает сообщение об ошибке в лог.
     * После закрытия ресурсов, метод завершает работу WebDriver.
     */
    private void closeResources() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            logger.error("Ошибка при закрытии writer.", e);
        }
        driver.quit();
    }
}
