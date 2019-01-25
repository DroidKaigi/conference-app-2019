package io.github.droidkaigi.confsched2019.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class StaffResponseImpl(
    override val staffs: List<StaffItemResponseImpl>
) : StaffResponse
