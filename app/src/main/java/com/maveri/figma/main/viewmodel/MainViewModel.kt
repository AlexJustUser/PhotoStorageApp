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

    val viewState: MutableLiveData<MainViewState.State> = MutableLiveData()

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
            }else{
                getStreetName(users[0].firebaseId)
            }
        }
    }

    private fun getLocationsInfo(userId: String){
        firebaseRepository.getLocationsInfo(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<Map<String, Any>>() {

                override fun onSuccess(locations: Map<String, Any>) {
                    getPhotosInfo(userId, locations)
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, e.stackTraceToString())
                }
            })
    }

    private fun getPhotosInfo(userId: String, locations: Map<String, Any>){
        var locationItems: MutableList<String>(locations.)
        firebaseRepository.getPhotosInfo(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<Map<String, Any>>() {

                override fun onSuccess(photos: Map<String, Any>) {
                    locations.forEach{

                    }
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, e.stackTraceToString())
                }
            })
    }

    private fun getStreetName(userId: String) {
            firebaseRepository.getStreetName(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<String>() {

                    override fun onSuccess(streetName: String) {
                        viewState.value = MainViewState.State(streetName = streetName)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, e.stackTraceToString())
                    }
                })
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

    private fun setStreetInfo(){
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

    fun updateStreetName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = roomRepository.readAllUsers()[0].firebaseId
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
}