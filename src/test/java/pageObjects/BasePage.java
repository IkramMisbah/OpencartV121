package pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

//this class containes only contructor
//it will be parent of all page classes

public class BasePage {
	WebDriver driver;
		
		public BasePage(WebDriver driver)
		{
			this.driver=driver;
			PageFactory.initElements(driver,this);
		}
}
