-- CREARE DATABASE SPAZIALE
-- createdb gim -U postgres
-- createlang -U postgres plpgsql gim
-- psql -U postgres -d gim -f /usr/share/postgresql/8.4/contrib/postgis-1.5/postgis.sql
-- psql -U postgres -d gim -f /usr/share/postgresql/8.4/contrib/postgis-1.5/spatial_ref_sys.sql

CREATE TABLE strt_new
(
  idno numeric,
  tail numeric,
  head numeric,
  dirx numeric,
  leng double precision,
  "name" character varying,
  levh numeric,
  sped numeric,
  poly geometry,
  gid serial NOT NULL,
  CONSTRAINT pk_strt_new PRIMARY KEY (gid),
  CONSTRAINT strt_new_idno_key UNIQUE (idno)
)
WITH (
  OIDS=FALSE
);

CREATE INDEX poly_idx ON strt_new USING GIST ( poly );

CREATE TABLE link
(
  gid serial NOT NULL,
  idno numeric,
  the_geom geometry,
  CONSTRAINT link_pkey PRIMARY KEY (gid)
)
WITH (
  OIDS=FALSE
);

CREATE INDEX the_geom ON link USING GIST ( shap );

CREATE INDEX roma_drid_idx ON "PROV_ROMA_GRID" USING GIST ( the_geom );

-- AGGIUNGER VINCOLO UNIVOCITA' PER STRT_NEW(IDNO) 

CREATE TABLE "configuration"
(
  "key" character varying,
  "value" character varying
)
WITH (
  OIDS=FALSE
);

insert into configuration values('IN_LAST_ID_PROCESSED', '-1');

CREATE TABLE t_strade
(
  id serial NOT NULL,
  descrizione character varying,
  CONSTRAINT t_strade_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE t_strade_dettaglio
(
  idno numeric,
  id serial NOT NULL,
  id_strada integer,
  CONSTRAINT t_strade_dettaglio_pkey PRIMARY KEY (id),
  CONSTRAINT t_strade_dettaglio_id_strada_fkey FOREIGN KEY (id_strada)
      REFERENCES t_strade (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
