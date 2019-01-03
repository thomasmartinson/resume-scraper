package scraper;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Version of a WebDriver that I can manipulate.
 */
public class ScraperDriver extends ChromeDriver {

    public ScraperDriver() {
        super(initOptions());
    }

    private static ChromeOptions initOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36",
                "disable-infobars", "chrome.switches", "--disable-extensions", "--test-type", "--start-maximized");
        if (WebScraper.HEADLESS) {
            options.addArguments("--headless");
        }

        System.setProperty("webdriver.chrome.driver", WebScraper.DRIVER_PATH);
        return options;
    }

    /**
     * whether an element on the page with the given selector is present and
     * displayed
     *
     * @param selector the selector for the page
     * @return true if there, false if not
     */
    public boolean elementIsPresent(By selector) {
        try {
            return findElement(selector).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * gives the first WebElement on the page with the given tag name
     *
     * @param str the tag name
     * @return the WebElement
     */
    public WebElement findByTag(String str) {
        return findElement(By.tagName(str));
    }

    /**
     * gives the first WebElement on the page with the given class name
     *
     * @param str the class name
     * @return the WebElement
     */
    public WebElement findByClass(String str) {
        return findElement(By.className(str));
    }

    /**
     * gives the first WebElement on the page with the given ID
     *
     * @param str the ID
     * @return the WebElement
     */
    public WebElement findById(String str) {
        return findElement(By.id(str));
    }

    /**
     * gives the first WebElement on the page with the given link text
     *
     * @param str the link text
     * @return the WebElement
     */
    public WebElement findByText(String str) {
        return findElement(By.linkText(str));
    }

    /**
     * gives all WebElements on the page with the given tag name
     *
     * @param str the tag name
     * @return all WebElements
     */
    public List<WebElement> findAllByTag(String str) {
        return findElements(By.tagName(str));
    }

    /**
     * gives all WebElements on the page with the given class name
     *
     * @param str the class name
     * @return all WebElements
     */
    public List<WebElement> findAllByClass(String str) {
        return findElements(By.className(str));
    }

    /**
     * gives all WebElements on the page with the given ID
     *
     * @param str the ID
     * @return all WebElements
     */
    public List<WebElement> findAllById(String str) {
        return findElements(By.id(str));
    }

    /**
     * gives all WebElements on the page with the given linkText
     *
     * @param str the linkText
     * @return all WebElements
     */
    public List<WebElement> findAllByText(String str) {
        return findElements(By.linkText(str));
    }
}
