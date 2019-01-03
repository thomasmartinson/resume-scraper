package scraper;

/**
 * Thrown when there are no saved searches on a profile.
 */
public class NoSavesException extends ScraperException {

    private static final String DEFAULT_MESSAGE = "No saved searches were found on this profile.";

    public NoSavesException() {
        super(DEFAULT_MESSAGE);
    }
}
