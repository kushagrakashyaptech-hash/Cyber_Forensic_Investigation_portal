import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class Frontend extends JFrame {
    private final Backend backend = new Backend();
    private static final Color BTN_WHITE = Color.WHITE;
    private static final Color TEXT_MAIN = new Color(15, 23, 42); // #0f172a
    private static final Color BORDER_COLOR = new Color(203, 213, 225); // #cbd5e1

    public Frontend() { showLogin(); }

    private void showLogin() {
        setTitle("Cyber Forensic Investigation Portal - Authentication");
        setSize(480, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("CYBER FORENSIC PORTAL", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel sub = new JLabel("Unified Sign-In (User & Administrator)", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel top = new JPanel(new GridLayout(2, 1, 4, 4));
        top.add(title); top.add(sub);
        root.add(top, BorderLayout.NORTH);

        JPanel p = new JPanel(new GridLayout(4, 2, 10, 12));
        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();
        JButton login = new JButton("LOGIN");
        JButton register = new JButton("REGISTER NEW USER");
        JButton forgot = new JButton("Forgot Password?");

        styleWhiteButton(login);
        styleWhiteButton(register);
        styleWhiteButton(forgot);

        p.add(new JLabel("Username / Email:")); p.add(user);
        p.add(new JLabel("Password:")); p.add(pass);
        p.add(register); p.add(login);
        p.add(new JLabel()); p.add(forgot);

        root.add(p, BorderLayout.CENTER);

        JLabel demoLbl = new JLabel("<html><center>Demo Admin: <b>admin / admin123</b> &nbsp;|&nbsp; Demo User: <b>user1 / user123</b></center></html>", SwingConstants.CENTER);
        demoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        root.add(demoLbl, BorderLayout.SOUTH);

        setContentPane(root);

        login.addActionListener(e -> {
            try {
                String u = user.getText().trim();
                String pStr = new String(pass.getPassword());
                if (u.isBlank() || pStr.isBlank()) {
                    JOptionPane.showMessageDialog(this, "Please enter username and password.", "Login Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Backend.UserSession session = backend.loginUser(u, pStr);
                if (session != null) {
                    this.dispose();
                    if ("ADMIN".equalsIgnoreCase(session.role)) {
                        adminDashboard(session.username);
                    } else {
                        SwingUtilities.invokeLater(() -> new UserPortal(backend, session).setVisible(true));
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password.", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) { error(ex); }
        });

        register.addActionListener(e -> showRegistrationDialog());
        forgot.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Password Reset Notice:\n\nPlease contact your Cyber Cell System Administrator to reset your credentials.\nDefault Admin: admin / admin123\nDefault User: user1 / user123",
            "Forgot Password Placeholder", JOptionPane.INFORMATION_MESSAGE));
    }

    private void showRegistrationDialog() {
        JDialog dlg = new JDialog(this, "New Citizen User Registration", true);
        dlg.setSize(440, 420);
        dlg.setLocationRelativeTo(this);

        JPanel p = new JPanel(new GridLayout(6, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JTextField mobile = new JTextField();
        JTextField uname = new JTextField();
        JPasswordField pass1 = new JPasswordField();
        JPasswordField pass2 = new JPasswordField();

        p.add(new JLabel("Full Name *:")); p.add(name);
        p.add(new JLabel("Email Address *:")); p.add(email);
        p.add(new JLabel("Mobile Number *:")); p.add(mobile);
        p.add(new JLabel("Username *:")); p.add(uname);
        p.add(new JLabel("Password *:")); p.add(pass1);
        p.add(new JLabel("Confirm Password *:")); p.add(pass2);

        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("CREATE USER ACCOUNT", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        root.add(title, BorderLayout.NORTH);
        root.add(p, BorderLayout.CENTER);

        JButton btnSubmit = new JButton("REGISTER");
        styleWhiteButton(btnSubmit);
        root.add(btnSubmit, BorderLayout.SOUTH);

        btnSubmit.addActionListener(e -> {
            try {
                String n = name.getText().trim();
                String em = email.getText().trim();
                String mob = mobile.getText().trim();
                String un = uname.getText().trim();
                String p1 = new String(pass1.getPassword());
                String p2 = new String(pass2.getPassword());

                if (n.isBlank() || em.isBlank() || mob.isBlank() || un.isBlank() || p1.isBlank()) {
                    throw new Exception("All fields are required.");
                }
                if (!em.contains("@") || !em.contains(".")) {
                    throw new Exception("Please enter a valid email address.");
                }
                if (!p1.equals(p2)) {
                    throw new Exception("Password and Confirm Password do not match.");
                }

                if (backend.registerUser(n, em, mob, un, p1)) {
                    JOptionPane.showMessageDialog(dlg, "Registration successful! You can now log in with your credentials.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dlg.dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.setContentPane(root);
        dlg.setVisible(true);
    }

    private void adminDashboard(String username) {
        setTitle("Cyber Forensic Portal - Administrator Dashboard");
        setSize(750, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        JLabel title = new JLabel("CYBER FORENSIC INVESTIGATION PORTAL");
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        JLabel welcome = new JLabel("Administrator Mode | Logged in as: " + username);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.NORTH);
        top.add(welcome, BorderLayout.SOUTH);

        JPanel buttons = new JPanel(new GridLayout(3, 2, 15, 15));
        JButton cases = new JButton("CASE MANAGEMENT");
        JButton evidence = new JButton("EVIDENCE MANAGEMENT");
        JButton citizenReports = new JButton("CITIZEN REPORTS MANAGEMENT");
        JButton timeline = new JButton("INVESTIGATION TIMELINE");
        JButton reports = new JButton("FORENSIC REPORTS");
        JButton logout = new JButton("LOGOUT");

        styleWhiteButton(cases);
        styleWhiteButton(evidence);
        styleWhiteButton(citizenReports);
        styleWhiteButton(timeline);
        styleWhiteButton(reports);
        
        // Keep Logout button Crimson Red
        logout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logout.setBackground(new Color(225, 29, 72));
        logout.setForeground(Color.WHITE);
        logout.setFocusPainted(false);
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttons.add(cases); buttons.add(evidence);
        buttons.add(citizenReports); buttons.add(timeline);
        buttons.add(reports); buttons.add(logout);

        root.add(top, BorderLayout.NORTH); root.add(buttons, BorderLayout.CENTER);
        root.add(new JLabel("Cyber Forensic Investigation Portal | Admin & Investigator Console", SwingConstants.CENTER), BorderLayout.SOUTH);
        setContentPane(root);

        cases.addActionListener(e -> caseWindow());
        evidence.addActionListener(e -> evidenceWindow());
        citizenReports.addActionListener(e -> citizenReportsWindow());
        timeline.addActionListener(e -> JOptionPane.showMessageDialog(this, "Timeline: Planned for full release phase."));
        reports.addActionListener(e -> JOptionPane.showMessageDialog(this, "Reports: Planned for full release phase."));
        logout.addActionListener(e -> {
            this.dispose();
            SwingUtilities.invokeLater(() -> new Frontend().setVisible(true));
        });

        setVisible(true);
        revalidate(); repaint();
    }

    private void caseWindow() {
        JFrame f = new JFrame("Case Management"); f.setSize(850, 520); f.setLocationRelativeTo(this);
        JPanel root = new JPanel(new BorderLayout(10, 10)); root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        JTextField no = new JTextField(), title = new JTextField(), inv = new JTextField(), desc = new JTextField();
        JButton add = new JButton("ADD CASE");
        styleWhiteButton(add);

        form.add(new JLabel("Case Number:")); form.add(no);
        form.add(new JLabel("Case Title:")); form.add(title);
        form.add(new JLabel("Investigator:")); form.add(inv);
        form.add(new JLabel("Description:")); form.add(desc);
        form.add(new JLabel()); form.add(add);

        DefaultTableModel m = new DefaultTableModel(new String[]{"Case Number", "Title", "Investigator", "Status"}, 0);
        JTable table = new JTable(m); table.setRowHeight(26);
        JButton refresh = new JButton("REFRESH");
        styleWhiteButton(refresh);

        JPanel top = new JPanel(new BorderLayout()); top.add(form, BorderLayout.CENTER); top.add(refresh, BorderLayout.SOUTH);
        root.add(top, BorderLayout.NORTH); root.add(new JScrollPane(table), BorderLayout.CENTER);

        Runnable load = () -> {
            try {
                m.setRowCount(0);
                for (String[] r : backend.getCases()) m.addRow(r);
            } catch (Exception ex) { error(ex); }
        };

        add.addActionListener(e -> {
            try {
                if (no.getText().isBlank() || title.getText().isBlank() || inv.getText().isBlank()) {
                    throw new Exception("Case number, title and investigator are required.");
                }
                backend.addCase(new Backend.CaseRecord(no.getText().trim(), title.getText().trim(), inv.getText().trim(), desc.getText().trim(), "Open"));
                JOptionPane.showMessageDialog(f, "Case added successfully.");
                no.setText(""); title.setText(""); inv.setText(""); desc.setText("");
                load.run();
            } catch (Exception ex) { error(ex); }
        });

        refresh.addActionListener(e -> load.run());
        f.setContentPane(root); f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); f.setVisible(true); load.run();
    }

    private void evidenceWindow() {
        JFrame f = new JFrame("Evidence Management"); f.setSize(1000, 550); f.setLocationRelativeTo(this);
        JPanel root = new JPanel(new BorderLayout(10, 10)); root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        JTextField cn = new JTextField(), name = new JTextField(), file = new JTextField(); file.setEditable(false);
        JComboBox<String> type = new JComboBox<>(new String[]{"Image", "Document", "Video", "Audio", "Log", "Other"});
        JButton choose = new JButton("CHOOSE FILE"), add = new JButton("ADD EVIDENCE");
        styleWhiteButton(choose);
        styleWhiteButton(add);

        JPanel fp = new JPanel(new BorderLayout(5, 0)); fp.add(file, BorderLayout.CENTER); fp.add(choose, BorderLayout.EAST);
        form.add(new JLabel("Case Number:")); form.add(cn);
        form.add(new JLabel("Evidence Name:")); form.add(name);
        form.add(new JLabel("Evidence Type:")); form.add(type);
        form.add(new JLabel("Evidence File:")); form.add(fp);
        form.add(new JLabel()); form.add(add);

        DefaultTableModel m = new DefaultTableModel(new String[]{"Case", "Evidence", "Type", "SHA-256", "Status"}, 0);
        JTable table = new JTable(m); table.setRowHeight(26);
        JButton refresh = new JButton("REFRESH");
        styleWhiteButton(refresh);

        JPanel top = new JPanel(new BorderLayout()); top.add(form, BorderLayout.CENTER); top.add(refresh, BorderLayout.SOUTH);
        root.add(top, BorderLayout.NORTH); root.add(new JScrollPane(table), BorderLayout.CENTER);

        choose.addActionListener(e -> {
            JFileChooser c = new JFileChooser();
            if (c.showOpenDialog(f) == JFileChooser.APPROVE_OPTION) {
                File x = c.getSelectedFile();
                file.setText(x.getAbsolutePath());
                if (name.getText().isBlank()) name.setText(x.getName());
            }
        });

        Runnable load = () -> {
            try {
                m.setRowCount(0);
                for (String[] r : backend.getEvidence()) {
                    if (r[3].length() > 18) r[3] = r[3].substring(0, 18) + "...";
                    m.addRow(r);
                }
            } catch (Exception ex) { error(ex); }
        };

        add.addActionListener(e -> {
            try {
                if (file.getText().isBlank()) throw new Exception("Choose an evidence file first.");
                String hash = backend.sha256(file.getText());
                backend.addEvidence(new Backend.EvidenceRecord(cn.getText().trim(), name.getText().trim(), String.valueOf(type.getSelectedItem()), file.getText().trim(), hash, "Verified"));
                JOptionPane.showMessageDialog(f, "Evidence saved!\n\nSHA-256:\n" + hash);
                cn.setText(""); name.setText(""); file.setText(""); load.run();
            } catch (Exception ex) { error(ex); }
        });

        refresh.addActionListener(e -> load.run());
        f.setContentPane(root); f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); f.setVisible(true); load.run();
    }

    private void citizenReportsWindow() {
        JFrame f = new JFrame("Admin Console - Citizen Cybercrime Reports Management");
        f.setSize(1050, 600);
        f.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Submitted Citizen Cybercrime Complaints & Report Tracking");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        root.add(title, BorderLayout.NORTH);

        String[] cols = {"Report ID", "Victim Name", "Category", "Amount (₹)", "Status", "Assigned Unit", "Date Submitted"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(m);
        table.setRowHeight(28);

        Runnable load = () -> {
            try {
                m.setRowCount(0);
                List<Backend.CybercrimeReport> reports = backend.getAllUserReports();
                for (Backend.CybercrimeReport r : reports) {
                    m.addRow(new Object[]{
                        r.reportId, r.victimName, r.crimeCategory,
                        (r.amountLost != null && !r.amountLost.isBlank() ? "₹" + r.amountLost : "N/A"),
                        r.status, r.assignedUnit,
                        r.createdAt != null ? r.createdAt.substring(0, Math.min(10, r.createdAt.length())) : "N/A"
                    });
                }
            } catch (Exception ex) { error(ex); }
        };

        root.add(new JScrollPane(table), BorderLayout.CENTER);

        // Control Panel to update report status
        JPanel pnlUpdate = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        pnlUpdate.setBorder(BorderFactory.createTitledBorder(" Update Selected Report Status & Investigation Unit "));

        JComboBox<String> comboStatus = new JComboBox<>(new String[]{
            "SUBMITTED", "UNDER REVIEW", "UNDER INVESTIGATION", "EVIDENCE ANALYSIS", "RESOLVED", "CLOSED"
        });
        JTextField txtUnit = new JTextField("Cyber Crime Cell Unit 1", 18);
        JTextField txtRemarks = new JTextField(20);
        JButton btnSave = new JButton("UPDATE STATUS");
        styleWhiteButton(btnSave);

        pnlUpdate.add(new JLabel("New Status:")); pnlUpdate.add(comboStatus);
        pnlUpdate.add(new JLabel("Assigned Unit:")); pnlUpdate.add(txtUnit);
        pnlUpdate.add(new JLabel("Remarks:")); pnlUpdate.add(txtRemarks);
        pnlUpdate.add(btnSave);

        root.add(pnlUpdate, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(f, "Please select a report from the table above.", "Select Report", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String reportId = (String) m.getValueAt(row, 0);
            String newStatus = String.valueOf(comboStatus.getSelectedItem());
            String unit = txtUnit.getText().trim();
            String remarks = txtRemarks.getText().trim();

            try {
                if (backend.updateReportStatus(reportId, newStatus, unit, remarks)) {
                    JOptionPane.showMessageDialog(f, "Report " + reportId + " status updated to '" + newStatus + "'. Notification sent to user.", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
                    load.run();
                }
            } catch (Exception ex) { error(ex); }
        });

        f.setContentPane(root);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setVisible(true);
        load.run();
    }

    private void styleWhiteButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(BTN_WHITE);
        btn.setForeground(TEXT_MAIN);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
    }

    private void error(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Frontend().setVisible(true));
    }
}
