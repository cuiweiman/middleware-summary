USE `test`;

DROP TABLE IF EXISTS employee;

CREATE TABLE employee
(
    emp_id   VARCHAR(10)  NOT NULL primary key,
    emp_name VARCHAR(100) NOT NULL
);
insert into employee(`emp_id`, `emp_name`)
values ('1001', 'employee1001');