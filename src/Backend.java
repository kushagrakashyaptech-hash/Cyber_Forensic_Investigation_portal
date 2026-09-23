import java.io.FileInputStream;
import java.security.MessageDigest;
import java.sql.*;
import java.util.*;

public class Backend {
    // CHANGE ONLY THIS PASSWORD
    private static final String URL =
        "jdbc:mysql://localhost:3307/cyber_forensics?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "anku";

    private Connection connect() throws SQLException {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
        throw new SQLException("MySQL JDBC Driver not found.", e);
    }

    return DriverManager.getConnection(URL, USER, PASSWORD);
}

    public static class CaseRecord {
        String caseNumber, title, investigator, description, status;
        public CaseRecord(String n, String t, String i, String d, String s) {
            caseNumber=n; title=t; investigator=i; description=d; status=s;
        }
    }

    public static class EvidenceRecord {
        String caseNumber, name, type, path, hash, status;
        public EvidenceRecord(String c, String n, String t, String p, String h, String s) {
            caseNumber=c; name=n; type=t; path=p; hash=h; status=s;
        }
    }

    public boolean login(String username, String password) throws SQLException {
        String sql = "SELECT id FROM users WHERE username=? AND password=?";
        try (Connection c=connect(); PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1, username); p.setString(2, password);
            try (ResultSet r=p.executeQuery()) { return r.next(); }
        }
    }

    public void addCase(CaseRecord x) throws SQLException {
        String sql="INSERT INTO cases(case_number,title,investigator,description,status) VALUES(?,?,?,?,?)";
        try (Connection c=connect(); PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1,x.caseNumber); p.setString(2,x.title); p.setString(3,x.investigator);
            p.setString(4,x.description); p.setString(5,x.status); p.executeUpdate();
        }
    }

    public List<String[]> getCases() throws SQLException {
        List<String[]> out=new ArrayList<>();
        String sql="SELECT case_number,title,investigator,status FROM cases ORDER BY id DESC";
        try(Connection c=connect(); PreparedStatement p=c.prepareStatement(sql); ResultSet r=p.executeQuery()){
            while(r.next()) out.add(new String[]{r.getString(1),r.getString(2),r.getString(3),r.getString(4)});
        }
        return out;
    }

    private boolean caseExists(String n) throws SQLException {
        try(Connection c=connect(); PreparedStatement p=c.prepareStatement("SELECT id FROM cases WHERE case_number=?")){
            p.setString(1,n);
            try(ResultSet r=p.executeQuery()){return r.next();}
        }
    }

    public void addEvidence(EvidenceRecord x) throws SQLException {
        if(!caseExists(x.caseNumber)) throw new SQLException("Case number does not exist.");
        String sql="INSERT INTO evidence(case_number,evidence_name,evidence_type,file_path,sha256,status) VALUES(?,?,?,?,?,?)";
        try(Connection c=connect(); PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,x.caseNumber); p.setString(2,x.name); p.setString(3,x.type);
            p.setString(4,x.path); p.setString(5,x.hash); p.setString(6,x.status); p.executeUpdate();
        }
    }

    public List<String[]> getEvidence() throws SQLException {
        List<String[]> out=new ArrayList<>();
        String sql="SELECT case_number,evidence_name,evidence_type,sha256,status FROM evidence ORDER BY id DESC";
        try(Connection c=connect(); PreparedStatement p=c.prepareStatement(sql); ResultSet r=p.executeQuery()){
            while(r.next()) out.add(new String[]{r.getString(1),r.getString(2),r.getString(3),r.getString(4),r.getString(5)});
        }
        return out;
    }

    public String sha256(String path) throws Exception {
        MessageDigest d=MessageDigest.getInstance("SHA-256");
        try(FileInputStream in=new FileInputStream(path)){
            byte[] b=new byte[8192]; int n;
            while((n=in.read(b))!=-1) d.update(b,0,n);
        }
        StringBuilder s=new StringBuilder();
        for(byte b:d.digest()) s.append(String.format("%02x",b));
        return s.toString();
    }
}
