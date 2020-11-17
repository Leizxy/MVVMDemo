package cn.leizy.base.v

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

/**
 * @author Created by wulei
 * @date 2020/11/17, 017
 * @description
 */
abstract class BaseFragment<VB : ViewDataBinding> : Fragment() {
    private var isViewCreated: Boolean = false
    private var currentVisibleState: Boolean = false
    private var isFirstVisible: Boolean = true
    protected lateinit var vb: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vb = DataBindingUtil.inflate(inflater, getLayoutResId(), container, false)
        isViewCreated = true
        if (!isHidden && userVisibleHint) {
            dispatchVisibleState(true)
        }
        //点击穿越
        vb.root.isClickable = true
        initViews()
        initData()
        return vb.root
    }

    abstract fun getLayoutResId(): Int

    abstract fun initViews()

    abstract fun initData()

    override fun onResume() {
        super.onResume()
        if (!isFirstVisible) {
            if (!isHidden && !currentVisibleState && userVisibleHint) {
                dispatchVisibleState(true)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (currentVisibleState && userVisibleHint)
            dispatchVisibleState(false)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isViewCreated) {
            if (currentVisibleState && !isVisibleToUser) {
                dispatchVisibleState(false)
            } else if (!currentVisibleState && isVisibleToUser) {
                dispatchVisibleState(true)
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        dispatchVisibleState(!hidden)
    }

    private fun dispatchVisibleState(isVisible: Boolean) {
        if (isVisible && isParentInvisible()) return
        if (isVisible == currentVisibleState) return
        currentVisibleState = isVisible
        if (isVisible) {
            if (isFirstVisible) {
                isFirstVisible = false
                onFragmentFirstVisible()
            }
            onFragmentResume()
            dispatchChildVisibilityState(true)
        } else {
            onFragmentPause()
            dispatchChildVisibilityState(false)
        }
    }

    private fun isParentInvisible(): Boolean {
        val parent = parentFragment
        if (parent is BaseFragment<*>) {
            val lz: BaseFragment<*> = parent
            return !lz.currentVisibleState
        }
        return false
    }

    private fun dispatchChildVisibilityState(isVisible: Boolean) {
        val fragmentManager = childFragmentManager
        fragmentManager.fragments.forEach {
            if (it is BaseFragment<*> && !it.isHidden && it.userVisibleHint) {
                it.dispatchVisibleState(isVisible)
            }
        }
    }

    protected fun onFragmentResume() {
        Log.i(this.javaClass.simpleName, "onFragmentResume: ")
    }

    protected fun onFragmentPause() {
        Log.i(this.javaClass.simpleName, "onFragmentPause: ")
    }

    protected fun onFragmentFirstVisible() {
        Log.i(this.javaClass.simpleName, "onFragmentFirstVisible: ")
    }

    fun toast(string: String) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("ResourceType")
    fun toast(@IdRes idRes: Int) {
        Toast.makeText(context, idRes, Toast.LENGTH_SHORT).show()
    }
}