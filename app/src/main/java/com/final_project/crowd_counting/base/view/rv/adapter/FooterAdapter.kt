package com.final_project.crowd_counting.base.view.rv.adapter

abstract class FooterAdapter<T>: BaseRvAdapter<T>(){

  companion object{
    const val VIEW_TYPE_CONTENT = 0
    const val VIEW_TYPE_FOOTER = 1
  }

  protected var isFooterVisible = false

  fun addFooter(value: T, isNotified: Boolean = true){
    isFooterVisible = true
    mutableItems.add(value)
    if (isNotified)
      notifyItemInserted(mutableItemCount - 1)
  }

  fun removeFooter(isNotified: Boolean = true){
    isFooterVisible = false
    if (mutableItems.isNotEmpty()){
      mutableItems.removeAt(mutableItemCount-1)
      if (isNotified)
        notifyItemRemoved(mutableItemCount-1)
    }
  }
}