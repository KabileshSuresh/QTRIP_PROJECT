package qtriptest.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;
import qtriptest.pages.*;

public class TestCases {
    WebDriver driver;
    HomePage homePage;
    RegisterPage registerPage;
    LoginPage loginPage;

    @BeforeMethod(enabled = false)
    public void setUp() {
        driver = new ChromeDriver();
        driver.get("https://qtripdynamic-qa-frontend.vercel.app/");
        homePage = new HomePage(driver);
        registerPage = new RegisterPage(driver);
        loginPage = new LoginPage(driver);
    }

    @Test(enabled = false)
    public void testRegisterPageLoaded() {
        homePage.navigateToRegister();
        assert driver.getCurrentUrl().contains("/register");
    }

    @Test(enabled = false)
    public void testLoginMethod() {
        homePage.navigateToRegister();
        registerPage.registerUser("testuser1@example.com", "Test@123");
        loginPage.loginUser("testuser1@example.com", "Test@123");
        assert loginPage.isUserLoggedIn();
    }

    @AfterMethod(enabled = false)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
