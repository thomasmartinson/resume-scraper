package scraper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Extracts names, email addresses, and phone numbers from candidates in an
 * existing saved search on Dice.com.
 */
public class DiceSearch extends Scraper {

    private final ScraperDriver driver;
    private final TranscriptWindow tw;
    private final WebDriverWait wait;
    private int lastPage;
    private boolean goingForward;

    public DiceSearch(ScraperDriver driver, TranscriptWindow tw) {
        super(driver, tw);
        this.driver = driver;
        this.tw = tw;
        wait = new WebDriverWait(driver, 1);
    }

    @Override
    public boolean login(String username, String password) {
        // add the email domain if not provided by the user
        if (!username.contains("@")) {
            username += "@precisionsystems.com";
        }

        // navigate to login page
        driver.get("https://employer.dice.com");

        // repeat login attempts (max three times)
        int attempts = 0;
        try {
            while (!driver.getCurrentUrl().contains("login") && attempts < 3) {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.id("loginForm")));
                WebElement form = driver.findElement(By.id("loginForm"));
                form.findElement(By.name("USERNAME")).sendKeys(username);
                wait(1, 2);
                form.findElement(By.name("PASSWORD")).sendKeys(password);
                form.submit();
                attempts++;
                wait(1, 2);
                // check if someone else is logged in, overrides if so
                if (driver.getCurrentUrl().contains("bounce")) {
                    wait(1, 2);
                    driver.findElement(By.id("CONTINUE-button")).click();
                }
                wait(1, 2);
            }
        } catch (org.openqa.selenium.WebDriverException e) {
            return false;
        }

        // report login success
        return attempts < 3;
    }

    @Override
    public void goToSearches() {
        // go to home page
        driver.get("https://employer.dice.com");

        // navigate to saved search page
        wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Post Jobs & Find Candidates")));
        wait(1, 2);
        driver.findElement(By.id("Post Jobs & Find Candidates")).click();
        wait(1, 2);
        driver.findElement(By.linkText("Manage Alerts")).click();
        wait.until(ExpectedConditions.urlContains("alerts"));
        wait(1, 2);
    }

    @Override
    public void logout() throws Exception {
        driver.get("https://employer.dice.com"); // navigate to homepage
        // close out of beta promo window if it exists
        wait(1, 2);
        try {
            driver.findElement(By.id("ts4-beta-modal-close")).click();
        } catch (Exception e) {
        }
        // try for ~30 seconds, then quit
        WebElement logoutBtn;
        int numAttempts = 0;
        while (true) {
            wait(1, 2);
            logoutBtn = driver.findElement(By.linkText("Logout"));
            try {
                logoutBtn.click();
                break;
            } catch (Exception e) {
                if (numAttempts == 15) {
                    throw e;
                }
            }
            numAttempts++;
        }
        wait(1, 2);
    }

    @Override
    public boolean isLoggedIn() {
        // navigate to homepage
        driver.get("https://employer.dice.com");

        // check if the login form is present
        try {
            driver.findElement(By.id("loginForm"));
            return false;
        } catch (NoSuchElementException e) {
            return true;
        }
    }

    @Override
    public String[] getTargets() throws NoSavesException {
        // navigate to saved searches
        goToSearches();

        // find searches in the list
        ArrayList<String> targets = new ArrayList();
        int i = 0;
        for (WebElement target : driver.findElements(By.className("ng-binding"))) {
            if (i % 3 == 0) { // add every third
                targets.add(target.getText());
            }
            i++;
        }

        // throw exception
        if (targets.isEmpty()) {
            throw new NoSavesException();
        }

        // return the findings
        return targets.toArray(new String[0]);
    }

    @Override
    public void scrape(String targetName) throws ScraperException {
        // navigate to saved search
        goToSearches();

        // find the search in the list
        driver.findElement(By.linkText(targetName)).click();
        print("Search located.");

        // make sure page loads fully, extract the total number of results
        int total, numLoops = 0;
        while (true) {
            wait(1, 2);
            numLoops++;
            try {
                // assign the total number of search results, checking for comma if > 1000
                String results = driver.findElement(By.className("total-candidates-subhead")).getText();
                total = takeTotalFrom(results);
                print(total + " search results.");
                break;
            } catch (NoSuchElementException ex) {
                // do nothing, loop
            }
            // break loop after ~30 seconds
            if (numLoops > 20) {
                throw new TimeOutException();
            }
        }
        lastPage = ((total - 1) / 20) + 1;
        wait(4, 5);

        // scrape over all of the candidates
        goingForward = true;
        int num = 0, numExtracted = 0, numHidden = 0,
                numSkipped = 0, currentPage = 1, timesRepeated = 0;
        TreeSet<String> appeared = new TreeSet();
        while (true) {
            // view profile, outputs info, close tab, and repeat
            for (WebElement candidate : driver.findElements(By.className("list"))) {
                num++;

                // extract info from search results page
                String lastUpdated = candidate.findElement(By.className("last-updated")).getText();
                String name = candidate.findElement(By.tagName("a")).getText();
                String candidateInfo = name + " (" + lastUpdated + ")";
                String extractReport = num + " of " + total + ": " + candidateInfo;

                // check if this candidate is a duplicate
                if (appeared.contains(candidateInfo)) {
                    num--;
                    continue;
                } else {
                    appeared.add(candidateInfo);
                }

                // compute how many days since candidate was viewed
                String viewMsg;
                if (byMe) {
                    viewMsg = candidate.findElement(By.className("user-viewed")).findElement(By.tagName("i")).getAttribute("data-original-title");
                } else {
                    viewMsg = candidate.findElement(By.className("group-viewed")).findElement(By.tagName("i")).getAttribute("data-original-title");
                }
                int days = daysSinceViewed(viewMsg);

                if (days <= cutoff) { // skip if cutoff not met
                    if (days == 0) {
                        extractReport += ", skipped, viewed earlier today.";
                    } else if (days == 1) {
                        extractReport += ", skipped, viewed yesterday.";
                    } else { // plural
                        extractReport += ", skipped, viewed " + days + " days ago.";
                    }
                    numSkipped++;
                } else { // extract candidate
                    try {
                        String contactInfo = extract(candidate.findElement(By.tagName("a")));
                        // clean up data into simpler form
                        if (contactInfo == null) { // the profile is hidden
                            extractReport += ", skipped, resume hidden.";
                            numHidden++;
                        } else { // profile extracted
                            String email, phone;
                            if (contactInfo.contains(" ")) { // it has a phone number
                                email = contactInfo.substring(0, contactInfo.indexOf(" "));
                                phone = contactInfo.substring(email.length() + 3);
                            } else { // no phone number is provided
                                email = contactInfo;
                                phone = "";
                            }
                            String url = driver.getCurrentUrl();
                            extractReport += ", extracted.";
                            outputCandidate(name, email, phone, lastUpdated, url);
                            numExtracted++;
                        }
                    } catch (TimeOutException e) {
                        extractReport = "[INFO] " + extractReport + ", unable to load resume.";
                    }
                }
                print(extractReport);
                if (num >= total) {
                    break;
                }
            }

            // navigate to next page if it exists 
            if (num >= total) {
                break;
            }

            // make sure the next page button isn't disabled
            try {
                String disabled = driver.findElement(By.id("search-pagination")).findElement(By.className("disabled")).getText();
                if ((disabled.contains(">") && goingForward) || (disabled.contains("<") && !goingForward)) {
                    // here we want to repeat the scrape, because num < total
                    // we must have missed some people
                    print("[INFO] Some candidates are missing from the results.");
                    print("Select 'yes' or 'no' to continue.");

                    // open an option dialog
                    RepeatOptionsPanel panel = new RepeatOptionsPanel();
                    tw.showOptionPane(panel, siteName + " error");

                    // get response
                    int response = panel.userResponse;
                    if (response == RepeatOptionsPanel.NULL_OPTION) {
                        print("No response from user.");
                    }

                    if (response == RepeatOptionsPanel.YES_OPTION
                            || (response != RepeatOptionsPanel.NO_OPTION
                            && timesRepeated < 1)) {
                        timesRepeated++;
                        goingForward = !goingForward; // reverse direction
                        print("Repeating scrape of " + targetName + "...");
                        print("Looking for candidates that haven't appeared in this search yet...");

                        // jump around a bit
                        if (total > 20) {
                            if (goingForward) {
                                goToNextPage(lastPage);
                                goToNextPage(1);
                            } else {
                                goToNextPage(1);
                                goToNextPage(lastPage);
                            }
                        }
                    } else {
                        print("Won't repeat scrape of " + targetName + ".");
                        break;
                    }
                } else {
                    // go to next page
                    throw new NoSuchElementException("");
                }
            } catch (NoSuchElementException ex) {
                // go to next page
                try {
                    if (goingForward) {
                        currentPage++;
                    } else {
                        currentPage--;
                    }
                    goToNextPage(currentPage);
                } catch (TimeOutException e) {
                    print("[INFO] Page took too long to load. Attempting to reload the page...");
                    jumpToPage(currentPage);
                }
            }
        }

        // report results of scrape
        int numUnaccounted = total - Math.min(num, total);
        print("Extracted " + numExtracted + " of " + total + " candidates from " + targetName + ".");
        print(numSkipped + " skipped, " + numHidden + " hidden, " + numUnaccounted + " unaccounted for.");
    }

    private void goToNextPage(int nextPage) throws TimeOutException {
        // save some candidate names
        String oldName;
        try {
            oldName = driver.findElement(By.className("user-name")).findElement(By.tagName("a")).getText();
        } catch (NoSuchElementException e) {
            oldName = "blank";
        }

        // click on the button for the next page
        String buttonText;
        if (nextPage == lastPage) {
            buttonText = ">>";
        } else if (nextPage == 1) {
            buttonText = "<<";
        } else if (nextPage % 5 == 1 && goingForward) {
            buttonText = ">";
        } else if (nextPage % 5 == 0 && !goingForward) {
            buttonText = "<";
        } else {
            buttonText = String.valueOf(nextPage);
        }
        print("Moving to page " + nextPage + "...");
        driver.findElement(By.linkText(buttonText)).click();

        // wait until the page is sufficiently loaded by checking if an element has changed
        String newName = oldName;
        int numLoops = 0;
        while (newName.equals(oldName)) {
            numLoops++;
            wait(2.0);
            java.util.List<WebElement> test = driver.findElements(By.className("user-name"));
            if (!test.isEmpty()) {
                try {
                    newName = test.get(0).findElement(By.tagName("a")).getText();
                } catch (org.openqa.selenium.StaleElementReferenceException e) {
                }
            }
            // break loop after 20ish seconds of loading
            if (numLoops >= 10) {
                if (test.isEmpty()) {
                    print("This page is empty.");
                    break;
                } else {
                    throw new TimeOutException();
                }
            }
        }

        // wait for the rest of the candidates on the page to appear
        wait(3, 4);
    }

    private void jumpToPage(int destination) throws TimeOutException {
        // save the state of goingForward
        boolean oldDirection = goingForward;
        goingForward = true;

        // refresh page
        driver.get(driver.getCurrentUrl());
        wait(8, 9);

        // move to the current page
        int currentPage = 1;
        while (currentPage < destination) {
            goToNextPage(++currentPage);
        }

        // reassign goingForward to its previous state
        goingForward = oldDirection;
    }

    /**
     * extracts all of the contact info from the resume tab, closes the tab, and
     * returns the contact info
     *
     * @param candidate
     * @return the name and email of the candidate, null if hidden
     * @throws ScraperException
     */
    private String extract(WebElement candidate) throws ScraperException {
        candidate.click();
        switchTab();

        // handler in case dice be hatin and wants u to fail
        int overuses = 0;
        while (driver.getCurrentUrl().contains("overUsage")) {
            overuses++;
            if (overuses > 4) {
                throw new ViewLimitException();
            }
            print("[INFO] Overusage screen thrown.");
            driver.close();
            switchTab();
            candidate.click();
            switchTab();
        }

        // where u go if u gucci
        String contactInfo = "";
        if (driver.getCurrentUrl().contains("profileHidden")) {
            contactInfo = null;
        } else {
            // loop until the page is full loaded
            boolean isLoaded = false;
            int numLoops = 0;
            while (!isLoaded) {
                numLoops++;
                wait(1, 2);
                try {
                    contactInfo = driver.findElement(By.id("profile-email")).getText();
                } catch (NoSuchElementException exc) {
                }
                if (contactInfo.length() > 4) {
                    isLoaded = true;
                }
                // break the loop after 30ish seconds
                if (numLoops > 20) {
                    // check to make sure the user is still logged in
                    if (isLoggedIn()) {
                        driver.close();
                        switchTab();
                        throw new TimeOutException();
                    } else {
                        throw new LogoutException();
                    }
                }
            }
        }
        driver.close(); // close tab
        switchTab();    // switch back to parent tab
        return contactInfo;
    }

    /**
     * computes the days since the candidate was last viewed
     *
     * @param msg the view message given by Dice
     */
    private int daysSinceViewed(String msg) {
        int daysSinceViewed;
        if (msg == null || msg.length() == 0) { // has not been viewed
            daysSinceViewed = Integer.MAX_VALUE;
        } else if (msg.contains("day")) { // i.e. if it has been viewed in the past week
            int x, y = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            if (msg.contains("Sunday")) {
                x = 1;
            } else if (msg.contains("Monday")) {
                x = 2;
            } else if (msg.contains("Tuesda")) {
                x = 3;
            } else if (msg.contains("Wednes")) {
                x = 4;
            } else if (msg.contains("Thursd")) {
                x = 5;
            } else if (msg.contains("Friday")) {
                x = 6;
            } else if (msg.contains("Saturd")) {
                x = 7;
            } else if (msg.contains("Yester")) {
                x = y - 1;
            } else { // today
                x = y;
            }
            if (y < x) {
                daysSinceViewed = 7 + (y - x);
            } else {
                daysSinceViewed = y - x;
            }
        } else { // if the view message provides a full date
            int monthViewed = Integer.valueOf(
                    msg.substring(msg.length() - 10, msg.length() - 8)) - 1;
            int dayViewed = Integer.valueOf(
                    msg.substring(msg.length() - 7, msg.length() - 5));
            int yearViewed = Integer.valueOf(
                    msg.substring(msg.length() - 4, msg.length()));
            Calendar c = Calendar.getInstance();
            c.set(yearViewed, monthViewed, dayViewed);
            Date past = c.getTime();
            Date today = Calendar.getInstance().getTime();
            long time = today.getTime() - past.getTime();
            daysSinceViewed = (int) TimeUnit.DAYS.convert(time, TimeUnit.MILLISECONDS);
        }
        return daysSinceViewed;
    }

    /**
     * switches focus to the next open tab, waits
     */
    public void switchTab() {
        for (String childTab : driver.getWindowHandles()) {
            driver.switchTo().window(childTab);
        }
        wait(1, 2);
    }

    /**
     * extracts the total number of search results from a string in the format:
     * "x Candidates"
     *
     * @param str the input string
     * @return the number x
     */
    public int takeTotalFrom(String str) {
        int total;
        if (str.contains(",")) {
            total = Integer.valueOf(str.substring(0, str.indexOf(","))) * 1000
                    + Integer.valueOf(str.substring(str.indexOf(",") + 1, str.indexOf(" ")));
        } else {
            total = Integer.valueOf(str.substring(0, str.indexOf(" ")));
        }
        return total;
    }
}
