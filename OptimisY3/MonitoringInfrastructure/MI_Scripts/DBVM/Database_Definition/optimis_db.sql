-- phpMyAdmin SQL Dump
-- version 3.4.9
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: May 28, 2013 at 03:59 PM
-- Server version: 5.1.65
-- PHP Version: 5.3.3

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

CREATE SCHEMA IF NOT EXISTS `optimis_db` DEFAULT CHARACTER SET latin1 ;
USE `optimis_db` ;

--
-- Database: `optimis_db`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`mmanager_usr`@`%` PROCEDURE `clean_up_monitoring_resource`(p_month_offset INT)
BEGIN
  DECLARE done BOOLEAN DEFAULT FALSE;
  DECLARE continueloop BOOLEAN DEFAULT TRUE;
  DECLARE cur_row_id VARCHAR(45);
  DECLARE cur_del CURSOR FOR SELECT row_id FROM monitoring_resource WHERE date(metric_timestamp) <= date_sub(curdate(), INTERVAL p_month_offset MONTH) LIMIT 0, 1000;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  WHILE continueloop DO
     OPEN cur_del;
     read_loop: LOOP
       FETCH cur_del INTO cur_row_id;
       IF done THEN
         LEAVE read_loop;
       END IF;
    
       DELETE FROM monitoring_resource where row_id = cur_row_id;
 
     END LOOP;

     COMMIT;
     CLOSE cur_del;
     SET done = FALSE;
   
     
     SELECT COUNT(*) INTO @remaining_rows
     FROM monitoring_resource
     WHERE date(metric_timestamp) <= date_sub(curdate(), INTERVAL p_month_offset MONTH);

     IF @remaining_rows = 0 THEN
        SET continueloop = FALSE;
     END IF;
  END WHILE;
END$$

CREATE DEFINER=`mmanager_usr`@`%` PROCEDURE `data_migration_energy`()
BEGIN
  DECLARE done BOOLEAN DEFAULT FALSE;
  DECLARE continueloop BOOLEAN DEFAULT TRUE;
  DECLARE cur_row_id VARCHAR(45);
  DECLARE cur_mig CURSOR FOR SELECT row_id FROM monitoring_resource_energy WHERE migrated = FALSE LIMIT 0, 1000;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  WHILE continueloop DO
     OPEN cur_mig;
     read_loop: LOOP
       FETCH cur_mig INTO cur_row_id;
       IF done THEN
         LEAVE read_loop;
       END IF;

       INSERT INTO monitoring_resource_energy_new
       (row_id, physical_resource_id, virtual_resource_id, monitoring_information_collector_id,
       metric_name, metric_value, metric_unit, metric_timestamp, service_resource_id)
       SELECT
          row_id, physical_resource_id, virtual_resource_id, monitoring_information_collector_id,
          metric_name, metric_value, metric_unit, UNIX_TIMESTAMP(metric_timestamp),
          service_resource_id
       FROM monitoring_resource_energy
       WHERE row_id = cur_row_id;

       UPDATE monitoring_resource_energy
       SET migrated = TRUE
       WHERE row_id = cur_row_id;
 
     END LOOP;

     COMMIT;
     CLOSE cur_mig;
     SET done = FALSE;
   
     
     SELECT COUNT(*) INTO @remaining_rows
     FROM monitoring_resource_energy
     WHERE migrated = FALSE;

     IF @remaining_rows = 0 THEN
        SET continueloop = FALSE;
     END IF;
  END WHILE;
END$$

