package me.hgj.jetpackmvvm.demo.ui.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.hgj.jetpackmvvm.demo.R

/**
 * @author : hgj
 * @date   : 2020/9/7
 * 海王测试Adapter，以后海王再问 问题 发红包
 */

class TestAdapter(data: ArrayList<String>) :BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_integral, data) {

    var clickAction: (position: Int, item: String) -> Unit = { _, _-> }

    override fun convert(holder: BaseViewHolder, item: String) {
        holder.getView<TextView>(R.id.item_integral_rank).text = item
        holder.getView<TextView>(R.id.item_integral_rank).setOnClickListener { var1 ->
            clickAction.invoke(holder.adapterPosition, item)
        }
    }
}


