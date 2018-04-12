CREATE TABLE "SHT_KPI_STORAGE" (
    KPI_ID                       NUMBER(11)          NOT NULL,
    KPI_QUERY_TIME               VARCHAR2(11),
    KPI_TYPE                     NUMBER(1)          DEFAULT 0 NOT NULL,
    DL_NUMBER                    NUMBER(11),
    REG_NUMBER                   NUMBER(11),
    REG_RATIO                    NUMBER(11,2),
    OWNER_NUMBER                 NUMBER(11),
    POST_NUMBER                  NUMBER(11),
    POST_RATIO                   NUMBER(11,2),
    PARTNER_NUMBER               NUMBER(11),
    TRANS_NUMBER                 NUMBER(11),
    TRANS_RATIO                  NUMBER(11,2),
    ACTOR_NUMBER                 NUMBER(11),


    CMN_DELETE_FLAG           NUMBER(1)           DEFAULT 0 NOT NULL,
    CMN_ENTRY_DATE            DATE                DEFAULT CURRENT_DATE NOT NULL,
    CMN_LAST_UPDT_DATE        DATE                DEFAULT CURRENT_DATE NOT NULL,
    CMN_ENTRY_USER_NO         NUMBER(11),
    CMN_ENTRY_USER_TYPE       NUMBER(1),
    CMN_LAST_UPDT_USER_NO     NUMBER(11),
    CMN_LAST_UPDT_USER_TYPE   NUMBER(1)
);

ALTER TABLE "SHT_KPI_STORAGE"
    ADD CONSTRAINT SHT_KPI_STORAGE_PK PRIMARY KEY (KPI_ID);

CREATE SEQUENCE SHT_KPI_STORAGE_SEQ START WITH 1;