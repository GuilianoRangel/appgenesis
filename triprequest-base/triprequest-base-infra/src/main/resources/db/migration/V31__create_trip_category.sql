CREATE TABLE trip_category (
                               id BIGSERIAL PRIMARY KEY,
                               code VARCHAR(30) NOT NULL UNIQUE,
                               name VARCHAR(100) NOT NULL,
                               description VARCHAR(255),
                               active BOOLEAN NOT NULL DEFAULT TRUE,
                               created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                               updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX uk_trip_category_code ON trip_category(code);