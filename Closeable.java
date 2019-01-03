package scraper;

/**
 * Interface for anything that can be closed.
 */
public interface Closeable {

    /**
     * whether the dev want the user to be allowed to close the thing
     *
     * @return true if it's cool with the dev, false if not
     */
    public boolean canBeClosed();
}
