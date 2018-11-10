package io.github.droidkaigi.confsched2019.dispatcher

import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.model.Session
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
        val sessions = listOf<Session>(mockk())
        val dispatcher = Dispatcher()

        runBlocking {
            val allSessionLoaded = async {
                dispatcher.subscribe<Action.AllSessionLoaded>().receive()
            }
            val job = launch {
                dispatcher.send(Action.AllSessionLoaded(sessions))
            }
            job.join()
            assertThat(allSessionLoaded.await().sessions, `is`(sessions))
        }
    }

    @Test fun sendAndMultipleReceive() {
        val sessions = listOf<Session>(mockk())
        val dispatcher = Dispatcher()


        runBlocking {
            val allSessionLoaded1 = async {
                dispatcher.subscribe<Action.AllSessionLoaded>().receive()
            }
            val allSessionLoaded2 = async {
                dispatcher.subscribe<Action.AllSessionLoaded>().receive()
            }
            val job = launch {
                dispatcher.send(Action.AllSessionLoaded(sessions))
            }
            job.join()
            assertThat(allSessionLoaded1.await().sessions, `is`(sessions))
            assertThat(allSessionLoaded2.await().sessions, `is`(sessions))
        }
    }

    @Test fun multipleSendAndReceive() {
        val sessions1 = listOf<Session>(mockk())
        val sessions2 = listOf<Session>(mockk())
        val dispatcher = Dispatcher()


        runBlocking {
            val allSessionLoaded1 = async {
                dispatcher.subscribe<Action.AllSessionLoaded>().take(2).toList()
            }
            launch {
                dispatcher.send(Action.AllSessionLoaded(sessions1))
            }
            launch {
                dispatcher.send(Action.AllSessionLoaded(sessions2))
            }
            assertThat(allSessionLoaded1.await().map { it.sessions },
                `is`(listOf(sessions1, sessions2)))
        }
    }

    @Test fun multipleSendAndMultipleReceive() {
        val sessions1 = listOf<Session>(mockk(name = "session1"))
        val sessions2 = listOf<Session>(mockk(name = "session2"))
        val dispatcher = Dispatcher()


        runBlocking {
            val allSessionLoaded1 = async {
                dispatcher.subscribe<Action.AllSessionLoaded>().map {
                    println("map1:" + it);it
                }.take(2).toList()
            }
            val allSessionLoaded2 = async {
                dispatcher.subscribe<Action.AllSessionLoaded>().map {
                    println("map2:" + it);it
                }.take(2).toList()
            }
            launch {
                dispatcher.send(Action.AllSessionLoaded(sessions1))
            }
            launch {
                dispatcher.send(Action.AllSessionLoaded(sessions2))
            }
            assertThat(allSessionLoaded1.await().map { it.sessions },
                hasItems(sessions1, sessions2))
            assertThat(allSessionLoaded2.await().map { it.sessions },
                hasItems(sessions1, sessions2))
        }
    }
}
