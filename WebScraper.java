package scraper;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * The main class. Stores global variables and runs the show.
 *
 * @author Tom Martinson | tmm2@princeton.edu
 * @version 1.4.2
 */
public class WebScraper {

    // CHANGE BEFORE DEPLOYING
    static final String VERSION = "1.4.2";
    static final boolean TESTING = true;
    static final boolean HEADLESS = false;

    private static final Dimension SCREEN = Toolkit.getDefaultToolkit().getScreenSize();
    private static final int SCREEN_WIDTH = SCREEN.width;
    static final int SCREEN_HEIGHT = SCREEN.height;
    static final String AUTHOR = "Tom Martinson";
    static final String MY_EMAIL = "tmm2@princeton.edu";
    static final String LOCAL_PATH = System.getenv("USERPROFILE") + "/Documents/WebScraper/";
    static final String SERVER_PATH = "//server1/c/Precision/Corporate/Software/WebScraper/archive/";
    static final String DRIVER_PATH = "driver/chromedriver.exe";

    public static void main(String[] args) {
        new WebScraper().launch();
    }

    private void launch() {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("bloop");
        }
        //</editor-fold>
        
        // extract the chromedriver
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("scraper/chromedriver.exe");
        File f = new File("Driver");
        if (!f.exists()) {
            f.mkdirs();
        }
        File chromeDriver = new File(DRIVER_PATH);
        if (!chromeDriver.exists()) {
            try {
                chromeDriver.createNewFile();
                org.apache.commons.io.FileUtils.copyURLToFile(resource, chromeDriver);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        // run the thing
        ScraperUI scraperUI = new ScraperUI();
        scraperUI.setVisible(true);
    }

    public static Point center(Dimension dim) {
        int x = SCREEN_WIDTH / 2 - dim.width / 2;
        int y = SCREEN_HEIGHT / 2 - dim.height / 2;
        return new Point(x, y);
    }

    /**
     * Method used for unit testing.
     *
     * @throws java.lang.Exception
     */
    public static void test() throws Exception {
        new WebScraper().launch();
        new ScraperDriver();
    }
}
