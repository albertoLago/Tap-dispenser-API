CREATE TABLE dispenser_tap_usage (
                                     id SERIAL PRIMARY KEY,
                                     dispenser_id BIGINT NOT NULL,
                                     flow_volume DOUBLE PRECISION NOT NULL,
                                     started_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                     ended_at TIMESTAMP WITHOUT TIME ZONE,
                                     volume DOUBLE PRECISION,
                                     CONSTRAINT fk_dispenser
                                         FOREIGN KEY(dispenser_id)
                                             REFERENCES dispenser(id)
                                             ON DELETE CASCADE
);