package cn.fover.jk_demo.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import cn.fover.jk_demo.fragment.MyFragment

/**
 * Created by apple on 2017/6/11.
 */
class ViewPagerAdapter(val fm:FragmentManager):FragmentPagerAdapter(fm) {

    val items= listOf("Android","IOS","PHP","JavaScript","Python")
    val urls= listOf("http://www.jikexueyuan.com/path/android",
            "http://www.jikexueyuan.com/path/ios",
            "http://www.jikexueyuan.com/path/php",
            "http://www.jikexueyuan.com/path/javascript",
            "http://www.jikexueyuan.com/path/python")

    override fun getItem(p0: Int): Fragment {
        val bundle=Bundle()
        bundle.putString("url",urls[p0])
        bundle.putString("type",items[p0])
        val fragment=MyFragment()
        fragment.arguments=bundle
        return fragment

    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return items[position]
    }
}