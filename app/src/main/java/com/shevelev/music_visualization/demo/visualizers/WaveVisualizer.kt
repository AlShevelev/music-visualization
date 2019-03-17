package com.shevelev.music_visualization.demo.visualizers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import com.shevelev.music_visualization.library.VisualizerBase

class WaveVisualizer
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VisualizerBase(context, attrs, defStyleAttr) {

    private var paint: Paint
    private val waveformPath: Path = Path()

    init {
        paint = Paint()
            .apply {
                this.color = Color.YELLOW
                this.strokeWidth = 3f
                this.style = Paint.Style.STROKE
                this.isAntiAlias = true
            }
    }

    /**
     *
     */
    override fun draw(rawData: ByteArray, canvas: Canvas) {
        waveformPath.reset()

        val width = canvas.width.toFloat()
        val height = canvas.height

        val xIncrement = width / rawData.size
        val yIncrement = height / 255f

        val halfHeight = (height * 0.5f)
        waveformPath.moveTo(0f, halfHeight)

        for (i in 1 until rawData.size) {
            val yPosition = if (rawData[i] > 0) {
                height - yIncrement * rawData[i]
            }
            else {
                -(yIncrement * rawData[i])
            }

            waveformPath.lineTo(xIncrement * i, yPosition)
        }
        waveformPath.lineTo(width, halfHeight)

        canvas.drawPath(waveformPath, paint)
    }
}