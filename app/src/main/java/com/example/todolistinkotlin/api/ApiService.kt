package com.example.todolistinkotlin.api

import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
@POST("testurl")
suspend fun sendData(@Body body: HashMap<String,Any>):retrofit2.Response<Any>
}