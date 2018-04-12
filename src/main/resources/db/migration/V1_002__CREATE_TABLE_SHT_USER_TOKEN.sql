CREATE TABLE "SHT_USER_TOKEN" (
    USER_ID                   NUMBER(11)          NOT NULL,
    TOKEN_ID                  VARCHAR2(255)       NOT NULL,
    TOKEN_TYPE                NUMBER(2)           NOT NULL, -- 0 - access token, 1 - refresh token, 2 - mail active token, 3 - registration token, 4 - reset password token
    TOKEN_EXPIRE_IN           NUMBER(6)           NOT NULL, -- Duration (in second) that this token is still valid (from entry_date).
                                                            -- Negative value (Eg: -1): Token's never expired."
    CMN_DELETE_FLAG           NUMBER(1)           DEFAULT 0 NOT NULL, -- 0 - False, 1 - True
    CMN_ENTRY_DATE            DATE                DEFAULT CURRENT_DATE NOT NULL,
    CMN_LAST_UPDT_DATE        DATE                DEFAULT CURRENT_DATE NOT NULL,
    CMN_ENTRY_USER_NO         NUMBER(11),
    CMN_ENTRY_USER_TYPE       NUMBER(1),
    CMN_LAST_UPDT_USER_NO     NUMBER(11),
    CMN_LAST_UPDT_USER_TYPE   NUMBER(1)
);

ALTER TABLE "SHT_USER_TOKEN"
ADD CONSTRAINT SHT_USER_TOKEN_PK PRIMARY KEY (TOKEN_ID);

ALTER TABLE "SHT_USER_TOKEN"
ADD CONSTRAINT SHT_USER_TOKEN_SHM_USER_FK
FOREIGN KEY (USER_ID) REFERENCES "SHM_USER" (USER_ID) ON DELETE CASCADE;