package com.example.smilecare.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.smilecare.R
import com.example.smilecare.databinding.FragmentRecoverBinding
import com.example.smilecare.utils.AuthRepository
import com.example.smilecare.utils.Validators
import com.example.smilecare.utils.shake
import com.example.smilecare.utils.showError
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RecoverFragment : Fragment() {

    private var _binding: FragmentRecoverBinding? = null
    private val binding get() = _binding!!

    private var emailSent: String = ""

    // Solo activo en el paso 2 (código): vuelve al paso 1
    private val backCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() = showEmailStep()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecoverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, backCallback)

        binding.etEmail.doAfterTextChanged { binding.tilEmail.error = null }
        binding.etCode.doAfterTextChanged { binding.tilCode.error = null }
        binding.etNewPassword.doAfterTextChanged { binding.tilNewPassword.error = null }

        binding.btnSendCode.setOnClickListener { sendCode() }
        binding.btnVerify.setOnClickListener { verify() }
    }

    private fun sendCode() {
        val email = binding.etEmail.text.toString()
        val err = Validators.email(email)
        binding.tilEmail.showError(err)
        if (err != null) {
            binding.btnSendCode.shake()
            return
        }

        binding.btnSendCode.isEnabled = false
        binding.progress.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1200) // simula el envío del email
            binding.progress.visibility = View.GONE
            binding.btnSendCode.isEnabled = true
            emailSent = email.trim()
            showCodeStep()
            Snackbar.make(binding.root, R.string.recover_sent, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun verify() {
        val codeErr = Validators.code(binding.etCode.text.toString())
            ?: if (binding.etCode.text.toString() != AuthRepository.DEMO_OTP)
                R.string.err_code_invalid else null
        val passErr = Validators.password(binding.etNewPassword.text.toString())

        binding.tilCode.showError(codeErr)
        binding.tilNewPassword.showError(passErr)
        binding.tilNewPassword.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE

        if (codeErr != null || passErr != null) {
            binding.btnVerify.shake()
            return
        }

        AuthRepository.updatePassword(emailSent, binding.etNewPassword.text.toString())
        Toast.makeText(requireContext(), R.string.recover_success, Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.action_recover_to_login)
    }

    private fun showCodeStep() {
        binding.groupEmail.visibility = View.GONE
        binding.groupCode.visibility = View.VISIBLE
        binding.tvRecoverTitle.setText(R.string.recover_code_title)
        binding.tvRecoverSubtitle.text = getString(R.string.recover_code_subtitle, emailSent)
        backCallback.isEnabled = true
    }

    private fun showEmailStep() {
        binding.groupCode.visibility = View.GONE
        binding.groupEmail.visibility = View.VISIBLE
        binding.tvRecoverTitle.setText(R.string.recover_title)
        binding.tvRecoverSubtitle.setText(R.string.recover_subtitle)
        backCallback.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}