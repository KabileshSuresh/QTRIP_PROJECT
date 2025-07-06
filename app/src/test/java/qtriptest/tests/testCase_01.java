package qtriptest.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;

import qtriptest.DP;

public class testCase_01 {
    WebDriver driver;

    @BeforeMethod
    public void setUp() {//
        //
        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver-linux64/chromedriver");
       

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        driver.get("https://crio-qtrip-qa.vercel.app/");
    }

    @Test(dataProvider = "data-provider", dataProviderClass = DP.class)
    public void TestCase01(String tcId, String username, String password) throws InterruptedException {
        System.out.println("Running test case ID: " + tcId + ", User: " + username + ", Pass: " + password);

    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
