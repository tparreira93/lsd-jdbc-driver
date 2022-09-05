package trxsys.lsd.api

import trxsys.lsd.future.Future
import java.util.function.Consumer

class FutureRunner<T>(private val `fun`: () -> T): Future<T> {
    private var then: (Consumer<T>)? = null
    override fun resolve(): T {
        val result = `fun`.invoke()
        then?.accept(result)

        return result
    }
}