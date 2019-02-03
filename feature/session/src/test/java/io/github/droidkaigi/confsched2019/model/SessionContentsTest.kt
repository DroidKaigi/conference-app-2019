package io.github.droidkaigi.confsched2019.model

import io.github.droidkaigi.confsched2019.dummyServiceSessionData
import io.github.droidkaigi.confsched2019.dummySpeakerData
import io.github.droidkaigi.confsched2019.dummySpeechSessionData
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Enclosed::class)
class SessionContentsTest {

    // SpeechSession Only TestCase

    @RunWith(Parameterized::class)
    class WhenSpeechSessionsFiltered(
        private val param: Param
    ) {
        companion object {
            private val session = dummySpeechSessionData()

            @JvmStatic
            @Parameterized.Parameters(name = "{0}")
            fun testParams() = listOf(
                Param.forSpeechSession(
                    target = "session.title.en",
                    session = session,
                    query = "SpeechSession",
                    resultSession = session
                ),
                Param.forSpeechSession(
                    target = "session.title.ja",
                    session = session,
                    query = "スピーチセッション",
                    resultSession = session
                ),
                Param.forSpeechSession(
                    target = "session.desc",
                    session = session,
                    query = "Dummy",
                    resultSession = session
                ),
                Param.forSpeechSession(
                    target = "session.speakers",
                    session = session,
                    query = "スピーカー",
                    resultSession = session
                ),
                Param.forSpeechSession(
                    title = "No session applicable to speech session filter",
                    session = session,
                    query = "empty",
                    resultSession = null
                )
            )
        }

        @Test fun sessionResult_forSpeechSeeion(): Unit = with(param) {
            assetEquals(sessionContents.search(query))
        }
    }

    // ServiceSession Only TestCase

    @RunWith(Parameterized::class)
    class WhenServiceSessionsFiltered(
        private val param: Param
    ) {
        companion object {
            private val session = dummyServiceSessionData()

            @JvmStatic
            @Parameterized.Parameters(name = "{0}")
            fun testParams() = listOf(
                Param.forServiceSession(
                    target = "session.title.en",
                    session = session,
                    query = "ServiceSession",
                    resultSession = session
                ),
                Param.forServiceSession(
                    target = "session.title.ja",
                    session = session,
                    query = "サービスセッション",
                    resultSession = session
                ),
                Param.forServiceSession(
                    title = "No session applicable to service session filter",
                    session = session,
                    query = "empty",
                    resultSession = null
                )
            )
        }

        @Test fun sessionResult_forServiceSeeion(): Unit = with(param) {
            assetEquals(sessionContents.search(query))
        }
    }

    // Speaker Only TestCase

    @RunWith(Parameterized::class)
    class WhenSpeakersFiltered(
        private val param: Param
    ) {
        companion object {
            private val speaker = dummySpeakerData()

            @JvmStatic
            @Parameterized.Parameters(name = "{0}")
            fun testParams() = listOf(
                Param.forSpeaker(
                    target = "speaker.name",
                    speaker = speaker,
                    query = "スピーカー",
                    resultSpeaker = speaker
                ),
                Param.forSpeaker(
                    target = "speaker.tagLine",
                    speaker = speaker,
                    query = "#session_speaker",
                    resultSpeaker = speaker
                ),
                Param.forSpeaker(
                    target = "speaker.bio",
                    speaker = speaker,
                    query = "Android",
                    resultSpeaker = speaker
                ),
                Param.forSpeaker(
                    target = "speaker.twitterUrl",
                    speaker = speaker,
                    query = "twitter",
                    resultSpeaker = speaker
                ),
                Param.forSpeaker(
                    target = "speaker.githubUrl",
                    speaker = speaker,
                    query = "github",
                    resultSpeaker = speaker
                ),
                Param.forSpeaker(
                    title = "No speaker applicable to speaker filter",
                    speaker = speaker,
                    query = "empty",
                    resultSpeaker = null
                )
            )
        }

        @Test fun sessionResult_forSpeaker(): Unit = with(param) {
            assetEquals(sessionContents.search(query))
        }
    }

