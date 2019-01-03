package scraper;

import java.io.File;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * The main graphic user interface for the WebScraper tool.
 */
public class ScraperUI extends javax.swing.JFrame {

    // instance variables
    private boolean isClosing;
    private Scraper scraper;
    private final ArrayList<Scraper> activeScrapers;
    private final ArrayList<Thread> activeThreads;

    /**
     * Creates new form ScraperUI
     */
    public ScraperUI() {
        super("Resume Database Scraper " + WebScraper.VERSION);
        initComponents();
        initCustom();
        activeScrapers = new ArrayList();
        activeThreads = new ArrayList();
        isClosing = false;
    }

    private void initCustom() {
        byMeSetVisible(false);
        setLocation(WebScraper.center(getSize()));
        this.setIconImage(new ImageIcon(getClass().getResource("comb_grey.png")).getImage());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        siteComboBox = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        username = new javax.swing.JTextField();
        scrapeButton = new javax.swing.JButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        cutoffField = new javax.swing.JTextField();
        password = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jRadioButton4 = new javax.swing.JRadioButton();
        byMe = new javax.swing.JRadioButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    formWindowClosing(evt);
                }
            });

        siteComboBox.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        siteComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Dice Search", "Monster Search", "Dice Search (beta)" }));
        siteComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    siteComboBoxActionPerformed(evt);
                }
            });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel3.setText("Password:");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Target type:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel2.setText("Username:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel4.setText("days");

        username.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        username.setText("@precisionsystems.com");

        scrapeButton.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        scrapeButton.setText("Scrape");
        scrapeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    scrapeButtonActionPerformed(evt);
                }
            });

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Extract all candidates");
        jRadioButton1.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    jRadioButton1StateChanged(evt);
                }
            });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jRadioButton2.setText("Extract no candidates viewed in the past");

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jRadioButton3.setText("Extract no candidates viewed in the past");

        cutoffField.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N

        password.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel5.setText("by");

        buttonGroup2.add(jRadioButton4);
        jRadioButton4.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jRadioButton4.setText("anyone");

        buttonGroup2.add(byMe);
        byMe.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        byMe.setSelected(true);
        byMe.setText("me");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addGap(40, 40, 40))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel3)
                                    .addGap(47, 47, 47)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(24, 24, 24)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(username, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                            .addComponent(siteComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(password)))
                    .addComponent(jRadioButton1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jRadioButton3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jRadioButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cutoffField, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(204, 204, 204)
                .addComponent(scrapeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(byMe)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton4)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(siteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton2)
                    .addComponent(cutoffField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton3)
                .addGap(3, 3, 3)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(byMe)
                    .addComponent(jRadioButton4)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addComponent(scrapeButton)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jMenuBar1.setBorder(null);

        jMenu1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jMenu1.setText("Help");
        jMenu1.setAutoscrolls(true);

        jMenuItem1.setText("How do I exclude candidates that have been viewed before? (Dice)");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jMenuItem1ActionPerformed(evt);
                }
            });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("How do I exclude candidates that have been viewed before? (Monster)");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jMenuItem2ActionPerformed(evt);
                }
            });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("Can I run multiple scrapes simultaneously?");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jMenuItem3ActionPerformed(evt);
                }
            });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setText("How do I select multiple saved searches?");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jMenuItem4ActionPerformed(evt);
                }
            });
        jMenu1.add(jMenuItem4);

        jMenuItem5.setText("How do I update the scraper?");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jMenuItem5ActionPerformed(evt);
                }
            });
        jMenu1.add(jMenuItem5);

        jMenuItem6.setText("Where can I find the extracted candidates' info?");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jMenuItem6ActionPerformed(evt);
                }
            });
        jMenu1.add(jMenuItem6);

        jMenuItem7.setText("Do you know the way?");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jMenuItem7ActionPerformed(evt);
                }
            });
        jMenu1.add(jMenuItem7);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void scrapeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scrapeButtonActionPerformed
        // make an output folder if it does not already exist
        File folder = new File(WebScraper.LOCAL_PATH);
        if (!folder.isDirectory()) {
            folder.mkdir();
        }
        folder = new File(WebScraper.LOCAL_PATH + "Extracted Candidates");
        if (!folder.isDirectory()) {
            folder.mkdir();
        }

        // take in all info from GUI
        String user = username.getText();
        String pass = password.getText();
        if (user == null || user.length() == 0
                || pass == null || pass.length() == 0) {
            errorMessage("Invalid input, field(s) left blank.");
            return;
        }
        String targetType = (String) siteComboBox.getSelectedItem();
        String siteName = targetType.substring(0, targetType.indexOf(" "));
        if (targetType.contains("beta")) {
            siteName += "Beta";
        }
        boolean isByMe = byMe.isSelected();
        String byMeMessage = "";
        if (siteName.contains("Dice")) {
            if (isByMe) {
                byMeMessage = " by me";
            } else {
                byMeMessage = " by anyone";
            }
        }

        // determine the cutoff from the radio buttons
        int cutoff;
        String cutoffMessage = "Extracting ";
        if (jRadioButton1.isSelected()) {
            cutoff = -1;
            cutoffMessage += "all candidates.";
        } else if (jRadioButton2.isSelected() && targetType.contains("Dice")) {
            String entry = cutoffField.getText();
            try {
                cutoff = Integer.valueOf(entry);
                cutoffMessage += "no candidates viewed in the past " + cutoff + " days" + byMeMessage + ".";
            } catch (NumberFormatException ex) {
                errorMessage("'" + entry + "' is not a valid number.");
                return;
            }
        } else { // i.e. if the third button is selected
            cutoff = Integer.MAX_VALUE - 1;
            cutoffMessage += "no candidates viewed in the past" + byMeMessage + ".";
        }

        // construct the header for the transcript window
        String shortUser = user;
        if (user.contains("@")) {
            shortUser = user.substring(0, user.indexOf("@"));
        }
        String header = siteName + " " + shortUser;

        // create new transcript window
        TranscriptWindow tw = new TranscriptWindow(header);
        tw.print(header);
        tw.print("WebScraper " + WebScraper.VERSION);
        tw.print(cutoffMessage);
        tw.setVisible(true);

        // create driver with desired options
        ScraperDriver driver = new ScraperDriver();

        // create the scraper
        if (targetType.equals("Dice Search")) {
            scraper = new DiceSearch(driver, tw);
        } else if (targetType.equals("Monster Search")) {
            scraper = new MonsterSearch(driver, tw);
        } else { // i.e. if the target type is the Dice Beta
            scraper = new DiceSearchBeta(driver, tw);
        }

        // assign instance variables to scraper
        scraper.cutoff = cutoff;
        scraper.byMe = isByMe;
        scraper.siteName = siteName;

        // run the scrape
        activeScrapers.add(scraper);
        Runnable runnable = new ScrapeThread();
        Thread thread = new Thread(runnable);
        activeThreads.add(thread);
        thread.start();
    }//GEN-LAST:event_scrapeButtonActionPerformed

    class ScrapeThread implements Runnable {

        @Override
        public void run() {
            Scraper thisScraper = scraper;
            thisScraper.scrape(username.getText(), password.getText());
            activeScrapers.remove(thisScraper);
        }
    }

    private void siteComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_siteComboBoxActionPerformed
        String selected = (String) siteComboBox.getSelectedItem();

        // make the relevant parts visible/invisible
        if (selected.equals("Monster Search")) {
            if (username.getText().equals("@precisionsystems.com")) {
                username.setText("");
            }
            jRadioButton2.setVisible(false);
            jLabel4.setVisible(false);
            cutoffField.setVisible(false);
            byMeSetVisible(false);
            // reselect if button 2 is selected
            if (jRadioButton2.isSelected()) {
                jRadioButton1.setSelected(true);
            }
        } else {
            if (username.getText().equals("")) {
                username.setText("@precisionsystems.com");
            }
            jRadioButton2.setVisible(true);
            cutoffField.setVisible(true);
            jLabel4.setVisible(true);
            if (!jRadioButton1.isSelected()) {
                byMeSetVisible(true);
            }
        }
    }//GEN-LAST:event_siteComboBoxActionPerformed

    private void jRadioButton1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jRadioButton1StateChanged
        String selected = (String) siteComboBox.getSelectedItem();

        // make the byMe options invisible
        if (jRadioButton1.isSelected()
                || !selected.contains("Dice")) {
            byMeSetVisible(false);
        } else { // make it visible
            byMeSetVisible(true);
        }
    }//GEN-LAST:event_jRadioButton1StateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // make sure only occurs once
        if (isClosing) {
            return;
        }
        isClosing = true;
        for (Thread thread : activeThreads) {
            thread.interrupt();
        }
        while (!activeScrapers.isEmpty()) {
            Scraper victim = activeScrapers.remove(0);
            victim.print("[INFO] User forcibly stopped this scrape.");
            victim.quitScrape();
        }
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // How do I exclude candidates that have been viewed before? (Dice)
        helpMessage("If you select \"Extract all candidates,\" the "
                + "application will extract contact information for every "
                + "candidate who appears in the search.\n"
                + "If you select \"Extract no candidates viewed in the past "
                + "___ days,\" you can enter a number X, and the scraper will "
                + "not extract any candidate who has been viewed X or fewer "
                + "days ago. E.G. If \"0\" is entered, the scraper will exclude "
                + "any candidate that has been viewed today, if "
                + " \"1\" is entered, it will exclude any candidate that has been viewed "
                + "today or yesterday, and so forth.\n"
                + "If you select \"Extract no candidates viewed in the past,\" "
                + "the scraper will not extract any candidate who has been viewed in "
                + "the past.\n"
                + "Finally, if you selected the second or third option, "
                + "two more suboptions appear. If you select \"by me,\" "
                + "candidates will be excluded based on when they were last "
                + "viewed by the given login. If you select \"by anyone,\" "
                + "candidates will be excluded based on when they were last "
                + "viewed by you or anyone else at Precision.\n"
                + "N.B. In the current version of the Dice Search Beta, no "
                + "last-viewed data is shared among the Precision logins, so "
                + "selecting \"anyone\" or \"me\" will have the same result. "
                + "If this changes, the scraper should function as normal.",
                jMenuItem1.getText());
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // How do I exclude candidates that have been viewed before? (Monster)
        helpMessage("If you select the option \"Extract all candidates,\" the "
                + "application will extract contact information for every "
                + "candidate who appears in the search.\n"
                + "If you select \"Extract no candidates viewed in the past,\" "
                + "it will NOT extract ANY candidate who is marked as "
                + "\"viewed.\"\n"
                + "N.B. Candidates are marked as \"viewed\" when a candidate's "
                + "resume profile is opened by the user (or the scraper). "
                + "Monster.com keeps track of only the last 1,000 viewed "
                + "candidates, so the 1,001st most recent person to have been "
                + "viewed is always unmarked. Each of Precision's logins have "
                + "different candidates marked; no last-viewed "
                + "information is shared among them.",
                jMenuItem2.getText());
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // Can I run multiple scrapes simultaneously?
        helpMessage("Yes. You can press the \"Scrape\" button while another "
                + "scrape is running. Just be sure that you are using a login "
                + "that is not currently in use.",
                jMenuItem3.getText());
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // How do I select multiple saved searches?
        helpMessage("When the window saying \"Select from "
                + "the list\" pops up, you can select multiple searches by "
                + "holding down CTRL or SHIFT while you click. The scraper "
                + "will run through the searches in the order in which they "
                + "were clicked.",
                jMenuItem4.getText());
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // How do I update the scraper?
        helpMessage("Go to the server in the file explorer -> Precision -> "
                + "Corporate -> Software -> WebScraper\n Double-click the file "
                + "named \"INSTALL.bat\", follow the on-screen instructions, "
                + "and you're good to go.",
                jMenuItem5.getText());
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // Where to find the extracted candidates
        helpMessage("Go to file explorer -> Documents -> WebScraper -> "
                + "Extracted Candidates\n The candidates are grouped by search "
                + "name.",
                jMenuItem6.getText());
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        // jokes
        helpMessage("<b>[furious clucking ensues]</b>",
                jMenuItem7.getText());
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void errorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void helpMessage(String answer, String question) {
        // insert automatic line-breaks
        StringBuilder msg = new StringBuilder();
        boolean followsLineBreak = true;
        for (char c : answer.toCharArray()) {
            if (followsLineBreak) {
                msg.append("<html><body><p style='width: 300px;'>");
                followsLineBreak = false;
            }
            if (c == '\n') {
                msg.append("</p></body></html>\n");
                followsLineBreak = true;
            }
            msg.append(c);
        }
        msg.append("</p></body></html>");

        JOptionPane.showMessageDialog(this, msg, question, JOptionPane.PLAIN_MESSAGE);
    }

    private void byMeSetVisible(boolean b) {
        jRadioButton4.setVisible(b);
        byMe.setVisible(b);
        jLabel5.setVisible(b);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JRadioButton byMe;
    private javax.swing.JTextField cutoffField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JTextField password;
    private javax.swing.JButton scrapeButton;
    private javax.swing.JComboBox<String> siteComboBox;
    private javax.swing.JTextField username;
    // End of variables declaration//GEN-END:variables
}