import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;

public class UserPortal extends JFrame {
    private final Backend backend;
    private final Backend.UserSession currentUser;

    // UI Color Palette (Clean Light Theme)
    private static final Color BG_LIGHT = new Color(248, 250, 252);     // #f8fafc Light Neutral Background
    private static final Color CARD_BG = Color.WHITE;                   // #ffffff Clean White Cards
    private static final Color CARD_BORDER = new Color(226, 232, 240); // #e2e8f0 Subtle Light Border
    private static final Color ACCENT_BLUE = new Color(37, 99, 235);   // #2563eb Accent Blue
    private static final Color TEXT_MAIN = new Color(15, 23, 42);       // #0f172a Dark Main Text
    private static final Color TEXT_MUTED = new Color(100, 116, 139);   // #64748b Muted Slate Text
    
    // Sidebar Colors (White Theme)
    private static final Color SIDEBAR_BG = Color.WHITE;                  // #ffffff White Sidebar
    private static final Color SIDEBAR_ACTIVE = new Color(239, 246, 255); // #eff6ff Soft Blue Active Highlight
    private static final Color SIDEBAR_TEXT_MAIN = new Color(15, 23, 42);  // #0f172a
    private static final Color SIDEBAR_TEXT_MUTED = new Color(71, 85, 105); // #475569

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JButton activeNavButton = null;

    // Stat labels on dashboard
    private JLabel lblTotalReports, lblPendingReports, lblInvestigating, lblResolved;

    public UserPortal(Backend backend, Backend.UserSession user) {
        this.backend = backend;
        this.currentUser = user;
        initUI();
    }

    private void initUI() {
        setTitle("Cyber Forensic Investigation Portal - Citizen User Portal");
        setSize(1100, 720);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BG_LIGHT);

