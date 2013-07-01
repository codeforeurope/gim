-- Tabella con le statistiche di traffico aggiornate in tempo (quasi) reale

CREATE TABLE `traffic_stats` (
  `idno` int(11) NOT NULL,
  `dir` int(11) NOT NULL DEFAULT '0',
  `samples` int(11) NOT NULL DEFAULT '0',
  `vehicles` int(11) NOT NULL DEFAULT '0',
  `avg_speed` float NOT NULL,
  `stddev_speed` float NOT NULL DEFAULT '0',
  `density` float DEFAULT NULL,
  `flow` float DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`idno`,`dir`,`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Statistiche di traffico live';