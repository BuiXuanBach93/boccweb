CREATE TABLE "SHT_USER_READ_MSG" (
    READ_MSG_ID                       NUMBER(11)      NOT NULL,
    QA_ID                     NUMBER(11),
    USER_ID                   NUMBER(11),
    CMN_DELETE_FLAG           NUMBER(1)           DEFAULT 0 NOT NULL,
    CMN_ENTRY_DATE            DATE                DEFAULT CURRENT_DATE NOT NULL,
    CMN_LAST_UPDT_DATE        DATE                DEFAULT CURRENT_DATE NOT NULL,
    CMN_ENTRY_USER_NO         NUMBER(11),
    CMN_ENTRY_USER_TYPE       NUMBER(1),
    CMN_LAST_UPDT_USER_NO     NUMBER(11),
    CMN_LAST_UPDT_USER_TYPE   NUMBER(1)
);

ALTER TABLE "SHT_USER_READ_MSG"
    ADD CONSTRAINT SHT_USER_READ_MSG_PK PRIMARY KEY (READ_MSG_ID);

CREATE SEQUENCE SHT_USER_READ_MSG_SEQ START WITH 1;