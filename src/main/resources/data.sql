-- Tipos de usuarios
INSERT INTO authorities(id,authority) VALUES (1,'ADMIN');
INSERT INTO authorities(id,authority) VALUES (2,'PLAYER');

-- Usuarios
INSERT INTO appusers(id,username,first_name,last_name,age,email,password,avatar,authority) VALUES (1,'davguifer','David','Guillen',20,'davguifer@alum.us.es','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS','https://hips.hearstapps.com/hmg-prod/images/hadi-choopan-1671440684.jpg?crop=0.888421052631579xw:1xh;center,top&resize=1200:*',1);
INSERT INTO appusers(id,username,first_name,last_name,age,email,password,avatar,authority) VALUES (2,'SrmisterL','Luis','Giraldo',21,'luigirsan1@alum.us.es','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS','https://i.pinimg.com/originals/66/e6/23/66e6230aa7ce7107f9707493dee0d9ba.png',1);
INSERT INTO appusers(id,username,first_name,last_name,age,email,password,avatar,authority) VALUES (3,'davvarmun','David','Vargas',20,'davvarmu@alum.us.es','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS','https://images.genius.com/088e0da5bb6920103a204366e5da0041.327x327x1.jpg',1);
INSERT INTO appusers(id,username,first_name,last_name,age,email,password,avatar,authority) VALUES (4,'rafmolgar2','Rafael','Molina',20,'rafmolgar2@alum.us.es<','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS','https://d1nxzqpcg2bym0.cloudfront.net/itunes_connect/476702540/2e724f6c-3b4c-11e8-88a2-2902ebea4f39/128x128',1);
INSERT INTO appusers(id,username,first_name,last_name,age,email,password,avatar,authority) VALUES (5,'alevarmun1','Alejandro','Vargas',20,'alevarmun1@alum.us.es','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS','https://th.bing.com/th/id/R.04f0164bc76b87492c7bee235c8135fa?rik=NG1jZrzitQVC1g&pid=ImgRaw&r=0',1);
INSERT INTO appusers(id,username,first_name,last_name,age,email,password,avatar,authority) VALUES (6,'cargarort3','Carlos','Garcia',20,'cargarort3@alum.us.es','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS','https://pokemon.gishan.cc/static/i/pokemon/shiny-cyndaquil.png',1);
INSERT INTO appusers(id,username,first_name,last_name,age,email,password,avatar,authority) VALUES (7,'Mario','Pizza','4quesos',80,'fulano@gmail.com','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS','https://cdn-3.expansion.mx/dims4/default/7c61fda/2147483647/strip/true/crop/931x652+0+0/resize/1200x840!/format/webp/quality/60/?url=https%3A%2F%2Fcherry-brightspot.s3.amazonaws.com%2Ffb%2Fcf%2F6e3db6b04a49a5adc855a4cf461a%2Fmario.JPG',2);
INSERT INTO stats(id, games_played, victories, time_played, user_id) VALUES (1, 9, 5, 10., 7);
INSERT INTO appusers(id,username,first_name,last_name,age,email,password,avatar,authority) VALUES (8,'Luigi','Pizza','Prosciuto',79,'fulanito@gmail.com','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS','https://i.pinimg.com/originals/e4/e7/dc/e4e7dc4656dad2ecfdf7057403f6316f.jpg',2);
INSERT INTO stats(id, games_played, victories, time_played, user_id) VALUES (2, 10, 8, 10., 8);
INSERT INTO appusers(id,username,first_name,last_name,age,email,password,avatar,authority) VALUES (9,'Wario','Pizza','4estaciones',78,'menganito@gmail.com','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS','https://i.pinimg.com/564x/2c/a6/38/2ca638b0a1a09405b66a3932382b93bb.jpg',2);
INSERT INTO stats(id, games_played, victories, time_played, user_id) VALUES (3, 10, 0, 10., 9);
INSERT INTO appusers(id,username,first_name,last_name,age,email,password,avatar,authority) VALUES (10,'Waluigi','Pizza','Caprichosa',81,'mengano@gmail.com','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS','https://pm1.aminoapps.com/6291/355b658f567988df48370cffdbbfac63b3f59ff5_00.jpg',2);
INSERT INTO stats(id, games_played, victories, time_played, user_id) VALUES (4, 3, 0, 3., 10);
INSERT INTO appusers(id,username,first_name,last_name,age,email,password,avatar,authority) VALUES (11,'BabyDaisy','Pizza','Margarita  ',21,'daisyWorld@gmail.com','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS','https://i.pinimg.com/originals/a3/2e/fa/a32efa9cfdea1fce949f91100f52deab.jpg',2);
INSERT INTO stats(id, games_played, victories, time_played, user_id) VALUES (5, 2, 1, 2., 11);

