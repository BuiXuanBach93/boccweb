CREATE TABLE "SHT_MEMO_USER" (
    MEMO_USER_ID                NUMBER(11)        NOT NULL,
    ADMIN_ID                    NUMBER(11)        NOT NULL,
    USER_ID                     NUMBER(11)        NOT NULL,
    MEMO_CONTENT                VARCHAR (1000)    NOT NULL,
    CMN_DELETE_FLAG           NUMBER(1)           DEFAULT 0 NOT NULL, -- 0 - False, 1 - True
    CMN_ENTRY_DATE            DATE                DEFAULT CURRENT_DATE NOT NULL,
    CMN_LAST_UPDT_DATE        DATE                DEFAULT CURRENT_DATE NOT NULL,
    CMN_ENTRY_USER_NO         NUMBER(11),
    CMN_ENTRY_USER_TYPE       NUMBER(1),
    CMN_LAST_UPDT_USER_NO     NUMBER(11),
    CMN_LAST_UPDT_USER_TYPE   NUMBER(1)
);

ALTER TABLE "SHT_MEMO_USER"
ADD CONSTRAINT SHT_MEMO_USER_PK PRIMARY KEY (MEMO_USER_ID);

CREATE SEQUENCE SHT_MEMO_USER_SEQ START WITH 1;