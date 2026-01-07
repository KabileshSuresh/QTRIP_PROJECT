package qtriptest.tests;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.function.Function;
import java.util.regex.Pattern;

import qtriptest.DP;
import qtriptest.DriverSingleton;
import qtriptest.pages.AdventureDetailsPage;
import qtriptest.pages.AdventurePage;
import qtriptest.pages.HomePage;
import qtriptest.pages.HistoryPage;
import qtriptest.pages.LoginPage;
import qtriptest.pages.RegisterPage;

public class testCase_04 {

    WebDriver driver;
    RegisterPage register;
    LoginPage login;
    HomePage home;
    AdventurePage adventure;
    AdventureDetailsPage details;
    HistoryPage history;
    
    @BeforeMethod(alwaysRun = true)
    public void setup() {
    driver = DriverSingleton.getDriver();
    driver.manage().window().maximize();
    driver.manage().deleteAllCookies();
    driver.get("https://qtripdynamic-qa-frontend.vercel.app/");
    
    register = new RegisterPage(driver);
    login = new LoginPage(driver);
    home = new HomePage(driver);
    adventure = new AdventurePage(driver);
    details = new AdventureDetailsPage(driver);
    history = new HistoryPage(driver);
    
    }
    
    @Test(dataProvider = "data-provider", dataProviderClass = DP.class, priority = 4, groups = {"Reliability Flow"})
    
    public void TestCase04(String tcId, String username, String password, String dataset1, String dataset2, String dataset3) throws InterruptedException {
    
    WebDriverWait wait = new WebDriverWait(driver, 30);

    String email = "user" + System.currentTimeMillis() + "@test.com";

    driver.manage().deleteAllCookies();
    
    driver.get("https://qtripdynamic-qa-frontend.vercel.app/pages/register/");
    waitForPageLoad(wait);
    register.registerUser(email, password);
    driver.manage().deleteAllCookies();
    
    driver.get("https://qtripdynamic-qa-frontend.vercel.app/pages/login/");
    
    waitForPageLoad(wait);
    
    login.loginUser(email, password);
    
    wait.until(ExpectedConditions.visibilityOfElementLocated(
    
    By.xpath("//*[@id='navbarNavDropdown']/ul/li[4]/div")));
    
    performBooking(dataset1, wait);
    
    performBooking(dataset2, wait);
    
    performBooking(dataset3, wait);
    
    scrollAndClick(By.xpath("//div[@id='reserved-banner']/a"));
    
    wait.until(ExpectedConditions.urlContains("/pages/adventures/reservations/"));
    
    waitForPageLoad(wait);
    
    history.waitForNoReservationsBannerToDisappear();
    
    int finalCount = history.getTransactionCount();
    
    Assert.assertEquals(finalCount, 3, "Expected 3 bookings but found: " + finalCount);
    
}
   
    private void performBooking(String dataset, WebDriverWait wait) throws InterruptedException {
    
    String[] parts = dataset.split(";");
    
    String city = parts[0];
    
    String adventureName = parts[1];
    
    String guest = parts[2];
    
    String date = parts[3];
    
    String count = parts[4];
    
            String cityId = city.toLowerCase().replaceAll(" ", "-");
    
            driver.manage().deleteAllCookies();
    
            driver.get("https://qtripdynamic-qa-frontend.vercel.app/");
    
            waitForPageLoad(wait);

            WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("autocomplete")));
    
            searchBox.clear();
    
            searchBox.sendKeys(city);
    
            scrollAndClick(By.id(cityId));
    
            wait.until(ExpectedConditions.urlContains("/pages/adventures/?city=" + cityId));
    
            waitForPageLoad(wait);
    
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("data")));
    
            By adventureCardLocator = By.xpath("//div[contains(@class, 'activity-card')]//h5[contains(text(),'" + adventureName + "')]");
    
            scrollAndClick(adventureCardLocator);
    
            wait.until(ExpectedConditions.urlContains("/pages/adventures/detail/"));
    
            waitForPageLoad(wait);
    
            details.makeReservation(guest, date, count);
    
            Pattern pattern = Pattern.compile("Greetings!\\s*Reservation for this adventure is successful\\.", Pattern.CASE_INSENSITIVE);
    
            wait.until((Function<WebDriver, Boolean>) d -> {
    
                try {
    
                    WebElement banner = d.findElement(By.id("reserved-banner"));
    
                    return pattern.matcher(banner.getText()).find();
    
                } catch (WebDriverException e) {
    
                    return false;
    
                }
    
            });
    
        }
    
        private void scrollAndClick(By locator) {
            WebDriverWait wait = new WebDriverWait(driver, 15);
        
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                WebElement elementToScroll = driver.findElement(locator);
        
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", elementToScroll);
                Thread.sleep(300);
        
                WebElement elementToClick = wait.until(ExpectedConditions.elementToBeClickable(locator));
        
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", elementToClick);
                } catch (ElementClickInterceptedException e) {
                    new Actions(driver).moveToElement(elementToClick).click().perform();
                }
        
                wait.until(driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
        
            } catch (Exception e) {
                throw new RuntimeException("Failed to scroll and click element", e);
            }
        }        
    
        private void waitForPageLoad(WebDriverWait wait) {
    
            wait.until(driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
    
        }
    
        @AfterMethod(alwaysRun = true)
    
        public void teardown() {
    
            DriverSingleton.quitDriver();
    
        }
    
    }
