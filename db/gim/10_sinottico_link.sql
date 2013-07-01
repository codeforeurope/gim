CREATE TABLE `sinottico_link` (
  `idno` int(11) NOT NULL,
  `dirx` int(11) NOT NULL,
  `id_street` int(11) NOT NULL DEFAULT '-1',
  `avg_speed` double DEFAULT NULL,
  `speed_limit` double DEFAULT NULL,
  `shap` multilinestring NOT NULL,
  `timestamp` datetime NOT NULL,
  PRIMARY KEY (`idno`,`dirx`,`id_street`,`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;