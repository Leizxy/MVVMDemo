package cn.leizy.base.v

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.annotation.IdRes
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
    protected val viewModel: VM by lazy {
        val pt = this.javaClass.genericSuperclass as ParameterizedType
        val clazz: Class<VM> = pt.actualTypeArguments[0] as Class<VM>
        ViewModelProvider(this).get(clazz)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.bindView(this)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelJobs()
    }

    override fun showToast(str: String) {
        toast(str)
    }

    @SuppressLint("ResourceType")
    override fun showToast(@IdRes idRes: Int) {
        toast(idRes)
    }
}