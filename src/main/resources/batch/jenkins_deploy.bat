REM SET BUILD_ID=dontKillMe
SET PROJECT_NAME=himma
SET TOMCAT_HOME=D:\apache-tomcat-test
SET CATALINA_BASE=%TOMCAT_HOME%
SET CATALINA_HOME=%TOMCAT_HOME%
SET WAR_PATH=D:\Jenkins\workspace\%PROJECT_NAME%\target
SET APP_PATH=%TOMCAT_HOME%\APPS\%PROJECT_NAME%
SET APP_BACK=%TOMCAT_HOME%\BACK

SET HOUR="%time:~0,1%"
if %HOUR%==" " (
    SET TIMESTAMP=%date:~0,4%%date:~5,2%%date:~8,2%-0%time:~1,1%%time:~3,2%%time:~6,2%
)else (   
    SET TIMESTAMP=%date:~0,4%%date:~5,2%%date:~8,2%-%time:~0,2%%time:~3,2%%time:~6,2%
)

SET BACK_NAME=%PROJECT_NAME%_%TIMESTAMP%.war

IF EXIST %APP_PATH%\%PROJECT_NAME%.war (
	IF EXIST %APP_BACK%\%PROJECT_NAME%_last.war (
		DEL %APP_BACK%\%PROJECT_NAME%_last.war
	    ECHO %BACK_NAME%
	    ECHO delete file %APP_BACK%\%PROJECT_NAME%_last.war
	)
	MOVE %APP_PATH%\%PROJECT_NAME%.war %APP_BACK%\%PROJECT_NAME%_last.war
    ECHO backup file %APP_PATH%\%PROJECT_NAME%.war as %APP_BACK%\%PROJECT_NAME%_last.war
)

CALL %TOMCAT_HOME%\bin\catalina.bat stop -force
REN %WAR_PATH%\%PROJECT_NAME%-*.war %PROJECT_NAME%.war
XCOPY %WAR_PATH%\%PROJECT_NAME%.war %APP_PATH% /Y
CD %APP_PATH%
JAR xvf %PROJECT_NAME%.war

REM START "C:\Program Files\Internet Explorer\iexplore.exe" http://localhost:8180/index.do
REM START "C:\Program Files\Internet Explorer\iexplore.exe" http://localhost:8180/report/index.html
REM EXPLORER http://localhost:8180/index.do
REM EXPLORER http://localhost:8180/report/index.html

ECHO http://localhost:8180/index.do
ECHO http://localhost:8180/report/index.html

CMD /K "CALL %TOMCAT_HOME%\bin\catalina.bat start"
ECHO startup tomcat finished