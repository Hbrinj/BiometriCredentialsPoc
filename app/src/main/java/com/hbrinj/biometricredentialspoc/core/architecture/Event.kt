package com.hbrinj.biometricredentialspoc.core.architecture


open class DataEvent<T>(private val content: T) {
    var handled = false
        private set

    fun getContent(): T? {
        return if(!handled) {
            handled = true
            content
        } else null
    }
}

class Event: DataEvent<Unit>(Unit)

