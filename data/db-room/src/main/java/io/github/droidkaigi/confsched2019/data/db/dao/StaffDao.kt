package io.github.droidkaigi.confsched2019.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.droidkaigi.confsched2019.data.db.entity.StaffEntityImpl

@Dao
abstract class StaffDao {
    @Query("SELECT * FROM staff")
    abstract suspend fun allStaffs(): List<StaffEntityImpl>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(staffs: List<StaffEntityImpl>)

    @Query("DELETE FROM staff")
    abstract fun deleteAll()
}
