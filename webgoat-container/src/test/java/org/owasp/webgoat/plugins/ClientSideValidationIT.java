package org.owasp.webgoat.plugins;

import com.google.common.base.Predicate;
import org.junit.Test;
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

/**
 * ************************************************************************************************
 * This file is part of WebGoat, an Open Web Application Security Project utility. For details,
 * please see http://www.owasp.org/
 * <p>
 * Copyright (c) 2002 - 20014 Bruce Mayhew
 * <p>
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 * <p>
 * Getting Source ==============
 * <p>
 * Source for this application is maintained at https://github.com/WebGoat/WebGoat, a repository for free software
 * projects.
 * <p>
 *
 * @author WebGoat
 * @version $Id: $Id
 * @since September 18, 2016
 */
public class ClientSideValidationIT extends WebGoatIT {
    /**
     * Constructs a new instance of the test.  The constructor requires three string parameters, which represent the operating
     * system, version and browser to be used when launching a Sauce VM.  The order of the parameters should be the same
     * as that of the elements within the {@link #browsersStrings()} method.
     *
     * @param os
     * @param version
     * @param browser
     * @param deviceName
     * @param deviceOrientation
     */
    public ClientSideValidationIT(String os, String version, String browser, String deviceName, String deviceOrientation) {
        super(os, version, browser, deviceName, deviceOrientation);
    }

    @Test
    public void testClientSideValidation() throws IOException {
        doLoginWebgoatUser();

        driver.get(baseWebGoatUrl + "/start.mvc#attack/1129417221/200");
        driver.get(baseWebGoatUrl + "/service/restartlesson.mvc");
        driver.get(baseWebGoatUrl + "/start.mvc#attack/1129417221/200");

        FluentWait<WebDriver> wait = new WebDriverWait(driver, 15); // wait for a maximum of 15 seconds
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("lesson-title"), "Insecure Client Storage"));

        //Stage 1
        WebElement user = driver.findElement(By.name("field1"));
        user.click();
        user.sendKeys("PLATINUM");

        WebElement submit = driver.findElement(By.name("SUBMIT"));
        submit.click();
        wait = new FluentWait(driver)
                .withTimeout(20, SECONDS)
                .pollingEvery(2, SECONDS)
                .ignoring(NoSuchElementException.class);
        wait.until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver input) {
                return driver.getPageSource().contains("Stage 2");
            }
        });

        //Stage 2
        wait = new FluentWait(driver)
                .withTimeout(10, SECONDS)
                .pollingEvery(2, SECONDS)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        WebElement qty = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("QTY1")));
        qty.click();
        qty.sendKeys("8");
        qty = driver.findElement(By.name("QTY1"));
        qty.click();
        qty.sendKeys("8");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);


        JavascriptExecutor javascript = (JavascriptExecutor) driver;
        String cmd = "document.getElementsByName('GRANDTOT')[0].value = '$0.00';";
        javascript.executeScript(cmd);

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);


        submit = driver.findElement(By.name("SUBMIT"));
        submit.click();
        wait = new FluentWait(driver)
                .withTimeout(10, SECONDS)
                .pollingEvery(2, SECONDS)
                .ignoring(NoSuchElementException.class);
        wait.until(new Predicate<WebDriver>() {
            public boolean apply(WebDriver driver) {
                return driver.getPageSource().contains("Congratulations");
            }
        });
    }
}
