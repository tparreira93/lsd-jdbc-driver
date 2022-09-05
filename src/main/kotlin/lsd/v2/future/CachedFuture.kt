package lsd.v2.future

class CachedFuture<T>(private val f: Future<T>) : Future<T> {
    private var hasExecuted = false
    private var result: T? = null

    override fun resolve(): T {
        if (!hasExecuted) {
            result = f.resolve()
            hasExecuted = true
        }

        return result!!
    }
}