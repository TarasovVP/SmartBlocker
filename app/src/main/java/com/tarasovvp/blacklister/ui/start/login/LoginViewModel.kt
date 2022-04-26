package com.tarasovvp.blacklister.ui.start.login

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.ui.start.GoogleViewModel

class LoginViewModel(application: Application) : GoogleViewModel(application) {

    fun sendPasswordResetEmail(email: String) {
        BlackListerApp.instance?.auth?.sendPasswordResetEmail(email)?.addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                Toast.makeText(getApplication(), "Check your email", Toast.LENGTH_LONG).show()
                Log.e("signUserTAG",
                    "LoginFragment signInWithEmailAndPassword task.isSuccessful ${task.isSuccessful} currentUser.email ${BlackListerApp.instance?.auth?.currentUser?.email}")
            } else {
                Toast.makeText(getApplication(), task.exception?.localizedMessage, Toast.LENGTH_LONG)
                    .show()
                Log.e("signUserTAG",
                    "LoginFragment signInWithEmailAndPassword task.exception ${task.exception}")
            }
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        BlackListerApp.instance?.auth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    successSignInLiveData.postValue(true)
                    Log.e("signUserTAG",
                        "LoginFragment signInWithEmailAndPassword task.isSuccessful ${task.isSuccessful} currentUser.email ${BlackListerApp.instance?.auth?.currentUser?.email}")
                } else {
                    Toast.makeText(getApplication(), task.exception?.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                    Log.e("signUserTAG",
                        "LoginFragment signInWithEmailAndPassword task.exception ${task.exception}")
                }

            }
    }

}