package lsd.v2.api

fun interface Future<T> {
    fun resolve(): T

    fun dispose() {
    }
}
