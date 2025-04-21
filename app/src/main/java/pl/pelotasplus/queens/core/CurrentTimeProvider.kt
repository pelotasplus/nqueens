package pl.pelotasplus.queens.core

import javax.inject.Inject

interface
CurrentTimeProvider {
    fun getCurrentTimeMillis(): Long
}

class CurrentTimeProviderImpl @Inject constructor() : CurrentTimeProvider {
    override fun getCurrentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}
