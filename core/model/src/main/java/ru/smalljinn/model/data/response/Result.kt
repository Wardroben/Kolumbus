package ru.smalljinn.model.data.response

/*sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    data class Loading<T>(val isLoading: Boolean) : Resource<T>()
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>()
}*/

typealias RootError = Error

sealed interface Result<out D, out E : RootError> {
    data class Error<out D, out E : RootError>(val error: E) : Result<D, E>
    data class Success<out D, out E : RootError>(val data: D) : Result<D, E>
}