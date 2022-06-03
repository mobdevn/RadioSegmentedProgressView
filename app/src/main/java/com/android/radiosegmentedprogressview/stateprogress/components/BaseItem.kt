package com.android.radiosegmentedprogressview.stateprogress.components

open class BaseItem() {

    /*constructor(builder: Builder<*>) : this() {
        color = builder.color
        size = builder.size
    }

    private var color = 0
    private var size = 0f

    abstract class Builder<T : Builder<T>?> {
        var color = 0
        var size = 0f
        protected abstract fun self(): T
        fun color(color: Int): T {
            this.color = color
            return self()
        }

        fun size(size: Float): T {
            this.size = size
            return self()
        }

        open fun build(): BaseItem {
            return BaseItem()
        }

        abstract fun BaseItem(): BaseItem
    }

    private class Builder2 : Builder<Builder2?>() {
        override fun self(): Builder2 {
            return this
        }

        override fun BaseItem(): BaseItem {
            return BaseItem()
        }
    }

    open fun builder(): Builder<*>? {
        return Builder2()
    }

    protected fun BaseItem(builder: Builder<*>) {
        color = builder.color
        size = builder.size
    }

    fun getColor(): Int {
        return color
    }

    fun getSize(): Float {
        return size
    }*/
}