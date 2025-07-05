package qtriptest.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class RegisterPage {
    WebDriver driver;

    @FindBy(id = "floatingInput") WebElement emailInput;
    @FindBy(id = "floatingPassword") WebElement passwordInput;
    @FindBy(id = "confirmPassword") WebElement confirmPasswordInput;
    @FindBy(xpath = "//button[text()='Register Now']") WebElement registerButton;

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void registerUser(String email, String password) {
        emailInput.sendKeys(email);
        passwordInput.sendKeys(password);
        confirmPasswordInput.sendKeys(password);
        registerButton.click();
    }
}
