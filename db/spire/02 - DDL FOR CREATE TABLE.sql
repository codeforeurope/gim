--------------------------------------------------------
--  DDL for Table Sezioni
--------------------------------------------------------
CREATE TABLE `gim_milano`.`Sezioni` 
(
	`IdSezione` int NOT NULL AUTO_INCREMENT,
	`TagSezione` varchar(32) DEFAULT NULL,
	`DescrSezione` varchar(256) DEFAULT NULL,
	`Strada` varchar(256) DEFAULT NULL,
	`Km` double DEFAULT NULL,
	`X` double DEFAULT NULL,
	`Y` double DEFAULT NULL,
	`Comune` varchar(50) DEFAULT NULL,
	`Direzione` varchar(50) DEFAULT NULL,
	`NumeroCorsie` int DEFAULT NULL,
	PRIMARY KEY (`IdSezione`)
)
ENGINE=InnoDB;

--------------------------------------------------------
--  DDL for Table DatiAggregati
--------------------------------------------------------
CREATE TABLE `gim_milano`.`DatiAggregati` 
(
	`IdAggregato` bigint NOT NULL,
	`IdSezione` int NOT NULL,
	`DataOra` datetime NOT NULL,
	`Periodicita` int DEFAULT NULL,
	`IdCorsia` int NOT NULL,
	`IdAggrUnivoco` bigint NOT NULL AUTO_INCREMENT,
	`NumeroVeicoli` int DEFAULT NULL,
	`VelocitaMedia` double DEFAULT NULL,
	`VelocitaMassima` double DEFAULT NULL,
	`VelocitaMinima` double DEFAULT NULL,
	`LunghezzaMedia` double DEFAULT NULL,
	`HeadwayMedio` double DEFAULT NULL,
	`GapMedio` double DEFAULT NULL,
	`Occupazione` double DEFAULT NULL,
	`Diagnostica` int DEFAULT NULL,
	PRIMARY KEY (`IdAggregato`, `IdSezione`, `DataOra`, `IdCorsia`),
	UNIQUE INDEX `Idx_IdAggregatoUnivoco` (`IdAggrUnivoco`),
	FOREIGN KEY `FK_DatiAggregati_Sezioni` (`IdSezione`) REFERENCES `gim_milano`.`Sezioni` (`IdSezione`)
)
ENGINE=InnoDB;

--------------------------------------------------------
--  DDL for Table ClassiLun
--------------------------------------------------------
CREATE TABLE `gim_milano`.`ClassiLun` 
(
	`IdClasseLun` int NOT NULL AUTO_INCREMENT,
	`TagClasseLun` varchar(32) DEFAULT NULL,
	`DaCm` int DEFAULT NULL,
	`ACm` int DEFAULT NULL,
	`DescrClasseLun` varchar(256) DEFAULT NULL,
	PRIMARY KEY (`IdClasseLun`)
)
ENGINE=InnoDB;

--------------------------------------------------------
--  DDL for Table DatiClassiLun
--------------------------------------------------------
CREATE TABLE `gim_milano`.`DatiClassiLun` 
(
	`IdAggrUnivoco` bigint NOT NULL,
	`IdClasseLun` int NOT NULL,
	`NumeroVeicoli` int DEFAULT NULL,
	`VelocitaMedia` double DEFAULT NULL,
	`VelocitaMassima` double DEFAULT NULL,
	`VelocitaMinima` double DEFAULT NULL,
	PRIMARY KEY (`IdAggrUnivoco`, `IdClasseLun`),
	INDEX `Idx_IdAggregato` (`IdAggrUnivoco`),
	INDEX `Idx_IdClasseLun` (`IdClasseLun`),
	FOREIGN KEY `FK_DatiClassiLun_DatiAggregati_1` (`IdAggrUnivoco`) REFERENCES `gim_milano`.`DatiAggregati` (`IdAggrUnivoco`),
	FOREIGN KEY `FK_DatiClassiLun_ClassiLun` (`IdClasseLun`) REFERENCES `gim_milano`.`ClassiLun` (`IdClasseLun`)
)
ENGINE=InnoDB;