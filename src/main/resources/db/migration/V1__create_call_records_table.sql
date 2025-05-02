CREATE TABLE IF NOT EXISTS call_records (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    endpoint VARCHAR(100) NOT NULL,
    request_params VARCHAR(1000),
    response VARCHAR(1000),
    error_message VARCHAR(1000),
    status_code INTEGER
);

-- índice para mejorar el rendimiento en búsquedas por fecha
CREATE INDEX idx_call_records_timestamp ON call_records (timestamp DESC);
