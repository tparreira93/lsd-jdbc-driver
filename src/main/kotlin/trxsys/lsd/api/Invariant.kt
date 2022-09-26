package trxsys.lsd.api

import trxsys.lsd.future.Future

interface Invariant {
    fun check(): Boolean
}

class FutureInvariant<T>(
    private val future: Future<T>,
    private val condition: (T) -> Boolean
) : Invariant {
    companion object {
        fun <T> futureIs(future: Future<T>, condition: (T) -> Boolean): FutureInvariant<T> {
            return FutureInvariant(future, condition)
        }
    }

    override fun check(): Boolean {
        val result = future.resolve()
        return condition.invoke(result)
    }
}

class And : Invariant {
    private val invariants = mutableListOf<Invariant>()

    override fun check(): Boolean {
        return invariants.all { it.check() }
    }

    internal fun add(condition: Invariant) {
        invariants.add(condition)
    }
}

class Or : Invariant {
    private val invariants = mutableListOf<Invariant>()

    override fun check(): Boolean {
        return invariants.any { it.check() }
    }

    internal fun add(condition: Invariant) {
        invariants.add(condition)
    }
}

class InvariantChain {
    companion object {
        fun and(vararg invariants: Invariant): And {
            val and = And()
            for (inv in invariants) {
                and.add(inv)
            }

            return and
        }

        fun or(vararg invariants: Invariant): Or {
            val and = Or()
            for (inv in invariants) {
                and.add(inv)
            }

            return and
        }
    }
}