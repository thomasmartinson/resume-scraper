package scraper;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Extracts names, email addresses, and phone numbers from candidates in an
 * existing saved search on Monster.com.
 */
public class MonsterSearch extends Scraper {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public MonsterSearch(ScraperDriver driver, TranscriptWindow tw) {
        super(driver, tw);
        this.driver = driver;
        wait = new WebDriverWait(driver, 1);
    }

    @Override
    public boolean login(String username, String password) {
        try {
            driver.get("https://hiring.monster.com/login.aspx");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("form-control")));
            wait(1, 2);
            driver.findElements(By.className("form-control")).get(0).sendKeys(username);
            wait(1, 2);
            driver.findElements(By.className("form-control")).get(1).sendKeys(password);
            wait(1, 2);
            driver.findElements(By.className("redux-button")).get(1).click();
            wait(5, 6);
            if (driver.getCurrentUrl().contains("ogin")) {
                try {
                    String msg = driver.findElement(By.className("validation-summary")).findElement(By.tagName("li")).getText();
                    if (!msg.contains("You have entered an invalid email and/or password.")) {
                        print("This account is currently in use or was not logged out recently.");
                        print("The account will automatically reset in 1 hour.");
                    }
                } catch (NoSuchElementException e) {
                }
                return false;
            }
            if (driver.getCurrentUrl().contains("Challenge")) {
                // answer the security question
                print("Answering security question...");
                try {
                    WebElement answer = driver.findElement(By.className("question-answer-textbox"));
                    answer.sendKeys("GreatQuestion");
                    answer = driver.findElement(By.className("question-enter"));
                    answer.findElement(By.tagName("input")).click();
                } catch (NoSuchElementException e) {
                }
            }

            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("inventory-body")));
            wait(1, 2);
            return true;
        } catch (org.openqa.selenium.WebDriverException e) {
            return false;
        }
    }

    @Override
    public void logout() {
        driver.get("https://hiring.monster.com");
        wait(3, 4);
        driver.findElement(By.className("fa-cog")).click();
        wait(2, 3);
        driver.findElement(By.linkText("Sign Out")).click();
    }

    @Override
    public boolean isLoggedIn() {
        return true;
    }

    @Override
    public void goToSearches() {
        // navigate to homepage
        driver.get("https://hiring.monster.com");
        wait(6, 7);

        // view all saved searches
        driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_cphHomeBody_ctl03_ctrlSavedSearches_rptrRecentlySavedSearches_ctl00_lnkViewSavedSearches"))
                .click();
        wait(3, 4);
    }

    @Override
    public String[] getTargets() throws NoSavesException {
        goToSearches();

        // make list of all saved searches
        List<WebElement> targets = driver.findElements(By.className("dgColumnName"));
        String[] result = new String[targets.size() - 1];
        for (int i = 1; i < targets.size(); i++) {
            result[i - 1] = targets.get(i).findElement(By.tagName("a")).getText();
        }

        // throw exception if there are no saved searches
        if (result.length == 0) {
            throw new NoSavesException();
        }

        return result;
    }

    @Override
    public void scrape(String targetName) throws ScraperException {
        goToSearches();

        // click on correct search
        driver.findElement(By.linkText(targetName)).click();

        print("Search located.");
        wait(2, 3);

        // find total number of search results
        int numResults = getNumResults();
        print(numResults + " search results...");

        // find all candidates on page
        List<WebElement> candidates;
        int num = 0, numExtracted = 0, numHidden = 0, numSkipped = 0, page = 1;
        while (num < numResults) { // loop through every page
            int numErrors = 0;
            for (int i = 0; i < 50 && num < numResults; i++) {
                num++;

                // throw exception if too many errors are caught
                if (numErrors >= 3) {
                    throw new TimeOutException();
                }

                WebElement candidate = driver.findElements(By.className("row")).get(i);
                String name;
                try {
                    name = candidate.findElement(By.id("linkResumeTitle")).getText().trim();
                } catch (Exception e) {
                    name = candidate.findElement(By.className("candidateNameTitle")).getText().trim();
                }

                if (name.contains("—")) {
                    name = name.substring(0, name.indexOf("—") - 1);
                }
                String extractReport = num + " of " + numResults + ": " + name;

                // handle confidential or blocked candidates
                boolean blocked = false;
                try {
                    candidate.findElement(By.linkText("Unblock Resume"));
                    blocked = true;
                } catch (WebDriverException e) {
                }
                if (name.contains("CONFIDENTIAL") || blocked) {
                    print(extractReport + ", skipped, resume hidden.");
                    numHidden++;
                    continue;
                }

                // handle previously viewed candidates
                if (cutoff > 0 && candidate.findElement(By.className("ViewedLabel")).isDisplayed()) {
                    print(extractReport + ", skipped, viewed before.");
                    numSkipped++;
                    continue;
                }

                // access the candidate profile
                boolean isLoaded = true;
                int numClicks = 0;
                WebElement date = candidate;
                while (true) {
                    try {
                        driver.findElements(By.id("linkResumeTitle")).get(i).click();
                    } catch (Exception e) {
                        driver.findElements(By.className("candidateNameTitle")).get(i).click();
                    }
                    numClicks++;

                    // wait until the page is fully loaded
                    wait(3, 6);
                    int numLoops = 0;
                    try {
                        try {
                            date = driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_ctl00_ctlResumeTab_lblSubmitted"));
                        } catch (Exception e) {
                            date = driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_lld_Detail_controlResumeTab_lblResumeUpdated"));
                        }
                        while (!date.isDisplayed()) {
                            isLoaded = true;
                            numLoops++;
                            wait(0, 1);
                            // break loop after ~30 seconds
                            if (numLoops > 60) {
                                print("[INFO] Profile took longer to load than expected.");
                                isLoaded = false;
                                break;
                            }
                        }
                    } catch (WebDriverException w) {
                        isLoaded = false;
                        print("[INFO] Profile loaded improperly.");
                    }

                    // go back to the search page if profile wasn't loaded
                    if (!isLoaded) {
                        try {
                            driver.findElement(By.className("btn-link-back")).click();
                        } catch (WebDriverException w) {
                            print("'Back to search' button is not working.");
                        }
                    } else {
                        break; // escape the loop and continue
                    }

                    // print error warning if tried too many times
                    if (numClicks >= 3) {
                        numErrors++;
                        print("[WARNING] " + extractReport + ", could not load profile.");
                        break;
                    } else {
                        print("Trying again...");
                    }
                    wait(6, 7);
                }

                // continue if the page wasn't loaded
                if (!isLoaded) {
                    continue;
                }

                // scrape info from page
                String email, lastUpdated, phone;
                try {
                    email = driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_lld_Detail_controlDetailTop_linkEmail")).getText().trim();
                    // extract phone number, favoring mobile number
                    phone = driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_lld_Detail_controlDetailTop_lbMobile")).getText().trim();
                    if (phone.length() < 10) {
                        phone = driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_lld_Detail_controlDetailTop_lbHome")).getText().trim();
                    }
                } catch (Exception e) {
                    email = driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_ctl00_controlDetailTop_linkEmail")).getText().trim();
                    // extract phone number, favoring mobile number
                    phone = driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_ctl00_controlDetailTop_lbMobile")).getText().trim();
                    if (phone.length() < 10) {
                        phone = driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_ctl00_controlDetailTop_lbHome")).getText().trim();
                    }
                }
                lastUpdated = date.getText().trim();

                // output info
                print(extractReport + " (" + lastUpdated + "), extracted.");
                outputCandidate(name, email, phone, lastUpdated);
                numExtracted++;

                // go back to folder
                driver.findElement(By.className("btn-link-back")).click();
                wait(1, 2);
                numErrors = 0;
            }

            if (num < numResults) {
                // get to the next page
                goToNextPage(++page);
            }
        }

        // print extraction report
        int numUnaccounted = numResults - Math.min(num, numResults);
        print("Extracted " + numExtracted + " of " + numResults + " candidates from " + targetName + ".");
        print(numSkipped + " skipped, " + numHidden + " hidden, " + numUnaccounted + " unaccounted for.");
    }

    /**
     * navigates scraper to the next page of monster candidates
     *
     * @param page the page it is currently on
     */
    private void goToNextPage(int page) {
        print("Moving to page " + page + "...");
        if (page % 5 != 1) {
            List<WebElement> pages = driver.findElements(By.className("page"));
            for (WebElement pg : pages) {
                if (pg.getText().contains(Integer.toString(page))) {
                    pg.click();
                    break;
                }
            }
        } else {
            driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_ctl00_pagingHeader_pagerNext")).click();
        }
        wait(7, 8);
    }

    /**
     * finds the total number of results in a given search or folder must inside
     * folder or search results page to work properly
     *
     * @return the number of results in the target folder/search
     */
    private int getNumResults() {
        wait(1, 2);
        String str;
        try {
            str = driver.findElement(By.className("recordsDisplay")).findElement(By.tagName("span")).getText();
        } catch (Exception e) {
            str = driver.findElement(By.className("recordPortionIndicator")).findElement(By.tagName("span")).getText();
        }
        str = str.replace(",", "");
        str = str.substring(0, str.indexOf(" Candidates"));
        if (str.length() == 2) {
            return 0;
        } else {
            return Integer.parseInt(str.substring(str.indexOf(" of ") + 4));
        }
    }
}
