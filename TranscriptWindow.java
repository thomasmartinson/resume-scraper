package scraper;

import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

/**
 * A window that displays the transcript for the current scrape.
 */
public class TranscriptWindow extends javax.swing.JFrame {

    /**
     * Creates new form TranscriptWindow
     *
     * @param header the header to be shown on the window
     */
    public TranscriptWindow(String header) {
        super(header);
        initComponents();
        initCustom();
    }

    /**
     * Creates new form Transcript Window
     */
    public TranscriptWindow() {
        super();
        initComponents();
        initCustom();
    }

    private void initCustom() {
        this.setIconImage(new ImageIcon(getClass().getResource("comb_grey.png")).getImage());
        setLocation(WebScraper.center(getSize()));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        textArea.setEditable(false);
        textArea.setColumns(20);
        textArea.setFont(new java.awt.Font("Monospaced", 0, 24)); // NOI18N
        textArea.setRows(5);
        jScrollPane1.setViewportView(textArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 867, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * shows text in the window, marked with a timestamp
     *
     * @param str the string to be displayed with a timestamp
     */
    public void printWithTimestamp(String str) {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        print(timestamp + " - " + str);
    }

    /**
     * shows text in the window
     *
     * @param str the text to be displayed
     */
    public void print(String str) {
        // redirects data to the text area
        textArea.append(str + "\n");
        // scrolls the text area to the end of data
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    /**
     * allows the output window to close when the close button is pressed
     */
    public void close() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     * returns the main text area
     *
     * @return a reference to the main text area
     */
    public JTextArea getTextArea() {
        return textArea;
    }

    /**
     * Shows an option pane which has as its contents the given closeable
     * object. The pane closes almost immediately if certain criteria are met,
     * as defined by the closeable object.
     *
     * @param contents the contents of the otherwise blank JOptionPane
     * @param title the title of the JOptionPane
     */
    public void showOptionPane(Closeable contents, String title) {
        JOptionPane optionPane = new JOptionPane(contents,
                JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null,
                new Object[]{}, null);

        JDialog dialog = new JDialog(this, title, true);
        dialog.setContentPane(optionPane);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        // dispose of dialog after .5 seconds if the closeable can be closed
        Timer timer = new Timer(500, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (contents.canBeClosed()) {
                    dialog.dispose();
                }
            }
        });

        //start timer to close JDialog as dialog modal we must start the timer before its visible
        timer.start();
        dialog.setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea textArea;
    // End of variables declaration//GEN-END:variables
}
