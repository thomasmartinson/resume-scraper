package scraper;

/**
 * Thrown when an element takes to long to load. Used to avoid infinite loops.
 */
public class TimeOutException extends ScraperException {

    private static final String DEFAULT_MESSAGE = "Page loaded improperly.";

    public TimeOutException() {
        super(DEFAULT_MESSAGE);
    }
}
