USE elearning;

-- Default password for all seeded users: "password"
-- BCrypt hash generated with cost 10
INSERT INTO users (id, full_name, email, phone, password_hash, role, status) VALUES
  (1, 'Admin User', 'admin@elearn.local', '1000000001', '$2a$10$CAUCN8hhz3y2cuWA2pJs8.CuiVarViW5u9IJ7rnAjvODBe8jeX9e.', 'ADMIN', 'ACTIVE'),
  (2, 'Instructor One', 'instructor@elearn.local', '1000000002', '$2a$10$CAUCN8hhz3y2cuWA2pJs8.CuiVarViW5u9IJ7rnAjvODBe8jeX9e.', 'INSTRUCTOR', 'ACTIVE'),
  (3, 'Student One', 'student@elearn.local', '1000000003', '$2a$10$CAUCN8hhz3y2cuWA2pJs8.CuiVarViW5u9IJ7rnAjvODBe8jeX9e.', 'STUDENT', 'ACTIVE');

INSERT INTO courses (id, instructor_id, title, description, status) VALUES
  (1, 2, 'Java Foundations', 'Core Java, OOP, and basic data structures.', 'PUBLISHED'),
  (2, 2, 'SQL for Beginners', 'Learn MySQL basics and queries.', 'DRAFT');

INSERT INTO lessons (id, course_id, title, content_json, order_index) VALUES
  (1, 1, 'Welcome to Java', '[{"type":"text","value":"Welcome to Java Foundations."},{"type":"link","value":"https://docs.oracle.com/javase/tutorial/"},{"type":"youtube","value":"https://www.youtube.com/watch?v=goXKXq3C5h0"}]', 1),
  (2, 1, 'Classes and Objects', '[{"type":"text","value":"Classes define objects. This lesson covers fields, methods, and constructors."}]', 2),
  (3, 2, 'SQL Intro', '[{"type":"text","value":"SQL lets you query relational databases."}]', 1);

INSERT INTO enrollments (id, student_id, course_id) VALUES
  (1, 3, 1);

INSERT INTO lesson_progress (id, student_id, lesson_id, is_completed, completed_at, last_opened_at) VALUES
  (1, 3, 1, 1, NOW(), NOW()),
  (2, 3, 2, 0, NULL, NOW());

INSERT INTO qa_questions (id, lesson_id, student_id, question_text, status) VALUES
  (1, 1, 3, 'Is Java pass-by-value or pass-by-reference?', 'OPEN');

INSERT INTO qa_answers (id, question_id, instructor_id, answer_text) VALUES
  (1, 1, 2, 'Java is pass-by-value for all parameters. Object references are passed by value.');
