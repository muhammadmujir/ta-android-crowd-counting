package com.final_project.crowd_counting.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.final_project.crowd_counting.R
import com.final_project.crowd_counting.base.model.CameraStreamRequest
import com.final_project.crowd_counting.base.model.CameraStreamResponse
import com.final_project.crowd_counting.base.view.BaseFragment
import com.final_project.crowd_counting.databinding.FragmentHomeBinding
import com.final_project.crowd_counting.home.viewmodel.HomeViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.*
import okio.ByteString
import okio.ByteString.Companion.decodeHex
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

  inner class EchoWebSocketListener : WebSocketListener() {
    private val CLOSE_STATUS = 1000
    override fun onOpen(webSocket: WebSocket, response: Response) {
      webSocket.send("What's up ?")
      webSocket.send("abcd".decodeHex())
      webSocket.close(CLOSE_STATUS, "Socket Closed !!")
    }

    override fun onMessage(webSocket: WebSocket, message: String) {
      printMessage("Receive Message: $message")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
      printMessage("Receive Bytes : " + bytes.hex())
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
      webSocket.close(CLOSE_STATUS, null)
      printMessage("Closing Socket : $code / $reason")
    }

    override fun onFailure(webSocket: WebSocket, throwable: Throwable, response: Response?) {
      print("Error : " + throwable.message)
    }
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
//    initVideoView()
    initWebSocket()
  }

  private fun initVideoView(){
    val mediaController = MediaController(requireContext()).apply { setAnchorView(viewBinding.vvCamera) }
    with(viewBinding.vvCamera){
      setOnCompletionListener {
        Toast.makeText(requireContext(), "Video Complete", Toast.LENGTH_SHORT).show()
      }
      setOnErrorListener { mediaPlayer, i, i2 ->
        Toast.makeText(requireContext(), "Video Error", Toast.LENGTH_SHORT).show()
        false
      }
      setMediaController(mediaController)
      setVideoPath("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
      requestFocus()
      start()
    }
  }

  private fun initWebSocket(){
    viewBinding.btnSend.setOnClickListener { start() }
    viewBinding.btnJoinRoom.setOnClickListener { joinRoom() }
  }

  private fun start(){
    try {
      //This address is the way you can connect to localhost with AVD(Android Virtual Device)
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

  companion object {
    @JvmStatic fun newInstance(param1: String, param2: String) = HomeFragment().apply {
      arguments = Bundle().apply {
        putString(ARG_PARAM1, param1)
        putString(ARG_PARAM2, param2)
      }
    }
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
    mSocket.emit("leave", gson.toJson(CameraStreamRequest(10)))
    mSocket.disconnect()
  }

}