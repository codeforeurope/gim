-- Tabella con lo storico delle statistiche del traffico

CREATE TABLE `traffic_stats_history` (
  `idno` int(11) NOT NULL,
  `dir` int(11) NOT NULL DEFAULT '0',
  `samples` int(11) DEFAULT NULL,
  `vehicles` int(11) NOT NULL DEFAULT '0',
  `avg_speed` float NOT NULL,
  `stddev_speed` float NOT NULL DEFAULT '0',
  `density` float DEFAULT NULL,
  `flow` float DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`idno`,`dir`,`timestamp`),
  KEY `timestamp` (`timestamp`),
  KEY `idno` (`idno`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='Contiene la serie storica delle statistiche del traffico';

CREATE INDEX idx_traffic_stats_history_timestamp ON traffic_stats_history (timestamp);
CREATE INDEX idx_traffic_stats_history_idno ON traffic_stats_history (idno);