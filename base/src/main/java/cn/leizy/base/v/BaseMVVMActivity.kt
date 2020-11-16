package cn.leizy.base.v

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import cn.leizy.base.vm.BaseViewModel
import cn.leizy.base.m.IModel
import java.lang.reflect.ParameterizedType

/**
 * @author Created by wulei
 * @date 2020/11/16, 016
 * @description
 */
abstract class BaseMVVMActivity<VM : BaseViewModel<out IView, out IModel>, VB : ViewDataBinding> :
    BaseActivity<VB>(), IView {
    protected val viewModel: VM by lazy { getVM() }

    @Suppress("UNCHECKED_CAST")
    private fun getVM(): VM {
        val pt = this.javaClass.genericSuperclass as ParameterizedType
        val clazz: Class<VM> = pt.actualTypeArguments[0] as Class<VM>
        return ViewModelProvider(this).get(clazz)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelJobs()
    }
}