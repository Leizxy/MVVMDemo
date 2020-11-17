package cn.leizy.base.v

import android.annotation.SuppressLint
import androidx.annotation.IdRes
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import cn.leizy.base.m.IModel
import cn.leizy.base.vm.BaseViewModel
import java.lang.reflect.ParameterizedType

/**
 * @author Created by wulei
 * @date 2020/11/17, 017
 * @description
 */
abstract class BaseMVVMFragment<VM : BaseViewModel<out IView, out IModel>, VB : ViewDataBinding> :
    BaseFragment<VB>(), IView {
    protected val viewModel: VM by lazy {
        val pt = this.javaClass.genericSuperclass as ParameterizedType
        val clazz: Class<VM> = pt.actualTypeArguments[0] as Class<VM>
        ViewModelProvider(this).get(clazz)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.cancelJobs()
    }

    override fun showToast(string: String) {
        toast(string)
    }

    @SuppressLint("ResourceType")
    override fun showToast(@IdRes idRes: Int) {
        toast(idRes)
    }
}