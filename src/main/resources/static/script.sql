DROP TABLE IF EXISTS tickets CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;



CREATE TABLE roles
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE users
(
    id         UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    username   VARCHAR(20)  NOT NULL UNIQUE,
    email      VARCHAR(60) UNIQUE,
    password   VARCHAR(255) NOT NULL,
    first_name VARCHAR(20),
    last_name  VARCHAR(20),
    phone      VARCHAR(10),
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE user_roles
(
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES roles (id)
            ON DELETE CASCADE
);

CREATE TABLE tickets
(
    id          UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    status      VARCHAR(20)  NOT NULL DEFAULT 'OPEN',
    created_by  UUID         NOT NULL,
    assigned_to UUID,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP,
    CONSTRAINT fk_tickets_assigned_to FOREIGN KEY (assigned_to) REFERENCES users (id),
    CONSTRAINT fk_tickets_user FOREIGN KEY (created_by) REFERENCES users (id)
);

CREATE INDEX idx_users_active ON users(active);
CREATE INDEX idx_users_active_created_at ON users(active, created_at DESC);
CREATE INDEX idx_users_full_name ON users(first_name, last_name);

CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

CREATE INDEX idx_tickets_status ON tickets(status);
CREATE INDEX idx_tickets_created_by ON tickets(created_by);
CREATE INDEX idx_tickets_assigned_to ON tickets(assigned_to);
CREATE INDEX idx_tickets_created_at ON tickets(created_at DESC);
CREATE INDEX idx_tickets_updated_at ON tickets(updated_at DESC);
CREATE INDEX idx_tickets_status_created_at ON tickets(status, created_at DESC);
CREATE INDEX idx_tickets_status_assigned_to ON tickets(status, assigned_to);
CREATE INDEX idx_tickets_assigned_to_status ON tickets(assigned_to, status);

-- Índice de texto completo (opcional)
CREATE INDEX idx_tickets_full_text_search ON tickets
    USING gin(to_tsvector('spanish', coalesce(title, '') || ' ' || coalesce(description, '')));