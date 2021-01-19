DROP TABLE IF EXISTS TBL_EMPLOYEES;

CREATE TABLE TBL_EMPLOYEES (
                               id INT  PRIMARY KEY,
                               name VARCHAR(250) NOT NULL
);

insert into TBL_EMPLOYEES values ( 1, 'toto' )
