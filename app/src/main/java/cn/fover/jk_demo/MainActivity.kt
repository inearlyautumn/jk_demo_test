package cn.fover.jk_demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import cn.fover.jk_demo.adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()

        main_viewpager.adapter=ViewPagerAdapter(supportFragmentManager)
        main_tab_layout.setupWithViewPager(main_viewpager)
    }
}
