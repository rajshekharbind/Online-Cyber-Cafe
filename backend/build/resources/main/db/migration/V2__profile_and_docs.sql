CREATE TABLE addresses (
    id UUID PRIMARY KEY,
    village_town VARCHAR(255),
    district VARCHAR(255),
    state VARCHAR(255),
    pin_code VARCHAR(20)
);

CREATE TABLE student_profiles (
    user_id UUID PRIMARY KEY REFERENCES users(id),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    father_name VARCHAR(255),
    mother_name VARCHAR(255),
    dob DATE,
    gender VARCHAR(50),
    nationality VARCHAR(100),
    marital_status VARCHAR(50),
    current_address_id UUID REFERENCES addresses(id),
    permanent_address_id UUID REFERENCES addresses(id),
    category VARCHAR(100),
    sub_category VARCHAR(100),
    is_ews BOOLEAN DEFAULT FALSE,
    is_pwd BOOLEAN DEFAULT FALSE,
    is_ex_serviceman BOOLEAN DEFAULT FALSE
);

CREATE TABLE education_records (
    id UUID PRIMARY KEY,
    profile_id UUID REFERENCES student_profiles(user_id),
    level VARCHAR(100),
    board_university VARCHAR(255),
    institution VARCHAR(255),
    passing_year INTEGER,
    roll_number VARCHAR(100),
    percentage DOUBLE PRECISION,
    cgpa DOUBLE PRECISION
);

CREATE TABLE documents (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    type VARCHAR(100) NOT NULL,
    s3_key VARCHAR(512) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(100),
    file_size BIGINT,
    status VARCHAR(50) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL,
    verified_at TIMESTAMP
);
