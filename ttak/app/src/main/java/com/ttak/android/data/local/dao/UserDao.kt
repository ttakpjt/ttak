package com.ttak.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ttak.android.domain.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE name LIKE '%' || :query || '%'")
    suspend fun searchUsers(query: String): List<User>

    @Insert
    suspend fun insertUser(user: User)
}