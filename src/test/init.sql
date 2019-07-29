-- INSERT INTO "location" (address,create_time,delete_time,full_name,lat,level,lng,name,parent_id,polyline,safety_contact,safety_officer,type,update_time ) VALUES (NULL, NULL, NULL, 'B10', NULL, 1, NULL, 'B10', -1, E'E'''''::bytea, NULL, NULL, NULL, NULL);
-- INSERT INTO "location" (address,create_time,delete_time,full_name,lat,level,lng,name,parent_id,polyline,safety_contact,safety_officer,type,update_time ) VALUES (NULL, NULL, NULL, 'B10/1F', NULL, 2, NULL, '1F', 14, E'E'''''::bytea, NULL, NULL, NULL, NULL);
-- INSERT INTO "location" (address,create_time,delete_time,full_name,lat,level,lng,name,parent_id,polyline,safety_contact,safety_officer,type,update_time ) VALUES (NULL, NULL, NULL, 'B10/2F', NULL, 2, NULL, '2F', 14, E'E'''''::bytea, NULL, NULL, NULL, NULL);
-- INSERT INTO "location" (address,create_time,delete_time,full_name,lat,level,lng,name,parent_id,polyline,safety_contact,safety_officer,type,update_time ) VALUES (NULL, NULL, NULL, 'B10/3F', NULL, 2, NULL, '3F', 14, E'E'''''::bytea, NULL, NULL, NULL, NULL);
-- INSERT INTO "location" (address,create_time,delete_time,full_name,lat,level,lng,name,parent_id,polyline,safety_contact,safety_officer,type,update_time ) VALUES (NULL, NULL, NULL, 'B10/4F', NULL, 2, NULL, '4F', 14, E'E'''''::bytea, NULL, NULL, NULL, NULL);
-- INSERT INTO "location" (address,create_time,delete_time,full_name,lat,level,lng,name,parent_id,polyline,safety_contact,safety_officer,type,update_time ) VALUES (NULL, NULL, NULL, 'B10/5F', NULL, 2, NULL, '5F', 14, E'E'''''::bytea, NULL, NULL, NULL, NULL);

--business
INSERT INTO "public"."dev_business_device"( "brand_name", "date_of_commissioning", "date_of_installation", "date_of_poduction", "description", "device_label", "device_no", "device_status", "importance", "life_time", "location_detail", "location_id", "map_location", "type_specification", "weight", "device_type_id", "system_type_id") VALUES ( NULL, NULL, NULL, NULL, NULL, 'B10栋2F轴  C-D-1-2 烟感', '1-2-1-5', NULL, NULL, NULL, NULL, 8, '{"s3":[81.252,45.084,81.252],"r3":[0,0,0],"p3":[-4961.6661,280,412.2893]}', NULL, NULL, 21, 1);

--location

