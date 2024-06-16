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
  