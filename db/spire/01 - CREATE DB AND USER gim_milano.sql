--------------------------------------------------------
-- DDL for Create Database gim_milano 
--------------------------------------------------------
CREATE DATABASE gim_milano;

--------------------------------------------------------
-- DDL for Create User gim_milano
--------------------------------------------------------
-- Create User
CREATE USER 'gim_milano' IDENTIFIED BY 'gim_milano';

-- Set the host ('%' means that this user can connect to the db from every ip)
UPDATE mysql.user SET HOST='%' WHERE USER='gim_milano';

-- Set the privileges to the user
GRANT ALL PRIVILEGES ON gim_milano.* TO 'gim_milano'@'%' IDENTIFIED BY 'gim_milano' WITH GRANT OPTION;

-- Set the password to the user
UPDATE mysql.user SET PASSWORD=PASSWORD('gim_milano') WHERE USER='gim_milano' AND HOST='%';

-- The flushing of the privileges
FLUSH PRIVILEGES;