CYBER FORENSIC INVESTIGATION PORTAL — UPDATED BUILD

This ZIP replaces the previous web-backend folder. It retains the existing Gradle Wrapper (no Maven) and the current login/registration API, and adds working citizen portal modules.

WHAT IS INCLUDED
- Citizen registration and login with BCrypt password hashing (legacy plaintext accounts are upgraded at login)
- Dashboard counts from MySQL
- Complaint submission and user-owned complaint listing
- Complaint tracking with server-side ownership checks
- Evidence upload and SHA-256 digest calculation
- Notifications and mark-as-read
- Profile editing and password change
- Responsive portal UI with working navigation and no “Coming soon” placeholders

REPLACE
1. Stop the currently running server with Ctrl+C in its CMD window.
2. Rename your existing web-backend folder to web-backend-backup.
3. Extract this ZIP. Copy the extracted web-backend folder into C:\CyberForensicPortal.
4. Do not delete your backup until the new version is verified.

DATABASE
- This build uses the existing database named cyber_forensics and its existing tables.
- SQL reference is in database/schema.sql. Do not rerun the CREATE DATABASE / CREATE TABLE statements over an existing installation unless you have backed up your data.
- Existing tables must include users, cybercrime_reports, evidence, and user_notifications as shown in database/schema.sql.
- Keep your database password private. Set it only in your local CMD before starting the server.

RUN (CMD)
cd /d C:\CyberForensicPortal\web-backend
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "DB_USERNAME=root"
set "DB_PASSWORD=YOUR_LOCAL_MYSQL_PASSWORD"
C:\Tools\gradle-8.14.3\bin\gradle.bat bootRun

Replace YOUR_LOCAL_MYSQL_PASSWORD with your actual local MySQL password in your own CMD only. Never share screenshots that show it and never commit it to GitHub.

OPEN
http://localhost:8080

NOTES
- Uploaded evidence files are stored in the evidence-uploads folder beside the application working directory. Back this folder up along with MySQL.
- This revision also fixes the dashboard statistics query and stores the actual calculated SHA-256 digest in the evidence table (rather than a status string).\n\nThis is a functional academic project build, not a certified production cybercrime case-management system. A production deployment would require stronger access controls, audit logging, encrypted file storage, malware scanning, and formal operational/security review.
