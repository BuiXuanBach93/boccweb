CREATE TABLE
    SHR_SYS_CONFIG
    (
        SYS_CONFIG_CODE VARCHAR2(500) NOT NULL,
        SYS_CONFIG_VALUE VARCHAR2(1),
        CONSTRAINT SHR_SYS_CONFIG_PK PRIMARY KEY (SYS_CONFIG_CODE)
    );

INSERT INTO SHR_SYS_CONFIG (SYS_CONFIG_CODE, SYS_CONFIG_VALUE) VALUES ('MAINTAIN_MODE', '0');

