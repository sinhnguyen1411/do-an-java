CREATE DATABASE IF NOT EXISTS elearning CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE elearning;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  full_name VARCHAR(120) NOT NULL,
  email VARCHAR(160) NOT NULL UNIQUE,
  phone VARCHAR(40) NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  role ENUM('ADMIN','INSTRUCTOR','STUDENT') NOT NULL,
  status ENUM('PENDING','ACTIVE','SUSPENDED') NOT NULL DEFAULT 'PENDING',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS courses (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  instructor_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  description TEXT NULL,
  status ENUM('DRAFT','PUBLISHED') NOT NULL DEFAULT 'DRAFT',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_courses_instructor FOREIGN KEY (instructor_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS lessons (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  content_json TEXT NOT NULL,
  order_index INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_lessons_course FOREIGN KEY (course_id) REFERENCES courses(id)
);

CREATE TABLE IF NOT EXISTS enrollments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  course_id BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_enrollments_student_course (student_id, course_id),
  CONSTRAINT fk_enrollments_student FOREIGN KEY (student_id) REFERENCES users(id),
  CONSTRAINT fk_enrollments_course FOREIGN KEY (course_id) REFERENCES courses(id)
);

CREATE TABLE IF NOT EXISTS lesson_progress (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  lesson_id BIGINT NOT NULL,
  is_completed TINYINT(1) NOT NULL DEFAULT 0,
  completed_at TIMESTAMP NULL,
  last_opened_at TIMESTAMP NULL,
  UNIQUE KEY uq_lesson_progress_student_lesson (student_id, lesson_id),
  CONSTRAINT fk_progress_student FOREIGN KEY (student_id) REFERENCES users(id),
  CONSTRAINT fk_progress_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id)
);

CREATE TABLE IF NOT EXISTS qa_questions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  lesson_id BIGINT NOT NULL,
  student_id BIGINT NOT NULL,
  question_text TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status ENUM('OPEN','ANSWERED') NOT NULL DEFAULT 'OPEN',
  CONSTRAINT fk_questions_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id),
  CONSTRAINT fk_questions_student FOREIGN KEY (student_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS qa_answers (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  question_id BIGINT NOT NULL,
  instructor_id BIGINT NOT NULL,
  answer_text TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_answers_question FOREIGN KEY (question_id) REFERENCES qa_questions(id),
  CONSTRAINT fk_answers_instructor FOREIGN KEY (instructor_id) REFERENCES users(id)
);
