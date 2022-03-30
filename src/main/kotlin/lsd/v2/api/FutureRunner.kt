package lsd.v2.api

class FutureRunner<T>(private val `fun`: () -> T): Future<T> {
    override fun resolve(): T = `fun`.invoke()

    override fun dispose() { }
}