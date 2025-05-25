-- USERS table
CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    registration_date TIMESTAMP
);

-- TASKS table
CREATE TABLE tasks (
    task_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(user_id),
    task_text VARCHAR(255) NOT NULL,
    is_complete BOOLEAN,
    is_deleted BOOLEAN,
    is_overdue_notified BOOLEAN,
    creation_date TIMESTAMP,
    target_date TIMESTAMP
);

-- NOTIFICATIONS table
CREATE TABLE notifications (
    notification_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(user_id),
    task_id BIGINT REFERENCES tasks(task_id),
    text VARCHAR(255) NOT NULL,
    is_read BOOLEAN,
    date TIMESTAMP
);
