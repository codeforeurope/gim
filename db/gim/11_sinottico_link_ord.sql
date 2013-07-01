CREATE TABLE `sinottico_link_ord` (
  `id` int(11) NOT NULL,
  `dir` int(11) NOT NULL DEFAULT '-1',
  `id_street` int(11) NOT NULL DEFAULT '-1',
  `avg_speed` text,
  `speed_limit` text,
  `shap` text NOT NULL,
  `timestamp` datetime NOT NULL,
  PRIMARY KEY (`id`,`timestamp`),
  KEY `idx_sinottico_link_ord_id_street` (`id_street`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;