CREATE
SEQUENCE IF NOT EXISTS plant_species_seq_sequence START
WITH 1 INCREMENT BY 5;

CREATE TABLE plant_species
(
    id                BIGINT       NOT NULL,
    name              VARCHAR(150) NOT NULL,
    scientific_name   VARCHAR(255),
    description       TEXT,
    life_cycle        VARCHAR(50),
    light_requirement VARCHAR(50),
    water_requirement VARCHAR(50),
    created_at        TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP(6) WITHOUT TIME ZONE,
    plant_category_id BIGINT       NOT NULL,
    CONSTRAINT pk_plant_species PRIMARY KEY (id)
);

ALTER TABLE plant_species
    ADD CONSTRAINT uc_plant_species_name UNIQUE (name);

ALTER TABLE plant_species
    ADD CONSTRAINT uc_plant_species_scientific_name UNIQUE (scientific_name);

ALTER TABLE plant_species
    ADD CONSTRAINT FK_PLANT_SPECIES_ON_PLANT_CATEGORY FOREIGN KEY (plant_category_id) REFERENCES plant_categories (id);
