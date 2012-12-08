for %%F in ("%filename%") do set "dirName=%%~dp"
ECHO %dirName%
java -DapplyEvolutions.default=true -cp "%dirName%\bin\*;%dirName%\lib\*" play.core.server.NettyServer "%dirName%"
