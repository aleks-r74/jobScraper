package com.alexportfolio.jobScraper.parser;

import com.alexportfolio.jobScraper.JobScraperApplication;
import com.alexportfolio.jobScraper.service.FileService;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.GeckoDriverInfo;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.SessionStorage;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
class NoElementsException extends RuntimeException{}

public class ParserUtils {

    FileService fileService;
    public enum WaitType { PRESENCE, CLICKABLE; }
    public WebDriver driver;
    WebDriverWait wDriver;
    JavascriptExecutor js;
    public static Map<String,String> xpaths;

    String broswerStateFileName = "settings/driverState/browserState.dat";
    String loginUrl = "https://www.linkedin.com/login";
    static Map<String, Object> browserState = null;
    private static final Logger logger = LoggerFactory.getLogger(ParserUtils.class);

    public ParserUtils() throws IOException {
        this.driver = new EdgeDriver();
        wDriver = new WebDriverWait(driver, Duration.ofSeconds(5));
        js = (JavascriptExecutor) driver;
        driver.manage().window().minimize();
        loadXpathProperties();
    }

    public void setFileService(FileService fileService) throws IOException {
        this.fileService = fileService;

    }

    // Gracefully locating a single element based on wait type with retry mechanism
    public Optional<WebElement> getElement(String xpath, WaitType w) {
        Optional<WebElement> element= Optional.empty();
        for(int i=0;i<2;i++)
            try{
                element = Optional.of(
                        switch (w) {
                            case PRESENCE ->
                                wDriver.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
                            case CLICKABLE ->
                                 wDriver.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
                });
                break;
            }catch(TimeoutException | NoElementsException e){
                if(i==1) return Optional.empty();
                sleep(2000);
            }
        return element;
    }

