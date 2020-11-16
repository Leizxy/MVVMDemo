package cn.leizy.base.v

import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kotlin.math.abs

/**
 * @author Created by wulei
 * @date 2020/11/16, 016
 * @description
 */
abstract class BaseActivity<VB : ViewDataBinding> : AppCompatActivity() {
    companion object {
        const val MIN_CLICK_DELAY_TIME: Long = 500
    }

    private var downX: Float = 0f
    private var downY: Float = 0f
    private var lastTime: Long = 0

    protected lateinit var vb: VB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        vb = DataBindingUtil.setContentView(this, getLayoutResId())
        injectRouter()
        initViews()
        initData()
    }

    protected abstract fun getLayoutResId(): Int

    private fun injectRouter() {
        //add judge
//        ARouter.getInstance().inject(this)
    }

    protected abstract fun initViews()

    protected abstract fun initData()

    protected fun blockDoubleClick(): Boolean = true

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (blockDoubleClick()) {
            ev?.let {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        downX = it.x
                        downY = it.y
                        if (System.currentTimeMillis() - lastTime < MIN_CLICK_DELAY_TIME) {
                            return true
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (abs(it.x - downX) < 20 || abs(it.y - downY) < 20) {
                            if (System.currentTimeMillis() - lastTime > MIN_CLICK_DELAY_TIME) {
                                lastTime = System.currentTimeMillis()
                            }
                        }
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun toast(string: String) {
//        ToastUtil.showToast(string = string)
    }

    fun toast(@IdRes idRes: Int) {
//        ToastUtil.showToast(resId = idRes)
    }
}