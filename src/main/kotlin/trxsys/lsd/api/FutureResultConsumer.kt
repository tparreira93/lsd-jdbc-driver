package trxsys.lsd.api

import trxsys.lsd.future.Future
import trxsys.lsd.future.ResultConsumer
import java.util.function.Consumer

class FutureResultConsumer<T>(private val future: Future<T>): ResultConsumer<T>, Future<Unit> {
    private var consumer: Consumer<T>? = null

    override fun then(function: Consumer<T>) {
        consumer = function
    }

    override fun resolve() {
        val result = future.resolve()
        consumer?.accept(result)
    }
}