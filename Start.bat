@echo off 
if "%1"=="h" goto begin 
mshta vbscript:createobject("wscript.shell").run("%~nx0 h",0)(window.close)&&exit 
:begin 
setlocal enabledelayedexpansion

REM ����ģ��·����ģ���б�
set MODULE_PATH=lib
set ADD_MODULES=javafx.controls,javafx.fxml,javafx.web

REM ���� classpath��bin + ���� lib �µ� jar
set CLASSPATH=bin
for %%f in (%MODULE_PATH%\*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%f
)

REM ������־
echo [INFO] ���� JavaFX Ӧ��...
echo [INFO] ʹ��ģ��·��: %MODULE_PATH%
echo [INFO] ʹ��ģ��: %ADD_MODULES%
echo [INFO] ʹ����·��: %CLASSPATH%

REM ��������
java --module-path %MODULE_PATH% --add-modules %ADD_MODULES% -cp "%CLASSPATH%" Main

REM ������
if errorlevel 1 (
    echo [ERROR] ����ʧ�ܣ����������Ƿ�������Main.class �Ƿ���ڣ��Լ� lib ���Ƿ���� gson �� jar��
) else (
    echo [INFO] �����ɹ���
)

endlocal
pause
