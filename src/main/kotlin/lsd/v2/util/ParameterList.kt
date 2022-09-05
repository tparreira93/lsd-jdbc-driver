package lsd.v2.util

import lsd.v2.future.Future


class ParameterList : Future<Unit> {
    private var params = ArrayList<Future<*>>()

    fun add(value: Future<*>) {
        params.add(value)
    }

    override fun resolve() {
        for (v in params) {
            v.resolve()
        }
    }

    override fun dispose() {
        params.clear()
    }

    fun clear() {
        params.clear()
    }
}