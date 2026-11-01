package com.example.smilecare.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.smilecare.R
import com.example.smilecare.databinding.FragmentSignUpBinding
import com.example.smilecare.utils.AuthRepository
import com.example.smilecare.utils.Validators
import com.example.smilecare.utils.showError
import com.example.smilecare.utils.showValid
import com.example.smilecare.utils.shake
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Validación en tiempo real: al perder el foco y al escribir
        binding.etName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateName()
        }
        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateEmail()
        }
        binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validatePassword()
        }

        binding.etName.doAfterTextChanged { binding.tilName.error = null }
        binding.etEmail.doAfterTextChanged {
            // Feedback inmediato: error si hay texto inválido, check si es válido
            val text = it.toString()
            if (text.isNotBlank()) {
                val err = Validators.email(text)
                if (err == null) binding.tilEmail.showValid()
                else binding.tilEmail.showError(null)
            }
        }
        binding.etPassword.doAfterTextChanged { binding.tilPassword.error = null }
        binding.cbTerms.setOnCheckedChangeListener { _, checked ->
            if (checked) binding.tvTermsError.visibility = View.GONE
        }

        binding.btnSignUp.setOnClickListener { submit() }
        binding.tvGoLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signUp_to_login)
        }
    }

    private fun validateName(): Boolean {
        val err = Validators.name(binding.etName.text.toString())
        binding.tilName.showError(err)
        return err == null
    }

    private fun validateEmail(): Boolean {
        val err = Validators.email(binding.etEmail.text.toString())
        if (err == null) binding.tilEmail.showValid() else binding.tilEmail.showError(err)
        return err == null
    }

    private fun validatePassword(): Boolean {
        val err = Validators.password(binding.etPassword.text.toString())
        binding.tilPassword.showError(err)
        // El campo de contraseña mantiene el icono de mostrar/ocultar
        binding.tilPassword.endIconMode =
            com.google.android.material.textfield.TextInputLayout.END_ICON_PASSWORD_TOGGLE
        return err == null
    }

    private fun submit() {
        // Se evaluún todos para mostrar todos los errores a la vez
        val nameOk = validateName()
        val emailOk = validateEmail()
        val passOk = validatePassword()
        val termsOk = binding.cbTerms.isChecked
        binding.tvTermsError.visibility = if (termsOk) View.GONE else View.VISIBLE

        if (!(nameOk && emailOk && passOk && termsOk)) {
            binding.btnSignUp.shake()
            return
        }

        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1200) // simula la llamada al servidor
            val created = AuthRepository.register(name, email, password)
            setLoading(false)
            if (created) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.signup_success, name.trim()),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_signUp_to_home)
            } else {
                binding.tilEmail.showError(R.string.err_email_taken)
                binding.btnSignUp.shake()
            }
        }
    }

    /** Estado CARGA: botón deshabilitado + indicador circular. */
    private fun setLoading(loading: Boolean) {
        binding.btnSignUp.isEnabled = !loading
        binding.btnSignUp.text = if (loading) "" else getString(R.string.btn_sign_up)
        binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}