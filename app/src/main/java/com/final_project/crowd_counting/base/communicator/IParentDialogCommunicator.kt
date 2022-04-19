package com.final_project.crowd_counting.base.communicator

import android.os.Bundle

interface IParentDialogCommunicator {
  fun submit(key: Int?, data: Bundle?)
}