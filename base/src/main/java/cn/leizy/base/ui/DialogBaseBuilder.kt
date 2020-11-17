package cn.leizy.base.ui

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import cn.leizy.base.R
import java.lang.reflect.ParameterizedType

/**
 * @author Created by wulei
 * @date 2020/11/17, 017
 * @description 继承该Builder，需Dialog
 */
abstract class DialogBaseBuilder<D : Dialog, VB : ViewDataBinding>(
    protected val context: Context,
    @LayoutRes private val layoutId: Int
) {
    protected var vb: VB

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(layoutId, null)
        vb = DataBindingUtil.bind(layout)!!
    }

    fun create(): D {
        return createDialog(R.style.dialog)!!
    }

    protected fun createDialog(@StyleRes styleRes: Int): D? {
        val d: D
        d = try {
            val type = this.javaClass.genericSuperclass as ParameterizedType
            val cls = type.actualTypeArguments[0] as Class<D>
            val constructor = cls.getConstructor(Context::class.java, Int::class.java)
            constructor.newInstance(context, styleRes)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        d.setContentView(vb.root)
        initView(d)
        return d
    }

    protected open fun initView(dialog: D) {
    }
}