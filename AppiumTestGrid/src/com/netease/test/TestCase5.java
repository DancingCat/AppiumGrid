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
 * 本地相册的大图浏览 
 * 前置条件：登录账号
 * 测试点：查看照片信息
 * 		  上传到云端相册
 * 
 * 步骤：登陆  获取云相册相片数量   上传   再次获取云相册相片数量  断言 
 */
public class TestCase5 {
	private AndroidDriver<AndroidElement> driver;
	private WebDriverWait wait;
	private int pics;
	
	@Parameters({"deviceName","platformVersion"})
	@BeforeClass
	public void setUp(String deviceName,String platformVersion) throws Exception {
		File classpathRoot = new File(System.getProperty("user.dir"));
		File appDir = new File(classpathRoot, "app");
		File app = new File(appDir, "Album_netease.apk");
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("deviceName", deviceName); // "Android Emulator"
		capabilities.setCapability("platformVersion", platformVersion);
		capabilities.setCapability("app", app.getAbsolutePath());
		capabilities.setCapability("appPackage", "com.netease.cloudalbum");
		// capabilities.setCapability("noReset", true);
		
		driver = new AndroidDriver<>(new URL("http://192.168.31.119:8888/wd/hub"),
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
	@Test( dependsOnMethods= "login")
    public void cloudalbum(){
        driver.swipe(200, 80, 300, 80, 200);
		driver.findElement(By.id("g_slidemenu_cloud_txt")).click();
		String text = driver.findElementsById("cloud_album_num").get(0).getText();
		pics = Integer.valueOf(text.substring(1, text.length()-1));

        
    }
	
	@Test(dependsOnMethods = "cloudalbum")
	public void browseLocalPic(){
		driver.swipe(100, 200, 600, 200, 200);
        driver.findElement(By.id("g_slidemenu_local_txt")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("本地相册")));
		//点击第一个相册 的 第一张图
		driver.findElementsById("local_album_wrap").get(0).click();
		driver.findElementsById("image_photo").get(0).click();
		driver.findElementById("photo_info_btn").click();
		//弹窗显示 照片信息
		Assert.assertTrue(driver.findElementByXPath("//*[contains(@text,'相片信息')]").isDisplayed(),"本地图片查看照片信息失败");
		driver.findElementById("button1").click();
		driver.tap(1, 200, 200, 200);
		driver.findElementById("photo_back_or_not").click();
		driver.findElementById("cloud_album_cover").click();
		driver.findElementById("photo_confirm_upload_btn").click();
		//断言  图片备份成功 
		//Assert.assertTrue(driver.findElementByXPath("//*[contains(@text,'已备份')]").isDisplayed(),"本地图片备份失败");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		driver.pressKeyCode(4);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("image_photo")));
		driver.pressKeyCode(4);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("local_album_wrap")));
	}
	
	@Test(dependsOnMethods="browseLocalPic")
	public void getCloudPicts(){
		driver.swipe(100, 200, 600, 200, 200);
        driver.findElement(By.id("g_slidemenu_cloud_txt")).click();
        String text = driver.findElementsById("cloud_album_num").get(0).getText();
		int total = Integer.valueOf(text.substring(1, text.length()-1));
		pics += 1 ;
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
