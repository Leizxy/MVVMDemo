package cn.leizy.mvvmdemo

import android.util.Log
import cn.leizy.base.v.BaseMVVMActivity
import cn.leizy.mvvmdemo.databinding.ActivityMainBinding

class MainActivity : BaseMVVMActivity<MainViewModel, ActivityMainBinding>() {

    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }

    override fun initViews() {
    }

    override fun initData() {
        viewModel.apply {
            stringData.observe(this@MainActivity, {
                vb.test.text = it
                Log.i("MainActivity", "onCreate: $it")
            })
        }.initString()
    }
}