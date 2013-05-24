set dirname=%CD%
ECHO %dirname%
del .\RUNNING_PID
java -DapplyEvolutions.default=true -cp "%dirname%\bin\*;%dirname%\lib\*" play.core.server.NettyServer "%dirname%"