CREATE DEFINER=`mmanager_usr`@`%` PROCEDURE `data_migration_physical`()
BEGIN
  DECLARE done BOOLEAN DEFAULT FALSE;
  DECLARE continueloop BOOLEAN DEFAULT TRUE;
  DECLARE cur_row_id VARCHAR(45);
  DECLARE cur_mig CURSOR FOR SELECT row_id FROM monitoring_resource_physical WHERE migrated = FALSE LIMIT 0, 1000;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  WHILE continueloop DO
     OPEN cur_mig;
     read_loop: LOOP
       FETCH cur_mig INTO cur_row_id;
       IF done THEN
         LEAVE read_loop;
       END IF;

       INSERT INTO monitoring_resource_physical_new
       (row_id, physical_resource_id, virtual_resource_id, monitoring_information_collector_id,
       metric_name, metric_value, metric_unit, metric_timestamp, service_resource_id)
       SELECT
          row_id, physical_resource_id, virtual_resource_id, monitoring_information_collector_id,
          metric_name, metric_value, metric_unit, UNIX_TIMESTAMP(metric_timestamp),
          service_resource_id
       FROM monitoring_resource_physical
       WHERE row_id = cur_row_id;

       UPDATE monitoring_resource_physical
       SET migrated = TRUE
       WHERE row_id = cur_row_id;
 
     END LOOP;

     COMMIT;
     CLOSE cur_mig;
     SET done = FALSE;
   
     
     SELECT COUNT(*) INTO @remaining_rows
     FROM monitoring_resource_physical
     WHERE migrated = FALSE;

     IF @remaining_rows = 0 THEN
        SET continueloop = FALSE;
     END IF;
  END WHILE;
END$$

CREATE DEFINER=`mmanager_usr`@`%` PROCEDURE `data_migration_service`()
BEGIN
  DECLARE done BOOLEAN DEFAULT FALSE;
  DECLARE continueloop BOOLEAN DEFAULT TRUE;
  DECLARE cur_row_id VARCHAR(45);
  DECLARE cur_mig CURSOR FOR SELECT row_id FROM monitoring_resource_service WHERE migrated = FALSE LIMIT 0, 1000;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  WHILE continueloop DO
     OPEN cur_mig;
     read_loop: LOOP
       FETCH cur_mig INTO cur_row_id;
       IF done THEN
         LEAVE read_loop;
       END IF;

       INSERT INTO monitoring_resource_service_new
       (row_id, physical_resource_id, virtual_resource_id, monitoring_information_collector_id,
       metric_name, metric_value, metric_unit, metric_timestamp, service_resource_id)
       SELECT
          row_id, physical_resource_id, virtual_resource_id, monitoring_information_collector_id,
          metric_name, metric_value, metric_unit, UNIX_TIMESTAMP(metric_timestamp),
          service_resource_id
       FROM monitoring_resource_service
       WHERE row_id = cur_row_id;

       UPDATE monitoring_resource_service
       SET migrated = TRUE
       WHERE row_id = cur_row_id;
 
     END LOOP;

     COMMIT;
     CLOSE cur_mig;
     SET done = FALSE;
   
     
     SELECT COUNT(*) INTO @remaining_rows
     FROM monitoring_resource_service
     WHERE migrated = FALSE;

     IF @remaining_rows = 0 THEN
        SET continueloop = FALSE;
     END IF;
  END WHILE;
END$$

CREATE DEFINER=`mmanager_usr`@`%` PROCEDURE `data_migration_virtual`()
BEGIN
  DECLARE done BOOLEAN DEFAULT FALSE;
  DECLARE continueloop BOOLEAN DEFAULT TRUE;
  DECLARE cur_row_id VARCHAR(45);
  DECLARE cur_mig CURSOR FOR SELECT row_id FROM monitoring_resource_virtual WHERE migrated = FALSE LIMIT 0, 1000;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  WHILE continueloop DO
     OPEN cur_mig;
     read_loop: LOOP
       FETCH cur_mig INTO cur_row_id;
       IF done THEN
         LEAVE read_loop;
       END IF;

       INSERT INTO monitoring_resource_virtual_new
       (row_id, physical_resource_id, virtual_resource_id, monitoring_information_collector_id,
       metric_name, metric_value, metric_unit, metric_timestamp, service_resource_id)
       SELECT
          row_id, physical_resource_id, virtual_resource_id, monitoring_information_collector_id,
          metric_name, metric_value, metric_unit, UNIX_TIMESTAMP(metric_timestamp),
          service_resource_id
       FROM monitoring_resource_virtual
       WHERE row_id = cur_row_id;

       UPDATE monitoring_resource_virtual
       SET migrated = TRUE
       WHERE row_id = cur_row_id;
 
     END LOOP;

     COMMIT;
     CLOSE cur_mig;
     SET done = FALSE;
   
     
     SELECT COUNT(*) INTO @remaining_rows
     FROM monitoring_resource_virtual
     WHERE migrated = FALSE;

     IF @remaining_rows = 0 THEN
        SET continueloop = FALSE;
     END IF;
  END WHILE;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `infrastructure_provider`
--

