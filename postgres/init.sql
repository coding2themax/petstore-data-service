CREATE SCHEMA IF NOT EXISTS petstore;

CREATE TABLE IF NOT EXISTS petstore.category
(
    catid numeric(10,0) NOT NULL PRIMARY KEY,
    name character varying(80) COLLATE pg_catalog."default",
    descn character varying(255) COLLATE pg_catalog."default"
);

   
CREATE TABLE IF NOT EXISTS petstore.photo
(
    photoid numeric(80,0) NOT NULL PRIMARY KEY,
    petid numeric(10,0) NOT NULL,
    filename character varying(80) COLLATE pg_catalog."default" NOT NULL,
    filetype character varying(80) COLLATE pg_catalog."default" NOT NULL,
    filesize integer NOT NULL,
    fileextension character varying(80) COLLATE pg_catalog."default" NOT NULL,
    uploaddate date NOT NULL,
    uploadpath character varying(80) COLLATE pg_catalog."default" NOT NULL,
    photourl character varying(80) COLLATE pg_catalog."default" NOT NULL
);


create table petstore.TAG (
    tagid NUMERIC(80) not null primary key,
    tagname varchar(80) not null,
    petid NUMERIC(10) not null
);

create TYPE petstore.STATUS AS ENUM ('available', 'pending', 'sold');
create table petstore.PET (
    petid numeric(10,0) NOT NULL PRIMARY KEY, 
    category numeric(10,0) NOT NULL,
    name character varying(80) COLLATE pg_catalog."default",
    descn character varying(255) COLLATE pg_catalog."default",
    petstatus petstore.status NOT NULL
);

DELETE FROM petstore.CATEGORY;
DELETE FROM petstore.PET;
DELETE FROM petstore.TAG;
DELETE FROM petstore.PHOTO;


INSERT INTO petstore.CATEGORY VALUES (1,'Fish','<image src="../images/fish_icon.gif"><font size="5" color="blue"> Fish</font>');
INSERT INTO petstore.CATEGORY VALUES (2,'Dogs','<image src="../images/dogs_icon.gif"><font size="5" color="blue"> Dogs</font>');
INSERT INTO petstore.CATEGORY VALUES (3,'Reptiles','<image src="../images/reptiles_icon.gif"><font size="5" color="blue"> Reptiles</font>');
INSERT INTO petstore.CATEGORY VALUES (4,'Cats','<image src="../images/cats_icon.gif"><font size="5" color="blue"> Cats</font>');
INSERT INTO petstore.CATEGORY VALUES (5,'Birds','<image src="../images/birds_icon.gif"><font size="5" color="blue"> Birds</font>');

INSERT INTO petstore.PHOTO  VALUES (1,1,'fish1.gif','image/gif',1000,'gif','2005-02-16','/images/fish1.gif','http://www.jpetstore.com/images/fish1.gif');
INSERT INTO petstore.PHOTO  VALUES (2,1,'fish2.gif','image/gif',1000,'gif','2005-02-16','/images/fish2.gif','http://www.jpetstore.com/images/fish2.gif');
INSERT INTO petstore.PHOTO  VALUES (3,2,'fish3.gif','image/gif',1000,'gif','2005-02-16','/images/fish3.gif','http://www.jpetstore.com/images/fish3.gif');

INSERT INTO petstore.PET VALUES (1,1,'Angelfish','<image src="../images/fish1.gif">Salt Water fish from Australia', 'available');
insert into petstore.PET VALUES (2,1,'Tiger Shark','<image src="../images/fish4.gif">Salt Water fish from Australia','available');    

INSERT Into petstore.TAG VALUES (1,'tag1',1);
INSERT Into petstore.TAG VALUES (2,'tag2',1);
INSERT INTO petstore.TAG VALUES (3,'tag3',2);