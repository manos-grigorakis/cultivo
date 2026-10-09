CREATE
SEQUENCE IF NOT EXISTS plant_sequence START
WITH 1 INCREMENT BY 5;

CREATE
SEQUENCE IF NOT EXISTS revinfo_seq START
WITH 1 INCREMENT BY 50;

CREATE TABLE plants
(
    id          BIGINT       NOT NULL,
    label       VARCHAR(150) NOT NULL,
    status      VARCHAR(255) NOT NULL,
    archived_at TIMESTAMP WITHOUT TIME ZONE,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_plants PRIMARY KEY (id)
);

CREATE TABLE revchanges
(
    rev        BIGINT NOT NULL,
    entityname VARCHAR(255)
);

CREATE TABLE revinfo
(
    rev      BIGINT NOT NULL,
    revtstmp BIGINT,
    CONSTRAINT pk_revinfo PRIMARY KEY (rev)
);

ALTER TABLE revchanges
    ADD CONSTRAINT fk_revchanges_on_default_tracking_modified_entities_changelog FOREIGN KEY (rev) REFERENCES revinfo (rev);
