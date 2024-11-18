package com.ttak.android.utils

//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
import android.graphics.*
import android.view.SurfaceHolder

class DrawThread(private val surfaceHolder: SurfaceHolder, private val animationType: String) : Thread() {
    private var running = true
    private val paint = Paint()
    private var colorIndex = 0
    private val colors = arrayOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA)

    init {
        paint.style = Paint.Style.FILL
    }

    override fun run() {
        while (running) {
            val canvas: Canvas? = surfaceHolder.lockCanvas()
            canvas?.let {
                try {
                    when (animationType) {
                        "WATER_BOOM" -> drawWaterBoomEffect(it)
                        else -> it.drawColor(colors[colorIndex])
                    }
                    colorIndex = (colorIndex + 1) % colors.size
                } finally {
                    surfaceHolder.unlockCanvasAndPost(it)
                }
            }
            sleep(500) // 0.5초마다 색 변경
        }
    }

    private fun drawWaterBoomEffect(canvas: Canvas) {
        canvas.drawColor(Color.CYAN)
        paint.color = Color.WHITE
        paint.alpha = (Math.random() * 255).toInt()
        canvas.drawCircle(
            (Math.random() * canvas.width).toFloat(),
            (Math.random() * canvas.height).toFloat(),
            (Math.random() * 100).toFloat(),
            paint
        )
    }

    fun stopDrawing() {
        running = false
    }
}