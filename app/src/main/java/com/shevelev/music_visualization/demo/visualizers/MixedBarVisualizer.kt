package com.shevelev.music_visualization.demo.visualizers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.support.annotation.ColorInt
import android.support.v4.graphics.ColorUtils
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import com.shevelev.music_visualization.library.VisualizerBase
import java.util.*

class MixedBarVisualizer
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VisualizerBase(context, attrs, defStyleAttr) {

    companion object {
        private const val barWidth = 5f        // [dp]
        private const val gapWidth = 5f        // [dp]
    }

    private val paint: Paint

    // Bars sizes
    private val barWidthPix: Float
    private val gapWidthPix: Float

    private var barXBoards: List<Pair<Float, Float>>? = null

    private var priorData: ByteArray? = null

    // Colors blending
    private val colorRandomize = Random()
    private val colorBlendSpeed = 0.05f
    private var startColor = getRandomColor()
    private var nextColor = getRandomColor()
    private var colorBlendFactor = 0f

    /**
     *
     */
    init {
        paint = Paint()
            .apply {
                this.color = startColor
                this.style = Paint.Style.FILL
                this.isAntiAlias = true
            }

        resources.displayMetrics
            .also { displayMetrics ->
                barWidthPix = convertDpToPx(barWidth, displayMetrics)
                gapWidthPix = convertDpToPx(gapWidth, displayMetrics)
            }
    }

    /**
     *
     */
    override fun draw(rawData: ByteArray, canvas: Canvas) {
        if(barXBoards == null) {
            barXBoards = calculateBarXBoards(canvas.width)
        }

        // Current and previous data mixing
        val dataToProcess = if(priorData == null) {
            priorData = rawData
            rawData
        }
        else {
            val mixedData = ByteArray(rawData.size)
            for(i in 0 until mixedData.size) {
                mixedData[i] = ((priorData!![i] + rawData[i])/2).toByte()
            }
            priorData = mixedData
            mixedData
        }

        // Bars calculation
        val dataItemsInBar = dataToProcess.size / barXBoards!!.size

        var dataItemsCounter = 0
        var barsCounter = 0
        var dataItemsSum = 0

        paint.color = calculateCurrentColor()

        dataToProcess.forEach { dataItem ->
            dataItemsSum += dataItem
            dataItemsCounter++

            if(dataItemsCounter == dataItemsInBar && barsCounter < barXBoards!!.size) {
                val averageData = dataItemsSum / dataItemsInBar

                drawBar(averageData, barXBoards!![barsCounter], canvas)

                barsCounter++

                dataItemsCounter = 0
                dataItemsSum = 0
            }
        }
    }

    /**
     *
     */
    private fun convertDpToPx(dpValue: Float, displayMetrics: DisplayMetrics): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, displayMetrics)

    /**
     * Calculates offset from edges of a canvas to bars
     */
    private fun calculateEdgeOffset(canvasWidth: Int): Int =
        ((canvasWidth/2 - barWidthPix/2) % (barWidthPix + gapWidthPix)).toInt()

    /**
     * Calculates bar boards in a canvas
     */
    private fun calculateBarXBoards(canvasWidth: Int): List<Pair<Float, Float>> {

        val edgeOffset = calculateEdgeOffset(canvasWidth)

        val result = mutableListOf<Pair<Float, Float>>()

        var left = edgeOffset.toFloat()
        var right = left + barWidthPix

        do {
            result.add(Pair(left, right))

            left = right + gapWidthPix
            right = left + barWidthPix
        }
        while(right <= canvasWidth)

        return result
    }

    /**
     *
     */
    private fun drawBar(sourceData: Int, xBoards: Pair<Float, Float>, canvas: Canvas) {
        val normalizedData = sourceData + 128

        val barHeight = canvas.height * (normalizedData / 256f)

        val barTop = (canvas.height-barHeight)/2
        val barRect = RectF(xBoards.first, barTop, xBoards.second, canvas.height-barTop)

        canvas.drawRoundRect(barRect, 100f, 100f, paint)
    }

    /**
     *
     */
    @ColorInt
    private fun getRandomColor(): Int =
        Color.rgb(
            colorRandomize.nextInt(224)+32,
            colorRandomize.nextInt(224)+32,
            colorRandomize.nextInt(224)+32)

    /**
     *
     */
    @ColorInt
    private fun calculateCurrentColor(): Int {
        colorBlendFactor += colorBlendSpeed

        return if(colorBlendFactor <= 1f) {
            ColorUtils.blendARGB(startColor, nextColor, colorBlendFactor)
        }
        else {
            colorBlendFactor = 0f
            startColor = nextColor
            nextColor = getRandomColor()
            startColor
        }
    }
}