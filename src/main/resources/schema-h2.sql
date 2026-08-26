-- ============================================
-- H2 Compatible Schema + Seed Data
-- University Database Management System
-- ============================================

CREATE TABLE IF NOT EXISTS departments (
    department_id INT PRIMARY KEY AUTO_INCREMENT,
    department_name VARCHAR(100) NOT NULL,
    department_code VARCHAR(20) NOT NULL UNIQUE,
    location VARCHAR(100),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS lecturers (
    lecturer_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    hire_date DATE,
    department_id INT,
    lecturer_rank VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(department_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE,
    gender VARCHAR(10) NOT NULL CHECK (gender IN ('Male', 'Female', 'Other')),
    enrollment_date DATE NOT NULL,
    department_id INT,
    gpa DECIMAL(3,2) DEFAULT 0.00,
    status VARCHAR(20) DEFAULT 'Active' CHECK (status IN ('Active', 'Inactive', 'Graduated', 'Suspended')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(department_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(100) NOT NULL,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    credits INT NOT NULL DEFAULT 3,
    department_id INT,
    lecturer_id INT,
    description VARCHAR(1000),
    max_enrollment INT DEFAULT 50,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(department_id) ON DELETE SET NULL,
    FOREIGN KEY (lecturer_id) REFERENCES lecturers(lecturer_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    enrollment_date DATE NOT NULL,
    semester VARCHAR(30) NOT NULL,
    academic_year VARCHAR(10) NOT NULL,
    grade VARCHAR(2),
    status VARCHAR(20) DEFAULT 'Enrolled' CHECK (status IN ('Enrolled', 'Completed', 'Dropped', 'Failed')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS system_lookup (
    lookup_id INT PRIMARY KEY AUTO_INCREMENT,
    lookup_category VARCHAR(50) NOT NULL,
    lookup_value VARCHAR(100) NOT NULL,
    sort_order INT DEFAULT 0,
    is_active TINYINT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'Staff' CHECK (role IN ('Admin', 'Staff', 'Lecturer')),
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- Seed Data
-- ============================================

INSERT INTO departments (department_name, department_code, location, phone) VALUES
('Computer Science', 'CS', 'Building A, Room 101', '+250788100100'),
('Mathematics', 'MATH', 'Building A, Room 102', '+250788100200'),
('Physics', 'PHY', 'Building B, Room 201', '+250788100300'),
('Information Technology', 'IT', 'Building B, Room 202', '+250788100400'),
('Business Administration', 'BA', 'Building C, Room 301', '+250788100500');

INSERT INTO lecturers (first_name, last_name, email, password, phone, hire_date, department_id, lecturer_rank) VALUES
('Jean', 'Mugabo', 'jean.mugabo@university.rw', '$2a$10$wB9OPdErA5AWQ8XXXOf/mucZqX3b7PMWAXzP33b0lWQAB7UPsEU6K', '+250788200101', '2018-09-01', 1, 'Professor'),
('Marie', 'Uwimana', 'marie.uwimana@university.rw', '$2a$10$wB9OPdErA5AWQ8XXXOf/mucZqX3b7PMWAXzP33b0lWQAB7UPsEU6K', '+250788200102', '2019-01-15', 1, 'Associate Professor'),
('Patrick', 'Niyonzima', 'patrick.niyonzima@university.rw', '$2a$10$wB9OPdErA5AWQ8XXXOf/mucZqX3b7PMWAXzP33b0lWQAB7UPsEU6K', '+250788200103', '2017-06-20', 2, 'Professor'),
('Claire', 'Ingabire', 'claire.ingabire@university.rw', '$2a$10$wB9OPdErA5AWQ8XXXOf/mucZqX3b7PMWAXzP33b0lWQAB7UPsEU6K', '+250788200104', '2020-03-10', 3, 'Lecturer'),
('Emmanuel', 'Hakizimana', 'emmanuel.hakizimana@university.rw', '$2a$10$wB9OPdErA5AWQ8XXXOf/mucZqX3b7PMWAXzP33b0lWQAB7UPsEU6K', '+250788200105', '2016-11-01', 4, 'Senior Lecturer'),
('Diane', 'Mukamana', 'diane.mukamana@university.rw', '$2a$10$wB9OPdErA5AWQ8XXXOf/mucZqX3b7PMWAXzP33b0lWQAB7UPsEU6K', '+250788200106', '2021-02-01', 5, 'Lecturer'),
('Robert', 'Bizimana', 'robert.bizimana@university.rw', '$2a$10$wB9OPdErA5AWQ8XXXOf/mucZqX3b7PMWAXzP33b0lWQAB7UPsEU6K', '+250788200107', '2015-08-15', 2, 'Associate Professor'),
('Ange', 'Iradukunda', 'ange.iradukunda@university.rw', '$2a$10$wB9OPdErA5AWQ8XXXOf/mucZqX3b7PMWAXzP33b0lWQAB7UPsEU6K', '+250788200108', '2022-01-10', 3, 'Assistant Lecturer');

INSERT INTO students (first_name, last_name, email, password, phone, date_of_birth, gender, enrollment_date, department_id, gpa, status) VALUES
('David', 'Nsengimana', 'david.nsengimana@student.rw', '$2a$10$4BLFqW8pB0Zf1L.CAuAj7O7SUk3slY03YtBCk52hSZgi.7hI8pOMe', '+250788300101', '2001-05-15', 'Male', '2022-09-01', 1, 3.75, 'Active'),
('Grace', 'Uwera', 'grace.uwera@student.rw', '$2a$10$4BLFqW8pB0Zf1L.CAuAj7O7SUk3slY03YtBCk52hSZgi.7hI8pOMe', '+250788300102', '2000-08-22', 'Female', '2022-09-01', 1, 3.90, 'Active'),
('Samuel', 'Nshimiyimana', 'samuel.nshimiyimana@student.rw', '$2a$10$4BLFqW8pB0Zf1L.CAuAj7O7SUk3slY03YtBCk52hSZgi.7hI8pOMe', '+250788300103', '2001-03-10', 'Male', '2022-09-01', 2, 3.20, 'Active'),
('Esperance', 'Kayitesi', 'esperance.kayitesi@student.rw', '$2a$10$4BLFqW8pB0Zf1L.CAuAj7O7SUk3slY03YtBCk52hSZgi.7hI8pOMe', '+250788300104', '1999-12-05', 'Female', '2021-09-01', 3, 3.55, 'Active'),
('Jean', 'Habimana', 'jean.habimana@student.rw', '$2a$10$4BLFqW8pB0Zf1L.CAuAj7O7SUk3slY03YtBCk52hSZgi.7hI8pOMe', '+250788300105', '2002-01-20', 'Male', '2023-09-01', 4, 3.40, 'Active'),
('Chantal', 'Nyirahabimana', 'chantal.nyirahabimana@student.rw', '$2a$10$4BLFqW8pB0Zf1L.CAuAj7O7SUk3slY03YtBCk52hSZgi.7hI8pOMe', '+250788300106', '2000-07-18', 'Female', '2022-09-01', 1, 3.85, 'Active'),
('Eric', 'Byiringiro', 'eric.byiringiro@student.rw', '$2a$10$4BLFqW8pB0Zf1L.CAuAj7O7SUk3slY03YtBCk52hSZgi.7hI8pOMe', '+250788300107', '2001-09-30', 'Male', '2022-09-01', 5, 3.10, 'Active'),
('Alice', 'Mukamponda', 'alice.mukamponda@student.rw', '$2a$10$4BLFqW8pB0Zf1L.CAuAj7O7SUk3slY03YtBCk52hSZgi.7hI8pOMe', '+250788300108', '2000-04-12', 'Female', '2021-09-01', 2, 3.65, 'Active'),
('Olivier', 'Ndayisaba', 'olivier.ndayisaba@student.rw', '$2a$10$4BLFqW8pB0Zf1L.CAuAj7O7SUk3slY03YtBCk52hSZgi.7hI8pOMe', '+250788300109', '2002-06-25', 'Male', '2023-09-01', 4, 3.30, 'Active'),
('Immaculee', 'Sindikubwabo', 'immaculee.sindikubwabo@student.rw', '$2a$10$4BLFqW8pB0Zf1L.CAuAj7O7SUk3slY03YtBCk52hSZgi.7hI8pOMe', '+250788300110', '2001-11-08', 'Female', '2022-09-01', 3, 3.70, 'Active'),
('Patrick', 'Rukundo', 'patrick.rukundo@student.rw', '$2a$10$4BLFqW8pB0Zf1L.CAuAj7O7SUk3slY03YtBCk52hSZgi.7hI8pOMe', '+250788300111', '2000-02-14', 'Male', '2021-09-01', 1, 3.95, 'Active'),
('Cecile', 'Maniragaba', 'cecile.maniragaba@student.rw', '$2a$10$4BLFqW8pB0Zf1L.CAuAj7O7SUk3slY03YtBCk52hSZgi.7hI8pOMe', '+250788300112', '2002-08-03', 'Female', '2023-09-01', 5, 3.50, 'Active');

INSERT INTO courses (course_name, course_code, credits, department_id, lecturer_id, description, max_enrollment) VALUES
('Introduction to Programming', 'CS101', 3, 1, 1, 'Fundamental concepts of programming using Java', 60),
('Data Structures and Algorithms', 'CS201', 4, 1, 2, 'Advanced data structures and algorithm design', 50),
('Database Management Systems', 'CS301', 3, 1, 1, 'Relational databases and SQL', 45),
('Calculus I', 'MATH101', 4, 2, 3, 'Limits, derivatives, and integrals', 70),
('Linear Algebra', 'MATH201', 3, 2, 7, 'Vectors, matrices, and linear transformations', 55),
('Physics I', 'PHY101', 4, 3, 4, 'Mechanics and thermodynamics', 65),
('Web Development', 'IT101', 3, 4, 5, 'HTML, CSS, JavaScript fundamentals', 55),
('Database Administration', 'IT201', 3, 4, 5, 'Advanced database administration with MySQL', 40),
('Principles of Management', 'BA101', 3, 5, 6, 'Introduction to business management', 80),
('Operating Systems', 'CS302', 3, 1, 2, 'Process management, memory management, file systems', 45);

INSERT INTO enrollments (student_id, course_id, enrollment_date, semester, academic_year, grade, status) VALUES
(1, 1, '2022-09-05', 'September', '2022', 'A', 'Completed'),
(1, 2, '2023-01-10', 'January Intake', '2023', 'B+', 'Completed'),
(1, 3, '2023-09-05', 'September', '2023', NULL, 'Enrolled'),
(2, 1, '2022-09-05', 'September', '2022', 'A+', 'Completed'),
(2, 2, '2023-01-10', 'January Intake', '2023', 'A', 'Completed'),
(2, 3, '2023-09-05', 'September', '2023', NULL, 'Enrolled'),
(3, 4, '2022-09-05', 'September', '2022', 'B', 'Completed'),
(3, 5, '2023-01-10', 'January Intake', '2023', 'B+', 'Completed'),
(4, 6, '2021-09-05', 'September', '2021', 'A', 'Completed'),
(4, 1, '2022-09-05', 'September', '2022', 'B+', 'Completed'),
(5, 7, '2023-09-05', 'September', '2023', NULL, 'Enrolled'),
(5, 8, '2024-01-10', 'January Intake', '2024', NULL, 'Enrolled'),
(6, 1, '2022-09-05', 'September', '2022', 'A', 'Completed'),
(6, 2, '2023-01-10', 'January Intake', '2023', 'A-', 'Completed'),
(7, 9, '2022-09-05', 'September', '2022', 'B', 'Completed'),
(8, 4, '2021-09-05', 'September', '2021', 'A-', 'Completed'),
(8, 5, '2022-01-10', 'January Intake', '2022', 'A', 'Completed'),
(9, 7, '2023-09-05', 'September', '2023', NULL, 'Enrolled'),
(10, 6, '2022-09-05', 'September', '2022', 'B+', 'Completed'),
(11, 1, '2021-09-05', 'September', '2021', 'A+', 'Completed'),
(11, 2, '2022-01-10', 'January Intake', '2022', 'A', 'Completed'),
(11, 3, '2022-09-05', 'September', '2022', 'A', 'Completed'),
(12, 9, '2023-09-05', 'September', '2023', NULL, 'Enrolled');

INSERT INTO users (username, password, full_name, role) VALUES
('admin', '$2a$10$aa58R2hwVaITdWpCFSnrQud/0kJKJZkDIKpTzQA6Sd2jFRNeIN7P6', 'System Administrator', 'Admin'),
('staff1', '$2a$10$ltEFMv5u3ZDIKAO7hEcMaOMi2xDcgG.TI2y92Xz68cZGvc1bvIjaq', 'John Staff', 'Staff'),
('lecturer1', '$2a$10$wB9OPdErA5AWQ8XXXOf/mucZqX3b7PMWAXzP33b0lWQAB7UPsEU6K', 'Jean Mugabo', 'Lecturer'),
('lecturer2', '$2a$10$wB9OPdErA5AWQ8XXXOf/mucZqX3b7PMWAXzP33b0lWQAB7UPsEU6K', 'Marie Uwimana', 'Lecturer'),
('staff2', '$2a$10$ltEFMv5u3ZDIKAO7hEcMaOMi2xDcgG.TI2y92Xz68cZGvc1bvIjaq', 'Mary Staff', 'Staff');

INSERT INTO system_lookup (lookup_category, lookup_value, sort_order, is_active) VALUES
('gender', 'Male', 1, 1),
('gender', 'Female', 2, 1),
('gender', 'Other', 3, 1),
('student_status', 'Active', 1, 1),
('student_status', 'Inactive', 2, 1),
('student_status', 'Graduated', 3, 1),
('student_status', 'Suspended', 4, 1),
('enrollment_status', 'Enrolled', 1, 1),
('enrollment_status', 'Completed', 2, 1),
('enrollment_status', 'Dropped', 3, 1),
('enrollment_status', 'Failed', 4, 1),
('semester', 'January Intake', 1, 1),
('semester', 'Summer', 2, 1),
('semester', 'September', 3, 1),
('academic_year', '2021', 1, 1),
('academic_year', '2022', 2, 1),
('academic_year', '2023', 3, 1),
('academic_year', '2024', 4, 1),
('academic_year', '2025', 5, 1),
('academic_year', '2026', 6, 1),
('academic_year', '2027', 7, 1),
('academic_year', '2028', 8, 1),
('grade', '', 0, 1),
('grade', 'A+', 1, 1),
('grade', 'A', 2, 1),
('grade', 'A-', 3, 1),
('grade', 'B+', 4, 1),
('grade', 'B', 5, 1),
('grade', 'B-', 6, 1),
('grade', 'C+', 7, 1),
('grade', 'C', 8, 1),
('grade', 'C-', 9, 1),
('grade', 'D+', 10, 1),
('grade', 'D', 11, 1),
('grade', 'F', 12, 1),
('lecturer_rank', 'Assistant Lecturer', 1, 1),
('lecturer_rank', 'Lecturer', 2, 1),
('lecturer_rank', 'Senior Lecturer', 3, 1),
('lecturer_rank', 'Associate Professor', 4, 1),
('lecturer_rank', 'Professor', 5, 1);
