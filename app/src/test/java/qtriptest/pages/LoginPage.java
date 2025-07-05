package qtriptest.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {
    WebDriver driver;

    @FindBy(id = "floatingInput") WebElement emailInput;
    @FindBy(id = "floatingPassword") WebElement passwordInput;
    @FindBy(xpath = "//button[text()='Login to QTrip']") WebElement loginButton;
    @FindBy(id = "logout") WebElement logoutButton;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void loginUser(String email, String password) {
        emailInput.sendKeys(email);
        passwordInput.sendKeys(password);
        loginButton.click();
    }

    public boolean isUserLoggedIn() {
        return logoutButton.isDisplayed();
    }

    public void logout() {
        logoutButton.click();
    }

    public boolean isLoggedOut() {
        return loginButton.isDisplayed();
    }
}
