# Student Lecturer Management System

A desktop application that helps a university manage its students, lecturers, courses, and enrollments — the way a real admissions office would use it.

---

## How It Works (Real World)

### The Problem
A university has hundreds of students, dozens of lecturers, and courses spread across departments. Managing who is enrolled in what, assigning lecturers to courses, tracking grades, and generating reports is normally done with paper forms or messy spreadsheets. This system replaces all of that with one application.

### What Each Person Sees

**An Admin logs in and sees everything:**
- Add new departments (e.g. Computer Science, Mathematics)
- Register new students and assign them to departments
- Hire lecturers and assign them to departments
- Create courses and assign a lecturer to each course
- Enroll students into courses for a given semester
- View all grades and enrollment status
- Generate reports (e.g. "How many students are in the CS department?")
- Manage system user accounts (who can log in and what they can do)

**A Lecturer logs in and sees only their work:**
- View only the courses assigned to them
- See which students are enrolled in each of their courses
- Enter and update grades for those students

**A Student logs in and sees only their academics:**
- Browse available courses they can enroll in
- Enroll in a course for the current semester
- See all their enrollments and grades
- Track their GPA and enrollment status

### A Typical Day at the University

1. **September intake** — The admin registers 50 new students, assigns them to departments
2. **Course registration** — The admin creates new courses and assigns lecturers
3. **Student enrollment** — Students log in and enroll in courses for the semester
4. **During the semester** — Lecturers log in, see their assigned courses, and track enrolled students
5. **End of semester** — Lecturers enter grades (A, B+, etc.) for each student
6. **Reporting** — The admin generates a report showing pass/fail rates per department and exports it as CSV

### What the System Tracks

| Entity | What It Holds |
|--------|--------------|
| **Department** | Name, code (e.g. "CS"), location, phone |
| **Student** | Name, email, phone, DOB, gender, GPA, status (Active/Graduated/etc.) |
| **Lecturer** | Name, email, phone, hire date, rank (Professor, Lecturer, etc.) |
| **Course** | Name, code (e.g. "CS101"), credits, max enrollment, assigned lecturer |
| **Enrollment** | Which student took which course, in which semester/year, and their grade |
| **User** | Login credentials, role (Admin/Staff/Lecturer) |

---

## Features

- **Full CRUD** — Add, edit, delete, and search for all entities
- **Role-Based Access** — Each role sees a different sidebar and different screens
- **Dashboard** — Shows total students, lecturers, courses, and enrollments at a glance
- **Reports** — Generate reports with print preview and CSV export
- **Theme Toggle** — Switch between dark mode and light mode from the View menu
- **Demo Mode** — Run instantly with a built-in database (H2) — no MySQL installation needed

---

## How to Run

### Demo Mode (No Setup Required)

```bash
mvn clean package -Pdemo -DskipTests
java -jar target/student-lecturer-management-1.0-SNAPSHOT.jar
```

### Login Credentials (Demo)

| Role      | Username / Email                | Password     |
|-----------|---------------------------------|--------------|
| Admin     | `admin`                         | `admin123`   |
| Staff     | `staff1`                        | `staff123`   |
| Lecturer  | `lecturer1`                     | `lecturer123`|
| Student   | `david.nsengimana@student.rw`   | `student123` |

### Production Mode (MySQL Required)

```bash
# 1. Create the database
mysql -u root -p < sql/university_db.sql

# 2. Build and run (connects to MySQL on localhost)
mvn clean package -DskipTests
java -jar target/student-lecturer-management-1.0-SNAPSHOT.jar
```

---

## Running Tests

```bash
mvn test
```

161 unit tests covering services, controllers, DAOs, and validation utilities.

---

## Tech Stack

| Component       | Technology             |
|-----------------|------------------------|
| Language        | Java 11                |
| UI              | Swing + FlatLaf        |
| ORM             | Hibernate 6            |
| Database        | MySQL 8 / H2 (demo)   |
| Connection Pool | HikariCP               |
| Security        | BCrypt password hashing|
| Testing         | JUnit 5 + Mockito     |
| Build           | Maven + Shade Plugin   |

---

## License

MIT License — see [LICENSE](LICENSE).
