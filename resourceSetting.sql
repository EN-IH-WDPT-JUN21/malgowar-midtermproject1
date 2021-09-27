CREATE USER 'midterm-project-1'@'localhost' IDENTIFIED BY 'midterm-project-1';
GRANT ALL PRIVILEGES ON *.* TO 'midterm-project-1'@'localhost';
FLUSH PRIVILEGES;

CREATE DATABASE midtermproject1;
USE midtermproject1;
SHOW TABLES;
DROP DATABASE midtermproject1;

select * from student_checking_accounts;
select * from checking_accounts;
select * from saving_accounts;
select * from credit_cards;
select * from account_holders;
select * from transactions;

INSERT INTO 
	users (enable, password, username) 
VALUES
	(1, '$2a$10$MSzkrmfd5ZTipY0XkuCbAejBC9g74MAg2wrkeu8/m1wQGXDihaX3e', 'accountHolder1'),
	(1, '$2a$10$MSzkrmfd5ZTipY0XkuCbAejBC9g74MAg2wrkeu8/m1wQGXDihaX3e', 'accountHolder2'),
	(1, '$2a$10$MSzkrmfd5ZTipY0XkuCbAejBC9g74MAg2wrkeu8/m1wQGXDihaX3e', 'accountHolder3'),
	(1, '$2a$10$MSzkrmfd5ZTipY0XkuCbAejBC9g74MAg2wrkeu8/m1wQGXDihaX3e', 'accountHolder4'),
    (1, '$2a$10$MSzkrmfd5ZTipY0XkuCbAejBC9g74MAg2wrkeu8/m1wQGXDihaX3e', 'admin1'),
    (1, '$2a$10$MSzkrmfd5ZTipY0XkuCbAejBC9g74MAg2wrkeu8/m1wQGXDihaX3e', 'admin2'),
    (1, '$2a$10$MSzkrmfd5ZTipY0XkuCbAejBC9g74MAg2wrkeu8/m1wQGXDihaX3e', 'thirdParty');  

INSERT INTO role (name, user_id) VALUES
	("ROLE_ACCOUNTHOLDER", 1),
	("ROLE_ACCOUNTHOLDER", 2),
	("ROLE_ACCOUNTHOLDER", 3),
	("ROLE_ACCOUNTHOLDER", 4),
	("ROLE_ADMIN", 5),
	("ROLE_ADMIN", 6),
	("ROLE_THIRDPARTY", 7);

INSERT INTO
	account_holders_addresses(city, country, house_number, local_number, postal_code, street)
VALUES
	('Warsaw', 'Poland', '2a', 3, '34-098', 'Klonowa'),
    ('London', 'United Kingdom', '34', 78, '367-098', 'Abbey Road'),
	('Seville', 'Spain', '108', 8, '7890a', 'Mateos Gago'),
    ('Denamrk', 'Copenhagen', '22b', 34, '78-098', 'Elmegade');

INSERT INTO 
	account_holders (birth_date, email, first_name, last_name, username_id, address_id) 
VALUES
	('1999-01-01', 'johnmayer@gmail.com', 'John', 'Mayer', 1, 1),
	('2000-09-09', 'edwardsmith@gmail.com', 'Edward', 'Smith', 2, 2),
	('1978-12-13', 'annamarco@gmail.com', 'Anna', 'Marco', 3, 3),
	('1980-04-23', 'simonmaxwell@gmail.com', 'Simon', 'Maxwell', 4, 4);
    
INSERT INTO 
	admins (first_name, last_name, username_id) 
VALUES
	('John', 'Mayer', 1, 1),
	('Edward', 'Smith', 2, 2),
	('Anna', 'Marco', 3, 3),
	('Simon', 'Maxwell', 4, 4);

INSERT INTO 
	student_checking_accounts (balance, creation_date, secret_key, status, primary_owner_id, secondary_owner_id, currency) 
VALUES
	(1000.56, sysdate(), 'ASDF123', 'FROZEN', 1, NULL, 'EUR'),
	(1000.00, sysdate(),  'AGtY567', 'ACTIVE', 1, 2, 'USD'),
	(106.90, sysdate(), 'SPor45s', 'ACTIVE', 2, NULL, 'PLN'),
	(250, sysdate(), '12fg4fr', 'ACTIVE', 3, 1, 'USD'),
	(250, sysdate(),  'ADgt7ik', 'FROZEN', 4, NULL, 'PLN'),
	(450.45, sysdate(),  'wOpYT56', 'ACTIVE', 3, NULL, 'EUR');

INSERT INTO 
	checking_accounts (balance, creation_date, minimum_balance, monthly_maintenance_fee, secret_key, status, primary_owner_id, secondary_owner_id, charge_mainfee_last_date, currency) 
VALUES
	(1567.89, sysdate(), 1000, 12, 'ASDF123', 'FROZEN', 1, 3, sysdate(), 'EUR'),
	(1234.00, sysdate(), 1500, 12, 'AGtY567', 'ACTIVE', 3, NULL, sysdate(), 'EUR'),
	(678.90, sysdate(), 250, 12, 'SPor45s', 'FROZEN', 2, NULL, sysdate(), 'USD'),
	(39876.09, sysdate(), 500, 12, '12fg4fr', 'ACTIVE', 1, 2, sysdate(), 'USD'),
	(376, sysdate(), 300, 12, 'ADgt7ik', 'ACTIVE', 4, NULL, sysdate(), 'EUR'),
	(1234.78, sysdate(), 250, 12, 'wOpYT56', 'ACTIVE', 3, 2, sysdate(), 'PLN');    
  
INSERT INTO 
	saving_accounts ( balance, creation_date, last_interest_date, interest_rate, minimum_balance, secret_key, status, primary_owner_id, secondary_owner_id, currency) 
VALUES
	(1000.00, sysdate(), sysdate()-2, 0.0025, 1000.00, 'ASDF123', 'FROZEN', 2, 3, 'EUR'),
	(234.00, sysdate(), sysdate()-2, 0.5, 234.00, 'AGtY567','ACTIVE', 3, 1, 'USD'),
	(700.90, sysdate(), sysdate()-2, 0.25, 700.90, 'SPor45s', 'FROZEN', 1, NULL, 'PLN'),
	(9876.09, sysdate(), sysdate()-2, 0.0025, 500.00, '12fg4fr', 'ACTIVE', 2, NULL, 'USD'),
	(37656.89, sysdate(), sysdate()-2, 0.45, 300.00, 'ADgt7ik', 'ACTIVE', 4, NULL, 'EUR'),
	(1500.78, sysdate(), sysdate()-2, 0.1, 250.00, 'wOpYT56', 'ACTIVE', 3, 2, 'EUR');      
    
INSERT INTO 
	credit_cards (balance, creation_date, last_interest_date, credit_limit, interest_rate, primary_owner_id, secondary_owner_id, currency) 
VALUES
	(1111.00, sysdate(), sysdate()-2, 1000.00, 0.1, 2, 3, 'EUR'),
	(2340.00, sysdate(), sysdate()-2, 234.00, 0.2, 3, 1, 'PLN'),
	(3700.90, sysdate(), sysdate()-2, 700.90, 0.12, 1, NULL, 'USD'),
	(97.09, sysdate(), sysdate()-2, 500.00, 0.15, 2, NULL, 'EUR'),
	(37.89, sysdate(), sysdate()-2, 300.00, 0.2, 4, NULL, 'PLN'),
	(500.78, sysdate(), sysdate()-2, 250.00, 0.19, 3, 2, 'USD');   

