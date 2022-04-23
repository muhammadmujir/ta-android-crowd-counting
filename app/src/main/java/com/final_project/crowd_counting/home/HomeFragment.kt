package com.final_project.crowd_counting.home

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.final_project.crowd_counting.R
import com.final_project.crowd_counting.base.model.CameraStreamRequest
import com.final_project.crowd_counting.base.model.CameraStreamResponse
import com.final_project.crowd_counting.base.view.BaseFragment
import com.final_project.crowd_counting.databinding.FragmentHomeBinding
import com.final_project.crowd_counting.home.viewmodel.HomeViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import java.net.URI

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
  private var param1: String? = null
  private var param2: String? = null
  private val viewModel: HomeViewModel by viewModels()
  private lateinit var mSocket: Socket
  private val gson = Gson()

  private fun printMessage(message: String) {
    viewBinding.tvTitle.text = message
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      param1 = it.getString(ARG_PARAM1)
      param2 = it.getString(ARG_PARAM2)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initWebSocket()
    viewBinding.btnToCameraDetail.setOnClickListener {
      findNavController().navigate(R.id.to_camera_detail, Bundle().apply {
        putString(ARG_RTSP_ADDRESS, viewBinding.etText.text.toString())
      })
    }
    viewBinding.btnPlayVideo.setOnClickListener { initVideoView() }
  }

  private fun initVideoView() {
    val mediaController = MediaController(requireContext()).apply { setAnchorView(viewBinding.vvCamera) }
    with(viewBinding.vvCamera) {
      setOnCompletionListener {
        Toast.makeText(requireContext(), "Video Complete", Toast.LENGTH_SHORT).show()
      }
      setOnErrorListener { mediaPlayer, i, i2 ->
        Toast.makeText(requireContext(), "Video Error", Toast.LENGTH_SHORT).show()
        false
      }
      setMediaController(mediaController)
      setVideoURI(Uri.parse(viewBinding.etText.text.toString()))
//      setVideoPath()
      requestFocus()
      start()
    }
  }


  private fun initWebSocket(){
    viewBinding.btnSend.setOnClickListener { start() }
    viewBinding.btnJoinRoom.setOnClickListener { joinRoom() }
  }

  private fun stopWebSocket(){
    mSocket.emit("leave", gson.toJson(CameraStreamRequest(10)))
    mSocket.disconnect()
  }

  private fun start(){
    try {
//      This address is the way you can connect to localhost with AVD(Android Virtual Device)
//      mSocket = IO.socket("http://192.168.43.194:5000/")
//      Log.d("success", mSocket.id().toString())
      val manager = Manager(URI("http://192.168.43.194:5000/"))
      mSocket = manager.socket("/camera")
    } catch (e: Exception) {
      e.printStackTrace()
      Log.d("fail", "Failed to connect")
    }

    mSocket.connect()
//    //Register all the listener and callbacks here.
    mSocket.on(Socket.EVENT_CONNECT, onConnect)
    mSocket.on("connect_response", onConnectResponse)
    mSocket.on("my_response", onJoinRoom)// To know if the new user entered the room.
  }

  private fun joinRoom(){
    mSocket.emit("join", gson.toJson(CameraStreamRequest(10)))
  }

  var onConnect = Emitter.Listener {
//    Toast.makeText(requireContext(), "Connected", Toast.LENGTH_SHORT).show()
  }

  var onConnectResponse = Emitter.Listener {
    viewBinding.tvTitle.text = it[1].toString()
  }

  var onJoinRoom = Emitter.Listener {
    val response = gson.fromJson(it[0].toString(), CameraStreamResponse::class.java)
    viewBinding.tvTitle.text = response.count.toString()
//    viewBinding.tvTitle.text = it[1].toString()
  }

  override fun getVM(): HomeViewModel = viewModel

  override fun shouldHandleLoading(): Boolean = false

  override fun getFragmentBinding(
    inflater: LayoutInflater, container: ViewGroup?
  ): FragmentHomeBinding {
    return FragmentHomeBinding.inflate(inflater, container, false)
  }

  override fun shouldResetToolbarView(): Boolean = true

  override fun onDestroy() {
    super.onDestroy()
    //Before disconnecting, send "unsubscribe" event to server so that
    //server can send "userLeftChatRoom" event to other users in chatroom
    stopWebSocket()
  }

  companion object {
    @JvmStatic fun newInstance(param1: String, param2: String) = HomeFragment().apply {
      arguments = Bundle().apply {
        putString(ARG_PARAM1, param1)
        putString(ARG_PARAM2, param2)
      }
    }
  }
}