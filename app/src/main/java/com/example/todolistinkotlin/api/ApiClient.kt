package com.example.todolistinkotlin.api

import android.util.Base64
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object ApiClient {
    var retrofit:Retrofit? = null
    val callApi= getClient().create(ApiService::class.java)
    fun getClient(): Retrofit {
        val client: OkHttpClient.Builder = OkHttpClient.Builder()
            .addInterceptor(EncryptionInterceptor())
            .connectTimeout(70, TimeUnit.SECONDS)
            .readTimeout(70, TimeUnit.SECONDS)

            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            client.interceptors().add(0, loggingInterceptor)

        if (ApiClient.retrofit == null) {
            ApiClient.retrofit = Retrofit.Builder()
                .baseUrl("http:\\google.com")
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return ApiClient.retrofit!!
    }
    private const val AES_KEY = "YourSecretKeyHere"
    private const val AES_IV = "YourInitializationVector"

    fun encrypt(input: String): String {
        val keySpec = SecretKeySpec(AES_KEY.toByteArray(), "AES")
        val ivSpec = IvParameterSpec(AES_IV.toByteArray())
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        val encrypted = cipher.doFinal(input.toByteArray())
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    fun decrypt(input: String): String {
        val keySpec = SecretKeySpec(AES_KEY.toByteArray(), "AES")
        val ivSpec = IvParameterSpec(AES_IV.toByteArray())
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val encryptedBytes = Base64.decode(input, Base64.DEFAULT)
        val decrypted = cipher.doFinal(encryptedBytes)
        return String(decrypted)
    }
}