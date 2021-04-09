package com.hbrinj.biometricredentialspoc.features.decryptSecret

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.hbrinj.biometricredentialspoc.R

class DecryptFragment: Fragment(R.layout.fragment_decrypt) {
    private val viewModel: DecryptViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}