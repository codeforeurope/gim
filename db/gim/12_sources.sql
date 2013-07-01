
CREATE TABLE IF NOT EXISTS `sources` (
  `id_source` char(10) CHARACTER SET latin1 NOT NULL,
  `description` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `color` varchar(6) CHARACTER SET latin1 NOT NULL,
  `level` int(11) NOT NULL,
  PRIMARY KEY (`id_source`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `sources` (`id_source`, `description`, `color`, `level`) VALUES
('B', 'BeMove', '00FF00', 1),
('I', 'Infomobility', '00FFFF', 50),
('P', 'Pollicino', '000000', 1),
('T', 'TargaInfomobility', '0000FF', 10);

