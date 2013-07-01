CREATE TABLE IF NOT EXISTS `link_ord` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idno` text NOT NULL,
  `dir` int(11) NOT NULL,
  `shap` text NOT NULL,
  `id_street` int(11) DEFAULT NULL,
  `dirx` text,
  `free_flow_speed` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3147 ;
