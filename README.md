gswitchblade
============

Groovy wrapper for the Razer SwitchBlade UI

Razer provides a free SDK for their [SwitchBlade UI](http://www.razerzone.com/switchblade-ui) (essentially programmable LCD keys and a programmable LCD multi-touch touchpad) which comes on their Blade Pro laptop and their DeathStalker Ultimate keyboards.  This project leverages JNA to interface with the SDK dll and provide an easy to use Groovy interface.

Licensing
---------
This project is not affiliated with Razer.  It is released under the Apache 2 license.


Environment Setup
-----------------
In order to use this library, you must first download the [Razer SwitchBlade UI SDK](http://developer.razerzone.com/sbui/sbui-sdk/).  Once installed, there should be a RzSwitchbladeSDK2.dll file installed somewhere on your machine (C:\ProgramData\Razer\Switchblade\SDK in Windows 8.1).  You must ensure that the DLL is in your system PATH, which is usually handled by the installer.

Razer does not seem inclined to provide Linux or Windows 64-bit drivers.  Boo!  So, you must have a 32-bit JDK installed on your windows machine in order to interface with their DLL.  This can be downloaded from Oracle.  I have not tried JNA and the DLL from any other vendor.

Sample Code
-----------
Sample scripts are available in the test tree:

- FontSampler - Allows paging through Lorem Ipsum of various system fonts and sizes.
- GestureEmitter - Echos gesture events emitted by the touchpad.

Other Projects
--------------
This project is targetted more for standalone roll-your-own apps written by hell-bent Groovy/Java developers.  If you are looking to deploy on the Synapse platform, then you may be interested in the native C# library [SharpBlade](https://github.com/SharpBlade/SharpBlade).

Thanks
------
Thanks goes out to Adam Hellberg for providing the critical code for configuring a Windows message pump via JNA.  
