CREATE
SEQUENCE IF NOT EXISTS seedbed_sowings_sequence START
WITH 1 INCREMENT BY 10;

CREATE TABLE seedbed_sowings
(
    id                 BIGINT       NOT NULL,
    status             VARCHAR(255) NOT NULL,
    seed_count         INTEGER      NOT NULL,
    germinated_count   INTEGER,
    transplanted_count INTEGER,
    notes              TEXT,
    sown_at            TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    completed_at       TIMESTAMP(6) WITHOUT TIME ZONE,
    created_at         TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at         TIMESTAMP(6) WITHOUT TIME ZONE,
    seedbed_slot_id    BIGINT       NOT NULL,
    seed_packet_id     BIGINT       NOT NULL,
    CONSTRAINT pk_seedbed_sowings PRIMARY KEY (id)
);

ALTER TABLE seedbed_sowings
    ADD CONSTRAINT FK_SEEDBED_SOWINGS_ON_SEEDBED_SLOT FOREIGN KEY (seedbed_slot_id) REFERENCES seedbed_slots (id);

ALTER TABLE seedbed_sowings
    ADD CONSTRAINT FK_SEEDBED_SOWINGS_ON_SEED_PACKET FOREIGN KEY (seed_packet_id) REFERENCES seed_packets (id);

ALTER TABLE seedbed_sowings
    ADD CONSTRAINT CK_SEEDBED_SOWINGS_SEED_COUNT CHECK ( seed_count > 0 );

ALTER TABLE seedbed_sowings
    ADD CONSTRAINT CK_SEEDBED_SOWINGS_GERMINATED_COUNT CHECK ( germinated_count >= 0 AND germinated_count <= seed_count );

ALTER TABLE seedbed_sowings
    ADD CONSTRAINT CK_SEEDBED_SOWINGS_TRANSPLANTED_COUNT CHECK ( transplanted_count IS NULL OR
                                                                 (germinated_count IS NOT NULL AND
                                                                  transplanted_count >= 0 AND
                                                                  transplanted_count <= germinated_count));

ALTER TABLE seedbed_sowings
    ADD CONSTRAINT CK_SEEDBED_SOWINGS_COMPLETED_AT CHECK ( completed_at >= sown_at );

