create database connection;
use connection;
create table connections (id integer auto_increment primary key,
                            id_connection integer, mac varchar(17),
                                hash varchar(32), salt varchar(32));
select * from connections;
insert into connections (id,id_connection,mac,hash,salt) value (1,null,'2C-56-DC-9F-FA-B3','1A1DC91C907325C69271DDF0C944BC72',null);
select * from connections;
SET GLOBAL time_zone = '+5:00';