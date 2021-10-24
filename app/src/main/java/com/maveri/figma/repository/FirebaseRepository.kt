package com.maveri.figma.repository;

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.EventListener
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
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
                firebaseStorage.reference.child(photoId.toString().replace("/", "")).putFile(photoId)
                    .addOnFailureListener{
                        emitter.onError(it)
                    }
                    .addOnCompleteListener{
                        firebaseStorage.reference.child(photoId.toString().replace("/", "")).downloadUrl
                            .addOnCompleteListener {
                                if(it.isComplete) {
                                    firebaseFirestore.collection(userId).document(documentsName[1]).collection(documentsName[2]).get()
                                        .addOnSuccessListener { collection ->
                                            if (!collection.documents[locationId.toInt() - 1].data.isNullOrEmpty()) {
                                                val keysValues = mutableListOf<Int>()
                                                collection.documents[locationId.toInt() - 1].data?.keys?.forEach{
                                                    keysValues.add(it.toInt())
                                                }
                                                firebaseFirestore.collection(userId).document(documentsName[1]).collection(documentsName[2]).document(locationId)
                                                    .update(
                                                        keysValues.maxOrNull()?.plus(1).toString(),
                                                        photoId.toString().replace("/", "").plus("*").plus(it.result.toString()))
                                                    .addOnFailureListener { e ->
                                                        emitter.onError(e)
                                                    }
                                            } else {
                                                firebaseFirestore.collection(userId).document(documentsName[1]).collection(documentsName[2]).document(locationId)
                                                    .set(hashMapOf("1" to photoId.toString().replace("/", "").plus("*").plus(it.result.toString())))
                                                    .addOnFailureListener { e ->
                                                        emitter.onError(e)
                                                    }
                                            }
                                        }
                                }
                            }
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
                            val keysValues = mutableListOf<Int>()
                            document.data?.keys?.forEach{
                                keysValues.add(it.toInt())
                            }
                            firebaseFirestore.collection(userId).document(documentsName[1]).update(
                                keysValues.maxOrNull()?.plus(1).toString(),
                                "Название локации"
                            )
                            firebaseFirestore.collection(userId).document(documentsName[1])
                                .collection(documentsName[2])
                                .document(keysValues.maxOrNull()?.plus(1).toString())
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

    fun deletePhoto(userId: String, locationId: String, listForDelete: MutableList<String?>): Completable {
        return Completable.create { emitter ->
            firebaseAuth.currentUser?.let {
                firebaseFirestore.collection(userId).document(documentsName[1]).collection(documentsName[2]).document(locationId)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val deletingList = mutableListOf<String?>()
                            val deletingIndexes = mutableListOf<String?>()
                            document.data?.values?.forEach{ doc->
                                listForDelete.forEach{
                                    if((doc as String).split("*")[1] == it){
                                        deletingList.add(doc)
                                    }
                                }
                            }
                            document.data?.forEach { doc ->
                                deletingList.forEach{
                                    if(doc.value == it){
                                        deletingIndexes.add(doc.key)
                                    }
                                }
                            }
                            deletingIndexes.forEach{
                                val updates = hashMapOf<String?, Any>(
                                    it to FieldValue.delete())
                                firebaseFirestore.collection(userId).document(documentsName[1]).collection(documentsName[2]).document(locationId)
                                    .update(updates).addOnFailureListener{ e ->
                                        Log.w(TAG, "Error updating document", e)
                                        emitter.onError(e)
                                    }
                            }
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

    fun deleteLocation(userId: String, locationId: String): Completable {
        return Completable.create { emitter ->
            firebaseAuth.currentUser?.let {
                firebaseFirestore.collection(userId).document(documentsName[1]).collection(documentsName[2]).document(locationId)
                    .delete()
                    .addOnCompleteListener {
                        Log.d(TAG, "DocumentSnapshot successfully created!")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating document", e)
                        emitter.onError(e)
                    }
                val updates = hashMapOf<String?, Any>(
                    locationId to FieldValue.delete())
                firebaseFirestore.collection(userId).document(documentsName[1]).update(updates)
                    .addOnCompleteListener {
                        Log.d(TAG, "DocumentSnapshot successfully created!")
                        emitter.onComplete()
                    }
                    .addOnFailureListener{ e ->
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

    fun checkUpdates(userId: String) : Observable<String?>{
        return Observable.create { emitter ->
            firebaseAuth.currentUser?.let {
                firebaseFirestore.collection(userId).document(documentsName[1]).collection(documentsName[2])
                    .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, e ->
                        emitter.onNext("")
                    }
            }
        }
    }
}