CREATE TABLE IF NOT EXISTS `infrastructure_provider` (
  `id` varchar(50) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `ip_vm_ip_address` varchar(50) NOT NULL,
  `creation_date` datetime NOT NULL,
  `created_by` varchar(50) NOT NULL,
  `last_update_date` datetime NOT NULL,
  `last_updated_by` varchar(50) NOT NULL,
  `blo` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `monitoring_resource_energy`
--

CREATE TABLE IF NOT EXISTS `monitoring_resource_energy` (
  `row_id` varchar(45) NOT NULL,
  `physical_resource_id` varchar(45) DEFAULT NULL,
  `virtual_resource_id` varchar(45) DEFAULT NULL,
  `monitoring_information_collector_id` varchar(45) DEFAULT NULL,
  `metric_name` varchar(45) DEFAULT NULL,
  `metric_value` varchar(5000) DEFAULT NULL,
  `metric_unit` varchar(45) DEFAULT NULL,
  `metric_timestamp` int(11) NOT NULL,
  `service_resource_id` varchar(45) DEFAULT NULL,
  KEY `monitoring_resource_01` (`service_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_02` (`virtual_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_03` (`physical_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_04` (`metric_name`,`metric_timestamp`),
  KEY `monitoring_resource_05` (`service_resource_id`),
  KEY `monitoring_resource_06` (`virtual_resource_id`),
  KEY `monitoring_resource_07` (`physical_resource_id`),
  KEY `monitoring_resource_08` (`metric_name`),
  KEY `monitoring_resource_09` (`metric_timestamp`),
  KEY `monitoring_resource_10` (`row_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Stores collected values of type ''service''.'
/*!50100 PARTITION BY RANGE (metric_timestamp)
(PARTITION part0 VALUES LESS THAN (1325372400) ENGINE = InnoDB,
 PARTITION part1 VALUES LESS THAN (1328050800) ENGINE = InnoDB,
 PARTITION part2 VALUES LESS THAN (1330556400) ENGINE = InnoDB,
 PARTITION part3 VALUES LESS THAN (1333231200) ENGINE = InnoDB,
 PARTITION part4 VALUES LESS THAN (1335823200) ENGINE = InnoDB,
 PARTITION part5 VALUES LESS THAN (1338501600) ENGINE = InnoDB,
 PARTITION part6 VALUES LESS THAN (1341093600) ENGINE = InnoDB,
 PARTITION part7 VALUES LESS THAN (1343772000) ENGINE = InnoDB,
 PARTITION part8 VALUES LESS THAN (1346450400) ENGINE = InnoDB,
 PARTITION part9 VALUES LESS THAN (1349042400) ENGINE = InnoDB,
 PARTITION part10 VALUES LESS THAN (1351724400) ENGINE = InnoDB,
 PARTITION part11 VALUES LESS THAN (1354316400) ENGINE = InnoDB,
 PARTITION part12 VALUES LESS THAN (1356994800) ENGINE = InnoDB,
 PARTITION part13 VALUES LESS THAN (1359673200) ENGINE = InnoDB,
 PARTITION part14 VALUES LESS THAN (1362092400) ENGINE = InnoDB,
 PARTITION part15 VALUES LESS THAN (1364767200) ENGINE = InnoDB,
 PARTITION part16 VALUES LESS THAN (1367359200) ENGINE = InnoDB,
 PARTITION part17 VALUES LESS THAN (1370037600) ENGINE = InnoDB,
 PARTITION part18 VALUES LESS THAN (1372629600) ENGINE = InnoDB,
 PARTITION part19 VALUES LESS THAN (1375308000) ENGINE = InnoDB,
 PARTITION part20 VALUES LESS THAN (1377986400) ENGINE = InnoDB,
 PARTITION part21 VALUES LESS THAN (1380578400) ENGINE = InnoDB,
 PARTITION part22 VALUES LESS THAN (1383260400) ENGINE = InnoDB,
 PARTITION part23 VALUES LESS THAN (1385852400) ENGINE = InnoDB,
 PARTITION part24 VALUES LESS THAN MAXVALUE ENGINE = InnoDB) */;

-- --------------------------------------------------------

--
-- Table structure for table `monitoring_resource_physical`
--

CREATE TABLE IF NOT EXISTS `monitoring_resource_physical` (
  `row_id` varchar(45) NOT NULL,
  `physical_resource_id` varchar(45) DEFAULT NULL,
  `virtual_resource_id` varchar(45) DEFAULT NULL,
  `monitoring_information_collector_id` varchar(45) DEFAULT NULL,
  `metric_name` varchar(45) DEFAULT NULL,
  `metric_value` varchar(5000) DEFAULT NULL,
  `metric_unit` varchar(45) DEFAULT NULL,
  `metric_timestamp` int(11) NOT NULL,
  `service_resource_id` varchar(45) DEFAULT NULL,
  KEY `monitoring_resource_01` (`service_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_02` (`virtual_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_03` (`physical_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_04` (`metric_name`,`metric_timestamp`),
  KEY `monitoring_resource_05` (`service_resource_id`),
  KEY `monitoring_resource_06` (`virtual_resource_id`),
  KEY `monitoring_resource_07` (`physical_resource_id`),
  KEY `monitoring_resource_08` (`metric_name`),
  KEY `monitoring_resource_09` (`metric_timestamp`),
  KEY `monitoring_resource_10` (`row_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Stores collected values of type ''service''.'
/*!50100 PARTITION BY RANGE (metric_timestamp)
(PARTITION part0 VALUES LESS THAN (1325372400) ENGINE = InnoDB,
 PARTITION part1 VALUES LESS THAN (1328050800) ENGINE = InnoDB,
 PARTITION part2 VALUES LESS THAN (1330556400) ENGINE = InnoDB,
 PARTITION part3 VALUES LESS THAN (1333231200) ENGINE = InnoDB,
 PARTITION part4 VALUES LESS THAN (1335823200) ENGINE = InnoDB,
 PARTITION part5 VALUES LESS THAN (1338501600) ENGINE = InnoDB,
 PARTITION part6 VALUES LESS THAN (1341093600) ENGINE = InnoDB,
 PARTITION part7 VALUES LESS THAN (1343772000) ENGINE = InnoDB,
 PARTITION part8 VALUES LESS THAN (1346450400) ENGINE = InnoDB,
 PARTITION part9 VALUES LESS THAN (1349042400) ENGINE = InnoDB,
 PARTITION part10 VALUES LESS THAN (1351724400) ENGINE = InnoDB,
 PARTITION part11 VALUES LESS THAN (1354316400) ENGINE = InnoDB,
 PARTITION part12 VALUES LESS THAN (1356994800) ENGINE = InnoDB,
 PARTITION part13 VALUES LESS THAN (1359673200) ENGINE = InnoDB,
 PARTITION part14 VALUES LESS THAN (1362092400) ENGINE = InnoDB,
 PARTITION part15 VALUES LESS THAN (1364767200) ENGINE = InnoDB,
 PARTITION part16 VALUES LESS THAN (1367359200) ENGINE = InnoDB,
 PARTITION part17 VALUES LESS THAN (1370037600) ENGINE = InnoDB,
 PARTITION part18 VALUES LESS THAN (1372629600) ENGINE = InnoDB,
 PARTITION part19 VALUES LESS THAN (1375308000) ENGINE = InnoDB,
 PARTITION part20 VALUES LESS THAN (1377986400) ENGINE = InnoDB,
 PARTITION part21 VALUES LESS THAN (1380578400) ENGINE = InnoDB,
 PARTITION part22 VALUES LESS THAN (1383260400) ENGINE = InnoDB,
 PARTITION part23 VALUES LESS THAN (1385852400) ENGINE = InnoDB,
 PARTITION part24 VALUES LESS THAN MAXVALUE ENGINE = InnoDB) */;

-- --------------------------------------------------------

--
-- Table structure for table `monitoring_resource_service`
--

CREATE TABLE IF NOT EXISTS `monitoring_resource_service` (
  `row_id` varchar(45) NOT NULL,
  `physical_resource_id` varchar(45) DEFAULT NULL,
  `virtual_resource_id` varchar(45) DEFAULT NULL,
  `monitoring_information_collector_id` varchar(45) DEFAULT NULL,
  `metric_name` varchar(45) DEFAULT NULL,
  `metric_value` varchar(5000) DEFAULT NULL,
  `metric_unit` varchar(45) DEFAULT NULL,
  `metric_timestamp` int(11) NOT NULL,
  `service_resource_id` varchar(45) DEFAULT NULL,
  KEY `monitoring_resource_01` (`service_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_02` (`virtual_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_03` (`physical_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_04` (`metric_name`,`metric_timestamp`),
  KEY `monitoring_resource_05` (`service_resource_id`),
  KEY `monitoring_resource_06` (`virtual_resource_id`),
  KEY `monitoring_resource_07` (`physical_resource_id`),
  KEY `monitoring_resource_08` (`metric_name`),
  KEY `monitoring_resource_09` (`metric_timestamp`),
  KEY `monitoring_resource_10` (`row_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Stores collected values of type ''service''.'
/*!50100 PARTITION BY RANGE (metric_timestamp)
(PARTITION part0 VALUES LESS THAN (1325372400) ENGINE = InnoDB,
 PARTITION part1 VALUES LESS THAN (1328050800) ENGINE = InnoDB,
 PARTITION part2 VALUES LESS THAN (1330556400) ENGINE = InnoDB,
 PARTITION part3 VALUES LESS THAN (1333231200) ENGINE = InnoDB,
 PARTITION part4 VALUES LESS THAN (1335823200) ENGINE = InnoDB,
 PARTITION part5 VALUES LESS THAN (1338501600) ENGINE = InnoDB,
 PARTITION part6 VALUES LESS THAN (1341093600) ENGINE = InnoDB,
 PARTITION part7 VALUES LESS THAN (1343772000) ENGINE = InnoDB,
 PARTITION part8 VALUES LESS THAN (1346450400) ENGINE = InnoDB,
 PARTITION part9 VALUES LESS THAN (1349042400) ENGINE = InnoDB,
 PARTITION part10 VALUES LESS THAN (1351724400) ENGINE = InnoDB,
 PARTITION part11 VALUES LESS THAN (1354316400) ENGINE = InnoDB,
 PARTITION part12 VALUES LESS THAN (1356994800) ENGINE = InnoDB,
 PARTITION part13 VALUES LESS THAN (1359673200) ENGINE = InnoDB,
 PARTITION part14 VALUES LESS THAN (1362092400) ENGINE = InnoDB,
 PARTITION part15 VALUES LESS THAN (1364767200) ENGINE = InnoDB,
 PARTITION part16 VALUES LESS THAN (1367359200) ENGINE = InnoDB,
 PARTITION part17 VALUES LESS THAN (1370037600) ENGINE = InnoDB,
 PARTITION part18 VALUES LESS THAN (1372629600) ENGINE = InnoDB,
 PARTITION part19 VALUES LESS THAN (1375308000) ENGINE = InnoDB,
 PARTITION part20 VALUES LESS THAN (1377986400) ENGINE = InnoDB,
 PARTITION part21 VALUES LESS THAN (1380578400) ENGINE = InnoDB,
 PARTITION part22 VALUES LESS THAN (1383260400) ENGINE = InnoDB,
 PARTITION part23 VALUES LESS THAN (1385852400) ENGINE = InnoDB,
 PARTITION part24 VALUES LESS THAN MAXVALUE ENGINE = InnoDB) */;

-- --------------------------------------------------------

--
-- Table structure for table `monitoring_resource_virtual`
--

CREATE TABLE IF NOT EXISTS `monitoring_resource_virtual` (
  `row_id` varchar(45) NOT NULL,
  `physical_resource_id` varchar(45) DEFAULT NULL,
  `virtual_resource_id` varchar(45) DEFAULT NULL,
  `monitoring_information_collector_id` varchar(45) DEFAULT NULL,
  `metric_name` varchar(45) DEFAULT NULL,
  `metric_value` varchar(5000) DEFAULT NULL,
  `metric_unit` varchar(45) DEFAULT NULL,
  `metric_timestamp` int(11) NOT NULL,
  `service_resource_id` varchar(45) DEFAULT NULL,
  KEY `monitoring_resource_01` (`service_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_02` (`virtual_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_03` (`physical_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_04` (`metric_name`,`metric_timestamp`),
  KEY `monitoring_resource_05` (`service_resource_id`),
  KEY `monitoring_resource_06` (`virtual_resource_id`),
  KEY `monitoring_resource_07` (`physical_resource_id`),
  KEY `monitoring_resource_08` (`metric_name`),
  KEY `monitoring_resource_09` (`metric_timestamp`),
  KEY `monitoring_resource_10` (`row_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Stores collected values of type ''service''.'
/*!50100 PARTITION BY RANGE (metric_timestamp)
(PARTITION part0 VALUES LESS THAN (1325372400) ENGINE = InnoDB,
 PARTITION part1 VALUES LESS THAN (1328050800) ENGINE = InnoDB,
 PARTITION part2 VALUES LESS THAN (1330556400) ENGINE = InnoDB,
 PARTITION part3 VALUES LESS THAN (1333231200) ENGINE = InnoDB,
 PARTITION part4 VALUES LESS THAN (1335823200) ENGINE = InnoDB,
 PARTITION part5 VALUES LESS THAN (1338501600) ENGINE = InnoDB,
 PARTITION part6 VALUES LESS THAN (1341093600) ENGINE = InnoDB,
 PARTITION part7 VALUES LESS THAN (1343772000) ENGINE = InnoDB,
 PARTITION part8 VALUES LESS THAN (1346450400) ENGINE = InnoDB,
 PARTITION part9 VALUES LESS THAN (1349042400) ENGINE = InnoDB,
 PARTITION part10 VALUES LESS THAN (1351724400) ENGINE = InnoDB,
 PARTITION part11 VALUES LESS THAN (1354316400) ENGINE = InnoDB,
 PARTITION part12 VALUES LESS THAN (1356994800) ENGINE = InnoDB,
 PARTITION part13 VALUES LESS THAN (1359673200) ENGINE = InnoDB,
 PARTITION part14 VALUES LESS THAN (1362092400) ENGINE = InnoDB,
 PARTITION part15 VALUES LESS THAN (1364767200) ENGINE = InnoDB,
 PARTITION part16 VALUES LESS THAN (1367359200) ENGINE = InnoDB,
 PARTITION part17 VALUES LESS THAN (1370037600) ENGINE = InnoDB,
 PARTITION part18 VALUES LESS THAN (1372629600) ENGINE = InnoDB,
 PARTITION part19 VALUES LESS THAN (1375308000) ENGINE = InnoDB,
 PARTITION part20 VALUES LESS THAN (1377986400) ENGINE = InnoDB,
 PARTITION part21 VALUES LESS THAN (1380578400) ENGINE = InnoDB,
 PARTITION part22 VALUES LESS THAN (1383260400) ENGINE = InnoDB,
 PARTITION part23 VALUES LESS THAN (1385852400) ENGINE = InnoDB,
 PARTITION part24 VALUES LESS THAN MAXVALUE ENGINE = InnoDB) */;

-- --------------------------------------------------------

--
-- Table structure for table `physical_resource`
--

CREATE TABLE IF NOT EXISTS `physical_resource` (
  `id` varchar(50) NOT NULL,
  `hostname` varchar(100) NOT NULL,
  `hypervisor` varchar(50) DEFAULT NULL,
  `disk_size_in_gigabytes` int(10) unsigned DEFAULT NULL,
  `cpu_cores` int(10) unsigned DEFAULT NULL,
  `memory_in_gigabytes` int(10) unsigned DEFAULT NULL,
  `os` varchar(50) DEFAULT NULL,
  `network_adapter` varchar(100) DEFAULT NULL,
  `public_ip_address` varchar(45) DEFAULT NULL,
  `private_ip_address` varchar(45) DEFAULT NULL,
  `infrastructure_provider_id` varchar(50) NOT NULL,
  `active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `virtual_resource`
--

CREATE TABLE IF NOT EXISTS `virtual_resource` (
  `id` varchar(50) NOT NULL,
  `hostname` varchar(100) NOT NULL,
  `physical_resource_id` varchar(50) NOT NULL,
  `service_id` varchar(40) NOT NULL,
  `type` varchar(50) DEFAULT NULL,
  `hypervisor` varchar(50) DEFAULT NULL,
  `disk_size_in_gigabytes` int(10) unsigned DEFAULT NULL,
  `cpu_cores` int(10) unsigned DEFAULT NULL,
  `memory_in_gigabytes` int(10) unsigned DEFAULT NULL,
  `os` varchar(50) DEFAULT NULL,
  `network_adapter` varchar(100) DEFAULT NULL,
  `public_ip_address` varchar(45) DEFAULT NULL,
  `private_ip_address` varchar(45) DEFAULT NULL,
  `comments` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `physical_resource_id` (`physical_resource_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
