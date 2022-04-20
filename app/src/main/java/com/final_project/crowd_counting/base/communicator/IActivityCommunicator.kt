package com.final_project.crowd_counting.base.communicator

import android.os.Bundle
import androidx.fragment.app.Fragment

interface IActivityCommunicator {
  fun replaceFragment(fragment: Fragment?, tag: String?, bundle: Bundle?, isAddedToBackStack: Boolean)
}