package com.hbrinj.biometricredentialspoc.features.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hbrinj.biometricredentialspoc.R
import com.hbrinj.biometricredentialspoc.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment: Fragment(R.layout.fragment_main) {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var _binding: FragmentMainBinding

    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.encryptDataEvent.observe(viewLifecycleOwner) {
            encrypt()
        }

        viewModel.decryptDataEvent.observe(viewLifecycleOwner) {
            decrypt()
        }

        binding.mainButtonEncrypt.setOnClickListener { viewModel.encrypt() }
        binding.mainButtonDecrypt.setOnClickListener { viewModel.decrypt() }
    }

    private fun encrypt() = findNavController().navigate(R.id.action_mainFragment_to_encryptFragment)

    private fun decrypt() = findNavController().navigate(R.id.action_mainFragment_to_decryptFragment)
}