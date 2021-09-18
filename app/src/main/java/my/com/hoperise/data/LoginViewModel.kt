package my.com.hoperise.data

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {
    // Live data of user record
    private val loginUser = MutableLiveData<User>()
    // Listen for real time changes
    private var listener: ListenerRegistration? = null

    // Remove snapshot listener when view model is destroyed
    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }

    // Return the copy of user live data
    fun getUserLiveData(): LiveData<User> {
        return loginUser
    }

    // Return user data from live data
    fun getUser(): User? { // null means haven't login
        return loginUser.value
    }

    suspend fun login(ctx: Context, id: String, password: String, remember: Boolean = false): Boolean {
        val user = USER
            .whereEqualTo(FieldPath.documentId(), id)
            .whereEqualTo("password", password)
            .get()
            .await()
            .toObjects<User>()
            .firstOrNull() ?: return false // false means login failed, do not find any matches

        // Remove first
        listener?.remove()
        // Listen for real time changes
        listener = USER.document(user.id).addSnapshotListener { doc, _ ->
            loginUser.value = doc?.toObject()
        }

        if (remember) {
            getPreferences(ctx)
                .edit() // Write data into shared preferences
                .putString("id", id) // Login Credentials
                .putString("password", password) // Login Credentials
                .apply()
        }

        return true
    }

    fun logout(ctx: Context) {

        listener?.remove()  // No longer need to listen the changes of firestore
        loginUser.value = null // Use to inform whether its login or logout

       // Clear shared preferences
        getPreferences(ctx).edit().clear().apply()
    }

    // Get encrypted shared preferences
    private fun getPreferences(ctx: Context): SharedPreferences {
        return EncryptedSharedPreferences.create(
            "LoginUser",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            ctx,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // Remember me auto login
    suspend fun loginFromPreferences(ctx: Context) {
        val pref = getPreferences(ctx)
        val id = pref.getString("id", null)
        val password = pref.getString("password", null)

        if (id != null && password != null) {
            login(ctx, id, password)
        }
    }
}