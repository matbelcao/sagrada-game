@ECHO off

SET ROOT_KEY="HKEY_CURRENT_USER"


FOR /f "skip=2 tokens=3" %%i in ('reg query HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\Nls\CodePage /v OEMCP') do set OEMCP=%%i 

echo Set default Codepage to UTF-8
reg add "%ROOT_KEY%\Software\Microsoft\Command Processor" /v Autorun /t REG_SZ /d "@chcp 65001>nul" /f