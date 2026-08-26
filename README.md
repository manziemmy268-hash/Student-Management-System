# Student Lecturer Management System

[![CI](https://github.com/anomalyco/opencode/actions/workflows/ci.yml/badge.svg)](https://github.com/anomalyco/opencode/actions)

A desktop-based University Management System built with Java Swing, Hibernate ORM, and a layered MVC architecture. This portfolio project demonstrates full CRUD operations, role-based access control, report generation, HikariCP connection pooling, and embedded database support — all wrapped in a modern FlatLaf-themed UI.

---

## Architecture

```
┌──────────────────────────────────────────────┐
│                   View                        │
│  Swing UI (JPanel) — FlatLaf Dark/Light      │
│  StudentPanel, LecturerPanel, CoursePanel,    │
│  EnrollmentPanel, StatisticsPanel, etc.       │
├──────────────────────────────────────────────┤
│                Controller                     │
│  LoginController — thin, delegates to auth    │
├──────────────────────────────────────────────┤
│                 Service Layer                 │
│  Business rules, validation, cross-DAO       │
│  StudentService, EnrollmentService, etc.      │
│  Custom exceptions: ValidationException,      │
│  DuplicateEntityException, etc.              │
├──────────────────────────────────────────────┤
│          Data Access (DAO Layer)              │
│  Hibernate ORM — HQL + NativeQuery            │
│  @ManyToOne entity relationships              │
│  StudentDao, CourseDao, EnrollmentDao, etc.   │
├──────────────────────────────────────────────┤
│              Model (JPA Entities)             │
│  Student, Lecturer, Course, Enrollment,       │
│  Department, User — with @ManyToOne joins     │
├──────────────────────────────────────────────┤
│            Database & Connection Pool         │
│  HikariCP — MySQL (production)                │
│  Embedded H2 — Zero-setup demo mode           │
│  SessionFactoryManager singleton              │
└──────────────────────────────────────────────┘
```

## Features

- **Full CRUD** — Manage Students, Lecturers, Courses, Departments, and Enrollments
- **Role-Based Access** — Admin, Staff, Student, and Lecturer roles with distinct sidebar/menus
- **Dashboard** — At-a-glance statistics with summary cards and charts
- **Reports** — Generate reports with print preview and CSV export
- **Search** — Search across all entities with real-time filtering
- **Modern UI** — FlatLaf dark/light theme toggle via View menu
- **Hibernate ORM** — Entity relationships with `@ManyToOne` joins and HQL queries
- **HikariCP** — Production-grade connection pooling for both MySQL and H2
- **Demo Mode** — Zero-setup embedded H2 database with seed data
- **Constructor Injection** — All service impls support DI for testability
- **CI/CD** — GitHub Actions pipeline with Maven build and test

## Tech Stack

| Component          | Technology                  |
|--------------------|-----------------------------|
| Language           | Java 11                     |
| UI                 | Swing + FlatLaf 3.2.1       |
| ORM                | Hibernate 6.2.7             |
| Database           | MySQL 8.0 / H2 2.2          |
| Connection Pool    | HikariCP 5.0.1              |
| Security           | BCrypt (jBCrypt 0.4)        |
| Logging            | SLF4J 2.0 + Logback 1.4     |
| Testing            | JUnit 5 + Mockito 5         |
| Build              | Maven + Shade Plugin        |
| CI/CD              | GitHub Actions              |

## How to Run

### Demo Mode (Zero Setup)

Runs with an embedded H2 database — no external setup required.

```bash
mvn clean package -Pdemo
java -jar target/student-lecturer-management-1.0-SNAPSHOT.jar
```

### Demo Login Credentials

| Role      | Username / Email                | Password     |
|-----------|---------------------------------|--------------|
| Admin     | `admin`                         | `admin123`   |
| Staff     | `staff1`                        | `staff123`   |
| Lecturer  | `lecturer1`                     | `lecturer123`|
| Student   | `student1@university.edu`       | `student123` |

### Production Mode (MySQL Required)

```bash
# 1. Create the database
mysql -u root -p < sql/university_db.sql

# 2. Configure credentials (optional)
# Edit src/main/resources/db.properties or set environment variables:
#   JDBC_URL, DB_USERNAME, DB_PASSWORD

# 3. Build and run
mvn clean package
java -jar target/student-lecturer-management-1.0-SNAPSHOT.jar
```

## Running Tests

```bash
mvn test
```

All unit tests use mocked DAOs — no database required.

## Project Structure

```
src/
├── main/
│   ├── java/com/university/
│   │   ├── Main.java                    # Entry point
│   │   ├── database/
│   │   │   ├── DBConnection.java        # HikariCP pool, MySQL/H2 auto-detect
│   │   │   └── SessionFactoryManager.java # Hibernate SessionFactory singleton
│   │   ├── model/                       # JPA entities with @ManyToOne relationships
│   │   │   ├── Student.java
│   │   │   ├── Lecturer.java
│   │   │   ├── Course.java
│   │   │   ├── Enrollment.java
│   │   │   ├── Department.java
│   │   │   └── User.java
│   │   ├── dao/                         # Hibernate data access (HQL + NativeQuery)
│   │   │   ├── StudentDao.java
│   │   │   ├── LecturerDao.java
│   │   │   ├── CourseDao.java
│   │   │   ├── EnrollmentDao.java
│   │   │   ├── DepartmentDao.java
│   │   │   ├── UserDao.java
│   │   │   └── LookupDao.java
│   │   ├── service/                     # Business logic (interfaces + impls)
│   │   │   ├── StudentService.java / StudentServiceImpl.java
│   │   │   ├── CourseService.java / CourseServiceImpl.java
│   │   │   ├── EnrollmentService.java / EnrollmentServiceImpl.java
│   │   │   ├── ReportService.java / ReportServiceImpl.java
│   │   │   ├── UserService.java / UserServiceImpl.java
│   │   │   └── ...
│   │   ├── controller/
│   │   │   └── LoginController.java
│   │   ├── view/                        # Swing panels with role-based routing
│   │   │   ├── LoginFrame.java
│   │   │   ├── DashboardFrame.java      # CardLayout, sidebar, theme toggle
│   │   │   ├── StudentPanel.java
│   │   │   ├── LecturerPanel.java
│   │   │   ├── CoursePanel.java
│   │   │   ├── EnrollmentPanel.java
│   │   │   ├── StatisticsPanel.java
│   │   │   ├── ReportPanel.java
│   │   │   ├── DepartmentPanel.java
│   │   │   ├── UserPanel.java
│   │   │   ├── LecturerCoursePanel.java
│   │   │   └── StudentEnrollPanel.java
│   │   ├── dto/
│   │   │   └── ReportData.java
│   │   ├── exception/
│   │   │   ├── ValidationException.java
│   │   │   ├── DuplicateEntityException.java
│   │   │   └── ReferentialIntegrityException.java
│   │   └── utility/
│   │       ├── ValidationUtil.java
│   │       └── Constants.java
│   └── resources/
│       ├── db.properties
│       ├── schema-h2.sql
│       └── hibernate.cfg.xml
├── test/java/com/university/
│   ├── controller/LoginControllerTest.java
│   ├── dao/StudentDaoTest.java
│   ├── service/
│   │   ├── StudentServiceImplTest.java
│   │   ├── LecturerServiceImplTest.java
│   │   ├── CourseServiceImplTest.java
│   │   ├── EnrollmentServiceImplTest.java
│   │   ├── ReportServiceImplTest.java
│   │   └── UserServiceImplTest.java
│   └── utility/ValidationUtilTest.java
└── .github/workflows/ci.yml
```

## Key Design Decisions

1. **Constructor Injection** — All service impls have overloaded constructors accepting DAOs for testability, with no-arg constructors creating real DAOs for production.
2. **HQL for ORM, NativeQuery for complex joins** — Most DAOs use HQL with `LEFT JOIN FETCH` for entity relationships. Aggregate queries and complex subqueries use NativeQuery.
3. **Entity Relationships** — `@ManyToOne(fetch=LAZY)` with `insertable=false, updatable=false` on join columns, keeping int fields for writes.
4. **Maven Demo Profile** — `mvn package -Pdemo` bundles H2 for zero-setup demo.
5. **Role-Based Routing** — `DashboardFrame` uses `CardLayout` with role-specific sidebar items and panel routing.

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file.
