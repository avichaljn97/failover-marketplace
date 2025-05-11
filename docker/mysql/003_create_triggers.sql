DELIMITER //

CREATE TRIGGER applogs_before_insert
BEFORE INSERT ON applogs
FOR EACH ROW
BEGIN
  SET NEW.created_at = CURRENT_TIMESTAMP;
  SET NEW.updated_at = CURRENT_TIMESTAMP;
  SET NEW.last_updated_by = USER();
END;
//

CREATE TRIGGER applogs_before_update
BEFORE UPDATE ON applogs
FOR EACH ROW
BEGIN
  SET NEW.updated_at = CURRENT_TIMESTAMP;
  SET NEW.last_updated_by = USER();
END;
//

DELIMITER ;
