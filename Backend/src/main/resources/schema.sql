CREATE TABLE IF NOT EXISTS heart_rate (
                                          id SERIAL PRIMARY KEY,
                                          timestamp INTEGER NOT NULL,
                                          heart_rate_value DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS acceleration (
                                            id SERIAL PRIMARY KEY,
                                            timestamp INTEGER NOT NULL,
                                            acceleration_x DOUBLE PRECISION NOT NULL,
                                            acceleration_y DOUBLE PRECISION NOT NULL,
                                            acceleration_z DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS sleep_stage (
                                            id SERIAL PRIMARY KEY,
                                            timestamp INTEGER NOT NULL,
                                            stage VARCHAR(45) NOT NULL
);