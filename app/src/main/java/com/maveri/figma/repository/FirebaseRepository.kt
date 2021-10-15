package com.maveri.figma.repository;

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firebaseFirestore:FirebaseFirestore,
    private val firebaseAuth:FirebaseAuth
) {

    companion object {
        const val TAG = "FirebaseRepository"
    }

    private val documentsName = arrayOf("StreetName", "LocationsName", "PhotosName")

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

    fun updateStreetName(name: String, userId: String): Completable {
        return Completable.create { emitter ->
            firebaseAuth.currentUser?.let {
                firebaseFirestore.collection(userId).document(documentsName[0]).get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            firebaseFirestore.collection(userId).document(documentsName[0]).update("name", name)
                                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                                .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
                            emitter.onComplete()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "get failed with ", exception)
                    }
            }
        }
    }

    fun setStreetInfo() : Single<String> {
        return Single.create{ emitter ->
            firebaseAuth.currentUser?.let {
                val userId = FieldValue.serverTimestamp().toString()
                firebaseFirestore.runBatch { batch ->
                    batch.set(  firebaseFirestore.collection(userId).document(documentsName[0]), hashMapOf("name" to "Улицы"))
                    batch.set(  firebaseFirestore.collection(userId).document(documentsName[1]), HashMap<String, String>())
                    batch.set(  firebaseFirestore.collection(userId).document(documentsName[2]), HashMap<String, String>())

                }.addOnCompleteListener {
                    Log.d(TAG, "DocumentSnapshot successfully created!")
                    emitter.onSuccess(userId)
                }
                    .addOnFailureListener{
                        e -> Log.w(TAG, "Error updating document", e)
                        emitter.onError(e)
                    }
            }
        }
    }
}
