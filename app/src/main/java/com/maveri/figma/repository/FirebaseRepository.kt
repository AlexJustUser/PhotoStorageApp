package com.maveri.figma.repository;

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.maveri.figma.model.Location
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseAuth: FirebaseAuth
) {

    companion object {
        const val TAG = "FirebaseRepository"
    }

    private val documentsName = arrayOf("StreetName", "LocationsName", "Photos")

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
                            firebaseFirestore.collection(userId).document(documentsName[0])
                                .update("name", name)
                                .addOnSuccessListener {
                                    Log.d(
                                        TAG,
                                        "DocumentSnapshot successfully updated!"
                                    )
                                }
                                .addOnFailureListener { e ->
                                    Log.w(
                                        TAG,
                                        "Error updating document",
                                        e
                                    )
                                }
                            emitter.onComplete()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "get failed with ", exception)
                    }
            }
        }
    }

    fun updateLocationName(locationId: String, locationName: String, userId: String): Completable {
        return Completable.create { emitter ->
            firebaseAuth.currentUser?.let {
                firebaseFirestore.collection(userId).document(documentsName[1])
                    .update(locationId, locationName)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully updated!")
                        emitter.onComplete()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating document", e)
                        emitter.onError(e)
                    }
            }
        }
    }

    fun setStreetInfo(): Single<String> {
        return Single.create { emitter ->
            firebaseAuth.currentUser?.let {
                val userId = FieldValue.serverTimestamp().toString()
                firebaseFirestore.runBatch { batch ->
                    batch.set(
                        firebaseFirestore.collection(userId).document(documentsName[0]),
                        hashMapOf("name" to "Улицы")
                    )
                    batch.set(
                        firebaseFirestore.collection(userId).document(documentsName[1]),
                        hashMapOf("1" to "Название локации")
                    )
                    batch.set(
                        firebaseFirestore.collection(userId).document(documentsName[1])
                            .collection(documentsName[2]).document("1"), HashMap<String, String>()
                    )

                }.addOnCompleteListener {
                    Log.d(TAG, "DocumentSnapshot successfully created!")
                    emitter.onSuccess(userId)
                }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating document", e)
                        emitter.onError(e)
                    }
            }
        }
    }

    fun addNewPhoto(userId: String, photoId: Uri, locationId: String): Completable {
        return Completable.create { emitter ->
            firebaseAuth.currentUser?.let {
                firebaseStorage.reference.child(photoId.toString().replace("/", ""))
                    .putFile(photoId)
                    .addOnCompleteListener {
                        Log.d(TAG, "DocumentSnapshot successfully created!")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating document", e)
                        emitter.onError(e)
                    }
                firebaseFirestore.collection(userId).document(documentsName[1])
                    .collection(documentsName[2])
                    .get()
                    .addOnSuccessListener { collection ->
                        if (collection.documents[locationId.toInt() - 1].data != null) {
                            firebaseFirestore.collection(userId).document(documentsName[1])
                                .collection(documentsName[2]).document(locationId)
                                .update(
                                    collection.documents[locationId.toInt() - 1].data?.size?.plus(1)
                                        .toString(),
                                    photoId.toString().replace("/", "")
                                ).addOnFailureListener { e ->
                                    Log.w(TAG, "Error updating document", e)
                                    emitter.onError(e)
                                }
                        } else {
                            firebaseFirestore.collection(userId).document(documentsName[1])
                                .collection(documentsName[2]).document(locationId)
                                .set(hashMapOf("1" to photoId.toString().replace("/", "")))
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error updating document", e)
                                    emitter.onError(e)
                                }
                        }
                    }.addOnCompleteListener {
                        Log.d(TAG, "DocumentSnapshot successfully created!")
                        emitter.onComplete()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating document", e)
                        emitter.onError(e)
                    }
            }
        }
    }

    fun addNewLocation(userId: String): Completable {
        return Completable.create { emitter ->
            firebaseAuth.currentUser?.let {
                firebaseFirestore.collection(userId).document(documentsName[1])
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            firebaseFirestore.collection(userId).document(documentsName[1]).update(
                                (document.data?.size?.plus(1)).toString(),
                                "Название локации"
                            )

                            firebaseFirestore.collection(userId).document(documentsName[1])
                                .collection(documentsName[2])
                                .document((document.data?.size?.plus(1)).toString())
                                .set(HashMap<String, String>())

                            Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        } else {
                            Log.d(TAG, "No such document")
                        }
                    }.addOnCompleteListener {
                        Log.d(TAG, "DocumentSnapshot successfully created!")
                        emitter.onComplete()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating document", e)
                        emitter.onError(e)
                    }
            }
        }
    }

    fun getLocationsInfo(userId: String): Single<Map<String, Any>> {
        return Single.create { emitter ->
            firebaseAuth.currentUser?.let {
                firebaseFirestore.collection(userId).document(documentsName[1])
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            emitter.onSuccess(document.data)
                            Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        } else {
                            Log.d(TAG, "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "get failed with ", exception)
                        emitter.onError(exception)
                    }
            }
        }
    }

    fun getPhotosInfo(userId: String): Single<List<DocumentSnapshot>> {
        return Single.create { emitter ->
            firebaseAuth.currentUser?.let {
                firebaseFirestore.collection(userId).document(documentsName[1])
                    .collection(documentsName[2])
                    .get()
                    .addOnSuccessListener { collection ->
                        if (collection != null) {
                            emitter.onSuccess(collection.documents)
                            Log.d(TAG, "DocumentSnapshot data: ${collection.documents}")
                        } else {
                            Log.d(TAG, "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "get failed with ", exception)
                        emitter.onError(exception)
                    }
            }
        }
    }

    fun getStreetName(userId: String): Single<String> {
        return Single.create { emitter ->
            firebaseAuth.currentUser?.let {
                firebaseFirestore.collection(userId).document(documentsName[0])
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            emitter.onSuccess(document.getString("name"))
                            Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        } else {
                            emitter.onSuccess("Error")
                            Log.d(TAG, "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "get failed with ", exception)
                        emitter.onError(exception)
                    }
            }
        }
    }
}
