ALTER TABLE
    "SHM_POST" MODIFY ("POST_IMAGES" NULL);

CREATE INDEX IDX_POST_NAME ON SHM_POST (POST_NAME);
CREATE INDEX IDX_POST_DESCIPTION ON SHM_POST (POST_DESCRIPTION);

CREATE INDEX IDX_USER_BSID ON SHM_USER (USER_BSID);
