package com.final_project.crowd_counting.base.view.rv.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.final_project.crowd_counting.base.utils.Util.removeRange

abstract class BaseRvAdapter<T>: RecyclerView.Adapter<BaseRvAdapter.BaseRvViewHolder<T>>() {
  protected var mutableItems: MutableList<T> = mutableListOf()
  protected val mutableItemCount
    get() = mutableItems.size
  protected var screenWidth: Int = 0

  override fun getItemCount(): Int {
    return mutableItemCount
  }

  override fun onBindViewHolder(holder: BaseRvViewHolder<T>, position: Int) {
    holder.bind(mutableItems[position%mutableItemCount], position)
  }

  open fun setItemList(itemList: List<T>, isNotified: Boolean = true) {
    mutableItems = itemList.toMutableList()
    if (isNotified)
      notifyDataSetChanged()
  }

  fun addItemList(itemList: List<T>, isNotified: Boolean = true){
    if (mutableItemCount == 0) {
      mutableItems = itemList.toMutableList()
      if (isNotified)
        notifyDataSetChanged()
    } else {
      val prevCount = mutableItemCount
      mutableItems.addAll(itemList)
      if (isNotified)
        notifyItemRangeInserted(prevCount-1, itemList.size)
    }
  }

  fun addItemAt(position: Int, data: T, isNotified: Boolean = true){
    mutableItems.add(position, data)
    if (isNotified)
      notifyItemInserted(position)
  }

  fun clearData(){
    mutableItems.clear()
    notifyDataSetChanged()
  }

  fun removeAt(position: Int, isNotified: Boolean = true){
    if (position < mutableItemCount)
      mutableItems.removeAt(position)
    if (isNotified)
      notifyItemRemoved(position)
  }

  fun removeRange(start: Int, itemCount: Int, isNotified: Boolean = true){
    mutableItems.removeRange(start, itemCount)
    if (isNotified)
      notifyItemRangeRemoved(start, itemCount)
  }

  protected fun initScreenWidth(context: Context){
    val displayMetrics = context.resources.displayMetrics
    screenWidth = displayMetrics.widthPixels
  }

  abstract class BaseRvViewHolder<T>(itemView: View): RecyclerView.ViewHolder(itemView){
    abstract fun bind(data: T, position: Int? = null)
  }
}