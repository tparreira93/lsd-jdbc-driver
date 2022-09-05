package trxsys.lsd.api

import trxsys.lsd.future.Future

interface FutureCondition: Future<Boolean> {
    fun whenTrue(function: Runnable): FutureCondition
    fun whenFalse(function: Runnable): FutureCondition
}