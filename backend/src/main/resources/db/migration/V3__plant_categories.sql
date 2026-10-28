CREATE SEQUENCE IF NOT EXISTS plant_categories_sequence START WITH 1 INCREMENT BY 20;

CREATE TABLE plant_categories
(
    id          BIGINT       NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_plant_categories PRIMARY KEY (id)
);

ALTER TABLE plant_categories
    ADD CONSTRAINT uc_plant_categories_name UNIQUE (name);
