CREATE TABLE dispenser (
                           id SERIAL PRIMARY KEY,
                           flow_volume DOUBLE PRECISION NOT NULL,
                           is_open BOOLEAN NOT NULL DEFAULT FALSE,
                           usage_count INT NOT NULL DEFAULT 0,
                           total_usage_time_in_seconds BIGINT NOT NULL DEFAULT 0,
                           last_tap_usage_start TIMESTAMP WITHOUT TIME ZONE
);