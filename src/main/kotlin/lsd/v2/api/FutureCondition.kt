package lsd.v2.api

import lsd.v2.future.Future

interface FutureCondition: Future<Boolean> {
    fun whenTrue(function: Runnable): FutureCondition
    fun whenFalse(function: Runnable): FutureCondition
}