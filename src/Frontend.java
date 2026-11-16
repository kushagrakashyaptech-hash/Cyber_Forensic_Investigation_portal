import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.Map;

public class Frontend extends JFrame implements ActionListener {
    Backend backend = new Backend();
    String adminUsername = "admin";

    Color backgroundColor = new Color(204, 229, 255);
    Color headerColor = new Color(255, 80, 80);
    Color sectionColor = new Color(0, 51, 102);
    Color blueColor = new Color(0, 102, 204);
    Color greenColor = new Color(0, 153, 76);
    Color orangeColor = new Color(255, 153, 51);
    Color greyColor = new Color(128, 128, 128);
    Color redColor = new Color(204, 0, 0);

    JLabel jLabelHeading;
    JLabel jLabelSubHeading;
    JLabel jLabelUsername;
    JLabel jLabelPassword;
    JTextField jTextFieldUsername;
    JPasswordField jPasswordFieldPassword;
    JButton jButtonLogin;
    JButton jButtonRegister;

    public Frontend() {
        setTitle("Cyber Forensic Portal - Login");
        setSize(500, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(backgroundColor);

        jLabelHeading = new JLabel("CYBER FORENSIC PORTAL", SwingConstants.CENTER);
        jLabelHeading.setBounds(0, 0, 500, 60);
        jLabelHeading.setFont(new Font("Arial", Font.BOLD, 24));
        jLabelHeading.setOpaque(true);
        jLabelHeading.setBackground(headerColor);
        jLabelHeading.setForeground(Color.BLACK);
        add(jLabelHeading);

        jLabelSubHeading = new JLabel("Login for User and Administrator", SwingConstants.CENTER);
        jLabelSubHeading.setBounds(0, 70, 500, 25);
        jLabelSubHeading.setFont(new Font("Arial", Font.BOLD, 14));
        add(jLabelSubHeading);

        jLabelUsername = new JLabel("Username / Email :");
        jLabelUsername.setBounds(40, 115, 160, 30);
        jLabelUsername.setFont(new Font("Arial", Font.BOLD, 14));
        add(jLabelUsername);

        jTextFieldUsername = new JTextField();
        jTextFieldUsername.setBounds(200, 115, 240, 30);
        jTextFieldUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        add(jTextFieldUsername);

        jLabelPassword = new JLabel("Password :");
        jLabelPassword.setBounds(40, 160, 160, 30);
        jLabelPassword.setFont(new Font("Arial", Font.BOLD, 14));
        add(jLabelPassword);

        jPasswordFieldPassword = new JPasswordField();
        jPasswordFieldPassword.setBounds(200, 160, 240, 30);
        add(jPasswordFieldPassword);

        jButtonLogin = new JButton("LOGIN");
        jButtonLogin.setBounds(40, 215, 190, 45);
        jButtonLogin.setFont(new Font("Arial", Font.BOLD, 16));
        jButtonLogin.setBackground(greenColor);
        jButtonLogin.setForeground(Color.WHITE);
        jButtonLogin.addActionListener(this);
        add(jButtonLogin);

        jButtonRegister = new JButton("REGISTER");
        jButtonRegister.setBounds(250, 215, 190, 45);
        jButtonRegister.setFont(new Font("Arial", Font.BOLD, 16));
        jButtonRegister.setBackground(blueColor);
        jButtonRegister.setForeground(Color.WHITE);
        jButtonRegister.addActionListener(this);
        add(jButtonRegister);


    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jButtonLogin) {
            try {
                String username = jTextFieldUsername.getText().trim();
                String password = new String(jPasswordFieldPassword.getPassword());
                if (username.isBlank() || password.isBlank()) {
                    JOptionPane.showMessageDialog(this, "Please enter username and password.", "Login Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Backend.UserSession session = backend.loginUser(username, password);
                if (session != null) {
                    dispose();
                    if ("ADMIN".equalsIgnoreCase(session.role)) {
                        adminDashboard(session.username);
                    } else {
                        new UserPortal(backend, session).setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password.", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (e.getSource() == jButtonRegister) {
            showRegistrationDialog();
        }
    }

    void showRegistrationDialog() {
        JDialog jDialogRegister = new JDialog(this, "New User Registration", true);
        jDialogRegister.setSize(450, 470);
        jDialogRegister.setLocationRelativeTo(this);
        jDialogRegister.setResizable(false);
        jDialogRegister.setLayout(null);
        jDialogRegister.getContentPane().setBackground(backgroundColor);

        JLabel jLabelRegisterHeading = new JLabel("CREATE NEW ACCOUNT", SwingConstants.CENTER);
        jLabelRegisterHeading.setBounds(0, 0, 450, 50);
        jLabelRegisterHeading.setFont(new Font("Arial", Font.BOLD, 20));
        jLabelRegisterHeading.setOpaque(true);
        jLabelRegisterHeading.setBackground(headerColor);
        jLabelRegisterHeading.setForeground(Color.BLACK);
        jDialogRegister.add(jLabelRegisterHeading);

        JLabel jLabelFullName = new JLabel("Full Name * :");
        jLabelFullName.setBounds(30, 70, 160, 30);
        jLabelFullName.setFont(new Font("Arial", Font.BOLD, 14));
        jDialogRegister.add(jLabelFullName);
        JTextField jTextFieldFullName = new JTextField();
        jTextFieldFullName.setBounds(190, 70, 220, 30);
        jDialogRegister.add(jTextFieldFullName);

        JLabel jLabelEmail = new JLabel("Email Address * :");
        jLabelEmail.setBounds(30, 115, 160, 30);
        jLabelEmail.setFont(new Font("Arial", Font.BOLD, 14));
        jDialogRegister.add(jLabelEmail);
        JTextField jTextFieldEmail = new JTextField();
        jTextFieldEmail.setBounds(190, 115, 220, 30);
        jDialogRegister.add(jTextFieldEmail);

        JLabel jLabelMobile = new JLabel("Mobile Number * :");
        jLabelMobile.setBounds(30, 160, 160, 30);
        jLabelMobile.setFont(new Font("Arial", Font.BOLD, 14));
        jDialogRegister.add(jLabelMobile);
        JTextField jTextFieldMobile = new JTextField();
        jTextFieldMobile.setBounds(190, 160, 220, 30);
        jDialogRegister.add(jTextFieldMobile);

        JLabel jLabelNewUsername = new JLabel("Username * :");
        jLabelNewUsername.setBounds(30, 205, 160, 30);
        jLabelNewUsername.setFont(new Font("Arial", Font.BOLD, 14));
        jDialogRegister.add(jLabelNewUsername);
        JTextField jTextFieldNewUsername = new JTextField();
        jTextFieldNewUsername.setBounds(190, 205, 220, 30);
        jDialogRegister.add(jTextFieldNewUsername);

        JLabel jLabelNewPassword = new JLabel("Password * :");
        jLabelNewPassword.setBounds(30, 250, 160, 30);
        jLabelNewPassword.setFont(new Font("Arial", Font.BOLD, 14));
        jDialogRegister.add(jLabelNewPassword);
        JPasswordField jPasswordFieldNewPassword = new JPasswordField();
        jPasswordFieldNewPassword.setBounds(190, 250, 220, 30);
        jDialogRegister.add(jPasswordFieldNewPassword);

        JLabel jLabelConfirmPassword = new JLabel("Confirm Password * :");
        jLabelConfirmPassword.setBounds(30, 295, 160, 30);
        jLabelConfirmPassword.setFont(new Font("Arial", Font.BOLD, 14));
        jDialogRegister.add(jLabelConfirmPassword);
        JPasswordField jPasswordFieldConfirmPassword = new JPasswordField();
        jPasswordFieldConfirmPassword.setBounds(190, 295, 220, 30);
        jDialogRegister.add(jPasswordFieldConfirmPassword);

        JButton jButtonSubmitRegister = new JButton("REGISTER");
        jButtonSubmitRegister.setBounds(30, 350, 180, 45);
        jButtonSubmitRegister.setFont(new Font("Arial", Font.BOLD, 15));
        jButtonSubmitRegister.setBackground(greenColor);
        jButtonSubmitRegister.setForeground(Color.WHITE);
        jDialogRegister.add(jButtonSubmitRegister);

        JButton jButtonCancel = new JButton("CANCEL");
        jButtonCancel.setBounds(230, 350, 180, 45);
        jButtonCancel.setFont(new Font("Arial", Font.BOLD, 15));
        jButtonCancel.setBackground(greyColor);
        jButtonCancel.setForeground(Color.WHITE);
        jDialogRegister.add(jButtonCancel);

        jButtonSubmitRegister.addActionListener(e -> {
            try {
                String fullName = jTextFieldFullName.getText().trim();
                String email = jTextFieldEmail.getText().trim();
                String mobile = jTextFieldMobile.getText().trim();
                String username = jTextFieldNewUsername.getText().trim();
                String password = new String(jPasswordFieldNewPassword.getPassword());
                String confirmPassword = new String(jPasswordFieldConfirmPassword.getPassword());

                if (fullName.isBlank() || email.isBlank() || mobile.isBlank() || username.isBlank() || password.isBlank()) {
                    throw new Exception("All fields are required.");
                }
                if (!email.contains("@") || !email.contains(".")) {
                    throw new Exception("Please enter a valid email address.");
                }
                if (!password.equals(confirmPassword)) {
                    throw new Exception("Password and Confirm Password do not match.");
                }
                if (backend.registerUser(fullName, email, mobile, username, password)) {
                    JOptionPane.showMessageDialog(jDialogRegister, "Registration successful! You can now log in with your credentials.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    jDialogRegister.dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(jDialogRegister, ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        jButtonCancel.addActionListener(e -> jDialogRegister.dispose());

        jDialogRegister.setVisible(true);
    }

    void adminDashboard(String username) {
        adminUsername = username;
        JFrame jFrameAdmin = new JFrame("Cyber Forensic Portal - Admin Dashboard");
        jFrameAdmin.setSize(700, 520);
        jFrameAdmin.setLocationRelativeTo(null);
        jFrameAdmin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrameAdmin.setResizable(false);
        jFrameAdmin.setLayout(null);
        jFrameAdmin.getContentPane().setBackground(backgroundColor);

        JLabel jLabelAdminHeading = new JLabel("CYBER FORENSIC PORTAL - ADMIN", SwingConstants.CENTER);
        jLabelAdminHeading.setBounds(0, 0, 700, 60);
        jLabelAdminHeading.setFont(new Font("Arial", Font.BOLD, 24));
        jLabelAdminHeading.setOpaque(true);
        jLabelAdminHeading.setBackground(headerColor);
        jLabelAdminHeading.setForeground(Color.BLACK);
        jFrameAdmin.add(jLabelAdminHeading);

        JLabel jLabelWelcome = new JLabel("Welcome Admin : " + username);
        jLabelWelcome.setBounds(40, 70, 400, 25);
        jLabelWelcome.setFont(new Font("Arial", Font.BOLD, 16));
        jFrameAdmin.add(jLabelWelcome);

        JButton jButtonCaseManagement = new JButton("CASE MANAGEMENT");
        jButtonCaseManagement.setBounds(40, 110, 300, 70);
        jButtonCaseManagement.setFont(new Font("Arial", Font.BOLD, 16));
        jButtonCaseManagement.setBackground(blueColor);
        jButtonCaseManagement.setForeground(Color.WHITE);
        jFrameAdmin.add(jButtonCaseManagement);

        JButton jButtonEvidenceManagement = new JButton("EVIDENCE MANAGEMENT");
        jButtonEvidenceManagement.setBounds(360, 110, 300, 70);
        jButtonEvidenceManagement.setFont(new Font("Arial", Font.BOLD, 16));
        jButtonEvidenceManagement.setBackground(blueColor);
        jButtonEvidenceManagement.setForeground(Color.WHITE);
        jFrameAdmin.add(jButtonEvidenceManagement);

        JButton jButtonCitizenReports = new JButton("CITIZEN REPORTS");
        jButtonCitizenReports.setBounds(40, 195, 300, 70);
        jButtonCitizenReports.setFont(new Font("Arial", Font.BOLD, 16));
        jButtonCitizenReports.setBackground(blueColor);
        jButtonCitizenReports.setForeground(Color.WHITE);
        jFrameAdmin.add(jButtonCitizenReports);

        JButton jButtonTimeline = new JButton("INVESTIGATION TIMELINE");
        jButtonTimeline.setBounds(360, 195, 300, 70);
        jButtonTimeline.setFont(new Font("Arial", Font.BOLD, 16));
        jButtonTimeline.setBackground(blueColor);
        jButtonTimeline.setForeground(Color.WHITE);
        jFrameAdmin.add(jButtonTimeline);

        JButton jButtonForensicReports = new JButton("FORENSIC REPORTS");
        jButtonForensicReports.setBounds(40, 280, 300, 70);
        jButtonForensicReports.setFont(new Font("Arial", Font.BOLD, 16));
        jButtonForensicReports.setBackground(blueColor);
        jButtonForensicReports.setForeground(Color.WHITE);
        jFrameAdmin.add(jButtonForensicReports);

        JButton jButtonLogout = new JButton("LOGOUT");
        jButtonLogout.setBounds(360, 280, 300, 70);
        jButtonLogout.setFont(new Font("Arial", Font.BOLD, 16));
        jButtonLogout.setBackground(redColor);
        jButtonLogout.setForeground(Color.WHITE);
        jFrameAdmin.add(jButtonLogout);


        jButtonCaseManagement.addActionListener(e -> caseWindow(jFrameAdmin));
        jButtonEvidenceManagement.addActionListener(e -> evidenceWindow(jFrameAdmin));
        jButtonCitizenReports.addActionListener(e -> citizenReportsWindow(jFrameAdmin));
        jButtonTimeline.addActionListener(e -> timelineWindow(jFrameAdmin));
        jButtonForensicReports.addActionListener(e -> forensicReportsWindow(jFrameAdmin));
        jButtonLogout.addActionListener(e -> {
            for (Window window : Window.getWindows()) {
                window.dispose();
            }
            new Frontend().setVisible(true);
        });

        jFrameAdmin.setVisible(true);
    }

    void caseWindow(JFrame parent) {
        JFrame jFrameCase = new JFrame("Case Management");
        jFrameCase.setSize(850, 600);
        jFrameCase.setLocationRelativeTo(parent);
        jFrameCase.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrameCase.setLayout(null);
        jFrameCase.getContentPane().setBackground(backgroundColor);

        JLabel jLabelCaseHeading = new JLabel("CASE MANAGEMENT", SwingConstants.CENTER);
        jLabelCaseHeading.setBounds(0, 0, 850, 50);
        jLabelCaseHeading.setFont(new Font("Arial", Font.BOLD, 22));
        jLabelCaseHeading.setOpaque(true);
        jLabelCaseHeading.setBackground(headerColor);
        jLabelCaseHeading.setForeground(Color.BLACK);
        jFrameCase.add(jLabelCaseHeading);

        JLabel jLabelCaseNumber = new JLabel("Case Number :");
        jLabelCaseNumber.setBounds(30, 70, 140, 30);
        jLabelCaseNumber.setFont(new Font("Arial", Font.BOLD, 14));
        jFrameCase.add(jLabelCaseNumber);
        JTextField jTextFieldCaseNumber = new JTextField();
        jTextFieldCaseNumber.setBounds(180, 70, 250, 30);
        jFrameCase.add(jTextFieldCaseNumber);

        JLabel jLabelCaseTitle = new JLabel("Case Title :");
        jLabelCaseTitle.setBounds(30, 110, 140, 30);
        jLabelCaseTitle.setFont(new Font("Arial", Font.BOLD, 14));
        jFrameCase.add(jLabelCaseTitle);
        JTextField jTextFieldCaseTitle = new JTextField();
        jTextFieldCaseTitle.setBounds(180, 110, 250, 30);
        jFrameCase.add(jTextFieldCaseTitle);

        JLabel jLabelInvestigator = new JLabel("Investigator :");
        jLabelInvestigator.setBounds(30, 150, 140, 30);
        jLabelInvestigator.setFont(new Font("Arial", Font.BOLD, 14));
        jFrameCase.add(jLabelInvestigator);
        JTextField jTextFieldInvestigator = new JTextField();
        jTextFieldInvestigator.setBounds(180, 150, 250, 30);
        jFrameCase.add(jTextFieldInvestigator);

        JLabel jLabelDescription = new JLabel("Description :");
        jLabelDescription.setBounds(30, 190, 140, 30);
        jLabelDescription.setFont(new Font("Arial", Font.BOLD, 14));
        jFrameCase.add(jLabelDescription);
        JTextField jTextFieldDescription = new JTextField();
        jTextFieldDescription.setBounds(180, 190, 250, 30);
        jFrameCase.add(jTextFieldDescription);

        JButton jButtonAddCase = new JButton("ADD CASE");
        jButtonAddCase.setBounds(480, 70, 170, 45);
        jButtonAddCase.setFont(new Font("Arial", Font.BOLD, 15));
        jButtonAddCase.setBackground(greenColor);
        jButtonAddCase.setForeground(Color.WHITE);
        jFrameCase.add(jButtonAddCase);

        JButton jButtonRefresh = new JButton("REFRESH");
        jButtonRefresh.setBounds(480, 130, 170, 45);
        jButtonRefresh.setFont(new Font("Arial", Font.BOLD, 15));
        jButtonRefresh.setBackground(orangeColor);
        jButtonRefresh.setForeground(Color.BLACK);
        jFrameCase.add(jButtonRefresh);

        JButton jButtonClose = new JButton("CLOSE");
        jButtonClose.setBounds(670, 70, 140, 45);
        jButtonClose.setFont(new Font("Arial", Font.BOLD, 15));
        jButtonClose.setBackground(greyColor);
        jButtonClose.setForeground(Color.WHITE);
        jFrameCase.add(jButtonClose);

        DefaultTableModel defaultTableModelCases = new DefaultTableModel(new String[]{"Case Number", "Title", "Investigator", "Status"}, 0);
        JTable jTableCases = new JTable(defaultTableModelCases);
        jTableCases.setRowHeight(25);
        JScrollPane jScrollPaneCases = new JScrollPane(jTableCases);
        jScrollPaneCases.setBounds(30, 240, 780, 300);
        jFrameCase.add(jScrollPaneCases);

        Runnable loadCases = () -> {
            try {
                defaultTableModelCases.setRowCount(0);
                for (String[] row : backend.getCases()) {
                    defaultTableModelCases.addRow(row);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(jFrameCase, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        jButtonAddCase.addActionListener(e -> {
            try {
                if (jTextFieldCaseNumber.getText().isBlank() || jTextFieldCaseTitle.getText().isBlank() || jTextFieldInvestigator.getText().isBlank()) {
                    throw new Exception("Case number, title and investigator are required.");
                }
                backend.addCase(new Backend.CaseRecord(jTextFieldCaseNumber.getText().trim(), jTextFieldCaseTitle.getText().trim(), jTextFieldInvestigator.getText().trim(), jTextFieldDescription.getText().trim(), "Open"));
                JOptionPane.showMessageDialog(jFrameCase, "Case added successfully.");
                jTextFieldCaseNumber.setText("");
                jTextFieldCaseTitle.setText("");
                jTextFieldInvestigator.setText("");
                jTextFieldDescription.setText("");
                loadCases.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(jFrameCase, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        jButtonRefresh.addActionListener(e -> loadCases.run());
        jButtonClose.addActionListener(e -> jFrameCase.dispose());

        jFrameCase.setVisible(true);
        loadCases.run();
    }

    void evidenceWindow(JFrame parent) {
        JFrame jFrameEvidence = new JFrame("Evidence Management");
        jFrameEvidence.setSize(950, 620);
        jFrameEvidence.setLocationRelativeTo(parent);
        jFrameEvidence.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrameEvidence.setLayout(null);
        jFrameEvidence.getContentPane().setBackground(backgroundColor);

        JLabel jLabelEvidenceHeading = new JLabel("EVIDENCE MANAGEMENT", SwingConstants.CENTER);
        jLabelEvidenceHeading.setBounds(0, 0, 950, 50);
        jLabelEvidenceHeading.setFont(new Font("Arial", Font.BOLD, 22));
        jLabelEvidenceHeading.setOpaque(true);
        jLabelEvidenceHeading.setBackground(headerColor);
        jLabelEvidenceHeading.setForeground(Color.BLACK);
        jFrameEvidence.add(jLabelEvidenceHeading);

        JLabel jLabelCaseNumber = new JLabel("Case Number :");
        jLabelCaseNumber.setBounds(30, 70, 140, 30);
        jLabelCaseNumber.setFont(new Font("Arial", Font.BOLD, 14));
        jFrameEvidence.add(jLabelCaseNumber);
        JTextField jTextFieldCaseNumber = new JTextField();
        jTextFieldCaseNumber.setBounds(180, 70, 250, 30);
        jFrameEvidence.add(jTextFieldCaseNumber);

        JLabel jLabelEvidenceName = new JLabel("Evidence Name :");
        jLabelEvidenceName.setBounds(30, 110, 140, 30);
        jLabelEvidenceName.setFont(new Font("Arial", Font.BOLD, 14));
        jFrameEvidence.add(jLabelEvidenceName);
        JTextField jTextFieldEvidenceName = new JTextField();
        jTextFieldEvidenceName.setBounds(180, 110, 250, 30);
        jFrameEvidence.add(jTextFieldEvidenceName);

        JLabel jLabelEvidenceType = new JLabel("Evidence Type :");
        jLabelEvidenceType.setBounds(30, 150, 140, 30);
        jLabelEvidenceType.setFont(new Font("Arial", Font.BOLD, 14));
        jFrameEvidence.add(jLabelEvidenceType);
        JComboBox<String> jComboBoxEvidenceType = new JComboBox<>(new String[]{"Image", "Document", "Video", "Audio", "Log", "Other"});
        jComboBoxEvidenceType.setBounds(180, 150, 250, 30);
        jFrameEvidence.add(jComboBoxEvidenceType);

        JLabel jLabelEvidenceFile = new JLabel("Evidence File :");
        jLabelEvidenceFile.setBounds(30, 190, 140, 30);
        jLabelEvidenceFile.setFont(new Font("Arial", Font.BOLD, 14));
        jFrameEvidence.add(jLabelEvidenceFile);
        JTextField jTextFieldEvidenceFile = new JTextField();
        jTextFieldEvidenceFile.setBounds(180, 190, 250, 30);
        jTextFieldEvidenceFile.setEditable(false);
        jFrameEvidence.add(jTextFieldEvidenceFile);

        JButton jButtonChooseFile = new JButton("CHOOSE FILE");
        jButtonChooseFile.setBounds(440, 190, 140, 30);
        jButtonChooseFile.setFont(new Font("Arial", Font.BOLD, 13));
        jButtonChooseFile.setBackground(orangeColor);
        jButtonChooseFile.setForeground(Color.BLACK);
        jFrameEvidence.add(jButtonChooseFile);

        JButton jButtonAddEvidence = new JButton("ADD EVIDENCE");
        jButtonAddEvidence.setBounds(620, 70, 170, 45);
        jButtonAddEvidence.setFont(new Font("Arial", Font.BOLD, 15));
        jButtonAddEvidence.setBackground(greenColor);
        jButtonAddEvidence.setForeground(Color.WHITE);
        jFrameEvidence.add(jButtonAddEvidence);

        JButton jButtonRefresh = new JButton("REFRESH");
        jButtonRefresh.setBounds(620, 130, 170, 45);
        jButtonRefresh.setFont(new Font("Arial", Font.BOLD, 15));
        jButtonRefresh.setBackground(orangeColor);
        jButtonRefresh.setForeground(Color.BLACK);
        jFrameEvidence.add(jButtonRefresh);

        JButton jButtonClose = new JButton("CLOSE");
        jButtonClose.setBounds(800, 70, 120, 45);
        jButtonClose.setFont(new Font("Arial", Font.BOLD, 15));
        jButtonClose.setBackground(greyColor);
        jButtonClose.setForeground(Color.WHITE);
        jFrameEvidence.add(jButtonClose);

        DefaultTableModel defaultTableModelEvidence = new DefaultTableModel(new String[]{"Case", "Evidence", "Type", "SHA-256", "Status"}, 0);
        JTable jTableEvidence = new JTable(defaultTableModelEvidence);
        jTableEvidence.setRowHeight(25);
        JScrollPane jScrollPaneEvidence = new JScrollPane(jTableEvidence);
        jScrollPaneEvidence.setBounds(30, 245, 880, 310);
        jFrameEvidence.add(jScrollPaneEvidence);

        jButtonChooseFile.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            if (jFileChooser.showOpenDialog(jFrameEvidence) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = jFileChooser.getSelectedFile();
                jTextFieldEvidenceFile.setText(selectedFile.getAbsolutePath());
                if (jTextFieldEvidenceName.getText().isBlank()) {
                    jTextFieldEvidenceName.setText(selectedFile.getName());
                }
            }
        });

        Runnable loadEvidence = () -> {
            try {
                defaultTableModelEvidence.setRowCount(0);
                for (String[] row : backend.getEvidence()) {
                    if (row[3].length() > 18) {
                        row[3] = row[3].substring(0, 18) + "...";
                    }
                    defaultTableModelEvidence.addRow(row);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(jFrameEvidence, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        jButtonAddEvidence.addActionListener(e -> {
            try {
                if (jTextFieldEvidenceFile.getText().isBlank()) {
                    throw new Exception("Choose an evidence file first.");
                }
                String hash = backend.sha256(jTextFieldEvidenceFile.getText());
                backend.addEvidence(new Backend.EvidenceRecord(jTextFieldCaseNumber.getText().trim(), jTextFieldEvidenceName.getText().trim(), String.valueOf(jComboBoxEvidenceType.getSelectedItem()), jTextFieldEvidenceFile.getText().trim(), hash, "Verified"));
                JOptionPane.showMessageDialog(jFrameEvidence, "Evidence saved!\n\nSHA-256:\n" + hash);
                jTextFieldCaseNumber.setText("");
                jTextFieldEvidenceName.setText("");
                jTextFieldEvidenceFile.setText("");
                loadEvidence.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(jFrameEvidence, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        jButtonRefresh.addActionListener(e -> loadEvidence.run());
        jButtonClose.addActionListener(e -> jFrameEvidence.dispose());

        jFrameEvidence.setVisible(true);
        loadEvidence.run();
    }

    void citizenReportsWindow(JFrame parent) {
        JFrame jFrameReports = new JFrame("Citizen Cybercrime Reports");
        jFrameReports.setSize(1050, 620);
        jFrameReports.setLocationRelativeTo(parent);
        jFrameReports.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrameReports.setLayout(null);
        jFrameReports.getContentPane().setBackground(backgroundColor);

        JLabel jLabelReportsHeading = new JLabel("CITIZEN CYBERCRIME REPORTS", SwingConstants.CENTER);
        jLabelReportsHeading.setBounds(0, 0, 1050, 50);
        jLabelReportsHeading.setFont(new Font("Arial", Font.BOLD, 22));
        jLabelReportsHeading.setOpaque(true);
        jLabelReportsHeading.setBackground(headerColor);
        jLabelReportsHeading.setForeground(Color.BLACK);
        jFrameReports.add(jLabelReportsHeading);

        String[] columns = {"Report ID", "Victim Name", "Category", "Amount (Rs)", "Status", "Assigned Unit", "Date Submitted"};
        DefaultTableModel defaultTableModelReports = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable jTableReports = new JTable(defaultTableModelReports);
        jTableReports.setRowHeight(25);
        JScrollPane jScrollPaneReports = new JScrollPane(jTableReports);
        jScrollPaneReports.setBounds(20, 60, 995, 270);
        jFrameReports.add(jScrollPaneReports);

        JButton jButtonViewFullReport = new JButton("VIEW FULL REPORT");
        jButtonViewFullReport.setBounds(20, 340, 220, 40);
        jButtonViewFullReport.setFont(new Font("Arial", Font.BOLD, 15));
        jButtonViewFullReport.setBackground(blueColor);
        jButtonViewFullReport.setForeground(Color.WHITE);
        jFrameReports.add(jButtonViewFullReport);

        JLabel jLabelUpdate = new JLabel("Update Selected Report :");
        jLabelUpdate.setBounds(20, 395, 300, 25);
        jLabelUpdate.setFont(new Font("Arial", Font.BOLD, 16));
        jFrameReports.add(jLabelUpdate);

        JLabel jLabelNewStatus = new JLabel("New Status :");
        jLabelNewStatus.setBounds(20, 435, 110, 30);
        jLabelNewStatus.setFont(new Font("Arial", Font.BOLD, 14));
        jFrameReports.add(jLabelNewStatus);
        JComboBox<String> jComboBoxStatus = new JComboBox<>(new String[]{
            "SUBMITTED", "UNDER REVIEW", "UNDER INVESTIGATION", "EVIDENCE ANALYSIS", "RESOLVED", "CLOSED"
        });
        jComboBoxStatus.setBounds(130, 435, 200, 30);
        jFrameReports.add(jComboBoxStatus);

        JLabel jLabelAssignedUnit = new JLabel("Assigned Unit :");
        jLabelAssignedUnit.setBounds(350, 435, 120, 30);
        jLabelAssignedUnit.setFont(new Font("Arial", Font.BOLD, 14));
        jFrameReports.add(jLabelAssignedUnit);
        JTextField jTextFieldAssignedUnit = new JTextField("Cyber Crime Cell Unit 1");
        jTextFieldAssignedUnit.setBounds(470, 435, 220, 30);
        jFrameReports.add(jTextFieldAssignedUnit);

        JLabel jLabelRemarks = new JLabel("Remarks :");
        jLabelRemarks.setBounds(20, 480, 110, 30);
        jLabelRemarks.setFont(new Font("Arial", Font.BOLD, 14));
        jFrameReports.add(jLabelRemarks);
        JTextField jTextFieldRemarks = new JTextField();
        jTextFieldRemarks.setBounds(130, 480, 560, 30);
        jFrameReports.add(jTextFieldRemarks);

        JButton jButtonUpdateStatus = new JButton("UPDATE STATUS");
        jButtonUpdateStatus.setBounds(720, 430, 180, 45);
        jButtonUpdateStatus.setFont(new Font("Arial", Font.BOLD, 15));
        jButtonUpdateStatus.setBackground(greenColor);
        jButtonUpdateStatus.setForeground(Color.WHITE);
        jFrameReports.add(jButtonUpdateStatus);

        JButton jButtonRefresh = new JButton("REFRESH");
        jButtonRefresh.setBounds(720, 485, 180, 40);
        jButtonRefresh.setFont(new Font("Arial", Font.BOLD, 15));
        jButtonRefresh.setBackground(orangeColor);
        jButtonRefresh.setForeground(Color.BLACK);
        jFrameReports.add(jButtonRefresh);

        JButton jButtonClose = new JButton("CLOSE");
        jButtonClose.setBounds(910, 430, 105, 45);
        jButtonClose.setFont(new Font("Arial", Font.BOLD, 15));
        jButtonClose.setBackground(greyColor);
        jButtonClose.setForeground(Color.WHITE);
        jFrameReports.add(jButtonClose);

        Runnable loadReports = () -> {
            try {
                defaultTableModelReports.setRowCount(0);
                List<Backend.CybercrimeReport> reports = backend.getAllUserReports();
                for (Backend.CybercrimeReport report : reports) {
                    String amount = "N/A";
                    if (report.amountLost != null && !report.amountLost.isBlank()) {
                        amount = "Rs. " + report.amountLost;
                    }
                    String date = "N/A";
                    if (report.createdAt != null) {
                        date = report.createdAt.substring(0, Math.min(10, report.createdAt.length()));
                    }
                    defaultTableModelReports.addRow(new Object[]{
                        report.reportId, report.victimName, report.crimeCategory, amount, report.status, report.assignedUnit, date
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(jFrameReports, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        jButtonUpdateStatus.addActionListener(e -> {
            int selectedRow = jTableReports.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(jFrameReports, "Please select a report from the table above.", "Select Report", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String reportId = (String) defaultTableModelReports.getValueAt(selectedRow, 0);
            String newStatus = String.valueOf(jComboBoxStatus.getSelectedItem());
            String assignedUnit = jTextFieldAssignedUnit.getText().trim();
            String remarks = jTextFieldRemarks.getText().trim();
            try {
                if (backend.updateReportStatus(reportId, newStatus, assignedUnit, remarks, adminUsername)) {
                    JOptionPane.showMessageDialog(jFrameReports, "Report " + reportId + " status updated to '" + newStatus + "'. Notification sent to user.", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
                    loadReports.run();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(jFrameReports, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        jButtonViewFullReport.addActionListener(e -> {
            int selectedRow = jTableReports.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(jFrameReports, "Please select a report from the table above.", "Select Report", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String reportId = (String) defaultTableModelReports.getValueAt(selectedRow, 0);
            try {
                Backend.CybercrimeReport report = backend.getReportById(reportId);
                if (report != null) {
                    showFullReportDialog(jFrameReports, report);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(jFrameReports, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        jButtonRefresh.addActionListener(e -> loadReports.run());
        jButtonClose.addActionListener(e -> jFrameReports.dispose());

        jFrameReports.setVisible(true);
        loadReports.run();
    }

    String showValue(String value) {
        if (value == null || value.isBlank()) {
            return "N/A";
        }
        return value;
    }

    void showFullReportDialog(JFrame parent, Backend.CybercrimeReport report) {
        JDialog jDialogFullReport = new JDialog(parent, "Full Complaint Report - " + report.reportId, true);
        jDialogFullReport.setSize(820, 690);
        jDialogFullReport.setLocationRelativeTo(parent);
        jDialogFullReport.setLayout(null);
        jDialogFullReport.getContentPane().setBackground(backgroundColor);

        JLabel jLabelFullReportHeading = new JLabel("COMPLAINT REPORT : " + report.reportId, SwingConstants.CENTER);
        jLabelFullReportHeading.setBounds(0, 0, 820, 50);
        jLabelFullReportHeading.setFont(new Font("Arial", Font.BOLD, 22));
        jLabelFullReportHeading.setOpaque(true);
        jLabelFullReportHeading.setBackground(headerColor);
        jLabelFullReportHeading.setForeground(Color.BLACK);
        jDialogFullReport.add(jLabelFullReportHeading);

        String details = "";
        details = details + "--- CASE STATUS ---\n";
        details = details + "Report ID             : " + report.reportId + "\n";
        details = details + "Current Status        : " + showValue(report.status) + "\n";
        details = details + "Assigned Unit         : " + showValue(report.assignedUnit) + "\n";
        details = details + "Submitted On          : " + showValue(report.createdAt) + "\n";
        details = details + "Last Updated          : " + showValue(report.updatedAt) + "\n\n";
        details = details + "--- COMPLAINANT DETAILS ---\n";
        details = details + "Victim Name           : " + showValue(report.victimName) + "\n";
        details = details + "Email                 : " + showValue(report.email) + "\n";
        details = details + "Mobile Number         : " + showValue(report.mobileNumber) + "\n\n";
        details = details + "--- INCIDENT DETAILS ---\n";
        details = details + "Crime Category        : " + showValue(report.crimeCategory) + "\n";
        details = details + "Incident Date         : " + showValue(report.incidentDate) + "\n";
        details = details + "Incident Location     : " + showValue(report.location) + "\n";
        details = details + "Amount Lost (Rs)      : " + showValue(report.amountLost) + "\n\n";
        details = details + "Incident Description :\n" + showValue(report.description) + "\n\n";
        details = details + "--- SUSPECT / DIGITAL IDENTIFIERS ---\n";
        details = details + "Suspect Name          : " + showValue(report.suspectName) + "\n";
        details = details + "Suspect Phone         : " + showValue(report.suspectPhone) + "\n";
        details = details + "Suspect Email         : " + showValue(report.suspectEmail) + "\n";
        details = details + "Wallet Address        : " + showValue(report.walletAddress) + "\n";
        details = details + "Transaction ID / Hash : " + showValue(report.transactionHash) + "\n";
        details = details + "Website / Profile URL : " + showValue(report.websiteUrl) + "\n\n";
        details = details + "--- ADDITIONAL INFORMATION ---\n";
        details = details + showValue(report.additionalDetails) + "\n\n";
        details = details + "--- INVESTIGATOR REMARKS ---\n";
        details = details + showValue(report.investigatorRemarks) + "\n";

        JTextArea jTextAreaFullReport = new JTextArea(details);
        jTextAreaFullReport.setEditable(false);
        jTextAreaFullReport.setLineWrap(true);
        jTextAreaFullReport.setWrapStyleWord(true);
        jTextAreaFullReport.setFont(new Font("Monospaced", Font.PLAIN, 13));
        jTextAreaFullReport.setCaretPosition(0);
        JScrollPane jScrollPaneFullReport = new JScrollPane(jTextAreaFullReport);
        jScrollPaneFullReport.setBounds(20, 60, 765, 330);
        jDialogFullReport.add(jScrollPaneFullReport);

        JLabel jLabelEvidence = new JLabel("Evidence Submitted For This Report :");
        jLabelEvidence.setBounds(20, 400, 400, 25);
        jLabelEvidence.setFont(new Font("Arial", Font.BOLD, 15));
        jDialogFullReport.add(jLabelEvidence);

        String[] columns = {"Evidence Name", "Type", "Description", "SHA-256", "Uploaded On", "File Path"};
        DefaultTableModel defaultTableModelEvidence = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable jTableEvidence = new JTable(defaultTableModelEvidence);
        jTableEvidence.setRowHeight(25);
        JScrollPane jScrollPaneEvidence = new JScrollPane(jTableEvidence);
        jScrollPaneEvidence.setBounds(20, 430, 765, 140);
        jDialogFullReport.add(jScrollPaneEvidence);

        try {
            List<Backend.EvidenceRecord> evidenceList = backend.getEvidenceByReport(report.reportId);
            for (Backend.EvidenceRecord evidence : evidenceList) {
                String hash = showValue(evidence.hash);
                if (hash.length() > 18) {
                    hash = hash.substring(0, 18) + "...";
                }
                defaultTableModelEvidence.addRow(new Object[]{
                    evidence.name, evidence.type, showValue(evidence.description), hash, showValue(evidence.uploadedAt), showValue(evidence.path)
                });
            }
            if (evidenceList.size() == 0) {
                jLabelEvidence.setText("Evidence Submitted For This Report : No evidence submitted yet.");
                jLabelEvidence.setBounds(20, 400, 700, 25);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(jDialogFullReport, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JButton jButtonOpenFile = new JButton("OPEN EVIDENCE FILE");
        jButtonOpenFile.setBounds(20, 585, 230, 40);
        jButtonOpenFile.setFont(new Font("Arial", Font.BOLD, 15));
        jButtonOpenFile.setBackground(blueColor);
        jButtonOpenFile.setForeground(Color.WHITE);
        jDialogFullReport.add(jButtonOpenFile);

        JButton jButtonCloseReport = new JButton("CLOSE");
        jButtonCloseReport.setBounds(635, 585, 150, 40);
        jButtonCloseReport.setFont(new Font("Arial", Font.BOLD, 15));
        jButtonCloseReport.setBackground(greyColor);
        jButtonCloseReport.setForeground(Color.WHITE);
        jDialogFullReport.add(jButtonCloseReport);

        jButtonOpenFile.addActionListener(e -> {
            int selectedRow = jTableEvidence.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(jDialogFullReport, "Please select an evidence file from the table.", "Select Evidence", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String filePath = (String) defaultTableModelEvidence.getValueAt(selectedRow, 5);
            File evidenceFile = new File(filePath);
            if (!evidenceFile.exists()) {
                JOptionPane.showMessageDialog(jDialogFullReport, "File not found on this computer:\n" + filePath, "File Not Found", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Desktop.getDesktop().open(evidenceFile);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(jDialogFullReport, "Could not open the file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        jButtonCloseReport.addActionListener(e -> jDialogFullReport.dispose());

        jDialogFullReport.setVisible(true);
    }

    void timelineWindow(JFrame parent) {
        JFrame jFrameTimeline = new JFrame("Investigation Timeline");
        jFrameTimeline.setSize(950, 600);
        jFrameTimeline.setLocationRelativeTo(parent);
        jFrameTimeline.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrameTimeline.setLayout(null);
        jFrameTimeline.getContentPane().setBackground(backgroundColor);

        JLabel jLabelTimelineHeading = new JLabel("INVESTIGATION TIMELINE", SwingConstants.CENTER);
        jLabelTimelineHeading.setBounds(0, 0, 950, 50);
        jLabelTimelineHeading.setFont(new Font("Arial", Font.BOLD, 22));
        jLabelTimelineHeading.setOpaque(true);
        jLabelTimelineHeading.setBackground(headerColor);
        jLabelTimelineHeading.setForeground(Color.BLACK);
        jFrameTimeline.add(jLabelTimelineHeading);

        JLabel jLabelSelectReport = new JLabel("Select Report ID :");
        jLabelSelectReport.setBounds(30, 70, 160, 30);
        jLabelSelectReport.setFont(new Font("Arial", Font.BOLD, 14));
        jFrameTimeline.add(jLabelSelectReport);

        JComboBox<String> jComboBoxReportId = new JComboBox<>();
        jComboBoxReportId.setBounds(190, 70, 220, 30);
        jFrameTimeline.add(jComboBoxReportId);

        JButton jButtonShowTimeline = new JButton("SHOW TIMELINE");
        jButtonShowTimeline.setBounds(430, 65, 180, 40);
        jButtonShowTimeline.setFont(new Font("Arial", Font.BOLD, 15));
        jButtonShowTimeline.setBackground(blueColor);
        jButtonShowTimeline.setForeground(Color.WHITE);
        jFrameTimeline.add(jButtonShowTimeline);

        JButton jButtonClose = new JButton("CLOSE");
        jButtonClose.setBounds(780, 65, 140, 40);
        jButtonClose.setFont(new Font("Arial", Font.BOLD, 15));
        jButtonClose.setBackground(greyColor);
        jButtonClose.setForeground(Color.WHITE);
        jFrameTimeline.add(jButtonClose);

        JLabel jLabelReportInfo = new JLabel("Select a report and click SHOW TIMELINE.");
        jLabelReportInfo.setBounds(30, 120, 890, 30);
        jLabelReportInfo.setFont(new Font("Arial", Font.BOLD, 14));
        jFrameTimeline.add(jLabelReportInfo);

        String[] columns = {"Date & Time", "Event", "Status", "Assigned Unit", "Remarks", "Done By"};
        DefaultTableModel defaultTableModelTimeline = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable jTableTimeline = new JTable(defaultTableModelTimeline);
        jTableTimeline.setRowHeight(25);
        JScrollPane jScrollPaneTimeline = new JScrollPane(jTableTimeline);
        jScrollPaneTimeline.setBounds(30, 160, 890, 290);
        jFrameTimeline.add(jScrollPaneTimeline);

        JLabel jLabelNote = new JLabel("Add Investigation Note :");
        jLabelNote.setBounds(30, 470, 200, 30);
        jLabelNote.setFont(new Font("Arial", Font.BOLD, 14));
        jFrameTimeline.add(jLabelNote);

        JTextField jTextFieldNote = new JTextField();
        jTextFieldNote.setBounds(230, 470, 500, 30);
        jFrameTimeline.add(jTextFieldNote);

        JButton jButtonAddNote = new JButton("ADD NOTE");
        jButtonAddNote.setBounds(750, 465, 170, 40);
        jButtonAddNote.setFont(new Font("Arial", Font.BOLD, 15));
        jButtonAddNote.setBackground(greenColor);
        jButtonAddNote.setForeground(Color.WHITE);
        jFrameTimeline.add(jButtonAddNote);

        try {
            List<Backend.CybercrimeReport> reports = backend.getAllUserReports();
            for (Backend.CybercrimeReport report : reports) {
                jComboBoxReportId.addItem(report.reportId);
            }
            if (reports.size() == 0) {
                jLabelReportInfo.setText("No complaints have been submitted yet.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(jFrameTimeline, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        Runnable loadTimeline = () -> {
            if (jComboBoxReportId.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(jFrameTimeline, "No report selected.", "Select Report", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String reportId = String.valueOf(jComboBoxReportId.getSelectedItem());
            try {
                Backend.CybercrimeReport report = backend.getReportById(reportId);
                jLabelReportInfo.setText("Victim : " + showValue(report.victimName) + "     Category : " + showValue(report.crimeCategory) + "     Current Status : " + showValue(report.status));
                defaultTableModelTimeline.setRowCount(0);
                List<Backend.TimelineEvent> events = backend.getTimeline(reportId);
                for (Backend.TimelineEvent event : events) {
                    defaultTableModelTimeline.addRow(new Object[]{
                        showValue(event.createdAt), event.eventType, showValue(event.status), showValue(event.assignedUnit), showValue(event.remarks), showValue(event.doneBy)
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(jFrameTimeline, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        jButtonShowTimeline.addActionListener(e -> loadTimeline.run());

        jButtonAddNote.addActionListener(e -> {
            if (jComboBoxReportId.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(jFrameTimeline, "No report selected.", "Select Report", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String note = jTextFieldNote.getText().trim();
            if (note.isBlank()) {
                JOptionPane.showMessageDialog(jFrameTimeline, "Please type a note first.", "Empty Note", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String reportId = String.valueOf(jComboBoxReportId.getSelectedItem());
            try {
                backend.addInvestigationNote(reportId, note, adminUsername);
                JOptionPane.showMessageDialog(jFrameTimeline, "Note added to the timeline of " + reportId + ".");
                jTextFieldNote.setText("");
                loadTimeline.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(jFrameTimeline, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        jButtonClose.addActionListener(e -> jFrameTimeline.dispose());

        jFrameTimeline.setVisible(true);
    }

    void forensicReportsWindow(JFrame parent) {
        JFrame jFrameForensicReports = new JFrame("Forensic Reports & Analytics");
        jFrameForensicReports.setSize(950, 620);
        jFrameForensicReports.setLocationRelativeTo(parent);
        jFrameForensicReports.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrameForensicReports.setLayout(null);
        jFrameForensicReports.getContentPane().setBackground(backgroundColor);

        JLabel jLabelForensicHeading = new JLabel("FORENSIC REPORTS & ANALYTICS", SwingConstants.CENTER);
        jLabelForensicHeading.setBounds(0, 0, 950, 50);
        jLabelForensicHeading.setFont(new Font("Arial", Font.BOLD, 22));
        jLabelForensicHeading.setOpaque(true);
        jLabelForensicHeading.setBackground(headerColor);
        jLabelForensicHeading.setForeground(Color.BLACK);
        jFrameForensicReports.add(jLabelForensicHeading);

        JLabel jLabelTotal = new JLabel("", SwingConstants.CENTER);
        jLabelTotal.setBounds(30, 70, 205, 60);
        jLabelTotal.setFont(new Font("Arial", Font.BOLD, 14));
        jLabelTotal.setOpaque(true);
        jLabelTotal.setBackground(Color.WHITE);
        jLabelTotal.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        jFrameForensicReports.add(jLabelTotal);

        JLabel jLabelPending = new JLabel("", SwingConstants.CENTER);
        jLabelPending.setBounds(255, 70, 205, 60);
        jLabelPending.setFont(new Font("Arial", Font.BOLD, 14));
        jLabelPending.setOpaque(true);
        jLabelPending.setBackground(Color.WHITE);
        jLabelPending.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        jFrameForensicReports.add(jLabelPending);

        JLabel jLabelInvestigating = new JLabel("", SwingConstants.CENTER);
        jLabelInvestigating.setBounds(480, 70, 205, 60);
        jLabelInvestigating.setFont(new Font("Arial", Font.BOLD, 14));
        jLabelInvestigating.setOpaque(true);
        jLabelInvestigating.setBackground(Color.WHITE);
        jLabelInvestigating.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        jFrameForensicReports.add(jLabelInvestigating);

        JLabel jLabelResolved = new JLabel("", SwingConstants.CENTER);
        jLabelResolved.setBounds(705, 70, 215, 60);
        jLabelResolved.setFont(new Font("Arial", Font.BOLD, 14));
        jLabelResolved.setOpaque(true);
        jLabelResolved.setBackground(Color.WHITE);
        jLabelResolved.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        jFrameForensicReports.add(jLabelResolved);

        JLabel jLabelAmount = new JLabel("Total Amount Lost : Rs. 0");
        jLabelAmount.setBounds(30, 145, 420, 30);
        jLabelAmount.setFont(new Font("Arial", Font.BOLD, 15));
        jFrameForensicReports.add(jLabelAmount);

        JLabel jLabelEvidenceCount = new JLabel("Total Evidence Files : 0");
        jLabelEvidenceCount.setBounds(480, 145, 420, 30);
        jLabelEvidenceCount.setFont(new Font("Arial", Font.BOLD, 15));
        jFrameForensicReports.add(jLabelEvidenceCount);

        JLabel jLabelByCategory = new JLabel("Complaints by Crime Category :");
        jLabelByCategory.setBounds(30, 185, 400, 25);
        jLabelByCategory.setFont(new Font("Arial", Font.BOLD, 14));
        jFrameForensicReports.add(jLabelByCategory);

        DefaultTableModel defaultTableModelCategory = new DefaultTableModel(new String[]{"Crime Category", "Number of Complaints"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable jTableCategory = new JTable(defaultTableModelCategory);
        jTableCategory.setRowHeight(25);
        JScrollPane jScrollPaneCategory = new JScrollPane(jTableCategory);
        jScrollPaneCategory.setBounds(30, 215, 430, 260);
        jFrameForensicReports.add(jScrollPaneCategory);

        JLabel jLabelByStatus = new JLabel("Complaints by Status :");
        jLabelByStatus.setBounds(490, 185, 400, 25);
        jLabelByStatus.setFont(new Font("Arial", Font.BOLD, 14));
        jFrameForensicReports.add(jLabelByStatus);

        DefaultTableModel defaultTableModelStatus = new DefaultTableModel(new String[]{"Status", "Number of Complaints"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable jTableStatus = new JTable(defaultTableModelStatus);
        jTableStatus.setRowHeight(25);
        JScrollPane jScrollPaneStatus = new JScrollPane(jTableStatus);
        jScrollPaneStatus.setBounds(490, 215, 430, 260);
        jFrameForensicReports.add(jScrollPaneStatus);

        JButton jButtonExportCsv = new JButton("EXPORT CSV");
        jButtonExportCsv.setBounds(30, 495, 200, 45);
        jButtonExportCsv.setFont(new Font("Arial", Font.BOLD, 15));
        jButtonExportCsv.setBackground(greenColor);
        jButtonExportCsv.setForeground(Color.WHITE);
        jFrameForensicReports.add(jButtonExportCsv);

        JButton jButtonRefresh = new JButton("REFRESH");
        jButtonRefresh.setBounds(250, 495, 150, 45);
        jButtonRefresh.setFont(new Font("Arial", Font.BOLD, 15));
        jButtonRefresh.setBackground(orangeColor);
        jButtonRefresh.setForeground(Color.BLACK);
        jFrameForensicReports.add(jButtonRefresh);

        JButton jButtonClose = new JButton("CLOSE");
        jButtonClose.setBounds(770, 495, 150, 45);
        jButtonClose.setFont(new Font("Arial", Font.BOLD, 15));
        jButtonClose.setBackground(greyColor);
        jButtonClose.setForeground(Color.WHITE);
        jFrameForensicReports.add(jButtonClose);

        Runnable loadAnalytics = () -> {
            try {
                Map<String, Integer> stats = backend.getAdminStats();
                jLabelTotal.setText("<html><center>Total Complaints<br>" + stats.get("total") + "</center></html>");
                jLabelPending.setText("<html><center>Pending Review<br>" + stats.get("pending") + "</center></html>");
                jLabelInvestigating.setText("<html><center>Under Investigation<br>" + stats.get("investigating") + "</center></html>");
                jLabelResolved.setText("<html><center>Resolved / Closed<br>" + stats.get("resolved") + "</center></html>");
                jLabelEvidenceCount.setText("Total Evidence Files : " + stats.get("evidence"));
                jLabelAmount.setText("Total Amount Lost : Rs. " + String.format("%.2f", backend.getTotalAmountLost()));

                defaultTableModelCategory.setRowCount(0);
                Map<String, Integer> byCategory = backend.getCountByCategory();
                for (String category : byCategory.keySet()) {
                    defaultTableModelCategory.addRow(new Object[]{category, byCategory.get(category)});
                }

                defaultTableModelStatus.setRowCount(0);
                Map<String, Integer> byStatus = backend.getCountByStatus();
                for (String status : byStatus.keySet()) {
                    defaultTableModelStatus.addRow(new Object[]{status, byStatus.get(status)});
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(jFrameForensicReports, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        jButtonExportCsv.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setSelectedFile(new File("complaint_report.csv"));
            if (jFileChooser.showSaveDialog(jFrameForensicReports) == JFileChooser.APPROVE_OPTION) {
                String filePath = jFileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".csv")) {
                    filePath = filePath + ".csv";
                }
                try {
                    int count = backend.exportReportsToCsv(filePath);
                    JOptionPane.showMessageDialog(jFrameForensicReports, count + " complaints exported to:\n" + filePath, "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(jFrameForensicReports, ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        jButtonRefresh.addActionListener(e -> loadAnalytics.run());
        jButtonClose.addActionListener(e -> jFrameForensicReports.dispose());

        jFrameForensicReports.setVisible(true);
        loadAnalytics.run();
    }

    public static void main(String[] args) {
        new Frontend().setVisible(true);
    }
}
