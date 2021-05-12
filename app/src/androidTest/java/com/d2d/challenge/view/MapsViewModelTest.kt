package com.d2d.challenge.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.d2d.challenge.CoroutineTestRule
import com.d2d.challenge.data.entity.Payload
import com.d2d.challenge.data.interactor.IServiceHelper
import com.d2d.challenge.data.repository.MapsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()


    @Mock
    private lateinit var iServiceHelper: IServiceHelper


    @Mock
    private lateinit var mapsRepository: MapsRepository

    private var payload: Payload? = null

    @Before
    fun setUp() {
        payload = Payload("Ride", null, null, "Event")
    }

    @Test
    fun testData() {
        coroutineTestRule.runBlockingTest {

            iServiceHelper.getSocket()

            doReturn(payload)
                .`when`(iServiceHelper).getSocket()

            var viewModel = MapsViewModel(mapsRepository)

            verify(iServiceHelper).getSocket()

        }

    }




}