package com.hbrinj.biometricredentialspoc.features.encryptSecret

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.hbrinj.biometricredentialspoc.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EncryptFragment: Fragment(R.layout.fragment_encrypt) {

    private val encryptViewModel: EncryptViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}