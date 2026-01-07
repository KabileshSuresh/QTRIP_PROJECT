package qtriptest.tests;
import qtriptest.DP;
import qtriptest.DriverSingleton;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.List;

public class testCase_02 {
    WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        driver = DriverSingleton.getDriver();
        driver.manage().deleteAllCookies();

        driver.get("https://qtripdynamic-qa-frontend.vercel.app/");
        System.out.println("Browser launched and navigated to home page");
    }

    @Test(dataProvider = "data-provider", dataProviderClass = DP.class, groups = {"Search and Filter Flow"})
    public void TestCase02(String tcId, String city, String category, String duration, String expectedFilteredCount, String expectedTotalCount) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 15);

        
        System.out.println("Running invalid city search as part of TestCase2 iteration for ID: " + tcId);
        try {
            WebElement searchBoxInvalid = wait.until(ExpectedConditions.elementToBeClickable(By.id("autocomplete")));
            searchBoxInvalid.clear();
            searchBoxInvalid.sendKeys("InvalidCity123");

            Thread.sleep(1000);

            List<WebElement> citySuggestionsInvalid = driver.findElements(By.xpath("//ul[@id='results']/a"));
            Assert.assertTrue(citySuggestionsInvalid.isEmpty(),
                "Valid city suggestions were unexpectedly found for an invalid city search! Found: " + citySuggestionsInvalid.size() + " suggestions.");
            System.out.println("Invalid city search handled successfully: No valid suggestions found for TestCase2 iteration " + tcId);

        } catch (Exception e) {
            Assert.fail("An unexpected error occurred during invalid city search in TestCase2 for ID " + tcId + ": " + e.getMessage());
        }
System.out.println("\n--- Starting Valid Search and Filter Flow for TestCase2 iteration " + tcId + " ---");

WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("autocomplete")));
searchBox.clear();
searchBox.sendKeys(city);

Thread.sleep(1500); 

wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[@id='results']/a")));

List<WebElement> suggestions = driver.findElements(By.xpath("//ul[@id='results']/a"));

boolean cityFound = false;
for (WebElement el : suggestions) {
    if (el.getText().trim().equalsIgnoreCase(city)) {
        el.click();
        cityFound = true;
        break;
    }
}
Assert.assertTrue(cityFound, "Valid city not found in suggestions for city: " + city + " (TC ID: " + tcId + ")");

wait.until(ExpectedConditions.urlContains("/pages/adventures/?city="));

List<WebElement> originalResults = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("activity-card")));
Assert.assertEquals(originalResults.size(), Integer.parseInt(expectedTotalCount), "Total results mismatch after city search for " + city + " (TC ID: " + tcId + ")");

WebElement durationSelectElement = wait.until(ExpectedConditions.elementToBeClickable(By.id("duration-select")));
Select durationFilter = new Select(durationSelectElement);
durationFilter.selectByVisibleText(duration);


WebElement addCategoryButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(normalize-space(), 'Add Category')]")));
addCategoryButton.click();

WebElement categorySelectElement = wait.until(ExpectedConditions.elementToBeClickable(By.id("category-select")));
Select categoryFilterSelect = new Select(categorySelectElement);

categoryFilterSelect.selectByVisibleText(category);

wait.until(ExpectedConditions.numberOfElementsToBe(By.className("activity-card"), Integer.parseInt(expectedFilteredCount)));
List<WebElement> finalFilteredResults = driver.findElements(By.className("activity-card"));
Assert.assertEquals(finalFilteredResults.size(), Integer.parseInt(expectedFilteredCount), "Final filtered results mismatch for " + city + " (TC ID: " + tcId + ")");

JavascriptExecutor js = (JavascriptExecutor) driver; // Initialize JavascriptExecutor once

WebElement clearDurationButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[normalize-space()='Clear' and @onclick='clearDuration(event)']")));
js.executeScript("arguments[0].click();", clearDurationButton);

Thread.sleep(500);

WebElement clearCategoryButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[normalize-space()='Clear' and @onclick='clearCategory(event)']")));
js.executeScript("arguments[0].click();", clearCategoryButton);


wait.until(ExpectedConditions.numberOfElementsToBe(By.className("activity-card"), Integer.parseInt(expectedTotalCount)));
List<WebElement> resetResults = driver.findElements(By.className("activity-card"));
Assert.assertEquals(resetResults.size(), Integer.parseInt(expectedTotalCount), "Clear filter did not reset results for " + city + " (TC ID: " + tcId + ")");

System.out.println("TestCase2 with ID: " + tcId + " passed successfully");
}

@AfterClass(alwaysRun = true)
public void tearDown() {
if (driver != null) {
    driver.quit();
    System.out.println("Browser closed");
}
}
}