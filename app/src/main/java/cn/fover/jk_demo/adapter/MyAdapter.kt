package cn.fover.jk_demo.adapter

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.fover.jk_demo.DetailActivity
import cn.fover.jk_demo.R
import cn.fover.jk_demo.entity.ItemEntity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.adapter_list.view.*

/**
 * Created by apple on 2017/6/11.
 */
class MyAdapter(val mList:MutableList<ItemEntity>,val type:String):RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(p0: ViewHolder?, p1: Int) {
        p0!!.bind(mList[p1])
    }

    override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(p0!!.context)
                .inflate(R.layout.adapter_list,p0,false))

    }


    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        fun bind(itemEntity: ItemEntity) {
            itemView.adapter_title.text=itemEntity.title
            itemView.adapter_des.text=itemEntity.describe
            itemView.adapter_time_and_class.text=itemEntity.time_and_class

            if(itemEntity.is_like){
                itemView.adapter_like.setImageResource(R.mipmap.ic_love_select)
            }else{
                itemView.adapter_like.setImageResource(R.mipmap.ic_love_normal)
            }

            Picasso.with(itemView.context).load(itemEntity.image).into(itemView.adapter_img)

            itemView.setOnClickListener {
                val intent= Intent(itemView.context,DetailActivity::class.java)
                intent.putExtra("url",itemEntity.url)
                intent.putExtra("title",itemEntity.title)
                intent.putExtra("is_like",itemEntity.is_like)
                intent.putExtra("type",type)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                itemView.context.startActivity(intent)
            }
        }

    }
}