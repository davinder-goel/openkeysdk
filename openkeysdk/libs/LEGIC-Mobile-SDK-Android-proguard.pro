# -----------------------------------------------------------------------------
# Copyright© 2017 LEGIC® Identsystems AG, CH-8623 Wetzikon
# Confidential. All rights reserved!
# -----------------------------------------------------------------------------
# Proguard Configuration for LEGIC Mobile SDK V2
#
# Please include this file to your Proguard Configuration with:
# -include LEGIC-Mobile-Android-SDK-proguard.pro
#
# -----------------------------------------------------------------------------

# NFC-HCE Service Interface
-keep public class com.legic.mobile.sdk.services.nfc.hce.NfcHceHandler
-keep public class com.legic.mobile.sdk.services.nfc.hce.NfcHceExchange

# Legacy Deployment Data Handling (SDK V1.x.x.x)
-keep,includedescriptorclasses class com.idconnect.** {*;}

# Remove notes releated to the LEGIC Mobile SDK 
-dontnote com.legic.mobile.sdk.**
