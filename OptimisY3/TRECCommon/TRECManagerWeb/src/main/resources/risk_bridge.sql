-- phpMyAdmin SQL Dump
-- version 3.4.9
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Feb 26, 2013 at 08:46 PM
-- Server version: 5.1.65
-- PHP Version: 5.3.3

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `risk_bridge`
--

-- --------------------------------------------------------

--
-- Table structure for table `ip_deployment`
--

CREATE TABLE IF NOT EXISTS `ip_deployment` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT,
  `providerId` int(10) unsigned NOT NULL,
  `serviceId` int(10) NOT NULL,
  `timeStamp` int(10) unsigned NOT NULL,
  `riskValue` double unsigned NOT NULL,
  `graphType` int(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `ip_operation`
--

CREATE TABLE IF NOT EXISTS `ip_operation` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT,
  `providerId` int(10) unsigned NOT NULL,
  `serviceId` int(10) NOT NULL,
  `timeStamp` int(10) unsigned NOT NULL,
  `riskValue` double unsigned NOT NULL,
  `graphType` int(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `provider_id`
--

CREATE TABLE IF NOT EXISTS `provider_id` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(1024) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `service_id`
--

CREATE TABLE IF NOT EXISTS `service_id` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(1024) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `sp_deployment`
--

CREATE TABLE IF NOT EXISTS `sp_deployment` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT,
  `providerId` int(10) unsigned NOT NULL,
  `serviceId` int(10) NOT NULL,
  `timeStamp` int(10) unsigned NOT NULL,
  `riskValue` double unsigned NOT NULL,
  `graphType` int(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `sp_operation`
--

CREATE TABLE IF NOT EXISTS `sp_operation` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT,
  `providerId` int(10) unsigned NOT NULL,
  `serviceId` int(10) NOT NULL,
  `timeStamp` int(10) unsigned NOT NULL,
  `riskValue` double unsigned NOT NULL,
  `graphType` int(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
