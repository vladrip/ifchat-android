package com.vladrip.ifchat.model.api

import android.os.Build
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    //10.0.2.2 for emulator, localhost for hardware. Will be changed to real url when server will be hosted
    private val BASE_URL =
        if (Build.PRODUCT.contains("sdk")) "http://10.0.2.2:8080/api/v1/" else "http://localhost:8080/api/v1/"

    @Provides
    @Singleton
    fun provideIFChatApi(): IFChatApi {
        val gson = GsonBuilder()
            .registerTypeAdapter(
                LocalDateTime::class.java,
                JsonDeserializer { json, _, _ -> LocalDateTime.parse(json.asString) }
            )
            .create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .build()
            .create(IFChatApi::class.java)
    }
}