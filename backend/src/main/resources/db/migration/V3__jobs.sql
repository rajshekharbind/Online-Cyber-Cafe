CREATE TABLE jobs (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    organization VARCHAR(255) NOT NULL,
    department VARCHAR(255),
    is_government BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(50) NOT NULL,
    qualification_required TEXT NOT NULL,
    min_age INTEGER,
    max_age INTEGER,
    official_fee DOUBLE PRECISION NOT NULL,
    service_fee DOUBLE PRECISION NOT NULL,
    start_date TIMESTAMP NOT NULL,
    deadline TIMESTAMP NOT NULL,
    official_notification_url VARCHAR(512),
    official_website_url VARCHAR(512),
    eligibility_rules_json TEXT,
    created_at TIMESTAMP NOT NULL,
    last_verified_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_jobs_status ON jobs(status);
CREATE INDEX idx_jobs_deadline ON jobs(deadline);
