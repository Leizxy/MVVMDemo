package cn.leizy.base.v

import androidx.annotation.IdRes

/**
 * @author Created by wulei
 * @date 2020/11/16
 * @description
 */
interface IView {
    fun showLoading() {}
    fun hideLoading() {}
    fun showToast(str: String){}
    fun showToast(@IdRes idRes: Int){}
}