-- Partidas
INSERT INTO games(id,name,code,create,start,finish, round) VALUES (1,'Los reales','SiSomos','2023-11-04T18:56:04.770Z','2023-11-04T18:56:04.770Z','2023-11-04T18:56:04.770Z', 3);
INSERT INTO games(id,name,code,create,start,finish, round) VALUES (2,'Fiesta loca','','2023-11-04T18:56:04.770Z',null,null, 1);
INSERT INTO games(id,name,code,create,start,finish, round) VALUES (3,'El valle encantado','','2023-11-04T18:56:04.770Z',null,null, 1);

-- Jugadores de una partida
INSERT INTO games_appusers(game_id, user_id) VALUES (1,1);
INSERT INTO games_appusers(game_id, user_id) VALUES (1,2);
INSERT INTO games_appusers(game_id, user_id) VALUES (1,3);
INSERT INTO games_appusers(game_id, user_id) VALUES (1,4);
INSERT INTO games_appusers(game_id, user_id) VALUES (2,5);
INSERT INTO games_appusers(game_id, user_id) VALUES (3,7);
INSERT INTO games_appusers(game_id, user_id) VALUES (3,8);
INSERT INTO games_appusers(game_id, user_id) VALUES (3,11);
INSERT INTO games_appusers(game_id, user_id) VALUES (3,10);

