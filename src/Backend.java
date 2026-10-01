import java.io.FileInputStream;
import java.security.MessageDigest;
import java.sql.*;
import java.util.*;

public class Backend {
    // CHANGE ONLY THIS PASSWORD
    private static final String SERVER_URL =
        "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String URL =
        "jdbc:mysql://localhost:3306/cyber_forensics?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "anku";

    private static boolean dbInitialized = false;

    private synchronized void ensureDatabase() {
        if (dbInitialized) return;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS cyber_forensics");
            }

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 Statement stmt = conn.createStatement()) {
                
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(100) NOT NULL, " +
                    "role VARCHAR(20) DEFAULT 'USER', " +
                    "full_name VARCHAR(100), " +
                    "email VARCHAR(100) UNIQUE, " +
                    "mobile_number VARCHAR(20), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

                addColumnIfNotExists(conn, "users", "role", "VARCHAR(20) DEFAULT 'USER'");
                addColumnIfNotExists(conn, "users", "full_name", "VARCHAR(100)");
                addColumnIfNotExists(conn, "users", "email", "VARCHAR(100)");
                addColumnIfNotExists(conn, "users", "mobile_number", "VARCHAR(20)");
                addColumnIfNotExists(conn, "users", "created_at", "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");

                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS cases (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "case_number VARCHAR(30) UNIQUE NOT NULL, " +
                    "title VARCHAR(150) NOT NULL, " +
                    "investigator VARCHAR(100) NOT NULL, " +
                    "description VARCHAR(500), " +
                    "status VARCHAR(30) DEFAULT 'Open')");

                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS cybercrime_reports (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "report_id VARCHAR(30) UNIQUE NOT NULL, " +
                    "user_id INT NOT NULL, " +
                    "victim_name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL, " +
                    "mobile_number VARCHAR(20) NOT NULL, " +
                    "crime_category VARCHAR(100) NOT NULL, " +
                    "incident_date VARCHAR(50) NOT NULL, " +
                    "location VARCHAR(200), " +
                    "description TEXT NOT NULL, " +
                    "amount_lost VARCHAR(50), " +
                    "suspect_name VARCHAR(100), " +
                    "suspect_phone VARCHAR(50), " +
                    "suspect_email VARCHAR(100), " +
                    "wallet_address VARCHAR(150), " +
                    "transaction_hash VARCHAR(150), " +
                    "website_url VARCHAR(200), " +
                    "additional_details TEXT, " +
                    "status VARCHAR(50) DEFAULT 'SUBMITTED', " +
                    "assigned_unit VARCHAR(100) DEFAULT 'Cyber Crime Cell Unit 1', " +
                    "investigator_remarks TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");

                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS evidence (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "case_number VARCHAR(30), " +
                    "report_id VARCHAR(30), " +
                    "evidence_name VARCHAR(150) NOT NULL, " +
                    "evidence_type VARCHAR(50) NOT NULL, " +
                    "description TEXT, " +
                    "file_path VARCHAR(500), " +
                    "sha256 VARCHAR(64) NOT NULL, " +
                    "status VARCHAR(30) DEFAULT 'Verified', " +
                    "user_id INT, " +
                    "uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

                addColumnIfNotExists(conn, "evidence", "report_id", "VARCHAR(30)");
                addColumnIfNotExists(conn, "evidence", "description", "TEXT");
                addColumnIfNotExists(conn, "evidence", "user_id", "INT");
                addColumnIfNotExists(conn, "evidence", "uploaded_at", "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");

                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS user_notifications (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "user_id INT NOT NULL, " +
                    "report_id VARCHAR(30), " +
                    "message TEXT NOT NULL, " +
                    "is_read BOOLEAN DEFAULT FALSE, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");

                // Default admin and user accounts
                stmt.executeUpdate("INSERT IGNORE INTO users(username, password, role, full_name, email, mobile_number) " +
                    "VALUES('admin', 'admin123', 'ADMIN', 'System Administrator', 'admin@cyberforensics.gov', '9876543210')");
                stmt.executeUpdate("UPDATE users SET role='ADMIN' WHERE username='admin'");

                stmt.executeUpdate("INSERT IGNORE INTO users(username, password, role, full_name, email, mobile_number) " +
                    "VALUES('user1', 'user123', 'USER', 'John Doe', 'user1@example.com', '9123456789')");
            }
            dbInitialized = true;
        } catch (Exception e) {
            System.err.println("Database auto-init notice: " + e.getMessage());
        }
    }

    private void addColumnIfNotExists(Connection conn, String table, String column, String def) {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + column + " " + def);
        } catch (SQLException ignored) {
            // Column already exists
        }
    }

    private Connection connect() throws SQLException {
        ensureDatabase();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ==================== DATA MODELS ====================

    public static class UserSession {
        public int id;
        public String username, role, fullName, email, mobileNumber;
        public UserSession(int id, String username, String role, String fullName, String email, String mobileNumber) {
            this.id = id; this.username = username; this.role = role;
            this.fullName = fullName; this.email = email; this.mobileNumber = mobileNumber;
        }
    }

    public static class CaseRecord {
        public String caseNumber, title, investigator, description, status;
        public CaseRecord(String n, String t, String i, String d, String s) {
            caseNumber=n; title=t; investigator=i; description=d; status=s;
        }
    }

    public static class EvidenceRecord {
        public int id;
        public String caseNumber, reportId, name, type, description, path, hash, status, uploadedAt;
        public int userId;
        public EvidenceRecord(String c, String n, String t, String p, String h, String s) {
            this.caseNumber=c; this.name=n; this.type=t; this.path=p; this.hash=h; this.status=s;
        }
        public EvidenceRecord(int id, String caseNumber, String reportId, String name, String type, String description, String path, String hash, String status, int userId, String uploadedAt) {
            this.id = id; this.caseNumber = caseNumber; this.reportId = reportId;
            this.name = name; this.type = type; this.description = description;
            this.path = path; this.hash = hash; this.status = status;
            this.userId = userId; this.uploadedAt = uploadedAt;
        }
    }

    public static class CybercrimeReport {
        public int id, userId;
        public String reportId, victimName, email, mobileNumber, crimeCategory, incidentDate, location, description;
        public String amountLost, suspectName, suspectPhone, suspectEmail, walletAddress, transactionHash, websiteUrl, additionalDetails;
        public String status, assignedUnit, investigatorRemarks, createdAt, updatedAt;

        public CybercrimeReport() {}
    }

    public static class UserNotification {
        public int id, userId;
        public String reportId, message, createdAt;
        public boolean isRead;
        public UserNotification(int id, int userId, String reportId, String message, boolean isRead, String createdAt) {
            this.id = id; this.userId = userId; this.reportId = reportId;
            this.message = message; this.isRead = isRead; this.createdAt = createdAt;
        }
    }

    // ==================== AUTH & USER METHODS ====================

    public boolean login(String username, String password) throws SQLException {
        UserSession s = loginUser(username, password);
        return s != null;
    }

    public UserSession loginUser(String usernameOrEmail, String password) throws SQLException {
        String sql = "SELECT id, username, role, full_name, email, mobile_number FROM users WHERE (username=? OR email=?) AND password=?";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, usernameOrEmail);
            p.setString(2, usernameOrEmail);
            p.setString(3, password);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    String role = r.getString("role");
                    if (role == null || role.isBlank()) role = "USER";
                    return new UserSession(
                        r.getInt("id"),
                        r.getString("username"),
                        role,
                        r.getString("full_name"),
                        r.getString("email"),
                        r.getString("mobile_number")
                    );
                }
            }
        }
        return null;
    }

    public boolean registerUser(String fullName, String email, String mobile, String username, String password) throws SQLException {
        String checkSql = "SELECT id FROM users WHERE username=? OR email=?";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(checkSql)) {
            p.setString(1, username);
            p.setString(2, email);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    throw new SQLException("Username or Email is already registered.");
                }
            }
        }

        String sql = "INSERT INTO users(username, password, role, full_name, email, mobile_number) VALUES(?, ?, 'USER', ?, ?, ?)";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username);
            p.setString(2, password);
            p.setString(3, fullName);
            p.setString(4, email);
            p.setString(5, mobile);
            return p.executeUpdate() > 0;
        }
    }

    public UserSession getUserProfile(int userId) throws SQLException {
        String sql = "SELECT id, username, role, full_name, email, mobile_number FROM users WHERE id=?";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, userId);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    return new UserSession(
                        r.getInt("id"),
                        r.getString("username"),
                        r.getString("role"),
                        r.getString("full_name"),
                        r.getString("email"),
                        r.getString("mobile_number")
                    );
                }
            }
        }
        return null;
    }

    public boolean updateProfile(int userId, String fullName, String email, String mobile) throws SQLException {
        String sql = "UPDATE users SET full_name=?, email=?, mobile_number=? WHERE id=?";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, fullName);
            p.setString(2, email);
            p.setString(3, mobile);
            p.setInt(4, userId);
            return p.executeUpdate() > 0;
        }
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) throws SQLException {
        String checkSql = "SELECT id FROM users WHERE id=? AND password=?";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(checkSql)) {
            p.setInt(1, userId);
            p.setString(2, oldPassword);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) {
                    throw new SQLException("Incorrect current password.");
                }
            }
        }
        String sql = "UPDATE users SET password=? WHERE id=?";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, newPassword);
            p.setInt(2, userId);
            return p.executeUpdate() > 0;
        }
    }

    // ==================== REPORT MANAGEMENT ====================

    public synchronized String generateReportId() throws SQLException {
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        String sql = "SELECT COUNT(*) FROM cybercrime_reports";
        int count = 1;
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {
            if (r.next()) {
                count = r.getInt(1) + 1;
            }
        }
        return String.format("CF-%s-%04d", year, count);
    }

    public String createReport(CybercrimeReport r) throws SQLException {
        if (r.reportId == null || r.reportId.isBlank()) {
            r.reportId = generateReportId();
        }
        r.status = "SUBMITTED";
        if (r.assignedUnit == null || r.assignedUnit.isBlank()) {
            r.assignedUnit = "Cyber Crime Cell Unit 1";
        }

        String sql = "INSERT INTO cybercrime_reports(report_id, user_id, victim_name, email, mobile_number, " +
            "crime_category, incident_date, location, description, amount_lost, suspect_name, suspect_phone, " +
            "suspect_email, wallet_address, transaction_hash, website_url, additional_details, status, assigned_unit) " +
            "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, r.reportId);
            p.setInt(2, r.userId);
            p.setString(3, r.victimName);
            p.setString(4, r.email);
            p.setString(5, r.mobileNumber);
            p.setString(6, r.crimeCategory);
            p.setString(7, r.incidentDate);
            p.setString(8, r.location);
            p.setString(9, r.description);
            p.setString(10, r.amountLost);
            p.setString(11, r.suspectName);
            p.setString(12, r.suspectPhone);
            p.setString(13, r.suspectEmail);
            p.setString(14, r.walletAddress);
            p.setString(15, r.transactionHash);
            p.setString(16, r.websiteUrl);
            p.setString(17, r.additionalDetails);
            p.setString(18, r.status);
            p.setString(19, r.assignedUnit);
            p.executeUpdate();
        }

        createNotification(r.userId, r.reportId, "Your report " + r.reportId + " has been submitted successfully.");
        return r.reportId;
    }

    public List<CybercrimeReport> getReportsByUser(int userId) throws SQLException {
        List<CybercrimeReport> list = new ArrayList<>();
        String sql = "SELECT * FROM cybercrime_reports WHERE user_id=? ORDER BY id DESC";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, userId);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    list.add(mapReport(r));
                }
            }
        }
        return list;
    }

    public List<CybercrimeReport> getAllUserReports() throws SQLException {
        List<CybercrimeReport> list = new ArrayList<>();
        String sql = "SELECT * FROM cybercrime_reports ORDER BY id DESC";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {
            while (r.next()) {
                list.add(mapReport(r));
            }
        }
        return list;
    }

    public CybercrimeReport getReportById(String reportId) throws SQLException {
        String sql = "SELECT * FROM cybercrime_reports WHERE report_id=?";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, reportId.trim());
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    return mapReport(r);
                }
            }
        }
        return null;
    }

    public boolean updateReportStatus(String reportId, String newStatus, String assignedUnit, String remarks) throws SQLException {
        CybercrimeReport report = getReportById(reportId);
        if (report == null) throw new SQLException("Report ID not found: " + reportId);

        String sql = "UPDATE cybercrime_reports SET status=?, assigned_unit=?, investigator_remarks=? WHERE report_id=?";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, newStatus);
            p.setString(2, assignedUnit);
            p.setString(3, remarks);
            p.setString(4, reportId);
            boolean updated = p.executeUpdate() > 0;
            if (updated) {
                createNotification(report.userId, reportId, "Your report " + reportId + " status updated to " + newStatus + ".");
            }
            return updated;
        }
    }

    private CybercrimeReport mapReport(ResultSet r) throws SQLException {
        CybercrimeReport cr = new CybercrimeReport();
        cr.id = r.getInt("id");
        cr.reportId = r.getString("report_id");
        cr.userId = r.getInt("user_id");
        cr.victimName = r.getString("victim_name");
        cr.email = r.getString("email");
        cr.mobileNumber = r.getString("mobile_number");
        cr.crimeCategory = r.getString("crime_category");
        cr.incidentDate = r.getString("incident_date");
        cr.location = r.getString("location");
        cr.description = r.getString("description");
        cr.amountLost = r.getString("amount_lost");
        cr.suspectName = r.getString("suspect_name");
        cr.suspectPhone = r.getString("suspect_phone");
        cr.suspectEmail = r.getString("suspect_email");
        cr.walletAddress = r.getString("wallet_address");
        cr.transactionHash = r.getString("transaction_hash");
        cr.websiteUrl = r.getString("website_url");
        cr.additionalDetails = r.getString("additional_details");
        cr.status = r.getString("status");
        cr.assignedUnit = r.getString("assigned_unit");
        cr.investigatorRemarks = r.getString("investigator_remarks");
        cr.createdAt = r.getString("created_at");
        cr.updatedAt = r.getString("updated_at");
        return cr;
    }

    public Map<String, Integer> getUserStats(int userId) throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", 0);
        stats.put("pending", 0);
        stats.put("investigating", 0);
        stats.put("resolved", 0);

        List<CybercrimeReport> reports = getReportsByUser(userId);
        stats.put("total", reports.size());

        for (CybercrimeReport r : reports) {
            String s = r.status.toUpperCase();
            if (s.contains("SUBMITTED")) {
                stats.put("pending", stats.get("pending") + 1);
            } else if (s.contains("REVIEW") || s.contains("INVESTIGATION") || s.contains("EVIDENCE")) {
                stats.put("investigating", stats.get("investigating") + 1);
            } else if (s.contains("RESOLVED") || s.contains("CLOSED")) {
                stats.put("resolved", stats.get("resolved") + 1);
            } else {
                stats.put("pending", stats.get("pending") + 1);
            }
        }
        return stats;
    }

    // ==================== ADMIN CASES METHODS ====================

    public void addCase(CaseRecord x) throws SQLException {
        String sql = "INSERT INTO cases(case_number,title,investigator,description,status) VALUES(?,?,?,?,?)";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, x.caseNumber); p.setString(2, x.title); p.setString(3, x.investigator);
            p.setString(4, x.description); p.setString(5, x.status); p.executeUpdate();
        }
    }

    public List<String[]> getCases() throws SQLException {
        List<String[]> out = new ArrayList<>();
        String sql = "SELECT case_number,title,investigator,status FROM cases ORDER BY id DESC";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {
            while (r.next()) out.add(new String[]{r.getString(1), r.getString(2), r.getString(3), r.getString(4)});
        }
        return out;
    }

    // ==================== EVIDENCE METHODS ====================

    public void addEvidence(EvidenceRecord x) throws SQLException {
        if (!caseExists(x.caseNumber)) throw new SQLException("Case number does not exist.");
        String sql = "INSERT INTO evidence(case_number,evidence_name,evidence_type,file_path,sha256,status) VALUES(?,?,?,?,?,?)";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, x.caseNumber); p.setString(2, x.name); p.setString(3, x.type);
            p.setString(4, x.path); p.setString(5, x.hash); p.setString(6, x.status); p.executeUpdate();
        }
    }

    public void addUserEvidence(String reportId, String name, String type, String desc, String filePath, String sha256, int userId) throws SQLException {
        CybercrimeReport rep = getReportById(reportId);
        if (rep == null) throw new SQLException("Invalid Report ID: " + reportId);
        if (rep.userId != userId) throw new SQLException("You are not authorized to submit evidence for this report.");

        String sql = "INSERT INTO evidence(report_id, evidence_name, evidence_type, description, file_path, sha256, status, user_id) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, reportId);
            p.setString(2, name);
            p.setString(3, type);
            p.setString(4, desc);
            p.setString(5, filePath);
            p.setString(6, sha256);
            p.setString(7, "Verified");
            p.setInt(8, userId);
            p.executeUpdate();
        }

        createNotification(userId, reportId, "New evidence '" + name + "' uploaded for report " + reportId + ".");
    }

    public List<EvidenceRecord> getEvidenceByReport(String reportId) throws SQLException {
        List<EvidenceRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM evidence WHERE report_id=? ORDER BY id DESC";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, reportId.trim());
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    list.add(new EvidenceRecord(
                        r.getInt("id"),
                        r.getString("case_number"),
                        r.getString("report_id"),
                        r.getString("evidence_name"),
                        r.getString("evidence_type"),
                        r.getString("description"),
                        r.getString("file_path"),
                        r.getString("sha256"),
                        r.getString("status"),
                        r.getInt("user_id"),
                        r.getString("uploaded_at")
                    ));
                }
            }
        }
        return list;
    }

    public List<String[]> getEvidence() throws SQLException {
        List<String[]> out = new ArrayList<>();
        String sql = "SELECT case_number,evidence_name,evidence_type,sha256,status FROM evidence WHERE case_number IS NOT NULL ORDER BY id DESC";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {
            while (r.next()) out.add(new String[]{r.getString(1), r.getString(2), r.getString(3), r.getString(4), r.getString(5)});
        }
        return out;
    }

    private boolean caseExists(String n) throws SQLException {
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement("SELECT id FROM cases WHERE case_number=?")) {
            p.setString(1, n);
            try (ResultSet r = p.executeQuery()) { return r.next(); }
        }
    }

    public String sha256(String path) throws Exception {
        MessageDigest d = MessageDigest.getInstance("SHA-256");
        try (FileInputStream in = new FileInputStream(path)) {
            byte[] b = new byte[8192]; int n;
            while ((n = in.read(b)) != -1) d.update(b, 0, n);
        }
        StringBuilder s = new StringBuilder();
        for (byte b : d.digest()) s.append(String.format("%02x", b));
        return s.toString();
    }

    // ==================== NOTIFICATION METHODS ====================

    public void createNotification(int userId, String reportId, String message) throws SQLException {
        String sql = "INSERT INTO user_notifications(user_id, report_id, message) VALUES(?,?,?)";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, userId);
            p.setString(2, reportId);
            p.setString(3, message);
            p.executeUpdate();
        }
    }

    public List<UserNotification> getNotificationsByUser(int userId) throws SQLException {
        List<UserNotification> list = new ArrayList<>();
        String sql = "SELECT * FROM user_notifications WHERE user_id=? ORDER BY id DESC";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, userId);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    list.add(new UserNotification(
                        r.getInt("id"),
                        r.getInt("user_id"),
                        r.getString("report_id"),
                        r.getString("message"),
                        r.getBoolean("is_read"),
                        r.getString("created_at")
                    ));
                }
            }
        }
        return list;
    }

    public void markNotificationsRead(int userId) throws SQLException {
        String sql = "UPDATE user_notifications SET is_read=TRUE WHERE user_id=?";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, userId);
            p.executeUpdate();
        }
    }
}
