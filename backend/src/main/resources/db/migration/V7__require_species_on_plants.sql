-- Sets NOT NULL constraint to `plant_species_id` column only for new or updated records,
-- without affecting the existing one
ALTER TABLE plants
    ADD CONSTRAINT CK_PLANTS_PLANT_SPECIES_ID_NOT_NULL NOT NULL plant_species_id NOT VALID;
