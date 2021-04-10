package com.hbrinj.biometricredentialspoc.core.crypto

data class EncryptedMessage(val encryptedMessage: ByteArray, val iv: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptedMessage

        if (!encryptedMessage.contentEquals(other.encryptedMessage)) return false
        if (!iv.contentEquals(other.iv)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = encryptedMessage.contentHashCode()
        result = 31 * result + iv.contentHashCode()
        return result
    }
}
