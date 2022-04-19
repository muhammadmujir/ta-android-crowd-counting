package com.final_project.crowd_counting.base.communicator

import android.os.Bundle
import androidx.fragment.app.Fragment

const val KEY_DATA = "keyData"

interface IViewPagerManager{
  fun changeFragment(position: Int, fragment: Fragment)
  fun getCurrentItem(): Pair<Int, Fragment>
  fun setData(data: Bundle)
}