CREATE
SEQUENCE IF NOT EXISTS seedbed_slots_sequence START
WITH 1 INCREMENT BY 10;

CREATE
SEQUENCE IF NOT EXISTS seedbeds_sequence START
WITH 1 INCREMENT BY 10;

CREATE TABLE seedbed_slots
(
    id           BIGINT  NOT NULL,
    row_index    INTEGER NOT NULL,
    column_index INTEGER NOT NULL,
    created_at   TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP(6) WITHOUT TIME ZONE,
    seedbed_id   BIGINT  NOT NULL,
    CONSTRAINT pk_seedbed_slots PRIMARY KEY (id)
);

CREATE TABLE seedbeds
(
    id                     BIGINT       NOT NULL,
    name                   VARCHAR(100) NOT NULL,
    location_type          VARCHAR(255) NOT NULL,
    rows_count             INTEGER      NOT NULL,
    columns_count          INTEGER      NOT NULL,
    length_cm              DECIMAL(6, 2),
    width_cm               DECIMAL(6, 2),
    soil_depth_cm          DECIMAL(5, 2),
    has_enclosure          BOOLEAN      NOT NULL DEFAULT FALSE,
    has_humidity_control   BOOLEAN      NOT NULL DEFAULT FALSE,
    has_automatic_watering BOOLEAN      NOT NULL DEFAULT FALSE,
    notes                  TEXT,
    created_at             TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at             TIMESTAMP(6) WITHOUT TIME ZONE,
    CONSTRAINT pk_seedbeds PRIMARY KEY (id)
);

ALTER TABLE seedbeds
    ADD CONSTRAINT uc_seedbeds_name UNIQUE (name);

ALTER TABLE seedbed_slots
    ADD CONSTRAINT uk_seedbed_slots_position UNIQUE (seedbed_id, row_index, column_index);

ALTER TABLE seedbed_slots
    ADD CONSTRAINT FK_SEEDBED_SLOTS_ON_SEEDBED FOREIGN KEY (seedbed_id) REFERENCES seedbeds (id);
