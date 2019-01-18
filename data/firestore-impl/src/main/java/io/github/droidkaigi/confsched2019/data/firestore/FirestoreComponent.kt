package io.github.droidkaigi.confsched2019.data.firestore

import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
@Component(
    modules = [
        FirestoreModule::class
    ]
)
interface FirestoreComponent {
    fun firestore(): Firestore

    @Component.Builder
    interface Builder {

        @BindsInstance fun coroutineContext(coroutineContext: CoroutineContext): Builder

        fun build(): FirestoreComponent
    }

    companion object {
        fun builder(): Builder = DaggerFirestoreComponent.builder()
    }
}
