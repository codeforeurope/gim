CREATE TABLE IF NOT EXISTS `t_street` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `type` varchar(5) NOT NULL,
  `lat_up` double DEFAULT NULL,
  `lon_up` double DEFAULT NULL,
  `lat_down` double DEFAULT NULL,
  `lon_down` double DEFAULT NULL,
  `offset` int(11) DEFAULT NULL,
  `dir_offset` varchar(45) DEFAULT NULL,
  `width` int(11) DEFAULT NULL,
  `dir0` varchar(45) DEFAULT NULL,
  `dir1` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `t_street` (`id`, `name`, `type`, `lat_up`, `lon_up`, `lat_down`, `lon_down`, `offset`, `dir_offset`, `width`, `dir0`, `dir1`) VALUES
(1, 'Grande Raccordo Anulare Di Roma (A90)', 'A', 42.0021, 12.32557, 41.77002, 12.64829, -5, '1', 4, 'Orario', 'Antiorario'),
(2, 'Autostrada Aeroporto Di Fiumicino (A91)', 'A', 41.82466, 12.23596, 41.78244, 12.40453, 5, '0', 4, 'da Ovest a Est', 'da Est a Ovest'),
(3, 'Autostrada Azzurra (A12)', 'A', 42.16314, 11.78037, 41.80893, 12.31046, 5, '1', 4, 'da Sud a Nord', 'da Nord a Sud'),
(4, 'Diramazione Roma Nord (A1)', 'A', 42.16454, 12.51319, 41.98513, 12.61756, -5, '0', 4, 'da Sud a Nord', 'da Nord a Sud'),
(5, 'Diramazione Roma Sud (A1)', 'A', 41.85472, 12.59473, 41.80944, 12.80553, 5, '1', 4, 'da Ovest a Est', 'da Est a Ovest'),
(6, 'Autostrada del Sole (A1)', 'A', 42.27965, 12.52649, 41.73884, 13.03323, 5, '1', 4, 'da Sud a Nord', 'da Nord a Sud'),
(7, 'Strada dei Parchi (A24)', 'A', 41.99126, 12.53843, 42.03641, 12.96535, 5, '1', 4, 'da Ovest a Est', 'da Est a Ovest'),
(8, 'Via Appia Nuova (SS7)', 'C', 41.88846, 12.50375, 41.70982, 12.68777, -3, '0', 2, 'da Sud a Nord', 'da Nord a Sud'),
(9, 'Via Cassia (SS2)', 'C', 42.1583, 12.33758, 41.94314, 12.47457, 3, '1', 2, 'da Sud a Nord', 'da Nord a Sud'),
(10, 'Via Aurelia (SS1)', 'C', 42.05144, 11.81024, 41.85334, 12.43234, -3, '0', 2, 'da Ovest a Est', 'da Est a Ovest'),
(11, 'Via Flaminia (SS3)', 'C', 42.23978, 12.455, 41.94671, 12.50581, 3, '1', 2, 'da Sud a Nord', 'da Nord a Sud'),
(12, 'Via Salaria (SS4)', 'C', 42.19392, 12.47285, 41.91453, 12.79558, 3, '1', 2, 'da Sud a Nord', 'da Nord a Sud'),
(13, 'Diramazione Salaria (SS4DIR)', 'C', 42.12903, 12.59975, 42.15073, 12.6534, 3, '1', 2, 'da Ovest a Est', 'da Est a Ovest'),
(14, 'Via Tiburtina (SS5)', 'C', 41.90162, 12.51903, 42.03487, 12.94956, 3, '1', 2, 'da Ovest a Est', 'da Est a Ovest'),
(15, 'Via Pontina (SS148)', 'C', 41.82185, 12.42221, 41.61684, 12.58563, 3, '1', 2, 'da Sud a Nord', 'da Nord a Sud'),
(16, 'Via Anagnina (SP511)', 'C', 41.84014, 12.58718, 41.78973, 12.68365, 3, '1', 2, 'da Ovest a Est', 'da Est a Ovest'),
(17, 'Via Tuscolana (SP215)', 'C', 41.87637, 12.52615, 41.74382, 12.86055, 3, '1', 2, 'da Ovest a Est', 'da Est a Ovest'),
(18, 'Via Casilina (SS6)', 'C', 41.89817, 12.50569, 41.72622, 13.07149, 3, '1', 2, 'da Ovest a Est', 'da Est a Ovest'),
(20, 'Via Laurentina (SP95b)', 'C', 41.84909, 12.46667, 41.54866, 12.55182, 3, '1', 2, 'da Sud a Nord', 'da Nord a Sud'),
(21, 'Via Nomentana (SP22a)', 'C', 41.90993, 12.50203, 42.04775, 12.62614, 3, '1', 2, 'da Sud a Nord', 'da Nord a Sud');

