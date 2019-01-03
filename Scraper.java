package scraper;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Contains the main operation template of a scraper of a resume database.
 */
public abstract class Scraper {

    boolean byMe;
    int cutoff;
    String siteName;

    private final ArrayList<String> outFiles;
    private final String serverPath, localPath;
    private PrintWriter out;
    private final ScraperDriver driver;
    private final TranscriptWindow tw;

    public Scraper(ScraperDriver driver, TranscriptWindow tw) {
        this.driver = driver;
        this.tw = tw;
        String str = WebScraper.SERVER_PATH;
        if (WebScraper.TESTING) {
            str += "testing/";
        }
        serverPath = str;
        localPath = WebScraper.LOCAL_PATH;
        outFiles = new ArrayList();
    }

    /**
     * performs the scrape operation
     *
     * @param username the username for login attempt
     * @param password the password for login attempt
     */
    public void scrape(String username, String password) {
        boolean wasLoggedIn = false;
        try {
            // attempt login
            print("Attempting login...");
            if (login(username, password)) {
                print("Login successful; retrieving saved searches...");
                wasLoggedIn = true;
            } else { // login failed
                print("Login failed.");
                quit();
                return;
            }

            // open a window of all of the search options
            String[] savedSearches = getTargets();
            SavedSearchesPanel ss = new SavedSearchesPanel(savedSearches);
            print("Select the search(es) you want to scrape.");
            tw.showOptionPane(ss, siteName + " saved searches");
            String[] targetNames = ss.getSelected();

            // loop over all targets
            for (String targetName : targetNames) {
                // create new file and save its name
                outFiles.add(newOutFile(targetName));

                // scrape target
                print("Extracting candidates from " + targetName + "...");
                try {
                    scrape(targetName);
                } catch (TimeOutException e) {
                    if (isLoggedIn()) {
                        print("[INFO] Page loaded improperly. Aborting scrape of " + targetName + ".");
                    } else {
                        throw new LogoutException();
                    }
                }
                out.close();
            }
        } catch (java.io.InterruptedIOException
                | org.openqa.selenium.NoSuchSessionException ex) {
            return;
        } // this really only gets thrown when the user closes
        catch (LogoutException ex) {
            print("[INFO] You have been forcibly logged off of this profile.");
            quit();
            return;
        } catch (ScraperException ex) {
            print("[INFO] " + ex.getMessage());
        } catch (Exception ex) { // catch all errors, prevent total crash
            if (!wasLoggedIn || isLoggedIn()) { // check if error wasn't caused by a sudden logoff
                print("[ERROR] The scraper ran into a critical error; aborting...");
                print("Saving error reports...");
                sendErrorReports(ex);
            } else {
                print("[INFO] You have been forcibly logged off of this profile.");
                quit();
                return;
            }
        }

        // logout
        try {
            if (wasLoggedIn) {
                print("Logging out...");
                logout();
            }
        } catch (Exception e) {
            print("[ERROR] Logout failed.");
            sendErrorReports(e);
        }

        // terminate this scrape
        quit();
    }

    private void sendErrorReports(Exception ex) {
        // send the error report to the server
        try {
            createErrorReport(serverPath + "errors/", ex);
            print("Server copy of error report successfully saved.");
        } catch (IOException e) {
            print("[INFO] Unable to save error report in the server.");
            try {
                createErrorReport(localPath + "Error Report ", ex);
                print("[INFO] Please send the file named 'Error Report' to " + WebScraper.MY_EMAIL);
            } catch (IOException exc) {
                print("[ERROR] Unable to save local copy of error report.");
                print("[INFO] Please send the text of this transcript to " + WebScraper.MY_EMAIL);
            }
        }
    }

    /**
     * creates a new error report at the given file location
     *
     * @param path the address of the folder it will be saved
     * @param ex the error to be reported
     */
    private void createErrorReport(String path, Exception ex) throws IOException {
        out = newLog(path);
        if (tw != null) {
            tw.getTextArea().write(out); // write out transcript
        }
        ex.printStackTrace(out);         // write out stack trace
        out.flush();
        out.close();
    }

