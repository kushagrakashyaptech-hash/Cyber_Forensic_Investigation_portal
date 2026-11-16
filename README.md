# Cyber Forensic Investigation Portal

A Java Swing desktop application for reporting and managing cybercrime complaints. Citizens can file complaints, upload digital evidence and track progress. Admins can review complaints, update their status, keep an investigation timeline and view reports and analytics.

**Tech used:** Java (Swing) · MySQL · JDBC (MySQL Connector/J)
No Gradle or Maven needed. It is compiled and run with plain `javac` and `java` from CMD.

---

## Features

### Citizen (User)
- **Register / Login** with username or email
- **Report Cybercrime**: file a complaint with incident, victim and suspect details. A Report ID like `CF-2026-0001` is generated automatically.
- **My Reports**: list of your own complaints, with a full details view
- **Track Complaint**: current status, assigned unit, remarks and a 6-stage progress bar
- **Submit Evidence**: attach a file to your complaint. Its SHA-256 hash is calculated and stored.
- **Notifications**: alerts when a complaint is submitted, evidence is uploaded or the status changes
- **My Profile**: edit name, email and mobile, and change your password

### Admin
- **Case Management**: add investigation cases and view all cases
- **Evidence Management**: add evidence files to a case, with a SHA-256 hash
- **Citizen Reports**: view all complaints, update status, assigned unit and remarks (the citizen is notified), and open the **full report** with the evidence the citizen uploaded
- **Investigation Timeline**: event-by-event history of a complaint (submitted, evidence uploaded, status updated, admin notes), plus **Add Note**
- **Forensic Reports & Analytics**: total, pending, under investigation and resolved counts, total amount lost, evidence count, complaints by category and by status, and **Export CSV**

---

## Project Structure

```
CyberForensicPortal/
├── src/
│   ├── Frontend.java     Login, Register, Admin menu and all admin windows, main()
│   ├── UserPortal.java   Citizen portal (menu and all user windows)
│   └── Backend.java      All MySQL/JDBC code: tables, login, reports, evidence,
│                         notifications, timeline, analytics, CSV export
├── sql/
│   └── schema.sql        Reference SQL (optional; tables are created automatically)
├── out/                  Compiled .class files (created by javac)
└── README.md
```

---

## Requirements

- **Java JDK** 11 or newer (check with `java -version`)
- **MySQL Server** 8.x, installed and running
- **MySQL Connector/J** jar, for example `mysql-connector-j-26.7.0.jar`

---

## Setup

### 1. Set your MySQL password
Open `src/Backend.java` and change line 15 to **your** MySQL root password:
```java
private static final String PASSWORD = "your_mysql_password";
```
If this is wrong, the app shows `Access denied for user 'root'@'localhost'`.

### 2. Put the connector jar in place
Copy `mysql-connector-j-26.7.0.jar` into the project folder, next to `src`.
If you keep it somewhere else, change the jar path in the commands below.

### 3. Database
Nothing to do manually. On the first run the app automatically creates:
- the database `cyber_forensics`
- the tables `users`, `cybercrime_reports`, `evidence`, `user_notifications`, `cases`, `investigation_timeline`
- two demo accounts (see below)

---

## How to Run (Windows CMD)

Make sure MySQL is running, then:

```bat
cd /d "path\to\CyberForensicPortal"

javac -cp "out;mysql-connector-j-26.7.0.jar" -d out src\Backend.java src\UserPortal.java src\Frontend.java

java -cp "out;mysql-connector-j-26.7.0.jar" Frontend
```

Compile again after changing any `.java` file.

---

## Demo Accounts

| Role  | Username | Password   |
|-------|----------|------------|
| Admin | `admin`  | `admin123` |
| User  | `user1`  | `user123`  |

New accounts created with **Register** are always normal users.

---

## Complaint Status Flow

```
SUBMITTED → UNDER REVIEW → UNDER INVESTIGATION → EVIDENCE ANALYSIS → RESOLVED / CLOSED
```
Each status change by the admin updates the complaint, sends a notification to the citizen and adds an entry to the investigation timeline.

---

## Known Limitations

- Passwords are stored as plain text (no hashing).
- Track Complaint does not check whether the report belongs to the logged-in user.
- Evidence files are not copied anywhere. Only the file path and SHA-256 hash are stored, so the file must stay on the same computer.
- The SHA-256 hash is stored but not re-verified later.
- Admin cases are not linked to citizen complaints, and cases cannot be edited or closed.
- The timeline only contains events recorded after the timeline feature was added.
- The MySQL password is written in the source code (`Backend.java`).

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| `error: file not found: src\Backend.java` | CMD is in the wrong folder. Run the `cd /d` command first. |
| `Access denied for user 'root'@'localhost'` | Wrong MySQL password on line 15 of `Backend.java`. Fix it and compile again. |
| `Communications link failure` | MySQL Server is not running. Start it from Services (`services.msc`). |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | The connector jar path in `-cp` is wrong. |
| `Could not find or load main class Frontend` | Compile first, and run from the project folder. |
