package pl.pelotasplus.queens.core

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    fun provideCurrentTimeProvider(): CurrentTimeProvider {
        return CurrentTimeProviderImpl()
    }
}
