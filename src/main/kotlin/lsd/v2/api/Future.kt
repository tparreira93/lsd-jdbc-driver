package lsd.v2.api

interface Future<T> {
    fun resolve(): T

    fun dispose() {
    }
}
