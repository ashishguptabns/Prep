CREATE TABLE IF NOT EXISTS racks (
    rack_id TEXT PRIMARY KEY,
    site_id TEXT NOT NULL,
    max_power_kw NUMERIC NOT NULL
);

CREATE TABLE IF NOT EXISTS power_readings (
    reading_id SERIAL PRIMARY KEY,
    rack_id TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    power_kw NUMERIC NOT NULL
);
