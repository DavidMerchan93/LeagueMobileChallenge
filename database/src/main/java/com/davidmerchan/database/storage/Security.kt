package com.davidmerchan.database.storage

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import java.util.Base64

class Security(
    context: Context,
) {
    private val aead: Aead

    init {
        AeadConfig.register()
        aead =
            AndroidKeysetManager
                .Builder()
                .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
                .withSharedPref(context, "league_keyset", "league_keyset")
                .withMasterKeyUri("android-keystore://master_key_alias")
                .build()
                .keysetHandle
                .getPrimitive(RegistryConfiguration.get(), Aead::class.java)
    }

    fun encrypt(plain: String): String =
        Base64
            .getEncoder()
            .encodeToString(aead.encrypt(plain.toByteArray(), null))

    fun decrypt(cipherText: String): String = String(aead.decrypt(Base64.getDecoder().decode(cipherText), null))
}
