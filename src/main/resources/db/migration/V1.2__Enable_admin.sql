ALTER TABLE customer
ADD COLUMN (
    created_date datetime,
    last_modified_date datetime,
    last_modified_by varchar(50),
    enable bit
);

UPDATE customer
SET id = 'RootAdmin',
    enable = 1,
    created_date = NOW(),
    last_modified_date = NOW(),
    last_modified_by = 'admin'
WHERE username = 'admin';
