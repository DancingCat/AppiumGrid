package com.netease.test;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
/**
 * 	批量上传照片
 * 前置条件：登录账号
 * 测试点：本地照片批量上传
 * 
 * 步骤：登陆   获取云相册照片数    批量上传   再获取云相册照片数量  断言  
 */
public class TestCase3 {
	private AndroidDriver<AndroidElement> driver;
	private WebDriverWait wait;
	private int pics;
	
	@Parameters({"deviceName","port","platformVersion"})
	@BeforeClass
	public void setUp(String port,String deviceName,String platformVersion) throws Exception {
		File classpathRoot = new File(System.getProperty("user.dir"));
		File appDir = new File(classpathRoot, "app");
		File app = new File(appDir, "Album_netease.apk");
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("deviceName", deviceName); // "Android Emulator"
		capabilities.setCapability("platformVersion", platformVersion);
		capabilities.setCapability("app", app.getAbsolutePath());
		capabilities.setCapability("appPackage", "com.netease.cloudalbum");
		// capabilities.setCapability("noReset", true);
		
		driver = new AndroidDriver<>(new URL("http://127.0.0.1:"+port+"/wd/hub"),
				capabilities);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		//设置等待时间30s
		wait = new WebDriverWait(driver,30);
	}
	@Test(dataProvider = "loginData")
	public void login(String userName,String password) {
		// 滑动引导页 不使用noReset
		wait.until(ExpectedConditions.presenceOfElementLocated(By
				.id("h_help1")));
		driver.swipe(400, 800, 200, 800, 200);
		driver.swipe(400, 800, 200, 800, 200);
		driver.findElementById("guide_btn").click();
		driver.findElementById("skipSet").click();
		WebDriverWait wait = new WebDriverWait(driver, 60);
		wait.until(ExpectedConditions.presenceOfElementLocated(By
				.id("UserName")));
		driver.findElement(By.id("UserName")).sendKeys(userName);
		driver.findElement(By.id("PassWord")).click();
		driver.findElement(By.id("PassWord")).sendKeys(password);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.findElement(By.id("login")).click();
		//断言是否登录成功
		Assert.assertTrue(driver.findElementByXPath("//*[contains(@text,'本地相册')]").isDisplayed(),"登录失败");
	}
	@Test(dependsOnMethods= "login")
	public void getCloudPics(){
		driver.swipe(200, 80, 300, 80, 200);
		driver.findElement(By.id("g_slidemenu_cloud_txt")).click();
		String text = driver.findElementsById("cloud_album_num").get(0).getText();
		pics = Integer.valueOf(text.substring(1, text.length()-1));
		
	}

	@Test(dependsOnMethods= "getCloudPics")
	public void batchUploadPic(){
		driver.swipe(100, 200, 600, 200, 200);
        driver.findElement(By.id("g_slidemenu_local_txt")).click();
        driver.findElementsById("local_album_wrap").get(0).click();
        int add = driver.findElements(By.id("image_photo")).size();
        driver.findElementsByClassName("android.widget.ImageView").get(0).click();
        driver.findElement(By.xpath("//*[contains(@text,'勾 选')]")).click();
        driver.findElementByXPath("//*[contains(@text,'全选')]").click();
        driver.findElementsById("photo_list_backup_btn").get(0).click();
        driver.findElementsById("cloud_album_name").get(0).click();
        driver.findElementsById("photo_list_backup_btn").get(0).click();
        try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(@text,'备份报告')]")));
        driver.findElementById("button2").click();
        String text = driver.findElementsById("cloud_album_num").get(0).getText();
		int total = Integer.valueOf(text.substring(1, text.length()-1));
		pics+=add;
		Assert.assertEquals(total, pics);
	}

	@AfterClass(alwaysRun = true)
	public void tearDown() throws Exception {
		driver.quit();
	}
	
	@DataProvider(name="loginData")
    public Object[][] loginData(){
        return new Object[][]{
            {"kengiloveyou057888@126.com","1990@lfk"}
        };
    }
}
