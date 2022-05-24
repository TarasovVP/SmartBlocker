package com.tarasovvp.blacklister.ui.dialogs

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
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.APP_EXIT
import com.tarasovvp.blacklister.constants.Constants.BLACK_NUMBER
import com.tarasovvp.blacklister.databinding.DialogInfoBinding
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class InfoDialog : DialogFragment() {

    private lateinit var binding: DialogInfoBinding

    private val args: InfoDialogArgs by navArgs()

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog.isNotNull() && dialog?.window.isNotNull()) {
            val width = Resources.getSystem().displayMetrics.widthPixels
            dialog?.window?.setLayout((width * 0.85).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
            dialog?.window?.setGravity(Gravity.CENTER)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.setCancelable(true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = DialogInfoBinding.inflate(inflater)
        initUI()
        return binding.root
    }

    private fun initUI() {
        binding.dialogInfoTitle.text =
            if (args.blackNumber == null) getString(R.string.exit_application) else String.format(
                getString(R.string.delete),
                args.blackNumber?.blackNumber)
        binding.dialogInfoCancel.setSafeOnClickListener {
            dismiss()
        }
        binding.dialogInfoConfirm.setSafeOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                if (args.blackNumber == null) APP_EXIT else BLACK_NUMBER,
                if (args.blackNumber == null) true else args.blackNumber
            )
            dismiss()
        }
    }
}