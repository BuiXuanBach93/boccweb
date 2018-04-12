----DROP SEQUENCE SHM_ADDR_SEQ;
--CREATE SEQUENCE SHM_ADDR_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;
----DROP SEQUENCE IF EXISTS SHM_ADMIN_NG_SEQ;
CREATE SEQUENCE SHM_ADMIN_NG_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;
--DROP SEQUENCE SHM_ADMIN_SEQ;
CREATE SEQUENCE SHM_ADMIN_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;
--DROP SEQUENCE SHM_CATEGORY_SEQ;
CREATE SEQUENCE SHM_CATEGORY_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;
--DROP SEQUENCE SHM_POST_SEQ;
CREATE SEQUENCE SHM_POST_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;
----DROP SEQUENCE SHM_USER_SEQ;
--CREATE SEQUENCE SHM_USER_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;
--DROP SEQUENCE SHR_FILE_SEQ;
--CREATE SEQUENCE SHR_FILE_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;
--DROP SEQUENCE SHT_ADMIN_CSV_HST_SEQ;
CREATE SEQUENCE SHT_ADMIN_CSV_HST_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;
--DROP SEQUENCE SHT_ADMIN_LOG_SEQ;
CREATE SEQUENCE SHT_ADMIN_LOG_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;
--DROP SEQUENCE SHT_MEMO_QA_SEQ;
CREATE SEQUENCE SHT_MEMO_QA_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;
--DROP SEQUENCE SHT_MSG_SMS_SEQ;
--CREATE SEQUENCE SHT_MSG_SMS_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;
--DROP SEQUENCE SHT_QA_SEQ;
CREATE SEQUENCE SHT_QA_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;
--DROP SEQUENCE SHT_TALK_PURC_MSG_SEQ;
CREATE SEQUENCE SHT_TALK_PURC_MSG_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;
--DROP SEQUENCE SHT_TALK_PURC_SEQ;
CREATE SEQUENCE SHT_TALK_PURC_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;
--DROP SEQUENCE SHT_TALK_QA_SEQ;
CREATE SEQUENCE SHT_TALK_QA_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;
--DROP SEQUENCE SHT_USER_FVRT_SEQ;
CREATE SEQUENCE SHT_USER_FVRT_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;
--DROP SEQUENCE SHT_USER_REV_SEQ;
CREATE SEQUENCE SHT_USER_REV_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;
--DROP SEQUENCE SHT_USER_RPRT_SEQ;
CREATE SEQUENCE SHT_USER_RPRT_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;
----DROP SEQUENCE SHT_USER_TOKEN_SEQ;
--CREATE SEQUENCE SHT_USER_TOKEN_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20 NOORDER;
----DROP TABLE SHM_ADDR;
--CREATE TABLE SHM_ADDR (ADDR_ID NUMBER(11) NOT NULL, ADDR_AREA_CODE VARCHAR2(10) NOT NULL, ADDR_AREA_NAME VARCHAR2(100) NOT NULL, ADDR_PARRENT_ID NUMBER(11), CMN_DELETE_FLAG NUMBER(1) NOT NULL, CMN_ENTRY_DATE DATE NOT NULL, CMN_LAST_UPDT_DATE DATE NOT NULL, CMN_ENTRY_USER_NO NUMBER(11), CMN_ENTRY_USER_TYPE NUMBER(1), CMN_LAST_UPDT_USER_NO NUMBER(11), CMN_LAST_UPDT_USER_TYPE NUMBER(1), CONSTRAINT SHM_ADDR_PK PRIMARY KEY (ADDR_ID));
--DROP TABLE SHM_ADMIN;
CREATE TABLE SHM_ADMIN (ADMIN_ID NUMBER(11) NOT NULL, ADMIN_NAME VARCHAR2(50) NOT NULL, ADMIN_PWD VARCHAR2(60) NOT NULL, ADMIN_EMAIL VARCHAR2(100) NOT NULL, ADMIN_ROLE NUMBER(1) NOT NULL, CMN_DELETE_FLAG NUMBER(1) NOT NULL, CMN_ENTRY_DATE DATE NOT NULL, CMN_LAST_UPDT_DATE DATE NOT NULL, CMN_ENTRY_USER_NO NUMBER(11), CMN_ENTRY_USER_TYPE NUMBER(1), CMN_LAST_UPDT_USER_NO NUMBER(11), CMN_LAST_UPDT_USER_TYPE NUMBER(1), CONSTRAINT SHM_ADMIN_PK PRIMARY KEY (ADMIN_ID));
--DROP TABLE SHM_ADMIN_NG;
CREATE TABLE SHM_ADMIN_NG (ADMIN_NG_ID NUMBER(11) NOT NULL, ADMIN_NG_CONTENT VARCHAR2(100) NOT NULL, CMN_DELETE_FLAG NUMBER(1) NOT NULL, CMN_ENTRY_DATE DATE NOT NULL, CMN_LAST_UPDT_DATE DATE NOT NULL, CMN_ENTRY_USER_NO NUMBER(11), CMN_ENTRY_USER_TYPE NUMBER(1), CMN_LAST_UPDT_USER_NO NUMBER(11), CMN_LAST_UPDT_USER_TYPE NUMBER(1), CONSTRAINT SHM_ADMIN_NG_PK PRIMARY KEY (ADMIN_NG_ID));
--DROP TABLE SHM_CATEGORY;
CREATE TABLE SHM_CATEGORY (CATEGORY_ID NUMBER(11) NOT NULL, CATEGORY_NAME VARCHAR2(30) NOT NULL, CATEGORY_ICON NUMBER(11) NOT NULL, CATEGORY_PARRENT_ID NUMBER(11) NOT NULL, CMN_DELETE_FLAG NUMBER(1) NOT NULL, CMN_ENTRY_DATE DATE NOT NULL, CMN_LAST_UPDT_DATE DATE NOT NULL, CMN_ENTRY_USER_NO NUMBER(11), CMN_ENTRY_USER_TYPE NUMBER(1), CMN_LAST_UPDT_USER_NO NUMBER(11), CMN_LAST_UPDT_USER_TYPE NUMBER(1), CONSTRAINT SHM_CATEGORY_PK PRIMARY KEY (CATEGORY_ID));
--DROP TABLE SHM_POST;
CREATE TABLE SHM_POST (POST_ID NUMBER(11) NOT NULL, POST_USER_ID NUMBER(11) NOT NULL, POST_NAME VARCHAR2(100) NOT NULL, POST_DESCRIPTION VARCHAR2(1000) NOT NULL, POST_CATEGORY_ID NUMBER(11) NOT NULL, POST_PRICE NUMBER(11) NOT NULL, POST_LIKE_TIMES NUMBER(11) NOT NULL, POST_REPORT_TIMES NUMBER(11) NOT NULL, POST_TYPE NUMBER(1) NOT NULL, POST_IMAGES VARCHAR2(255) NOT NULL, POST_ADDRESS NUMBER(11) NOT NULL, POST_ADDR_TXT VARCHAR2(1000), POST_SELL_STATUS NUMBER(1) NOT NULL, POST_CTRL_STATUS NUMBER(1) NOT NULL, POST_PTRL_STATUS NUMBER(1) NOT NULL, CMN_DELETE_FLAG NUMBER(1) NOT NULL, CMN_ENTRY_DATE DATE NOT NULL, CMN_LAST_UPDT_DATE DATE NOT NULL, CMN_ENTRY_USER_NO NUMBER(11), CMN_ENTRY_USER_TYPE NUMBER(1), CMN_LAST_UPDT_USER_NO NUMBER(11), CMN_LAST_UPDT_USER_TYPE NUMBER(1), CONSTRAINT SHM_POST_PK PRIMARY KEY (POST_ID));
----DROP TABLE SHM_USER;
--CREATE TABLE SHM_USER (USER_ID NUMBER(11) NOT NULL, USER_BSID VARCHAR2(16) NOT NULL, USER_FIRST_NAME VARCHAR2(40), USER_LAST_NAME VARCHAR2(40), USER_NICK_NAME VARCHAR2(40), USER_PWD VARCHAR2(50), USER_USER_EMAIL VARCHAR2(100) NOT NULL, USER_PHONE VARCHAR2(13), USER_GENDER NUMBER(1), USER_DATE_OF_BIRTH DATE, USER_ADDRESS_ID NUMBER(11), USER_DESCR VARCHAR2(500), USER_AVTR NUMBER(11), USER_STATUS NUMBER(2) NOT NULL, USER_CTRL_STATUS NUMBER(2) NOT NULL, USER_PTRL_STATUS NUMBER(1) NOT NULL, CMN_DELETE_FLAG NUMBER(1) NOT NULL, CMN_ENTRY_DATE DATE NOT NULL, CMN_LAST_UPDT_DATE DATE NOT NULL, CMN_ENTRY_USER_NO NUMBER(11), CMN_ENTRY_USER_TYPE NUMBER(1), CMN_LAST_UPDT_USER_NO NUMBER(11), CMN_LAST_UPDT_USER_TYPE NUMBER(1), CONSTRAINT SHM_USER_PK PRIMARY KEY (USER_ID));
--DROP TABLE SHR_FILE;
--CREATE TABLE SHR_FILE (FILE_ID NUMBER(11) NOT NULL, FILE_PROVIDER VARCHAR2(100), FILE_ORG_NAME VARCHAR2(100), FILE_NAME VARCHAR2(100), FILE_WIDTH NUMBER(6), FILE_HEIGHT NUMBER(6), FILE_SIZE NUMBER(9), FILE_EXT VARCHAR2(10), FILE_DIR VARCHAR2(1000), CMN_DELETE_FLAG NUMBER(1) NOT NULL, CMN_ENTRY_DATE DATE NOT NULL, CMN_LAST_UPDT_DATE DATE NOT NULL, CMN_ENTRY_USER_NO NUMBER(11), CMN_ENTRY_USER_TYPE NUMBER(1), CMN_LAST_UPDT_USER_NO NUMBER(11), CMN_LAST_UPDT_USER_TYPE NUMBER(1), CONSTRAINT SHR_FILE_PK PRIMARY KEY (FILE_ID));
--DROP TABLE SHT_ADMIN_CSV_HST;
CREATE TABLE SHT_ADMIN_CSV_HST (ADMIN_CSV_HST_ID NUMBER(11) NOT NULL, ADMIN_ID NUMBER(11) NOT NULL, ADMIN_CSV_HST_TYPE VARCHAR2(200) NOT NULL, CMN_DELETE_FLAG NUMBER(1) NOT NULL, CMN_ENTRY_DATE DATE NOT NULL, CMN_LAST_UPDT_DATE DATE NOT NULL, CMN_ENTRY_USER_NO NUMBER(11), CMN_ENTRY_USER_TYPE NUMBER(1), CMN_LAST_UPDT_USER_NO NUMBER(11), CMN_LAST_UPDT_USER_TYPE NUMBER(1), CONSTRAINT SHT_ADMIN_CSV_HST_PK PRIMARY KEY (ADMIN_CSV_HST_ID));
--DROP TABLE SHT_ADMIN_LOG;
CREATE TABLE SHT_ADMIN_LOG (ADMIN_LOG_ID NUMBER(11) NOT NULL, ADMIN_ID VARCHAR2(100) NOT NULL, ADMIN_LOG_TYPE NUMBER(1), ADMIN_LOG_TITLE NUMBER(1), ADMIN_LOG_CONT VARCHAR2(1000), CMN_DELETE_FLAG NUMBER(1) NOT NULL, CMN_ENTRY_DATE DATE NOT NULL, CMN_LAST_UPDT_DATE DATE NOT NULL, CMN_ENTRY_USER_NO NUMBER(11), CMN_ENTRY_USER_TYPE NUMBER(1), CMN_LAST_UPDT_USER_NO NUMBER(11), CMN_LAST_UPDT_USER_TYPE NUMBER(1), CONSTRAINT SHT_ADMIN_LOG_PK PRIMARY KEY (ADMIN_LOG_ID));
--DROP TABLE SHT_MEMO_QA;
CREATE TABLE SHT_MEMO_QA (MEMO_ID NUMBER(11) NOT NULL, QA_ID NUMBER(11) NOT NULL, MEMO_CONT VARCHAR2(1000) NOT NULL, ADMIN_ID NUMBER(11) NOT NULL, CMN_DELETE_FLAG NUMBER(1) NOT NULL, CMN_ENTRY_DATE DATE NOT NULL, CMN_LAST_UPDT_DATE DATE NOT NULL, CMN_ENTRY_USER_NO NUMBER(11), CMN_ENTRY_USER_TYPE NUMBER(1), CMN_LAST_UPDT_USER_NO NUMBER(11), CMN_LAST_UPDT_USER_TYPE NUMBER(1), CONSTRAINT SHT_MEMO_QA_PK PRIMARY KEY (MEMO_ID));
--DROP TABLE SHT_MSG_SMS;
--CREATE TABLE SHT_MSG_SMS (SHT_MSG_SMS_ID NUMBER(11) NOT NULL, SHT_MSG_SMS_PHONE NUMBER(11) NOT NULL, SHT_MSG_SMS_CONTENT VARCHAR2(500) NOT NULL, SHT_MSG_SMS_STATUS INTEGER NOT NULL, SHT_MSG_SMS_TYPE INTEGER NOT NULL, CMN_DELETE_FLAG NUMBER(1) NOT NULL, CMN_ENTRY_DATE DATE NOT NULL, CMN_LAST_UPDT_DATE DATE NOT NULL, CMN_ENTRY_USER_NO NUMBER(11), CMN_ENTRY_USER_TYPE NUMBER(1), CMN_LAST_UPDT_USER_NO NUMBER(11), CONSTRAINT SHT_MSG_SMS_PK PRIMARY KEY (SHT_MSG_SMS_ID));
--DROP TABLE SHT_QA;
CREATE TABLE SHT_QA (QA_ID NUMBER(11) NOT NULL, USER_ID NUMBER(11) NOT NULL, QA_TYPE NUMBER(2) NOT NULL, QA_STATUS NUMBER(1) NOT NULL, CMN_DELETE_FLAG NUMBER(1) NOT NULL, CMN_ENTRY_DATE DATE NOT NULL, CMN_LAST_UPDT_DATE DATE NOT NULL, CMN_ENTRY_USER_NO NUMBER(11), CMN_ENTRY_USER_TYPE NUMBER(1), CMN_LAST_UPDT_USER_NO NUMBER(11), CMN_LAST_UPDT_USER_TYPE NUMBER(1), CONSTRAINT SHT_QA_PK PRIMARY KEY (QA_ID));
--DROP TABLE SHT_TALK_PURC;
CREATE TABLE SHT_TALK_PURC (TALK_PURC_ID NUMBER(11) NOT NULL, TALK_PURC_POST_ID NUMBER(11) NOT NULL, TALK_PURC_PART_ID NUMBER(11) NOT NULL, TALK_PURC_STATUS NUMBER(1) NOT NULL, OWNER_BLCK_FLAG NUMBER(1), PART_BLCK_FLAG NUMBER(1), CMN_DELETE_FLAG NUMBER(1) NOT NULL, CMN_ENTRY_DATE DATE NOT NULL, CMN_LAST_UPDT_DATE DATE NOT NULL, CMN_ENTRY_USER_NO NUMBER(11), CMN_ENTRY_USER_TYPE NUMBER(1), CMN_LAST_UPDT_USER_NO NUMBER(11), CMN_LAST_UPDT_USER_TYPE NUMBER(1), CONSTRAINT SHT_TALK_PURC_PK PRIMARY KEY (TALK_PURC_ID));
--DROP TABLE SHT_TALK_PURC_MSG;
CREATE TABLE SHT_TALK_PURC_MSG (TALK_PURC_MSG_ID NUMBER(11) NOT NULL, TALK_PURC_ID NUMBER(11) NOT NULL, TALK_PURC_MSG_CONT VARCHAR2(1000) NOT NULL, TALK_PURC_MSG_STATUS NUMBER(1) NOT NULL, CMN_DELETE_FLAG NUMBER(1) NOT NULL, CMN_ENTRY_DATE DATE NOT NULL, CMN_LAST_UPDT_DATE DATE NOT NULL, CMN_ENTRY_USER_NO NUMBER(11), CMN_ENTRY_USER_TYPE NUMBER(1), CMN_LAST_UPDT_USER_NO NUMBER(11), CMN_LAST_UPDT_USER_TYPE NUMBER(1), CONSTRAINT SHT_TALK_PURC_MSG_PK PRIMARY KEY (TALK_PURC_MSG_ID));
--DROP TABLE SHT_TALK_QA;
CREATE TABLE SHT_TALK_QA (TALK_QA_ID NUMBER(11) NOT NULL, QA_ID NUMBER(11) NOT NULL, TALK_QA_MSG VARCHAR2(1000) NOT NULL, ADMIN_ID NUMBER(11) NOT NULL, CMN_DELETE_FLAG NUMBER(1) NOT NULL, CMN_ENTRY_DATE DATE NOT NULL, CMN_LAST_UPDT_DATE DATE NOT NULL, CMN_ENTRY_USER_NO NUMBER(11), CMN_ENTRY_USER_TYPE NUMBER(1), CMN_LAST_UPDT_USER_NO NUMBER(11), CMN_LAST_UPDT_USER_TYPE NUMBER(1), CONSTRAINT SHT_TALK_QA_PK PRIMARY KEY (TALK_QA_ID));
--DROP TABLE SHT_USER_FVRT;
CREATE TABLE SHT_USER_FVRT (USER_FVRT_ID NUMBER(11) NOT NULL, POST_ID NUMBER(11) NOT NULL, USER_ID NUMBER(11) NOT NULL, USER_FVRT_STATUS NUMBER(1) NOT NULL, CMN_DELETE_FLAG NUMBER(1) NOT NULL, CMN_ENTRY_DATE DATE NOT NULL, CMN_LAST_UPDT_DATE DATE NOT NULL, CMN_ENTRY_USER_NO NUMBER(11), CMN_ENTRY_USER_TYPE NUMBER(1), CMN_LAST_UPDT_USER_NO NUMBER(11), CMN_LAST_UPDT_USER_TYPE NUMBER(1), CONSTRAINT SHT_USER_FVRT_PK PRIMARY KEY (USER_FVRT_ID));
--DROP TABLE SHT_USER_REV;
CREATE TABLE SHT_USER_REV (USER_REV_ID NUMBER(11) NOT NULL, TALK_PURC_ID NUMBER(11) NOT NULL, USER_REV_TYPE NUMBER(1) NOT NULL, USER_REV_RATE NUMBER(1) NOT NULL, USER_REV_CONT VARCHAR2(1000) NOT NULL, CMN_DELETE_FLAG NUMBER(1) NOT NULL, CMN_ENTRY_DATE DATE NOT NULL, CMN_LAST_UPDT_DATE DATE NOT NULL, CMN_ENTRY_USER_NO NUMBER(11), CMN_ENTRY_USER_TYPE NUMBER(1), CMN_LAST_UPDT_USER_NO NUMBER(11), CMN_LAST_UPDT_USER_TYPE NUMBER(1), CONSTRAINT SHT_USER_REV_PK PRIMARY KEY (USER_REV_ID));
--DROP TABLE SHT_USER_RPRT;
CREATE TABLE SHT_USER_RPRT (USER_RPRT_ID NUMBER(11) NOT NULL, POST_ID NUMBER(11) NOT NULL, USER_ID NUMBER(11) NOT NULL, USER_RPRT_TYPE NUMBER(2) NOT NULL, USER_RPRT_CONT VARCHAR2(1000), CMN_DELETE_FLAG NUMBER(1) NOT NULL, CMN_ENTRY_DATE DATE NOT NULL, CMN_LAST_UPDT_DATE DATE NOT NULL, CMN_ENTRY_USER_NO NUMBER(11), CMN_ENTRY_USER_TYPE NUMBER(1), CMN_LAST_UPDT_USER_NO NUMBER(11), CMN_LAST_UPDT_USER_TYPE NUMBER(1), CONSTRAINT SHT_USER_RPRT_PK PRIMARY KEY (USER_RPRT_ID));
----DROP TABLE SHT_USER_TOKEN;
--CREATE TABLE SHT_USER_TOKEN (USER_ID NUMBER(11) NOT NULL, TOKEN_ID VARCHAR2(255) NOT NULL, TOKEN_TYPE NUMBER(2) NOT NULL, TOKEN_EXPIRE_IN NUMBER(6) NOT NULL, CMN_DELETE_FLAG NUMBER(1) NOT NULL, CMN_ENTRY_DATE DATE NOT NULL, CMN_LAST_UPDT_DATE DATE NOT NULL, CMN_ENTRY_USER_NO NUMBER(11), CMN_ENTRY_USER_TYPE NUMBER(1), CMN_LAST_UPDT_USER_NO NUMBER(11), CMN_LAST_UPDT_USER_TYPE NUMBER(1), CONSTRAINT SHT_USER_TOKEN_PK PRIMARY KEY (TOKEN_ID));