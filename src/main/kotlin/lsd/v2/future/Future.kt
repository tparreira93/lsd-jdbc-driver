package lsd.v2.future

import java.util.function.Consumer

fun interface Future<T> {
    fun resolve(): T

    fun dispose() {

    }
}

interface ResultConsumer<T> {
    fun then(function: Consumer<T>)
}



