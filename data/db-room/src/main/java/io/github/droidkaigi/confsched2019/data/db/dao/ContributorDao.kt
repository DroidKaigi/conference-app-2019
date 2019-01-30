package io.github.droidkaigi.confsched2019.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.droidkaigi.confsched2019.data.db.entity.ContributorEntityImpl

@Dao
abstract class ContributorDao {
    @Query("SELECT * FROM contributor")
    abstract suspend fun allContributorList(): List<ContributorEntityImpl>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(contributorList: List<ContributorEntityImpl>)

    @Query("DELETE FROM contributor")
    abstract fun deleteAll()
}
