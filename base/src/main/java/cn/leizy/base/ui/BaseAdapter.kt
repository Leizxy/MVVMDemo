package cn.leizy.base.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * @author Created by wulei
 * @date 2020/10/18
 * @description
 */
abstract class BaseAdapter<VB : ViewDataBinding, T>(protected val context: Context, @IdRes private val layout: Int) : RecyclerView.Adapter<BaseViewHolder>() {

    protected var dataList: MutableList<T> = arrayListOf()
    private var listener: ((Int, T) -> Unit)? = null
    protected var dataUpdateListener: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding: VB = DataBindingUtil.inflate(LayoutInflater.from(context), layout, parent, false)
        return BaseViewHolder(binding)
    }

    final override fun getItemCount(): Int {
        return if (addHeader() && dataList.size > 0) dataList.size + 1 else dataList.size
    }

    fun setData(list: MutableList<T>, append: Boolean = true) {
        if (append) {
            this.dataList.addAll(list)
        } else {
            this.dataList.clear()
            this.dataList.addAll(list)
        }
        update()
    }

    fun add(t: T) {
        this.dataList.add(t)
        update(if (addHeader()) dataList.size else dataList.size - 1)
    }

    fun clear() {
        this.dataList.clear()
        update()
    }

    fun update() {
        dataUpdateListener?.invoke()
        notifyDataSetChanged()
    }

    fun update(index: Int) {
        dataUpdateListener?.invoke()
        notifyItemRangeInserted(index, 1)
    }

    protected open fun addHeader(): Boolean {
        return false
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (dataList.size > 0) {
            if (addHeader()) {
                if (position == 0) {
                    convert(position, holder.binding as VB, null)
                } else {
                    convert(position, holder.binding as VB, dataList[position - 1])
                    holder.binding.root.setOnClickListener {
                        invokeClickListener(position, dataList[position - 1])
                    }
                }
            } else {
                convert(position, holder.binding as VB, dataList[position])
                holder.binding.root.setOnClickListener {
                    invokeClickListener(position, dataList[position])
                }
            }
            holder.binding.executePendingBindings()
        }
    }

    abstract fun convert(position: Int, binding: VB, obj: T?)

    fun setOnItemClickListener(listener: (position: Int, obj: T) -> Unit): BaseAdapter<*, *> {
        this.listener = listener
        return this
    }

    fun addDataUpdateListener(listener: () -> Unit): BaseAdapter<*, *> {
        this.dataUpdateListener = listener
        return this
    }

    private fun invokeClickListener(position: Int, obj: T) {
        listener?.invoke(position, obj)
    }
}