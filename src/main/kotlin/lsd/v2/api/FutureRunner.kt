package lsd.v2.api

import lsd.v2.future.Future
import java.util.function.Consumer

class FutureRunner<T>(private val `fun`: () -> T): Future<T> {
    private var then: (Consumer<T>)? = null
    override fun resolve(): T {
        val result = `fun`.invoke()
        then?.accept(result)

        return result
    }
}