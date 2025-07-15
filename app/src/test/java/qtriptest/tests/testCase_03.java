package qtriptest.tests;

import org.testng.annotations.*;
import org.openqa.selenium.*;
import qtriptest.DriverSingleton;
import qtriptest.DP;
import qtriptest.pages.*;
import org.testng.Assert;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;
import java.util.regex.Pattern;
import com.google.common.base.Function;

public class testCase_03 {
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

    @Test(dataProvider = "data-provider", dataProviderClass = DP.class, priority = 3, groups = {"Booking and Cancellation Flow"})
    public void TestCase03(String tcId, String username, String password, String searchCity,
                           String adventureName, String guestName, String date, String count) throws InterruptedException {

        WebDriverWait wait = new WebDriverWait(driver, 30);
        driver.manage().deleteAllCookies();

        // Register
        driver.get("https://qtripdynamic-qa-frontend.vercel.app/pages/register/");
        waitForPageLoad(wait);

        String uniqueEmail = username.split("@")[0] + System.currentTimeMillis() + "@" + username.split("@")[1];
        register.registerUser(uniqueEmail, password);
        Assert.assertTrue(driver.getCurrentUrl().contains("/pages/login"));
        driver.manage().deleteAllCookies();

        // Login
        driver.get("https://qtripdynamic-qa-frontend.vercel.app/pages/login/");
        waitForPageLoad(wait);
        login.loginUser(uniqueEmail, password);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='navbarNavDropdown']/ul/li[4]/div")));
        driver.manage().deleteAllCookies();

        // Search City
        driver.get("https://qtripdynamic-qa-frontend.vercel.app/");
        waitForPageLoad(wait);

        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("autocomplete")));
        searchBox.clear();
        searchBox.sendKeys(searchCity);

        String cityId = searchCity.toLowerCase();
        wait.until((Function<WebDriver, Boolean>) d -> {
            try {
                WebElement cityTile = d.findElement(By.id(cityId));
                scrollAndClick(cityTile);
                return true;
            } catch (WebDriverException e) {
                return false;
            }
        });

        wait.until(ExpectedConditions.urlContains("/pages/adventures/?city=" + cityId));
        waitForPageLoad(wait);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("data")));

        // Click Adventure Card
        wait.until((Function<WebDriver, Boolean>) d -> {
            try {
                WebElement card = d.findElement(By.xpath("//div[contains(@class, 'activity-card')]//h5[contains(normalize-space(), '" + adventureName + "')]"));
                scrollAndClick(card);
                return true;
            } catch (WebDriverException e) {
                return false;
            }
        });

        // Make reservation
        wait.until(ExpectedConditions.urlContains("/pages/adventures/detail/"));
        waitForPageLoad(wait);
        details.makeReservation(guestName, date, count);

        // Verify reservation banner
        Pattern pattern = Pattern.compile("Greetings!\\s*Reservation for this adventure is successful\\.", Pattern.CASE_INSENSITIVE);
        wait.until((Function<WebDriver, Boolean>) d -> {
            try {
                WebElement banner = d.findElement(By.id("reserved-banner"));
                return pattern.matcher(banner.getText()).find();
            } catch (WebDriverException e) {
                return false;
            }
        });

        WebElement reservedBanner = driver.findElement(By.id("reserved-banner"));
        Assert.assertTrue(pattern.matcher(reservedBanner.getText()).find(), "Reservation was not successful!");

        // Go to history
        WebElement reservationLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='reserved-banner']/a")));
        scrollAndClick(reservationLink);

        wait.until(ExpectedConditions.urlContains("/pages/adventures/reservations/"));
        waitForPageLoad(wait);
        history.waitForNoReservationsBannerToDisappear();

        int initialBookingCount = history.getTransactionCount();
        Assert.assertEquals(initialBookingCount, 1);

        int personsInBooking = history.getPersonsCountForFirstBooking();
        Assert.assertEquals(personsInBooking, Integer.parseInt(count));

        // Cancel booking
        history.cancelFirstBooking();
        driver.navigate().refresh();
        waitForPageLoad(wait);

        int finalBookingCount = history.getTransactionCount();
        Assert.assertEquals(finalBookingCount, 0);
    }

    @AfterMethod(alwaysRun = true)
    public void teardown() {
        if (driver != null) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, 10);
                WebElement logoutButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='nav-link login register' and text()='Logout']")));
                scrollAndClick(logoutButton);
                wait.until(ExpectedConditions.invisibilityOf(logoutButton));
            } catch (WebDriverException ignored) {
            } finally {
                DriverSingleton.quitDriver();
            }
        }
    }

    private void waitForPageLoad(WebDriverWait wait) {
        wait.until(driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
    }

    private void scrollAndClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", element);
    }
}
