package com.final_project.crowd_counting.base.view.rv.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class BaseViewPagerAdapter(fragment: Fragment, private val fragmentList:MutableList<Fragment>): FragmentStateAdapter(fragment) {

  private var fragmentIds = fragmentList.map { it.hashCode().toLong() }

  override fun getItemCount(): Int {
    return fragmentList.size
  }

  override fun createFragment(position: Int): Fragment {
    return fragmentList[position]
  }

  override fun getItemId(position: Int): Long {
    return fragmentList[position].hashCode().toLong()
  }

  override fun containsItem(itemId: Long): Boolean {
    return fragmentIds.contains(itemId)
  }

  fun changeFragment(position: Int, mFragment: Fragment){
    fragmentList.removeAt(position)
    fragmentList.add(position, mFragment)
    fragmentIds = fragmentList.map { it.hashCode().toLong() }
    notifyItemChanged(position)
  }

  fun getCurrentItem(position: Int): Fragment {
    return fragmentList[position]
  }
}