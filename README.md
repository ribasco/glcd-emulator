# Graphics LCD Simulator

Graphics LCD prototyping tool for hobbyists

[![HitCount](http://hits.dwyl.io/{username}/ribasco/glcd-emulator.svg)](http://hits.dwyl.io/{username}/ribasco/glcd-emulator)
[![Build Status](https://travis-ci.org/ribasco/glcd-emulator.svg?branch=master)](https://travis-ci.org/ribasco/glcd-emulator)
[![Build status](https://ci.appveyor.com/api/projects/status/720a6efdfw1hq7gi?svg=true)](https://ci.appveyor.com/project/ribasco34191/glcd-emulator)
[![License: LGPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0.en.html)

#### MAX7219 Simulation

![MAX7219](docs/images/main09.jpg)

#### ST7920 Simulation

![ST7920](docs/images/main.jpg)

## Demo

[![YouTube Demo](https://img.youtube.com/vi/dgUQQN6_eOM/0.jpg)](https://www.youtube.com/watch?v=dgUQQN6_eOM)

## Overview

The client can be configured to use either Serial or TCP/IP for data transmission.

### Arduino

The Arduino client extends from the [U8g2 class](https://github.com/olikraus/u8g2/blob/master/cppsrc/U8g2lib.h). It can then be used just like how it is used in any actual u8g2 development. Data is transmitted from the client to the simulator program (via Serial or TCP/IP)

![Workflow](docs/images/workflow.png)

## Requirements

- Java 8 or higher

## Client libraries

- [Arduino client](https://github.com/ribasco/glcd-emulator-client-arduino)
- [Java client (Using ucgdisplay)](https://github.com/ribasco/glcd-emulator-client-java)
- C/C++ (Coming soon)

## Project Resources

- [Release](https://github.com/ribasco/glcd-emulator/releases)
- [Snapshots](https://ci.appveyor.com/project/ribasco34191/glcd-emulator/build/artifacts)

## Installation

### Manual

Extract the archive `glcd-emulator-<version>-amd64.zip` or `glcd-emulator-<version>-amd64.tar.gz` to any directory and use the run script to start the application

### Installer

Execute any of the files below whatever matches your target operating system.

- Windows: `glcd-emulator-setup-amd64.exe`
- Linux (Debian): `glcd-emulator-<version>-amd64.deb`
- Mac OSX: `glcd-emulator-<version>-amd64.pkg`

## Features

### Cross-platform

Supports Windows, Mac OSX and Linux

### Client available for Arduino/C/C++ and Java clients

See [Client Libraries](#Client-libraries)

### Customizable look and feel

![Customizable look and feel](docs/images/main02.jpg)

### Font Browser

Browse through all available u8g2 fonts

![Font Browser](docs/images/main03.jpg)

### Supports over 40+ displays

Most displays are available and supported by the simulator, but some limitations may apply depending on the hardware used.

![Supports over 40+ displays](docs/images/main04.jpg)

### Screenshots

Take screenshots (Use F5 key) and save it as PNG image format

![Screenshots](docs/images/main08.png)

### Developer mode

Developer mode allows you to play around with the functions available in the u8g2 graphics library. You will be able to preview the output and inspect the generated instructions for the operation. Please note that this is not enabled by default, you can enable this by specifying the `-dev` application startup parameter.

![Developer mode](docs/images/main05.jpg)

![Developer mode](docs/images/main06.jpg)

### Profile Support

You can create profiles to store individual settings

![Profile Support](docs/images/main07.jpg)
