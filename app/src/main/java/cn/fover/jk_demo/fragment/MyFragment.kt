package cn.fover.jk_demo.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.fover.jk_demo.adapter.MyAdapter
import cn.fover.jk_demo.db.database
import cn.fover.jk_demo.entity.ItemEntity
import cn.fover.jk_demo.entity.LikeModel
import cn.fover.jk_demo.event.LikeEvent
import cn.fover.jk_demo.utils.RxBus
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import org.jsoup.Jsoup

/**
 * Created by apple on 2017/6/11.
 * AnkoLogger
 */
class MyFragment : Fragment(), AnkoLogger {

    private var mRecycleView: RecyclerView? = null
    private var mList = mutableListOf<ItemEntity>()
    private var mType: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mRecycleView == null) {
            mRecycleView = RecyclerView(activity)
            mRecycleView!!.layoutManager = LinearLayoutManager(activity)

            val url = arguments.getString("url")
            mType = arguments.getString("type")

            initRecycleView(url)

            registerRxBus()

        }
        return mRecycleView
    }

    private fun registerRxBus() {
        RxBus.getInstance().toObservable().subscribe { event ->
            when (event) {
            //LikeEvent：创建事件，对进行分类
                is LikeEvent -> handleLikeEvent(event)
            }
        }

    }

    private fun handleLikeEvent(event: LikeEvent) {
        if (event.type == mType) {
            save_database(event)
            update_ui(event)
        }
    }

    private fun update_ui(event: LikeEvent) {
        //kotlin提供的对Iterable的扩展，通过it.url得到每个遍历出来的url，再通过froEach对处理
        mList.filter { "http:${it.url}" == event.url }.forEach {
            it.is_like = event.is_like
        }
        mRecycleView!!.adapter.notifyDataSetChanged()
    }

    private fun save_database(event: LikeEvent) {
        async(UI) {
            val task = bg {
                activity.database.use {
                    //whereArgs：where的查询条件
                    select("Like", "id").whereArgs("(type={type}) and (url={url})",
                            "type" to event.type,
                            "url" to event.url).exec {
                        //exec：的作用是select执行完后，就继续执行select的结果
                        if (count > 0) {//count：查询到的记录数
                            //update
                            update("Like", "is_like" to event.is_like)
                                    .whereArgs("(type={type}) and (url={url})",
                                            "type" to event.type,
                                            "url" to event.url).exec().toLong()//toLong()：返回一个long类型给task
                        } else {
                            //insert
                            insert("Like", "type" to mType,
                                    "url" to event.url,
                                    "is_like" to event.is_like)
                        }
                    }
                }
            }

            task.await()
            //在activity的环境下，
            with(activity) {
                toast("$mType ${event.url}的类型被更新为${event.is_like}")
            }
        }
    }

    private fun initRecycleView(url: String) {
        //async是anko给我们封装的，在github上kotlin->Anko Coroutines点击(wiki)
        async(UI) {
            //异步执行在bg{...}中，在这个依赖中"org.jetbrains.anko:anko-coroutines:$anko_version"
            val result = bg {
                //Jsoup：html的语法分析器
                val jsoup = Jsoup.connect(url).get()
                Log.i("MyFragment", "---00 jsoup = " + jsoup)
                val uls = jsoup.select("ul.cf")
                for (x in uls) {
                    val lis = x.select("li")
                    for (i in lis) {
                        val title = i.select("div.lesson-infor > h2 > a").text()
                        val item_url = i.select("div.lesson-infor > h2 > a").attr("href")
                        val describe = i.select("div.lesson-infor > p").text()
                        val image = i.select("div.lessonimg-box > a > img").attr("src")
                        val time_and_class = i.select("div.lesson-infor > div > div:nth-child(1) > dl > dd.mar-b8 > em").text()

                        Log.i("MyFragment", "---00 title = " + title)

                        val entity = ItemEntity(title = title, url = item_url, describe = describe, image = image
                                , time_and_class = time_and_class, is_like = false)
                        mList.add(entity)
                    }
                }

                val DBList = activity.database.use {
                    //将查询的结果转化为一个对应的对象
                    val parser = classParser<LikeModel>()
                    select("Like", "*").whereArgs("(type={type}) and (is_like={is_like})",
                            "type" to mType!!,
                            "is_like" to true)
                            .parseList(parser)
                }

                //在mList集合中的遍历操作
                mList.forEach {
                    val itemEntity = it
                    //返回只包含与给定谓词匹配的元素的列表。
                    DBList.filter {
                        "http:" + itemEntity.url == it.url
                    }.forEach {
                                //info{...} anko提供的打印日志的方法
                                info { "change url is ${it.url}" }
                                itemEntity.is_like = true
                            }
                }

            }
            //上面的代码执行完成后再执行下面的方法
            result.await()
            //！！不可能为空，不为空的状态
            mRecycleView!!.adapter = MyAdapter(mList, mType!!)

        }

    }
}