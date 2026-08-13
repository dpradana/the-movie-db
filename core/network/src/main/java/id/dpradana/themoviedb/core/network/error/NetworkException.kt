package id.dpradana.themoviedb.core.network.error

sealed interface NetworkException {
    data object Unauthorized : NetworkException
    data object Forbidden : NetworkException
    data object NotFound : NetworkException
    data object TooManyRequests : NetworkException
    data class ServerError(val code: Int) : NetworkException
    data object NoInternet : NetworkException
    data object Timeout : NetworkException
    data object Unknown : NetworkException
}
