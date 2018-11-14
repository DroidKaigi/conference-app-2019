package io.github.droidkaigi.confsched2019.dispatcher

import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.model.SessionContents
import io.mockk.mockk
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.channels.take
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class DispatcherTest {
    @Test fun sendAndReceive() {
        val sessionContents: SessionContents = mockk()
        val dispatcher = Dispatcher()

        runBlocking {
            val allSessionLoaded = async {
                dispatcher.subscribe<Action.AllSessionLoaded>().receive()
            }
            val job = launch {
                dispatcher.dispatch(Action.AllSessionLoaded(sessionContents))
            }
            job.join()
            assertThat(allSessionLoaded.await().sessionContents, `is`(sessionContents))
        }
    }

    @Test fun sendAndMultipleReceive() {
        val sessionContents: SessionContents = mockk()
        val dispatcher = Dispatcher()


        runBlocking {
            val allSessionLoaded1 = async {
                dispatcher.subscribe<Action.AllSessionLoaded>().receive()
            }
            val allSessionLoaded2 = async {
                dispatcher.subscribe<Action.AllSessionLoaded>().receive()
            }
            val job = launch {
                dispatcher.dispatch(Action.AllSessionLoaded(sessionContents))
            }
            job.join()
            assertThat(allSessionLoaded1.await().sessionContents, `is`(sessionContents))
            assertThat(allSessionLoaded2.await().sessionContents, `is`(sessionContents))
        }
    }

    @Test fun multipleSendAndReceive() {
        val sessionContents1: SessionContents = mockk()
        val sessionContents2: SessionContents = mockk()
        val dispatcher = Dispatcher()


        runBlocking {
            val allSessionLoaded1 = async {
                dispatcher.subscribe<Action.AllSessionLoaded>().take(2).toList()
            }
            launch {
                dispatcher.dispatch(Action.AllSessionLoaded(sessionContents1))
            }
            launch {
                dispatcher.dispatch(Action.AllSessionLoaded(sessionContents2))
            }
            assertThat(
                allSessionLoaded1.await().map { it.sessionContents },
                `is`(listOf(sessionContents1, sessionContents2))
            )
        }
    }

    @Test fun multipleSendAndMultipleReceive() {
        val sessionContents1: SessionContents = mockk()
        val sessionContents2: SessionContents = mockk()
        val dispatcher = Dispatcher()


        runBlocking {
            val allSessionLoaded1 = async {
                dispatcher.subscribe<Action.AllSessionLoaded>().map {
                    it
                }.take(2).toList()
            }
            val allSessionLoaded2 = async {
                dispatcher.subscribe<Action.AllSessionLoaded>().map {
                    it
                }.take(2).toList()
            }
            launch {
                dispatcher.dispatch(Action.AllSessionLoaded(sessionContents1))
            }
            launch {
                dispatcher.dispatch(Action.AllSessionLoaded(sessionContents2))
            }
            assertThat(
                allSessionLoaded1.await().map { it.sessionContents },
                hasItems(sessionContents1, sessionContents2)
            )
            assertThat(
                allSessionLoaded2.await().map { it.sessionContents },
                hasItems(sessionContents1, sessionContents2)
            )
        }
    }
}
