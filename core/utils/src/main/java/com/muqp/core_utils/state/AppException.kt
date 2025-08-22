package com.muqp.core_utils.state

import com.muqp.core_utils.state.AppException.DataException
import com.muqp.core_utils.state.AppException.NetworkException
import java.io.IOException

sealed class AppException(
    val code: Int? = null,
    override val message: String
) : Exception(message) {
    class NetworkException(code: Int?, message: String) : AppException(code, message)
    class DataException(message: String) : AppException(message = message)
}

suspend fun <T> wrapNetworkCall(block: suspend () -> T): T {
    return try {
        block()
    } catch (e: AppException) {
        throw e
    } catch (e: IOException) {
        throw NetworkException(
            code = null,
            message = "Network error: ${e.message}"
        )
    } catch (e: Exception) {
        throw DataException(
            message = "Data processing error: ${e.message}"
        )
    }
}