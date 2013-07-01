CREATE TABLE `position_fcd_geo` (
  `id_pos` int(11) DEFAULT NULL,
  `idno` int(11) DEFAULT NULL,
  `dist_from_start` float DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  KEY `FK_position_fcd_geo` (`id_pos`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Campionamenti FCD georeferenziati';
