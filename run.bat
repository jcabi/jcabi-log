@echo "Starting..." > log

@set /a countfiles=0

:while 
	@call mvn clean test >> log 2>&1
	@set /a countfiles+=1
	@echo "*** LOOP FINISHED ***" %countfiles% >> log
	@findstr /n "FAILURE" log >> log
	@if %errorlevel% == 0 goto :leave
	@echo. >> log
	@echo. >> log
	@if %countfiles% leq 0 goto :while
	
:leave
@echo "Finishied." >> log

::FAILURE
