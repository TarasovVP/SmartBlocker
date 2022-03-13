package com.example.blacklister.ui.dialogs

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.blacklister.R
import com.example.blacklister.constants.Constants.BLACK_NUMBER
import com.example.blacklister.databinding.DialogInfoBinding
import com.example.blacklister.utils.setSafeOnClickListener

class InfoDialog : DialogFragment() {

    private lateinit var binding: DialogInfoBinding

    private val args: InfoDialogArgs by navArgs()

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null && dialog.window != null) {
            val width = Resources.getSystem().displayMetrics.widthPixels
            dialog.window!!
                .setLayout((width * 0.85).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
            dialog.window!!.setGravity(Gravity.CENTER)
            dialog.window!!
                .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogInfoBinding.inflate(inflater)
        initUI()
        return binding.root
    }

    private fun initUI() {
        binding.dialogInfoTitle.text =
            String.format(getString(R.string.delete), args.blackNumber?.blackNumber)
        binding.dialogInfoCancel.setSafeOnClickListener {
            dismiss()
        }
        binding.dialogInfoConfirm.setSafeOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                BLACK_NUMBER,
                args.blackNumber
            )
            dismiss()
        }
    }
}