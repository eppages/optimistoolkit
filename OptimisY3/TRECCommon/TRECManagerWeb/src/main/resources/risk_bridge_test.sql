-- phpMyAdmin SQL Dump
-- version 3.4.9
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Feb 26, 2013 at 08:47 PM
-- Server version: 5.1.65
-- PHP Version: 5.3.3

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `risk_bridge_test`
--

-- --------------------------------------------------------

--
-- Table structure for table `ip_deployment`
--

DROP TABLE IF EXISTS `ip_deployment`;
CREATE TABLE IF NOT EXISTS `ip_deployment` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT,
  `providerId` int(10) unsigned NOT NULL,
  `serviceId` int(10) NOT NULL,
  `timeStamp` int(10) unsigned NOT NULL,
  `riskValue` double unsigned NOT NULL,
  `graphType` int(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `ip_deployment`
--

INSERT INTO `ip_deployment` (`id`, `providerId`, `serviceId`, `timeStamp`, `riskValue`, `graphType`) VALUES
(1, 1, 1, 1361804843, 1.5, 1);

-- --------------------------------------------------------

--
-- Table structure for table `ip_operation`
--

DROP TABLE IF EXISTS `ip_operation`;
CREATE TABLE IF NOT EXISTS `ip_operation` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT,
  `providerId` int(10) NOT NULL,
  `serviceId` int(10) unsigned NOT NULL,
  `timeStamp` int(10) unsigned NOT NULL,
  `riskValue` double unsigned NOT NULL,
  `graphType` int(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=121 ;

--
-- Dumping data for table `ip_operation`
--

INSERT INTO `ip_operation` (`id`, `providerId`, `serviceId`, `timeStamp`, `riskValue`, `graphType`) VALUES
(1, 1, 1, 1361804843, 1.1, 1),
(2, 1, 1, 1361804844, 1.1, 1),
(3, 1, 1, 1361804845, 1.1, 1),
(4, 1, 1, 1361804846, 1.1, 1),
(5, 1, 1, 1361804847, 1.1, 1),
(6, 1, 1, 1361804848, 1.1, 1),
(7, 1, 1, 1361804849, 1.1, 1),
(8, 1, 1, 1361804850, 1.1, 1),
(9, 1, 1, 1361804851, 1.1, 1),
(10, 1, 1, 1361804852, 1.1, 1),
(11, 1, 1, 1361804853, 1.1, 1),
(12, 1, 1, 1361804854, 1.1, 1),
(13, 1, 1, 1361804855, 2, 1),
(14, 1, 1, 1361804856, 2, 1),
(15, 1, 1, 1361804857, 2, 1),
(16, 1, 1, 1361804858, 2, 1),
(17, 1, 1, 1361804859, 2, 1),
(18, 1, 1, 1361804860, 2, 1),
(19, 1, 1, 1361804861, 1.1, 1),
(20, 1, 1, 1361804862, 1.1, 1),
(21, 1, 1, 1361804863, 1.1, 1),
(22, 1, 1, 1361804864, 1.1, 1),
(23, 1, 1, 1361804865, 1.1, 1),
(24, 1, 1, 1361804866, 1.1, 1),
(25, 1, 1, 1361804867, 1.1, 1),
(26, 1, 1, 1361804868, 1.1, 1),
(27, 1, 1, 1361804869, 1.1, 1),
(28, 1, 1, 1361804870, 1.1, 1),
(29, 1, 1, 1361804871, 1.1, 1),
(30, 1, 1, 1361804872, 1.1, 1),
(31, 1, 1, 1361804843, 2.3, 2),
(32, 1, 1, 1361804844, 2.3, 2),
(33, 1, 1, 1361804845, 2.3, 2),
(34, 1, 1, 1361804846, 2.3, 2),
(35, 1, 1, 1361804847, 2.3, 2),
(36, 1, 1, 1361804848, 2.3, 2),
(37, 1, 1, 1361804849, 2.3, 2),
(38, 1, 1, 1361804850, 2.3, 2),
(39, 1, 1, 1361804851, 2.3, 2),
(40, 1, 1, 1361804852, 2.3, 2),
(41, 1, 1, 1361804853, 2.3, 2),
(42, 1, 1, 1361804854, 2.3, 2),
(43, 1, 1, 1361804855, 2.3, 2),
(44, 1, 1, 1361804856, 2.7, 2),
(45, 1, 1, 1361804857, 2.7, 2),
(46, 1, 1, 1361804858, 2.7, 2),
(47, 1, 1, 1361804859, 2.7, 2),
(48, 1, 1, 1361804860, 2.7, 2),
(49, 1, 1, 1361804861, 2.3, 2),
(50, 1, 1, 1361804862, 2.3, 2),
(51, 1, 1, 1361804863, 2.3, 2),
(52, 1, 1, 1361804864, 2.3, 2),
(53, 1, 1, 1361804865, 2.3, 2),
(54, 1, 1, 1361804866, 2.3, 2),
(55, 1, 1, 1361804867, 3.5, 2),
(56, 1, 1, 1361804868, 3.5, 2),
(57, 1, 1, 1361804869, 3.5, 2),
(58, 1, 1, 1361804870, 2.3, 2),
(59, 1, 1, 1361804871, 2.3, 2),
(60, 1, 1, 1361804872, 2.3, 2),
(61, 1, 1, 1361804843, 1.5, 3),
(62, 1, 1, 1361804844, 1.5, 3),
(63, 1, 1, 1361804845, 1.5, 3),
(64, 1, 1, 1361804846, 1.5, 3),
(65, 1, 1, 1361804847, 1.5, 3),
(66, 1, 1, 1361804848, 1.5, 3),
(67, 1, 1, 1361804849, 1.5, 3),
(68, 1, 1, 1361804850, 1.5, 3),
(69, 1, 1, 1361804851, 1.5, 3),
(70, 1, 1, 1361804852, 1.5, 3),
(71, 1, 1, 1361804853, 1.5, 3),
(72, 1, 1, 1361804854, 1.5, 3),
(73, 1, 1, 1361804855, 1.5, 3),
(74, 1, 1, 1361804856, 4.1, 3),
(75, 1, 1, 1361804857, 4.1, 3),
(76, 1, 1, 1361804858, 4.1, 3),
(77, 1, 1, 1361804859, 4.1, 3),
(78, 1, 1, 1361804860, 4.1, 3),
(79, 1, 1, 1361804861, 4.1, 3),
(80, 1, 1, 1361804862, 1.5, 3),
(81, 1, 1, 1361804863, 1.5, 3),
(82, 1, 1, 1361804864, 1.5, 3),
(83, 1, 1, 1361804865, 1.5, 3),
(84, 1, 1, 1361804866, 1.5, 3),
(85, 1, 1, 1361804867, 2.9, 3),
(86, 1, 1, 1361804868, 2.9, 3),
(87, 1, 1, 1361804869, 2.9, 3),
(88, 1, 1, 1361804870, 1.5, 3),
(89, 1, 1, 1361804871, 1.5, 3),
(90, 1, 1, 1361804872, 1.5, 3),
(91, 1, 1, 1361804843, 0.9, 4),
(92, 1, 1, 1361804844, 0.9, 4),
(93, 1, 1, 1361804845, 0.9, 4),
(94, 1, 1, 1361804846, 0.9, 4),
(95, 1, 1, 1361804847, 0.9, 4),
(96, 1, 1, 1361804848, 0.9, 4),
(97, 1, 1, 1361804849, 0.9, 4),
(98, 1, 1, 1361804850, 0.9, 4),
(99, 1, 1, 1361804851, 0.9, 4),
(100, 1, 1, 1361804852, 0.9, 4),
(101, 1, 1, 1361804853, 0.9, 4),
(102, 1, 1, 1361804854, 0.9, 4),
(103, 1, 1, 1361804855, 0.9, 4),
(104, 1, 1, 1361804856, 0.9, 4),
(105, 1, 1, 1361804857, 0.9, 4),
(106, 1, 1, 1361804858, 0.9, 4),
(107, 1, 1, 1361804859, 0.9, 4),
(108, 1, 1, 1361804860, 0.9, 4),
(109, 1, 1, 1361804861, 0.9, 4),
(110, 1, 1, 1361804862, 0.9, 4),
(111, 1, 1, 1361804863, 0.9, 4),
(112, 1, 1, 1361804864, 0.9, 4),
(113, 1, 1, 1361804865, 0.9, 4),
(114, 1, 1, 1361804866, 0.9, 4),
(115, 1, 1, 1361804867, 0.9, 4),
(116, 1, 1, 1361804868, 0.9, 4),
(117, 1, 1, 1361804869, 0.9, 4),
(118, 1, 1, 1361804870, 0.9, 4),
(119, 1, 1, 1361804871, 0.9, 4),
(120, 1, 1, 1361804872, 0.9, 4);

-- --------------------------------------------------------

--
-- Table structure for table `provider_id`
--

DROP TABLE IF EXISTS `provider_id`;
CREATE TABLE IF NOT EXISTS `provider_id` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(1024) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `provider_id`
--

INSERT INTO `provider_id` (`id`, `name`) VALUES
(1, 'atos');

-- --------------------------------------------------------

--
-- Table structure for table `service_id`
--

DROP TABLE IF EXISTS `service_id`;
CREATE TABLE IF NOT EXISTS `service_id` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(1024) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `service_id`
--

INSERT INTO `service_id` (`id`, `name`) VALUES
(1, '76c44bda-4f5a-4f97-806d-011d174bea44');

-- --------------------------------------------------------

--
-- Table structure for table `sp_deployment`
--

DROP TABLE IF EXISTS `sp_deployment`;
CREATE TABLE IF NOT EXISTS `sp_deployment` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT,
  `providerId` int(10) unsigned NOT NULL,
  `serviceId` int(10) NOT NULL,
  `timeStamp` int(10) unsigned NOT NULL,
  `riskValue` double unsigned NOT NULL,
  `graphType` int(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `sp_deployment`
--

INSERT INTO `sp_deployment` (`id`, `providerId`, `serviceId`, `timeStamp`, `riskValue`, `graphType`) VALUES
(1, 1, 1, 1361804843, 1, 1),
(2, 1, 1, 1361804844, 2, 2);

-- --------------------------------------------------------

--
-- Table structure for table `sp_operation`
--

DROP TABLE IF EXISTS `sp_operation`;
CREATE TABLE IF NOT EXISTS `sp_operation` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT,
  `providerId` int(10) NOT NULL,
  `serviceId` int(10) unsigned NOT NULL,
  `timeStamp` int(10) unsigned NOT NULL,
  `riskValue` double unsigned NOT NULL,
  `graphType` int(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=31 ;

--
-- Dumping data for table `sp_operation`
--

INSERT INTO `sp_operation` (`id`, `providerId`, `serviceId`, `timeStamp`, `riskValue`, `graphType`) VALUES
(1, 1, 1, 1361804843, 1.4, 1),
(2, 1, 1, 1361804844, 1.4, 1),
(3, 1, 1, 1361804845, 1.4, 1),
(4, 1, 1, 1361804846, 1.4, 1),
(5, 1, 1, 1361804847, 1.4, 1),
(6, 1, 1, 1361804848, 1.4, 1),
(7, 1, 1, 1361804849, 1.4, 1),
(8, 1, 1, 1361804850, 1.4, 1),
(9, 1, 1, 1361804851, 1.4, 1),
(10, 1, 1, 1361804852, 1.4, 1),
(11, 1, 1, 1361804853, 1.4, 1),
(12, 1, 1, 1361804854, 1.4, 1),
(13, 1, 1, 1361804855, 1.4, 1),
(14, 1, 1, 1361804856, 2.7, 1),
(15, 1, 1, 1361804857, 2.7, 1),
(16, 1, 1, 1361804858, 2.7, 1),
(17, 1, 1, 1361804859, 2.7, 1),
(18, 1, 1, 1361804860, 2.7, 1),
(19, 1, 1, 1361804861, 1.4, 1),
(20, 1, 1, 1361804862, 1.4, 1),
(21, 1, 1, 1361804863, 1.4, 1),
(22, 1, 1, 1361804864, 1.4, 1),
(23, 1, 1, 1361804865, 1.4, 1),
(24, 1, 1, 1361804866, 1.4, 1),
(25, 1, 1, 1361804867, 1.4, 1),
(26, 1, 1, 1361804868, 1.4, 1),
(27, 1, 1, 1361804869, 1.4, 1),
(28, 1, 1, 1361804870, 1.4, 1),
(29, 1, 1, 1361804871, 1.4, 1),
(30, 1, 1, 1361804872, 1.4, 1);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
