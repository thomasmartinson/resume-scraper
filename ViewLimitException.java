package scraper;

/**
 * Thrown when the user's view limit has been reached.
 */
public class ViewLimitException extends ScraperException {

    private static final String DEFAULT_MESSAGE = "View limit on this profile has been reached.";

    public ViewLimitException() {
        super(DEFAULT_MESSAGE);
    }
}
