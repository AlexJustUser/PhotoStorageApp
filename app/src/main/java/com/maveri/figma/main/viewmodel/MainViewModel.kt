package com.maveri.figma.main.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.maveri.figma.model.User
import com.maveri.figma.repository.FirebaseRepository
import com.maveri.figma.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableCompletableObserver
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(application: Application, private val firebaseRepository: FirebaseRepository, private val roomRepository: RoomRepository) :
    AndroidViewModel(application) {

    private val viewState: MutableLiveData<MainViewState.State> = MutableLiveData()

    companion object {
        const val TAG = "MainViewModel"
    }


    fun saveUserId(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            roomRepository.addUser(user)
        }
    }

    fun checkAuth() {
        viewModelScope.launch(Dispatchers.IO) {
            //roomRepository.delUsers()
            val users: List<User> = roomRepository.readAllUsers()
            if(users.isEmpty()){
                setStreetInfo()
            }
        }
    }

    fun signInAnonymously() {
        firebaseRepository.signInAnonymously()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    viewState.value =
                        MainViewState.State(authStatus = MainViewState.AuthFirebaseStatus.Success)
                }

                override fun onError(e: Throwable?) {
                    viewState.value =
                        MainViewState.State(authStatus = MainViewState.AuthFirebaseStatus.Error)
                }

            })
    }

    fun setStreetInfo(){
        firebaseRepository.setStreetInfo()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<String>() {

                override fun onSuccess(userId: String) {
                    val user: User = User(0, userId)
                    saveUserId(user)
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, e.stackTraceToString())
                }
            })
    }

    fun sendStreetName(name: String, userId: String) {
        firebaseRepository.updateStreetName(name, userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    Log.d(TAG, "DocumentSnapshot successfully updated!")
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, e.stackTraceToString())
                }

            })
    }
}