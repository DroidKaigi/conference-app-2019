package confsched2018.droidkaigi.github.io.dispatcher

import com.nhaarman.mockitokotlin2.mock
import io.github.droidkaigi.confsched2019.model.Action
import io.github.droidkaigi.confsched2019.model.Session
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.map
import kotlinx.coroutines.experimental.channels.take
import kotlinx.coroutines.experimental.channels.toList
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class DispatcherTest {
    @Test
    fun sendAndReceive() {
        val sessions = listOf<Session>(mock())
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

    @Test
    fun sendAndMultipleReceive() {
        val sessions = listOf<Session>(mock())
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

    @Test
    fun multipleSendAndReceive() {
        val sessions1 = listOf<Session>(mock())
        val sessions2 = listOf<Session>(mock())
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
            assertThat(allSessionLoaded1.await().map { it.sessions }, `is`(listOf(sessions1, sessions2)))
        }
    }

    @Test
    fun multipleSendAndMultipleReceive() {
        val sessions1 = listOf<Session>(mock(name = "session1"))
        val sessions2 = listOf<Session>(mock(name = "session2"))
        val dispatcher = Dispatcher()


        runBlocking {
            val allSessionLoaded1 = async {
                dispatcher.subscribe<Action.AllSessionLoaded>().map { println("map1:" + it);it }.take(2).toList()
            }
            val allSessionLoaded2 = async {
                dispatcher.subscribe<Action.AllSessionLoaded>().map { println("map2:" + it);it }.take(2).toList()
            }
            launch {
                dispatcher.send(Action.AllSessionLoaded(sessions1))
            }
            launch {
                dispatcher.send(Action.AllSessionLoaded(sessions2))
            }
            assertThat(allSessionLoaded1.await().map { it.sessions }, hasItems(sessions1, sessions2))
            assertThat(allSessionLoaded2.await().map { it.sessions }, hasItems(sessions1, sessions2))
        }
    }
}
