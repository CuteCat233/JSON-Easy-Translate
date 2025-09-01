@echo off 
if "%1"=="h" goto begin 
mshta vbscript:createobject("wscript.shell").run("%~nx0 h",0)(window.close)&&exit 
:begin 
setlocal enabledelayedexpansion

REM 设置模块路径和模块列表
set MODULE_PATH=lib
set ADD_MODULES=javafx.controls,javafx.fxml,javafx.web

REM 构建 classpath：bin + 所有 lib 下的 jar
set CLASSPATH=bin
for %%f in (%MODULE_PATH%\*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%f
)

REM 启动日志
echo [INFO] 启动 JavaFX 应用...
echo [INFO] 使用模块路径: %MODULE_PATH%
echo [INFO] 使用模块: %ADD_MODULES%
echo [INFO] 使用类路径: %CLASSPATH%

REM 启动程序
java --module-path %MODULE_PATH% --add-modules %ADD_MODULES% -cp "%CLASSPATH%" Main

REM 错误处理
if errorlevel 1 (
    echo [ERROR] 启动失败，请检查依赖是否完整，Main.class 是否存在，以及 lib 中是否包含 gson 等 jar。
) else (
    echo [INFO] 启动成功。
)

endlocal
pause
