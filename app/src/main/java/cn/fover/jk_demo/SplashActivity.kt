package cn.fover.jk_demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by apple on 2017/6/11.
 */
class SplashActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_splash)
    }
}