package com.vladrip.ifchat.api

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        try {
            val user = Firebase.auth.currentUser ?: return chain.proceed(request)
            val token = Tasks
                .await(user.getIdToken(false), 5, TimeUnit.SECONDS)
                .token

            request = request.newBuilder().addHeader("Authorization", "Bearer $token").build()
            return chain.proceed(request)
        } catch (e: Exception) {
            return chain.proceed(request)
        }
    }
}