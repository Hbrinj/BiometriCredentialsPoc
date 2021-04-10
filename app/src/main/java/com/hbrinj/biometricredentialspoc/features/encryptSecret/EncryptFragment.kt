package com.hbrinj.biometricredentialspoc.features.encryptSecret

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.hbrinj.biometricredentialspoc.R
import com.hbrinj.biometricredentialspoc.core.biometric.BiometricPromptCallback
import com.hbrinj.biometricredentialspoc.databinding.FragmentEncryptBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EncryptFragment: Fragment(R.layout.fragment_encrypt) {

    private val viewModel: EncryptViewModel by viewModels()
    private lateinit var _binding: FragmentEncryptBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEncryptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.eligibilityEvent.observe(viewLifecycleOwner) {
            it.getContent()?.let { event ->
                when(event) {
                    EligibilityEvent.CanEnroll -> canEnrollInAuth()
                    EligibilityEvent.NotAvailable -> noAvailableAuth()
                    is EligibilityEvent.Prompt -> promptForAuth(event.callback)
                }
            }
        }
        viewModel.promptEvent.observe(viewLifecycleOwner) {
            it.getContent()?.let { event ->
                when(event) {
                    PromptEvent.Error -> TODO()
                    PromptEvent.Failure -> TODO()
                    PromptEvent.Successful -> Toast.makeText(context, "hello!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.encryptButton.setOnClickListener { viewModel.checkEligibilityAndPrompt() }
    }


    private fun noAvailableAuth() {

    }

    private fun canEnrollInAuth() {

    }

    private fun promptForAuth(promptCallback: BiometricPromptCallback) {

    }
}