    // Gracefully locating a collection of elements with retry mechanism
    public Optional<List<WebElement>> getElements(String xpath) {
        //re-try mechanism
        Optional<List<WebElement>> items = Optional.empty();
        for(int i=0;i<2;i++)
            try{
                items = Optional.of(wDriver.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xpath))));
                if(items.isEmpty()) throw new NoElementsException();
                break;
            } catch(TimeoutException | NoElementsException e){
                if(i==1) return Optional.empty();
                sleep(2000);
            }
        return items;
    }

    // Scrolling
    public void scrollPage(String cardsXPath) {
        // we're scrolling the page while new cards available
        int cardsBefore = 0;
        int cardsAfter = 0;
        do {
           var optional = getElements(cardsXPath);
           if (optional.isEmpty()) return;
           List<WebElement> items = optional.get();

           cardsBefore = items.size();
           // take the last card and scroll it into view
           WebElement lastCard = items.get(cardsBefore - 1);
           js.executeScript("arguments[0].scrollIntoView(true);", lastCard);
           sleep(1000);
           cardsAfter = getElements(cardsXPath).get().size();
        } while (cardsAfter - cardsBefore > 0);

    }

    //Typing text
    public void typeText(WebElement e, String s) {
        if (e == null || s == null) throw new InvalidArgumentException("element or string to type in can not be null");
        char[] array = s.strip().toCharArray();
        js.executeScript("arguments[0].focus();", e);
        for (char ch : array) {
            e.sendKeys("" + ch);
            sleep(20);
        }
    }

    public void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void loadXpathProperties() throws IOException {
        if (xpaths != null) return; // return if xpaths exists

        Properties properties = new Properties();
        try(var fis = this.getClass().getResourceAsStream("/xpath.properties")){
            properties.load(fis);
        }
        // remove empty properties and remove whitespaces in keys and values
       xpaths = properties.entrySet()
                .stream()
                .filter(
                      entry->!entry.getKey().toString().isBlank() &&
                       !entry.getValue().toString().strip().isBlank()
                )
                .collect(Collectors.toMap(
                       entry->entry.getKey().toString().strip(),
                       entry->entry.getValue().toString().strip()
               ));

    }

    public Map<String, List<String>> getFilteringKeywords() {
        var prop = fileService.getKeywords();
        Function<String, List<String>> strToList = str->{
            return Arrays.stream(str.split(","))
                    .map(s->s.toLowerCase().strip())
                    .filter(s->s.length()!=0)
                    .collect(Collectors.toList());
        };

        var stopKeywords  = strToList.apply(prop.getProperty("stopKeywords"));
        var saveKeywords  = strToList.apply(prop.getProperty("saveKeywords"));
        return Map.of("stop", stopKeywords, "save", saveKeywords);
    }

    void loadBrowserState(){
         // load state from file only once
        if(browserState == null) {
            Map<String, Object> stateFromFile = loadObject(broswerStateFileName);
            if(stateFromFile instanceof Map<String,Object>)
                browserState = stateFromFile;
        }
        // if it's still null, return
        if(browserState == null) return;
        // before restoring the state we need to be on linkedIn.com domain
        driver.get(loginUrl);
        // proceed to set browser state
        LocalStorage    localStorage    = ((WebStorage) driver).getLocalStorage();
        SessionStorage  sessionStorage  = ((WebStorage) driver).getSessionStorage();

        // broswerState contains Map<String, Object>: two Map<String,String> and Set<Cookie>
        // we will extract these objects and will use their values to set browser state

        // saved data from previous session
        Map<String,String> savedLoacalStorage   = (Map<String,String>)  browserState.get("localStorage");
        Map<String,String> savedSessionStorage  = (Map<String,String>)  browserState.get("sessionStorage");
        Set<Cookie> savedCookies                = (Set<Cookie>)         browserState.get("cookies");
        // setting LocalStorage
        for(Map.Entry<String, String> entry : savedLoacalStorage.entrySet())
            localStorage.setItem(entry.getKey(), entry.getValue());
        // setting SessionStorage
        for(Map.Entry<String, String> entry : savedSessionStorage.entrySet())
            sessionStorage.setItem(entry.getKey(), entry.getValue());
        // setting Cookies
        for(Cookie cookie: savedCookies)
            driver.manage().addCookie(cookie);
        //after the state is restored we need to open 3rd party site before attempt to open LinkedIn.com
        driver.get("https://google.com");
    }

    void saveBrowserState(){
        LocalStorage localStorage = ((WebStorage) driver).getLocalStorage();
        SessionStorage sessionStorage = ((WebStorage) driver).getSessionStorage();
        Set<Cookie> currentCookieSet = driver.manage().getCookies();

        Map<String,String> sessionStorageMap = new HashMap<>();
        Map<String,String> localStorageMap = new HashMap<>();
        // extract keys from each object into Map<String, String>
        for (String key : localStorage.keySet())
            localStorageMap.put(key, localStorage.getItem(key));

        for (String key : sessionStorage.keySet())
            sessionStorageMap.put(key, sessionStorage.getItem(key));

        // put all Map<String,String> and Set<Cookies> into Map<String, Object>
        Map<String, Object> state = new HashMap<>();
        state.put("localStorage", localStorageMap);
        state.put("sessionStorage", sessionStorageMap);
        state.put("cookies", currentCookieSet);
        // save to file
        browserState = state;
        saveObject(state, broswerStateFileName);
    }

    public <T> void saveObject(T obj, String fileName){
        if(!Files.exists(Path.of(fileName)))
            try {
                Files.createFile(Path.of(fileName));
            } catch (IOException e) {
                logger.info("Couldn't create " + fileName);
            }

            try(var oos = new ObjectOutputStream(new FileOutputStream(fileName))){
                oos.writeObject(obj);
            } catch (FileNotFoundException e) {
                logger.info("File not found " + fileName);
            } catch (IOException e) {
                logger.info("Couldn't write to " + fileName);
            }
    }
    public <T> T loadObject(String fileName){
        try(var ois = new ObjectInputStream(new FileInputStream(fileName))){
            return (T) ois.readObject();
        } catch (FileNotFoundException e) {
           logger.info((fileName+" not found"));
        } catch (IOException | ClassNotFoundException e) {
            logger.info("Exception in ParserUtils.loadObject: "+e.getMessage());
        }
        return null;
    }

}
