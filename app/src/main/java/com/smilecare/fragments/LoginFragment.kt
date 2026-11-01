package com.example.smilecare.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.smilecare.R
import com.example.smilecare.databinding.FragmentLoginBinding
import com.example.smilecare.utils.AuthRepository
import com.example.smilecare.utils.Validators
import com.example.smilecare.utils.shake
import com.example.smilecare.utils.showError
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etEmail.doAfterTextChanged { binding.tilEmail.error = null }
        binding.etPassword.doAfterTextChanged { binding.tilPassword.error = null }

        binding.btnSignIn.setOnClickListener { signIn() }

        binding.btnGoogle.setOnClickListener {
            Snackbar.make(binding.root, R.string.google_soon, Snackbar.LENGTH_SHORT).show()
        }
        binding.tvForgot.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_recover)
        }
        binding.tvGoSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_signUp)
        }
    }

    private fun signIn() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        val emailErr = Validators.email(email)
        val passErr = if (password.isEmpty()) R.string.err_password_empty else null
        binding.tilEmail.showError(emailErr)
        binding.tilPassword.showError(passErr)
        binding.tilPassword.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
        if (emailErr != null || passErr != null) {
            binding.btnSignIn.shake()
            return
        }

        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1200) // simula la llamada al servidor
            setLoading(false)

            if (AuthRepository.login(email, password)) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.signup_success, AuthRepository.currentUserName),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_login_to_home)
            } else {
                onLoginFailed()
            }
        }
    }

    private fun onLoginFailed() {
        AuthRepository.failedAttempts++
        val remaining = AuthRepository.MAX_ATTEMPTS - AuthRepository.failedAttempts

        if (remaining <= 0) {
            // Demasiados intentos: pantalla de error dedicada
            AuthRepository.failedAttempts = 0
            findNavController().navigate(
                R.id.action_login_to_error,
                bundleOf("error_message" to getString(R.string.error_too_many))
            )
        } else {
            // Estado ERROR en línea: campo en rojo + Snackbar + sacudida
            val message = getString(R.string.login_wrong_credentials, remaining)
            binding.tilPassword.error = message
            binding.tilPassword.shake()
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.btnSignIn.isEnabled = !loading
        binding.btnSignIn.text = if (loading) "" else getString(R.string.btn_sign_in)
        binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}