CREATE TABLE order_notifications (
     id SERIAL PRIMARY KEY,
     order_id VARCHAR(255) NOT NULL,
     user_id VARCHAR(255) NOT NULL,
     product_name VARCHAR(255),
     notification_status VARCHAR(50) DEFAULT 'pending',  -- Trạng thái thông báo (pending, sent, failed)
     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
