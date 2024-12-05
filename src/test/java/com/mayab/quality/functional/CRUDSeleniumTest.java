package com.mayab.quality.functional;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CRUDSeleniumTest {

  private static WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  JavascriptExecutor js;

  @BeforeEach
  public void setUp() throws Exception {
      WebDriverManager.chromedriver().setup();
      ChromeOptions options = new ChromeOptions();
      options.addArguments("--headless");
      options.addArguments("--disable-gpu");
      options.addArguments("--window-size=1920,1080");
      options.addArguments("--disable-extensions");
      options.addArguments("--no-sandbox");
      options.addArguments("--disable-dev-shm-usage");
      
      driver = new ChromeDriver(options);
      baseUrl = "https://mern-crud-mpfr.onrender.com/";
      driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
      js = (JavascriptExecutor) driver;
  }

  
  @Order(1)
  @Test
  public void createNewRecord_test() throws Exception {
    driver.get(baseUrl);
    driver.findElement(By.xpath("//div[@id='root']/div/div[2]/button")).click();
    driver.findElement(By.name("name")).click();
    driver.findElement(By.name("name")).clear();
    driver.findElement(By.name("name")).sendKeys("monkey");
    driver.findElement(By.name("email")).click();
    driver.findElement(By.name("email")).clear();
    driver.findElement(By.name("email")).sendKeys("monkey@hotmail.com");
    driver.findElement(By.name("age")).click();
    driver.findElement(By.name("age")).clear();
    driver.findElement(By.name("age")).sendKeys("18");
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Gender'])[2]/following::div[2]")).click();
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Male'])[1]/following::div[2]")).click();
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Woah!'])[1]/following::button[1]")).click();
    pause(5000);
    String actualResult = driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/form/div[4]/div/p")).getText();
    assertThat(actualResult, is("Successfully added!"));
    takeScreenshot("createNewRecord");
  }

  @Order(2)
  @Test
  public void existingEmail_test() throws Exception {
    driver.get(baseUrl);
    driver.findElement(By.xpath("//div[@id='root']/div/div[2]/button")).click();
    driver.findElement(By.name("name")).click();
    driver.findElement(By.name("name")).clear();
    driver.findElement(By.name("name")).sendKeys("monkey");
    driver.findElement(By.name("email")).click();
    driver.findElement(By.name("email")).clear();
    driver.findElement(By.name("email")).sendKeys("monkey@hotmail.com");
    driver.findElement(By.name("age")).click();
    driver.findElement(By.name("age")).clear();
    driver.findElement(By.name("age")).sendKeys("18");
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Gender'])[2]/following::div[2]")).click();
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Male'])[1]/following::div[2]")).click();
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Woah!'])[1]/following::button[1]")).click();
    pause(5000);
    String actualResult = driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/form/div[5]/div/p")).getText();
    assertThat(actualResult, is("That email is already taken."));
    takeScreenshot("existingEmail");
  }

  @Order(3)
  @Test
  public void modifyAge_test() throws Exception {
      driver.get(baseUrl);

      List<WebElement> rows = driver.findElements(By.xpath("//table/tbody/tr"));
      boolean recordFound = false;

      for (WebElement row : rows) {
          if (row.getText().contains("monkey")) {
              recordFound = true;

              WebElement editButton = row.findElement(By.xpath(".//td[5]/button[1]"));
              editButton.click();
              pause(1000);

              WebElement ageInput = driver.findElement(By.name("age"));
              ageInput.click();
              ageInput.clear();
              ageInput.sendKeys("25");

              WebElement saveButton = driver.findElement(By.xpath("//button[text()='Save']"));
              saveButton.click();
              pause(2000);
              break;
          }
      }

      assertTrue("The record 'monkey' was not found in the table.", recordFound);

      String actualResult = driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/form/div[4]/div/p")).getText();
      assertThat(actualResult, is("Successfully updated!"));
      
      boolean ageUpdated = driver.findElements(By.xpath("//table/tbody/tr"))
                                  .stream()
                                  .anyMatch(row -> row.getText().contains("monkey") && row.getText().contains("25"));
      assertTrue("The age was not updated correctly.", ageUpdated);
      takeScreenshot("modifyAge");
  }


  @Order(4)
  @Test
  public void findRecordByName_test() throws Exception {
	  driver.get(baseUrl);
	  pause(5000);

	  String targetName = "monkey";
	  
	  WebElement table = driver.findElement(By.xpath("//div[@id='root']/div/div[2]/table/tbody"));
	  
	  boolean isPresent = table.getText().contains(targetName);
	  
	  assertTrue("The record with the name '" + targetName + "' was not found in the table.", isPresent);
	  takeScreenshot("findRecordByName");
  }
  
  @Order(5)
  @Test
  public void deleteRecord_test() throws Exception {
      driver.get(baseUrl);

      List<WebElement> rows = driver.findElements(By.xpath("//table/tbody/tr"));

      boolean recordFound = false;

      for (WebElement row : rows) {
          if (row.getText().contains("monkey")) {
              recordFound = true;

              WebElement deleteButton = row.findElement(By.xpath(".//td[5]/button[2]"));
              deleteButton.click();
              pause(1000);

              WebElement confirmButton = driver.findElement(By.xpath("//button[text()='Yes']"));              
              confirmButton.click();
              pause(2000);
              break;
          }
      }

      assertTrue("The record 'monkey' was not found in the table.", recordFound);

      boolean isPresent = driver.findElements(By.xpath("//table/tbody/tr"))
                               .stream()
                               .anyMatch(row -> row.getText().contains("monkey"));
      assertFalse("The record 'monkey' is still present in the table.", isPresent);
      takeScreenshot("deleteRecord");
  }
  
  @Order(6)
  @Test
  public void findAllRecords_test() throws Exception {
	  driver.get(baseUrl);
      pause(5000);

      List<WebElement> rows = driver.findElements(By.xpath("//div[@id='root']/div/div[2]/table/tbody/tr"));
      
      assertFalse("No records were found in the table.", rows.isEmpty());

      for (WebElement row : rows) {
          System.out.println("records found: " + row.getText());
      }
  }

  @AfterEach
  public void tearDown() throws Exception {
    driver.quit();
  }

  private void pause(long mils) {
    try {
      Thread.sleep(mils);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  public static void takeScreenshot(String fileName) throws IOException {
	  File file = ((TakesScreenshot)driver).
			  getScreenshotAs(OutputType.FILE);
	  
	  FileUtils.copyFile(file, 
			  new File("src/test/resources/screenshots/"
			  		+ fileName + ".png"));
  }
}
