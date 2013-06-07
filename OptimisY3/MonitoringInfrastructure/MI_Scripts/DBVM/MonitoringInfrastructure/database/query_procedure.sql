delimiter //

DROP PROCEDURE IF EXISTS query_data_[TABLENAME] //

CREATE PROCEDURE query_data_[TABLENAME](p_month_offset INT)
BEGIN
SELECT * INTO OUTFILE '/tmp/[FILENAME]' FROM [TABLENAME] WHERE date(from_unixtime(metric_timestamp)) <= date_sub(curdate(), INTERVAL p_month_offset MONTH); 
END //

CALL query_data_[TABLENAME]([ARGUMENT]) //
DROP PROCEDURE IF EXISTS query_data_[TABLENAME] //

