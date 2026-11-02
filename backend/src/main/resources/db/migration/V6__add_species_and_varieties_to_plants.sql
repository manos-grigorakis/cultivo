ALTER TABLE plants
    ADD plant_species_id BIGINT;

ALTER TABLE plants
    ADD plant_variety_id BIGINT;

ALTER TABLE plants
    ADD CONSTRAINT FK_PLANTS_ON_PLANT_SPECIES FOREIGN KEY (plant_species_id) REFERENCES plant_species (id);

ALTER TABLE plants
    ADD CONSTRAINT FK_PLANTS_ON_PLANT_VARIETY FOREIGN KEY (plant_variety_id) REFERENCES plant_varieties (id);
