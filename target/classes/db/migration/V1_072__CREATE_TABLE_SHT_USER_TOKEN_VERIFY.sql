-- DROP TABLE SHT_USER_TOKEN_VERIFY;

CREATE TABLE SHT_USER_TOKEN_VERIFY (
	TOKEN_VERIFY_ID NUMBER(11,0) NOT NULL ENABLE,
	USER_ID NUMBER(11,0) NOT NULL ENABLE, 
	VERIFY_CODE VARCHAR2(255) NOT NULL ENABLE,
	TOKEN_ID VARCHAR2(255) NOT NULL ENABLE,
	CMN_DELETE_FLAG NUMBER(1,0) DEFAULT 0 NOT NULL ENABLE, 
	CMN_ENTRY_DATE DATE DEFAULT CURRENT_DATE NOT NULL ENABLE, 
	CMN_LAST_UPDT_DATE DATE DEFAULT CURRENT_DATE NOT NULL ENABLE, 
	CMN_ENTRY_USER_NO NUMBER(11,0), 
	CMN_ENTRY_USER_TYPE NUMBER(1,0), 
	CMN_LAST_UPDT_USER_NO NUMBER(11,0), 
	CMN_LAST_UPDT_USER_TYPE NUMBER(1,0)
);

ALTER TABLE SHT_USER_TOKEN_VERIFY
ADD CONSTRAINT SHT_USER_TOKEN_VERIFY_PK PRIMARY KEY (TOKEN_VERIFY_ID);

ALTER TABLE SHT_USER_TOKEN_VERIFY
ADD CONSTRAINT SHT_USER_TOKEN_V_USER_ID_FK
FOREIGN KEY (USER_ID) REFERENCES SHM_USER (USER_ID) ON DELETE CASCADE;

ALTER TABLE SHT_USER_TOKEN_VERIFY
ADD CONSTRAINT SHT_USER_TOKEN_V_TOKEN_ID_FK
FOREIGN KEY (TOKEN_ID) REFERENCES SHT_USER_TOKEN (TOKEN_ID) ON DELETE CASCADE;

CREATE INDEX IDX_TOKEN_VERIFY_VERIFY_CODE
ON SHT_USER_TOKEN_VERIFY(VERIFY_CODE);

--DROP SEQUENCE SHT_USER_TOKEN_VERIFY_SEQ;
CREATE SEQUENCE SHT_USER_TOKEN_VERIFY_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;