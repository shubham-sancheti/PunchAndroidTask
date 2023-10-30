package com.example.todolistinkotlin

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object Analytics {
    fun Context.logEvent(eventName: String){
        val b = Bundle()
        b.putString("user_os", "Android")
        b.putString("time",System.currentTimeMillis().toString())
        FirebaseAnalytics.getInstance(this).logEvent(eventName,b)
    }
}