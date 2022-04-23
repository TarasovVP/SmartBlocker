package com.tarasovvp.blacklister.ui.start

import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.tarasovvp.blacklister.ui.base.BaseFragment

abstract class GoogleFragment<VB : ViewBinding, T : ViewModel> :
    BaseFragment<VB, T>() {

    var googleSignInClient: GoogleSignInClient? = null

    abstract fun firebaseAuthWithGoogle(idToken: String)

    override fun onStart() {
        super.onStart()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("576475361826-qqu63i7ii3aquesphf7e071osjjh6178.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.e("signUserTAG",
                "LoginFragment googleSignInLauncher result.resultCode ${result.resultCode}")
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { firebaseAuthWithGoogle(it) }
            } catch (e: ApiException) {
                Log.e("signUserTAG",
                    "LoginFragment googleSignInLauncher ApiException ${e.localizedMessage}")
                Toast.makeText(
                    context,
                    e.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }


}