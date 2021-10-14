package com.maveri.figma.repository;

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.rxjava3.core.Completable

import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firebaseFirestore:FirebaseFirestore,
    private val firebaseAuth:FirebaseAuth
) {

    fun signInAnonymously(): Completable {
        return Completable.create { emitter ->
            firebaseAuth.signInAnonymously()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        emitter.onComplete()
                    } else {
                        emitter.onError(it.exception)
                    }
                }
        }
    }

    fun sendStreetName(name: String): Completable {
        return Completable.create { emitter ->
            firebaseAuth.currentUser?.let {
                    firebaseFirestore.collection(name)
                        .document("").set("")
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                emitter.onComplete()
                            } else {
                                emitter.onError(it.exception)
                            }
                        }
            }
        }
    }
}