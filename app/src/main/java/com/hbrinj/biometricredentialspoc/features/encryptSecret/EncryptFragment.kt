package com.hbrinj.biometricredentialspoc.features.encryptSecret

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hbrinj.biometricredentialspoc.R
import com.hbrinj.biometricredentialspoc.core.biometric.BiometricPromptCallback
import com.hbrinj.biometricredentialspoc.databinding.FragmentEncryptBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EncryptFragment: Fragment(R.layout.fragment_encrypt) {

    private val viewModel: EncryptViewModel by viewModels()
    private lateinit var _binding: FragmentEncryptBinding
    private val binding get() = _binding
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) viewModel.checkEligibilityAndPrompt()
    }

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
                    PromptEvent.Error -> Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show()
                    PromptEvent.Failure -> Toast.makeText(context, "Failure!", Toast.LENGTH_SHORT).show()
                    PromptEvent.Successful -> Toast.makeText(context, "Encrypted!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.encryptButton.setOnClickListener { viewModel.checkEligibilityAndPrompt() }
        binding.encryptKeyName.doOnTextChanged { text, _, _, _ ->  viewModel.keyNameChanged(text) }
        binding.encryptSecret.doOnTextChanged { text, _, _, _ -> viewModel.secretMessageChanged(text) }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, true) {
            findNavController().navigate(R.id.action_encryptFragment_to_mainFragment2)
        }
    }

    private fun noAvailableAuth() {
        Toast.makeText(context, "No auth Available on Device", Toast.LENGTH_SHORT).show()
    }

    private fun canEnrollInAuth() {
        launcher.launch(getEnrollIntent())
    }

    private fun getEnrollIntent() =
        Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                viewModel.allowedAuth
            )
        }

    private fun promptForAuth(promptCallback: BiometricPromptCallback) {
        val biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(context), promptCallback)
        biometricPrompt.authenticate(createPromptForAuth())
    }

    private fun createPromptForAuth() =
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.encrypt_prompt_title))
            .setSubtitle(getString(R.string.encrypt_prompt_subtitle))
            .setAllowedAuthenticators(viewModel.allowedAuth)
            .build()
}