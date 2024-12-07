CREATE DATABASE mybank;

USE mybank;

CREATE TABLE usersTable (
	user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    account_holder_name VARCHAR(50) NOT NULL UNIQUE,
    balance DECIMAL (10, 2) DEFAULT 10.00,
    time_registered TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactionTable (
	transaction_id INT auto_increment primary KEY,
    account_id INT NOT NULL,
    transaction_type ENUM ('DEPOSIT','withraw'),
    amount INT NOT NULL, 
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES usersTable(user_id)

);



SELECT * FROM transactionTable;
SELECT * FROM usersTable;