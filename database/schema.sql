-- ============================================
-- Event Management System - MySQL Database Schema
-- ============================================

-- Create Database
CREATE DATABASE IF NOT EXISTS event_management_db;
USE event_management_db;

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS registrations;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS users;

-- ============================================
-- Table: users
-- ============================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'ORGANIZER', 'PARTICIPANT') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table: events
-- ============================================
CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_date DATETIME NOT NULL,
    location VARCHAR(500) NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    organizer_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (organizer_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_status (status),
    INDEX idx_organizer (organizer_id),
    INDEX idx_event_date (event_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table: registrations
-- ============================================
CREATE TABLE registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    participant_id BIGINT NOT NULL,
    attended BOOLEAN DEFAULT FALSE,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (participant_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_registration (event_id, participant_id),
    INDEX idx_event (event_id),
    INDEX idx_participant (participant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Insert Sample Data
-- ============================================

-- Insert Sample Users
-- Password: admin123 (Note: In production, these should be hashed)
INSERT INTO users (name, email, password, role) VALUES
('Admin User', 'admin@example.com', 'admin123', 'ADMIN'),
('John Organizer', 'john@example.com', 'pass123', 'ORGANIZER'),
('Jane Organizer', 'jane@example.com', 'pass123', 'ORGANIZER'),
('Alice Participant', 'alice@example.com', 'pass123', 'PARTICIPANT'),
('Bob Participant', 'bob@example.com', 'pass123', 'PARTICIPANT'),
('Charlie Participant', 'charlie@example.com', 'pass123', 'PARTICIPANT');

-- Insert Sample Events
INSERT INTO events (title, description, event_date, location, status, organizer_id) VALUES
('Tech Conference 2025', 'Annual technology conference featuring latest innovations in AI and Machine Learning', '2025-11-15 09:00:00', 'Convention Center, Chennai', 'APPROVED', 2),
('Web Development Workshop', 'Hands-on workshop covering React, Node.js, and modern web development practices', '2025-11-20 10:00:00', 'Tech Hub, Anna Nagar', 'APPROVED', 2),
('Data Science Meetup', 'Monthly meetup for data science enthusiasts to share knowledge and network', '2025-11-25 18:00:00', 'Co-working Space, T Nagar', 'PENDING', 3),
('Mobile App Development', 'Learn to build cross-platform mobile apps using Flutter and React Native', '2025-12-01 14:00:00', 'Innovation Lab, Velachery', 'APPROVED', 3),
('Cloud Computing Seminar', 'Deep dive into AWS, Azure, and Google Cloud Platform services', '2025-12-05 11:00:00', 'IT Park, OMR', 'PENDING', 2);

-- Insert Sample Registrations
INSERT INTO registrations (event_id, participant_id, attended) VALUES
(1, 4, TRUE),   -- Alice registered and attended Tech Conference
(1, 5, TRUE),   -- Bob registered and attended Tech Conference
(1, 6, FALSE),  -- Charlie registered but didn't attend Tech Conference
(2, 4, FALSE),  -- Alice registered for Workshop (upcoming)
(2, 5, FALSE),  -- Bob registered for Workshop (upcoming)
(4, 4, FALSE),  -- Alice registered for Mobile App Development
(4, 6, FALSE);  -- Charlie registered for Mobile App Development

-- ============================================
-- Useful Queries for Testing
-- ============================================

-- View all users with their roles
-- SELECT id, name, email, role, created_at FROM users;

-- View all events with organizer details
-- SELECT e.id, e.title, e.event_date, e.location, e.status, u.name as organizer_name
-- FROM events e
-- JOIN users u ON e.organizer_id = u.id
-- ORDER BY e.event_date;

-- View all registrations with participant and event details
-- SELECT r.id, e.title as event_title, u.name as participant_name, r.attended, r.registered_at
-- FROM registrations r
-- JOIN events e ON r.event_id = e.id
-- JOIN users u ON r.participant_id = u.id
-- ORDER BY r.registered_at DESC;

-- Count participants per event
-- SELECT e.title, COUNT(r.id) as participant_count
-- FROM events e
-- LEFT JOIN registrations r ON e.id = r.event_id
-- GROUP BY e.id, e.title
-- ORDER BY participant_count DESC;

-- Get attendance statistics
-- SELECT
--     e.title,
--     COUNT(r.id) as total_registered,
--     SUM(CASE WHEN r.attended = TRUE THEN 1 ELSE 0 END) as attended_count,
--     SUM(CASE WHEN r.attended = FALSE THEN 1 ELSE 0 END) as absent_count
-- FROM events e
-- LEFT JOIN registrations r ON e.id = r.event_id
-- GROUP BY e.id, e.title;

-- ============================================
-- Views for Easy Data Access
-- ============================================

-- Create view for event summary
CREATE OR REPLACE VIEW event_summary AS
SELECT
    e.id,
    e.title,
    e.description,
    e.event_date,
    e.location,
    e.status,
    u.name as organizer_name,
    u.email as organizer_email,
    COUNT(r.id) as total_registrations,
    e.created_at
FROM events e
JOIN users u ON e.organizer_id = u.id
LEFT JOIN registrations r ON e.id = r.event_id
GROUP BY e.id, e.title, e.description, e.event_date, e.location, e.status, u.name, u.email, e.created_at;

-- Create view for participant registrations
CREATE OR REPLACE VIEW participant_registrations AS
SELECT
    r.id as registration_id,
    u.name as participant_name,
    u.email as participant_email,
    e.title as event_title,
    e.event_date,
    e.location,
    r.attended,
    r.registered_at
FROM registrations r
JOIN users u ON r.participant_id = u.id
JOIN events e ON r.event_id = e.id;

-- ============================================
-- Stored Procedures (Optional)
-- ============================================

-- Procedure to get event statistics
DELIMITER //
CREATE PROCEDURE GetEventStatistics()
BEGIN
    SELECT
        COUNT(*) as total_events,
        SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) as pending_events,
        SUM(CASE WHEN status = 'APPROVED' THEN 1 ELSE 0 END) as approved_events,
        SUM(CASE WHEN status = 'REJECTED' THEN 1 ELSE 0 END) as rejected_events
    FROM events;
END //
DELIMITER ;

-- Procedure to get user statistics
DELIMITER //
CREATE PROCEDURE GetUserStatistics()
BEGIN
    SELECT
        COUNT(*) as total_users,
        SUM(CASE WHEN role = 'ADMIN' THEN 1 ELSE 0 END) as admin_count,
        SUM(CASE WHEN role = 'ORGANIZER' THEN 1 ELSE 0 END) as organizer_count,
        SUM(CASE WHEN role = 'PARTICIPANT' THEN 1 ELSE 0 END) as participant_count
    FROM users;
END //
DELIMITER ;

-- ============================================
-- Sample Data Verification Queries
-- ============================================

-- Verify all tables are created
SELECT 'Database setup completed successfully!' as message;

-- Show table counts
SELECT 'users' as table_name, COUNT(*) as record_count FROM users
UNION ALL
SELECT 'events' as table_name, COUNT(*) as record_count FROM events
UNION ALL
SELECT 'registrations' as table_name, COUNT(*) as record_count FROM registrations;

-- ============================================
-- End of Schema
-- ============================================
SELECT * FROM users;