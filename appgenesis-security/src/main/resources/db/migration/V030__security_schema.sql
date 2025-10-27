-- Departments
CREATE TABLE IF NOT EXISTS departments (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  description VARCHAR(255),
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  manager_id BIGINT NULL,
  created_at TIMESTAMP NULL,
  updated_at TIMESTAMP NULL
);

-- Users
CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  full_name VARCHAR(120) NOT NULL,
  email VARCHAR(180) NOT NULL,
  department_id BIGINT NULL REFERENCES departments(id) ON DELETE SET NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMP NULL,
  updated_at TIMESTAMP NULL,
  password_hash TEXT NULL,
  last_login_at TIMESTAMP NULL,
  CONSTRAINT uk_users_username UNIQUE (username),
  CONSTRAINT uk_users_email UNIQUE (email)
);

-- Groups
CREATE TABLE IF NOT EXISTS groups (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(80) NOT NULL,
  description VARCHAR(255),
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMP NULL,
  updated_at TIMESTAMP NULL,
  CONSTRAINT uk_groups_name UNIQUE (name)
);

-- Permissions
CREATE TABLE IF NOT EXISTS permissions (
  id BIGSERIAL PRIMARY KEY,
  code VARCHAR(120) NOT NULL,
  description VARCHAR(255),
  scope VARCHAR(80),
  created_at TIMESTAMP NULL,
  updated_at TIMESTAMP NULL,
  CONSTRAINT uk_permissions_code UNIQUE (code)
);

-- Link tables
CREATE TABLE IF NOT EXISTS user_group (
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  group_id BIGINT NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
  PRIMARY KEY (user_id, group_id)
);

CREATE TABLE IF NOT EXISTS group_permission (
  group_id BIGINT NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
  permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
  PRIMARY KEY (group_id, permission_id)
);

CREATE INDEX IF NOT EXISTS idx_users_department_id ON users(department_id);

--refresh token
CREATE TABLE refresh_tokens (
                                id           BIGSERIAL PRIMARY KEY,
                                user_id      BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                token_hash   VARCHAR(128) NOT NULL UNIQUE,      -- SHA-256 (hex) ou outro formato
                                expires_at   TIMESTAMPTZ NOT NULL,
                                revoked      BOOLEAN NOT NULL DEFAULT FALSE,
                                created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_user ON refresh_tokens(user_id);


