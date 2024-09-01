import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Sel {

    public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException {

        //Initialize Remote Webdriver
        ChromeOptions options = new ChromeOptions();
        options.setCapability("se:downloadsEnabled", true);
        RemoteWebDriver driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), options);

        //Navigate TO Page and download
        driver.navigate().to("https://chromedriver.storage.googleapis.com/index.html?path=114.0.5735.90/");
        Thread.sleep(5000);
        driver.findElement(By.xpath("//a[text()='chromedriver_linux64.zip']")).click();
        Thread.sleep(5000);


        HttpClient client = HttpClient.newHttpClient();
        // Create a GET request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4444/session/"+driver.getSessionId()+"/se/files"))
                .GET()
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Print the response
        if (response.statusCode() == 200) {
            System.out.println(response.body());
        } else {
            System.out.println("Failed to retrieve data: " + response.statusCode());
        }


        Thread.sleep(2000);
        driver.close();
        driver.quit();
    }
}
