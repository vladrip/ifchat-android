package com.vladrip.ifchat.data.network

import android.os.Build
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    //10.0.2.2 for emulator, localhost for hardware. Will be changed to real url when server will be hosted
    //After connecting real device execute this command to forward server port:
    //adb reverse tcp:8080 tcp:8080
    //adb is located in C:\Users\{your user name}\AppData\Local\Android\Sdk\platform-tools
    private val BASE_URL =
        if (Build.PRODUCT.contains("sdk")) "http://10.0.2.2:8080/api/v1/" else "http://localhost:8080/api/v1/"

    @Provides
    @Singleton
    fun provideIFChatService(okHttpClient: OkHttpClient, gson: Gson): IFChatService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .build()
            .create(IFChatService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            //.addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(
                LocalDateTime::class.java,
                JsonDeserializer { json, _, _ -> LocalDateTime.parse(json.asString) },
            ).registerTypeAdapter(
                LocalDateTime::class.java,
                JsonSerializer<LocalDateTime> { dateTime, _, _ -> JsonPrimitive(dateTime.toString()) }
            ).create()
    }
}