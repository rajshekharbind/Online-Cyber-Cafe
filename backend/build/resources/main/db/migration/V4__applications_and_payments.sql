CREATE TABLE application_snapshots (
    id UUID PRIMARY KEY,
    profile_data_json TEXT NOT NULL,
    documents_data_json TEXT NOT NULL,
    snapped_at TIMESTAMP NOT NULL
);

CREATE TABLE payments (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    amount DOUBLE PRECISION NOT NULL,
    official_fee DOUBLE PRECISION NOT NULL,
    service_fee DOUBLE PRECISION NOT NULL,
    razorpay_order_id VARCHAR(255) NOT NULL,
    razorpay_payment_id VARCHAR(255),
    razorpay_signature VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    verified_at TIMESTAMP
);

CREATE TABLE applications (
    id UUID PRIMARY KEY,
    student_id UUID REFERENCES users(id),
    job_id UUID REFERENCES jobs(id),
    snapshot_id UUID REFERENCES application_snapshots(id),
    payment_id UUID REFERENCES payments(id),
    status VARCHAR(50) NOT NULL,
    application_number VARCHAR(255),
    registration_number VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_applications_student ON applications(student_id);
CREATE INDEX idx_payments_order_id ON payments(razorpay_order_id);
