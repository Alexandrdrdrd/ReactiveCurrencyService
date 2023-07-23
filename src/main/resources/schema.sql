CREATE TABLE if not exists exchange_rates (
                                id SERIAL PRIMARY KEY,
                                r030 INTEGER NOT NULL,
                                txt VARCHAR(255) NOT NULL,
                                rate DOUBLE PRECISION NOT NULL,
                                cc VARCHAR(10) NOT NULL,
                                exchangedate VARCHAR(10) NOT NULL
);