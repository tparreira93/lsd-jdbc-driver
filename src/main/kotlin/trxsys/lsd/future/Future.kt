package trxsys.lsd.future

import java.util.function.Consumer

fun interface Future<T> {
    fun resolve(): T

    fun dispose() {

    }
}

interface ResultChain<T> {
    fun then(function: Consumer<T>)
}



