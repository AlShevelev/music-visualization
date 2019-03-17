package com.shevelev.music_visualization.library

import android.content.Context
import android.graphics.Canvas
import android.media.audiofx.Visualizer
import android.util.AttributeSet
import android.view.View

/**
 *
 */
abstract class VisualizerBase
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    protected var audioBytes: ByteArray? = null
    private var visualizer: Visualizer? = null

    /**
     * Sets the audio session id for the currently playing audio
     */
    fun setAudioSessionId(audioSessionId: Int) {
        release()

        visualizer = Visualizer(audioSessionId)
            .apply {
                this.captureSize = Visualizer.getCaptureSizeRange()[1]    // Capture size is maximum

                this.setDataCaptureListener(object: Visualizer.OnDataCaptureListener {
                    override fun onFftDataCapture(visualizer: Visualizer?, fft: ByteArray?, samplingRate: Int) {
                    }

                    override fun onWaveFormDataCapture(visualizer: Visualizer?, waveform: ByteArray?, samplingRate: Int) {
                        waveform?.also {
                            audioBytes = it.copyOf()
                            invalidate()
                        }
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false)

                this.enabled = true
            }
    }

    /**
     * Releases the visualizer
     */
    fun release() {
        visualizer?.also { it.release() }
    }

    /**
     *
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        audioBytes
            ?.let {
                if(it.isNotEmpty()) {
                    draw(it, canvas)
                }
            }
    }

    /**
     * Draw algorithm must be implemented here
     */
    protected abstract fun draw(rawData: ByteArray, canvas: Canvas)
}