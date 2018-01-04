DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS "order";
DROP TABLE IF EXISTS supplier;
DROP TABLE IF EXISTS product_category;
DROP TABLE IF EXISTS product;

CREATE TABLE users
(
  id serial NOT NULL,
  name varchar(30) NOT NULL UNIQUE,
  password CHAR(60) NOT NULL
);

CREATE TABLE "order"
(
  id integer NOT NULL PRIMARY KEY,
  totalsize integer,
  totalprice numeric(10,2)
);

CREATE TABLE supplier
(
id integer PRIMARY KEY,
name text,
description text
);


CREATE TABLE product_category
(
id integer PRIMARY KEY,
name text,
description text,
department text
);

CREATE TABLE product
(
id integer PRIMARY KEY,
name text,
description text,
currency_string text,
default_price numeric(10,2),
category_id integer,
supplier_id integer
);


INSERT INTO users (id, name, password) VALUES (1,'TestUser1','TestPassword1');
INSERT INTO users (id, name, password) VALUES (2,'TestUser2','TestPassword2');
INSERT INTO users (id, name, password) VALUES (3,'TestUser3','TestPassword3');

INSERT INTO supplier (id, name, description) VALUES (1,'TestSupplier1','TestSupplierDescription1');
INSERT INTO supplier (id, name, description) VALUES (2,'TestSupplier2','TestSupplierDescription2');
INSERT INTO supplier (id, name, description) VALUES (3,'TestSupplier3','TestSupplierDescription3');

INSERT INTO product_category (id, name, description, department) VALUES (1, 'TestProductCategoryName1', 'TestProductCategoryDescription1', 'TestProductCategoryDepartment1');
INSERT INTO product_category (id, name, description, department) VALUES (2, 'TestProductCategoryName2', 'TestProductCategoryDescription2', 'TestProductCategoryDepartment2');
INSERT INTO product_category (id, name, description, department) VALUES (3, 'TestProductCategoryName3', 'TestProductCategoryDescription3', 'TestProductCategoryDepartment3');


INSERT INTO product (id, name, description, currency_string, default_price, category_id, supplier_id) VALUES (1, 'TestProductCategoryName1','TestProductCategoryDescription1','USD', 10, 1, 1);
INSERT INTO product (id, name, description, currency_string, default_price, category_id, supplier_id) VALUES (2, 'TestProductCategoryName2','TestProductCategoryDescription2','USD', 10, 1, 1);
INSERT INTO product (id, name, description, currency_string, default_price, category_id, supplier_id) VALUES (3, 'TestProductCategoryName3','TestProductCategoryDescription3','USD', 10, 2, 2);
INSERT INTO product (id, name, description, currency_string, default_price, category_id, supplier_id) VALUES (4, 'TestProductCategoryName4','TestProductCategoryDescription4','USD', 10, 2, 2);
INSERT INTO product (id, name, description, currency_string, default_price, category_id, supplier_id) VALUES (5, 'TestProductCategoryName5','TestProductCategoryDescription5','USD', 10, 3, 3);
INSERT INTO product (id, name, description, currency_string, default_price, category_id, supplier_id) VALUES (6, 'TestProductCategoryName6','TestProductCategoryDescription6','USD', 10, 3, 3);