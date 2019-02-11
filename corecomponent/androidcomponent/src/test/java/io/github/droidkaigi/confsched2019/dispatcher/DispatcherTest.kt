package io.github.droidkaigi.confsched2019.dispatcher

import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.ext.CoroutinePlugin
import io.github.droidkaigi.confsched2019.model.SessionContents
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.channels.take
import kotlinx.coroutines.channels.takeWhile
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class DispatcherTest {
    @Before fun setUp() {
        val pseudoMainDispatcher = newSingleThreadContext("DispatcherTest")
        CoroutinePlugin.mainDispatcherHandler = { pseudoMainDispatcher }
    }

    @Test fun sendAndReceive_dispatch() {
        val sessionContents: SessionContents = mockk()
        val dispatcher = Dispatcher()

        runBlocking {
            val allSessionLoaded = async {
                dispatcher.subscribe<Action.SessionContentsLoaded>().receive()
            }
            val job = launch {
                dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents))
            }
            job.join()
            assertThat(allSessionLoaded.await().sessionContents, `is`(sessionContents))
        }
    }

    @Test fun sendAndReceive_launchAndDispatch() {
        val sessionContents: SessionContents = mockk()
        val dispatcher = Dispatcher()

        runBlocking {
            val allSessionLoaded = async {
                dispatcher.subscribe<Action.SessionContentsLoaded>().receive()
            }
            yield() // Make sure subscribe() is called before launchAndDispatch()
            dispatcher.launchAndDispatch(Action.SessionContentsLoaded(sessionContents))
            assertThat(allSessionLoaded.await().sessionContents, `is`(sessionContents))
        }
    }

    @Test fun sendAndMultipleReceive_dispatch() {
        val sessionContents: SessionContents = mockk()
        val dispatcher = Dispatcher()

        runBlocking {
            val allSessionLoaded1 = async {
                dispatcher.subscribe<Action.SessionContentsLoaded>().receive()
            }
            val allSessionLoaded2 = async {
                dispatcher.subscribe<Action.SessionContentsLoaded>().receive()
            }
            val job = launch {
                dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents))
            }
            job.join()
            assertThat(allSessionLoaded1.await().sessionContents, `is`(sessionContents))
            assertThat(allSessionLoaded2.await().sessionContents, `is`(sessionContents))
        }
    }

    @Test fun sendAndMultipleReceive_launchAndDispatch() {
        val sessionContents: SessionContents = mockk()
        val dispatcher = Dispatcher()

        runBlocking {
            val allSessionLoaded1 = async {
                dispatcher.subscribe<Action.SessionContentsLoaded>().receive()
            }
            val allSessionLoaded2 = async {
                dispatcher.subscribe<Action.SessionContentsLoaded>().receive()
            }
            yield() // Make sure subscribe() is called before launchAndDispatch()
            dispatcher.launchAndDispatch(Action.SessionContentsLoaded(sessionContents))
            assertThat(allSessionLoaded1.await().sessionContents, `is`(sessionContents))
            assertThat(allSessionLoaded2.await().sessionContents, `is`(sessionContents))
        }
    }

    @Test fun multipleSendAndReceive_dispatch() {
        val sessionContents1: SessionContents = mockk()
        val sessionContents2: SessionContents = mockk()
        val dispatcher = Dispatcher()

        runBlocking {
            val allSessionLoaded1 = async {
                dispatcher.subscribe<Action.SessionContentsLoaded>().take(2).toList()
            }
            launch {
                dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents1))
            }
            launch {
                dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents2))
            }
            assertThat(
                allSessionLoaded1.await().map { it.sessionContents },
                `is`(listOf(sessionContents1, sessionContents2))
            )
        }
    }

    @Test fun multipleSendAndReceive_launchAndDispatch() {
        val sessionContents1: SessionContents = mockk()
        val sessionContents2: SessionContents = mockk()
        val dispatcher = Dispatcher()

        runBlocking {
            val allSessionLoaded1 = async {
                dispatcher.subscribe<Action.SessionContentsLoaded>().take(2).toList()
            }
            yield() // Make sure subscribe() is called before launchAndDispatch()
            dispatcher.launchAndDispatch(Action.SessionContentsLoaded(sessionContents1))
            dispatcher.launchAndDispatch(Action.SessionContentsLoaded(sessionContents2))
            assertThat(
                allSessionLoaded1.await().map { it.sessionContents },
                `is`(listOf(sessionContents1, sessionContents2))
            )
        }
    }

    @Test fun multipleSendAndMultipleReceive_dispatch() {
        val sessionContents1: SessionContents = mockk()
        val sessionContents2: SessionContents = mockk()
        val dispatcher = Dispatcher()

        runBlocking {
            val allSessionLoaded1 = async {
                dispatcher.subscribe<Action.SessionContentsLoaded>().map {
                    it
                }.take(2).toList()
            }
            val allSessionLoaded2 = async {
                dispatcher.subscribe<Action.SessionContentsLoaded>().map {
                    it
                }.take(2).toList()
            }
            launch {
                dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents1))
            }
            launch {
                dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents2))
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

    @Test fun multipleSendAndMultipleReceive_launchAndDispatch() {
        val sessionContents1: SessionContents = mockk()
        val sessionContents2: SessionContents = mockk()
        val dispatcher = Dispatcher()

        runBlocking {
            val allSessionLoaded1 = async {
                dispatcher.subscribe<Action.SessionContentsLoaded>().map {
                    it
                }.take(2).toList()
            }
            val allSessionLoaded2 = async {
                dispatcher.subscribe<Action.SessionContentsLoaded>().map {
                    it
                }.take(2).toList()
            }
            yield() // Make sure subscribe() is called before launchAndDispatch()
            dispatcher.launchAndDispatch(Action.SessionContentsLoaded(sessionContents1))
            dispatcher.launchAndDispatch(Action.SessionContentsLoaded(sessionContents2))
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

    @Test fun parallelMultipleSendAndReceive_dispatch() {
        val numActions = 256
        val sessionContentsList = List<SessionContents>(numActions) { mockk() }
        val terminator: SessionContents = mockk()
        val dispatcher = Dispatcher()

        runBlocking {
            val allSessionLoaded1 = async {
                dispatcher.subscribe<Action.SessionContentsLoaded>()
                    .takeWhile { it.sessionContents != terminator }
                    .toList()
            }
            yield() // Make sure subscribe() is called before launchAndDispatch()
            val jobs = sessionContentsList.map { sessionContents ->
                launch(Dispatchers.IO) {
                    dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents))
                }
            }
            jobs.forEach { it.join() }
            dispatcher.dispatch(Action.SessionContentsLoaded(terminator))
            assertThat(
                allSessionLoaded1.await().map { it.sessionContents }.toSet(),
                `is`(sessionContentsList.toSet())
            )
        }
    }

    @Test fun parallelMultipleSendAndReceive_launchAndDispatch() {
        val numActions = 256
        val sessionContentsList = List<SessionContents>(numActions) { mockk() }
        val terminator: SessionContents = mockk()
        val dispatcher = Dispatcher()

        runBlocking {
            val allSessionLoaded1 = async {
                dispatcher.subscribe<Action.SessionContentsLoaded>()
                    .takeWhile { it.sessionContents != terminator }
                    .toList()
            }
            yield() // Make sure subscribe() is called before launchAndDispatch()
            val jobs = sessionContentsList.map { sessionContents ->
                launch(Dispatchers.IO) {
                    dispatcher.launchAndDispatch(Action.SessionContentsLoaded(sessionContents))
                }
            }
            jobs.forEach { it.join() }
            dispatcher.dispatch(Action.SessionContentsLoaded(terminator))
            assertThat(
                allSessionLoaded1.await().map { it.sessionContents }.toSet(),
                `is`(sessionContentsList.toSet())
            )
        }
    }
}
