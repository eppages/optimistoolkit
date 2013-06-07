-- Procedure used to delete old records from monitoring_resource_*.
delimiter //

DROP PROCEDURE IF EXISTS clean_up_[TABLENAME] //

CREATE PROCEDURE clean_up_[TABLENAME](p_month_offset INT)
BEGIN
  DECLARE done BOOLEAN DEFAULT FALSE;
  DECLARE continueloop BOOLEAN DEFAULT TRUE;
  DECLARE cur_row_id VARCHAR(45);
  DECLARE cur_del CURSOR FOR SELECT row_id FROM [TABLENAME] WHERE date(metric_timestamp) <= date_sub(curdate(), INTERVAL p_month_offset MONTH) LIMIT 0, 1000;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  WHILE continueloop DO
     OPEN cur_del;
     read_loop: LOOP
       FETCH cur_del INTO cur_row_id;
       IF done THEN
         LEAVE read_loop;
       END IF;
    
       DELETE FROM [TABLENAME] where row_id = cur_row_id;
 
     END LOOP;

     COMMIT;
     CLOSE cur_del;
     SET done = FALSE;
   
     -- Check if there's more rows to be deleted.
     SELECT COUNT(*) INTO @remaining_rows
     FROM [TABLENAME]
     WHERE date(metric_timestamp) <= date_sub(curdate(), INTERVAL p_month_offset MONTH);

     IF @remaining_rows = 0 THEN
        SET continueloop = FALSE;
     END IF;
  END WHILE;
END //

CALL clean_up_[TABLENAME]([ARGUMENT]) //
DROP PROCEDURE IF EXISTS clean_up_[TABLENAME] //

