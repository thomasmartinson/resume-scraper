package scraper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Extracts names, email addresses, and phone numbers from candidates in an
 * existing saved search on the Dice.com Talent Search Beta.
 */
public class DiceSearchBeta extends DiceSearch {

    private final ScraperDriver driver;
    private final TranscriptWindow tw;
    private final WebDriverWait wait;
    private int lastPage;
    private boolean goingForward;
    private final String firstText, previousText, nextText, lastText;

    public DiceSearchBeta(ScraperDriver driver, TranscriptWindow tw) {
        super(driver, tw);
        this.driver = driver;
        this.tw = tw;
        wait = new WebDriverWait(driver, 1);
        firstText = "«";
        previousText = "‹";
        nextText = "›";
        lastText = "»";
    }

    @Override
    public void goToSearches() {
        // go to home page
        driver.get("https://employer.dice.com");

        // navigate to saved search page
        wait(1, 2);
        driver.findByClass("jobsCandidatesDD").click();
        wait(1, 2);
        driver.findElements(By.linkText("Manage Alerts")).get(1).click();
        wait.until(ExpectedConditions.urlContains("search"));
        wait(6, 7);
    }

    @Override
    public String[] getTargets() throws NoSavesException {
        goToSearches();

        // store target names
        ArrayList<String> targets = new ArrayList();
        for (WebElement e : driver.findElements(By.className("saved-search-name"))) {
            targets.add(e.findElement(By.tagName("button")).getText());
        }

        // throw exception
        if (targets.isEmpty()) {
            throw new NoSavesException();
        }

        // return
        return targets.toArray(new String[0]);
    }

