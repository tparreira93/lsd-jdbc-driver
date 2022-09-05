package lsd.v2.future

import lsd.v2.api.FutureRunner

class FutureUtils {
    companion object {
        fun <T> newFuture(function: () -> T): Future<T> {
            return FutureRunner {
                function.invoke()
            }
        }

        fun <T> newCachedFuture(function: () -> T): Future<T> {
            return CachedFuture {
                function.invoke()
            }
        }
    }
}