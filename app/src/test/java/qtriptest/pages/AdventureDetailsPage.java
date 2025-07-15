package qtriptest.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AdventureDetailsPage {
    WebDriver driver;
    WebDriverWait wait; 
    @FindBy(name = "name") 
    WebElement nameInput;

    @FindBy(name = "date") 
    WebElement dateInput;

    @FindBy(name = "person") 
    WebElement personInput;

   
    @FindBy(xpath = "//button[@type='submit' and text()='Reserve']") 
    WebElement reserveButton;

    public AdventureDetailsPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
      
        this.wait = new WebDriverWait(driver, 10); 
    }

    public void makeReservation(String name, String date, String personCount) {
        try {
         
            wait.until(ExpectedConditions.elementToBeClickable(nameInput));
            nameInput.sendKeys(name);
            System.out.println("Entered guest name: " + name);

            wait.until(ExpectedConditions.elementToBeClickable(dateInput));
            dateInput.sendKeys(date);
            System.out.println("Entered date: " + date);

            wait.until(ExpectedConditions.elementToBeClickable(personInput));
            personInput.clear();
            personInput.sendKeys(personCount);
            System.out.println("Entered number of persons: " + personCount);
            
            wait.until(ExpectedConditions.elementToBeClickable(reserveButton));
            reserveButton.click();
            System.out.println("Clicked Reserve button.");

        } catch (TimeoutException e) {
            System.err.println("Timeout waiting for element on Adventure Details Page for reservation: " + e.getMessage());
            System.err.println("Current URL: " + driver.getCurrentUrl());
            throw e;
        } catch (NoSuchElementException e) {
            System.err.println("Element not found on Adventure Details Page for reservation: " + e.getMessage());
            System.err.println("Current URL: " + driver.getCurrentUrl());
            throw e;
        }
    }
}