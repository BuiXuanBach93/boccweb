#Requirement
- jdk 1.8
- server: tomcat 8.5
- database: oracle 11g NLS_NCHAR_CHARACTERSET : UTF-8

_Note:_<br/>
In the desciption, there are abbreviated words:
- {TOMCAT_HOME} - navigate to where tomcat was installed.
- {ENV} - specify kind of environments. There 4 environments such as: dev(development), test, staging, prod(prodcution).
#Installation<br/>
**Get code from git repository:<br/>**
`git clone https://hblab.backlogtool.com/git/BOC/web.git`

**Build project:**

- _Setup db:_ <br/>
    >`mvn clean` <br/>
    `mvn compile -P{ENV}` <br/>
    `mvn initialize flyway:migrate -P{ENV}`
    
 _Note:_
 Setup DB successfully before execute package source code.
- _Execute package source code:_<br/>    
    >`mvn clean install -P{ENV}`
    
#Deployment<br/>
 In case the process builds successfully, bocc.war file will be generated in the _target_ folder.<br/>
 Copy the bocc.war file into {TOMCAT_HOME}\webapps folder
 - Start tomcat:
 >`{TOMCAT_HOME}/bin/startup.sh`
 
 
  
 