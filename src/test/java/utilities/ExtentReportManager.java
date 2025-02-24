package utilities;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.resolver.DataSourceUrlResolver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import testBase.BaseTest;

// Classe qui gère les rapports ExtentReports et implémente ITestListener de TestNG
public class ExtentReportManager implements ITestListener {
    public ExtentSparkReporter sparkReporter;
    public ExtentReports extent;
    public ExtentTest test;

    String repName;

    // Méthode appelée au début du test suite
    public void onStart(ITestContext testContext) {
        // Création d'un timestamp pour le nom du rapport
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        repName = "Test-Report-" + timeStamp + ".html";
        
        // Définition de l'emplacement du rapport
        sparkReporter = new ExtentSparkReporter(".\\reports\\" + repName);
        
        // Configuration du rapport
        sparkReporter.config().setDocumentTitle("Opencart Automation Report");
        sparkReporter.config().setReportName("Opencart Functional Testing");
        sparkReporter.config().setTheme(Theme.DARK);
        
        // Initialisation de l'objet ExtentReports
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        
        // Ajout d'informations système au rapport
        extent.setSystemInfo("Application", "Opencart");
        extent.setSystemInfo("Module", "Admin");
        extent.setSystemInfo("Sub Module", "Customers");
        extent.setSystemInfo("User Name", System.getProperty("user.name"));
        extent.setSystemInfo("Environment", "QA");
        
        // Ajout des informations sur l'OS et le navigateur
        String os = testContext.getCurrentXmlTest().getParameter("os");
        extent.setSystemInfo("Operating System", os);
        
        String browser = testContext.getCurrentXmlTest().getParameter("browser");
        extent.setSystemInfo("Browser", browser);
        
        // Ajout des groupes de test inclus dans le rapport
        List<String> includedGroups = testContext.getCurrentXmlTest().getIncludedGroups();
        if (!includedGroups.isEmpty()) {
            extent.setSystemInfo("Groups", includedGroups.toString());
        }
    }

    // Méthode appelée lorsqu'un test réussit
    public void onTestSuccess(ITestResult result) {
        test = extent.createTest(result.getTestClass().getName());
        test.assignCategory(result.getMethod().getGroups());
        test.log(Status.PASS, result.getName() + " got successfully executed");
    }

    // Méthode appelée lorsqu'un test échoue
    public void onTestFailure(ITestResult result) {
        test = extent.createTest(result.getTestClass().getName());
        test.assignCategory(result.getMethod().getGroups());
        test.log(Status.FAIL, result.getName() + " got failed");
        test.log(Status.INFO, result.getThrowable().getMessage());
        
        try {
            // Capture d'écran en cas d'échec
            String imgPath = new BaseTest().captureScreen(result.getName());
            test.addScreenCaptureFromPath(imgPath);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    // Méthode appelée lorsqu'un test est ignoré
    public void onTestSkipped(ITestResult result) {
        test = extent.createTest(result.getTestClass().getName());
        test.assignCategory(result.getMethod().getGroups());
        test.log(Status.SKIP, result.getName() + " got skipped");
        test.log(Status.INFO, result.getThrowable().getMessage());
    }

    // Méthode appelée à la fin du test suite
    public void onFinish(ITestContext testContext) {
        // Génération du rapport
        extent.flush();
        
        // Ouvrir automatiquement le rapport généré
        String pathOfExtentReport = System.getProperty("user.dir") + "\\reports\\" + repName;
        File extentReport = new File(pathOfExtentReport);
        
        try {
            Desktop.getDesktop().browse(extentReport.toURI());//will open the report auto in the browser no need to open it manually
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        /* Code pour envoyer le rapport par e-mail (actuellement désactivé) */
        /*
        try {
            URL url = new URL("file:///" + System.getProperty("user.dir") + "\\reports\\" + repName);
            ImageHtmlEmail email = new ImageHtmlEmail();
            email.setDataSourceResolver(new DataSourceUrlResolver(url));
            email.setHostName("smtp.googlemail.com");
            email.setSmtpPort(465);
            email.setAuthenticator(new DefaultAuthenticator("email@example.com", "password"));
            email.setSSLOnConnect(true);
            email.setFrom("email@example.com");
            email.setSubject("Test Results");
            email.setMsg("Please find Attached Report....");
            email.addTo("recipient@example.com");
            email.attach(url, "Extent Report", "Please check report...");
            email.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }
}
