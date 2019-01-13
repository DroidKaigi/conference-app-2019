package io.github.droidkaigi.confsched2019.data.api.parameter

import kotlinx.serialization.Serializable

@Serializable
data class LangParameterImpl(override val name: String) : LangParameter
