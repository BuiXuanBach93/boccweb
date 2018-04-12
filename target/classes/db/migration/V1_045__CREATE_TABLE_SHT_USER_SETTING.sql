CREATE TABLE "SHT_USER_SETTING" (
		USER_SETTING_ID           NUMBER(11)          NOT NULL,
		USER_ID                   NUMBER(11)          NOT NULL,
		RECEIVE_EMAIL             NUMBER(1)           DEFAULT 1 NOT NULL,
		RECEIVE_PUSH              NUMBER(1)           DEFAULT 1 NOT NULL,

    CMN_DELETE_FLAG           NUMBER(1)           DEFAULT 0 NOT NULL, -- 0 - False, 1 - True
    CMN_ENTRY_DATE            DATE                DEFAULT CURRENT_DATE NOT NULL,
    CMN_LAST_UPDT_DATE        DATE                DEFAULT CURRENT_DATE NOT NULL,
    CMN_ENTRY_USER_NO         NUMBER(11),
    CMN_ENTRY_USER_TYPE       NUMBER(1),
    CMN_LAST_UPDT_USER_NO     NUMBER(11),
    CMN_LAST_UPDT_USER_TYPE   NUMBER(1)
);

ALTER TABLE "SHT_USER_SETTING"
ADD CONSTRAINT SHT_USER_SETTING_PK PRIMARY KEY (USER_SETTING_ID);

CREATE SEQUENCE SHT_USER_SETTING_SEQ START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;