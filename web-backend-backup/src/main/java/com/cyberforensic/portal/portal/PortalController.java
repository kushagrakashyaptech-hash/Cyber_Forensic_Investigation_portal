package com.cyberforensic.portal.portal;

import com.cyberforensic.portal.auth.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class PortalController {
    private final JdbcTemplate jdbc;
    private final PasswordEncoder encoder;

    public PortalController(JdbcTemplate jdbc, PasswordEncoder encoder) {
        this.jdbc = jdbc;
        this.encoder = encoder;
    }

    private AuthService.AuthUser user(HttpSession s) {
        return (AuthService.AuthUser) s.getAttribute("loggedInUser");
    }
    private ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Please log in again."));
    }
    private boolean admin(AuthService.AuthUser u) {
        return u != null && "ADMIN".equalsIgnoreCase(u.role());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(HttpSession session) {
        var u = user(session);
        if (u == null) return unauthorized();

        String sql = admin(u)
            ? """
              SELECT COUNT(*) AS total,
                COALESCE(SUM(CASE WHEN UPPER(status) IN ('SUBMITTED','PENDING','UNDER REVIEW') THEN 1 ELSE 0 END),0) AS pending,
                COALESCE(SUM(CASE WHEN UPPER(status) IN ('UNDER INVESTIGATION','EVIDENCE ANALYSIS') THEN 1 ELSE 0 END),0) AS investigating,
                COALESCE(SUM(CASE WHEN UPPER(status) IN ('RESOLVED','CLOSED') THEN 1 ELSE 0 END),0) AS resolved
              FROM cybercrime_reports
              """
            : """
              SELECT COUNT(*) AS total,
                COALESCE(SUM(CASE WHEN UPPER(status) IN ('SUBMITTED','PENDING','UNDER REVIEW') THEN 1 ELSE 0 END),0) AS pending,
                COALESCE(SUM(CASE WHEN UPPER(status) IN ('UNDER INVESTIGATION','EVIDENCE ANALYSIS') THEN 1 ELSE 0 END),0) AS investigating,
                COALESCE(SUM(CASE WHEN UPPER(status) IN ('RESOLVED','CLOSED') THEN 1 ELSE 0 END),0) AS resolved
              FROM cybercrime_reports WHERE user_id=?
              """;
        Map<String,Object> stats = admin(u)
            ? jdbc.queryForMap(sql)
            : jdbc.queryForMap(sql, u.id());

        List<Map<String,Object>> recent = admin(u)
            ? jdbc.queryForList("SELECT report_id AS reportId, victim_name AS victimName, crime_category AS category, status, created_at AS createdAt FROM cybercrime_reports ORDER BY id DESC LIMIT 6")
            : jdbc.queryForList("SELECT report_id AS reportId, victim_name AS victimName, crime_category AS category, status, created_at AS createdAt FROM cybercrime_reports WHERE user_id=? ORDER BY id DESC LIMIT 6", u.id());

        Map<String,Object> result = new HashMap<>(stats);
        result.put("recent", recent);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/reports")
    public ResponseEntity<?> reports(HttpSession session) {
        var u=user(session); if(u==null) return unauthorized();
        if(admin(u)) return ResponseEntity.ok(jdbc.queryForList("SELECT * FROM cybercrime_reports ORDER BY id DESC"));
        return ResponseEntity.ok(jdbc.queryForList("SELECT * FROM cybercrime_reports WHERE user_id=? ORDER BY id DESC",u.id()));
    }

    @PostMapping("/reports")
    public ResponseEntity<?> createReport(@RequestBody Map<String,String> b,HttpSession session) {
        var u=user(session); if(u==null) return unauthorized();
        String[] required={"victimName","email","mobileNumber","crimeCategory","incidentDate","description"};
        for(String k:required) if(blank(b.get(k))) return ResponseEntity.badRequest().body(Map.of("message","Please complete all required complaint fields."));
        String id="CF-"+LocalDate.now().getYear()+"-"+String.format("%04d", jdbc.queryForObject("SELECT COALESCE(MAX(id),0)+1 FROM cybercrime_reports",Integer.class));
        jdbc.update("""
          INSERT INTO cybercrime_reports(report_id,user_id,victim_name,email,mobile_number,crime_category,incident_date,location,description,amount_lost,suspect_name,suspect_phone,suspect_email,wallet_address,transaction_hash,website_url,additional_details,status)
          VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'SUBMITTED')
          """,id,u.id(),b.get("victimName"),b.get("email"),b.get("mobileNumber"),b.get("crimeCategory"),b.get("incidentDate"),b.get("location"),b.get("description"),b.get("amountLost"),b.get("suspectName"),b.get("suspectPhone"),b.get("suspectEmail"),b.get("walletAddress"),b.get("transactionHash"),b.get("websiteUrl"),b.get("additionalDetails"));
        jdbc.update("INSERT INTO user_notifications(user_id,report_id,message) VALUES(?,?,?)",u.id(),id,"Your cybercrime complaint "+id+" was submitted successfully.");
        return ResponseEntity.status(201).body(Map.of("reportId",id,"message","Complaint submitted successfully."));
    }

    @GetMapping("/reports/{id}")
    public ResponseEntity<?> track(@PathVariable String id,HttpSession session) {
        var u=user(session); if(u==null) return unauthorized();
        List<Map<String,Object>> rows=admin(u)
          ? jdbc.queryForList("SELECT * FROM cybercrime_reports WHERE report_id=?",id)
          : jdbc.queryForList("SELECT * FROM cybercrime_reports WHERE report_id=? AND user_id=?",id,u.id());
        if(rows.isEmpty()) return ResponseEntity.status(404).body(Map.of("message","Report not found."));
        return ResponseEntity.ok(rows.get(0));
    }


    @PatchMapping("/reports/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String id,@RequestBody Map<String,String> b,HttpSession session) {
        var u=user(session); if(u==null) return unauthorized();
        if(!admin(u)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","Administrator access is required."));
        String status=b.get("status"), unit=b.get("assignedUnit"), remarks=b.get("remarks");
        if(blank(status)) return ResponseEntity.badRequest().body(Map.of("message","Select a valid investigation status."));
        int changed=jdbc.update("UPDATE cybercrime_reports SET status=?,assigned_unit=?,investigator_remarks=? WHERE report_id=?",status,unit,remarks,id);
        if(changed==0) return ResponseEntity.status(404).body(Map.of("message","Report not found."));
        Integer owner=jdbc.queryForObject("SELECT user_id FROM cybercrime_reports WHERE report_id=?",Integer.class,id);
        jdbc.update("INSERT INTO user_notifications(user_id,report_id,message) VALUES(?,?,?)",owner,id,"Your report "+id+" status was updated to "+status+".");
        return ResponseEntity.ok(Map.of("message","Investigation status updated."));
    }

    @GetMapping("/evidence")
    public ResponseEntity<?> evidence(@RequestParam(required=false) String reportId,HttpSession session) {
        var u=user(session); if(u==null) return unauthorized();
        if(admin(u)) return reportId==null ? ResponseEntity.ok(jdbc.queryForList("SELECT * FROM evidence ORDER BY id DESC")) : ResponseEntity.ok(jdbc.queryForList("SELECT * FROM evidence WHERE report_id=? ORDER BY id DESC",reportId));
        if(reportId==null) return ResponseEntity.ok(jdbc.queryForList("SELECT * FROM evidence WHERE user_id=? ORDER BY id DESC",u.id()));
        return ResponseEntity.ok(jdbc.queryForList("SELECT * FROM evidence WHERE user_id=? AND report_id=? ORDER BY id DESC",u.id(),reportId));
    }

    @PostMapping("/evidence")
    public ResponseEntity<?> uploadEvidence(@RequestParam String reportId,@RequestParam String description,
          @RequestParam("file") MultipartFile file,HttpSession session) throws Exception {
        var u=user(session); if(u==null) return unauthorized();
        if(file.isEmpty()) return ResponseEntity.badRequest().body(Map.of("message","Choose a file to upload."));
        Integer owned=jdbc.queryForObject("SELECT COUNT(*) FROM cybercrime_reports WHERE report_id=?"+(admin(u)?"":" AND user_id=?"),Integer.class,admin(u)?new Object[]{reportId}:new Object[]{reportId,u.id()});
        if(owned==null || owned==0) return ResponseEntity.status(404).body(Map.of("message","Complaint not found or access denied."));
        String original=Paths.get(Objects.requireNonNullElse(file.getOriginalFilename(),"evidence")).getFileName().toString();
        Path dir=Paths.get("evidence-uploads").toAbsolutePath(); Files.createDirectories(dir);
        String safe=UUID.randomUUID()+"_"+original.replaceAll("[^a-zA-Z0-9._-]","_");
        Path dest=dir.resolve(safe); file.transferTo(dest);
        String type = Objects.requireNonNullElse(file.getContentType(), "application/octet-stream");

        jdbc.update(
            "INSERT INTO evidence(report_id,evidence_name,evidence_type,description,file_path,sha256,status,user_id) VALUES(?,?,?,?,?,NULL,?,?)",
            reportId, original, type, description, dest.toString(), "Uploaded", u.id()
        );

        return ResponseEntity.status(201).body(
            Map.of("message", "Evidence uploaded successfully.", "fileName", original)
        );
    }
    

    @GetMapping("/notifications")
    public ResponseEntity<?> notifications(HttpSession session) {
        var u=user(session); if(u==null) return unauthorized();
        return ResponseEntity.ok(jdbc.queryForList("SELECT * FROM user_notifications WHERE user_id=? ORDER BY id DESC",u.id()));
    }
    @PostMapping("/notifications/read")
    public ResponseEntity<?> markRead(HttpSession session) {
        var u=user(session); if(u==null) return unauthorized();
        jdbc.update("UPDATE user_notifications SET is_read=TRUE WHERE user_id=?",u.id());
        return ResponseEntity.ok(Map.of("message","Notifications marked as read."));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String,String> b,HttpSession session) {
        var u=user(session); if(u==null) return unauthorized();
        if(blank(b.get("fullName")) || blank(b.get("email"))) return ResponseEntity.badRequest().body(Map.of("message","Name and email are required."));
        Integer duplicate=jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE email=? AND id<>?",Integer.class,b.get("email").trim(),u.id());
        if(duplicate!=null && duplicate>0) return ResponseEntity.badRequest().body(Map.of("message","That email is already used by another account."));
        jdbc.update("UPDATE users SET full_name=?,email=?,mobile_number=? WHERE id=?",b.get("fullName").trim(),b.get("email").trim(),b.getOrDefault("mobileNumber","").trim(),u.id());
        var updated=new AuthService.AuthUser(u.id(),u.username(),u.role(),b.get("fullName").trim(),b.get("email").trim(),b.getOrDefault("mobileNumber",""));
        session.setAttribute("loggedInUser",updated);
        return ResponseEntity.ok(updated);
    }
    @PutMapping("/profile/password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String,String> b,HttpSession session) {
        var u=user(session); if(u==null) return unauthorized();
        String old=b.get("currentPassword"), next=b.get("newPassword");
        if(blank(old)||blank(next)||next.length()<8) return ResponseEntity.badRequest().body(Map.of("message","Enter your current password and a new password of at least 8 characters."));
        String stored=jdbc.queryForObject("SELECT password FROM users WHERE id=?",String.class,u.id());
        boolean matches=stored.startsWith("$2") ? encoder.matches(old,stored) : old.equals(stored);
        if(!matches) return ResponseEntity.badRequest().body(Map.of("message","Current password is incorrect."));
        jdbc.update("UPDATE users SET password=? WHERE id=?",encoder.encode(next),u.id());
        return ResponseEntity.ok(Map.of("message","Password updated successfully."));
    }
    private boolean blank(String s){return s==null||s.isBlank();}
}
