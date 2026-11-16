import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.Map;

public class UserPortal extends JFrame implements ActionListener {
    Backend backend;
    Backend.UserSession currentUser;

    Color backgroundColor = new Color(204, 229, 255);
    Color headerColor = new Color(255, 80, 80);
    Color sectionColor = new Color(0, 51, 102);
    Color blueColor = new Color(0, 102, 204);
    Color greenColor = new Color(0, 153, 76);
    Color orangeColor = new Color(255, 153, 51);
    Color greyColor = new Color(128, 128, 128);
    Color redColor = new Color(204, 0, 0);

    JLabel jLabelHeading;
    JLabel jLabelWelcome;
    JLabel jLabelStatistics;
    JButton jButtonRefreshStatistics;
    JButton jButtonReportCybercrime;
    JButton jButtonMyReports;
    JButton jButtonTrackComplaint;
    JButton jButtonSubmitEvidence;
    JButton jButtonNotifications;
    JButton jButtonMyProfile;
    JButton jButtonLogout;

    public UserPortal(Backend backend, Backend.UserSession user) {
        this.backend = backend;
        this.currentUser = user;

        setTitle("Cyber Forensic Portal - User Menu");
        setSize(750, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(backgroundColor);

        jLabelHeading = new JLabel("CYBER FORENSIC PORTAL - USER MENU", SwingConstants.CENTER);
        jLabelHeading.setBounds(0, 0, 750, 60);
        jLabelHeading.setFont(new Font("Arial", Font.BOLD, 24));
        jLabelHeading.setOpaque(true);
        jLabelHeading.setBackground(headerColor);
        jLabelHeading.setForeground(Color.BLACK);
        add(jLabelHeading);

        String name = currentUser.username;
        if (currentUser.fullName != null && !currentUser.fullName.isBlank()) {
            name = currentUser.fullName;
        }
        jLabelWelcome = new JLabel("Welcome, " + name);
        jLabelWelcome.setBounds(40, 70, 500, 25);
        jLabelWelcome.setFont(new Font("Arial", Font.BOLD, 18));
        add(jLabelWelcome);

        jLabelStatistics = new JLabel("Total Reports : 0     Pending : 0     Under Investigation : 0     Resolved : 0");
        jLabelStatistics.setBounds(40, 100, 560, 30);
        jLabelStatistics.setFont(new Font("Arial", Font.BOLD, 14));
        add(jLabelStatistics);

        jButtonRefreshStatistics = new JButton("REFRESH");
        jButtonRefreshStatistics.setBounds(590, 100, 110, 30);
        jButtonRefreshStatistics.setFont(new Font("Arial", Font.BOLD, 13));
        jButtonRefreshStatistics.setBackground(orangeColor);
        jButtonRefreshStatistics.setForeground(Color.BLACK);
        jButtonRefreshStatistics.addActionListener(this);
        add(jButtonRefreshStatistics);

        jButtonReportCybercrime = new JButton("REPORT CYBERCRIME");
        jButtonReportCybercrime.setBounds(40, 145, 320, 70);
        jButtonReportCybercrime.setFont(new Font("Arial", Font.BOLD, 17));
        jButtonReportCybercrime.setBackground(blueColor);
        jButtonReportCybercrime.setForeground(Color.WHITE);
        jButtonReportCybercrime.addActionListener(this);
        add(jButtonReportCybercrime);

        jButtonMyReports = new JButton("MY REPORTS");
        jButtonMyReports.setBounds(380, 145, 320, 70);
        jButtonMyReports.setFont(new Font("Arial", Font.BOLD, 17));
        jButtonMyReports.setBackground(blueColor);
        jButtonMyReports.setForeground(Color.WHITE);
        jButtonMyReports.addActionListener(this);
        add(jButtonMyReports);

        jButtonTrackComplaint = new JButton("TRACK COMPLAINT");
        jButtonTrackComplaint.setBounds(40, 230, 320, 70);
        jButtonTrackComplaint.setFont(new Font("Arial", Font.BOLD, 17));
        jButtonTrackComplaint.setBackground(blueColor);
        jButtonTrackComplaint.setForeground(Color.WHITE);
        jButtonTrackComplaint.addActionListener(this);
        add(jButtonTrackComplaint);

        jButtonSubmitEvidence = new JButton("SUBMIT EVIDENCE");
        jButtonSubmitEvidence.setBounds(380, 230, 320, 70);
        jButtonSubmitEvidence.setFont(new Font("Arial", Font.BOLD, 17));
        jButtonSubmitEvidence.setBackground(blueColor);
        jButtonSubmitEvidence.setForeground(Color.WHITE);
        jButtonSubmitEvidence.addActionListener(this);
        add(jButtonSubmitEvidence);

        jButtonNotifications = new JButton("NOTIFICATIONS");
        jButtonNotifications.setBounds(40, 315, 320, 70);
        jButtonNotifications.setFont(new Font("Arial", Font.BOLD, 17));
        jButtonNotifications.setBackground(blueColor);
        jButtonNotifications.setForeground(Color.WHITE);
        jButtonNotifications.addActionListener(this);
        add(jButtonNotifications);

        jButtonMyProfile = new JButton("MY PROFILE");
        jButtonMyProfile.setBounds(380, 315, 320, 70);
        jButtonMyProfile.setFont(new Font("Arial", Font.BOLD, 17));
        jButtonMyProfile.setBackground(blueColor);
        jButtonMyProfile.setForeground(Color.WHITE);
        jButtonMyProfile.addActionListener(this);
        add(jButtonMyProfile);

        jButtonLogout = new JButton("LOGOUT");
        jButtonLogout.setBounds(270, 405, 200, 50);
        jButtonLogout.setFont(new Font("Arial", Font.BOLD, 17));
        jButtonLogout.setBackground(redColor);
        jButtonLogout.setForeground(Color.WHITE);
        jButtonLogout.addActionListener(this);
        add(jButtonLogout);


        refreshStatistics();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jButtonRefreshStatistics) {
            refreshStatistics();
        }
        if (e.getSource() == jButtonReportCybercrime) {
            openReportWindow();
        }
        if (e.getSource() == jButtonMyReports) {
            openMyReportsWindow();
        }
        if (e.getSource() == jButtonTrackComplaint) {
            openTrackWindow();
        }
        if (e.getSource() == jButtonSubmitEvidence) {
            openEvidenceWindow();
        }
        if (e.getSource() == jButtonNotifications) {
            openNotificationsWindow();
        }
        if (e.getSource() == jButtonMyProfile) {
            openProfileWindow();
        }
        if (e.getSource() == jButtonLogout) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                for (Window window : Window.getWindows()) {
                    window.dispose();
                }
                new Frontend().setVisible(true);
            }
        }
    }

    void refreshStatistics() {
        try {
            Map<String, Integer> stats = backend.getUserStats(currentUser.id);
            jLabelStatistics.setText("Total Reports : " + stats.getOrDefault("total", 0)
                + "     Pending : " + stats.getOrDefault("pending", 0)
                + "     Under Investigation : " + stats.getOrDefault("investigating", 0)
                + "     Resolved : " + stats.getOrDefault("resolved", 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    JLabel makeHeading(String text, int width) {
        JLabel jLabel = new JLabel(text, SwingConstants.CENTER);
        jLabel.setBounds(0, 0, width, 50);
        jLabel.setFont(new Font("Arial", Font.BOLD, 22));
        jLabel.setOpaque(true);
        jLabel.setBackground(headerColor);
        jLabel.setForeground(Color.BLACK);
        return jLabel;
    }

    JLabel makeLabel(String text, int x, int y, int width) {
        JLabel jLabel = new JLabel(text);
        jLabel.setBounds(x, y, width, 30);
        jLabel.setFont(new Font("Arial", Font.BOLD, 14));
        return jLabel;
    }

    JButton makeButton(String text, int x, int y, int width, int height, Color color) {
        JButton jButton = new JButton(text);
        jButton.setBounds(x, y, width, height);
        jButton.setFont(new Font("Arial", Font.BOLD, 15));
        jButton.setBackground(color);
        if (color == orangeColor) {
            jButton.setForeground(Color.BLACK);
        } else {
            jButton.setForeground(Color.WHITE);
        }
        return jButton;
    }

    void openReportWindow() {
        JFrame jFrameReport = new JFrame("Report Cybercrime");
        jFrameReport.setSize(900, 720);
        jFrameReport.setLocationRelativeTo(this);
        jFrameReport.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrameReport.setLayout(null);
        jFrameReport.getContentPane().setBackground(backgroundColor);

        jFrameReport.add(makeHeading("REPORT A CYBERCRIME", 900));

        JLabel jLabelVictimSection = new JLabel("Victim and Incident Details");
        jLabelVictimSection.setBounds(30, 60, 400, 25);
        jLabelVictimSection.setFont(new Font("Arial", Font.BOLD, 16));
        jLabelVictimSection.setForeground(sectionColor);
        jFrameReport.add(jLabelVictimSection);

        JLabel jLabelReportIdNote = new JLabel("(Report ID will be generated after submission)");
        jLabelReportIdNote.setBounds(470, 60, 400, 25);
        jLabelReportIdNote.setFont(new Font("Arial", Font.ITALIC, 13));
        jFrameReport.add(jLabelReportIdNote);

        jFrameReport.add(makeLabel("Victim Name * :", 30, 95, 160));
        JTextField jTextFieldVictimName = new JTextField(currentUser.fullName != null ? currentUser.fullName : "");
        jTextFieldVictimName.setBounds(190, 95, 230, 30);
        jFrameReport.add(jTextFieldVictimName);

        jFrameReport.add(makeLabel("Email Address * :", 460, 95, 160));
        JTextField jTextFieldEmail = new JTextField(currentUser.email != null ? currentUser.email : "");
        jTextFieldEmail.setBounds(620, 95, 230, 30);
        jFrameReport.add(jTextFieldEmail);

        jFrameReport.add(makeLabel("Mobile Number * :", 30, 135, 160));
        JTextField jTextFieldMobile = new JTextField(currentUser.mobileNumber != null ? currentUser.mobileNumber : "");
        jTextFieldMobile.setBounds(190, 135, 230, 30);
        jFrameReport.add(jTextFieldMobile);

        jFrameReport.add(makeLabel("Crime Category * :", 460, 135, 160));
        JComboBox<String> jComboBoxCategory = new JComboBox<>(new String[]{
            "Financial Fraud", "UPI Fraud", "Online Banking Fraud", "Cryptocurrency Fraud",
            "Phishing", "Identity Theft", "Social Media Fraud", "Cyberbullying", "Hacking", "Other"
        });
        jComboBoxCategory.setBounds(620, 135, 230, 30);
        jFrameReport.add(jComboBoxCategory);

        jFrameReport.add(makeLabel("Incident Date * :", 30, 175, 160));
        JTextField jTextFieldIncidentDate = new JTextField("2026-10-01");
        jTextFieldIncidentDate.setBounds(190, 175, 230, 30);
        jFrameReport.add(jTextFieldIncidentDate);

        jFrameReport.add(makeLabel("Incident Location :", 460, 175, 160));
        JTextField jTextFieldLocation = new JTextField();
        jTextFieldLocation.setBounds(620, 175, 230, 30);
        jFrameReport.add(jTextFieldLocation);

        jFrameReport.add(makeLabel("Amount Lost (Rs) :", 30, 215, 160));
        JTextField jTextFieldAmount = new JTextField();
        jTextFieldAmount.setBounds(190, 215, 230, 30);
        jFrameReport.add(jTextFieldAmount);

        JLabel jLabelDateFormat = new JLabel("Date format : YYYY-MM-DD");
        jLabelDateFormat.setBounds(460, 215, 300, 30);
        jLabelDateFormat.setFont(new Font("Arial", Font.ITALIC, 13));
        jFrameReport.add(jLabelDateFormat);

        jFrameReport.add(makeLabel("Incident Description * :", 30, 255, 250));
        JTextArea jTextAreaDescription = new JTextArea();
        jTextAreaDescription.setLineWrap(true);
        jTextAreaDescription.setWrapStyleWord(true);
        JScrollPane jScrollPaneDescription = new JScrollPane(jTextAreaDescription);
        jScrollPaneDescription.setBounds(30, 285, 820, 80);
        jFrameReport.add(jScrollPaneDescription);

        JLabel jLabelSuspectSection = new JLabel("Suspect Details (Optional)");
        jLabelSuspectSection.setBounds(30, 375, 400, 25);
        jLabelSuspectSection.setFont(new Font("Arial", Font.BOLD, 16));
        jLabelSuspectSection.setForeground(sectionColor);
        jFrameReport.add(jLabelSuspectSection);

        jFrameReport.add(makeLabel("Suspect Name :", 30, 405, 160));
        JTextField jTextFieldSuspectName = new JTextField();
        jTextFieldSuspectName.setBounds(190, 405, 230, 30);
        jFrameReport.add(jTextFieldSuspectName);

        jFrameReport.add(makeLabel("Suspect Phone :", 460, 405, 160));
        JTextField jTextFieldSuspectPhone = new JTextField();
        jTextFieldSuspectPhone.setBounds(620, 405, 230, 30);
        jFrameReport.add(jTextFieldSuspectPhone);

        jFrameReport.add(makeLabel("Suspect Email :", 30, 445, 160));
        JTextField jTextFieldSuspectEmail = new JTextField();
        jTextFieldSuspectEmail.setBounds(190, 445, 230, 30);
        jFrameReport.add(jTextFieldSuspectEmail);

        jFrameReport.add(makeLabel("Wallet Address :", 460, 445, 160));
        JTextField jTextFieldWallet = new JTextField();
        jTextFieldWallet.setBounds(620, 445, 230, 30);
        jFrameReport.add(jTextFieldWallet);

        jFrameReport.add(makeLabel("Txn ID / Hash :", 30, 485, 160));
        JTextField jTextFieldTransactionHash = new JTextField();
        jTextFieldTransactionHash.setBounds(190, 485, 230, 30);
        jFrameReport.add(jTextFieldTransactionHash);

        jFrameReport.add(makeLabel("Website / URL :", 460, 485, 160));
        JTextField jTextFieldWebsite = new JTextField();
        jTextFieldWebsite.setBounds(620, 485, 230, 30);
        jFrameReport.add(jTextFieldWebsite);

        jFrameReport.add(makeLabel("Additional Information (Optional) :", 30, 525, 300));
        JTextArea jTextAreaAdditional = new JTextArea();
        jTextAreaAdditional.setLineWrap(true);
        jTextAreaAdditional.setWrapStyleWord(true);
        JScrollPane jScrollPaneAdditional = new JScrollPane(jTextAreaAdditional);
        jScrollPaneAdditional.setBounds(30, 555, 820, 50);
        jFrameReport.add(jScrollPaneAdditional);

        JButton jButtonSubmitReport = makeButton("SUBMIT REPORT", 30, 620, 220, 45, greenColor);
        jFrameReport.add(jButtonSubmitReport);

        JButton jButtonClearForm = makeButton("CLEAR FORM", 270, 620, 180, 45, orangeColor);
        jFrameReport.add(jButtonClearForm);

        JButton jButtonBack = makeButton("BACK", 700, 620, 150, 45, greyColor);
        jFrameReport.add(jButtonBack);

        jButtonClearForm.addActionListener(e -> {
            jTextFieldVictimName.setText(currentUser.fullName != null ? currentUser.fullName : "");
            jTextFieldEmail.setText(currentUser.email != null ? currentUser.email : "");
            jTextFieldMobile.setText(currentUser.mobileNumber != null ? currentUser.mobileNumber : "");
            jTextFieldIncidentDate.setText("2026-10-01");
            jTextFieldLocation.setText("");
            jTextFieldAmount.setText("");
            jTextAreaDescription.setText("");
            jTextFieldSuspectName.setText("");
            jTextFieldSuspectPhone.setText("");
            jTextFieldSuspectEmail.setText("");
            jTextFieldWallet.setText("");
            jTextFieldTransactionHash.setText("");
            jTextFieldWebsite.setText("");
            jTextAreaAdditional.setText("");
        });

        jButtonSubmitReport.addActionListener(e -> {
            try {
                if (jTextFieldVictimName.getText().isBlank() || jTextFieldEmail.getText().isBlank()
                        || jTextFieldMobile.getText().isBlank() || jTextAreaDescription.getText().isBlank()) {
                    throw new Exception("Please fill in all required fields (Victim Name, Email, Mobile, Description).");
                }

                Backend.CybercrimeReport report = new Backend.CybercrimeReport();
                report.userId = currentUser.id;
                report.victimName = jTextFieldVictimName.getText().trim();
                report.email = jTextFieldEmail.getText().trim();
                report.mobileNumber = jTextFieldMobile.getText().trim();
                report.crimeCategory = String.valueOf(jComboBoxCategory.getSelectedItem());
                report.incidentDate = jTextFieldIncidentDate.getText().trim();
                report.location = jTextFieldLocation.getText().trim();
                report.description = jTextAreaDescription.getText().trim();
                report.amountLost = jTextFieldAmount.getText().trim();
                report.suspectName = jTextFieldSuspectName.getText().trim();
                report.suspectPhone = jTextFieldSuspectPhone.getText().trim();
                report.suspectEmail = jTextFieldSuspectEmail.getText().trim();
                report.walletAddress = jTextFieldWallet.getText().trim();
                report.transactionHash = jTextFieldTransactionHash.getText().trim();
                report.websiteUrl = jTextFieldWebsite.getText().trim();
                report.additionalDetails = jTextAreaAdditional.getText().trim();

                String generatedId = backend.createReport(report);

                JOptionPane.showMessageDialog(jFrameReport,
                    "Your cybercrime report has been submitted successfully.\n\n"
                    + "Report ID: " + generatedId + "\n"
                    + "Submission Status: SUBMITTED\n\n"
                    + "You can track this complaint using your Report ID under 'Track Complaint'.",
                    "Report Submitted", JOptionPane.INFORMATION_MESSAGE);

                refreshStatistics();
                jFrameReport.dispose();
                openMyReportsWindow();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(jFrameReport, ex.getMessage(), "Submission Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        jButtonBack.addActionListener(e -> jFrameReport.dispose());

        jFrameReport.setVisible(true);
    }

    void openMyReportsWindow() {
        JFrame jFrameMyReports = new JFrame("My Reports");
        jFrameMyReports.setSize(900, 550);
        jFrameMyReports.setLocationRelativeTo(this);
        jFrameMyReports.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrameMyReports.setLayout(null);
        jFrameMyReports.getContentPane().setBackground(backgroundColor);

        jFrameMyReports.add(makeHeading("MY SUBMITTED REPORTS", 900));

        String[] columns = {"Report ID", "Crime Category", "Incident Date", "Amount Lost", "Status", "Submission Date"};
        DefaultTableModel defaultTableModelMyReports = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable jTableMyReports = new JTable(defaultTableModelMyReports);
        jTableMyReports.setRowHeight(25);
        JScrollPane jScrollPaneMyReports = new JScrollPane(jTableMyReports);
        jScrollPaneMyReports.setBounds(30, 70, 825, 330);
        jFrameMyReports.add(jScrollPaneMyReports);

        JButton jButtonViewDetails = makeButton("VIEW FULL DETAILS", 30, 420, 220, 45, blueColor);
        jFrameMyReports.add(jButtonViewDetails);

        JButton jButtonRefresh = makeButton("REFRESH", 270, 420, 150, 45, orangeColor);
        jFrameMyReports.add(jButtonRefresh);

        JButton jButtonBack = makeButton("BACK", 705, 420, 150, 45, greyColor);
        jFrameMyReports.add(jButtonBack);

        Runnable loadMyReports = () -> {
            try {
                defaultTableModelMyReports.setRowCount(0);
                List<Backend.CybercrimeReport> reports = backend.getReportsByUser(currentUser.id);
                for (Backend.CybercrimeReport report : reports) {
                    String amount = "N/A";
                    if (report.amountLost != null && !report.amountLost.isBlank()) {
                        amount = "Rs. " + report.amountLost;
                    }
                    String date = "N/A";
                    if (report.createdAt != null) {
                        date = report.createdAt.substring(0, Math.min(10, report.createdAt.length()));
                    }
                    defaultTableModelMyReports.addRow(new Object[]{
                        report.reportId, report.crimeCategory, report.incidentDate, amount, report.status, date
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };

        jButtonViewDetails.addActionListener(e -> {
            int selectedRow = jTableMyReports.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(jFrameMyReports, "Please select a report from the table first.", "Select Report", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String reportId = (String) defaultTableModelMyReports.getValueAt(selectedRow, 0);
            try {
                Backend.CybercrimeReport report = backend.getReportById(reportId);
                if (report != null) {
                    showReportDetails(jFrameMyReports, report);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(jFrameMyReports, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        jButtonRefresh.addActionListener(e -> loadMyReports.run());
        jButtonBack.addActionListener(e -> jFrameMyReports.dispose());

        loadMyReports.run();
        jFrameMyReports.setVisible(true);
    }

    void showReportDetails(JFrame parent, Backend.CybercrimeReport report) {
        JDialog jDialogDetails = new JDialog(parent, "Report Details - " + report.reportId, true);
        jDialogDetails.setSize(650, 550);
        jDialogDetails.setLocationRelativeTo(parent);
        jDialogDetails.setLayout(null);
        jDialogDetails.getContentPane().setBackground(backgroundColor);

        String details = "";
        details = details + "====================================================\n";
        details = details + "  CYBERCRIME REPORT DETAILS: " + report.reportId + "\n";
        details = details + "====================================================\n\n";
        details = details + "Report Status         : " + report.status + "\n";
        details = details + "Assigned Unit         : " + report.assignedUnit + "\n";
        details = details + "Submission Date       : " + report.createdAt + "\n\n";
        details = details + "--- VICTIM DETAILS ---\n";
        details = details + "Victim Name           : " + report.victimName + "\n";
        details = details + "Email                 : " + report.email + "\n";
        details = details + "Mobile Number         : " + report.mobileNumber + "\n\n";
        details = details + "--- INCIDENT DETAILS ---\n";
        details = details + "Crime Category        : " + report.crimeCategory + "\n";
        details = details + "Incident Date         : " + report.incidentDate + "\n";
        details = details + "Incident Location     : " + (report.location != null ? report.location : "N/A") + "\n";
        details = details + "Amount Lost           : Rs. " + (report.amountLost != null ? report.amountLost : "0") + "\n\n";
        details = details + "Description:\n" + report.description + "\n\n";
        details = details + "--- SUSPECT / TECHNICAL IDENTIFIERS ---\n";
        details = details + "Suspect Name          : " + (report.suspectName != null ? report.suspectName : "N/A") + "\n";
        details = details + "Suspect Phone         : " + (report.suspectPhone != null ? report.suspectPhone : "N/A") + "\n";
        details = details + "Wallet Address        : " + (report.walletAddress != null ? report.walletAddress : "N/A") + "\n";
        details = details + "Transaction Hash      : " + (report.transactionHash != null ? report.transactionHash : "N/A") + "\n\n";
        details = details + "--- INVESTIGATOR REMARKS ---\n";
        if (report.investigatorRemarks != null && !report.investigatorRemarks.isBlank()) {
            details = details + report.investigatorRemarks + "\n";
        } else {
            details = details + "No remarks added yet.\n";
        }

        JTextArea jTextAreaDetails = new JTextArea(details);
        jTextAreaDetails.setEditable(false);
        jTextAreaDetails.setFont(new Font("Monospaced", Font.PLAIN, 13));
        jTextAreaDetails.setCaretPosition(0);
        JScrollPane jScrollPaneDetails = new JScrollPane(jTextAreaDetails);
        jScrollPaneDetails.setBounds(20, 20, 595, 420);
        jDialogDetails.add(jScrollPaneDetails);

        JButton jButtonClose = makeButton("CLOSE", 245, 455, 150, 40, greyColor);
        jButtonClose.addActionListener(e -> jDialogDetails.dispose());
        jDialogDetails.add(jButtonClose);

        jDialogDetails.setVisible(true);
    }

    void openTrackWindow() {
        JFrame jFrameTrack = new JFrame("Track Complaint");
        jFrameTrack.setSize(950, 560);
        jFrameTrack.setLocationRelativeTo(this);
        jFrameTrack.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrameTrack.setLayout(null);
        jFrameTrack.getContentPane().setBackground(backgroundColor);

        jFrameTrack.add(makeHeading("TRACK COMPLAINT", 950));

        jFrameTrack.add(makeLabel("Enter Report ID :", 30, 75, 150));
        JTextField jTextFieldReportId = new JTextField();
        jTextFieldReportId.setBounds(180, 75, 220, 30);
        jTextFieldReportId.setFont(new Font("Arial", Font.PLAIN, 14));
        jFrameTrack.add(jTextFieldReportId);

        JButton jButtonSearch = makeButton("SEARCH", 420, 70, 150, 40, blueColor);
        jFrameTrack.add(jButtonSearch);

        JButton jButtonBack = makeButton("BACK", 760, 70, 150, 40, greyColor);
        jFrameTrack.add(jButtonBack);

        JLabel jLabelProgress = new JLabel("Investigation Progress :");
        jLabelProgress.setBounds(30, 125, 300, 25);
        jLabelProgress.setFont(new Font("Arial", Font.BOLD, 16));
        jLabelProgress.setForeground(sectionColor);
        jFrameTrack.add(jLabelProgress);

        String[] stages = {"Report Submitted", "Initial Review", "Investigation Started", "Evidence Analysis", "Investigation Completed", "Resolved / Closed"};
        JLabel[] jLabelStages = new JLabel[6];
        for (int i = 0; i < 6; i++) {
            jLabelStages[i] = new JLabel("<html><center>Stage " + (i + 1) + "<br>" + stages[i] + "</center></html>", SwingConstants.CENTER);
            jLabelStages[i].setBounds(30 + (i * 148), 155, 140, 60);
            jLabelStages[i].setFont(new Font("Arial", Font.BOLD, 12));
            jLabelStages[i].setOpaque(true);
            jLabelStages[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            jFrameTrack.add(jLabelStages[i]);
        }
        updateTimeline(jLabelStages, "SUBMITTED");

        JLabel jLabelStatus = makeLabel("Current Status : N/A", 30, 235, 420);
        jFrameTrack.add(jLabelStatus);
        JLabel jLabelUnit = makeLabel("Assigned Unit : N/A", 470, 235, 440);
        jFrameTrack.add(jLabelUnit);
        JLabel jLabelSubmitted = makeLabel("Submitted Date : N/A", 30, 270, 420);
        jFrameTrack.add(jLabelSubmitted);
        JLabel jLabelUpdated = makeLabel("Last Updated : N/A", 470, 270, 440);
        jFrameTrack.add(jLabelUpdated);

        JLabel jLabelRemarks = new JLabel("Investigator Remarks :");
        jLabelRemarks.setBounds(30, 315, 300, 25);
        jLabelRemarks.setFont(new Font("Arial", Font.BOLD, 16));
        jLabelRemarks.setForeground(sectionColor);
        jFrameTrack.add(jLabelRemarks);

        JTextArea jTextAreaRemarks = new JTextArea("Enter a valid Report ID above to track live investigation progress.");
        jTextAreaRemarks.setEditable(false);
        jTextAreaRemarks.setLineWrap(true);
        jTextAreaRemarks.setWrapStyleWord(true);
        jTextAreaRemarks.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane jScrollPaneRemarks = new JScrollPane(jTextAreaRemarks);
        jScrollPaneRemarks.setBounds(30, 345, 875, 120);
        jFrameTrack.add(jScrollPaneRemarks);

        jButtonSearch.addActionListener(e -> {
            String reportId = jTextFieldReportId.getText().trim();
            if (reportId.isBlank()) {
                JOptionPane.showMessageDialog(jFrameTrack, "Please enter a Report ID (e.g. CF-2026-0001).", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                Backend.CybercrimeReport report = backend.getReportById(reportId);
                if (report == null) {
                    JOptionPane.showMessageDialog(jFrameTrack, "No report found with ID: " + reportId, "Not Found", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                jLabelStatus.setText("Current Status : " + report.status);
                jLabelUnit.setText("Assigned Unit : " + (report.assignedUnit != null ? report.assignedUnit : "Cyber Crime Cell"));
                jLabelSubmitted.setText("Submitted Date : " + (report.createdAt != null ? report.createdAt : "N/A"));
                jLabelUpdated.setText("Last Updated : " + (report.updatedAt != null ? report.updatedAt : "N/A"));
                if (report.investigatorRemarks != null && !report.investigatorRemarks.isBlank()) {
                    jTextAreaRemarks.setText(report.investigatorRemarks);
                } else {
                    jTextAreaRemarks.setText("No official investigator remarks added yet.");
                }
                updateTimeline(jLabelStages, report.status);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(jFrameTrack, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        jButtonBack.addActionListener(e -> jFrameTrack.dispose());

        jFrameTrack.setVisible(true);
    }

    void updateTimeline(JLabel[] jLabelStages, String status) {
        int activeStage = 0;
        String s = status.toUpperCase();
        if (s.contains("SUBMITTED")) {
            activeStage = 0;
        } else if (s.contains("REVIEW")) {
            activeStage = 1;
        } else if (s.contains("INVESTIGATION")) {
            activeStage = 2;
        } else if (s.contains("EVIDENCE")) {
            activeStage = 3;
        } else if (s.contains("COMPLETED")) {
            activeStage = 4;
        } else if (s.contains("RESOLVED") || s.contains("CLOSED")) {
            activeStage = 5;
        }

        for (int i = 0; i < jLabelStages.length; i++) {
            if (i < activeStage) {
                jLabelStages[i].setBackground(new Color(144, 238, 144));
            } else if (i == activeStage) {
                jLabelStages[i].setBackground(Color.YELLOW);
            } else {
                jLabelStages[i].setBackground(Color.LIGHT_GRAY);
            }
        }
    }

    void openEvidenceWindow() {
        JFrame jFrameEvidence = new JFrame("Submit Evidence");
        jFrameEvidence.setSize(900, 650);
        jFrameEvidence.setLocationRelativeTo(this);
        jFrameEvidence.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrameEvidence.setLayout(null);
        jFrameEvidence.getContentPane().setBackground(backgroundColor);

        jFrameEvidence.add(makeHeading("SUBMIT DIGITAL EVIDENCE", 900));

        jFrameEvidence.add(makeLabel("Report ID * :", 30, 70, 200));
        JTextField jTextFieldReportId = new JTextField();
        jTextFieldReportId.setBounds(240, 70, 300, 30);
        jFrameEvidence.add(jTextFieldReportId);

        JLabel jLabelReportIdExample = new JLabel("(e.g. CF-2026-0001)");
        jLabelReportIdExample.setBounds(560, 70, 200, 30);
        jLabelReportIdExample.setFont(new Font("Arial", Font.ITALIC, 13));
        jFrameEvidence.add(jLabelReportIdExample);

        jFrameEvidence.add(makeLabel("Evidence Name * :", 30, 110, 200));
        JTextField jTextFieldEvidenceName = new JTextField();
        jTextFieldEvidenceName.setBounds(240, 110, 300, 30);
        jFrameEvidence.add(jTextFieldEvidenceName);

        jFrameEvidence.add(makeLabel("Evidence Type * :", 30, 150, 200));
        JComboBox<String> jComboBoxEvidenceType = new JComboBox<>(new String[]{
            "Screenshot", "Document", "Image", "Video", "Audio", "Transaction Record", "Other"
        });
        jComboBoxEvidenceType.setBounds(240, 150, 300, 30);
        jFrameEvidence.add(jComboBoxEvidenceType);

        jFrameEvidence.add(makeLabel("Select File * :", 30, 190, 200));
        JTextField jTextFieldFilePath = new JTextField();
        jTextFieldFilePath.setBounds(240, 190, 300, 30);
        jTextFieldFilePath.setEditable(false);
        jFrameEvidence.add(jTextFieldFilePath);

        JButton jButtonChooseFile = makeButton("CHOOSE FILE", 560, 190, 150, 30, orangeColor);
        jFrameEvidence.add(jButtonChooseFile);

        jFrameEvidence.add(makeLabel("SHA-256 Hash :", 30, 230, 200));
        JTextField jTextFieldHash = new JTextField("Select a file to compute SHA-256 hash...");
        jTextFieldHash.setBounds(240, 230, 470, 30);
        jTextFieldHash.setEditable(false);
        jTextFieldHash.setFont(new Font("Monospaced", Font.BOLD, 12));
        jFrameEvidence.add(jTextFieldHash);

        jFrameEvidence.add(makeLabel("Description :", 30, 270, 200));
        JTextArea jTextAreaDescription = new JTextArea();
        jTextAreaDescription.setLineWrap(true);
        JScrollPane jScrollPaneDescription = new JScrollPane(jTextAreaDescription);
        jScrollPaneDescription.setBounds(240, 270, 470, 60);
        jFrameEvidence.add(jScrollPaneDescription);

        JButton jButtonSubmit = makeButton("SUBMIT EVIDENCE", 30, 345, 220, 45, greenColor);
        jFrameEvidence.add(jButtonSubmit);

        JButton jButtonBack = makeButton("BACK", 710, 345, 150, 45, greyColor);
        jFrameEvidence.add(jButtonBack);

        JLabel jLabelSubmitted = new JLabel("Submitted Evidence :");
        jLabelSubmitted.setBounds(30, 400, 300, 25);
        jLabelSubmitted.setFont(new Font("Arial", Font.BOLD, 16));
        jLabelSubmitted.setForeground(sectionColor);
        jFrameEvidence.add(jLabelSubmitted);

        String[] columns = {"Report ID", "Evidence Name", "Type", "SHA-256 Hash", "Status"};
        DefaultTableModel defaultTableModelEvidence = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable jTableEvidence = new JTable(defaultTableModelEvidence);
        jTableEvidence.setRowHeight(25);
        JScrollPane jScrollPaneEvidence = new JScrollPane(jTableEvidence);
        jScrollPaneEvidence.setBounds(30, 430, 830, 160);
        jFrameEvidence.add(jScrollPaneEvidence);

        jButtonChooseFile.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            if (jFileChooser.showOpenDialog(jFrameEvidence) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = jFileChooser.getSelectedFile();
                jTextFieldFilePath.setText(selectedFile.getAbsolutePath());
                if (jTextFieldEvidenceName.getText().isBlank()) {
                    jTextFieldEvidenceName.setText(selectedFile.getName());
                }
                try {
                    String hash = backend.sha256(selectedFile.getAbsolutePath());
                    jTextFieldHash.setText(hash);
                } catch (Exception ex) {
                    jTextFieldHash.setText("Error computing hash: " + ex.getMessage());
                }
            }
        });

        jButtonSubmit.addActionListener(e -> {
            String reportId = jTextFieldReportId.getText().trim();
            String evidenceName = jTextFieldEvidenceName.getText().trim();
            String evidenceType = String.valueOf(jComboBoxEvidenceType.getSelectedItem());
            String filePath = jTextFieldFilePath.getText().trim();
            String hash = jTextFieldHash.getText().trim();
            String description = jTextAreaDescription.getText().trim();

            if (reportId.isBlank() || evidenceName.isBlank() || filePath.isBlank() || hash.startsWith("Select") || hash.startsWith("Error")) {
                JOptionPane.showMessageDialog(jFrameEvidence, "Please provide Report ID, Evidence Name, and choose a valid file.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                backend.addUserEvidence(reportId, evidenceName, evidenceType, description, filePath, hash, currentUser.id);
                JOptionPane.showMessageDialog(jFrameEvidence, "Evidence submitted successfully!\n\nImmutable SHA-256 Hash:\n" + hash, "Evidence Uploaded", JOptionPane.INFORMATION_MESSAGE);
                defaultTableModelEvidence.addRow(new Object[]{reportId, evidenceName, evidenceType, hash.substring(0, Math.min(20, hash.length())) + "...", "Verified"});
                jTextFieldEvidenceName.setText("");
                jTextFieldFilePath.setText("");
                jTextFieldHash.setText("Select a file...");
                jTextAreaDescription.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(jFrameEvidence, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        jButtonBack.addActionListener(e -> jFrameEvidence.dispose());

        jFrameEvidence.setVisible(true);
    }

    void openNotificationsWindow() {
        JFrame jFrameNotifications = new JFrame("Notifications");
        jFrameNotifications.setSize(850, 500);
        jFrameNotifications.setLocationRelativeTo(this);
        jFrameNotifications.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrameNotifications.setLayout(null);
        jFrameNotifications.getContentPane().setBackground(backgroundColor);

        jFrameNotifications.add(makeHeading("NOTIFICATIONS", 850));

        String[] columns = {"ID", "Report ID", "Message", "Timestamp"};
        DefaultTableModel defaultTableModelNotifications = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable jTableNotifications = new JTable(defaultTableModelNotifications);
        jTableNotifications.setRowHeight(25);
        JScrollPane jScrollPaneNotifications = new JScrollPane(jTableNotifications);
        jScrollPaneNotifications.setBounds(30, 70, 775, 290);
        jFrameNotifications.add(jScrollPaneNotifications);

        JButton jButtonMarkRead = makeButton("MARK ALL AS READ", 30, 380, 220, 45, greenColor);
        jFrameNotifications.add(jButtonMarkRead);

        JButton jButtonRefresh = makeButton("REFRESH", 270, 380, 150, 45, orangeColor);
        jFrameNotifications.add(jButtonRefresh);

        JButton jButtonBack = makeButton("BACK", 655, 380, 150, 45, greyColor);
        jFrameNotifications.add(jButtonBack);

        Runnable loadNotifications = () -> {
            try {
                defaultTableModelNotifications.setRowCount(0);
                List<Backend.UserNotification> notifications = backend.getNotificationsByUser(currentUser.id);
                for (Backend.UserNotification notification : notifications) {
                    defaultTableModelNotifications.addRow(new Object[]{
                        notification.id, notification.reportId != null ? notification.reportId : "System", notification.message, notification.createdAt
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };

        jButtonMarkRead.addActionListener(e -> {
            try {
                backend.markNotificationsRead(currentUser.id);
                loadNotifications.run();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        jButtonRefresh.addActionListener(e -> loadNotifications.run());
        jButtonBack.addActionListener(e -> jFrameNotifications.dispose());

        loadNotifications.run();
        jFrameNotifications.setVisible(true);
    }

    void openProfileWindow() {
        JFrame jFrameProfile = new JFrame("My Profile");
        jFrameProfile.setSize(850, 480);
        jFrameProfile.setLocationRelativeTo(this);
        jFrameProfile.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrameProfile.setLayout(null);
        jFrameProfile.getContentPane().setBackground(backgroundColor);

        jFrameProfile.add(makeHeading("MY PROFILE", 850));

        JLabel jLabelEditProfile = new JLabel("Edit Profile");
        jLabelEditProfile.setBounds(30, 65, 300, 25);
        jLabelEditProfile.setFont(new Font("Arial", Font.BOLD, 16));
        jLabelEditProfile.setForeground(sectionColor);
        jFrameProfile.add(jLabelEditProfile);

        jFrameProfile.add(makeLabel("Username :", 30, 100, 130));
        JTextField jTextFieldUsername = new JTextField(currentUser.username);
        jTextFieldUsername.setBounds(170, 100, 230, 30);
        jTextFieldUsername.setEditable(false);
        jFrameProfile.add(jTextFieldUsername);

        jFrameProfile.add(makeLabel("Full Name :", 30, 145, 130));
        JTextField jTextFieldFullName = new JTextField(currentUser.fullName != null ? currentUser.fullName : "");
        jTextFieldFullName.setBounds(170, 145, 230, 30);
        jFrameProfile.add(jTextFieldFullName);

        jFrameProfile.add(makeLabel("Email :", 30, 190, 130));
        JTextField jTextFieldEmail = new JTextField(currentUser.email != null ? currentUser.email : "");
        jTextFieldEmail.setBounds(170, 190, 230, 30);
        jFrameProfile.add(jTextFieldEmail);

        jFrameProfile.add(makeLabel("Mobile Number :", 30, 235, 130));
        JTextField jTextFieldMobile = new JTextField(currentUser.mobileNumber != null ? currentUser.mobileNumber : "");
        jTextFieldMobile.setBounds(170, 235, 230, 30);
        jFrameProfile.add(jTextFieldMobile);

        JButton jButtonSaveProfile = makeButton("SAVE PROFILE", 30, 290, 200, 45, greenColor);
        jFrameProfile.add(jButtonSaveProfile);

        JLabel jLabelChangePassword = new JLabel("Change Password");
        jLabelChangePassword.setBounds(440, 65, 300, 25);
        jLabelChangePassword.setFont(new Font("Arial", Font.BOLD, 16));
        jLabelChangePassword.setForeground(sectionColor);
        jFrameProfile.add(jLabelChangePassword);

        jFrameProfile.add(makeLabel("Current Password :", 440, 100, 160));
        JPasswordField jPasswordFieldOld = new JPasswordField();
        jPasswordFieldOld.setBounds(600, 100, 210, 30);
        jFrameProfile.add(jPasswordFieldOld);

        jFrameProfile.add(makeLabel("New Password :", 440, 145, 160));
        JPasswordField jPasswordFieldNew = new JPasswordField();
        jPasswordFieldNew.setBounds(600, 145, 210, 30);
        jFrameProfile.add(jPasswordFieldNew);

        jFrameProfile.add(makeLabel("Confirm Password :", 440, 190, 160));
        JPasswordField jPasswordFieldConfirm = new JPasswordField();
        jPasswordFieldConfirm.setBounds(600, 190, 210, 30);
        jFrameProfile.add(jPasswordFieldConfirm);

        JButton jButtonChangePassword = makeButton("CHANGE PASSWORD", 440, 290, 220, 45, greenColor);
        jFrameProfile.add(jButtonChangePassword);

        JButton jButtonBack = makeButton("BACK", 345, 370, 150, 45, greyColor);
        jFrameProfile.add(jButtonBack);

        jButtonSaveProfile.addActionListener(e -> {
            try {
                String fullName = jTextFieldFullName.getText().trim();
                String email = jTextFieldEmail.getText().trim();
                String mobile = jTextFieldMobile.getText().trim();
                if (fullName.isBlank() || email.isBlank() || mobile.isBlank()) {
                    throw new Exception("Full Name, Email, and Mobile Number cannot be blank.");
                }
                if (backend.updateProfile(currentUser.id, fullName, email, mobile)) {
                    currentUser.fullName = fullName;
                    currentUser.email = email;
                    currentUser.mobileNumber = mobile;
                    jLabelWelcome.setText("Welcome, " + fullName);
                    JOptionPane.showMessageDialog(jFrameProfile, "Profile updated successfully.", "Profile Updated", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(jFrameProfile, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        jButtonChangePassword.addActionListener(e -> {
            try {
                String oldPassword = new String(jPasswordFieldOld.getPassword());
                String newPassword = new String(jPasswordFieldNew.getPassword());
                String confirmPassword = new String(jPasswordFieldConfirm.getPassword());
                if (oldPassword.isBlank() || newPassword.isBlank()) {
                    throw new Exception("Please enter your current and new password.");
                }
                if (!newPassword.equals(confirmPassword)) {
                    throw new Exception("New Password and Confirm Password do not match.");
                }
                if (backend.changePassword(currentUser.id, oldPassword, newPassword)) {
                    JOptionPane.showMessageDialog(jFrameProfile, "Password changed successfully.", "Password Changed", JOptionPane.INFORMATION_MESSAGE);
                    jPasswordFieldOld.setText("");
                    jPasswordFieldNew.setText("");
                    jPasswordFieldConfirm.setText("");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(jFrameProfile, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        jButtonBack.addActionListener(e -> jFrameProfile.dispose());

        jFrameProfile.setVisible(true);
    }
}
