package qtriptest.pages;
import org.openqa.selenium.NoAlertPresentException;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {
    WebDriver driver;

    @FindBy(id = "floatingInput") 
    WebElement emailInput;

    @FindBy(id = "floatingPassword") 
    WebElement passwordInput;

    @FindBy(css = "button.btn-login") 
    WebElement loginButton;

    @FindBy(xpath = "//*[@id='navbarNavDropdown']/ul/li[4]/div") 
    WebElement logoutButton;

    @FindBy(css = "button.navbar-toggler") 
    WebElement navbarTogglerButton;

    @FindBy(id = "navbarNavDropdown")
    WebElement navbarDropdownDiv;

    @FindBy(xpath = "//*[@id='navbarNavDropdown']/ul/li[3]/a")
    WebElement loginHereButton; // This is the button to verify after logout

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    
    private void ensureNavbarExpanded() {
        WebDriverWait wait = new WebDriverWait(driver, 5);

        try {
            if (navbarTogglerButton.isDisplayed()) {
                System.out.println("Navbar toggler button is displayed.");
                if (navbarTogglerButton.getAttribute("aria-expanded").equals("false")) {
                    System.out.println("Navbar is collapsed. Clicking toggle button to expand.");
                    wait.until(ExpectedConditions.elementToBeClickable(navbarTogglerButton)).click();
                    wait.until(ExpectedConditions.attributeToBe(navbarTogglerButton, "aria-expanded", "true"));
                    System.out.println("Navbar expanded successfully via toggler click.");
                } else {
                    System.out.println("Navbar is already expanded (toggler displayed, aria-expanded='true').");
                }
            } else {
                System.out.println("Navbar toggler button is NOT displayed (likely desktop view, menu is always open).");
                if (navbarDropdownDiv.getAttribute("class").contains("collapse") && !navbarDropdownDiv.getAttribute("class").contains("show")) {
                    System.out.println("WARNING: Navbar toggler not displayed, but menu still appears collapsed. This might be an issue.");
                } else {
                    System.out.println("Navbar dropdown div indicates it's expanded or not collapsible in this view.");
                }
            }
        } catch (TimeoutException e) {
            System.out.println("Timeout waiting for navbar toggler or its attributes: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error in ensureNavbarExpanded: " + e.getMessage());
        }
    }


    public void loginUser(String email, String password) {
        WebDriverWait wait = new WebDriverWait(driver, 20); 

        wait.until(ExpectedConditions.visibilityOf(emailInput));
        emailInput.clear();
        emailInput.sendKeys(email);

        wait.until(ExpectedConditions.visibilityOf(passwordInput));
        passwordInput.clear();
        passwordInput.sendKeys(password);

        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", loginButton);
        
        try {
            loginButton.click(); 
        } catch (ElementClickInterceptedException e) {
            System.out.println("ElementClickInterceptedException caught for login button, trying JS click.");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginButton);
        }

        try {
            WebDriverWait alertWait = new WebDriverWait(driver, 3);
            Alert alert = alertWait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            System.out.println("Browser alert detected after login button click: " + alertText);

            if (alertText.contains("Failed - Password is incorrect")) {
                alert.accept();
                System.out.println("Dismissed 'Failed - Password is incorrect' alert.");
              
                return;
            } else {
                alert.accept(); 
                System.out.println("Dismissed unexpected browser alert: " + alertText);
                return; 
            }
        } catch (NoAlertPresentException | TimeoutException e) {
           
            System.out.println("No browser alert present after login button click (within 3s timeout).");
        }

       
        try {
            wait.until(ExpectedConditions.urlToBe("https://qtripdynamic-qa-frontend.vercel.app/")); 
            System.out.println("Login successful: Redirected to home page URL.");
        } catch (TimeoutException e) {
            System.out.println("Login failed: Did not redirect to home page URL after login attempt within 20 seconds. This might be an in-page error. " + e.getMessage());
            return;
        }

        try {
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            System.out.println("Page fully loaded (document.readyState == 'complete').");
        } catch (TimeoutException e) {
            System.out.println("Timeout: Page did not reach 'complete' readyState within 20 seconds. " + e.getMessage());
           
        }

        ensureNavbarExpanded();

        try {
            wait.until(ExpectedConditions.visibilityOf(logoutButton));
            System.out.println("Login successful: Logout button is now visible.");
        } catch (TimeoutException e) {
            System.out.println("Login failed: Logout button did not become visible on the home page within 20 seconds. " + e.getMessage());
            }
    }
    
public boolean isUserLoggedIn() {
   
    if (driver.getCurrentUrl().contains("/pages/login/")) {
        return false;
    }

    if (driver.getCurrentUrl().equals("https://qtripdynamic-qa-frontend.vercel.app/")) {
        ensureNavbarExpanded();
    }
    
    try {
        WebDriverWait shortWait = new WebDriverWait(driver, 5);
      
        return shortWait.until(ExpectedConditions.visibilityOf(logoutButton)).isDisplayed();
    } catch (Exception e) {
        System.out.println("isUserLoggedIn check failed: Logout button not found or not displayed. " + e.getMessage());
        return false;
    }
}
    
    public void logout() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        ensureNavbarExpanded(); 
        try {
            wait.until(ExpectedConditions.elementToBeClickable(logoutButton)).click();
        } catch (ElementClickInterceptedException e) {
            System.out.println("ElementClickInterceptedException caught for logout button during logout, trying JS click.");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", logoutButton);
        } catch (TimeoutException e) {
            System.out.println("Timeout waiting for logout button to be clickable during logout. User might already be logged out or element is missing: " + e.getMessage());
        }
    }

    
    public boolean isLoggedOut() {
        WebDriverWait wait = new WebDriverWait(driver, 10); 
        ensureNavbarExpanded();
        try {
            wait.until(ExpectedConditions.visibilityOf(loginHereButton));
            return loginHereButton.isDisplayed();
        } catch (TimeoutException e) {
            System.out.println("isLoggedOut check failed: 'Login Here' button in navbar not found. " + e.getMessage());
            return false;
        }
    }
}