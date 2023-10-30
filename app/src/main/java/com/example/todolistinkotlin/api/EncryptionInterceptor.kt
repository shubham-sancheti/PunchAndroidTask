package com.example.todolistinkotlin.api

import okhttp3.Interceptor
import okhttp3.Interceptor.*
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException


import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import android.util.Base64
import okhttp3.ResponseBody

class EncryptionInterceptor:Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Encrypt the request body
        val encryptedRequest = originalRequest.newBuilder()
            .method(originalRequest.method, originalRequest.body)
            .build()

        // Execute the encrypted request
        val encryptedResponse = chain.proceed(encryptedRequest)

        // Decrypt the response body
        val decryptedBody = decryptResponseBody(encryptedResponse.body!!)

        // Build a new response with the decrypted body
        return encryptedResponse.newBuilder().body(decryptedBody).build()
    }

    private fun decryptResponseBody(body: ResponseBody): ResponseBody {
        return try {
            val encryptedData = body.string()
            val decryptedData = ApiClient.decrypt(encryptedData)
            ResponseBody.create(body.contentType(), decryptedData)
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseBody.create(null, "")
        }
    }
}
