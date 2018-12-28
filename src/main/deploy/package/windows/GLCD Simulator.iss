;This file will be executed next to the application bundle image
;I.e. current directory will contain folder GLCD Simulator with application files
[Setup]
AppId=com.ibasco.glcdemulator
AppName=GLCD Simulator
AppVersion=1.0.0-alpha-SNAPSHOT
AppVerName=GLCD Simulator
AppPublisher=Rafael Luis Ibasco
AppComments=A cross-platform Graphics LCD emulator designed for embedded devices and single board computers
AppCopyright=GLCD Simulator Copyright (C) 2018 Rafael Luis Ibasco
AppPublisherURL=https://github.com/ribasco/glcd-emulator/issues
AppSupportURL=https://github.com/ribasco/glcd-emulator/wiki
AppUpdatesURL=https://github.com/ribasco/glcd-emulator/releases
DefaultDirName={localappdata}\GLCD Simulator
DisableStartupPrompt=Yes
DisableDirPage=No
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=Yes
DisableWelcomePage=Yes
DefaultGroupName=GLCD Simulator
;Optional License
LicenseFile=license.txt
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename=glcd-emulator-setup-amd64
Compression=lzma
SolidCompression=yes
PrivilegesRequired=lowest
SetupIconFile=GLCD Simulator\GLCD Simulator.ico
UninstallDisplayIcon={app}\GLCD Simulator.ico
UninstallDisplayName=GLCD Simulator
WizardImageStretch=No
WizardSmallImageFile=GLCD Simulator-setup-icon.bmp
ArchitecturesInstallIn64BitMode=x64


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "GLCD Simulator\GLCD Simulator.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "GLCD Simulator\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\GLCD Simulator"; Filename: "{app}\GLCD Simulator.exe"; IconFilename: "{app}\GLCD Simulator.ico"; Check: returnTrue()
Name: "{commondesktop}\GLCD Simulator"; Filename: "{app}\GLCD Simulator.exe";  IconFilename: "{app}\GLCD Simulator.ico"; Check: returnTrue()

[Run]
Filename: "{app}\GLCD Simulator.exe"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\GLCD Simulator.exe"; Description: "{cm:LaunchProgram,GLCD Simulator}"; Flags: nowait postinstall runascurrentuser skipifsilent; Check: returnTrue()
Filename: "{app}\GLCD Simulator.exe"; Parameters: "-install -svcName ""GLCD Simulator"" -svcDesc ""GLCD Simulator"" -mainExe ""GLCD Simulator.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\GLCD Simulator.exe "; Parameters: "-uninstall -svcName GLCD Simulator -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;  
