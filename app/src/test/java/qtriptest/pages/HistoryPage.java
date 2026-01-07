package qtriptest.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.*;
import org.openqa.selenium.support.ui.WebDriverWait; 
import org.openqa.selenium.support.ui.ExpectedConditions; 
import java.util.List;

public class HistoryPage {
    WebDriver driver;
    WebDriverWait wait; 

    @FindBy(xpath = "//tbody[@id='reservation-table']/tr") 
    List<WebElement> transactions;

    @FindBy(id = "no-reservation-banner") //
    WebElement noReservationsBanner;

    @FindBy(id = "reservation-table-parent") //
    WebElement reservationTableParent;


    public HistoryPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.wait = new WebDriverWait(driver, 15); // 15 seconds wait
    }

    public void waitForNoReservationsBannerToDisappear() {
        System.out.println("DEBUG: Waiting for 'no reservations' banner to disappear...");
        try {
            
            wait.until(ExpectedConditions.invisibilityOf(noReservationsBanner));
            System.out.println("DEBUG: 'No reservations' banner has disappeared.");
        } catch (TimeoutException e) {
            System.err.println("DEBUG ERROR: Timeout waiting for 'no reservations' banner to disappear. It might still be visible or page not updated: " + e.getMessage());
           
        } catch (NoSuchElementException e) {
            System.out.println("DEBUG: 'No reservations' banner element not found, assuming it's already gone (initial state).");
        }
    }


    public int getTransactionCount() {
        System.out.println("DEBUG: Entering HistoryPage.getTransactionCount()...");
        try {
          
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOf(reservationTableParent),
                ExpectedConditions.visibilityOf(noReservationsBanner) 
            ));
            System.out.println("DEBUG: History page container is visible.");

            if (noReservationsBanner.isDisplayed()) { //
                System.out.println("DEBUG: 'No reservations' banner is displayed. Returning 0.");
                return 0;
            } else {
                
                wait.until(ExpectedConditions.and(
                    ExpectedConditions.visibilityOf(reservationTableParent), //
                    ExpectedConditions.invisibilityOf(noReservationsBanner) //
                ));

                List<WebElement> currentTransactions = driver.findElements(By.xpath("//tbody[@id='reservation-table']/tr")); //
                int count = currentTransactions.size();
                System.out.println("DEBUG: Found " + count + " transaction rows.");
                return count;
            }

        } catch (TimeoutException e) {
            System.err.println("DEBUG ERROR: Timeout waiting for expected history page content. Assuming 0 transactions: " + e.getMessage());
            return 0;
        } catch (NoSuchElementException e) {
             System.err.println("DEBUG ERROR: Expected elements (table parent/banner) not found at all. Assuming 0 reservations: " + e.getMessage());
             return 0;
        } catch (WebDriverException e) {
            System.err.println("DEBUG ERROR: WebDriver exception in getTransactionCount: " + e.getMessage());
            return 0;
        }
    }

    public int getPersonsCountForFirstBooking() {
        System.out.println("DEBUG: Entering HistoryPage.getPersonsCountForFirstBooking()...");
        
        WebElement personsTd = null; 

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tbody[@id='reservation-table']/tr"))); //
            
            List<WebElement> currentTransactions = driver.findElements(By.xpath("//tbody[@id='reservation-table']/tr")); //
            WebElement firstTransactionRow = currentTransactions.get(0); 

            personsTd = firstTransactionRow.findElement(By.xpath("./td[3]")); 
            
            String personsText = personsTd.getText().trim();
            int personsCount = Integer.parseInt(personsText);
            System.out.println("DEBUG: Persons count from first booking row: " + personsCount);
            return personsCount;

        } catch (IndexOutOfBoundsException e) {
            System.err.println("DEBUG ERROR: No booking rows found or unable to access first row to get persons count: " + e.getMessage());
            return 0; 
        } catch (NumberFormatException e) {
            String foundText = (personsTd != null) ? personsTd.getText() : "N/A (element not found or parsed)";
            System.err.println("DEBUG ERROR: Could not parse persons count to integer. Text found: \"" + foundText + "\". Error: " + e.getMessage());
            return -1; // Return -1 to indicate a parsing failure
        } catch (TimeoutException | NoSuchElementException e) {
            String foundText = (personsTd != null) ? personsTd.getText() : "N/A (element not found or parsed)";
            System.err.println("DEBUG ERROR: Element for persons count not found or not visible: \"" + foundText + "\". Error: " + e.getMessage());
            return 0; 
        }
    }

    public void cancelFirstBooking() {
        System.out.println("DEBUG: Entering HistoryPage.cancelFirstBooking()...");
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(transactions));
            
            WebElement firstCancelButton = transactions.get(0).findElement(By.cssSelector("td button.cancel-button")); 
            
            wait.until(ExpectedConditions.elementToBeClickable(firstCancelButton));
            
            firstCancelButton.click();
            System.out.println("DEBUG: Clicked cancel button for the first booking.");

            wait.until(ExpectedConditions.invisibilityOf(transactions.get(0))); 
            System.out.println("DEBUG: Waited for the canceled transaction row to disappear.");

        } catch (IndexOutOfBoundsException e) {
            System.err.println("DEBUG ERROR: No transactions found to cancel: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("DEBUG ERROR: Timeout waiting for cancel button or its disappearance: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.err.println("DEBUG ERROR: Cancel button or transaction row not found: " + e.getMessage());
        } catch (WebDriverException e) {
            System.err.println("DEBUG ERROR: WebDriver exception during cancellation: " + e.getMessage());
        }
    }
}