package pl.pelotasplus.queens.core

class TestCurrentTimeProvider : CurrentTimeProvider {

    var mockedMillis: Long = 348753460 * 1000L

    override fun getCurrentTimeMillis(): Long {
        return mockedMillis
    }
}
