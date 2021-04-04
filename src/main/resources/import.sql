-- 
-- El contenido de este fichero se cargará al arrancar la aplicación, suponiendo que uses
-- 		application-default ó application-externaldb en modo 'create'
--

-- Usuario de ejemplo con username = b y contraseña = aa  
INSERT INTO user(id,enabled,username,password,roles,first_name,last_name) VALUES (
	1, 1, 'a', 
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',
	'USER,ADMIN',
	'Abundio', 'Ejémplez'
);

-- Otro usuario de ejemplo con username = b y contraseña = aa  
INSERT INTO user(id,enabled,username,password,roles,first_name,last_name) VALUES (
	2, 1, 'b', 
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',
	'USER',
	'Berta', 'Muéstrez'
);

-- Unos pocos auto-mensajes de prueba
INSERT INTO MESSAGE VALUES(1,NULL,'2020-03-23 10:48:11.074000','probando 1',1,1);
INSERT INTO MESSAGE VALUES(2,NULL,'2020-03-23 10:48:15.149000','probando 2',1,1);
INSERT INTO MESSAGE VALUES(3,NULL,'2020-03-23 10:48:18.005000','probando 3',1,1);
INSERT INTO MESSAGE VALUES(4,NULL,'2020-03-23 10:48:20.971000','probando 4',1,1);
INSERT INTO MESSAGE VALUES(5,NULL,'2020-03-23 10:48:22.926000','probando 5',1,1);

--Diseños de prueba
INSERT INTO DESIGN VALUES(1, 'Deporte', 'Hola soy un diseño', 123, 'Pruebadiseño', 12, 0, 2);
INSERT INTO DESIGN VALUES(2, 'Accesorios', 'Hola soy un diseño1', 123, 'Pruebadiseño1', 12, 0, 1);
INSERT INTO DESIGN VALUES(3, 'Juguetes', 'Hola soy un diseño2', 123, 'Pruebadiseño2', 12, 0, 2);

-- Compras de prueba
--INSERT INTO design VALUES();
INSERT INTO SALES VALUES (1, 'Calle ola mayor', 40, 1);

--Sales line de prueba
INSERT INTO SALES_LINE VALUES (1, sysdate, 1, 40, 2, 1, 1);