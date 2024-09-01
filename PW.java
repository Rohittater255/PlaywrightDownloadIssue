import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;
import com.microsoft.playwright.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PW {

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        Playwright.CreateOptions options= new Playwright.CreateOptions();
        //Setting Remote URL
        Map<String, String> env = new HashMap<>();
        env.put("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "1");
        env.put("PLAYWRIGHT_BROWSERS_PATH", "C:\\Users\\rohittat\\Downloads\\ffmpeg-1009\\");
        env.put("SELENIUM_REMOTE_URL", "http://localhost:4444/wd/hub");
        options.setEnv(env);

        // Set additional Selenium Grid capabilities
        JsonObject capabilities = new JsonObject();
        capabilities.addProperty("se:downloadsEnabled", true);
        capabilities.addProperty("acceptInsecureCerts", true);
        System.out.println("cap"+capabilities);
        env.put("SELENIUM_REMOTE_CAPABILITIES", capabilities.toString());


        Playwright playwright=Playwright.create(options);
        BrowserType browserType  =playwright.chromium();
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();
        launchOptions.setHeadless(false);
        launchOptions.setArgs(Arrays.asList("--start-maximized", "--allow-running-insecure-content","--ignore-certificate-errors","--remote-allow-origins=*","--allow-insecure-localhost"));
    //UPdate Proxies if required
        //        Proxy proxy = new Proxy("");
//        proxy.setBypass("");
//        launchOptions.setProxy(proxy);
        launchOptions.setDownloadsPath(Paths.get("/home/seluser/Downloads"));

        Browser browser=browserType.launch(launchOptions);
        BrowserContext browserContext=browser.newContext();
        Page page=browserContext.newPage();
        page.navigate("https://chromedriver.storage.googleapis.com/index.html?path=114.0.5735.90/");
        page.waitForDownload(()->page.click("//a[text()='chromedriver_linux64.zip']"));


        HttpClient client = HttpClient.newHttpClient();

        // Create a GET request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4444/status"))
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

        ArrayList sessionIds=  JsonPath.read(response.body(), "$..sessionId");

        // Create a GET request
         request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4444/session/"+sessionIds.get(0)+"/se/files"))
                .GET()
                .build();

        // Send the request and get the response
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Print the response
        if (response.statusCode() == 200) {
            System.out.println(response.body());
        } else {
            System.out.println("Failed to retrieve data: " + response.statusCode());
        }

    }
}