-- -- Cartas Ingredientes(base)
INSERT INTO ingredients(id,valuable,type) VALUES (1,1,0);
INSERT INTO ingredients(id,valuable,type) VALUES (2,1,0);
INSERT INTO ingredients(id,valuable,type) VALUES (3,1,0);
INSERT INTO ingredients(id,valuable,type) VALUES (4,1,0);
INSERT INTO ingredients(id,valuable,type) VALUES (5,1,0);
INSERT INTO ingredients(id,valuable,type) VALUES (6,1,0);
INSERT INTO ingredients(id,valuable,type) VALUES (7,2,0);
INSERT INTO ingredients(id,valuable,type) VALUES (8,2,0);
INSERT INTO ingredients(id,valuable,type) VALUES (9,2,0);
INSERT INTO ingredients(id,valuable,type) VALUES (10,2,0);
INSERT INTO ingredients(id,valuable,type) VALUES (11,2,0);
INSERT INTO ingredients(id,valuable,type) VALUES (12,2,0);
INSERT INTO ingredients(id,valuable,type) VALUES (13,3,0);
INSERT INTO ingredients(id,valuable,type) VALUES (14,3,0);
INSERT INTO ingredients(id,valuable,type) VALUES (15,3,0);
INSERT INTO ingredients(id,valuable,type) VALUES (16,3,0);
INSERT INTO ingredients(id,valuable,type) VALUES (17,3,0);
INSERT INTO ingredients(id,valuable,type) VALUES (18,3,0);
INSERT INTO ingredients(id,valuable,type) VALUES (19,4,0);
INSERT INTO ingredients(id,valuable,type) VALUES (20,4,0);
INSERT INTO ingredients(id,valuable,type) VALUES (21,4,0);
INSERT INTO ingredients(id,valuable,type) VALUES (22,4,0);
INSERT INTO ingredients(id,valuable,type) VALUES (23,4,0);
INSERT INTO ingredients(id,valuable,type) VALUES (24,4,0);
INSERT INTO ingredients(id,valuable,type) VALUES (25,5,0);
INSERT INTO ingredients(id,valuable,type) VALUES (26,5,0);
INSERT INTO ingredients(id,valuable,type) VALUES (27,5,0);
INSERT INTO ingredients(id,valuable,type) VALUES (28,5,0);
INSERT INTO ingredients(id,valuable,type) VALUES (29,5,0);
INSERT INTO ingredients(id,valuable,type) VALUES (30,5,0);
INSERT INTO ingredients(id,valuable,type) VALUES (31,6,0);
INSERT INTO ingredients(id,valuable,type) VALUES (32,6,0);
INSERT INTO ingredients(id,valuable,type) VALUES (33,6,0);
INSERT INTO ingredients(id,valuable,type) VALUES (34,6,0);
INSERT INTO ingredients(id,valuable,type) VALUES (35,6,0);
INSERT INTO ingredients(id,valuable,type) VALUES (36,6,0);
INSERT INTO ingredients(id,valuable,type) VALUES (37,7,0);
INSERT INTO ingredients(id,valuable,type) VALUES (38,7,0);
INSERT INTO ingredients(id,valuable,type) VALUES (39,7,0);
INSERT INTO ingredients(id,valuable,type) VALUES (40,7,0);
INSERT INTO ingredients(id,valuable,type) VALUES (41,7,0);
INSERT INTO ingredients(id,valuable,type) VALUES (42,7,0);
INSERT INTO ingredients(id,valuable,type) VALUES (43,8,0);
INSERT INTO ingredients(id,valuable,type) VALUES (44,8,0);
INSERT INTO ingredients(id,valuable,type) VALUES (45,8,0);
INSERT INTO ingredients(id,valuable,type) VALUES (46,8,0);
INSERT INTO ingredients(id,valuable,type) VALUES (47,8,0);
INSERT INTO ingredients(id,valuable,type) VALUES (48,8,0);
INSERT INTO ingredients(id,valuable,type) VALUES (49,9,0);
INSERT INTO ingredients(id,valuable,type) VALUES (50,9,0);
INSERT INTO ingredients(id,valuable,type) VALUES (51,9,0);
INSERT INTO ingredients(id,valuable,type) VALUES (52,9,0);
INSERT INTO ingredients(id,valuable,type) VALUES (53,9,0);
INSERT INTO ingredients(id,valuable,type) VALUES (54,9,0);
INSERT INTO ingredients(id,valuable,type) VALUES (55,10,0);
INSERT INTO ingredients(id,valuable,type) VALUES (56,10,0);
INSERT INTO ingredients(id,valuable,type) VALUES (57,10,0);
INSERT INTO ingredients(id,valuable,type) VALUES (58,10,0);
INSERT INTO ingredients(id,valuable,type) VALUES (59,10,0);
INSERT INTO ingredients(id,valuable,type) VALUES (60,10,0);

-- Cartas Ingredientes(artefacto)
INSERT INTO ingredients(id,valuable,type) VALUES (61,2,1);
INSERT INTO ingredients(id,valuable,type) VALUES (62,2,1);
INSERT INTO ingredients(id,valuable,type) VALUES (63,2,1);
INSERT INTO ingredients(id,valuable,type) VALUES (64,2,1);
INSERT INTO ingredients(id,valuable,type) VALUES (65,2,1);
INSERT INTO ingredients(id,valuable,type) VALUES (66,3,1);
INSERT INTO ingredients(id,valuable,type) VALUES (67,3,1);
INSERT INTO ingredients(id,valuable,type) VALUES (68,3,1);
INSERT INTO ingredients(id,valuable,type) VALUES (69,4,1);
INSERT INTO ingredients(id,valuable,type) VALUES (70,4,1);
INSERT INTO ingredients(id,valuable,type) VALUES (71,4,1);
INSERT INTO ingredients(id,valuable,type) VALUES (72,5,1);

