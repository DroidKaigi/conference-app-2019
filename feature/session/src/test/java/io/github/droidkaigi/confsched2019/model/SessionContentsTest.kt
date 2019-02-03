package io.github.droidkaigi.confsched2019.model

import io.github.droidkaigi.confsched2019.dummyServiceSessionData
import io.github.droidkaigi.confsched2019.dummySpeakerData
import io.github.droidkaigi.confsched2019.dummySpeechSessionData
import org.junit.Assert.assertEquals;
import org.junit.Test

class SessionContentsTest {

    // Session TestCase

    @Test fun search_WhenSpeechSessionFilteredAndTitleEn() =
        search_WhenSpeechSessionFiltered("SpeechSession")

    @Test fun search_WhenSpeechSessionFilteredAndTitleJa() =
        search_WhenSpeechSessionFiltered("スピーチセッション")

    @Test fun search_WhenSpeechSessionFilteredAndDesc() =
        search_WhenSpeechSessionFiltered("Dummy")

    @Test fun search_WhenSpeechSessionFilteredAndSpeakers() =
        search_WhenSpeechSessionFiltered("スピーカー")

    @Test fun search_WhenSpeechSessionNotFiltered() =
        search_WhenSpeechSessionFiltered("empty", true)

    private fun search_WhenSpeechSessionFiltered(query: String, isEmptyResult: Boolean = false) =
        search_WhenSessionFiltered(query, dummySpeechSessionData(), isEmptyResult)

    @Test fun search_WhenServiceSessionAndTitleEn() =
        search_WhenServiceSessionFiltered("ServiceSession")

    @Test fun search_WhenServiceSessionAndTitleJa() =
        search_WhenServiceSessionFiltered("サービスセッション")

    @Test fun search_WhenServiceSessionNotFiltered() =
        search_WhenServiceSessionFiltered("empty", true)

    private fun search_WhenServiceSessionFiltered(query: String, isEmptyResult: Boolean = false) =
        search_WhenSessionFiltered(query, dummyServiceSessionData(), isEmptyResult)

    private fun search_WhenSessionFiltered(query: String, session: Session, isEmptyResult: Boolean = false) {
        val sessions = listOf(session)
        val sessionContents = SessionContents.EMPTY.copy(sessions = sessions)
        val searchResult = sessionContents.search(query)
        assertEquals(
            searchResult = searchResult,
            query = query,
            sessions = if (isEmptyResult) listOf() else sessions)
    }

    // Speaker TestCase

    @Test fun search_WhenSpeakerFilteredAndName() =
        search_WhenSpeakerFiltered("スピーカー")

    @Test fun search_WhenSpeakerFilteredAndTagLine() =
        search_WhenSpeakerFiltered("#session_speaker")

    @Test fun search_WhenSpeakerFilteredAndBio() =
        search_WhenSpeakerFiltered("Android")

    @Test fun search_WhenSpeakerFilteredAndGithubUrl() =
        search_WhenSpeakerFiltered("github")

    @Test fun search_WhenSpeakerFilteredAndTwitterUrl() =
        search_WhenSpeakerFiltered("twitter")

    @Test fun search_WhenSpeakerNotFiltered() =
        search_WhenSpeakerFiltered("empty", true)

    private fun search_WhenSpeakerFiltered(query: String, isEmptyResult: Boolean = false) {
        val speakers = listOf(dummySpeakerData())
        val sessionContents = SessionContents.EMPTY.copy(speakers = speakers)
        val searchResult = sessionContents.search(query)
        assertEquals(
            searchResult = searchResult,
            query = query,
            speakers = if (isEmptyResult) listOf() else speakers)
    }

    // Sessions + Speakers TestCase

    @Test fun search_WhenSessionsAndSpeakersFiltered() =
        search_WhenSessionsAndSpeakersFiltered("セッション")

    @Test fun search_WhenSessionsAndSpeakersNotFiltered() =
        search_WhenSessionsAndSpeakersFiltered("Empty", true)

    private fun search_WhenSessionsAndSpeakersFiltered(query: String, isEmptyResult: Boolean = false) {
        val sessions = listOf<Session>(dummySpeechSessionData(), dummyServiceSessionData())
        val speakers = listOf(dummySpeakerData())
        val sessionContents = SessionContents.EMPTY.copy(
            sessions = sessions,
            speakers = speakers)
        val searchResult = sessionContents.search(query)
        assertEquals(
            searchResult = searchResult,
            query = query,
            sessions = if (isEmptyResult) listOf() else sessions,
            speakers = if (isEmptyResult) listOf() else speakers)
    }

    private fun assertEquals(
        searchResult: SearchResult,
        query:String,
        sessions: List<Session> = listOf(),
        speakers: List<Speaker> = listOf()) {
        assertEquals(searchResult.sessions, sessions)
        assertEquals(searchResult.speakers, speakers)
        assertEquals(searchResult.query, query)
    }
}
