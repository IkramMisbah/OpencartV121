package testBase;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;  //Log4j
import org.apache.logging.log4j.Logger;  //Log4j
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;



//contient les methodes communrs entre les test cases(re usable methods)


public class BaseTest {
	   public static WebDriver driver;//car on a creer un BaseTestObject ds capturescreen qui aura un autre driver donc il aura un conflit donc on doit le mettre static pour le mettre common variable
	   public Logger logger;  //Log4j
	   public Properties p;
	   
	   
	   
	    @BeforeClass(groups= {"Sanity","Regression","Master"})
	    @Parameters({"os","browser"})
	    public void setup(String os, String br) throws IOException {
	    	
			//Loading config.properties file
			FileReader file=new FileReader("./src//test//resources//config.properties");
			p=new Properties();
			p.load(file);

	    	
	    	//this will auto get the log4j2.xml file
	    	logger=LogManager.getLogger(this.getClass());  //lOG4J2
	    	if(p.getProperty("execution_env").equalsIgnoreCase("remote"))
			{
				DesiredCapabilities capabilities=new DesiredCapabilities();
				
				//os
				if(os.equalsIgnoreCase("windows"))
				{
					capabilities.setPlatform(Platform.WIN11);
				}
				else if(os.equalsIgnoreCase("linux"))
				{
					capabilities.setPlatform(Platform.LINUX);
					
				}
				else if (os.equalsIgnoreCase("mac"))
				{
					capabilities.setPlatform(Platform.MAC);
				}
				else
				{
					System.out.println("No matching os");
					return;
				}
				
				//browser
				switch(br.toLowerCase())
				{
				case "chrome": capabilities.setBrowserName("chrome"); break;
				case "edge": capabilities.setBrowserName("MicrosoftEdge"); break;
				case "firefox": capabilities.setBrowserName("firefox"); break;
				default: System.out.println("No matching browser"); return;
				}
				
				driver=new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),capabilities);
			}
			
					
			if(p.getProperty("execution_env").equalsIgnoreCase("local"))
			{

				switch(br.toLowerCase())
				{
				case "chrome" : driver=new ChromeDriver(); break;
				case "edge" : driver=new EdgeDriver(); break;
				case "firefox": driver=new FirefoxDriver(); break;
				default : System.out.println("Invalid browser name.."); return;
				}
			}
	        driver.manage().deleteAllCookies();
	        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	        driver.get(p.getProperty("appURL2")); // Correction de driver.get()
	        driver.manage().window().maximize();
	    }

	    @AfterClass(groups= {"Sanity","Regression","Master"})
	    public void tearDown() {
	        
	            driver.quit();
	        
	    }

	    public String randomString() {
	        return RandomStringUtils.randomAlphabetic(5); // Correction du nom de la méthode randomAlphabetic
	    }

	    public String randomNumber() {
	        return RandomStringUtils.randomNumeric(10);
	    }

	    public String randomAlphaNumeric() {
	        String generatedString = RandomStringUtils.randomAlphabetic(3);
	        String generatedNumber = RandomStringUtils.randomNumeric(3);
	        return generatedString + "@" + generatedNumber;
	    }
	    
	    
	    public String captureScreen(String tname) throws IOException {

			String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
					
			TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
			File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
			
			String targetFilePath=System.getProperty("user.dir")+"\\screenshots\\" + tname + "_" + timeStamp + ".png";
			File targetFile=new File(targetFilePath);
			
			sourceFile.renameTo(targetFile);
				
			return targetFilePath;

		}
}
