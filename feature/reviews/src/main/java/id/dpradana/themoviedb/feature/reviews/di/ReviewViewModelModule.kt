package id.dpradana.themoviedb.feature.reviews.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import id.dpradana.themoviedb.feature.reviews.presentation.ReviewViewModel
import id.dpradana.themoviedb.feature.reviews.presentation.TrailerViewModel
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
abstract class ReviewViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ReviewViewModel::class)
    abstract fun bindReviewViewModel(viewModel: ReviewViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TrailerViewModel::class)
    abstract fun bindTrailerViewModel(viewModel: TrailerViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

class ViewModelFactory @Inject constructor(
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass] ?: creators.entries.firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value ?: throw IllegalArgumentException("unknown model class $modelClass")
        try {
            @Suppress("UNCHECKED_CAST")
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
