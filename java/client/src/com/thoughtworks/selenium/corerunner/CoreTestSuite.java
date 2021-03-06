// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package com.thoughtworks.selenium.corerunner;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CoreTestSuite {

  private String url;

  public CoreTestSuite(String url) {
    this.url = url;
  }

  public void run(Results results, WebDriver driver, Selenium selenium) {
    if (!url.equals(driver.getCurrentUrl())) {
      driver.get(url);
    }

    List<WebElement> allTables = driver.findElements(By.id("suiteTable"));
    if (allTables.isEmpty()) {
      throw new SeleniumException("Unable to locate suite table: " + url);
    }

    List<String> allTestUrls = (List<String>) ((JavascriptExecutor) driver).executeScript(
      "var toReturn = [];\n" +
      "for (var i = 0; i < arguments[0].rows.length; i++) {\n" +
      "  if (arguments[0].rows[i].cells.length == 0) {\n" +
      "    continue;\n" +
      "  }\n" +
      "  var cell = arguments[0].rows[i].cells[0];\n" +
      "  if (!cell) { continue; }\n" +
      "  var allLinks = cell.getElementsByTagName('a');\n" +
      "  if (allLinks.length > 0) {\n" +
      "    toReturn.push(allLinks[0].href);\n" +
      "  }\n" +
      "}\n" +
      "return toReturn;\n",
      allTables.get(0));

    for (String testUrl : allTestUrls) {
      new CoreTest(testUrl).run(results, driver, selenium);
    }
  }
}
