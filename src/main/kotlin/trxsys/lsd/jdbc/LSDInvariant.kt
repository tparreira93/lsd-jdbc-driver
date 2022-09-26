package trxsys.lsd.jdbc

import trxsys.lsd.api.FutureCondition
import trxsys.lsd.api.Invariant

class LSDInvariant(private val condition: Invariant) : FutureCondition {
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
        val result = condition.check()

        if (result) {
            futureTrueBranch?.run()
        } else {
            futureFalseBranch?.run()
        }

        return result
    }
}