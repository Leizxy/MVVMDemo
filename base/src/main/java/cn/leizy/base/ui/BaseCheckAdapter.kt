package cn.leizy.base.ui

import android.content.Context
import androidx.annotation.IdRes
import androidx.databinding.ViewDataBinding
import cn.leizy.bean.BaseCheckBean

/**
 * @author Created by wulei
 * @date 2020/11/1
 * @description
 */
abstract class BaseCheckAdapter<VB : ViewDataBinding, T : BaseCheckBean>(context: Context, @IdRes layout: Int) : BaseAdapter<VB, T>(context, layout) {
    var showCheck: Boolean = false
    fun getList(): MutableList<T> {
        return dataList
    }

    fun isAllChecked(): Boolean {
        for (task in dataList) {
            if (!task.isCheck) return false
        }
        return true
    }

    fun haveCheck(): Boolean {
        for (task in dataList) {
            if (task.isCheck) return true
        }
        return false
    }

    fun checkAll(check: Boolean) {
        for (taskBean in dataList) {
            taskBean.isCheck = check
        }
        update()
    }
}