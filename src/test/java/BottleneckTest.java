import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BottleneckTest {

    private final static String URL_UPLOAD = "https://the-internet.herokuapp.com/upload";
    private final static String URL_DOWNLOAD = "https://the-internet.herokuapp.com/download";
    private WebDriver driver;
    private File file;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        file = new File("D:\\New folder\\2018-07-04_164054.png");
        if (file.exists())
            file.delete();
    }

    @Test
    public void uploadWithFireFoxTest() {
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(URL_UPLOAD);

        WebElement upload = driver.findElement(By.id("file-upload"));
        upload.sendKeys("D:\\Dropbox\\1.txt");
        driver.findElement(By.id("file-submit")).click();
        String actualText = driver.findElement(By.xpath("//div[@class=\"example\"]/h3")).getText();
        Assert.assertEquals(actualText, "File Uploaded!", "File didn't upload!");
        driver.close();
    }

    @Test
    public void uploadWithRobotClassFireFoxTest() {
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(URL_UPLOAD);

        WebElement upload = driver.findElement(By.id("file-upload"));
        Actions actions = new Actions(driver);
        actions.moveToElement(upload).click().perform();

        setClipboardData("D:\\Dropbox\\1.txt");
        try {
            Robot robot = new Robot();
            robot.delay(1000);
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.delay(1000);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        driver.findElement(By.id("file-submit")).click();
        String actualText = driver.findElement(By.xpath("//div[@class=\"example\"]/h3")).getText();
        Assert.assertEquals(actualText, "File Uploaded!", "File didn't upload!");
        driver.close();
    }

    @Test
    public void downloadWithChromeDriverTest() {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", "D:\\New folder");
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        WebDriver driver = new ChromeDriver(capabilities);

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(URL_DOWNLOAD);
        driver.findElement(By.cssSelector(".example a:nth-of-type(1)")).click();

        file = new File("D:\\New folder\\2018-07-04_164054.png");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(file.exists(), "File didn't upload!");
        driver.close();
    }

    @Test
    public void downloadWithFireFoxDriverTest() {
        FirefoxProfile fxProfile = new FirefoxProfile();
        fxProfile.setPreference("browser.download.folderList", 2);
        fxProfile.setPreference("browser.download.manager.showWhenStarting", false);
        fxProfile.setPreference("browser.download.dir", "D:\\New folder");
        fxProfile.setPreference("browser.helperApps.alwaysAsk.force", false);
        fxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/octet-stream");
        FirefoxOptions options = new FirefoxOptions();
        options.setProfile(fxProfile);
        driver = new FirefoxDriver(options);

        driver.get(URL_DOWNLOAD);
        driver.findElement(By.cssSelector(".example a:nth-of-type(1)")).click();

        file = new File("D:\\New folder\\2018-07-04_164054.png");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(file.exists(), "File didn't upload!");
        driver.close();
    }

    private static void setClipboardData(String string) {
        StringSelection stringSelection = new StringSelection(string);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }
}
