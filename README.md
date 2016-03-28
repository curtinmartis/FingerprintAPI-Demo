## Android Fingerprint API Sample

### Overview

This repo contains a super basic app that demonstrates use of the Fingerprint API from Android Marshmallow (API 23). An AES encryption key is generated at app startup and stored in the Android `KeyStore`, and when a fingerprint is identified successfully the key is unlocked for a single use. If you attempt to use the key again, an exception will be thrown due to the user not being authenticated.

__Note:__ This project uses Otto for passing events around. It's fairly straightforward, but [see here](http://square.github.io/otto/) for more info.

#### FingerprintHelper

This class abstracts the following fingerprint-related functionality:
* Requesting the `USE_FINGERPRINT` permission (not needed currently, but it's there anyway)
* Checking for hardware compatibility and enrolled fingerprints by way of [`FingerprintManagerCompat`](http://developer.android.com/reference/android/support/v4/hardware/fingerprint/FingerprintManagerCompat.html)
* Scanning for fingerprints and cancelling a scan operation
* Handling [`AuthenticationCallback`](http://developer.android.com/reference/android/support/v4/hardware/fingerprint/FingerprintManagerCompat.AuthenticationCallback.html) events

#### CryptoHelper

`CryptoHelper` handles all cryptography logic, including:
* Generation of AES secret key and storage in the [`KeyStore`](http://developer.android.com/reference/java/security/KeyStore.html)
* Clearing a previously set key from the store
* Initialization of [`Cipher`](http://developer.android.com/reference/javax/crypto/Cipher.html) with the secret key
* Encrypting a basic string of text

For extra information about this demo project and how to use the Fingerprint API, see [this presentation](https://docs.google.com/presentation/d/1kmy_gJYYXBCm-kbb5H9LwwG7R2DeOno5T2L7Zofdllg/edit?usp=sharing).