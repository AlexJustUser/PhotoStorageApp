package com.maveri.figma.repository;

import androidx.lifecycle.MutableLiveData
import com.maveri.figma.model.User
import com.maveri.figma.model.UserDao

import javax.inject.Inject

class RoomRepository @Inject constructor(private val userDao: UserDao) {

    suspend fun readAllUsers(): List<User>{
        return userDao.readAllUsers()
    }

    suspend fun addUser(user: User){
        userDao.addUser(user)
    }

    fun delUsers(){
        userDao.deleteAllUsers()
    }

}