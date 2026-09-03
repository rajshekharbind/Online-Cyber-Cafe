CREATE TABLE application_status_history (
    id UUID PRIMARY KEY,
    application_id UUID REFERENCES applications(id),
    status VARCHAR(50) NOT NULL,
    remarks TEXT,
    changed_by_id UUID REFERENCES users(id),
    changed_at TIMESTAMP NOT NULL
);

CREATE TABLE assignments (
    id UUID PRIMARY KEY,
    application_id UUID REFERENCES applications(id) UNIQUE,
    employee_id UUID REFERENCES users(id),
    assigned_at TIMESTAMP NOT NULL,
    assigned_by_id UUID REFERENCES users(id)
);

CREATE INDEX idx_status_history_app ON application_status_history(application_id);
CREATE INDEX idx_assignments_employee ON assignments(employee_id);
