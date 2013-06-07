SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE DATABASE  IF NOT EXISTS `sptrecdb` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `sptrecdb`;

-- -----------------------------------------------------
-- Table `cost_sp_persisted_quote`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cost_sp_persisted_quote`;
CREATE  TABLE IF NOT EXISTS `cost_sp_persisted_quote` (
  `id` VARCHAR(45) NOT NULL ,
  `time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ,
  `service_name` VARCHAR(45) NULL DEFAULT NULL ,
  `plan_cap` FLOAT NULL DEFAULT NULL ,
  `plan_floor` FLOAT NULL DEFAULT NULL ,
  INDEX `id` (`id` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `cost_sp_persisted_quote_pricecomponent`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cost_sp_persisted_quote_pricecomponent`;
CREATE  TABLE IF NOT EXISTS `cost_sp_persisted_quote_pricecomponent` (
  `id` VARCHAR(45) NOT NULL ,
  `service_id` VARCHAR(45) NULL DEFAULT NULL ,
  `component_name` VARCHAR(45) NULL DEFAULT NULL ,
  `component_cap` FLOAT NULL DEFAULT NULL ,
  `component_floor` FLOAT NULL DEFAULT NULL )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `cost_sp_persisted_quote_pricecomponent_pricelevel`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cost_sp_persisted_quote_pricecomponent_pricelevel`;
CREATE  TABLE IF NOT EXISTS `cost_sp_persisted_quote_pricecomponent_pricelevel` (
  `id` VARCHAR(45) NOT NULL ,
  `service_id` VARCHAR(45) NOT NULL ,
  `component_name` VARCHAR(45) NOT NULL ,
  `price_type` VARCHAR(45) NOT NULL ,
  `price_level_name` VARCHAR(45) NOT NULL ,
  `absolute_amount` FLOAT NULL DEFAULT NULL ,
  `multiplier` FLOAT NULL DEFAULT NULL )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `dsahpproviders`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dsahpproviders`;
CREATE  TABLE IF NOT EXISTS `dsahpproviders` (
  `distName` varchar(255) NOT NULL,
 `pastPerf` double DEFAULT NULL,
`geography` double DEFAULT NULL,
`certandstd` double DEFAULT NULL,
 `businessStability` double DEFAULT NULL,
`infrastructure` double DEFAULT NULL,
  `security` double DEFAULT NULL,
 `privacy` double DEFAULT NULL, 
  `belief` double DEFAULT NULL,
  `plausibility` double DEFAULT NULL,
  `rating` double DEFAULT NULL,
 `t50` int(11) DEFAULT NULL,
  `t95` int(11) DEFAULT NULL,
  PRIMARY KEY (`distName`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `SP_eco_service_table`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SP_eco_service_table`;
CREATE  TABLE IF NOT EXISTS `SP_eco_service_table` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `service_id` VARCHAR(45) NOT NULL ,
  `energy_eff_service` DOUBLE NOT NULL ,
  `ecological_eff_service` DOUBLE NOT NULL ,
  `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `timestamp_UNIQUE` (`timestamp` ASC, `service_id` ASC) )
ENGINE = InnoDB
AUTO_INCREMENT = 8
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `ip_info`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ip_info`;
CREATE  TABLE IF NOT EXISTS `ip_info` (
  `ip_id` VARCHAR(50) NOT NULL ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  `ip_location` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`ip_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `ip_trust`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ip_trust`;
CREATE  TABLE IF NOT EXISTS `ip_trust` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `tstamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP ,
  `ip_id` VARCHAR(45) NOT NULL ,
  `ip_trust` DOUBLE NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ip_id` (`ip_id` ASC) ,
  CONSTRAINT `ip_trust_ibfk_1`
    FOREIGN KEY (`ip_id` )
    REFERENCES `ip_info` (`ip_id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 31
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `manifest_raw`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `manifest_raw`;
CREATE  TABLE IF NOT EXISTS `manifest_raw` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `service_id` VARCHAR(45) NOT NULL ,
  `service_manifest` LONGTEXT NOT NULL ,
  `is_broker` TINYINT(1) UNSIGNED ZEROFILL NOT NULL DEFAULT '0' ,
  `broker_host` VARCHAR(45) NULL DEFAULT NULL ,
  `broker_port` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = MyISAM
AUTO_INCREMENT = 16
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `providers`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `providers`;
CREATE  TABLE IF NOT EXISTS `providers` (
  `provider_id` INT(11) NOT NULL AUTO_INCREMENT ,
  `delegation_epr` LONGTEXT NULL DEFAULT NULL ,
  `inService` BIT(1) NULL DEFAULT NULL ,
  `provider_dn` LONGTEXT NULL DEFAULT NULL ,
  `provider_epr` LONGTEXT NULL DEFAULT NULL ,
  PRIMARY KEY (`provider_id`) )
ENGINE = MyISAM
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `quotes`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `quotes`;
CREATE  TABLE IF NOT EXISTS `quotes` (
  `sla_id` INT(11) NOT NULL AUTO_INCREMENT ,
  `provider_dn` VARCHAR(255) NULL DEFAULT NULL ,
  `time_quoted` DATETIME NULL DEFAULT NULL ,
  `uuid` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`sla_id`) )
ENGINE = MyISAM
AUTO_INCREMENT = 33449
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `rejections`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rejections`;
CREATE  TABLE IF NOT EXISTS `rejections` (
  `sla_id` INT(11) NOT NULL AUTO_INCREMENT ,
  `exception` LONGTEXT NULL DEFAULT NULL ,
  `provider_dn` VARCHAR(255) NULL DEFAULT NULL ,
  `provider_id` INT(11) NULL DEFAULT NULL ,
  `time_rejected` DATETIME NULL DEFAULT NULL ,
  `uuid` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`sla_id`) )
ENGINE = MyISAM
AUTO_INCREMENT = 3369
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `r_threats`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `r_threats`;
CREATE  TABLE IF NOT EXISTS `r_threats` (
  `threat_id` INT(11) NOT NULL ,
  `usecase_cloud` INT(11) NULL DEFAULT '0' ,
  `stage_dep_oper` INT(11) NULL DEFAULT '0' ,
  `asset` VARCHAR(45) NULL DEFAULT NULL ,
  `priority` INT(11) NULL DEFAULT '0' ,
  `likelihood` INT(11) NULL DEFAULT '0' ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  `mitigation` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`threat_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `service_info`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `service_info`;
CREATE  TABLE IF NOT EXISTS `service_info` (
  `service_id` VARCHAR(45) NOT NULL ,
  `spId` VARCHAR(45) NULL DEFAULT NULL ,
  `service_manifest` LONGTEXT NULL DEFAULT NULL ,
  `sla_id` VARCHAR(45) NULL DEFAULT NULL ,
  `deployed` TINYINT(1) NULL DEFAULT NULL ,
  PRIMARY KEY (`service_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `service_component`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `service_component`;
CREATE  TABLE IF NOT EXISTS `service_component` (
  `component_id` VARCHAR(45) NOT NULL ,
  `service_id` VARCHAR(45) NOT NULL ,
  `component_manifest` LONGTEXT NULL DEFAULT NULL ,
  PRIMARY KEY (`component_id`) ,
  INDEX `service_id` (`service_id` ASC) ,
  CONSTRAINT `service_id`
    FOREIGN KEY (`service_id` )
    REFERENCES `service_info` (`service_id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `service_sla`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `service_sla`;
CREATE  TABLE IF NOT EXISTS `service_sla` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `serviceId` VARCHAR(45) NOT NULL ,
  `ip_id` VARCHAR(45) NOT NULL ,
  `slaId` VARCHAR(100) NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `FK_service_sla` (`serviceId` ASC) ,
  INDEX `FK_ip_sla` (`ip_id` ASC) )
ENGINE = MyISAM
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `slas`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `slas`;
CREATE  TABLE IF NOT EXISTS `slas` (
  `sla_id` INT(11) NOT NULL AUTO_INCREMENT ,
  `expirationTime` DATETIME NULL DEFAULT NULL ,
  `offerTime` DATETIME NULL DEFAULT NULL ,
  `penalty` DOUBLE NULL DEFAULT NULL ,
  `price` DOUBLE NULL DEFAULT NULL ,
  `provider_dn` VARCHAR(255) NULL DEFAULT NULL ,
  `provider_id` INT(11) NULL DEFAULT NULL ,
  `risk` DOUBLE NULL DEFAULT NULL ,
  `state` INT(11) NULL DEFAULT NULL ,
  `uuid` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`sla_id`) )
ENGINE = MyISAM
AUTO_INCREMENT = 30080
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `sn_provider`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sn_provider`;
CREATE  TABLE IF NOT EXISTS `sn_provider` (
  `provider_id` VARCHAR(50) NOT NULL ,
  `provider_type` VARCHAR(10) NOT NULL ,
  PRIMARY KEY (`provider_id`, `provider_type`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `sn_trust_provider`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sn_trust_provider`;
CREATE  TABLE IF NOT EXISTS `sn_trust_provider` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `provider_id` VARCHAR(50) NOT NULL ,
  `provider_type` VARCHAR(10) NOT NULL ,
  `expectation` DOUBLE NULL DEFAULT NULL ,
  `belief` DOUBLE NULL DEFAULT NULL ,
  `disbelief` DOUBLE NULL DEFAULT NULL ,
  `uncertinty` DOUBLE NULL DEFAULT NULL ,
  `relative_automicity` DOUBLE NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `sn_trust_relationship`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sn_trust_relationship`;
CREATE  TABLE IF NOT EXISTS `sn_trust_relationship` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `origin` VARCHAR(50) NOT NULL ,
  `origin_type` VARCHAR(10) NOT NULL ,
  `destiny` VARCHAR(50) NOT NULL ,
  `destiny_type` VARCHAR(10) NOT NULL ,
  `expectation` DOUBLE NULL DEFAULT NULL ,
  `belief` DOUBLE NULL DEFAULT NULL ,
  `disbelief` DOUBLE NULL DEFAULT NULL ,
  `uncertinty` DOUBLE NULL DEFAULT NULL ,
  `relative_automicity` DOUBLE NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `origin` (`origin` ASC, `origin_type` ASC, `destiny` ASC, `destiny_type` ASC) )
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `spcost`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `spcost`;
CREATE  TABLE IF NOT EXISTS `spcost` (
  `sp_id` VARCHAR(45) NOT NULL ,
  `currency` VARCHAR(50) NULL DEFAULT NULL ,
  `PricePerVCPU` FLOAT NULL DEFAULT NULL ,
  `PricePerMBMemory` FLOAT NULL DEFAULT NULL ,
  `PricePerGBStorage` FLOAT NULL DEFAULT NULL ,
  `PricePerGBUploaded` FLOAT NULL DEFAULT NULL ,
  `PricePerGBDownloaded` FLOAT NULL DEFAULT NULL ,
  `PricePerWatt` FLOAT NULL DEFAULT NULL ,
  PRIMARY KEY (`sp_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `sp_info`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sp_info`;
CREATE  TABLE IF NOT EXISTS `sp_info` (
  `sp_id` VARCHAR(45) NOT NULL ,
  `sp_name` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`sp_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `sp_risk`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sp_risk`;
CREATE  TABLE IF NOT EXISTS `sp_risk` (
  `rec_id` INT NOT NULL ,
  `sp_id` VARCHAR(45) NULL DEFAULT NULL ,
  `sp_name` VARCHAR(45) NULL DEFAULT NULL ,
  `samplingPoint` LONGTEXT NULL DEFAULT NULL ,
  `pyhost_risk` DOUBLE NULL DEFAULT NULL ,
  `vm_risk` DOUBLE NULL DEFAULT NULL ,
  `sec_risk` DOUBLE NULL DEFAULT NULL ,
  `legaldm_risk` DOUBLE NULL DEFAULT NULL ,
  PRIMARY KEY (`rec_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `sp_to_ip`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sp_to_ip`;
CREATE  TABLE IF NOT EXISTS `sp_to_ip` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `component_id` VARCHAR(45) NOT NULL ,
  `service_id` VARCHAR(45) NOT NULL ,
  `sp_id` VARCHAR(45) NOT NULL ,
  `ip_id` VARCHAR(45) NOT NULL ,
  `service_time` TIMESTAMP NULL DEFAULT NULL ,
  `service_well_formed` DOUBLE NULL DEFAULT NULL ,
  `safety_run_gap` DOUBLE NULL DEFAULT NULL ,
  `elasticity_closely` DOUBLE NULL DEFAULT NULL ,
  `ip_reaction_time` DOUBLE NULL DEFAULT NULL ,
  `sla_compliance` DOUBLE NULL DEFAULT NULL ,
  `ip_compliance_with_legal` DOUBLE NULL DEFAULT NULL ,
  `service_trust` DOUBLE NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `sp_to_ip_02` (`sp_id` ASC, `ip_id` ASC) ,
  INDEX `component_id` (`component_id` ASC) ,
  INDEX `sp_id` (`sp_id` ASC) ,
  INDEX `ip_id` (`ip_id` ASC) ,
  CONSTRAINT `component_id`
    FOREIGN KEY (`component_id` )
    REFERENCES `service_component` (`component_id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `ip_id`
    FOREIGN KEY (`ip_id` )
    REFERENCES `ip_info` (`ip_id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `sp_id`
    FOREIGN KEY (`sp_id` )
    REFERENCES `sp_info` (`sp_id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `sp_trust`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sp_trust`;
CREATE  TABLE IF NOT EXISTS `sp_trust` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `tstamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP ,
  `sp_id` VARCHAR(45) NOT NULL ,
  `sp_trust` DOUBLE NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `sp_id` (`sp_id` ASC) ,
  CONSTRAINT `sp_trust_ibfk_1`
    FOREIGN KEY (`sp_id` )
    REFERENCES `sp_info` (`sp_id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 31
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `risk_asset`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `risk_asset`;
CREATE  TABLE IF NOT EXISTS `risk_asset` (
  `idrisk_asset` INT NOT NULL ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  `vulnerability` VARCHAR(45) NULL DEFAULT NULL ,
  `mitigation` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`idrisk_asset`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `risk_vulnerability`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `risk_vulnerability`;
CREATE  TABLE IF NOT EXISTS `risk_vulnerability` (
  `idrisk_vulnerability` INT NOT NULL ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  `description` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`idrisk_vulnerability`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `risk_threat`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `risk_threat`;
CREATE  TABLE IF NOT EXISTS `risk_threat` (
  `idrisk_threat` INT NOT NULL ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  `usecase` VARCHAR(45) NULL DEFAULT NULL ,
  `stage` VARCHAR(45) BINARY NULL DEFAULT NULL ,
  `likelihood` VARCHAR(45) NULL DEFAULT NULL ,
  `vulnerability` INT NULL DEFAULT 0 ,
  PRIMARY KEY (`idrisk_threat`) )
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
