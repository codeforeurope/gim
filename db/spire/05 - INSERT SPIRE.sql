----------------------------------------------------
-- Insert ANAGRAFICA Spire FAMAS
----------------------------------------------------

-- SPIRA 226 - Ex SS 8 bis - Ostiense
INSERT INTO gim_milano.Sezioni (TagSezione, DescrSezione, Strada, Km, X, Y, Comune, Direzione, NumeroCorsie) VALUES ('226', 'SPIRA FAMAS', 'Ex SS 8 bis - Ostiense', 10.620, 41.8172, 12.4331, 'ROMA', 'ASCENDENTE/DISCENDENTE', 2);
INSERT INTO gim_milano.CorsieSezioni (IdSezione, IdCorsia, IdArco) VALUES ((SELECT IdSezione from gim_milano.Sezioni WHERE TagSezione = '226'), 0, 123);
INSERT INTO gim_milano.CorsieSezioni (IdSezione, IdCorsia, IdArco) VALUES ((SELECT IdSezione from gim_milano.Sezioni WHERE TagSezione = '226'), 1, 321);

-- SPIRA 227 - Ex SS 493 - Braccianese
INSERT INTO gim_milano.Sezioni (TagSezione, DescrSezione, Strada, Km, X, Y, Comune, Direzione, NumeroCorsie) VALUES ('227', 'SPIRA FAMAS', 'Ex SS 493 - Braccianese', 1.0, 42.0258, 12.3433, 'ROMA', 'ASCENDENTE/DISCENDENTE', 2);
INSERT INTO gim_milano.CorsieSezioni (IdSezione, IdCorsia, IdArco) VALUES ((SELECT IdSezione from gim_milano.Sezioni WHERE TagSezione = '227'), 0, 123);
INSERT INTO gim_milano.CorsieSezioni (IdSezione, IdCorsia, IdArco) VALUES ((SELECT IdSezione from gim_milano.Sezioni WHERE TagSezione = '227'), 1, 321); 

-- SPIRA 228 - 12/a - Traversa del Grillo
INSERT INTO gim_milano.Sezioni (TagSezione, DescrSezione, Strada, Km, X, Y, Comune, Direzione, NumeroCorsie) VALUES ('228', 'SPIRA FAMAS', '12/a - Traversa del Grillo', 0.85, 42.0942, 12.5876, 'ROMA', 'ASCENDENTE/DISCENDENTE', 2);
INSERT INTO gim_milano.CorsieSezioni (IdSezione, IdCorsia, IdArco) VALUES ((SELECT IdSezione from gim_milano.Sezioni WHERE TagSezione = '228'), 0, 123);
INSERT INTO gim_milano.CorsieSezioni (IdSezione, IdCorsia, IdArco) VALUES ((SELECT IdSezione from gim_milano.Sezioni WHERE TagSezione = '228'), 1, 321);

-- SPIRA 229 - 51 ab - Bis maremmana Inferiore
INSERT INTO gim_milano.Sezioni (TagSezione, DescrSezione, Strada, Km, X, Y, Comune, Direzione, NumeroCorsie) VALUES ('229', 'SPIRA FAMAS', '51 ab - Bis maremmana Inferiore', 2.48, 41.9356, 12.7547, 'ROMA', 'ASCENDENTE/DISCENDENTE', 2);
INSERT INTO gim_milano.CorsieSezioni (IdSezione, IdCorsia, IdArco) VALUES ((SELECT IdSezione from gim_milano.Sezioni WHERE TagSezione = '229'), 0, 123);
INSERT INTO gim_milano.CorsieSezioni (IdSezione, IdCorsia, IdArco) VALUES ((SELECT IdSezione from gim_milano.Sezioni WHERE TagSezione = '229'), 1, 321);

-- SPIRA 233 - 93/b - Cancelliera
INSERT INTO gim_milano.Sezioni (TagSezione, DescrSezione, Strada, Km, X, Y, Comune, Direzione, NumeroCorsie) VALUES ('233', 'SPIRA FAMAS', '93/b - Cancelliera', 4.6, 41.688747, 12.593590, 'ROMA', 'ASCENDENTE/DISCENDENTE', 2);
INSERT INTO gim_milano.CorsieSezioni (IdSezione, IdCorsia, IdArco) VALUES ((SELECT IdSezione from gim_milano.Sezioni WHERE TagSezione = '233'), 0, 123);
INSERT INTO gim_milano.CorsieSezioni (IdSezione, IdCorsia, IdArco) VALUES ((SELECT IdSezione from gim_milano.Sezioni WHERE TagSezione = '233'), 1, 321);