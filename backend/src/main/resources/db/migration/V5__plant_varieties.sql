CREATE
SEQUENCE IF NOT EXISTS plant_varieties_sequence START
WITH 1 INCREMENT BY 20;

CREATE TABLE plant_varieties
(
    id               BIGINT       NOT NULL,
    name             VARCHAR(150) NOT NULL,
    description      TEXT,
    created_at       TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP(6) WITHOUT TIME ZONE,
    plant_species_id BIGINT       NOT NULL,
    CONSTRAINT pk_plant_varieties PRIMARY KEY (id)
);

ALTER TABLE plant_varieties
    ADD CONSTRAINT uk_plant_varieties_species_name UNIQUE (plant_species_id, name);

ALTER TABLE plant_varieties
    ADD CONSTRAINT FK_PLANT_VARIETIES_ON_PLANT_SPECIES FOREIGN KEY (plant_species_id) REFERENCES plant_species (id);