    @Override
    public void scrape(String targetName) throws TimeOutException {
        goToSearches();

        // click on search
        boolean isClicked = false;
        for (int i = 0; i < 15 && !isClicked; i++) {
            for (WebElement e : driver.findAllByClass("saved-search-result-item")) {
                WebElement f = e.findElement(By.className("btn-link"));
                if (f.getText().equals(targetName)) {
                    e.findElement(By.className("align-items-start")).findElement(By.className("run-search-button")).click();
                    print("Search located.");
                    isClicked = true;
                    break;
                }
            }
            wait(2.0);
        }

        // make sure page loads fully, extract the total number of results
        int total = -Integer.MAX_VALUE;
        for (int i = 0; i < 15; i++) {
            try {
                // assign the total number of search results, checking for comma if > 1000
                WebElement e = driver.findById("searchSummary");
                e = e.findElement(By.className("profileCount"));
                String results = e.getText();
                results = results.substring(results.indexOf(" OF ") + 4);
                total = takeTotalFrom(results);
                print(total + " search results.");
                break;
            } catch (NoSuchElementException ex) {
                // do nothing, loop
            }
            // break loop after ~30 seconds
            if (i == 14) {
                throw new TimeOutException();
            }
            wait(2.0);
        }
        lastPage = ((total - 1) / 20) + 1;
        wait(3, 7);

        // scrape over all the candidates
        int num = 0, numExtracted = 0, numSkipped = 0, numHidden = 0, currentPage = 1, timesRepeated = 0;
        goingForward = true;
        TreeSet<String> appeared = new TreeSet();
        while (num < total) {
            for (WebElement candidate : driver.findAllByTag("dhi-us-search-result")) {
                num++;
                WebElement link = candidate.findElement(By.className("view-link"));
                String name = link.getText();
                String lastActive = candidate.findElement(By.className("last-active-on-brand"))
                        .findElement(By.tagName("span")).getText();
                String report = num + " of " + total + ": " + name + " (" + lastActive + "), ";

                // skip if this candidate is a duplicate
                if (appeared.contains(report)) {
                    num--;
                    continue;
                }
                appeared.add(report);

                // skip based on last-viewed criteria
                String skipReport = shouldSkip(candidate);
                if (skipReport.contains("skipped")) {
                    print(report + skipReport);
                    numSkipped++;
                    continue;
                }

                // hot diggity
                link.click();
                switchTab();
                wait(1, 2);

                // make sure profile loads
                String email = "null";
                for (int i = 0; i < 60; i++) {
                    try {
                        email = driver.findByClass("email-icon").getText();
                        break;
                    } catch (Exception e) {
                    }
                    wait(0.5);
                    if (i >= 15) {
                        print("[INFO] " + report + "unable to load resume.");
                        driver.close();
                        switchTab();
                        wait(1, 2);
                    }
                }
                String phone;
                try {
                    phone = driver.findByClass("phone-icon").getText();
                } catch (WebDriverException e) {
                    phone = "";
                }
                String lastUpdated = driver.findByClass("profile-activity")
                        .findElements(By.className("media-body")).get(1).getText().replace("Résumé updated ", "");
                String url = driver.getCurrentUrl();
                outputCandidate(name, email, phone, lastUpdated, url);
                print(report + "extracted.");
                numExtracted++;

                // pull out, you're not doing any good back there!
                driver.close();
                switchTab();
                wait(1, 2);
            }

            // stop scraping if necessary
            if (num >= total) {
                break;
            }

            // go to the next page, make sure it loads fully
            // navigate to next page if it exists 
            if (num >= total) {
                break;
            }

            // make sure the next page button isn't disabled
            try {
                String disabled = driver.findByClass("pagination").findElement(By.className("disabled")).getText();
                if ((disabled.equals(nextText) && goingForward) || (disabled.equals(firstText) && !goingForward)) {
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
                        goToNextPage(++currentPage);
                    } else {
                        goToNextPage(--currentPage);
                    }
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

    private void jumpToPage(int destination) throws TimeOutException {
        // save the state of goingForward
        boolean oldDirection = goingForward;
        goingForward = true;

        // go to page 1
        if (lastPage == 1) {
            throw new TimeOutException();
        } else if (destination == 1) {
            goToNextPage(lastPage);
        }
        goToNextPage(1);

        // move to the current page
        int currentPage = 1;
        while (currentPage < destination) {
            goToNextPage(++currentPage);
        }

        // reassign goingForward to its previous state
        goingForward = oldDirection;
    }

    private void goToNextPage(int nextPage) throws TimeOutException {
        // save some candidate names
        String oldCount = driver.findByClass("profileCount").getText();

        // click on the button for the next page
        String buttonText;
        if (nextPage == lastPage) {
            buttonText = lastText;
        } else if (nextPage == 1) {
            buttonText = firstText;
        } else if (nextPage % 5 == 1 && goingForward) {
            buttonText = nextText;
        } else if (nextPage % 5 == 0 && !goingForward) {
            buttonText = previousText;
        } else {
            buttonText = String.valueOf(nextPage);
        }
        print("Moving to page " + nextPage + "...");
        driver.findElement(By.linkText(buttonText)).click();

        // wait until the page is sufficiently loaded by checking if an element has changed
        String newCount = oldCount;
        for (int i = 0; i < 15 && oldCount.equals(newCount); i++) {
            wait(2.0);
            try {
                newCount = driver.findByClass("profileCount").getText();
            } catch (Exception e) {
            }
            if (i >= 15) {
                throw new TimeOutException();
            }
        }

        // wait for the rest of the candidates on the page to appear
        wait(3, 4);
    }

    private int daysSinceViewed() {
        if (cutoff > 0 && driver.elementIsPresent(By.className("viewed"))) {
            String str = driver.findByClass("viewed").getText().trim();
            str = str.replace("Last Viewed: ", "");
            int month, day, year;
            if (str.contains("Jan")) {
                month = 0;
            } else if (str.contains("Feb")) {
                month = 1;
            } else if (str.contains("Mar")) {
                month = 2;
            } else if (str.contains("Apr")) {
                month = 3;
            } else if (str.contains("May")) {
                month = 4;
            } else if (str.contains("Jun")) {
                month = 5;
            } else if (str.contains("Jul")) {
                month = 6;
            } else if (str.contains("Aug")) {
                month = 7;
            } else if (str.contains("Sep")) {
                month = 8;
            } else if (str.contains("Oct")) {
                month = 9;
            } else if (str.contains("Nov")) {
                month = 10;
            } else {
                month = 11;
            }
            day = Integer.valueOf(str.substring(str.indexOf(" ") + 1, str.indexOf(",")));
            year = Integer.valueOf(str.substring(str.indexOf(", ") + 2));
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            Date past = c.getTime();
            Date today = Calendar.getInstance().getTime();
            long time = today.getTime() - past.getTime();
            return (int) TimeUnit.DAYS.convert(time, TimeUnit.MILLISECONDS);
        }
        return Integer.MAX_VALUE;
    }

    private String shouldSkip(WebElement candidate) {
        // get the variables
        int i = 2;
        if (byMe) {
            i = 1;
        }

        int[] days = new int[2];
        String[] viewMsgs = new String[2];
        for (int n = 0; n < i; n++) {
            // click on the icon
            candidate.findElement(By.className("search-result-profile-readonly-action-icons"))
                    .findElements(By.tagName("i")).get(n).click();

            // grab the text
            String viewMsg
                    = driver.findByTag("bs-tooltip-container").findElement(By.tagName("span")).getText();

            // cut off the "Viewed: " part
            String text = "Group Viewed: ";
            if (n == 0) {
                text = "Viewed: ";
            }
            if (viewMsg.contains(text)) {
                viewMsg = viewMsg.replace(text, "");
            }
            viewMsgs[n] = viewMsg;

            // get the days from the message
            days[n] = getDaysFrom(viewMsgs[n]);
        }

        // take the smallest
        int indexOfMin = 0;
        if (!byMe && days[1] < days[0]) {
            indexOfMin = 1;
        }

        // skip
        if (days[indexOfMin] <= cutoff) {
            return "skipped, viewed " + viewMsgs[indexOfMin] + ".";
        }
        return ""; // go through with it
    }

    /**
     * "a few seconds ago" "a minute ago" "x minutes ago" " " yesterday??? "x
     * days ago" "a month ago" "x months ago" "a year ago" "x years ago" "This
     * candidate has not been viewed yet." "This candidate has not been viewed
     * by anyone in your group yet."
     */
    private int getDaysFrom(String viewMsg) {
        // not viewed yet
        if (viewMsg.charAt(0) == 'T') {
            return Integer.MAX_VALUE;
        }

        // get the multiple of whatever unit
        int mult;
        try {
            mult = Integer.parseInt(viewMsg.substring(0, viewMsg.indexOf(" ")));
        } catch (NumberFormatException e) {
            mult = 1;
        }

        // day/month/year
        if (viewMsg.contains("day")) {
            return 1 * mult;
        } else if (viewMsg.contains("month")) {
            return 30 * mult;
        } else if (viewMsg.contains("year")) {
            return 365 * mult;
        }

        // yesterday
        if (viewMsg.contains("esterday")) {
            return 1;
        }
        // today
        return 0;
    }
}
