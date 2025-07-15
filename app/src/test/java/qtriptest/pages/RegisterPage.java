package qtriptest.pages;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.NoAlertPresentException;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class RegisterPage {
    WebDriver driver;

    @FindBy(id = "floatingInput") 
    WebElement emailInput;

    @FindBy(xpath = "(//input[@id='floatingPassword'])[1]") 
    WebElement passwordInput;

    @FindBy(xpath = "(//input[@id='floatingPassword'])[2]") 
    WebElement confirmPasswordInput;

    @FindBy(xpath = "//button[text()='Register Now']") 
    WebElement registerButton;

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean registerUser(String email, String password) {
        WebDriverWait wait = new WebDriverWait(driver, 20);

        wait.until(ExpectedConditions.visibilityOf(emailInput));
        emailInput.clear();
        emailInput.sendKeys(email);

        wait.until(ExpectedConditions.visibilityOf(passwordInput));
        passwordInput.clear();
        passwordInput.sendKeys(password);

        wait.until(ExpectedConditions.visibilityOf(confirmPasswordInput));
        confirmPasswordInput.clear();
        confirmPasswordInput.sendKeys(password);

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", registerButton);

        wait.until(ExpectedConditions.elementToBeClickable(registerButton));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", registerButton);
        
        try {
            WebDriverWait alertWait = new WebDriverWait(driver, 3);
            Alert alert = alertWait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            System.out.println("Browser alert detected after registration button click: " + alertText);

            if (alertText.contains("Failed - Email already exists")) {
                alert.accept();
                System.out.println("Dismissed 'Failed - Email already exists' alert during registration.");
                return false;
            } else if (alertText.contains("Registration Successful")) { 
                alert.accept();
                System.out.println("Dismissed 'Registration Successful' alert.");
                return true; 
            }
            else {
                alert.accept(); 
                System.out.println("Dismissed unexpected browser alert during registration: " + alertText);
                return false;
            }
        } catch (NoAlertPresentException | TimeoutException e) {
            System.out.println("No browser alert present after registration button click (within 3s timeout).");
            
        }

        try {
            wait.until(ExpectedConditions.urlContains("/login"));
            System.out.println("Redirected to login page after successful registration.");
            return true; 
        } catch (TimeoutException e) {
            System.out.println("Timeout: Did not redirect to login page after registration attempt within 20 seconds. " + e.getMessage());
        
            return false;
        }
    }
}
