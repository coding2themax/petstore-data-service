--
--    Copyright 2010-2023 the original author or authors.
--
--    Licensed under the Apache License, Version 2.0 (the "License");
--    you may not use this file except in compliance with the License.
--    You may obtain a copy of the License at
--
--       https://www.apache.org/licenses/LICENSE-2.0
--
--    Unless required by applicable law or agreed to in writing, software
--    distributed under the License is distributed on an "AS IS" BASIS,
--    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--    See the License for the specific language governing permissions and
--    limitations under the License.
--


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
    tagname varchar(80) not null
);

create TYPE  petstore.STATUS AS ENUM ('available', 'pending', 'sold');
create table petstore.PET (
    petid numeric(10,0) NOT NULL PRIMARY KEY, 
    category numeric(10,0) NOT NULL,
    name character varying(80) COLLATE pg_catalog."default",
    descn character varying(255) COLLATE pg_catalog."default",
    petstatus petstore.status NOT NULL
);
CREATE TABLE petstore.PetTags (
    petid NUMERIC(10) not null,
    tagid NUMERIC(80),
    date_assigned DATE,
    PRIMARY KEY (petid, tagid),
    FOREIGN KEY (petid) REFERENCES petstore.Pet(petid),
    FOREIGN KEY (tagid) REFERENCES petstore.Tag(tagid)
);