        // Sidebar
        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Content Area (CardLayout)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_LIGHT);

        // Create Modules
        contentPanel.add(createDashboardModule(), "DASHBOARD");
        contentPanel.add(createReportModule(), "REPORT");
        contentPanel.add(createMyReportsModule(), "MY_REPORTS");
        contentPanel.add(createTrackModule(), "TRACK");
        contentPanel.add(createEvidenceModule(), "EVIDENCE");
        contentPanel.add(createNotificationsModule(), "NOTIFICATIONS");
        contentPanel.add(createProfileModule(), "PROFILE");

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        refreshDashboardStats();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 700));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, CARD_BORDER));

        // Header / Logo
        JPanel logoPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        logoPanel.setBackground(SIDEBAR_BG);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 18, 20, 18));

        JLabel titleLbl = new JLabel("CYBER PORTAL");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLbl.setForeground(SIDEBAR_TEXT_MAIN);

        JLabel subtitleLbl = new JLabel("Citizen / User Portal");
        subtitleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLbl.setForeground(SIDEBAR_TEXT_MUTED);

        logoPanel.add(titleLbl);
        logoPanel.add(subtitleLbl);
        sidebar.add(logoPanel, BorderLayout.NORTH);

        // Nav Buttons
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(SIDEBAR_BG);
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnDash = createNavBtn("Dashboard", "DASHBOARD");
        JButton btnReport = createNavBtn("Report Cybercrime", "REPORT");
        JButton btnMyReports = createNavBtn("My Reports", "MY_REPORTS");
        JButton btnTrack = createNavBtn("Track Complaint", "TRACK");
        JButton btnEvidence = createNavBtn("Submit Evidence", "EVIDENCE");
        JButton btnNotif = createNavBtn("Notifications", "NOTIFICATIONS");
        JButton btnProfile = createNavBtn("User Profile", "PROFILE");

        navPanel.add(btnDash); navPanel.add(Box.createVerticalStrut(6));
        navPanel.add(btnReport); navPanel.add(Box.createVerticalStrut(6));
        navPanel.add(btnMyReports); navPanel.add(Box.createVerticalStrut(6));
        navPanel.add(btnTrack); navPanel.add(Box.createVerticalStrut(6));
        navPanel.add(btnEvidence); navPanel.add(Box.createVerticalStrut(6));
        navPanel.add(btnNotif); navPanel.add(Box.createVerticalStrut(6));
        navPanel.add(btnProfile);

        sidebar.add(navPanel, BorderLayout.CENTER);

        // Bottom User / Logout
        JPanel userPanel = new JPanel(new BorderLayout(5, 5));
        userPanel.setBackground(SIDEBAR_BG);
        userPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, CARD_BORDER),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel userLbl = new JLabel("Logged in: " + currentUser.username);
        userLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLbl.setForeground(SIDEBAR_TEXT_MAIN);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(225, 29, 72)); // Crimson
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        logoutBtn.addActionListener(e -> logout());

        userPanel.add(userLbl, BorderLayout.NORTH);
        userPanel.add(logoutBtn, BorderLayout.SOUTH);
        sidebar.add(userPanel, BorderLayout.SOUTH);

        // Set Dashboard as default active
        setNavActive(btnDash, "DASHBOARD");

        return sidebar;
    }

    private JButton createNavBtn(String text, String cardName) {
        JButton btn = new JButton("  " + text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(SIDEBAR_TEXT_MUTED);
        btn.setBackground(SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(220, 42));
        btn.setPreferredSize(new Dimension(220, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            setNavActive(btn, cardName);
            cardLayout.show(contentPanel, cardName);
            if (cardName.equals("DASHBOARD")) refreshDashboardStats();
            if (cardName.equals("MY_REPORTS")) loadMyReportsTable();
            if (cardName.equals("NOTIFICATIONS")) loadNotifications();
        });
        return btn;
    }

    private void setNavActive(JButton btn, String cardName) {
        if (activeNavButton != null) {
            activeNavButton.setBackground(SIDEBAR_BG);
            activeNavButton.setForeground(SIDEBAR_TEXT_MUTED);
            activeNavButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        activeNavButton = btn;
        btn.setBackground(SIDEBAR_ACTIVE);
        btn.setForeground(ACCENT_BLUE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new Frontend().setVisible(true));
        }
    }

    // ==================== 1. DASHBOARD MODULE ====================

    private JPanel createDashboardModule() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1, 4, 4));
        header.setBackground(BG_LIGHT);
        JLabel title = new JLabel("CYBER FORENSIC INVESTIGATION PORTAL");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_MAIN);

        JLabel sub = new JLabel("Welcome back, " + (currentUser.fullName != null && !currentUser.fullName.isBlank() ? currentUser.fullName : currentUser.username) + " | Citizen Cybercrime Helpdesk");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(TEXT_MUTED);

        header.add(title); header.add(sub);
        panel.add(header, BorderLayout.NORTH);

        // Center Content: Cards + Actions
        JPanel center = new JPanel(new BorderLayout(20, 20));
        center.setBackground(BG_LIGHT);

        // 4 Stat Cards (Clean white background, no colored strips)
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 15, 15));
        statsGrid.setBackground(BG_LIGHT);

        lblTotalReports = new JLabel("0", SwingConstants.CENTER);
        lblPendingReports = new JLabel("0", SwingConstants.CENTER);
        lblInvestigating = new JLabel("0", SwingConstants.CENTER);
        lblResolved = new JLabel("0", SwingConstants.CENTER);

        statsGrid.add(createStatCard("Total Reports", lblTotalReports));
        statsGrid.add(createStatCard("Pending Reports", lblPendingReports));
        statsGrid.add(createStatCard("Under Investigation", lblInvestigating));
        statsGrid.add(createStatCard("Resolved Reports", lblResolved));

        center.add(statsGrid, BorderLayout.NORTH);

        // Quick Actions & Help Banner
        JPanel actionPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        actionPanel.setBackground(BG_LIGHT);
        actionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            " Quick Actions ",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            TEXT_MAIN
        ));

        JButton btn1 = createActionButton("Report Cybercrime", "Submit a new complaint with incident details");
        btn1.addActionListener(e -> { cardLayout.show(contentPanel, "REPORT"); });

        JButton btn2 = createActionButton("View My Reports", "Check status and history of your complaints");
        btn2.addActionListener(e -> { cardLayout.show(contentPanel, "MY_REPORTS"); loadMyReportsTable(); });

        JButton btn3 = createActionButton("Track Complaint", "View timeline and investigator remarks");
        btn3.addActionListener(e -> { cardLayout.show(contentPanel, "TRACK"); });

        JButton btn4 = createActionButton("Submit Digital Evidence", "Upload screenshots, logs, or transaction records");
        btn4.addActionListener(e -> { cardLayout.show(contentPanel, "EVIDENCE"); });

        actionPanel.add(btn1); actionPanel.add(btn2);
        actionPanel.add(btn3); actionPanel.add(btn4);

        center.add(actionPanel, BorderLayout.CENTER);

        // Footer Note
        JLabel footerNote = new JLabel("For emergencies, call National Cyber Crime Helpline: 1930", SwingConstants.CENTER);
        footerNote.setFont(new Font("Segoe UI", Font.BOLD, 13));
        footerNote.setForeground(new Color(225, 29, 72)); // Alert Crimson
        panel.add(footerNote, BorderLayout.SOUTH);

        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(String title, JLabel valueLbl) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(TEXT_MUTED);

        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valueLbl.setForeground(TEXT_MAIN);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);
        return card;
    }

    private JButton createActionButton(String title, String desc) {
        JButton btn = new JButton("<html><body style='width: 180px;'><b><font size='4' color='#0f172a'>" + title + "</font></b><br><font size='2' color='#64748b'>" + desc + "</font></body></html>");
        btn.setBackground(CARD_BG);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        return btn;
    }

    private void refreshDashboardStats() {
        try {
            Map<String, Integer> stats = backend.getUserStats(currentUser.id);
            lblTotalReports.setText(String.valueOf(stats.getOrDefault("total", 0)));
            lblPendingReports.setText(String.valueOf(stats.getOrDefault("pending", 0)));
            lblInvestigating.setText(String.valueOf(stats.getOrDefault("investigating", 0)));
            lblResolved.setText(String.valueOf(stats.getOrDefault("resolved", 0)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== 2. REPORT CYBERCRIME MODULE ====================

    private JTextField txtVictimName, txtEmail, txtMobile, txtIncidentDate, txtLocation, txtAmount;
    private JTextField txtSuspectName, txtSuspectPhone, txtSuspectEmail, txtWallet, txtTxHash, txtWebsite;
    private JTextArea txtDescription, txtAdditional;
    private JComboBox<String> comboCategory;
    private JLabel lblGeneratedReportId;

    private JPanel createReportModule() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(BG_LIGHT);
        root.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Header
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG_LIGHT);
        JLabel title = new JLabel("REPORT A CYBERCRIME");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_MAIN);

        lblGeneratedReportId = new JLabel("Auto-Generated Report ID: Previewing...");
        lblGeneratedReportId.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblGeneratedReportId.setForeground(ACCENT_BLUE);

        top.add(title, BorderLayout.WEST);
        top.add(lblGeneratedReportId, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        // Form in ScrollPane
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(BG_LIGHT);

        // Section 1: Incident & Victim Info
        JPanel s1 = createFormSection("1. Victim & Incident Details");
        s1.setLayout(new GridLayout(4, 4, 12, 10));

        txtVictimName = new JTextField(currentUser.fullName != null ? currentUser.fullName : "");
        txtEmail = new JTextField(currentUser.email != null ? currentUser.email : "");
        txtMobile = new JTextField(currentUser.mobileNumber != null ? currentUser.mobileNumber : "");
        comboCategory = new JComboBox<>(new String[]{
            "Financial Fraud", "UPI Fraud", "Online Banking Fraud", "Cryptocurrency Fraud",
            "Phishing", "Identity Theft", "Social Media Fraud", "Cyberbullying", "Hacking", "Other"
        });
        txtIncidentDate = new JTextField("2026-10-01");
        txtLocation = new JTextField();
        txtAmount = new JTextField();

        addFormField(s1, "Victim Name *", txtVictimName);
        addFormField(s1, "Email Address *", txtEmail);
        addFormField(s1, "Mobile Number *", txtMobile);
        addFormField(s1, "Crime Category *", comboCategory);
        addFormField(s1, "Incident Date (YYYY-MM-DD) *", txtIncidentDate);
        addFormField(s1, "Incident Location", txtLocation);
        addFormField(s1, "Amount Lost (₹)", txtAmount);
        addFormField(s1, "", new JLabel()); // Spacer

        form.add(s1);
        form.add(Box.createVerticalStrut(15));

        // Section 2: Description
        JPanel s2 = createFormSection("2. Incident Description *");
        s2.setLayout(new BorderLayout(8, 8));
        txtDescription = new JTextArea(4, 50);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        s2.add(new JScrollPane(txtDescription), BorderLayout.CENTER);
        form.add(s2);
        form.add(Box.createVerticalStrut(15));

        // Section 3: Suspect & Technical Identifiers (Optional)
        JPanel s3 = createFormSection("3. Suspect & Digital Identifiers (Optional)");
        s3.setLayout(new GridLayout(3, 4, 12, 10));

        txtSuspectName = new JTextField();
        txtSuspectPhone = new JTextField();
        txtSuspectEmail = new JTextField();
        txtWallet = new JTextField();
        txtTxHash = new JTextField();
        txtWebsite = new JTextField();

        addFormField(s3, "Suspect Name", txtSuspectName);
        addFormField(s3, "Suspect Phone", txtSuspectPhone);
        addFormField(s3, "Suspect Email", txtSuspectEmail);
        addFormField(s3, "Crypto Wallet Address", txtWallet);
        addFormField(s3, "Txn ID / Hash", txtTxHash);
        addFormField(s3, "Website / Profile URL", txtWebsite);

        form.add(s3);
        form.add(Box.createVerticalStrut(15));

        // Section 4: Additional Remarks
        JPanel s4 = createFormSection("4. Additional Information (Optional)");
        s4.setLayout(new BorderLayout(8, 8));
        txtAdditional = new JTextArea(2, 50);
        txtAdditional.setLineWrap(true);
        txtAdditional.setWrapStyleWord(true);
        s4.add(new JScrollPane(txtAdditional), BorderLayout.CENTER);
        form.add(s4);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);

        // Bottom Buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottom.setBackground(BG_LIGHT);

        JButton btnClear = new JButton("Clear Form");
        styleSecondaryButton(btnClear);
        btnClear.addActionListener(e -> clearReportForm());

        JButton btnSubmit = new JButton("Submit Cybercrime Report");
        stylePrimaryButton(btnSubmit);
        btnSubmit.addActionListener(e -> submitCybercrimeReport());

        bottom.add(btnClear);
        bottom.add(btnSubmit);
        root.add(bottom, BorderLayout.SOUTH);

        return root;
    }

    private JPanel createFormSection(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                " " + title + " ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), TEXT_MAIN
            ),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        return panel;
    }

    private void addFormField(JPanel container, String labelText, JComponent field) {
        JPanel cell = new JPanel(new BorderLayout(2, 4));
        cell.setBackground(CARD_BG);
        if (!labelText.isEmpty()) {
            JLabel lbl = new JLabel(labelText);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lbl.setForeground(TEXT_MUTED);
            cell.add(lbl, BorderLayout.NORTH);
        }
        cell.add(field, BorderLayout.CENTER);
        container.add(cell);
    }

    private void clearReportForm() {
        txtVictimName.setText(currentUser.fullName != null ? currentUser.fullName : "");
        txtEmail.setText(currentUser.email != null ? currentUser.email : "");
        txtMobile.setText(currentUser.mobileNumber != null ? currentUser.mobileNumber : "");
        txtIncidentDate.setText("2026-10-01");
        txtLocation.setText(""); txtAmount.setText(""); txtDescription.setText("");
        txtSuspectName.setText(""); txtSuspectPhone.setText(""); txtSuspectEmail.setText("");
        txtWallet.setText(""); txtTxHash.setText(""); txtWebsite.setText(""); txtAdditional.setText("");
    }

    private void submitCybercrimeReport() {
        try {
            if (txtVictimName.getText().isBlank() || txtEmail.getText().isBlank() ||
                txtMobile.getText().isBlank() || txtDescription.getText().isBlank()) {
                throw new Exception("Please fill in all required fields (Victim Name, Email, Mobile, Description).");
            }

            Backend.CybercrimeReport r = new Backend.CybercrimeReport();
            r.userId = currentUser.id;
            r.victimName = txtVictimName.getText().trim();
            r.email = txtEmail.getText().trim();
            r.mobileNumber = txtMobile.getText().trim();
            r.crimeCategory = String.valueOf(comboCategory.getSelectedItem());
            r.incidentDate = txtIncidentDate.getText().trim();
            r.location = txtLocation.getText().trim();
            r.description = txtDescription.getText().trim();
            r.amountLost = txtAmount.getText().trim();
            r.suspectName = txtSuspectName.getText().trim();
            r.suspectPhone = txtSuspectPhone.getText().trim();
            r.suspectEmail = txtSuspectEmail.getText().trim();
            r.walletAddress = txtWallet.getText().trim();
            r.transactionHash = txtTxHash.getText().trim();
            r.websiteUrl = txtWebsite.getText().trim();
            r.additionalDetails = txtAdditional.getText().trim();

            String generatedId = backend.createReport(r);

            JOptionPane.showMessageDialog(this,
                "Your cybercrime report has been submitted successfully.\n\n" +
                "Report ID: " + generatedId + "\n" +
                "Submission Status: SUBMITTED\n\n" +
                "You can track this complaint using your Report ID under 'Track Complaint'.",
                "Report Submitted",
                JOptionPane.INFORMATION_MESSAGE
            );

            clearReportForm();
            refreshDashboardStats();
            cardLayout.show(contentPanel, "MY_REPORTS");
            loadMyReportsTable();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Submission Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== 3. MY REPORTS MODULE ====================

    private DefaultTableModel myReportsModel;
    private JTable tblMyReports;

    private JPanel createMyReportsModule() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(BG_LIGHT);
        root.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG_LIGHT);
        JLabel title = new JLabel("MY SUBMITTED REPORTS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_MAIN);

        JButton btnRefresh = new JButton("Refresh");
        styleSecondaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> loadMyReportsTable());

        top.add(title, BorderLayout.WEST);
        top.add(btnRefresh, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        String[] cols = {"Report ID", "Crime Category", "Incident Date", "Amount Lost", "Status", "Submission Date"};
        myReportsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMyReports = new JTable(myReportsModel);
        tblMyReports.setRowHeight(32);
        tblMyReports.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JScrollPane scroll = new JScrollPane(tblMyReports);
        scroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        root.add(scroll, BorderLayout.CENTER);

        // Bottom Details Button
        JPanel btm = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btm.setBackground(BG_LIGHT);
        JButton btnViewDetails = new JButton("View Full Report Details");
        stylePrimaryButton(btnViewDetails);
        btnViewDetails.addActionListener(e -> viewSelectedReportDetails());
        btm.add(btnViewDetails);
        root.add(btm, BorderLayout.SOUTH);

        return root;
    }

    private void loadMyReportsTable() {
        try {
            myReportsModel.setRowCount(0);
            List<Backend.CybercrimeReport> reports = backend.getReportsByUser(currentUser.id);
            for (Backend.CybercrimeReport r : reports) {
                myReportsModel.addRow(new Object[]{
                    r.reportId,
                    r.crimeCategory,
                    r.incidentDate,
                    (r.amountLost != null && !r.amountLost.isBlank()) ? "₹" + r.amountLost : "N/A",
                    r.status,
                    r.createdAt != null ? r.createdAt.substring(0, Math.min(10, r.createdAt.length())) : "N/A"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void viewSelectedReportDetails() {
        int row = tblMyReports.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a report from the table first.", "Select Report", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String reportId = (String) myReportsModel.getValueAt(row, 0);
        try {
            Backend.CybercrimeReport r = backend.getReportById(reportId);
            if (r != null) {
                showReportDetailsDialog(r);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showReportDetailsDialog(Backend.CybercrimeReport r) {
        JDialog dlg = new JDialog(this, "Report Details - " + r.reportId, true);
        dlg.setSize(650, 520);
        dlg.setLocationRelativeTo(this);

        JPanel p = new JPanel(new BorderLayout(15, 15));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("  CYBERCRIME REPORT DETAILS: ").append(r.reportId).append("\n");
        sb.append("====================================================\n\n");
        sb.append("Report Status         : ").append(r.status).append("\n");
        sb.append("Assigned Unit         : ").append(r.assignedUnit).append("\n");
        sb.append("Submission Date       : ").append(r.createdAt).append("\n\n");
        sb.append("--- VICTIM DETAILS ---\n");
        sb.append("Victim Name           : ").append(r.victimName).append("\n");
        sb.append("Email                 : ").append(r.email).append("\n");
        sb.append("Mobile Number         : ").append(r.mobileNumber).append("\n\n");
        sb.append("--- INCIDENT DETAILS ---\n");
        sb.append("Crime Category        : ").append(r.crimeCategory).append("\n");
        sb.append("Incident Date         : ").append(r.incidentDate).append("\n");
        sb.append("Incident Location     : ").append(r.location != null ? r.location : "N/A").append("\n");
        sb.append("Amount Lost           : ₹").append(r.amountLost != null ? r.amountLost : "0").append("\n\n");
        sb.append("Description:\n").append(r.description).append("\n\n");
        sb.append("--- SUSPECT / TECHNICAL IDENTIFIERS ---\n");
        sb.append("Suspect Name          : ").append(r.suspectName != null ? r.suspectName : "N/A").append("\n");
        sb.append("Suspect Phone         : ").append(r.suspectPhone != null ? r.suspectPhone : "N/A").append("\n");
        sb.append("Wallet Address        : ").append(r.walletAddress != null ? r.walletAddress : "N/A").append("\n");
        sb.append("Transaction Hash      : ").append(r.transactionHash != null ? r.transactionHash : "N/A").append("\n\n");
        sb.append("--- INVESTIGATOR REMARKS ---\n");
        sb.append(r.investigatorRemarks != null && !r.investigatorRemarks.isBlank() ? r.investigatorRemarks : "No remarks added yet.").append("\n");

        area.setText(sb.toString());
        area.setCaretPosition(0);

        p.add(new JScrollPane(area), BorderLayout.CENTER);
        JButton close = new JButton("Close");
        styleSecondaryButton(close);
        close.addActionListener(e -> dlg.dispose());
        p.add(close, BorderLayout.SOUTH);

        dlg.setContentPane(p);
        dlg.setVisible(true);
    }

    // ==================== 4. TRACK COMPLAINT MODULE ====================

    private JTextField txtTrackId;
    private JLabel lblTrackStatus, lblTrackUnit, lblTrackSubmitted, lblTrackUpdated;
    private JTextArea txtTrackRemarks;
    private JPanel timelinePanel;

    private JPanel createTrackModule() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(BG_LIGHT);
        root.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Header / Search
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBackground(BG_LIGHT);

        JLabel title = new JLabel("TRACK COMPLAINT PROGRESS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_MAIN);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchBar.setBackground(BG_LIGHT);

        txtTrackId = new JTextField(15);
        txtTrackId.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton btnTrack = new JButton("Search Report ID");
        stylePrimaryButton(btnTrack);

        searchBar.add(new JLabel("Enter Report ID: "));
        searchBar.add(txtTrackId);
        searchBar.add(btnTrack);

        top.add(title, BorderLayout.WEST);
        top.add(searchBar, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        // Center: Timeline + Info Panel
        JPanel center = new JPanel(new BorderLayout(15, 15));
        center.setBackground(BG_LIGHT);

        // Stage Timeline Panel
        timelinePanel = new JPanel(new GridLayout(1, 6, 8, 8));
        timelinePanel.setBackground(CARD_BG);
        timelinePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                " Investigation Progress Timeline ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), TEXT_MAIN
            ),
            BorderFactory.createEmptyBorder(15, 12, 15, 12)
        ));

        updateTimelineUI("SUBMITTED");
        center.add(timelinePanel, BorderLayout.NORTH);

        // Details Grid
        JPanel detailsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        detailsPanel.setBackground(BG_LIGHT);

        lblTrackStatus = new JLabel("Current Status: N/A");
        lblTrackUnit = new JLabel("Assigned Unit: N/A");
        lblTrackSubmitted = new JLabel("Submitted Date: N/A");
        lblTrackUpdated = new JLabel("Last Updated: N/A");

        detailsPanel.add(createDetailCard("Current Status", lblTrackStatus));
        detailsPanel.add(createDetailCard("Assigned Investigation Unit", lblTrackUnit));
        detailsPanel.add(createDetailCard("Submission Date", lblTrackSubmitted));
        detailsPanel.add(createDetailCard("Last System Update", lblTrackUpdated));

        center.add(detailsPanel, BorderLayout.CENTER);

        // Investigator Remarks
        JPanel remarksPanel = new JPanel(new BorderLayout(5, 5));
        remarksPanel.setBackground(CARD_BG);
        remarksPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                " Official Investigator Remarks ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), TEXT_MAIN
            ),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        txtTrackRemarks = new JTextArea(4, 50);
        txtTrackRemarks.setEditable(false);
        txtTrackRemarks.setText("Enter a valid Report ID above to track live investigation progress.");
        remarksPanel.add(new JScrollPane(txtTrackRemarks), BorderLayout.CENTER);

        center.add(remarksPanel, BorderLayout.SOUTH);
        root.add(center, BorderLayout.CENTER);

        btnTrack.addActionListener(e -> trackComplaint());
        return root;
    }

    private JPanel createDetailCard(String labelStr, JLabel valLbl) {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        JLabel title = new JLabel(labelStr);
        title.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        title.setForeground(TEXT_MUTED);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valLbl.setForeground(TEXT_MAIN);
        p.add(title, BorderLayout.NORTH);
        p.add(valLbl, BorderLayout.CENTER);
        return p;
    }

    private void updateTimelineUI(String activeStatus) {
        timelinePanel.removeAll();
        String[] stages = {
            "Report Submitted", "Initial Review", "Investigation Started",
            "Evidence Analysis", "Investigation Completed", "Resolved / Closed"
        };

        int activeIdx = 0;
        String s = activeStatus.toUpperCase();
        if (s.contains("SUBMITTED")) activeIdx = 0;
        else if (s.contains("REVIEW")) activeIdx = 1;
        else if (s.contains("INVESTIGATION")) activeIdx = 2;
        else if (s.contains("EVIDENCE")) activeIdx = 3;
        else if (s.contains("COMPLETED")) activeIdx = 4;
        else if (s.contains("RESOLVED") || s.contains("CLOSED")) activeIdx = 5;

        for (int i = 0; i < stages.length; i++) {
            JPanel node = new JPanel(new BorderLayout(4, 4));
            node.setBackground(CARD_BG);
            node.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(i <= activeIdx ? ACCENT_BLUE : CARD_BORDER, i == activeIdx ? 2 : 1),
                BorderFactory.createEmptyBorder(8, 6, 8, 6)
            ));

            JLabel num = new JLabel("Stage " + (i + 1), SwingConstants.CENTER);
            num.setFont(new Font("Segoe UI", Font.BOLD, 11));
            num.setForeground(i <= activeIdx ? ACCENT_BLUE : TEXT_MUTED);

            JLabel name = new JLabel("<html><center>" + stages[i] + "</center></html>", SwingConstants.CENTER);
            name.setFont(new Font("Segoe UI", i == activeIdx ? Font.BOLD : Font.PLAIN, 11));
            name.setForeground(i <= activeIdx ? TEXT_MAIN : TEXT_MUTED);

            node.add(num, BorderLayout.NORTH);
            node.add(name, BorderLayout.CENTER);
            timelinePanel.add(node);
        }
        timelinePanel.revalidate();
        timelinePanel.repaint();
    }

    private void trackComplaint() {
        String id = txtTrackId.getText().trim();
        if (id.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please enter a Report ID (e.g. CF-2026-0001).", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Backend.CybercrimeReport r = backend.getReportById(id);
            if (r == null) {
                JOptionPane.showMessageDialog(this, "No report found with ID: " + id, "Not Found", JOptionPane.ERROR_MESSAGE);
                return;
            }

            lblTrackStatus.setText(r.status);
            lblTrackUnit.setText(r.assignedUnit != null ? r.assignedUnit : "Cyber Crime Cell");
            lblTrackSubmitted.setText(r.createdAt != null ? r.createdAt : "N/A");
            lblTrackUpdated.setText(r.updatedAt != null ? r.updatedAt : "N/A");
            txtTrackRemarks.setText(r.investigatorRemarks != null && !r.investigatorRemarks.isBlank() ?
                r.investigatorRemarks : "No official investigator remarks added yet.");

            updateTimelineUI(r.status);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== 5. SUBMIT EVIDENCE MODULE ====================

    private JTextField txtEvReportId, txtEvName, txtEvFilePath, txtEvHash;
    private JComboBox<String> comboEvType;
    private JTextArea txtEvDesc;
    private DefaultTableModel userEvidenceModel;

    private JPanel createEvidenceModule() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(BG_LIGHT);
        root.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("SUBMIT DIGITAL EVIDENCE");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_MAIN);
        root.add(title, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridLayout(4, 2, 12, 10));
        form.setBackground(CARD_BG);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                " Evidence File Upload & SHA-256 Hashing ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), TEXT_MAIN
            ),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        txtEvReportId = new JTextField();
        txtEvName = new JTextField();
        comboEvType = new JComboBox<>(new String[]{
            "Screenshot", "Document", "Image", "Video", "Audio", "Transaction Record", "Other"
        });

        txtEvFilePath = new JTextField();
        txtEvFilePath.setEditable(false);
        JButton btnChoose = new JButton("Choose File");
        styleSecondaryButton(btnChoose);

        JPanel fileChooserPanel = new JPanel(new BorderLayout(5, 0));
        fileChooserPanel.setBackground(CARD_BG);
        fileChooserPanel.add(txtEvFilePath, BorderLayout.CENTER);
        fileChooserPanel.add(btnChoose, BorderLayout.EAST);

        txtEvHash = new JTextField("Select a file to compute SHA-256 hash...");
        txtEvHash.setEditable(false);
        txtEvHash.setFont(new Font("Monospaced", Font.BOLD, 12));
        txtEvHash.setForeground(ACCENT_BLUE);

        txtEvDesc = new JTextArea(2, 20);

        addFormField(form, "Report ID (e.g. CF-2026-0001) *", txtEvReportId);
        addFormField(form, "Evidence Name / Title *", txtEvName);
        addFormField(form, "Evidence Type *", comboEvType);
        addFormField(form, "Select File *", fileChooserPanel);
        addFormField(form, "Computed SHA-256 Hash (Immutable)", txtEvHash);
        addFormField(form, "Evidence Description", new JScrollPane(txtEvDesc));

        root.add(form, BorderLayout.NORTH);

        // Table of submitted evidence
        String[] cols = {"Report ID", "Evidence Name", "Type", "SHA-256 Hash", "Status"};
        userEvidenceModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblEvidence = new JTable(userEvidenceModel);
        tblEvidence.setRowHeight(28);

        JScrollPane tableScroll = new JScrollPane(tblEvidence);
        tableScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            " Submitted Evidence Records ",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 13), TEXT_MAIN
        ));
        root.add(tableScroll, BorderLayout.CENTER);

        // Submit Button
        JPanel btm = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btm.setBackground(BG_LIGHT);
        JButton btnSubmitEv = new JButton("Submit Digital Evidence");
        stylePrimaryButton(btnSubmitEv);
        btm.add(btnSubmitEv);
        root.add(btm, BorderLayout.SOUTH);

        btnChoose.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File sel = fc.getSelectedFile();
                txtEvFilePath.setText(sel.getAbsolutePath());
                if (txtEvName.getText().isBlank()) txtEvName.setText(sel.getName());
                try {
                    String hash = backend.sha256(sel.getAbsolutePath());
                    txtEvHash.setText(hash);
                } catch (Exception ex) {
                    txtEvHash.setText("Error computing hash: " + ex.getMessage());
                }
            }
        });

        btnSubmitEv.addActionListener(e -> submitUserEvidence());
        return root;
    }

    private void submitUserEvidence() {
        String reportId = txtEvReportId.getText().trim();
        String name = txtEvName.getText().trim();
        String type = String.valueOf(comboEvType.getSelectedItem());
        String filePath = txtEvFilePath.getText().trim();
        String hash = txtEvHash.getText().trim();
        String desc = txtEvDesc.getText().trim();

        if (reportId.isBlank() || name.isBlank() || filePath.isBlank() || hash.startsWith("Select") || hash.startsWith("Error")) {
            JOptionPane.showMessageDialog(this, "Please provide Report ID, Evidence Name, and choose a valid file.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            backend.addUserEvidence(reportId, name, type, desc, filePath, hash, currentUser.id);
            JOptionPane.showMessageDialog(this, "Evidence submitted successfully!\n\nImmutable SHA-256 Hash:\n" + hash, "Evidence Uploaded", JOptionPane.INFORMATION_MESSAGE);

            userEvidenceModel.addRow(new Object[]{reportId, name, type, hash.substring(0, Math.min(20, hash.length())) + "...", "Verified"});

            txtEvName.setText(""); txtEvFilePath.setText(""); txtEvHash.setText("Select a file..."); txtEvDesc.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== 6. NOTIFICATIONS MODULE ====================

    private DefaultTableModel notifModel;

    private JPanel createNotificationsModule() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(BG_LIGHT);
        root.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG_LIGHT);
        JLabel title = new JLabel("NOTIFICATIONS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_MAIN);

        JButton btnRead = new JButton("Mark All as Read");
        styleSecondaryButton(btnRead);
        btnRead.addActionListener(e -> {
            try {
                backend.markNotificationsRead(currentUser.id);
                loadNotifications();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        top.add(title, BorderLayout.WEST);
        top.add(btnRead, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        String[] cols = {"ID", "Report ID", "Message", "Timestamp"};
        notifModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblNotif = new JTable(notifModel);
        tblNotif.setRowHeight(30);

        root.add(new JScrollPane(tblNotif), BorderLayout.CENTER);
        return root;
    }

    private void loadNotifications() {
        try {
            notifModel.setRowCount(0);
            List<Backend.UserNotification> list = backend.getNotificationsByUser(currentUser.id);
            for (Backend.UserNotification n : list) {
                notifModel.addRow(new Object[]{n.id, n.reportId != null ? n.reportId : "System", n.message, n.createdAt});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== 7. PROFILE MODULE ====================

    private JTextField txtProfName, txtProfEmail, txtProfMobile;
    private JPasswordField txtOldPass, txtNewPass, txtConfirmPass;

    private JPanel createProfileModule() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(BG_LIGHT);
        root.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("USER PROFILE & ACCOUNT SETTINGS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_MAIN);
        root.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 20));
        content.setBackground(BG_LIGHT);

        // Edit Profile
        JPanel p1 = createFormSection("Edit User Profile");
        p1.setLayout(new GridLayout(4, 2, 10, 10));

        txtProfName = new JTextField(currentUser.fullName != null ? currentUser.fullName : "");
        txtProfEmail = new JTextField(currentUser.email != null ? currentUser.email : "");
        txtProfMobile = new JTextField(currentUser.mobileNumber != null ? currentUser.mobileNumber : "");
        JTextField txtProfUser = new JTextField(currentUser.username);
        txtProfUser.setEditable(false);

        addFormField(p1, "Username (Non-editable)", txtProfUser);
        addFormField(p1, "Full Name", txtProfName);
        addFormField(p1, "Email Address", txtProfEmail);
        addFormField(p1, "Mobile Number", txtProfMobile);

        JPanel p1Outer = new JPanel(new BorderLayout(10, 10));
        p1Outer.setBackground(BG_LIGHT);
        p1Outer.add(p1, BorderLayout.CENTER);

        JButton btnSaveProf = new JButton("Save Profile Changes");
        stylePrimaryButton(btnSaveProf);
        btnSaveProf.addActionListener(e -> updateProfile());
        p1Outer.add(btnSaveProf, BorderLayout.SOUTH);

        // Change Password
        JPanel p2 = createFormSection("Security & Password");
        p2.setLayout(new GridLayout(3, 2, 10, 10));

        txtOldPass = new JPasswordField();
        txtNewPass = new JPasswordField();
        txtConfirmPass = new JPasswordField();

        addFormField(p2, "Current Password *", txtOldPass);
        addFormField(p2, "New Password *", txtNewPass);
        addFormField(p2, "Confirm New Password *", txtConfirmPass);

        JPanel p2Outer = new JPanel(new BorderLayout(10, 10));
        p2Outer.setBackground(BG_LIGHT);
        p2Outer.add(p2, BorderLayout.CENTER);

        JButton btnChangePass = new JButton("Change Password");
        stylePrimaryButton(btnChangePass);
        btnChangePass.addActionListener(e -> changePassword());
        p2Outer.add(btnChangePass, BorderLayout.SOUTH);

        content.add(p1Outer);
        content.add(p2Outer);

        root.add(content, BorderLayout.CENTER);
        return root;
    }

    private void updateProfile() {
        try {
            String name = txtProfName.getText().trim();
            String email = txtProfEmail.getText().trim();
            String mobile = txtProfMobile.getText().trim();

            if (name.isBlank() || email.isBlank() || mobile.isBlank()) {
                throw new Exception("Full Name, Email, and Mobile Number cannot be blank.");
            }

            if (backend.updateProfile(currentUser.id, name, email, mobile)) {
                currentUser.fullName = name;
                currentUser.email = email;
                currentUser.mobileNumber = mobile;
                JOptionPane.showMessageDialog(this, "Profile updated successfully.", "Profile Updated", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changePassword() {
        try {
            String oldP = new String(txtOldPass.getPassword());
            String newP = new String(txtNewPass.getPassword());
            String confP = new String(txtConfirmPass.getPassword());

            if (oldP.isBlank() || newP.isBlank()) {
                throw new Exception("Please enter your current and new password.");
            }
            if (!newP.equals(confP)) {
                throw new Exception("New Password and Confirm Password do not match.");
            }

            if (backend.changePassword(currentUser.id, oldP, newP)) {
                JOptionPane.showMessageDialog(this, "Password changed successfully.", "Password Changed", JOptionPane.INFORMATION_MESSAGE);
                txtOldPass.setText(""); txtNewPass.setText(""); txtConfirmPass.setText("");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Button Styling Helpers
    private void stylePrimaryButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(TEXT_MAIN);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 18, 10, 18)
        ));
    }

    private void styleSecondaryButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(TEXT_MAIN);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
    }
}
