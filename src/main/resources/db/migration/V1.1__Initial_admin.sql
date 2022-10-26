CREATE TABLE customer (
    id varchar(50),
    username varchar(50),
    password varchar(100),
    full_name varchar(255),
    phone varchar(20),
    email varchar(100),
    address varchar(255),
    role varchar(50),
    PRIMARY KEY (id)
);

INSERT INTO customer (id, username, password, full_name, phone, email, address, role)
VALUES ("2d6385ff-4274-4ab5-ad48-ddf55f54b667", "admin", "$2a$10$CIOpc3S.aHRqwHOX/5eiSuxTKsxYkwwVBnjKkzGuLKkHLmCWS9Iwa", "Root Admin", "0909090909", "admin@demo.com", "address of admin", "ROLE_ADMIN");
