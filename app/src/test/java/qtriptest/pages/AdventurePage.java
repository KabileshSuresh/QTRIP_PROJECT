package qtriptest.pages;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.*;
import org.openqa.selenium.support.*;
import java.util.List;
import org.openqa.selenium.support.ui.UnexpectedTagNameException;

public class AdventurePage {
    WebDriver driver;
    WebDriverWait wait; 

    @FindBy(css = "input#duration-select") WebElement filterDuration;
    @FindBy(css = "div.category-filter label") List<WebElement> categoryFilters;
    @FindBy(css = "div.activity-card") List<WebElement> filteredActivities;
    @FindBy(xpath = "//div[@onclick='clearDuration(event)']") WebElement clearDurationFilterButton; 
    @FindBy(xpath = "//div[@onclick='clearCategory(event)']") WebElement clearCategoryFilterButton; 
    
    @FindBy(xpath = "//*[@id='data']/div[4]/h5") WebElement noAdventuresText; 

    public AdventurePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.wait = new WebDriverWait(driver, 15); 
    }

    public void filterByDuration(String value) {
        try {
            
            wait.until(ExpectedConditions.elementToBeClickable(filterDuration));
            filterDuration.clear();
            filterDuration.sendKeys(value);
            filterDuration.sendKeys(Keys.ENTER);
            System.out.println("Applied duration filter: " + value);
           
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfAllElements(filteredActivities),
                ExpectedConditions.visibilityOf(noAdventuresText) 
            ));
            System.out.println("Waited for activity cards to refresh after duration filter.");
        } catch (UnexpectedTagNameException e) {
            System.out.println("Error: filterDuration is not a <select> element. Using sendKeys. " + e.getMessage());
           
            filterDuration.clear();
            filterDuration.sendKeys(value);
            filterDuration.sendKeys(Keys.ENTER);
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfAllElements(filteredActivities),
                ExpectedConditions.visibilityOf(noAdventuresText)
            ));
        } catch (TimeoutException e) {
            System.out.println("Timeout applying duration filter or waiting for results: " + e.getMessage());
        }
    }

    public void selectCategory(String category) {
        WebElement targetCategory = null;
        for (WebElement cat : categoryFilters) {
            if (cat.getText().equalsIgnoreCase(category)) {
                targetCategory = cat;
                break;
            }
        }
        if (targetCategory != null) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(targetCategory)).click();
                System.out.println("Selected category filter: " + category);
               
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfAllElements(filteredActivities),
                    ExpectedConditions.visibilityOf(noAdventuresText)
                ));
                System.out.println("Waited for activity cards to refresh after category filter.");
            } catch (TimeoutException e) {
                System.out.println("Timeout selecting category filter or waiting for results: " + e.getMessage());
            }
        } else {
            System.out.println("Category filter '" + category + "' not found.");
        }
    }

    public int getFilteredActivityCount() {
        
        try {
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfAllElements(filteredActivities),
                ExpectedConditions.visibilityOf(noAdventuresText) 
            ));
            if (noAdventuresText.isDisplayed()) {
                if (noAdventuresText.getText().contains("No Adventure found")) {
                    return 0; 
                }
            }
        } catch (TimeoutException e) {
            System.out.println("Timeout waiting for activities or 'no adventures' text. Returning 0. " + e.getMessage());
            return 0; 
        } catch (NoSuchElementException e) {
            
            System.out.println("Neither filtered activities nor 'no adventures' text found. Assuming 0 activities. " + e.getMessage());
            return 0;
        }
        return filteredActivities.size();
    }


    public void clearFilters() {
        try {
            if (clearDurationFilterButton.isDisplayed()) {
                wait.until(ExpectedConditions.elementToBeClickable(clearDurationFilterButton)).click();
                System.out.println("Cleared duration filter using button.");
            }
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.println("No specific duration clear button found or clickable, proceeding.");
        }

        try {
            if (clearCategoryFilterButton.isDisplayed()) {
                wait.until(ExpectedConditions.elementToBeClickable(clearCategoryFilterButton)).click();
                System.out.println("Cleared category filter using button.");
            }
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.println("No specific category clear button found or clickable, proceeding.");
        }
       
        if (!driver.getCurrentUrl().contains("?")) { 
             System.out.println("Filters likely cleared or no filters applied. No refresh needed.");
        } else {
            System.out.println("Refreshing page to clear filters as no specific clear buttons were used.");
            driver.navigate().refresh();
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            System.out.println("Page reloaded after clearing filters.");
        }
        wait.until(ExpectedConditions.visibilityOfAllElements(filteredActivities));
        System.out.println("Waited for activities to load after clearing filters.");
    }
}
