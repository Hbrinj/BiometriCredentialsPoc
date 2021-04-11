package com.hbrinj.biometricredentialspoc.core.module

import android.content.Context
import android.content.SharedPreferences
import androidx.biometric.BiometricManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object BiometricModule {

    @Provides
    fun biometricManager(@ApplicationContext context: Context): BiometricManager {
        return BiometricManager.from(context)
    }

    @Provides
    fun biometricSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("BiometricSharedPrefs", Context.MODE_PRIVATE)
    }

    @Provides
    fun gson(): Gson {
        return GsonBuilder().serializeNulls().create()
    }
}