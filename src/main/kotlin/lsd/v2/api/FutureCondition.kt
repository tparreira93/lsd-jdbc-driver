package lsd.v2.api

interface FutureCondition: Future<Boolean> {
    fun whenTrue(future: () -> Unit): FutureCondition
    fun whenFalse(future: () -> Unit): FutureCondition
}