Summary: GLCD Emulator
Name: glcd-emulator
Version: 0.1.0_alpha_SNAPSHOT
Release: 1
License: GPL v3
Vendor: Rafael Ibasco
Prefix: /opt
Provides: glcd-emulator
Requires: ld-linux.so.2 libX11.so.6 libXext.so.6 libXi.so.6 libXrender.so.1 libXtst.so.6 libasound.so.2 libc.so.6 libdl.so.2 libgcc_s.so.1 libm.so.6 libpthread.so.0 libthread_db.so.1
Autoprov: 0
Autoreq: 0

#avoid ARCH subfolder
%define _rpmfilename %%{NAME}-%%{VERSION}-%%{RELEASE}.%%{ARCH}.rpm

#comment line below to enable effective jar compression
#it could easily get your package size from 40 to 15Mb but 
#build time will substantially increase and it may require unpack200/system java to install
%define __jar_repack %{nil}

%description
A cross-platform graphics LCD emulator for monochrome display devices.

%prep

%build

%install
rm -rf %{buildroot}
mkdir -p %{buildroot}/opt
cp -r %{_sourcedir}/GLCDEmulator %{buildroot}/opt

%files
%doc /opt/GLCDEmulator/app/license.txt
/opt/GLCDEmulator

%post


xdg-desktop-menu install --novendor /opt/GLCDEmulator/GLCDEmulator.desktop

if [ "false" = "true" ]; then
    cp /opt/GLCDEmulator/glcd-emulator.init /etc/init.d/glcd-emulator
    if [ -x "/etc/init.d/glcd-emulator" ]; then
        /sbin/chkconfig --add glcd-emulator
        if [ "false" = "true" ]; then
            /etc/init.d/glcd-emulator start
        fi
    fi
fi

%preun

xdg-desktop-menu uninstall --novendor /opt/GLCDEmulator/GLCDEmulator.desktop

if [ "false" = "true" ]; then
    if [ -x "/etc/init.d/glcd-emulator" ]; then
        if [ "true" = "true" ]; then
            /etc/init.d/glcd-emulator stop
        fi
        /sbin/chkconfig --del glcd-emulator
        rm -f /etc/init.d/glcd-emulator
    fi
fi

%clean
