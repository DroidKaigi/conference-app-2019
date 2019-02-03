package io.github.droidkaigi.confsched2019.item

import java.util.Arrays

interface EqualableContentsProvider {
    fun providerEqualableContents(): Array<*>

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    fun isTextHighlightNeedUpdate(): Boolean {
        return false
    }

    fun isSameContents(other: Any?): Boolean {
        other ?: return false
        if (other !is EqualableContentsProvider) return false
        if (other::class != this::class) return false
        if (isTextHighlightNeedUpdate()) return false
        if (other.isTextHighlightNeedUpdate()) return false
        return other.providerEqualableContents().contentDeepEquals(this.providerEqualableContents())
    }

    fun contentsHash(): Int {
        return Arrays.deepHashCode(arrayOf(this::class, this.providerEqualableContents()))
    }
}
