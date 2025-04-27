USE logs;

CREATE TABLE IF NOT EXISTS applogs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255),
    service VARCHAR(255),
    endpoint VARCHAR(255),
    status VARCHAR(50),
    method VARCHAR(10),
    severity VARCHAR(50),
    payload JSON,
    response JSON,
    timestamp VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_updated_by VARCHAR(255) DEFAULT 'router_app'
)
PARTITION BY HASH(id) PARTITIONS 4;

CREATE INDEX idx_service ON applogs(service);
CREATE INDEX idx_endpoint ON applogs(endpoint);
CREATE INDEX idx_status ON applogs(status);
CREATE INDEX idx_timestamp ON applogs(timestamp);
