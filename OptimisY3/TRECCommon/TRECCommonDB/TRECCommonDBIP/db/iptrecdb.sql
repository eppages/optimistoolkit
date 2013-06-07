CREATE DATABASE  IF NOT EXISTS `iptrecdb` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `iptrecdb`;
-- MySQL dump 10.13  Distrib 5.5.16, for Win32 (x86)
--
-- Host: localhost    Database: iptrecdb_tst
-- ------------------------------------------------------
-- Server version	5.5.23

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `service_sla`
--

DROP TABLE IF EXISTS `service_sla`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_sla` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `serviceId` varchar(45) NOT NULL,
  `ip_id` varchar(45) NOT NULL,
  `slaId` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_service_sla` (`serviceId`),
  KEY `FK_ip_sla` (`ip_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `risk_resource_physical`
--

DROP TABLE IF EXISTS `risk_resource_physical`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `risk_resource_physical` (
  `row_id` varchar(200) NOT NULL,
  `physical_resource_id` varchar(200) DEFAULT NULL,
  `virtual_resource_id` varchar(200) DEFAULT NULL,
  `metric_name` varchar(45) DEFAULT NULL,
  `metric_value` varchar(200) DEFAULT NULL,
  `metric_unit` varchar(45) DEFAULT NULL,
  `metric_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `service_resource_id` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`row_id`),
  KEY `monitoring_resource_01` (`service_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_02` (`virtual_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_03` (`physical_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_04` (`metric_name`,`metric_timestamp`),
  KEY `monitoring_resource_05` (`service_resource_id`),
  KEY `monitoring_resource_06` (`virtual_resource_id`),
  KEY `monitoring_resource_07` (`physical_resource_id`),
  KEY `monitoring_resource_08` (`metric_name`),
  KEY `monitoring_resource_09` (`metric_timestamp`),
  CONSTRAINT `risk_resource_physical_ibfk_1` FOREIGN KEY (`virtual_resource_id`) REFERENCES `risk_resource_virtual` (`virtual_resource_id`),
  CONSTRAINT `risk_resource_physical_ibfk_2` FOREIGN KEY (`service_resource_id`) REFERENCES `risk_resource_service` (`service_resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ipriskhistory`
--

DROP TABLE IF EXISTS `ipriskhistory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ipriskhistory` (
  `samplingPoint` bigint(20) NOT NULL,
  `ip_id` varchar(100) DEFAULT NULL,
  `phyhost_risk` double DEFAULT NULL,
  `vm_risk` double DEFAULT NULL,
  `sec_risk` double DEFAULT NULL,
  `legal_risk` double DEFAULT NULL,
  PRIMARY KEY (`samplingPoint`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ip_risk`
--

DROP TABLE IF EXISTS `ip_risk`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ip_risk` (
  `rec_id` int(11) NOT NULL,
  `ip_id` varchar(45) DEFAULT NULL,
  `ip_name` varchar(45) DEFAULT NULL,
  `samplingPoint` longtext,
  `pyhost_risk` double DEFAULT NULL,
  `vm_risk` double DEFAULT NULL,
  `sec_risk` double DEFAULT NULL,
  `legaldm_risk` double DEFAULT NULL,
  PRIMARY KEY (`rec_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `slas`
--

DROP TABLE IF EXISTS `slas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `slas` (
  `sla_id` int(11) NOT NULL,
  `expirationTime` datetime DEFAULT NULL,
  `offerTime` datetime DEFAULT NULL,
  `penalty` double DEFAULT NULL,
  `price` double DEFAULT NULL,
  `provider_dn` varchar(255) DEFAULT NULL,
  `provider_id` int(11) DEFAULT NULL,
  `risk` double DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`sla_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

-- -----------------------------------------------------
-- Table `iptrecdb`.`IP_eco_service`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `iptrecdb`.`IP_eco_service` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `service_id` VARCHAR(45) NOT NULL ,
  `energy_eff_service` DOUBLE NOT NULL ,
  `ecological_eff_service` DOUBLE NOT NULL ,
  `performance_service` DOUBLE NOT NULL ,
  `power_service` DOUBLE NOT NULL ,
  `grCO2s_service` DOUBLE NOT NULL ,
  `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `timestamp_UNIQUE` (`timestamp` ASC, `service_id` ASC) )
ENGINE = InnoDB
AUTO_INCREMENT = 27743
DEFAULT CHARACTER SET = latin1;

--
-- Table structure for table `quotes`
--

DROP TABLE IF EXISTS `quotes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quotes` (
  `sla_id` int(11) NOT NULL,
  `provider_dn` varchar(255) DEFAULT NULL,
  `time_quoted` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`sla_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dsahpproviders`
--

DROP TABLE IF EXISTS `dsahpproviders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dsahpproviders` (
  `distName` varchar(255) NOT NULL,
  `t50` int(11) DEFAULT NULL,
  `t95` int(11) DEFAULT NULL,
  `belief` double DEFAULT NULL,
  `businessStability` double DEFAULT NULL,
  `pastPerf` double DEFAULT NULL,
  `plausibility` double DEFAULT NULL,
  `rating` double DEFAULT NULL,
  `security` double DEFAULT NULL,
  PRIMARY KEY (`distName`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sp_trust`
--

DROP TABLE IF EXISTS `sp_trust`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sp_trust` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tstamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `sp_id` varchar(45) NOT NULL,
  `sp_trust` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `sp_id` (`sp_id`),
  CONSTRAINT `sp_trust_ibfk_1` FOREIGN KEY (`sp_id`) REFERENCES `sp_info` (`sp_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12030 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `proactiverule`
--

DROP TABLE IF EXISTS `proactiverule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `proactiverule` (
  `idproactiveRule` int(11) NOT NULL,
  `Component` varchar(45) DEFAULT NULL,
  `Rule` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idproactiveRule`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `spcost`
--

DROP TABLE IF EXISTS `spcost`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spcost` (
  `sp_id` varchar(45) NOT NULL,
  `currency` varchar(50) DEFAULT NULL,
  `PricePerVCPU` float DEFAULT NULL,
  `PricePerMBMemory` float DEFAULT NULL,
  `PricePerGBStorage` float DEFAULT NULL,
  `PricePerGBUploaded` float DEFAULT NULL,
  `PricePerGBDownloaded` float DEFAULT NULL,
  `PricePerWatt` float DEFAULT NULL,
  PRIMARY KEY (`sp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `cost_ip_persisted_quote_pricecomponent_pricelevel`
--

DROP TABLE IF EXISTS `cost_ip_persisted_quote_pricecomponent_pricelevel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cost_ip_persisted_quote_pricecomponent_pricelevel` (
  `id` varchar(45) NOT NULL,
  `sp_id` varchar(45) NOT NULL,
  `service_id` varchar(45) NOT NULL,
  `component_name` varchar(45) NOT NULL,
  `price_type` varchar(45) NOT NULL,
  `price_level_name` varchar(45) NOT NULL,
  `absolute_amount` float DEFAULT NULL,
  `multiplier` float DEFAULT NULL,
  KEY `service_id` (`service_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cost_ip_set_price`
--

DROP TABLE IF EXISTS `cost_ip_set_price`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cost_ip_set_price` (
  `id` varchar(45) NOT NULL,
  `Currency` varchar(45) NOT NULL,
  `PricePerVCPU` float DEFAULT NULL,
  `PricePerMBMemory` float DEFAULT NULL,
  `PricePerGBStorage` float DEFAULT NULL,
  `PricePerGBUploaded` float DEFAULT NULL,
  `PricePerGBDownloaded` float DEFAULT NULL,
  `PricePerWatt` float DEFAULT NULL,
  `PriceType` varchar(45) NOT NULL COMMENT 'Gold Silver Bronze',
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Price charged by IP';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `providers`
--

DROP TABLE IF EXISTS `providers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `providers` (
  `provider_id` int(11) NOT NULL,
  `delegation_epr` longtext,
  `inService` bit(1) DEFAULT NULL,
  `provider_dn` longtext,
  `provider_epn` longtext,
  PRIMARY KEY (`provider_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


-- -----------------------------------------------------
-- Table `iptrecdb`.`IP_eco_node_table`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `iptrecdb`.`IP_eco_node_table` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `node_id` VARCHAR(40) NOT NULL ,
  `energy_eff_node` DOUBLE NOT NULL ,
  `ecological_eff_node` DOUBLE NOT NULL ,
  `performance_node` DOUBLE NOT NULL ,
  `power_node` DOUBLE NOT NULL ,
  `grCO2s_node` DOUBLE NOT NULL ,
  `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `timestamp_UNIQUE` (`timestamp` ASC, `node_id` ASC) )
ENGINE = InnoDB
AUTO_INCREMENT = 114622
DEFAULT CHARACTER SET = latin1;

-- -----------------------------------------------------
-- Table `iptrecdb`.`IP_eco_vm_table`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `iptrecdb`.`IP_eco_vm_table` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `vm_id` VARCHAR(45) NOT NULL ,
  `energy_eff_vm` DOUBLE NOT NULL ,
  `ecological_eff_vm` DOUBLE NOT NULL ,
  `performance_vm` DOUBLE NOT NULL ,
  `power_vm` DOUBLE NOT NULL ,
  `grCO2s_vm` DOUBLE NOT NULL ,
  `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `timestamp_UNIQUE` (`timestamp` ASC, `vm_id` ASC) )
ENGINE = InnoDB
AUTO_INCREMENT = 14757
DEFAULT CHARACTER SET = latin1;

-- -----------------------------------------------------
-- Table `iptrecdb`.`IP_eco_ip_table`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `iptrecdb`.`IP_eco_ip_table` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `energy_eff_ip` DOUBLE NOT NULL ,
  `ecological_eff_ip` DOUBLE NOT NULL ,
  `performance_ip` DOUBLE NOT NULL ,
  `power_ip` DOUBLE NOT NULL ,
  `grCO2s_ip` DOUBLE NOT NULL ,
  `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `timestamp_UNIQUE` (`timestamp` ASC) )
ENGINE = InnoDB
AUTO_INCREMENT = 59650
DEFAULT CHARACTER SET = latin1;
--
-- Table structure for table `ip_to_sp`
--

DROP TABLE IF EXISTS `ip_to_sp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ip_to_sp` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `service_id` varchar(45) NOT NULL,
  `component_id` varchar(45) NOT NULL,
  `sp_id` varchar(45) NOT NULL,
  `ip_id` varchar(45) NOT NULL,
  `service_time` double DEFAULT NULL,
  `service_risk` double DEFAULT NULL,
  `sercurity_assessment` double DEFAULT NULL,
  `service_reliability` double DEFAULT NULL,
  `performance` double DEFAULT NULL,
  `legal_openess` double DEFAULT NULL,
  `service_trust` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `component_id` (`component_id`),
  KEY `service_id` (`service_id`),
  KEY `ip_info` (`ip_id`),
  KEY `sp_info` (`sp_id`),
  KEY `servie_id` (`service_id`),
  CONSTRAINT `component_id` FOREIGN KEY (`component_id`) REFERENCES `service_component` (`component_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ip_info` FOREIGN KEY (`ip_id`) REFERENCES `ip_info` (`ip_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `sp_info` FOREIGN KEY (`sp_id`) REFERENCES `sp_info` (`sp_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=24072 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `sp_to_ip`
--

DROP TABLE IF EXISTS `sp_to_ip`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sp_to_ip` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `component_id` varchar(45) NOT NULL,
  `service_id` varchar(45) NOT NULL,
  `sp_id` varchar(45) NOT NULL,
  `ip_id` varchar(45) NOT NULL,
  `service_time` timestamp NULL DEFAULT NULL,
  `service_well_formed` double DEFAULT NULL,
  `safety_run_gap` double DEFAULT NULL,
  `elasticity_closely` double DEFAULT NULL,
  `ip_reaction_time` double DEFAULT NULL,
  `sla_compliance` double DEFAULT NULL,
  `ip_compliance_with_legal` double DEFAULT NULL,
  `service_trust` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `component_id_FK` (`component_id`),
  KEY `sp_id_FK` (`sp_id`),
  KEY `ip_id_FK` (`ip_id`),
  KEY `sp_to_ip_02` (`sp_id`,`ip_id`),
  CONSTRAINT `ip_id_FK` FOREIGN KEY (`ip_id`) REFERENCES `ip_info` (`ip_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `component_id_FK` FOREIGN KEY (`component_id`) REFERENCES `service_component` (`component_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `sp_id_FK` FOREIGN KEY (`sp_id`) REFERENCES `sp_info` (`sp_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service_component`
--

DROP TABLE IF EXISTS `service_component`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_component` (
  `component_id` varchar(45) NOT NULL,
  `service_id` varchar(45) NOT NULL,
  `component_manifest` longtext NOT NULL,
  PRIMARY KEY (`component_id`),
  KEY `service_id` (`service_id`),
  CONSTRAINT `service_id` FOREIGN KEY (`service_id`) REFERENCES `service_info` (`service_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ip_info`
--

DROP TABLE IF EXISTS `ip_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ip_info` (
  `ip_id` varchar(45) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `ipp_type` varchar(45) DEFAULT 'ip',
  `ip_location` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ip_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service_info`
--

DROP TABLE IF EXISTS `service_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_info` (
  `service_id` varchar(45) NOT NULL,
  `spId` varchar(45) DEFAULT NULL,
  `service_manifest` longtext,
  `deployed` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`service_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cost_ip_assessed_cost`
--

DROP TABLE IF EXISTS `cost_ip_assessed_cost`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cost_ip_assessed_cost` (
  `id` varchar(45) NOT NULL,
  `time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `service_name` varchar(45) DEFAULT NULL,
  `component_name` varchar(45) DEFAULT NULL,
  `node_name` varchar(45) DEFAULT NULL,
  `Currency` varchar(45) DEFAULT NULL,
  `CostPerVCPU` float DEFAULT NULL,
  `CostPerMBMemory` float DEFAULT NULL,
  `CostPerGBStorage` float DEFAULT NULL,
  `CostPerGBUploaded` float DEFAULT NULL,
  `CostPerGBDownloaded` float DEFAULT NULL,
  `CostPerWatt` float DEFAULT NULL,
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Persisted cost to IP, assessed on IP side';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cost_ip_persisted_quote_pricecomponent`
--

DROP TABLE IF EXISTS `cost_ip_persisted_quote_pricecomponent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cost_ip_persisted_quote_pricecomponent` (
  `id` varchar(45) NOT NULL,
  `sp_id` varchar(45) DEFAULT NULL,
  `service_id` varchar(45) DEFAULT NULL,
  `component_name` varchar(45) DEFAULT NULL,
  `component_cap` float DEFAULT NULL,
  `component_floor` float DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `proactra`
--

DROP TABLE IF EXISTS `proactra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `proactra` (
  `samplingPoint` bigint(20) NOT NULL,
  `vm_id` varchar(200) DEFAULT NULL,
  `vm_avail` double DEFAULT NULL,
  PRIMARY KEY (`samplingPoint`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `r_threats`
--

DROP TABLE IF EXISTS `r_threats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `r_threats` (
  `threat_id` int(11) NOT NULL,
  `asset` varchar(255) DEFAULT NULL,
  `likelihood` int(11) DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `stage` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `usecase` int(11) DEFAULT NULL,
  PRIMARY KEY (`threat_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cost_ip_set_cost`
--

DROP TABLE IF EXISTS `cost_ip_set_cost`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cost_ip_set_cost` (
  `id` varchar(45) NOT NULL COMMENT 'foreign key from ip_info',
  `currency` varchar(45) NOT NULL,
  `CostPerVCPU` float DEFAULT NULL,
  `CostPerMBMemory` float DEFAULT NULL,
  `CostPerGBStorage` float DEFAULT NULL,
  `CostPerGBUploaded` float DEFAULT NULL,
  `CostPerGBDownloaded` float DEFAULT NULL,
  `CostPerWatt` float DEFAULT NULL,
  `NodeType` varchar(45) NOT NULL COMMENT 'Isolated vs Colocated',
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `risk_resource_virtual`
--

DROP TABLE IF EXISTS `risk_resource_virtual`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `risk_resource_virtual` (
  `row_id` varchar(200) NOT NULL,
  `physical_resource_id` varchar(200) DEFAULT NULL,
  `virtual_resource_id` varchar(200) DEFAULT NULL,
  `metric_name` varchar(45) DEFAULT NULL,
  `metric_value` varchar(200) DEFAULT NULL,
  `metric_unit` varchar(45) DEFAULT NULL,
  `metric_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `service_resource_id` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`row_id`),
  KEY `monitoring_resource_01` (`service_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_02` (`virtual_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_03` (`physical_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_04` (`metric_name`,`metric_timestamp`),
  KEY `monitoring_resource_05` (`service_resource_id`),
  KEY `monitoring_resource_06` (`virtual_resource_id`),
  KEY `monitoring_resource_07` (`physical_resource_id`),
  KEY `monitoring_resource_08` (`metric_name`),
  KEY `monitoring_resource_09` (`metric_timestamp`),
  CONSTRAINT `risk_resource_virtual_ibfk_1` FOREIGN KEY (`physical_resource_id`) REFERENCES `risk_resource_physical` (`physical_resource_id`),
  CONSTRAINT `risk_resource_virtual_ibfk_2` FOREIGN KEY (`service_resource_id`) REFERENCES `risk_resource_service` (`service_resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sn_provider`
--

DROP TABLE IF EXISTS `sn_provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sn_provider` (
  `provider_id` varchar(50) NOT NULL,
  `provider_type` varchar(10) NOT NULL,
  PRIMARY KEY (`provider_id`,`provider_type`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `opphrars`
--

DROP TABLE IF EXISTS `opphrars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `opphrars` (
  `samplingPoint` bigint(20) NOT NULL,
  `assessmentResult` double DEFAULT NULL,
  PRIMARY KEY (`samplingPoint`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cost_ip_persisted_quote`
--

DROP TABLE IF EXISTS `cost_ip_persisted_quote`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cost_ip_persisted_quote` (
  `id` varchar(45) NOT NULL,
  `time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `sp_id` varchar(45) DEFAULT NULL,
  `service_name` varchar(45) DEFAULT NULL,
  `plan_cap` float DEFAULT NULL,
  `plan_floor` float DEFAULT NULL,
  KEY `id` (`id`),
  KEY `sp_id` (`sp_id`),
  KEY `service_name` (`service_name`),
  CONSTRAINT `cost_ip_persisted_quote_ibfk_2` FOREIGN KEY (`sp_id`) REFERENCES `sp_info` (`sp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sp_info`
--

DROP TABLE IF EXISTS `sp_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sp_info` (
  `sp_id` varchar(45) NOT NULL,
  `sp_name` varchar(45) DEFAULT NULL,
  `sp_type` varchar(45) DEFAULT 'sp',
  PRIMARY KEY (`sp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `risk_resource_service`
--

DROP TABLE IF EXISTS `risk_resource_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `risk_resource_service` (
  `row_id` varchar(200) NOT NULL,
  `physical_resource_id` varchar(200) DEFAULT NULL,
  `virtual_resource_id` varchar(200) DEFAULT NULL,
  `metric_name` varchar(45) DEFAULT NULL,
  `metric_value` varchar(200) DEFAULT NULL,
  `metric_unit` varchar(45) DEFAULT NULL,
  `metric_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `service_resource_id` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`row_id`),
  KEY `monitoring_resource_01` (`service_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_02` (`virtual_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_03` (`physical_resource_id`,`metric_timestamp`),
  KEY `monitoring_resource_04` (`metric_name`,`metric_timestamp`),
  KEY `monitoring_resource_05` (`service_resource_id`),
  KEY `monitoring_resource_06` (`virtual_resource_id`),
  KEY `monitoring_resource_07` (`physical_resource_id`),
  KEY `monitoring_resource_08` (`metric_name`),
  KEY `monitoring_resource_09` (`metric_timestamp`),
  CONSTRAINT `risk_resource_service_ibfk_1` FOREIGN KEY (`physical_resource_id`) REFERENCES `risk_resource_physical` (`physical_resource_id`),
  CONSTRAINT `risk_resource_service_ibfk_3` FOREIGN KEY (`virtual_resource_id`) REFERENCES `risk_resource_virtual` (`virtual_resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ipcost`
--

DROP TABLE IF EXISTS `ipcost`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ipcost` (
  `ip_id` varchar(45) NOT NULL,
  `Currency` varchar(45) DEFAULT NULL,
  `CostPerVCPU` float DEFAULT NULL,
  `CostPerMBMemory` float DEFAULT NULL,
  `CostPerGBStorage` float DEFAULT NULL,
  `CostPerGBUploaded` float DEFAULT NULL,
  `CostPerGBDownloaded` float DEFAULT NULL,
  `CostPerWatt` float DEFAULT NULL,
  PRIMARY KEY (`ip_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ip_trust`
--

DROP TABLE IF EXISTS `ip_trust`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ip_trust` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tstamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ip_id` varchar(45) NOT NULL,
  `ip_trust` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ip_id` (`ip_id`),
  CONSTRAINT `ip_trust_ibfk_1` FOREIGN KEY (`ip_id`) REFERENCES `ip_info` (`ip_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sn_trust_provider`
--

DROP TABLE IF EXISTS `sn_trust_provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sn_trust_provider` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `provider_id` varchar(50) NOT NULL,
  `provider_type` varchar(10) NOT NULL,
  `expectation` double DEFAULT NULL,
  `belief` double DEFAULT NULL,
  `disbelief` double DEFAULT NULL,
  `uncertinty` double DEFAULT NULL,
  `relative_automicity` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `manifest_raw`
--

DROP TABLE IF EXISTS `manifest_raw`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `manifest_raw` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `service_id` varchar(45) NOT NULL,
  `service_manifest` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=28 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cost_ip_threshold_co`
--

DROP TABLE IF EXISTS `cost_ip_threshold_co`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cost_ip_threshold_co` (
  `id` varchar(45) NOT NULL,
  `service_id` varchar(45) NOT NULL,
  `cost_threshold` float DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sn_trust_relationship`
--

DROP TABLE IF EXISTS `sn_trust_relationship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sn_trust_relationship` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `origin` varchar(50) NOT NULL,
  `origin_type` varchar(10) NOT NULL,
  `destiny` varchar(50) NOT NULL,
  `destiny_type` varchar(10) NOT NULL,
  `expectation` double DEFAULT NULL,
  `belief` double DEFAULT NULL,
  `disbelief` double DEFAULT NULL,
  `uncertinty` double DEFAULT NULL,
  `relative_automicity` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `origin` (`origin`,`origin_type`,`destiny`,`destiny_type`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rejections`
--

DROP TABLE IF EXISTS `rejections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rejections` (
  `sla_id` int(11) NOT NULL,
  `exception` longtext,
  `provider_dn` varchar(255) DEFAULT NULL,
  `provider_id` int(11) DEFAULT NULL,
  `time_rejected` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`sla_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'iptrecdb_tst'
--

--
-- Dumping routines for database 'iptrecdb_tst'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-12-10 12:03:38
