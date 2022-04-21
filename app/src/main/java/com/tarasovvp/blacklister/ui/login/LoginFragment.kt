package com.tarasovvp.blacklister.ui.login

import android.R.attr
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentLoginBinding
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.PermissionUtil
import com.tarasovvp.blacklister.utils.PermissionUtil.checkPermissions
import com.tarasovvp.blacklister.utils.setSafeOnClickListener
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider


class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    override fun getViewBinding() = FragmentLoginBinding.inflate(layoutInflater)

    override val viewModelClass = LoginViewModel::class.java

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (context?.checkPermissions() == true) {
            (activity as MainActivity).getAllData()
        } else {
            requestPermissionLauncher.launch(PermissionUtil.permissionsArray())
        }
        Log.e("signUserTAG", "LoginFragment onViewCreated currentUser.email ${BlackListerApp.instance?.auth?.currentUser?.email}")
        setOnButtonsClick()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("576475361826-qqu63i7ii3aquesphf7e071osjjh6178.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    private fun setOnButtonsClick() {
        binding?.continueButton?.setSafeOnClickListener {
            signInWithEmailAndPassword(binding?.email?.text.toString(), binding?.password?.text.toString())
        }
        binding?.continueWithoutAccButton?.setSafeOnClickListener {
            findNavController().navigate(R.id.startCallLogList)
        }
        binding?.register?.setSafeOnClickListener {
            findNavController().navigate(R.id.startSignUpFragment)
        }
        binding?.buttonForgotPassword?.setSafeOnClickListener {
            sendPasswordResetEmail(binding?.email?.text.toString())
        }
        binding?.googleAuth?.setSafeOnClickListener {
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        activity?.let {
            BlackListerApp.instance?.auth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener(
                it) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.callLogListFragment)
                    Log.e("signUserTAG", "LoginFragment signInWithEmailAndPassword task.isSuccessful ${task.isSuccessful} currentUser.email ${BlackListerApp.instance?.auth?.currentUser?.email}")
                } else {
                    Toast.makeText(context, task.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                    Log.e("signUserTAG", "LoginFragment signInWithEmailAndPassword task.exception ${task.exception}")
                }
            }
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        activity?.let {
            BlackListerApp.instance?.auth?.sendPasswordResetEmail(email)?.addOnCompleteListener(
                it) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Check your email", Toast.LENGTH_LONG).show()
                    Log.e("signUserTAG", "LoginFragment signInWithEmailAndPassword task.isSuccessful ${task.isSuccessful} currentUser.email ${BlackListerApp.instance?.auth?.currentUser?.email}")
                } else {
                    Toast.makeText(context, task.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                    Log.e("signUserTAG", "LoginFragment signInWithEmailAndPassword task.exception ${task.exception}")
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted: Map<String, @JvmSuppressWildcards Boolean>? ->
            if (isGranted?.values?.contains(false) == true) {
                Toast.makeText(
                    context,
                    getString(R.string.give_all_permissions),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                (activity as MainActivity).getAllData()
            }
        }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.e("signUserTAG", "LoginFragment googleSignInLauncher result.resultCode ${result.resultCode}")
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e("signUserTAG", "LoginFragment googleSignInLauncher ApiException ${e.localizedMessage}")
                Toast.makeText(
                    context,
                    e.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        BlackListerApp.instance?.auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.e("signUserTAG", "LoginFragment firebaseAuthWithGoogle task.isSuccessful ${task.isSuccessful}")
                } else {
                    Log.e("signUserTAG", "LoginFragment firebaseAuthWithGoogle task.exception ${task.exception}")
                }
            }
    }


    override fun observeLiveData() = Unit
}