package org.owasp.webgoat.plugins;

import com.github.webdriverextensions.Bot;
import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import com.github.webdriverextensions.junitrunner.annotations.Chrome;
import com.google.common.base.Predicate;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Created by Doug Morato <dm@corp.io> on 8/21/15.
 */
@RunWith(WebDriverRunner.class)
//@Firefox
@Chrome
//@InternetExplorer
public class WebGoat2IT {

    // Since most Tomcat deployments run on port 8080, let's set the automated integration tests to
    // spawn tomcat on port 8888 so that we don't interfere with local Tomcat's
    private String baseWebGoatUrl = "http://localhost:8888/WebGoat";
    private String loginUser = "webgoat";
    private String loginPassword = "webgoat";

    public void doLoginWebgoatUser() {
        Bot.driver().get(baseWebGoatUrl + "/login.mvc");
        Bot.driver().navigate().refresh();


        WebDriverWait wait = new WebDriverWait(Bot.driver(), 15); // wait for a maximum of 15 seconds
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("exampleInputEmail1")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("exampleInputPassword1")));

        WebElement usernameElement = Bot.driver().findElement(By.name("username"));
        WebElement passwordElement = Bot.driver().findElement(By.name("password"));
        usernameElement.sendKeys(loginUser);
        passwordElement.sendKeys(loginPassword);
        passwordElement.submit();
        Bot.driver().get(baseWebGoatUrl + "/start.mvc");
    }

    /**
     * Runs a simple test verifying the UI and title of the WebGoat home page.
     *
     * @throws Exception
     */
    @Test
    public void verifyWebGoatLoginPage() {
        Bot.driver().get(baseWebGoatUrl + "/login.mvc");
        WebDriverWait wait = new WebDriverWait(Bot.driver(), 15); // wait for a maximum of 15 seconds
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("exampleInputEmail1")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("exampleInputPassword1")));

        assertTrue(Bot.driver().getTitle().equals("Login Page"));

        WebElement usernameElement = Bot.driver().findElement(By.name("username"));
        WebElement passwordElement = Bot.driver().findElement(By.name("password"));
        assertNotNull(usernameElement);
        assertNotNull(passwordElement);
    }


    @Test
    public void testStartMvc() {
        Bot.driver().get(baseWebGoatUrl + "/start.mvc");

        WebDriverWait wait = new WebDriverWait(Bot.driver(), 15); // wait for a maximum of 15 seconds
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("password")));
    }

    @Test
    public void testWebGoatUserLogin() {

        doLoginWebgoatUser();

        Bot.driver().get(baseWebGoatUrl + "/start.mvc");
        String pageSource = Bot.driver().getPageSource();

        assertTrue("user: webgoat is not in the page source", pageSource.contains("Role: webgoat_admin"));
        WebElement cookieParameters = Bot.driver().findElement(By.id("cookies-and-params"));
        assertNotNull("element id=cookieParameters should be displayed to user upon successful login", cookieParameters);
    }

    @Test
    public void testServiceLessonMenuMVC() {

        doLoginWebgoatUser();

        Bot.driver().get(baseWebGoatUrl + "/service/lessonmenu.mvc");

        String pageSource = Bot.driver().getPageSource();


        assertTrue("Page source should contain lessons: Test 1", pageSource.contains("Reflected XSS"));
        assertTrue("Page source should contain lessons: Test 2", pageSource.contains("Access Control Flaws"));
        assertTrue("Page source should contain lessons: Test 34", pageSource.contains("Fail Open Authentication Scheme"));
    }

    @Test
    public void testAccessControlFlaws() {
        doLoginWebgoatUser();

        Bot.driver().get(baseWebGoatUrl + "/start.mvc#attack/1708534694/200");
        Bot.driver().get(baseWebGoatUrl + "/service/restartlesson.mvc");
        Bot.driver().get(baseWebGoatUrl + "/start.mvc#attack/1708534694/200");

        FluentWait<WebDriver> wait = new WebDriverWait(Bot.driver(), 15); // wait for a maximum of 15 seconds
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("lesson-title"), "Using an Access Control Matrix"));

        wait = new FluentWait(Bot.driver())
                .withTimeout(10, SECONDS)
                .pollingEvery(2, SECONDS)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        WebElement user = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("User")));
        user.click();
        user.sendKeys("Larry");

        WebElement resource = Bot.driver().findElement(By.name("Resource"));
        resource.click();
        resource.sendKeys("A");

        WebElement submit = Bot.driver().findElement(By.name("SUBMIT"));
        submit.click();

        wait = new FluentWait(Bot.driver())
                .withTimeout(10, SECONDS)
                .pollingEvery(2, SECONDS)
                .ignoring(NoSuchElementException.class);

        wait.until(new Predicate<WebDriver>() {
            public boolean apply(WebDriver driver) {
                return Bot.driver().getPageSource().contains("Congratulations");
            }
        });
    }

    @Test
    public void testFailOpenAuthenticationScheme() throws IOException {
        doLoginWebgoatUser();

        Bot.driver().get(baseWebGoatUrl + "/start.mvc#attack/1075773632/200");
        Bot.driver().get(baseWebGoatUrl + "/service/restartlesson.mvc");
        Bot.driver().get(baseWebGoatUrl + "/start.mvc#attack/1075773632/200");

        FluentWait<WebDriver> wait = new WebDriverWait(Bot.driver(), 15); // wait for a maximum of 15 seconds
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("lesson-title"), "Fail Open Authentication Scheme"));

        wait = new FluentWait(Bot.driver())
                .withTimeout(10, SECONDS)
                .pollingEvery(2, SECONDS)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        WebElement user = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("Username")));
        user.click();
        user.sendKeys("Larry");

        JavascriptExecutor javascript = (JavascriptExecutor) Bot.driver();
        String todisable = "document.getElementsByName('Password')[0].setAttribute('disabled', '');";
        javascript.executeScript(todisable);
        assertFalse(Bot.driver().findElement(By.name("Password")).isEnabled());

        WebElement submit = Bot.driver().findElement(By.name("SUBMIT"));
        submit.click();
        wait = new FluentWait(Bot.driver())
                .withTimeout(10, SECONDS)
                .pollingEvery(2, SECONDS)
                .ignoring(NoSuchElementException.class);

        wait.until(new Predicate<WebDriver>() {
            public boolean apply(WebDriver driver) {
                return Bot.driver().getPageSource().contains("Congratulations");
            }
        });
    }

    @Test
    public void testSqlInjectionLabLessonPlanShouldBePresent() throws IOException {
        doLoginWebgoatUser();

        Bot.driver().get(baseWebGoatUrl + "/start.mvc#attack/1537271095/200");
        Bot.driver().get(baseWebGoatUrl + "/service/restartlesson.mvc");
        Bot.driver().get(baseWebGoatUrl + "/start.mvc#attack/1537271095/200");

        FluentWait<WebDriver> wait = new FluentWait(Bot.driver())
                .withTimeout(10, SECONDS)
                .pollingEvery(2, SECONDS)
                .ignoring(NoSuchElementException.class);
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("lesson-title"), "LAB: SQL Injection"));

        assertFalse(Bot.driver().getPageSource().contains("Lesson Plan Title: How to Perform a SQL Injection"));
        WebElement user = Bot.driver().findElement(By.id("show-plan-button"));
        user.click();

        wait = new WebDriverWait(Bot.driver(), 15); // wait for a maximum of 15 seconds
        wait.until(ExpectedConditions
                .textToBePresentInElementLocated(By.id("lesson-plan-content"), "Lesson Plan Title: How to Perform a SQL Injection"));
    }

    @Test
    public void testClientSideValidation() throws IOException {
        doLoginWebgoatUser();

        Bot.driver().get(baseWebGoatUrl + "/start.mvc#attack/1129417221/200");
        Bot.driver().get(baseWebGoatUrl + "/service/restartlesson.mvc");
        Bot.driver().get(baseWebGoatUrl + "/start.mvc#attack/1129417221/200");

        FluentWait<WebDriver> wait = new WebDriverWait(Bot.driver(), 15); // wait for a maximum of 15 seconds
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("lesson-title"), "Insecure Client Storage"));

        //Stage 1
        WebElement user = Bot.driver().findElement(By.name("field1"));
        user.click();
        user.sendKeys("PLATINUM");
        Bot.driver().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);


        WebElement submit = Bot.driver().findElement(By.name("SUBMIT"));
        submit.click();
        wait = new FluentWait(Bot.driver())
                .withTimeout(20, SECONDS)
                .pollingEvery(2, SECONDS)
                .ignoring(NoSuchElementException.class);
        wait.until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver input) {
                return Bot.driver().getPageSource().contains("Stage 2");
            }
        });


        //Stage 2
        wait = new FluentWait(Bot.driver())
                .withTimeout(10, SECONDS)
                .pollingEvery(2, SECONDS)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        WebElement qty = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("QTY1")));
        qty.click();
        qty.sendKeys("8");
        qty = Bot.driver().findElement(By.name("QTY1"));
        qty.click();
        qty.sendKeys("8");
        Bot.driver().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);


        JavascriptExecutor javascript = (JavascriptExecutor) Bot.driver();
        String cmd = "document.getElementsByName('GRANDTOT')[0].value = '$0.00';";
        javascript.executeScript(cmd);

        Bot.driver().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);


        submit = Bot.driver().findElement(By.name("SUBMIT"));
        submit.click();
        wait = new FluentWait(Bot.driver())
                .withTimeout(10, SECONDS)
                .pollingEvery(2, SECONDS)
                .ignoring(NoSuchElementException.class);
        wait.until(new Predicate<WebDriver>() {
            public boolean apply(WebDriver driver) {
                return Bot.driver().getPageSource().contains("Congratulations");
            }
        });
    }

    @Test
    public void testJavaScriptValidation() throws IOException {
        doLoginWebgoatUser();

        Bot.driver().get(baseWebGoatUrl + "/start.mvc#attack/1574219258/1700");
        Bot.driver().get(baseWebGoatUrl + "/service/restartlesson.mvc");
        Bot.driver().get(baseWebGoatUrl + "/start.mvc#attack/1574219258/1700");

        FluentWait<WebDriver> wait = new WebDriverWait(Bot.driver(), 15); // wait for a maximum of 15 seconds
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("lesson-title"), "Bypass Client Side JavaScript Validation"));

        Bot.driver().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        for (int i = 1; i <= 7; i++) {
            WebElement field = Bot.driver().findElement(By.name("field" + i));
            field.click();
            field.sendKeys("@#@{@#{");
        }

        JavascriptExecutor javascript = (JavascriptExecutor) Bot.driver();
        String cmd = "document.getElementById('submit_btn').onclick=''";
        javascript.executeScript(cmd);

        WebElement submit = Bot.driver().findElement(By.id("submit_btn"));
        submit.click();

        Bot.driver().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);


        wait = new FluentWait(Bot.driver())
                .withTimeout(10, SECONDS)
                .pollingEvery(2, SECONDS)
                .ignoring(NoSuchElementException.class);
        wait.until(new Predicate<WebDriver>() {
            public boolean apply(WebDriver driver) {
                return Bot.driver().getPageSource().contains("Congratulations");
            }
        });
    }

    @Test
    public void testSqlInjectionLabLessonSolutionAreNotAvailable() throws IOException {
        doLoginWebgoatUser();

        Bot.driver().get(baseWebGoatUrl + "/start.mvc#attack/1537271095/200");
        Bot.driver().get(baseWebGoatUrl + "/service/restartlesson.mvc");
        Bot.driver().get(baseWebGoatUrl + "/start.mvc#attack/1537271095/200");

        FluentWait<WebDriver> wait = new WebDriverWait(Bot.driver(), 15); // wait for a maximum of 15 seconds
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("lesson-title"), "LAB: SQL Injection"));

        WebElement user = Bot.driver().findElement(By.id("show-solution-button"));
        user.click();

        assertTrue(Bot.driver().getPageSource().contains("Could not find the solution file"));
    }


    @Test
    public void testLogoutMvc() {

        doLoginWebgoatUser();

        Bot.driver().get(baseWebGoatUrl + "/logout.mvc");

        assertTrue("Page title should be Logout Page", Bot.driver().getTitle().contains("Logout Page"));
        assertTrue("Logout message should be displayed to user when successful logout",
                Bot.driver().getPageSource().contains("You have logged out successfully"));
    }

    /**
     * Closes the {@link WebDriver} session.
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        Bot.driver().quit();
    }

}
