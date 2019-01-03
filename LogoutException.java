package scraper;

/**
 * Thrown when the user has been forcibly logged out.
 */
public class LogoutException extends ScraperException {

    private static final String DEFAULT_MESSAGE = "You are not logged into this profile.";

    public LogoutException() {
        super(DEFAULT_MESSAGE);
    }
}