    @RunWith(Parameterized::class)
    class WhenSessionsAndSpeakersFiltered(
        private val param: Param
    ) {
        companion object {
            private val sessions: List<Session> = listOf(
                dummySpeechSessionData(),
                dummyServiceSessionData()
            )

            private val speakers = listOf(
                dummySpeakerData()
            )

            @JvmStatic
            @Parameterized.Parameters(name = "{0}")
            fun testParams() = listOf(
                Param.forSessionsAndSpeakers(
                    target = "session.title.ja, speaker.name",
                    sessions = sessions,
                    speakers = speakers,
                    query = "セッション",
                    resultSessions = sessions,
                    resultSpeakers = speakers
                ),
                Param.forSessionsAndSpeakers(
                    title = "No session and speaker applicable to filter",
                    sessions = sessions,
                    speakers = speakers,
                    query = "empty",
                    resultSessions = null,
                    resultSpeakers = null
                )
            )
        }

        @Test fun sessionResult(): Unit = with(param) {
            assetEquals(sessionContents.search(query))
        }
    }
}

data class Param(
    val title: String,
    val target: String?,
    val sessionContents: SessionContents,
    val query: String,
    val expected: SearchResult
) {
    override fun toString(): String = if (target == null) title else "$title ($target in query)"

    fun assetEquals(
        actual: SearchResult
    ) {
        assertEquals(expected.sessions, actual.sessions)
        assertEquals(expected.speakers, actual.speakers)
        assertEquals(expected.query, actual.query)
    }

    companion object {

        fun forSpeechSession(
            title: String = "Pass the target session from the speech session filter",
            target: String? = null,
            session: Session,
            query: String,
            resultSession: Session?
        ) = forSession(
            title = title,
            target = target,
            session = session,
            query = query,
            resultSession = resultSession
        )

        fun forServiceSession(
            title: String = "Pass the target session from the service session filter",
            target: String? = null,
            session: Session,
            query: String,
            resultSession: Session?
        ) = forSession(
            title = title,
            target = target,
            session = session,
            query = query,
            resultSession = resultSession
        )

        fun forSpeaker(
            title: String = "Pass the target speaker from the speaker filter",
            target: String? = null,
            speaker: Speaker,
            query: String,
            resultSpeaker: Speaker?
        ) = Param(
            title = title,
            target = target,
            sessionContents = SessionContents.EMPTY.copy(
                speakers = listOf(speaker)
            ),
            query = query,
            expected = SearchResult(
                sessions = listOf(),
                speakers = if (resultSpeaker != null) listOf(resultSpeaker) else listOf(),
                query = query
            )
        )

        fun forSessionsAndSpeakers(
            title: String = "Pass the target sessions and speakers from the filter",
            target: String? = null,
            sessions: List<Session>,
            speakers: List<Speaker>,
            query: String,
            resultSessions: List<Session>?,
            resultSpeakers: List<Speaker>?
        ) = Param(
            title = title,
            target = target,
            sessionContents = SessionContents.EMPTY.copy(
                sessions = sessions,
                speakers = speakers
            ),
            query = query,
            expected = SearchResult(
                sessions = resultSessions ?: listOf(),
                speakers = resultSpeakers ?: listOf(),
                query = query
            )
        )

        private fun forSession(
            title: String,
            target: String?,
            session: Session,
            query: String,
            resultSession: Session?
        ) = Param(
            title = title,
            target = target,
            sessionContents = SessionContents.EMPTY.copy(
                sessions = listOf(session)
            ),
            query = query,
            expected = SearchResult(
                sessions = if (resultSession != null) listOf(resultSession) else listOf(),
                speakers = listOf(),
                query = query
            )
        )
    }
}


