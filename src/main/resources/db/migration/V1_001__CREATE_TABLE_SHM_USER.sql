CREATE TABLE "SHM_USER" (
    USER_ID                  NUMBER(11)         NOT NULL,
    USER_BSID                VARCHAR2(16)       NOT NULL,
    USER_FIRST_NAME          VARCHAR2(40),
    USER_LAST_NAME           VARCHAR2(40),
    USER_NICK_NAME           VARCHAR2(40),
    USER_PWD                 VARCHAR2(50),
    USER_EMAIL               VARCHAR2(100)      NOT NULL,
    USER_PHONE               VARCHAR2(13),
    USER_GENDER              NUMBER(1), -- 0 - Female, 1 - Male
    USER_DATE_OF_BIRTH       DATE,
    USER_ADDRESS_ID          NUMBER(11),
    USER_DESCR               VARCHAR2(500),
    USER_AVTR                NUMBER(11),
    USER_STATUS              NUMBER(2)           DEFAULT 0 NOT NULL, -- 0 - EmailUnactivated, 1 - SMSUnactivated, 2 - NoPassword, 3 - NoProfile, 4 - Activated, 5 - TendToLeave, 6 - Left
    USER_CTRL_STATUS         NUMBER(2)           DEFAULT 0 NOT NULL, -- 0 - Normal, 1 - Suspended
    USER_PTRL_STATUS         NUMBER(1)           DEFAULT 0 NOT NULL, -- 0 - Uncensored, 1 - Censoring, 2 - Censored
    CMN_DELETE_FLAG          NUMBER(1)           DEFAULT 0 NOT NULL, -- 0 - False, 1 - True
    CMN_ENTRY_DATE           DATE                DEFAULT CURRENT_DATE NOT NULL,
    CMN_LAST_UPDT_DATE       DATE                DEFAULT CURRENT_DATE NOT NULL,
    CMN_ENTRY_USER_NO        NUMBER(11),
    CMN_ENTRY_USER_TYPE      NUMBER(1),
    CMN_LAST_UPDT_USER_NO    NUMBER(11),
    CMN_LAST_UPDT_USER_TYPE  NUMBER(1)
);

ALTER TABLE "SHM_USER"
ADD CONSTRAINT SHM_USER_PK PRIMARY KEY (USER_ID);

CREATE SEQUENCE SHM_USER_SEQ START WITH 1;