    /**
     * makes a log file for either logs or errors. avoids overwriting
     */
    private PrintWriter newLog(String path) throws IOException {
        String fileName = path + WebScraper.VERSION + " " + timestamp();
        // gives it a new name if it already exists
        int type = 0;
        while (new java.io.File(fileName + ".txt").exists()) {
            if (type != 0) {
                fileName = fileName.substring(0, fileName.length() - 4);
            }
            type++;
            fileName += " (" + type + ")";
        }

        return new PrintWriter(new FileWriter(fileName + ".txt"));
    }

    /**
     * quits the current scrape, sending all output to the server
     */
    public void quit() {
        // shut down the objects
        if (out != null) {
            out.close();
        }
        driver.quit();
        tw.close();

        // send the results to the server
        try {
            for (String fileName : outFiles) {
                java.nio.file.Files.copy(
                        java.nio.file.Paths.get(localPath + "Extracted Candidates/" + fileName),
                        java.nio.file.Paths.get(serverPath + "results/" + fileName),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            print("[INFO] Unable to send extracted candidates to the server.");
        }

        // send the transcript to the server
        try {
            String path = serverPath + "logs/";
            out = newLog(path);
            if (tw != null) {
                tw.getTextArea().write(out); // write out transcript
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            print("[INFO] Unable to send the transcript to the server.");
        }

        // that's all, folks!
        print("Scrape completed.");
    }

    /**
     * quits the scrape, outputs the info to the server, and closes the
     * transcript window
     */
    public void quitScrape() {
        quit();
        tw.dispose();
    }

    /**
     * establishes a new output stream and a new output file
     *
     * @param searchName the name of the search
     * @return the path and name of the file
     */
    private String newOutFile(String searchName) throws IOException {
        // replace all illegal characters in the file name
        searchName = searchName.replaceAll("[\\\\/:*?\"<>|]", "_");

        // construct a name for the file
        String fileName = searchName + "-" + siteName + " " + timestamp() + ".txt";

        // create the file
        out = new PrintWriter(new FileWriter(localPath + "Extracted Candidates/" + fileName));
        return fileName;
    }

    /**
     * gives a timestamp in the format: YYYY-MM-dd HHmm
     */
    private String timestamp() {
        return new java.text.SimpleDateFormat("YYYY-MM-dd HHmm")
                .format(java.util.Calendar.getInstance().getTime());
    }

    /**
     * prints candidate info into the current outfile, flushes the output
     * stream, and resets all variables
     *
     * @param name
     * @param email
     * @param phone
     * @param lastUpdated
     */
    public void outputCandidate(String name, String email, String phone,
            String lastUpdated) {
        out.println(name + "," + takeFirstNameFrom(name) + ","
                + takeLastNameFrom(name) + "," + email + "," + toPhone(phone)
                + "," + siteName + "," + lastUpdated);
        out.flush();
    }

    /**
     * prints candidate info into the current outfile, flushes the output
     * stream, and resets all variables
     *
     * @param name
     * @param email
     * @param phone
     * @param lastUpdated
     * @param url
     */
    public void outputCandidate(String name, String email, String phone,
            String lastUpdated, String url) {
        out.println(name + "," + takeFirstNameFrom(name) + ","
                + takeLastNameFrom(name) + "," + email + "," + toPhone(phone)
                + "," + siteName + "," + lastUpdated + "," + url);
        out.flush();
    }

    /**
     * removes all formatting from a phone number
     */
    private String toPhone(String phone) {
        String result = "";

        // include only digits
        boolean allDigitsSame = true;
        for (char c : phone.toCharArray()) {
            if (Character.isDigit(c)) {
                if (allDigitsSame == true && result.length() > 0
                        && result.charAt(result.length() - 1) != c) {
                    allDigitsSame = false;
                }
                result += c;
            }
        }

        // include only last 10 digits
        if (result.length() > 10) {
            result = result.substring(result.length() - 10);
        }

        // remove phone number that is obviously fake
        if (allDigitsSame) {
            result = "";
        }

        return result;
    }

    /**
     * finds the first name of a given string with the format "firstname
     * lastname".
     */
    private String takeFirstNameFrom(String name) {
        String f = "";

        // remove all numbers from the name
        for (Character c : name.toCharArray()) {
            if (!Character.isDigit(c)) {
                f += c;
            }
        }

        // take name between parentheses or quotes
        if (f.lastIndexOf("\"") != f.indexOf("\"")) {
            f = name.substring(f.indexOf("\"") + 1, f.lastIndexOf("\""));
        } else if (f.contains("(") && f.contains(")")) {
            f = name.substring(f.indexOf("(") + 1, f.lastIndexOf(")"));
        }

        // first name (before the space)
        if (f.contains(" ")) {
            f = name.substring(0, f.indexOf(" "));
        }

        // return if name is already mixed case
        if (!f.equals(f.toLowerCase()) && !f.equals(f.toUpperCase())) {
            return f;
        }

        // capitalize all other two letter names
        if (f.length() == 2) {
            return f.toUpperCase();
        }

        // make all others mixed case
        f = f.toLowerCase();
        f = f.substring(0, 1).toUpperCase() + f.substring(1);
        return f;
    }

    private String takeLastNameFrom(String name) {
        String f = "";

        // remove all numbers from the name
        for (Character c : name.toCharArray()) {
            if (!Character.isDigit(c)) {
                f += c;
            }
        }

        // last name (after the last space)
        if (f.contains(" ")) {
            f = f.substring(f.lastIndexOf(" ") + 1);
        }

        // return if name is already mixed case
        if (!f.equals(f.toLowerCase()) && !f.equals(f.toUpperCase())) {
            return f;
        }

        // make all others mixed case
        f = f.toLowerCase();
        f = f.substring(0, 1).toUpperCase() + f.substring(1);
        return f;
    }

    /**
     * prints to the output window
     *
     * @param str the main text to be displayed
     */
    public void print(String str) {
        tw.printWithTimestamp(str);
    }

    /**
     * pauses script for random amount of time in seconds (lower limit, upper
     * limit)
     *
     * @param min lower bound, in seconds
     * @param max upper bound, in seconds
     */
    public void wait(int min, int max) {
        min = min * 1000;
        max = max * 1000;
        int t = min + (int) (Math.random() * (max - min));
        try {
            Thread.sleep(t);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * pauses script for a set amount of time
     *
     * @param seconds the time waiting in seconds
     */
    public void wait(double seconds) {
        try {
            long t = (long) (seconds * 1000);
            Thread.sleep(t);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * scrapes the info from a single target
     *
     * @param targetName name of the current search/folder/position
     * @throws ScraperException if there is a user error
     */
    public abstract void scrape(String targetName) throws ScraperException;

    /**
     * logs into a resume site with the given credentials, returning true if it
     * is successful
     *
     * @param username the username for login attempt
     * @param password the password for login attempt
     * @return true if the login was successful, false otherwise
     */
    public abstract boolean login(String username, String password);

    /**
     * checks if currently logged in, and not booted
     *
     * @return true if logged in, false otherwise
     */
    public abstract boolean isLoggedIn();

    /**
     * logs out of a resume database site
     *
     * @throws java.lang.Exception
     */
    public abstract void logout() throws Exception;

    /**
     * retrieves the names of all saved searches etc.
     *
     * @return the list of the saved searches on this site
     * @throws ScraperException if there is a user error
     */
    public abstract String[] getTargets() throws ScraperException;

    /**
     * navigates to the appropriate saved searches page
     */
    public abstract void goToSearches();
}
