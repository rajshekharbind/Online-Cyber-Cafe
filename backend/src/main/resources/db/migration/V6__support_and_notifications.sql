CREATE TABLE support_tickets (
    id UUID PRIMARY KEY,
    student_id UUID REFERENCES users(id),
    application_id UUID REFERENCES applications(id),
    subject VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE support_messages (
    id UUID PRIMARY KEY,
    ticket_id UUID REFERENCES support_tickets(id),
    sender_id UUID REFERENCES users(id),
    content TEXT NOT NULL,
    sent_at TIMESTAMP NOT NULL
);

CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_support_tickets_student ON support_tickets(student_id);
CREATE INDEX idx_support_messages_ticket ON support_messages(ticket_id);
CREATE INDEX idx_notifications_user ON notifications(user_id);
