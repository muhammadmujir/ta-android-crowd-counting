package com.final_project.crowd_counting.statistic

import android.graphics.DashPathEffect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.final_project.crowd_counting.R
import com.final_project.crowd_counting.base.communicator.ActivityObserver
import com.final_project.crowd_counting.base.communicator.IToolbarCommunicator
import com.final_project.crowd_counting.base.source.network.ResponseWrapper
import com.final_project.crowd_counting.base.utils.Util.dateToMillis
import com.final_project.crowd_counting.base.utils.Util.millisToDate
import com.final_project.crowd_counting.base.view.BaseFragment
import com.final_project.crowd_counting.databinding.FragmentStatisticBinding
import com.final_project.crowd_counting.statistic.chart.XAxisValueFormatter
import com.final_project.crowd_counting.statistic.model.Statistic
import com.final_project.crowd_counting.statistic.model.StatisticRequest
import com.final_project.crowd_counting.statistic.viewmodel.StatisticViewModel
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

private const val SELECTED_CAMERA_INDEX = "selectedCameraIndex"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class StatisticFragment : BaseFragment<FragmentStatisticBinding,StatisticViewModel>() {

  private val viewModel: StatisticViewModel by viewModels()
  private var selectedCameraIndex: Int = 0
  private val timeOffset = TimeZone.getDefault().getOffset(System.currentTimeMillis())
  private val cameraListAdapter by lazy { ArrayAdapter<String>(requireContext(), R.layout.item_only_textview) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    savedInstanceState?.let {
      selectedCameraIndex = it.getInt(SELECTED_CAMERA_INDEX, 0)
    }
  }

  override fun getVM(): StatisticViewModel = viewModel

  override fun shouldHandleLoading(): Boolean = true

  override fun getFragmentBinding(
    inflater: LayoutInflater, container: ViewGroup?
  ): FragmentStatisticBinding {
    requireActivity().lifecycle.addObserver(ActivityObserver{
      (requireActivity() as IToolbarCommunicator).setToolbarVisibility(false)
    })
    return FragmentStatisticBinding.inflate(inflater, container, false)
  }

  override fun shouldResetToolbarView(): Boolean = false

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
//    val millis = dateToMillis("Tue, 19 Apr 2022 22:00:00 GMT", "E, dd MMMM yyyy HH:mm:ss z")
//    val date = millisToDate(millis, "yyyy-MM-dd HH:mm:ssZ")
//    Log.d("MYMILLIS", millis.toString())
//    Log.d("MYDATE", date)
//    Log.d("MYOFFSET", TimeZone.getDefault().getOffset(System.currentTimeMillis()).toString())
    with(viewBinding){
      (tiCamera.editText as? AutoCompleteTextView)?.apply {
        setAdapter(cameraListAdapter)
        setOnItemClickListener { _, _, i, _ ->
          selectedCameraIndex = i
        }
      }
      tiStartDate.setEndIconOnClickListener { showDatePicker(0) }
      tiEndDate.setEndIconOnClickListener { showDatePicker(1) }
      etStartDate.keyListener = null
      etEndDate.keyListener = null
    }
    initChart()
    initObserver()
  }

  private fun initObserver(){
    viewModel.camera.observe(viewLifecycleOwner, {
      if (it.status == ResponseWrapper.Status.SUCCESS){
        it.body?.data?.map { getString(R.string.camera_id_location, it.id.toString(), it.location) }?.let {
          cameraListAdapter.addAll(it)
        }
      } else {
        viewBinding.tiCamera.error = getString(R.string.failed_load_camera_list)
      }
    })

    viewModel.statistic.observe(viewLifecycleOwner, {
      if (it.status == ResponseWrapper.Status.SUCCESS){
        it.body?.data?.let {
          renderDataAsChart(it)
        }
      } else {
        Toast.makeText(requireContext(), it.body?.errors?.getOrNull(0).toString(), Toast.LENGTH_SHORT)
          .show()
      }
    })

  }

  private fun initChart(){
    with(viewBinding.lcChart){
      setTouchEnabled(true)
      setPinchZoom(true)
      xAxis.run {
        position = XAxis.XAxisPosition.BOTTOM
        labelRotationAngle = 315F
        // The granularity determines interval for axis label value (float) increment starting from 0.0
        // ex: if granularity = 1.0, then the value passing to ValueFormatter.getFormattedValue(value: Float)
        // are 0.0, 1.0, 2.0, 3.0, etc
        isGranularityEnabled = true
        granularity = 1F
//        enableAxisLineDashedLine(2F, 7F, 0F)
//        axisMinimum = 0F
//        axisMaximum = 10F
//        labelCount = 6
//        setDrawLimitLinesBehindData(false)
      }
      axisLeft.run {
        enableGridDashedLine(10F, 10F, 0F)
        setDrawZeroLine(false)
//        setDrawLimitLinesBehindData(false)
//        axisMinimum = 0F
//        axisMaximum = 300F
      }
      axisRight.isEnabled = false
      description = Description().apply {
        textSize = 15F
        text = "Dates"
      }
    }
  }

  private fun renderDataAsChart(data: List<Statistic>){
    val xLabels = mutableListOf<String>()
    val entries = data.mapIndexed { index, it ->
      it.timestamp?.let { dateToMillis(it) }?.let { date ->
        xLabels.add(millisToDate(date, "yyyy-MM-dd HH:mm"))
        Entry(index.toFloat(), it.crowdCount.toFloat())
      }
    }.filterNotNull()
    with(viewBinding.lcChart){
      if (getData() != null){
        val dataset = getData().getDataSetByIndex(0) as LineDataSet
        dataset.values = entries
        getData().notifyDataChanged()
        notifyDataSetChanged()
      } else {
        val lineDataset = LineDataSet(entries, "crowd").apply {
          setDrawCircles(true)
          enableDashedLine(10F, 0F, 0F)
          enableDashedHighlightLine(10F, 0F, 0F)
          setColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
          setCircleColor(ContextCompat.getColor(requireContext(), R.color.black))
          lineWidth = 2F
          setCircleRadius(5F)
          setDrawCircleHole(true)
          valueTextSize = 10F
          setDrawFilled(true)
          formLineWidth = 5F
          formLineDashEffect = DashPathEffect(floatArrayOf(10F, 5F), 0F)
          formSize = 5F
          setDrawValues(true)
        }
        this.data = LineData(arrayListOf(lineDataset as ILineDataSet))
      }
      xAxis.valueFormatter = XAxisValueFormatter(xLabels)
      xAxis.labelCount = xLabels.size
      invalidate()
    }
  }

  private fun showDatePicker(key: Int = 0, message: String = "Select Date"){
    val datePickerBuilder = MaterialDatePicker.Builder.datePicker().setTitleText(message)

    viewBinding.apply {
      if (key == 0 && !etStartDate.text.isNullOrEmpty()){
        datePickerBuilder.setSelection(dateToMillis(etStartDate.text.toString(), "yyyy-MM-dd HH:mm:ssZ")+timeOffset)
      } else if (key == 1 && !etEndDate.text.isNullOrEmpty()){
        datePickerBuilder.setSelection(dateToMillis(etEndDate.text.toString(), "yyyy-MM-dd HH:mm:ssZ")+timeOffset)
      }

      val datePicker = datePickerBuilder.build()
      datePicker.addOnPositiveButtonClickListener { selection ->
        when(key){
          0 -> {
            etStartDate.setText(millisToDate(selection-timeOffset))
            if (!etEndDate.text.isNullOrEmpty())
              getStatistic()
          }
          else -> {
            etEndDate.setText(millisToDate(selection-timeOffset))
            if (!etStartDate.text.isNullOrEmpty())
              getStatistic()
          }
        }
      }
      childFragmentManager.beginTransaction().add(datePicker, "").commitNow()
    }
  }

  private fun getStatistic(){
    viewModel.camera.value?.body?.data?.getOrNull(selectedCameraIndex)?.id?.let { cameraId ->
      if (!viewBinding.etStartDate.text.isNullOrEmpty() && !viewBinding.etEndDate.text.isNullOrEmpty()){
        viewModel.getStatistic(cameraId,
          StatisticRequest(viewBinding.etStartDate.text.toString(),
            viewBinding.etEndDate.text.toString())
        )
      }
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.apply {
      putInt(SELECTED_CAMERA_INDEX, selectedCameraIndex)
    }
  }

}