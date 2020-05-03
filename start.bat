for /f "tokens=1" %%i in ('jps -m') do ( taskkill /F /PID %%i )
CD /D "C:\s3sync\target\"
start /min java -jar -Xmx4096M s3sync.jar >> log\s3sync-error.log

