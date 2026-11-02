package com.cyberforensic.portal.controller;

import com.cyberforensic.portal.auth.AuthService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.Year;
import java.util.*;

@RestController
public class PortalController {
    private final JdbcTemplate jdbc;
    private final PasswordEncoder encoder;
    private final Path evidenceRoot = Paths.get("evidence-uploads").toAbsolutePath().normalize();

    public PortalController(JdbcTemplate jdbc, PasswordEncoder encoder) {
        this.jdbc = jdbc;
        this.encoder = encoder;
    }

    @PostConstruct
    public void prepareSchema() {
        try {
            Files.createDirectories(evidenceRoot);
            ensureColumn("cybercrime_reports", "assigned_investigator", "VARCHAR(100) NULL");
            ensureColumn("cybercrime_reports", "priority", "VARCHAR(20) NOT NULL DEFAULT 'NORMAL'");
            ensureColumn("evidence", "sha256", "VARCHAR(64) NULL");
            jdbc.execute("ALTER TABLE evidence MODIFY sha256 VARCHAR(64) NULL");
            jdbc.execute("CREATE TABLE IF NOT EXISTS admin_audit_logs (id BIGINT PRIMARY KEY AUTO_INCREMENT, admin_user_id INT NULL, action VARCHAR(100) NOT NULL, entity_type VARCHAR(50), entity_id VARCHAR(80), details TEXT, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        } catch (Exception e) {
            throw new IllegalStateException("Could not prepare portal database tables/columns: " + e.getMessage(), e);
        }
    }

    private void ensureColumn(String table, String column, String definition) {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=? AND COLUMN_NAME=?", Integer.class, table, column);
        if (n != null && n == 0) jdbc.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
    }

    private AuthService.AuthUser user(HttpSession s) { return (AuthService.AuthUser) s.getAttribute("loggedInUser"); }
    private boolean admin(AuthService.AuthUser u) { return u != null && "ADMIN".equalsIgnoreCase(u.role()); }
    private ResponseEntity<?> unauthorized() { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Please sign in to continue.")); }
    private ResponseEntity<?> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Administrator access is required.")); }
    private void audit(AuthService.AuthUser u, String action, String type, String id, String details) {
        jdbc.update("INSERT INTO admin_audit_logs(admin_user_id,action,entity_type,entity_id,details) VALUES(?,?,?,?,?)", u.id(), action, type, id, details);
    }

    @GetMapping("/api/dashboard")
    public ResponseEntity<?> dashboard(HttpSession session) {
        AuthService.AuthUser u=user(session); if(u==null)return unauthorized();
        List<Map<String,Object>> rows = admin(u) ? jdbc.queryForList("SELECT * FROM cybercrime_reports ORDER BY created_at DESC") : jdbc.queryForList("SELECT * FROM cybercrime_reports WHERE user_id=? ORDER BY created_at DESC",u.id());
        int total=rows.size(), pending=0, investigating=0, resolved=0;
        for(Map<String,Object> r:rows){String s=String.valueOf(r.get("status")).toUpperCase(); if(s.contains("RESOLVED")||s.contains("CLOSED"))resolved++; else if(s.contains("INVESTIGATION")||s.contains("EVIDENCE"))investigating++; else pending++;}
        return ResponseEntity.ok(Map.of("total",total,"pending",pending,"investigating",investigating,"resolved",resolved,"recent",rows.stream().limit(7).toList()));
    }

    @GetMapping("/api/reports")
    public ResponseEntity<?> reports(HttpSession session) {
        AuthService.AuthUser u=user(session); if(u==null)return unauthorized();
        return ResponseEntity.ok(admin(u) ? jdbc.queryForList("SELECT * FROM cybercrime_reports ORDER BY created_at DESC") : jdbc.queryForList("SELECT * FROM cybercrime_reports WHERE user_id=? ORDER BY created_at DESC",u.id()));
    }

    @PostMapping("/api/reports")
    public ResponseEntity<?> createReport(@RequestBody Map<String,Object> b, HttpSession session) {
        AuthService.AuthUser u=user(session); if(u==null)return unauthorized();
        String victim=str(b,"victimName"), email=str(b,"email"), mobile=str(b,"mobileNumber"), category=str(b,"crimeCategory"), date=str(b,"incidentDate"), desc=str(b,"description");
        if(victim.isBlank()||email.isBlank()||mobile.isBlank()||category.isBlank()||date.isBlank()||desc.isBlank())return ResponseEntity.badRequest().body(Map.of("message","Please complete all required complaint fields."));
        Integer next=jdbc.queryForObject("SELECT COALESCE(MAX(id),0)+1 FROM cybercrime_reports",Integer.class); String id=String.format("CF-%d-%04d",Year.now().getValue(),next==null?1:next);
        jdbc.update("INSERT INTO cybercrime_reports(report_id,user_id,victim_name,email,mobile_number,crime_category,incident_date,location,description,amount_lost,suspect_name,suspect_phone,suspect_email,wallet_address,transaction_hash,website_url,additional_details,status,assigned_unit,priority) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'SUBMITTED','Cyber Crime Cell Unit 1','NORMAL')",
                id,u.id(),victim,email,mobile,category,date,str(b,"location"),desc,str(b,"amountLost"),str(b,"suspectName"),str(b,"suspectPhone"),str(b,"suspectEmail"),str(b,"walletAddress"),str(b,"transactionHash"),str(b,"websiteUrl"),str(b,"additionalDetails"));
        jdbc.update("INSERT INTO user_notifications(user_id,report_id,message) VALUES(?,?,?)",u.id(),id,"Your complaint " + id + " has been submitted and is awaiting review.");
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message","Complaint submitted successfully.","reportId",id));
    }

    @GetMapping("/api/reports/{id}")
    public ResponseEntity<?> getReport(@PathVariable String id,HttpSession session) {
        AuthService.AuthUser u=user(session); if(u==null)return unauthorized();
        List<Map<String,Object>> rows=jdbc.queryForList("SELECT * FROM cybercrime_reports WHERE report_id=?",id);
        if(rows.isEmpty())return ResponseEntity.status(404).body(Map.of("message","Complaint reference was not found."));
        Map<String,Object> r=rows.get(0); if(!admin(u)&&((Number)r.get("user_id")).intValue()!=u.id())return forbidden();
        return ResponseEntity.ok(r);
    }

    @PatchMapping("/api/reports/{id}/status")
    public ResponseEntity<?> updateReport(@PathVariable String id,@RequestBody Map<String,Object> b,HttpSession session) {
        AuthService.AuthUser u=user(session); if(u==null)return unauthorized(); if(!admin(u))return forbidden();
        List<Map<String,Object>> rows=jdbc.queryForList("SELECT user_id FROM cybercrime_reports WHERE report_id=?",id);
        if(rows.isEmpty())return ResponseEntity.status(404).body(Map.of("message","Complaint reference was not found."));
        String status=str(b,"status").toUpperCase(Locale.ROOT), unit=str(b,"assignedUnit"), investigator=str(b,"assignedInvestigator"), priority=str(b,"priority"), remarks=str(b,"remarks");
        Set<String> valid=Set.of("SUBMITTED","UNDER REVIEW","UNDER INVESTIGATION","EVIDENCE ANALYSIS","RESOLVED","CLOSED","REJECTED");
        if(!valid.contains(status))return ResponseEntity.badRequest().body(Map.of("message","Choose a valid case status."));
        if(priority.isBlank())priority="NORMAL"; if(!Set.of("LOW","NORMAL","HIGH","URGENT").contains(priority.toUpperCase()))priority="NORMAL";
        jdbc.update("UPDATE cybercrime_reports SET status=?,assigned_unit=?,assigned_investigator=?,priority=?,investigator_remarks=? WHERE report_id=?",status,unit.isBlank()?"Cyber Crime Cell Unit 1":unit,investigator.isBlank()?null:investigator,priority.toUpperCase(),remarks,id);
        int owner=((Number)rows.get(0).get("user_id")).intValue();
        String message="Your complaint " + id + " status has been updated to " + status + "." + (remarks.isBlank()?"":" Investigator update: " + remarks);
        jdbc.update("INSERT INTO user_notifications(user_id,report_id,message) VALUES(?,?,?)",owner,id,message);
        audit(u,"UPDATED_COMPLAINT","COMPLAINT",id,"Status="+status+"; assigned unit="+unit+"; investigator="+investigator+"; priority="+priority);
        return ResponseEntity.ok(Map.of("message","Investigation update saved and citizen notified."));
    }

    @GetMapping("/api/evidence")
    public ResponseEntity<?> evidence(HttpSession session) {
        AuthService.AuthUser u=user(session); if(u==null)return unauthorized();
        return ResponseEntity.ok(admin(u)?jdbc.queryForList("SELECT id,case_number,report_id,evidence_name,evidence_type,description,file_path,sha256,status,user_id,uploaded_at FROM evidence ORDER BY uploaded_at DESC"):jdbc.queryForList("SELECT id,case_number,report_id,evidence_name,evidence_type,description,file_path,sha256,status,user_id,uploaded_at FROM evidence WHERE user_id=? ORDER BY uploaded_at DESC",u.id()));
    }

    @PostMapping(value="/api/evidence",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadEvidence(@RequestParam String reportId,@RequestParam(required=false,defaultValue="") String description,@RequestParam("file") MultipartFile file,HttpSession session) throws IOException {
        AuthService.AuthUser u=user(session); if(u==null)return unauthorized();
        if(file.isEmpty())return ResponseEntity.badRequest().body(Map.of("message","Choose a file to upload."));
        List<Map<String,Object>> reports=jdbc.queryForList("SELECT user_id FROM cybercrime_reports WHERE report_id=?",reportId);
        if(reports.isEmpty())return ResponseEntity.badRequest().body(Map.of("message","Complaint reference does not exist."));
        int owner=((Number)reports.get(0).get("user_id")).intValue(); if(!admin(u)&&owner!=u.id())return forbidden();
        String original=Paths.get(Objects.requireNonNullElse(file.getOriginalFilename(),"evidence")).getFileName().toString();
        String stored=UUID.randomUUID()+"_"+original.replaceAll("[^A-Za-z0-9._-]","_"); Path destination=evidenceRoot.resolve(stored).normalize();
        if(!destination.startsWith(evidenceRoot))return ResponseEntity.badRequest().body(Map.of("message","Invalid file name."));
        Files.copy(file.getInputStream(),destination,StandardCopyOption.REPLACE_EXISTING);
        String type=Objects.requireNonNullElse(file.getContentType(),"application/octet-stream");
        jdbc.update("INSERT INTO evidence(report_id,evidence_name,evidence_type,description,file_path,sha256,status,user_id) VALUES(?,?,?,?,?,NULL,'Uploaded',?)",reportId,original,type,description,destination.toString(),owner);
        jdbc.update("INSERT INTO user_notifications(user_id,report_id,message) VALUES(?,?,?)",owner,reportId,"Evidence '"+original+"' was added to complaint "+reportId+".");
        if(admin(u))audit(u,"UPLOADED_EVIDENCE","EVIDENCE",reportId,original);
        return ResponseEntity.status(201).body(Map.of("message","Evidence submitted successfully.","fileName",original));
    }

    @GetMapping("/api/evidence/{id}/download")
    public ResponseEntity<?> downloadEvidence(@PathVariable int id,HttpSession session) {
        AuthService.AuthUser u=user(session); if(u==null)return unauthorized();
        List<Map<String,Object>> rows=jdbc.queryForList("SELECT file_path,evidence_name,user_id FROM evidence WHERE id=?",id);
        if(rows.isEmpty())return ResponseEntity.notFound().build(); Map<String,Object> r=rows.get(0);
        if(!admin(u)&&((Number)r.get("user_id")).intValue()!=u.id())return forbidden();
        Path p=Paths.get(String.valueOf(r.get("file_path"))).toAbsolutePath().normalize(); if(!p.startsWith(evidenceRoot)||!Files.exists(p))return ResponseEntity.notFound().build();
        Resource resource=new FileSystemResource(p); String name=String.valueOf(r.get("evidence_name")).replace("\"","");
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+name+"\"").contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
    }

    @GetMapping("/api/notifications") public ResponseEntity<?> notifications(HttpSession s){AuthService.AuthUser u=user(s);if(u==null)return unauthorized();return ResponseEntity.ok(jdbc.queryForList("SELECT * FROM user_notifications WHERE user_id=? ORDER BY id DESC",u.id()));}
    @PostMapping("/api/notifications/read") public ResponseEntity<?> markRead(HttpSession s){AuthService.AuthUser u=user(s);if(u==null)return unauthorized();jdbc.update("UPDATE user_notifications SET is_read=TRUE WHERE user_id=?",u.id());return ResponseEntity.ok(Map.of("message","Notifications marked as read."));}

    @PutMapping("/api/profile") public ResponseEntity<?> updateProfile(@RequestBody Map<String,Object>b,HttpSession s){AuthService.AuthUser u=user(s);if(u==null)return unauthorized();String name=str(b,"fullName"),email=str(b,"email"),mobile=str(b,"mobileNumber");if(name.isBlank()||email.isBlank())return ResponseEntity.badRequest().body(Map.of("message","Name and email are required."));try{jdbc.update("UPDATE users SET full_name=?,email=?,mobile_number=? WHERE id=?",name,email,mobile,u.id());}catch(Exception e){return ResponseEntity.badRequest().body(Map.of("message","Email address is already in use."));}return ResponseEntity.ok(jdbc.queryForMap("SELECT id,username,role,full_name AS fullName,email,mobile_number AS mobileNumber FROM users WHERE id=?",u.id()));}
    @PutMapping("/api/profile/password") public ResponseEntity<?> changePassword(@RequestBody Map<String,Object>b,HttpSession s){AuthService.AuthUser u=user(s);if(u==null)return unauthorized();String old=str(b,"currentPassword"),next=str(b,"newPassword");if(next.length()<8)return ResponseEntity.badRequest().body(Map.of("message","New password must contain at least 8 characters."));String saved=jdbc.queryForObject("SELECT password FROM users WHERE id=?",String.class,u.id());boolean ok=saved.startsWith("$2")?encoder.matches(old,saved):saved.equals(old);if(!ok)return ResponseEntity.badRequest().body(Map.of("message","Current password is incorrect."));jdbc.update("UPDATE users SET password=? WHERE id=?",encoder.encode(next),u.id());return ResponseEntity.ok(Map.of("message","Password changed successfully."));}

    // Administration workspace APIs
    @GetMapping("/api/admin/investigators") public ResponseEntity<?> investigators(HttpSession s){AuthService.AuthUser u=user(s);if(u==null)return unauthorized();if(!admin(u))return forbidden();return ResponseEntity.ok(jdbc.queryForList("SELECT id,username,full_name,email,mobile_number,created_at FROM users WHERE role='INVESTIGATOR' ORDER BY full_name"));}
    @PostMapping("/api/admin/investigators") public ResponseEntity<?> addInvestigator(@RequestBody Map<String,Object>b,HttpSession s){AuthService.AuthUser u=user(s);if(u==null)return unauthorized();if(!admin(u))return forbidden();String username=str(b,"username"),name=str(b,"fullName"),email=str(b,"email"),mobile=str(b,"mobileNumber"),password=str(b,"password");if(username.isBlank()||name.isBlank()||email.isBlank()||password.length()<8)return ResponseEntity.badRequest().body(Map.of("message","Enter username, name, email and a password of at least 8 characters."));try{jdbc.update("INSERT INTO users(username,password,role,full_name,email,mobile_number) VALUES(?,?,'INVESTIGATOR',?,?,?)",username,encoder.encode(password),name,email,mobile);}catch(Exception e){return ResponseEntity.badRequest().body(Map.of("message","Username or email already exists."));}audit(u,"CREATED_INVESTIGATOR","USER",username,name);return ResponseEntity.status(201).body(Map.of("message","Investigator account created."));}
    @PatchMapping("/api/admin/investigators/{id}") public ResponseEntity<?> updateInvestigator(@PathVariable int id,@RequestBody Map<String,Object>b,HttpSession s){AuthService.AuthUser u=user(s);if(u==null)return unauthorized();if(!admin(u))return forbidden();jdbc.update("UPDATE users SET full_name=?,email=?,mobile_number=? WHERE id=? AND role='INVESTIGATOR'",str(b,"fullName"),str(b,"email"),str(b,"mobileNumber"),id);audit(u,"UPDATED_INVESTIGATOR","USER",String.valueOf(id),str(b,"fullName"));return ResponseEntity.ok(Map.of("message","Investigator profile updated."));}
    @DeleteMapping("/api/admin/investigators/{id}") public ResponseEntity<?> deleteInvestigator(@PathVariable int id,HttpSession s){AuthService.AuthUser u=user(s);if(u==null)return unauthorized();if(!admin(u))return forbidden();jdbc.update("UPDATE users SET role='USER' WHERE id=? AND role='INVESTIGATOR'",id);jdbc.update("UPDATE cybercrime_reports SET assigned_investigator=NULL WHERE assigned_investigator=(SELECT username FROM users WHERE id=?)",id);audit(u,"DEACTIVATED_INVESTIGATOR","USER",String.valueOf(id),"Role changed to USER");return ResponseEntity.ok(Map.of("message","Investigator access removed."));}
    @GetMapping("/api/admin/users") public ResponseEntity<?> users(HttpSession s){AuthService.AuthUser u=user(s);if(u==null)return unauthorized();if(!admin(u))return forbidden();return ResponseEntity.ok(jdbc.queryForList("SELECT id,username,role,full_name,email,mobile_number,created_at FROM users ORDER BY id DESC"));}
    @PatchMapping("/api/admin/users/{id}/role") public ResponseEntity<?> updateUserRole(@PathVariable int id,@RequestBody Map<String,Object>b,HttpSession s){AuthService.AuthUser u=user(s);if(u==null)return unauthorized();if(!admin(u))return forbidden();String role=str(b,"role").toUpperCase();if(!Set.of("USER","INVESTIGATOR","ADMIN").contains(role))return ResponseEntity.badRequest().body(Map.of("message","Invalid account role."));if(id==u.id()&&!role.equals("ADMIN"))return ResponseEntity.badRequest().body(Map.of("message","You cannot remove your own administrator access."));jdbc.update("UPDATE users SET role=? WHERE id=?",role,id);audit(u,"CHANGED_USER_ROLE","USER",String.valueOf(id),role);return ResponseEntity.ok(Map.of("message","Account role updated."));}
    @GetMapping("/api/admin/audit") public ResponseEntity<?> auditLogs(HttpSession s){AuthService.AuthUser u=user(s);if(u==null)return unauthorized();if(!admin(u))return forbidden();return ResponseEntity.ok(jdbc.queryForList("SELECT l.*,u.username AS admin_username FROM admin_audit_logs l LEFT JOIN users u ON u.id=l.admin_user_id ORDER BY l.id DESC LIMIT 500"));}
    @GetMapping("/api/admin/reports/summary") public ResponseEntity<?> reportSummary(HttpSession s){AuthService.AuthUser u=user(s);if(u==null)return unauthorized();if(!admin(u))return forbidden();return ResponseEntity.ok(jdbc.queryForList("SELECT crime_category, status, COUNT(*) AS total FROM cybercrime_reports GROUP BY crime_category,status ORDER BY crime_category,status"));}

    private String str(Map<String,Object> b,String key){Object v=b.get(key);return v==null?"":String.valueOf(v).trim();}
}
