CREATE TABLE `position_fcd` (
  `id_pos` int(11) NOT NULL AUTO_INCREMENT,
  `id_vehicle` varchar(100) DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  `timestamp_gps` datetime DEFAULT NULL,
  `gps_status` varchar(100) DEFAULT NULL,
  `dir` int(11) DEFAULT NULL,
  `speed` int(11) DEFAULT NULL,
  `satellites` int(11) DEFAULT NULL,
  `odometer` int(11) DEFAULT NULL,
  `latitude` int(11) DEFAULT NULL,
  `longitude` int(11) DEFAULT NULL,
  `key` varchar(100) DEFAULT NULL,
  `event` varchar(100) DEFAULT NULL,
  `vehicle_type` varchar(100) DEFAULT NULL,
  `id_panelsession` int(11) DEFAULT NULL,
  `quality` int(11) DEFAULT NULL,
  `deltapos` int(11) DEFAULT NULL,
  `deltatime` int(11) DEFAULT NULL,
  `source` char(10) DEFAULT NULL,
  PRIMARY KEY (`id_pos`),
  KEY `position_timestamp` (`timestamp`),
  KEY `position_source` (`source`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Campionamenti FCD';

CREATE INDEX idx_position_fcd_timestamp ON position_fcd (timestamp);
CREATE INDEX idx_position_fcd_source ON position_fcd (source);