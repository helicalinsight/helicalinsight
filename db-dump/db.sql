SET PASSWORD FOR 'root'@'localhost' = PASSWORD('hiroot');
CREATE USER 'hiuser'@'localhost' IDENTIFIED BY 'hiuser';
GRANT ALL PRIVILEGES ON *.* TO 'hiuser'@'localhost' WITH GRANT OPTION;
CREATE DATABASE hice;
source SampleTravelData.sql
