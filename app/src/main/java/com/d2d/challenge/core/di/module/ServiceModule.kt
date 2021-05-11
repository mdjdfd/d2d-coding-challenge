package com.d2d.challenge.core.di.module

import com.d2d.challenge.BuildConfig
import com.d2d.challenge.data.entity.Payload
import com.d2d.challenge.data.entity.PayloadDeserializer
import com.d2d.challenge.data.interactor.IServiceHelper
import com.d2d.challenge.data.interactor.ServiceHelperImpl
import com.d2d.challenge.data.socket.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {


    @Provides
    fun provideWebSocketEndpoint() = BuildConfig.WEB_SOCKET_ENDPOINT


    @Provides
    @Singleton
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
    } else {
        OkHttpClient.Builder().build()
    }


    @Provides
    @Singleton
    fun provideOkHttpRequest(WEB_SOCKET_ENDPOINT: String) =
        Request.Builder().url(WEB_SOCKET_ENDPOINT).build()


    @Provides
    @Singleton
    fun provideGsonDeserializer() = PayloadDeserializer()


    @Singleton
    @Provides
    fun provideGson(gsonDeserializer: PayloadDeserializer): Gson = GsonBuilder()
        .registerTypeAdapter(Payload::class.java, gsonDeserializer)
        .create()


    @Provides
    @Singleton
    fun provideSocketCallback(gson: Gson) = SocketCallback(gson)


    @Provides
    @Singleton
    fun provideWebSocket(
        okHttpClient: OkHttpClient,
        request: Request,
        socketCallback: SocketCallback
    ): WebSocket = okHttpClient.newWebSocket(request, socketCallback)


    @Provides
    @Singleton
    fun provideSocketService(
        okHttpClient: OkHttpClient,
        socketCallback: SocketCallback,
        webSocket: WebSocket
    ): ISocketController = SocketControllerImpl(okHttpClient, socketCallback, webSocket)



    @Provides
    @Singleton
    fun provideSocket(serviceHelperImpl: ServiceHelperImpl): IServiceHelper = serviceHelperImpl


}