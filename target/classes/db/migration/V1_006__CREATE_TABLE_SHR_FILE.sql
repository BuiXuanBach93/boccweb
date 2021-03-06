CREATE TABLE "SHR_FILE" (
    FILE_ID                   NUMBER(11)          NOT NULL,
    FILE_PROVIDER             VARCHAR2(100),
    FILE_ORG_NAME             VARCHAR2(100),
    FILE_NAME                 VARCHAR2(100)       NOT NULL,
    FILE_WIDTH                NUMBER(6),
    FILE_HEIGHT               NUMBER(6),
    FILE_SIZE                 NUMBER(9),
    FILE_EXT                  VARCHAR2(10),
    FILE_DIR                  VARCHAR2(1000)      NOT NULL,

    CMN_DELETE_FLAG           NUMBER(1)           DEFAULT 0 NOT NULL, -- 0 - False, 1 - True
    CMN_ENTRY_DATE            DATE                DEFAULT CURRENT_DATE NOT NULL,
    CMN_LAST_UPDT_DATE        DATE                DEFAULT CURRENT_DATE NOT NULL,
    CMN_ENTRY_USER_NO         NUMBER(11),
    CMN_ENTRY_USER_TYPE       NUMBER(1),
    CMN_LAST_UPDT_USER_NO     NUMBER(11),
    CMN_LAST_UPDT_USER_TYPE   NUMBER(1)
);

ALTER TABLE "SHR_FILE"
ADD CONSTRAINT SHR_FILE_PK PRIMARY KEY (FILE_ID);

CREATE SEQUENCE SHR_FILE_SEQ START WITH 1;