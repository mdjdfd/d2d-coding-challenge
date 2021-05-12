package com.d2d.challenge.core.di.module

import com.d2d.challenge.BuildConfig
import com.d2d.challenge.data.entity.Payload
import com.d2d.challenge.data.entity.PayloadDeserializer
import com.d2d.challenge.data.interactor.IServiceHelper
import com.d2d.challenge.data.interactor.ServiceHelperImpl
import com.d2d.challenge.data.socket.ISocketController
import com.d2d.challenge.data.socket.SocketCallback
import com.d2d.challenge.data.socket.SocketControllerImpl
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


/**
 * Singleton module contains all component classes and dependency provider.
 * Scope of the components throughout application. Scope can be modified to activity, fragment, service
 */

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {


    /**
     * provides web socket endpoint url from generated BuildConfig file
     * @return returns web socket endpoint instance
     */
    @Provides
    fun provideWebSocketEndpoint() = BuildConfig.WEB_SOCKET_ENDPOINT


    /**
     * Provides singleton OkHttpClient based on application built type. For debug built the Http intercept generates log for
     * http requests.
     * @return returns OkHttpClient instance
     */
    @Provides
    @Singleton
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
    } else {
        OkHttpClient.Builder().build()
    }


    /**
     * Provides singleton OkHttpRequest with the instance of socket endpoint url.
     * @param WEB_SOCKET_ENDPOINT socket endpoint string type
     * @return returns an instance of Request
     */
    @Provides
    @Singleton
    fun provideOkHttpRequest(WEB_SOCKET_ENDPOINT: String) =
        Request.Builder().url(WEB_SOCKET_ENDPOINT).build()


    /**
     * Provides singleton instance of custom gson deserializer
     * @return returns an instance of PayloadDeserializer
     */
    @Provides
    @Singleton
    fun provideGsonDeserializer() = PayloadDeserializer()


    /**
     * Provides singleton instance of Gson by registering adapter types
     * @param gsonDeserializer instance of PayloadDeserializer
     * @return returns an instance of Gson
     */
    @Singleton
    @Provides
    fun provideGson(gsonDeserializer: PayloadDeserializer): Gson = GsonBuilder()
        .registerTypeAdapter(Payload::class.java, gsonDeserializer)
        .create()


    /**
     * Provides singleton instance of SocketCallback passing through gson instance
     * @param gson instance of Gson
     * @return return an instance of SocketCallback
     */
    @Provides
    @Singleton
    fun provideSocketCallback(gson: Gson) = SocketCallback(gson)


    /**
     * Provides singleton instance of WebSocket with OkHttpClient, Request and SocketCallback
     * @param okHttpClient instance of OkHttpClient
     * @param request instance of Request
     * @param socketCallback instance of SocketCallback
     * @return returns an instance of WebSocket
     */
    @Provides
    @Singleton
    fun provideWebSocket(
        okHttpClient: OkHttpClient,
        request: Request,
        socketCallback: SocketCallback
    ): WebSocket = okHttpClient.newWebSocket(request, socketCallback)


    /**
     * Provides singleton instance of socket controller which implements socket connection and disconnection
     * functionality.
     * @param okHttpClient instance of OkHttpClient
     * @param socketCallback instance of SocketCallback
     * @param webSocket instance of WebSocket
     * @return returns an instance of ISocketController
     */
    @Provides
    @Singleton
    fun provideSocketService(
        okHttpClient: OkHttpClient,
        socketCallback: SocketCallback,
        webSocket: WebSocket
    ): ISocketController = SocketControllerImpl(okHttpClient, socketCallback, webSocket)


    /**
     * Provides singleton instance of service helper which helps to pass data to view layer.
     * @param serviceHelperImpl instance of serviceHelperImpl
     * @return returns an instance of IServiceHelper
     */
    @Provides
    @Singleton
    fun provideSocket(serviceHelperImpl: ServiceHelperImpl): IServiceHelper = serviceHelperImpl


}