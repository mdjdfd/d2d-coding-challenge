package com.d2d.challenge.core.di.module

import com.d2d.challenge.BuildConfig
import com.d2d.challenge.CoroutineTestRule
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
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import junit.framework.TestCase.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Singleton


@UninstallModules(ServiceModule::class)
@HiltAndroidTest
@ExperimentalCoroutinesApi
class ServiceModuleTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val coroutineTestRule = CoroutineTestRule()

    @Inject
    lateinit var actualSocketEndpoint: String

    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var request: Request

    @Inject
    lateinit var payloadDeserializer: PayloadDeserializer

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var webSocket: WebSocket

    @Inject
    lateinit var iSocketController: ISocketController

    @Inject
    lateinit var iServiceHelper: IServiceHelper


    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testSocketEndpoint() {
        val expectedSocketEndpoint = "wss://d2d-frontend-code-challenge.herokuapp.com"
        assertEquals(expectedSocketEndpoint, actualSocketEndpoint)
    }

    @Test
    fun testOkHttpClient() {
        assertNotNull(okHttpClient)
    }

    @Test
    fun testOkHttpRequest() {
        assertNotNull(request)
    }

    @Test
    fun testPayloadDeserializer() {
        assertNotNull(payloadDeserializer)
    }

    @Test
    fun testGson() {
        assertNotNull(gson)
    }

    @Test
    fun testCustomDeserializer() {
        val jsonString = "{\"event\":\"statusUpdated\",\"data\":\"inVehicle\"}"
        val jsonObject =
            "{\"event\":\"vehicleLocationUpdated\",\"data\":{\"address\":null,\"lng\":13.423404693603516,\"lat\":52.520046627821515}}"
        val jsonArray =
            "{\"event\":\"intermediateStopLocationsChanged\",\"data\":[{\"lat\":52.519485,\"lng\":13.388238,\"address\":\"Friedrichstraße Station\"}]}"

        val asStringPayload = gson.fromJson(jsonString, Payload::class.java)
        val asObjectPayload = gson.fromJson(jsonObject, Payload::class.java)
        val asArrayPayload = gson.fromJson(jsonArray, Payload::class.java)

        assertEquals("\"inVehicle\"", asStringPayload.statusRide)
        assertEquals(13.423404693603516, asObjectPayload.statusCarLocation?.lng)

        asArrayPayload.statusStopLocations?.let {
            assertFalse(asArrayPayload.statusStopLocations!!.isEmpty())
        }

        assertEquals("Friedrichstraße Station", asArrayPayload.statusStopLocations?.get(0)?.address)
    }


    @Test
    fun testSocketCallback() {
        assertNotNull(SocketCallback(gson))
    }


    @Test
    fun testWebSocket() {
        assertNotNull(webSocket)
    }

    @Test
    fun testSocketService()  {
        coroutineTestRule.runBlockingTest {
            assertNotNull(iSocketController)
        }
    }

    @Test
    fun testServiceHelper() {
        coroutineTestRule.runBlockingTest {
            assertNotNull(iServiceHelper)
        }
    }

}


@Module
@InstallIn(SingletonComponent::class)
object TestServiceModule {

    @Provides
    fun testProvideWebSocketEndpoint() = BuildConfig.WEB_SOCKET_ENDPOINT


    @Provides
    @Singleton
    fun testProvideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
    } else {
        OkHttpClient.Builder().build()
    }


    @Provides
    @Singleton
    fun testProvideOkHttpRequest(WEB_SOCKET_ENDPOINT: String) =
        Request.Builder().url(WEB_SOCKET_ENDPOINT).build()


    @Provides
    @Singleton
    fun testProvideGsonDeserializer() = PayloadDeserializer()


    @Singleton
    @Provides
    fun testProvideGson(gsonDeserializer: PayloadDeserializer): Gson = GsonBuilder()
        .registerTypeAdapter(Payload::class.java, gsonDeserializer)
        .create()


    @Provides
    @Singleton
    fun testProvideSocketCallback(gson: Gson) = SocketCallback(gson)


    @Provides
    @Singleton
    fun testProvideWebSocket(
        okHttpClient: OkHttpClient,
        request: Request,
        socketCallback: SocketCallback
    ): WebSocket = okHttpClient.newWebSocket(request, socketCallback)


    @Provides
    @Singleton
    fun testProvideSocketService(
        okHttpClient: OkHttpClient,
        socketCallback: SocketCallback,
        webSocket: WebSocket
    ): ISocketController = SocketControllerImpl(okHttpClient, socketCallback, webSocket)


    @Provides
    @Singleton
    fun testProvideSocket(serviceHelperImpl: ServiceHelperImpl): IServiceHelper = serviceHelperImpl
}