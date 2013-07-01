--------------------------------------------------------
--  DDL for Table CorsieSezioni
--------------------------------------------------------
CREATE TABLE `gim_milano`.`CorsieSezioni` 
(
	`IdSezione` int NOT NULL,
	`IdCorsia` int NOT NULL,
	`IdArco` int NOT NULL,
	PRIMARY KEY (`IdSezione`,`IdCorsia`),
	FOREIGN KEY `FK_CorsieSezioni_Sezioni` (`IdSezione`) REFERENCES `gim_milano`.`Sezioni` (`IdSezione`)
)
ENGINE=InnoDB;