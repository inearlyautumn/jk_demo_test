package cn.fover.jk_demo

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_coroutines.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import okhttp3.*
import java.io.IOException
import java.util.concurrent.Executors

class CoroutineActivity : AppCompatActivity() {

    //我待会再初始化，确保textview不会为空
    private lateinit var textView: TextView
    private val url = "https://www.baidu.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutines)

        textView = main_tv_hello

        main_btn_thread.setOnClickListener { thread_1000() }
        main_btn_coroutine.setOnClickListener { coroutine_1000() }

        main_btn_thread_update.setOnClickListener { normal_update_ui() }

        main_btn_coroutine_update.setOnClickListener { coroutine_update_ui() }
/*        main_btn_coroutine_update.setOnClickListener {
            pack_net(url, {
                //lambada的方式
                textView.text = "我是通过网络封闭请求之后改变的"
            }, {
                response ->
                Log.i("fover", "error ${response.code()}")
            })
        }*/
    }

    private fun coroutine_update_ui() {
        val okHttp = OkHttpClient()
        val request = Request.Builder().url(url).build()
        launch(CommonPool) {
            val response = okHttp.newCall(request).execute()
            if (response.isSuccessful) {
                launch(UI) {
                    textView.text = "Hello, 我是通过协程更新的"
                }
            }
        }
    }

    private fun normal_update_ui() {
        val okHttp = OkHttpClient()
        val request = Request.Builder().url(url).build()
        okHttp.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("Fover", "some errors >> ${e?.printStackTrace()}")
            }

            override fun onResponse(call: Call?, response: Response?) {
                if (response!!.isSuccessful) {
                    textView.post {
                        textView.text = "Hello, 我是通过线程更新的"
                    }
                }
            }
        })
    }

    /**
     * 启动1000个协程
     * */
    private fun coroutine_1000() {
        val pool = newFixedThreadPoolContext(10, "coroutine")
        for (x in 0..1000) {
            pool.run { write_sp("Coroutine", x.toString(), x) }
        }
    }

    /**
     * 启动1000个线程
     * */
    private fun thread_1000() {
        val pool = Executors.newFixedThreadPool(10)
        for (x in 0..1000) {
            pool.execute { write_sp("Thread", x.toString(), x) }
        }
    }

    /**
     * 写入SharedPreference
     * @param way 方式
     * @param name key的名称
     * @param value 值
     * */
    private fun write_sp(way: String, name: String, value: Int) {
        val sp = getSharedPreferences("Jikexueyuan", Context.MODE_PRIVATE)
        sp.edit().putInt(name, value).apply()
        val count = sp.getInt(name, 0)
        println("调用方式：$way key值为: $name 取出来的count值为: $count")
    }

    fun pack_net(url: String, succeed: (Response) -> Unit, error: (Response) -> Unit) {
        val okHttp = OkHttpClient()
        val request = Request.Builder().url(url).build()
        launch(CommonPool) {
            val response = okHttp.newCall(request).execute()
            if (response.isSuccessful) {
                launch(UI) {
                    succeed(response)
                }
            } else {
                error(response)
            }
        }
    }
}
