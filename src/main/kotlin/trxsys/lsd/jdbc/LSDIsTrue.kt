package trxsys.lsd.jdbc

import trxsys.lsd.api.FutureCondition

class LSDIsTrue(private val condition: () -> Boolean) : FutureCondition {
    private var futureTrueBranch: Runnable? = null
    private var futureFalseBranch: Runnable? = null

    override fun whenTrue(function: Runnable): FutureCondition {
        futureTrueBranch = function

        return this
    }

    override fun whenFalse(function: Runnable): FutureCondition {
        futureFalseBranch = function

        return this
    }

    override fun resolve(): Boolean {
        val result = condition.invoke()

        if (result) {
            futureTrueBranch?.run()
        } else {
            futureFalseBranch?.run()
        }

        return result
    }
}