INSERT INTO "public"."location"(  "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, NULL, NULL, 'B10', NULL, 1, NULL, 'B10', -1, E'E'''''::bytea, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"(  "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, NULL, NULL, 'B11', 0, 1, NULL, 'B11', -1, E'E'''''::bytea, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"(  "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, NULL, NULL, 'B12', 0, 1, NULL, 'B12', -1, E'E'''''::bytea, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"(  "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, NULL, NULL, 'B13', NULL, 1, NULL, 'B13', -1, E'E'''''::bytea, NULL, NULL, NULL, NULL);

INSERT INTO "public"."location"(  "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES (NULL, NULL, NULL, 'B10/TM', NULL, 2, NULL, 'TM', 1, E'E'''''::bytea, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"(  "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, NULL, NULL, 'B10/1.5F', NULL, 2, NULL, '1.5F', 1, E'E'''''::bytea, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"(  "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, NULL, NULL, 'B10/1F', NULL, 2, NULL, '1F', 1, E'E'''''::bytea, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"(  "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, NULL, NULL, 'B10/2F', NULL, 2, NULL, '2F', 1, E'E'''''::bytea, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"(  "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, NULL, NULL, 'B10/3F', NULL, 2, NULL, '3F', 1, E'E'''''::bytea, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"(  "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, NULL, NULL, 'B10/4F', NULL, 2, NULL, '4F', 1, E'E'''''::bytea, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"(  "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, NULL, NULL, 'B10/5F', NULL, 2, NULL, '5F', 1, E'E'''''::bytea, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"(  "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.126', NULL, 'B10/1F-1.5F', NULL, 2, NULL, '1F-1.5F', 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"(  "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES (NULL, '2019-01-09 03:45:45.141', NULL, 'B10/2F-3F ', NULL, 2, NULL, '2F-3F ', 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"(  "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.151', NULL, 'B10/4F-5F', NULL, 2, NULL, '4F-5F', 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"(  "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, NULL, NULL, 'B10/CF', NULL, 2, NULL, 'CF', 1, E'E'''''::bytea, NULL, NULL, NULL, NULL);

INSERT INTO "public"."location"(  "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, NULL, NULL, 'B11/1.5F', NULL, 2, NULL, '1.5F', 2, E'E'''''::bytea, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"(  "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, NULL, NULL, 'B11/1F', NULL, 2, NULL, '1F', 2, E'E'''''::bytea, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, NULL, NULL, 'B11/2F', NULL, 2, NULL, '2F', 2, E'E'''''::bytea, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, NULL, NULL, 'B11/3F', NULL, 2, NULL, '3F', 2, E'E'''''::bytea, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, NULL, NULL, 'B11/4F', NULL, 2, NULL, '4F', 2, E'E'''''::bytea, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES (NULL, NULL, NULL, 'B11/5F', NULL, 2, NULL, '5F', 2, E'E'''''::bytea, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.168', NULL, 'B11/1F-1.5F', NULL, 2, NULL, '1F-1.5F', 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.178', NULL, 'B11/2F-3F ', NULL, 2, NULL, '2F-3F ', 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.186', NULL, 'B11/4F-5F', NULL, 2, NULL, '4F-5F', 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( '1F髙空区', '2019-01-07 15:38:39.278', NULL, 'B11/1F髙空区', NULL, 2, NULL, '1F髙空区', 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.196', NULL, 'B11/CF', NULL, 2, NULL, 'CF', 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.202', NULL, 'B11/TM', NULL, 2, NULL, 'TM', 2, NULL, NULL, NULL, NULL, NULL);

INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( '5-4F', '2019-01-07 15:38:39.326', NULL, 'B12/5-4F', NULL, 2, NULL, '5-4F', 3, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( '3-2F', '2019-01-07 15:38:39.341', NULL, 'B12/3-2F', NULL, 2, NULL, '3-2F', 3, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( '1.5-1F', '2019-01-07 15:38:39.361', NULL, 'B12/1.5-1F', NULL, 2, NULL, '1.5-1F', 3, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.209', NULL, 'B12/1.5F', NULL, 2, NULL, '1.5F', 3, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.215', NULL, 'B12/1F-1.5F', NULL, 2, NULL, '1F-1.5F', 3, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.241', NULL, 'B12/3F', NULL, 2, NULL, '3F', 3, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.247', NULL, 'B12/4F-5F', NULL, 2, NULL, '4F-5F', 3, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.253', NULL, 'B12/4F', NULL, 2, NULL, '4F', 3, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.259', NULL, 'B12/5F', NULL, 2, NULL, '5F', 3, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.265', NULL, 'B12/CF', NULL, 2, NULL, 'CF', 3, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.27', NULL, 'B12/TM', NULL, 2, NULL, 'TM', 3, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.221', NULL, 'B12/1F', NULL, 2, NULL, '1F', 3, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.227', NULL, 'B12/2F', NULL, 2, NULL, '2F', 3, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.235', NULL, 'B12/2F-3F ', NULL, 2, NULL, '2F-3F ', 3, NULL, NULL, NULL, NULL, NULL);

INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES (NULL, '2019-01-09 03:45:45.283', NULL, 'B13/1F-1.5F', NULL, 2, NULL, '1F-1.5F', 4, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.288', NULL, 'B13/1F', NULL, 2, NULL, '1F', 4, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.294', NULL, 'B13/2F', NULL, 2, NULL, '2F', 4, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES (NULL, '2019-01-09 03:45:45.3', NULL, 'B13/2F-3F ', NULL, 2, NULL, '2F-3F ', 4, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.306', NULL, 'B13/3F', NULL, 2, NULL, '3F', 4, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.313', NULL, 'B13/4F-5F', NULL, 2, NULL, '4F-5F', 4, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.32', NULL, 'B13/4F', NULL, 2, NULL, '4F', 4, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.325', NULL, 'B13/5F', NULL, 2, NULL, '5F', 4, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.332', NULL, 'B13/CF', NULL, 2, NULL, 'CF', 4, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.337', NULL, 'B13/TM', NULL, 2, NULL, 'TM', 4, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( NULL, '2019-01-09 03:45:45.277', NULL, 'B13/1.5F', NULL, 2, NULL, '1.5F', 4, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( '1-2F', '2019-01-07 15:38:39.39', NULL, 'B13/1-2F', NULL, 2, NULL, '1-2F', 4, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."location"( "address", "create_time", "delete_time", "full_name", "lat", "level", "lng", "name", "parent_id", "polyline", "safety_contact", "safety_officer", "type", "update_time") VALUES ( '3-5F', '2019-01-07 15:38:39.402', NULL, 'B13/3-5F', NULL, 2, NULL, '3-5F', 4, NULL, NULL, NULL, NULL, NULL);

