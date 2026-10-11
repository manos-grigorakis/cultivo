CREATE
SEQUENCE IF NOT EXISTS plant_event_sequence START
WITH 1 INCREMENT BY 10;

CREATE TABLE plant_events
(
    id          BIGINT       NOT NULL,
    title       VARCHAR(200) NOT NULL,
    description TEXT,
    type        VARCHAR(50)  NOT NULL,
    occurred_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    plant_id    BIGINT       NOT NULL,
    CONSTRAINT pk_plant_events PRIMARY KEY (id)
);

ALTER TABLE plant_events
    ADD CONSTRAINT FK_PLANT_EVENTS_ON_PLANT FOREIGN KEY (plant_id) REFERENCES plants (id);
