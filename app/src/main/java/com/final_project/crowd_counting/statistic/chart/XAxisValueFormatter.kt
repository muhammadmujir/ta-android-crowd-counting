package com.final_project.crowd_counting.statistic.chart

import com.github.mikephil.charting.formatter.ValueFormatter

class XAxisValueFormatter(private val dates: List<String>): ValueFormatter() {

//  override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//    return value.toString()+"x"
//  }

  override fun getFormattedValue(value: Float): String {
    val index = Math.round(value)
//    Log.d("index", "ori:"+ value +", after: "+index.toString())
    if (index >= 0 && index < dates.size)
      return dates[index]
    else
      return "no label"
  }
}