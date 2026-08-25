create table if not exists users (
    user_id     serial primary key,
    username    varchar(100) not null unique,
    first_name  varchar(100) not null,
    last_name   varchar(100) not null,
    email       varchar(254) not null unique,
    password    varchar(255) not null
);

create table if not exists products (
  product_id    serial primary key,
  name          varchar(255) not null unique
);

create table if not exists listings (
  listing_id    serial primary key,
  name          varchar(255) not null,
  creator_id    integer references users,
  price         decimal(100, 2) not null
);
