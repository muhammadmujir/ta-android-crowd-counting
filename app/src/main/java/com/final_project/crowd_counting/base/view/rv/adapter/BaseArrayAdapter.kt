package com.final_project.crowd_counting.base.view.rv.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter

abstract class BaseArrayAdapter<T>(
  mContext: Context,
  private val itemLayout: Int,
  private var dataList: List<T>
) : ArrayAdapter<T>(mContext, itemLayout, dataList) {

  private val listFilter: ListFilter = ListFilter()
  private var dataListAllItems: List<T>? = null

  override fun getCount(): Int {
    return dataList.size
  }

  override fun getItem(position: Int): T? {
    return dataList[position]
  }

  override fun getFilter(): Filter {
    return listFilter
  }

  override fun getView(position: Int, view: View?, parent: ViewGroup): View {
    Log.d("GET VIEW", "YES")
    var view = view
    if (view == null) {
      view = LayoutInflater.from(parent.context).inflate(itemLayout, parent, false)
    }
    //    view!!.setBackgroundColor(mContext.resources.getColor(android.R.color.transparent))
    //    val strName = view.findViewById<TextView>(R.id.tv_country_name)
    //    strName.text = getItem(position)
    getItem(position)?.let {
      bind(view!!, it)
    }
    return view!!
  }

  abstract fun bind(view: View, data: T)

  abstract fun filterMatch(data: T, query: String): Boolean

  inner class ListFilter : Filter() {
    private val lock = Any()
    override fun performFiltering(prefix: CharSequence?): FilterResults {
      Log.d("PERFORM FILTER: "+prefix, "YES")
      val results = FilterResults()
      if (dataListAllItems == null) {
        synchronized(lock) { dataListAllItems = dataList }
      }
      if (prefix == null || prefix.length == 0) {
        synchronized(lock) {
          results.values = dataListAllItems
          results.count = dataListAllItems!!.size
        }
      } else {
        val searchStrLowerCase = prefix.toString().toLowerCase()
        val matchValues = ArrayList<T>()
        for (dataItem in dataListAllItems!!) {
          Log.d("DATA ITEM", dataItem.toString())
          if (filterMatch(dataItem, searchStrLowerCase)){
            matchValues.add(dataItem)
          }
        }
        results.values = matchValues
        results.count = matchValues.size
      }
      return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
      Log.d("PUBLISH RESULT", "YES")
      results.values?.let {
        Log.d("PUBLISH", it.toString())
        dataList = it as ArrayList<T>
        Log.d("DATA LIST", dataList.toString())
      } ?: run {
        Log.d("NO RESULT", "YES")
      }
      if (results.count > 0) {
        notifyDataSetChanged()
      } else {
        notifyDataSetInvalidated()
      }
    }
  }

}