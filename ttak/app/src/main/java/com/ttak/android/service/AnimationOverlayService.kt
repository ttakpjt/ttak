//package com.ttak.android.service
//
//import android.app.Service
//import android.content.Intent
//import android.graphics.PixelFormat
//import android.os.IBinder
//import android.util.Log
//import android.view.Surface
//import android.view.SurfaceHolder
//import android.view.SurfaceView
//import android.view.WindowManager
//import com.ttak.android.utils.DrawThread
//
//class AnimationOverlayService : Service() {
//    private var windowManager: WindowManager? = null
//    private var surfaceView: SurfaceView? = null
//    private var drawThread: DrawThread? = null
//
//    override fun onCreate() {
//        super.onCreate()
//        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
////        setupSurfaceView(animationType)
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        val animationType = intent?.getStringExtra("animation")
//
//        if (animationType.isNullOrEmpty()) {
//            Log.d("AnimationOverlayService", "No animationType provided, stopping service.")
//            stopSelf()
//            return START_NOT_STICKY
//        }
//
//        setupSurfaceView(animationType)
//        return START_NOT_STICKY
//
//    }
//
//    private fun setupSurfaceView(animationType: String) {
//        surfaceView = SurfaceView(this)
//
//        val params = WindowManager.LayoutParams(
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.MATCH_PARENT,
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//            } else {
//                WindowManager.LayoutParams.TYPE_PHONE
//            },
//            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
//                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
//                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
//            PixelFormat.TRANSLUCENT
//        )
//
//        windowManager?.addView(surfaceView, params)
//
//        surfaceView?.holder?.addCallback(object: SurfaceHolder.Callback {
//            override fun surfaceCreated(holder: SurfaceHolder) {
//                drawThread = DrawThread(holder, animationType)
//                drawThread?.start()
//            }
//
//            override fun surfaceChanged(
//                holder: SurfaceHolder,
//                format: Int,
//                width: Int,
//                height: Int
//            ) {}
//
//            override fun surfaceDestroyed(holder: SurfaceHolder) {
//                drawThread?.stopDrawing()
//            }
//        })
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        drawThread?.stopDrawing()
//        if (surfaceView != null) {
//            windowManager?.removeView(surfaceView)
//            surfaceView = null
//        }
//    }
//
//    override fun onBind(intent: Intent?): IBinder? = null
//}
//package com.ttak.android.service
//
//import android.app.Service
//import android.content.Intent
//import android.graphics.PixelFormat
//import android.media.MediaPlayer
//import android.net.Uri
//import android.os.IBinder
//import android.util.Log
//import android.view.SurfaceHolder
//import android.view.SurfaceView
//import android.view.WindowManager
//import com.ttak.android.R
//
//class AnimationOverlayService : Service() {
//    private var windowManager: WindowManager? = null
//    private var surfaceView: SurfaceView? = null
//    private var mediaPlayer: MediaPlayer? = null
//
//    override fun onCreate() {
//        super.onCreate()
//        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        val videoResId = R.raw.sample_video // 앱 내 포함된 동영상 리소스 ID
//        setupSurfaceView(videoResId)
//        return START_NOT_STICKY
//    }
//
//    private fun setupSurfaceView(videoResId: Int) {
//        surfaceView = SurfaceView(this)
//
//        val params = WindowManager.LayoutParams(
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.MATCH_PARENT,
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//            } else {
//                WindowManager.LayoutParams.TYPE_PHONE
//            },
//            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
//                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
//            PixelFormat.TRANSLUCENT
//        )
//
//        windowManager?.addView(surfaceView, params)
//
//        surfaceView?.holder?.addCallback(object : SurfaceHolder.Callback {
//            override fun surfaceCreated(holder: SurfaceHolder) {
//                mediaPlayer = MediaPlayer.create(this@AnimationOverlayService, videoResId).apply {
//                    setDisplay(holder)
//                    isLooping = false
//                    start()
//                }
//            }
//
//            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
//
//            override fun surfaceDestroyed(holder: SurfaceHolder) {
//                mediaPlayer?.release()
//                mediaPlayer = null
//            }
//        })
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mediaPlayer?.release()
//        mediaPlayer = null
//        if (surfaceView != null) {
//            windowManager?.removeView(surfaceView)
//            surfaceView = null
//        }
//    }
//
//    override fun onBind(intent: Intent?): IBinder? = null
//}
package com.ttak.android.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import com.ttak.android.R

class AnimationOverlayService : Service() {
    private var windowManager: WindowManager? = null
    private var surfaceView: SurfaceView? = null
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.sample_video}") // 앱에 포함된 동영상 파일
        setupSurfaceView(videoUri)
        return START_NOT_STICKY
    }

    private fun setupSurfaceView(videoUri: Uri) {
        surfaceView = SurfaceView(this)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        windowManager?.addView(surfaceView, params)

        surfaceView?.holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                mediaPlayer = MediaPlayer.create(this@AnimationOverlayService, videoUri).apply {
                    setDisplay(holder)
                    isLooping = false // 반복 재생 비활성화
                    setOnCompletionListener {
                        Log.d("AnimationOverlayService", "Media playback completed. Stopping service.")
                        stopServiceAndRemoveOverlay()
                    }
                    setOnErrorListener { _, what, extra ->
                        Log.e("AnimationOverlayService", "Media playback error: $what, $extra")
                        stopServiceAndRemoveOverlay()
                        true
                    }
                    start()
                }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                stopServiceAndRemoveOverlay()
            }
        })
    }

    private fun stopServiceAndRemoveOverlay() {
        mediaPlayer?.release()
        mediaPlayer = null
        if (surfaceView != null) {
            windowManager?.removeView(surfaceView)
            surfaceView = null
        }
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopServiceAndRemoveOverlay()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
