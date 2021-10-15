package com.maveri.figma.model

import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User)

    @Query("SELECT * FROM user_table ORDER BY id ASC" )
    fun readAllUsers() : MutableLiveData<List<User>>
}