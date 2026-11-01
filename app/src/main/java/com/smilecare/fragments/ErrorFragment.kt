package com.example.smilecare.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.smilecare.R
import com.example.smilecare.databinding.FragmentErrorBinding

class ErrorFragment : Fragment() {

    private var _binding: FragmentErrorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentErrorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mensaje recibido por argumentos (Bundle) desde LoginFragment
        arguments?.getString("error_message")?.let {
            binding.tvErrorMessage.text = it
        }

        binding.btnResetPassword.setOnClickListener {
            findNavController().navigate(R.id.action_error_to_recover)
        }
        binding.btnRetry.setOnClickListener {
            findNavController().popBackStack() // vuelve al Login
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}