-- Cartas Conjuros
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (1,1,5,null,0);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (2,2,4,null,0);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (3,3,3,null,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (4,4,2,null,0);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (5,5,1,null,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (6,6,0,null,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (7,7,0,null,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (8,8,1,null,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (9,9,2,null,0);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (10,10,3,null,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (11,11,4,null,0);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (12,12,5,null,0);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (13,13,0,null,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (14,14,4,null,0);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (15,15,3,0,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (16,16,2,null,0);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (17,17,1,null,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (18,18,0,null,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (19,21,1,0,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (20,24,1,1,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (21,27,2,null,0);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (22,30,3,0,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (23,33,4,null,0);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (24,36,5,null,0);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (25,40,1,0,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (26,44,1,1,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (27,47,2,null,0);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (28,50,3,0,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (29,51,3,1,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (30,52,5,0,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (31,55,1,0,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (32,59,1,1,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (33,63,2,2,0);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (34,68,3,1,1);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (35,77,2,null,0);
INSERT INTO spells(id,valuable,effect1,effect2,target) VALUES (36,80,5,null,0);

-- Logros
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (1,'Aprendiz','Juega un total de 10 partidas',10.0,'https://cdn-icons-png.flaticon.com/512/5243/5243423.png','GAMES_PLAYED');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (2,'Experimentado','Juega un total de 20 partidas',20.0,'https://cdn-icons-png.flaticon.com/512/5243/5243423.png','GAMES_PLAYED');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (3,'Maestro','Juega un total de 30 partidas',30.0,'https://cdn-icons-png.flaticon.com/512/5243/5243423.png','GAMES_PLAYED');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (4,'Arcano','Juega un total de 40 partidas',40.0,'https://cdn-icons-png.flaticon.com/512/5243/5243423.png','GAMES_PLAYED');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (5,'Dios','Juega un total de 50 partidas',50.0,'https://cdn-icons-png.flaticon.com/512/5243/5243423.png','GAMES_PLAYED');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (6,'Triunfo inicial','Gana tu primera partida',1.0,'https://cdn-icons-png.flaticon.com/512/2617/2617955.png','VICTORIES');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (7,'Luchador','Gana 10 partidas',10.0,'https://cdn-icons-png.flaticon.com/512/2617/2617955.png','VICTORIES');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (8,'Victorioso','Gana 20 partidas',20.0,'https://cdn-icons-png.flaticon.com/512/2617/2617955.png','VICTORIES');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (9,'Imparable','Gana 30 partidas',30.0,'https://cdn-icons-png.flaticon.com/512/2617/2617955.png','VICTORIES');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (10,'Ganador','Gana 40 partidas',40.0,'https://cdn-icons-png.flaticon.com/512/2617/2617955.png','VICTORIES');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (11,'Triunfador','Gana 50 partidas',50.0,'https://cdn-icons-png.flaticon.com/512/2617/2617955.png','VICTORIES');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (12,'Derrota inicial','Pierde tu primera partida',1.0,'https://cdn-icons-png.flaticon.com/512/5220/5220262.png','LOSES');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (13,'Necesitas seguir practicando','Pierde 10 partidas',10.0,'https://cdn-icons-png.flaticon.com/512/5220/5220262.png','LOSES');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (14,'Novato','Pierde 20 partidas',20.0,'https://cdn-icons-png.flaticon.com/512/5220/5220262.png','LOSES');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (15,'Derrotado','Pierde 30 partidas',30.0,'https://cdn-icons-png.flaticon.com/512/5220/5220262.png','LOSES');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (16,'Aniquilado','Pierde 40 partidas',40.0,'https://cdn-icons-png.flaticon.com/512/5220/5220262.png','LOSES');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (17,'Perdedor','Pierde 50 partidas',50.0,'https://cdn-icons-png.flaticon.com/512/5220/5220262.png','LOSES');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (18,'Friki','Has jugado un total de 10 horas',36000.0,'https://cdn-icons-png.flaticon.com/512/2784/2784399.png','TOTAL_PLAY_TIME');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (19,'Adicto','Has jugado un total de 24 horas',86400.0,'https://cdn-icons-png.flaticon.com/512/2784/2784399.png','TOTAL_PLAY_TIME');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (20,'Obsesionado','Has jugado un total de 168 horas',604800.0,'https://cdn-icons-png.flaticon.com/512/2784/2784399.png','TOTAL_PLAY_TIME');

-- Logros de usuarios
INSERT INTO appusers_achievement(achievement_id, user_id) VALUES (1, 9);


-- Amistades
INSERT INTO friends(user1_id,user2_id) VALUES (7,1);
INSERT INTO friends(user1_id,user2_id) VALUES (7,2);
