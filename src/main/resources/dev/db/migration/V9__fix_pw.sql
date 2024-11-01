ALTER TABLE users
    MODIFY password CHAR(60) NULL,
    MODIFY google_account_id VARCHAR(255),
    MODIFY facebook_account_id VARCHAR(255) ;

ALTER table emails
    modify id CHAR(60);
ALTER table facebooks
    modify id CHAR(60);