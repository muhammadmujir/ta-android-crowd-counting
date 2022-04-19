package com.final_project.crowd_counting.base.view.rv.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.final_project.crowd_counting.base.R

class CountryListAdapter(
  context: Context,
  list: List<Pair<String, Drawable>>
): BaseArrayAdapter<Pair<String,Drawable>>(context,R.layout.dropdown_item_country,list) {

  override fun bind(view: View, model: Pair<String, Drawable>) {
    view.findViewById<ImageView>(R.id.iv_flag).run {
      setImageDrawable(model.second)
      Log.d("SET BACK"+model.second.toString(), "YES")
    }
    view.findViewById<TextView>(R.id.tv_country_name).text = model.first
  }

  override fun filterMatch(data: Pair<String, Drawable>, query: String): Boolean {
    Log.d("QUERY: ", data.toString()+" :: "+query)
    return data.first.startsWith(query,true)
  }

}