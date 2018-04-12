CREATE TABLE "SHT_BANNER" (
    BANNER_ID                    NUMBER(11)          NOT NULL,
    BANNER_NO                    VARCHAR2(11)        ,
    BANNER_NAME                  VARCHAR2(500)       NOT NULL,
    ADMIN_ID                     NUMBER(11)          NOT NULL,
    KEYWORDS                     VARCHAR2(3000)      ,
    POST_IDS                     VARCHAR2(3000)      ,
    WEB_URL                      VARCHAR2(1000)      ,
    CATEGORY_ID                  NUMBER(11),
    IMAGE_ID                     NUMBER(11),
    DESTINATION_TYPE             NUMBER(1)           DEFAULT 0 NOT NULL,
    BANNER_STATUS                NUMBER(1)           DEFAULT 0 NOT NULL,
    FROM_DATE                    DATE                NOT NULL,
    TO_DATE                      DATE                NOT NULL,

    CMN_DELETE_FLAG           NUMBER(1)           DEFAULT 0 NOT NULL,
    CMN_ENTRY_DATE            DATE                DEFAULT CURRENT_DATE NOT NULL,
    CMN_LAST_UPDT_DATE        DATE                DEFAULT CURRENT_DATE NOT NULL,
    CMN_ENTRY_USER_NO         NUMBER(11),
    CMN_ENTRY_USER_TYPE       NUMBER(1),
    CMN_LAST_UPDT_USER_NO     NUMBER(11),
    CMN_LAST_UPDT_USER_TYPE   NUMBER(1)
);

ALTER TABLE "SHT_BANNER"
    ADD CONSTRAINT SHT_BANNER_PK PRIMARY KEY (BANNER_ID);

CREATE SEQUENCE SHT_BANNER_SEQ START WITH 1;