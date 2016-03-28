package com.ticketmaster.fingerprintdemo;

import android.annotation.TargetApi;
import android.os.Build.VERSION_CODES;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class CryptoHelper {

    private static final String TAG = CryptoHelper.class.getSimpleName();
    private static final String KEYSTORE = "AndroidKeyStore";
    private static final String KEY_NAME = "my_key";
    private static final String SECRET_MESSAGE = "Very secret message";

    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;

    public CryptoHelper() {
        initCrypto();
    }

    public static void clearKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE);
            keyStore.load(null);
            keyStore.deleteEntry(KEY_NAME);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(VERSION_CODES.M)
    private void initCrypto() {
        try {
            keyStore = KeyStore.getInstance(KEYSTORE);
            keyStore.load(null);
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE);
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (KeyStoreException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (CertificateException | IOException e) {
            Log.e(TAG, "Failed to init crypto. Failed to load keystore." + e.getMessage());
        }
    }

    public boolean initCipher() {
        try {
            SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return true;
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            Log.e(TAG, "Failed to init cipher. " + e.getMessage());
        } catch (InvalidKeyException e) {
            Log.e(TAG, "Failed to init cipher. Key is invalid. " + e.getMessage());
        }
        return false;
    }

    public Cipher getCipher() {
        return cipher;
    }

    public boolean encryptStuff() {
        try {
            cipher.doFinal(SECRET_MESSAGE.getBytes());
            initCipher();
            return true;
        } catch (BadPaddingException | IllegalBlockSizeException | IllegalStateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            Log.e(TAG, throwable.getClass().getSimpleName() + ": Encrypt failed. " + throwable.getMessage());
        }
        return false;
    }

    @TargetApi(VERSION_CODES.M)
    public boolean createKey() {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_NAME, null);
            if (secretKey != null) return true;
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            // Try to generate a new key
        }
        try {
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            // Require the user to authenticate with a fingerprint to authorize every use
                            // of the key
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true)
                    .build());
            keyGenerator.generateKey();
            return true;
        } catch (IllegalStateException e) {
            // This happens when no fingerprints are registered.
            return false;
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

}
