CREATE
SEQUENCE IF NOT EXISTS seed_packets_sequence START
WITH 1 INCREMENT BY 10;

CREATE TABLE seed_packets
(
    id                    BIGINT       NOT NULL,
    brand                 VARCHAR(150) NOT NULL,
    supplier              VARCHAR(150),
    status                VARCHAR(50)  NOT NULL,
    code                  VARCHAR(100),
    height_cm             DECIMAL(7, 2),
    weight_g              DECIMAL(10, 3),
    plant_spacing_cm      DECIMAL(7, 2),
    row_spacing_cm        DECIMAL(7, 2),
    sowing_depth_cm       DECIMAL(5, 2),
    germination_days      INTEGER,
    start_harvest_days    INTEGER,
    sowing_outdoor_months SMALLINT[],
    sowing_seedbed_months SMALLINT[],
    purchased_at          TIMESTAMP(6) WITHOUT TIME ZONE,
    created_at            TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at            TIMESTAMP(6) WITHOUT TIME ZONE,
    plant_variety_id      BIGINT       NOT NULL,
    CONSTRAINT pk_seed_packets PRIMARY KEY (id)
);

ALTER TABLE seed_packets
    ADD CONSTRAINT uk_seed_packets_brand_code_plant_variety_id UNIQUE (brand, code, plant_variety_id);

ALTER TABLE seed_packets
    ADD CONSTRAINT FK_SEED_PACKETS_ON_PLANT_VARIETY FOREIGN KEY (plant_variety_id) REFERENCES plant_varieties (id);
