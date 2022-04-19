package com.final_project.crowd_counting.base.view.rv.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.final_project.crowd_counting.base.R
import com.final_project.crowd_counting.base.model.ShippingOption
import com.final_project.crowd_counting.base.utils.Util.formatRp

class ShippingListAdapter(
  context: Context,
  list: List<ShippingOption>
): BaseArrayAdapter<ShippingOption>(context,R.layout.dropdown_item_shipping,list) {

  override fun bind(view: View, model: ShippingOption) {
    with(view){
      findViewById<ImageView>(R.id.iv_logo).run {
        setImageDrawable(ContextCompat.getDrawable(context, model.icon))
      }
      findViewById<TextView>(R.id.tv_shipping).text = model.name
      findViewById<TextView>(R.id.tv_shipping_price).text = model.cost.formatRp()
    }
  }

  override fun filterMatch(data: ShippingOption, query: String): Boolean {
    Log.d("QUERY: ", data.toString()+" :: "+query)
    return data.name.startsWith(query,true)
  }

}