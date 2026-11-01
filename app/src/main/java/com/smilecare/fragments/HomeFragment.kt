package com.example.smilecare.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.smilecare.R
import com.example.smilecare.databinding.FragmentHomeBinding
import com.example.smilecare.utils.AuthRepository
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userName = AuthRepository.currentUserName
        val isGuest = userName == null

        binding.tvHomeTitle.text = if (isGuest)
            getString(R.string.home_title_guest)
        else
            getString(R.string.home_title_user, userName)

        binding.btnBook.setOnClickListener {
            if (isGuest) {
                // Muro de registro SOLO en la acción de valor
                Snackbar.make(binding.root, R.string.guest_wall, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_login) {
                        findNavController().navigate(R.id.action_home_to_login)
                    }.show()
            } else {
                Toast.makeText(requireContext(), R.string.btn_book, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            AuthRepository.logout()
            findNavController().navigate(R.id.action_home_to_welcome)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}