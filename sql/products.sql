﻿DROP TABLE IF EXISTS products;

CREATE TABLE products
(
id integer PRIMARY KEY,
name text,
description text,
currency_string text,
default_price numeric(10,2),
category_id integer,
supplier_id integer
);