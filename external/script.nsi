!include "MUI2.nsh"
!include LogicLib.nsh

;--------------------------------
;General

	;Name and file
	Name "Beact"
	Outfile "beact_setup.exe"
	 
	# define the directory to install to, the desktop in this case as specified  
	# by the predefined $DESKTOP variable
	InstallDir "$LocalAppdata\Beact"
	RequestExecutionLevel user

;--------------------------------
!define MUI_ICON "Logo_Icon_Installer.ico"
!define MUI_UNICON  "Logo_Icon_Installer.ico"
!define MUI_HEADERIMAGE
!define MUI_HEADERIMAGE_BITMAP "header_beact.bmp"

!define MUI_WELCOMEFINISHPAGE_BITMAP "img_welcome_beact.bmp"

# page components

ShowInstDetails Show


!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "..\LICENSE.md"
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_LANGUAGE "English"

 
# For removing Start Menu shortcut in Windows 7
RequestExecutionLevel user

Section "Start Menu Shortcut" SEC_ShortCut
SectionEnd

Section "Add to Path env variable" SEC_pathEnv
SectionEnd

# default section
Section
	 
	# read the value from the registry into the $0 register
    #ReadRegStr $0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" CurrentVersion
 
    # print the results in a popup message box
    #MessageBox MB_OK "version: $0"
	
	# define the output path for this file
	SetOutPath $INSTDIR
	 
	# define what to install and place it in the output path
	File ..\out\artifacts\Beact_kt_jar\Beact.kt.jar
	File beact.bat

	# create the uninstaller
	WriteUninstaller "$INSTDIR\uninstall.exe"

	${If} ${SectionIsSelected} ${SEC_ShortCut}
		# create a shortcut named "new shortcut" in the start menu programs directory
		# point the new shortcut at the program uninstaller
		CreateDirectory "$SMPROGRAMS\Beact"
		CreateShortcut "$SMPROGRAMS\Beact\beact.lnk" "$INSTDIR\beact.bat" "" "$INSTDIR\beact.bat" 0
		CreateShortCut "$SMPROGRAMS\Beact\beact_uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
	${EndIf}
	# Info Add/Remove programs
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Beact" \
					 "DisplayName" "Beact -- Beact Marker Language"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Beact" \
					 "UninstallString" "$\"$INSTDIR\uninstall.exe$\""
	
	 
	${If} ${SectionIsSelected} ${SEC_pathEnv}
    
		; Set back to HKCU
		EnVar::SetHKCU
			DetailPrint "EnVar::SetHKCU"
		  
		; Check for write access
		EnVar::Check "NULL" "NULL"
			Pop $0
			DetailPrint "EnVar::Check write access HKCU returned=|$0|"

		; Check if the 'temp' variable exists in EnVar::SetHKxx
		EnVar::Check "Path" "NULL"
			Pop $0
			DetailPrint "EnVar::Check returned=|$0|"

		EnVar::AddValue "Path" "$INSTDIR"
		  Pop $0
		  DetailPrint "EnVar::AddValue returned=|$0|"
	${EndIf}

SectionEnd


# uninstaller section start
Section "uninstall"
 
    # Remove the link from the start menu
    Delete "$SMPROGRAMS\beact_uninstaller.lnk"
	Delete "$SMPROGRAMS\Beact\*.*"
	RMDir "$SMPROGRAMS\Beact"
	
	DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Beact"
	

	; Set back to HKCU
	EnVar::SetHKCU
		DetailPrint "EnVar::SetHKCU"
	  
	; Check for write access
	EnVar::Check "NULL" "NULL"
		Pop $0
		DetailPrint "EnVar::Check write access HKCU returned=|$0|"
		
	; Delete a value from a variable
	EnVar::DeleteValue "Path" "$INSTDIR"
	  Pop $0
	  DetailPrint "EnVar::DeleteValue returned=|$0|"
  

	
	Delete "$INSTDIR\*.*"
    # Delete the uninstaller
    Delete $INSTDIR\uninstaller.exe
	
	 RMDir /r /REBOOTOK $INSTDIR
# uninstaller section end
SectionEnd

/* SectionGroup /e "Group 2"
Section /o "G2S1" SEC_G2S1
SectionEnd
Section "G2S2" SEC_G2S2
SectionEnd
SectionGroupEnd */

/*Section -Hidden
${If} ${SectionIsSelected} ${SEC_ShortCut}
    MessageBox mb_ok "G1S1 is selected"
${EndIf}
${If} ${SectionIsSelected} ${SEC_pathEnv}
    MessageBox mb_ok "G1S2 is selected"
${EndIf}
# Check the other sections here ...
SectionEnd*/