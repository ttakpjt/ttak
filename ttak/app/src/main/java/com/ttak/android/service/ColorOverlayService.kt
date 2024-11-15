package com.ttak.android.service

import android.animation.*
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.RippleDrawable
import android.os.*
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.view.animation.AccelerateDecelerateInterpolator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.VibrationEffect
import android.os.Vibrator

class ColorOverlayService : Service() {
    companion object {
        private const val TAG = "ColorOverlayService"
        private const val WAKE_LOCK_TIMEOUT = 10_000L
    }

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var fadeAnimator: ValueAnimator? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var vibrator: Vibrator? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "MyApp:ColorOverlayWakeLock"
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        wakeLock?.acquire(WAKE_LOCK_TIMEOUT)

        val color = intent?.getIntExtra("color", Color.BLUE) ?: Color.BLUE
        val duration = intent?.getLongExtra("duration", 500L) ?: 500L

        // 진동 패턴 실행
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(
                longArrayOf(0, 100, 100, 100), // 진동 패턴
                intArrayOf(0, 255, 0, 255),    // 진동 세기
                -1
            ))
        } else {
            vibrator?.vibrate(longArrayOf(0, 100, 100, 100), -1)
        }

        showColorOverlay(color, duration)
        return START_NOT_STICKY
    }

    private fun showColorOverlay(color: Int, duration: Long) {
        overlayView = View(this).apply {
            setBackgroundColor(color)
            alpha = 0f

            // 리플 효과 추가
            background = RippleDrawable(
                ColorStateList.valueOf(Color.WHITE),
                ColorDrawable(color),
                null
            )
        }

        val params = LayoutParams().apply {
            width = LayoutParams.MATCH_PARENT
            height = LayoutParams.MATCH_PARENT
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                LayoutParams.TYPE_PHONE
            }
            flags = LayoutParams.FLAG_NOT_FOCUSABLE or
                    LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                    LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    LayoutParams.FLAG_NOT_TOUCH_MODAL
            format = PixelFormat.TRANSLUCENT
            gravity = android.view.Gravity.CENTER
        }

        try {
            windowManager?.addView(overlayView, params)

            // 스케일 및 알파 애니메이션 결합
            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.8f, 1.2f, 1f)
            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.8f, 1.2f, 1f)
            val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f, 0f)

            fadeAnimator = ObjectAnimator.ofPropertyValuesHolder(overlayView, scaleX, scaleY, alpha).apply {
                this.duration = duration
                interpolator = AccelerateDecelerateInterpolator()
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        removeOverlay()
                        stopSelf()
                    }
                })
                start()
            }

            // 리플 효과 트리거
            overlayView?.postDelayed({
                (overlayView?.background as? RippleDrawable)?.setHotspot(
                    overlayView?.width?.div(2f) ?: 0f,
                    overlayView?.height?.div(2f) ?: 0f
                )
                overlayView?.background?.state = intArrayOf(android.R.attr.state_pressed)
            }, duration / 4)

        } catch (e: Exception) {
            Log.e(TAG, "Error showing overlay", e)
            stopSelf()
        }
    }

    private fun removeOverlay() {
        try {
            fadeAnimator?.cancel()
            windowManager?.removeView(overlayView)
            vibrator?.cancel()
        } catch (e: Exception) {
            Log.e(TAG, "Error removing overlay", e)
        } finally {
            wakeLock?.release()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeOverlay()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}