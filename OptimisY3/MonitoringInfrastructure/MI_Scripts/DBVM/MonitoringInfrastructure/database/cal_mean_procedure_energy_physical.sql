delimiter //

DROP PROCEDURE IF EXISTS cal_mean_value_[TABLENAME] //

CREATE PROCEDURE cal_mean_value_[TABLENAME](IN metricname VARCHAR(45), IN year INT, IN month INT)
BEGIN	
 	DECLARE done INT DEFAULT FALSE;
	DECLARE rowId_var VARCHAR(45); 	
	DECLARE metricName_var VARCHAR(45);
	DECLARE meanValue_var VARCHAR(45);
	DECLARE resourceId_var VARCHAR(45);
	DECLARE col_id_var VARCHAR(45);
	DECLARE metricUnit_var VARCHAR(45);
	DECLARE stp INT(11);
	DECLARE check_mean_val INT;
        -- Calculate mean values.
	DECLARE cal_cursor CURSOR FOR SELECT metric_name, AVG(metric_value), physical_resource_id,
                                             monitoring_information_collector_id, metric_unit
                                      FROM [TABLENAME]
                                      WHERE metric_name = metricname AND
                                            MONTH(FROM_UNIXTIME(metric_timestamp)) = month AND
                                            YEAR(FROM_UNIXTIME(metric_timestamp)) = year
                                      GROUP BY physical_resource_id;

        DECLARE CONTINUE HANDLER FOR NOT FOUND SET done=TRUE;

        -- Check if mean values are already present in the table.
        SELECT COUNT(*) INTO check_mean_val
        FROM [TABLENAME]
        WHERE
              metric_name = metricname AND
              MONTH(FROM_UNIXTIME(metric_timestamp)) = month AND
              YEAR(FROM_UNIXTIME(metric_timestamp)) = year AND
              row_id LIKE 'mean-%';

        IF check_mean_val = 0
        THEN
           OPEN cal_cursor;

	   set stp = (SELECT unix_timestamp(DATE(CONCAT(year,'-',month,'-15'))));
        
           read_loop:LOOP	
	      FETCH cal_cursor INTO metricName_var, meanValue_var, resourceId_var, col_id_var, metricUnit_var;
              IF done THEN LEAVE read_loop; END IF;
              set rowId_var = CONCAT('mean-', UUID_SHORT());
              INSERT INTO [TABLENAME]
              (row_id, physical_resource_id, virtual_resource_id, service_resource_id,
              monitoring_information_collector_id, metric_name, metric_value, metric_unit, metric_timestamp)
              VALUES(rowId_var, resourceId_var, null, null, col_id_var, metricName_var, meanValue_var, metricUnit_var, stp);
           END LOOP;
           CLOSE cal_cursor;

	   DELETE FROM [TABLENAME]
           WHERE metric_name = metricname AND
              MONTH(FROM_UNIXTIME(metric_timestamp)) = month AND
              YEAR(FROM_UNIXTIME(metric_timestamp)) = year AND
              row_id NOT LIKE 'mean-%';
        END IF;
END //

CALL cal_mean_value_[TABLENAME]([METRICNAME], [YEAR], [MONTH]) //

DROP PROCEDURE IF EXISTS cal_mean_value_[TABLENAME] //

