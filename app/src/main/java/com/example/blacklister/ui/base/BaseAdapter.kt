package com.example.blacklister.ui.base

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T : Any, VH : BaseViewHolder<T>>(
    data: List<T> = listOf()
) :
    RecyclerView.Adapter<VH>() {


    protected val data: MutableList<T> = data.toMutableList()


    val all: MutableList<T>
        get() = data


    val snapshot: List<T>
        get() = data.toMutableList()

    override fun getItemCount() = data.size


    @Throws(ArrayIndexOutOfBoundsException::class)
    fun getItem(position: Int): T = data[position]

    fun isEmpty() = data.isEmpty()

    fun isNotEmpty() = data.isNotEmpty()


    fun add(item: T) = data.add(item)

    fun replace(oldPosition: Int, newPosition: Int) = data.add(newPosition, remove(oldPosition))

    operator fun set(position: Int, item: T): T = data.set(position, item)

    fun remove(item: T) = data.remove(item)

    fun remove(position: Int): T = data.removeAt(position)

    open fun updateListItems(newItems: List<T>, callback: DiffUtil.Callback?) {
        callback?.let { DiffUtil.calculateDiff(callback).dispatchUpdatesTo(this) }
        data.clear()
        data.addAll(newItems)
    }

    fun updateAllNotify(newObjects: List<T>) {
        clear()
        addAll(newObjects)
        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
    }

    fun addAll(collection: Collection<T>) = data.addAll(collection)

    fun getItemPosition(item: T) = data.indexOf(item)

    open fun insert(item: T, position: Int) {
        data.add(position, item)
    }

    fun insertAll(items: Collection<T>, position: Int) {
        data.addAll(position, items)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        position.takeUnless { it == RecyclerView.NO_POSITION }?.let { holder.bind(getItem(it)) }
    }
}