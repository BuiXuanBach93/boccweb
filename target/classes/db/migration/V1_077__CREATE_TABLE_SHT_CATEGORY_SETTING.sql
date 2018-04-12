CREATE TABLE "SHT_CATEGORY_SETTING" (
    CATEGORY_SETTING_ID          NUMBER(11)          NOT NULL,
    CATEGORY_NO                  VARCHAR2(11)        ,
    CATEGORY_NAME                VARCHAR2(50)        NOT NULL,
    ADMIN_ID                     NUMBER(11)          NOT NULL,
    FILTER_TYPE                  NUMBER(1)           DEFAULT 0 NOT NULL,
    KEYWORDS                     VARCHAR2(3000)      ,
    POST_IDS                     VARCHAR2(3000)      ,
    PUBLISH_TYPE                 NUMBER(1)           DEFAULT 0 NOT NULL,
    CATEGORY_STATUS              NUMBER(1)           DEFAULT 0 NOT NULL,
    IS_DEFAULT                   NUMBER(1)           DEFAULT 0 NOT NULL,

    CMN_DELETE_FLAG           NUMBER(1)           DEFAULT 0 NOT NULL,
    CMN_ENTRY_DATE            DATE                DEFAULT CURRENT_DATE NOT NULL,
    CMN_LAST_UPDT_DATE        DATE                DEFAULT CURRENT_DATE NOT NULL,
    CMN_ENTRY_USER_NO         NUMBER(11),
    CMN_ENTRY_USER_TYPE       NUMBER(1),
    CMN_LAST_UPDT_USER_NO     NUMBER(11),
    CMN_LAST_UPDT_USER_TYPE   NUMBER(1)
);

ALTER TABLE "SHT_CATEGORY_SETTING"
    ADD CONSTRAINT SHT_CATEGORY_SETTING_PK PRIMARY KEY (CATEGORY_SETTING_ID);

CREATE SEQUENCE SHT_CATEGORY_SETTING_SEQ START WITH 1;