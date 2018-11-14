;This file will be executed next to the application bundle image
;I.e. current directory will contain folder GLCD Emulator with application files
[Setup]
AppId={{com.ibasco.glcdemulator}}
AppName=GLCD Emulator
AppVersion=0.1.0-alpha-SNAPSHOT
AppVerName=GLCD Emulator
AppPublisher=Rafael Ibasco
AppComments=GLCD Emulator
AppCopyright=GLCD Emulator Copyright (C) 2018 Rafael Luis Ibasco
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName={pf}\GLCD Emulator
DisableStartupPrompt=Yes
DisableDirPage=No
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=Yes
DisableWelcomePage=Yes
DefaultGroupName=GLCD Emulator
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename=glcd-emulator-setup-amd64
Compression=lzma
SolidCompression=yes
PrivilegesRequired=admin
SetupIconFile=GLCD Emulator\GLCD Emulator.ico
UninstallDisplayIcon={app}\GLCD Emulator.ico
UninstallDisplayName=GLCD Emulator
WizardImageStretch=No
WizardSmallImageFile=GLCD Emulator-setup-icon.bmp
ArchitecturesInstallIn64BitMode=x64


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "GLCD Emulator\GLCD Emulator.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "GLCD Emulator\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\GLCD Emulator"; Filename: "{app}\GLCD Emulator.exe"; IconFilename: "{app}\GLCD Emulator.ico"; Check: returnTrue()
Name: "{commondesktop}\GLCD Emulator"; Filename: "{app}\GLCD Emulator.exe";  IconFilename: "{app}\GLCD Emulator.ico"; Check: returnTrue()

[Run]
Filename: "{app}\GLCD Emulator.exe"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\GLCD Emulator.exe"; Description: "{cm:LaunchProgram,GLCD Emulator}"; Flags: nowait postinstall runascurrentuser skipifsilent; Check: returnTrue()
Filename: "{app}\GLCD Emulator.exe"; Parameters: "-install -svcName ""GLCD Emulator"" -svcDesc ""GLCD Emulator"" -mainExe ""GLCD Emulator.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\GLCD Emulator.exe "; Parameters: "-uninstall -svcName GLCD Emulator -stopOnUninstall"; Check: returnFalse()

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
