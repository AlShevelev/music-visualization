package com.shevelev.music_visualization.demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RawRes
import com.shevelev.music_visualization.R
import kotlinx.android.synthetic.main.activity_demo.*

class DemoActivity : AppCompatActivity() {

    private lateinit var audioPlayer: AudioPlayerUtil
    private lateinit var audioPlayer2: AudioPlayerUtil
    private lateinit var audioPlayer3: AudioPlayerUtil

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        audioPlayer = AudioPlayerUtil()
        audioPlayer2 = AudioPlayerUtil()
        audioPlayer3 = AudioPlayerUtil()
    }

    /**
     *
     */
    override fun onStart() {
        super.onStart()
        startPlayingAudio(R.raw.beethoven_12_variation)
    }

    /**
     *
     */
    override fun onStop() {
        super.onStop()
        stopPlayingAudio();
    }

    private fun startPlayingAudio(@RawRes resId: Int) {
        audioPlayer.play(this, resId) {
        }
        audioPlayer.getAudioSessionId()
            ?.also {
                barVisualizerPanel.setAudioSessionId(it)
            }

        audioPlayer2.play(this, resId) {
        }
        audioPlayer2.getAudioSessionId()
            ?.also {
                mixedBarVisualizerPanel.setAudioSessionId(it)
            }

        audioPlayer3.play(this, resId) {
        }
        audioPlayer3.getAudioSessionId()
            ?.also {
                waveVisualizerPanel.setAudioSessionId(it)
            }
    }

    private fun stopPlayingAudio() {
        audioPlayer.stop();
        barVisualizerPanel.release()

        audioPlayer2.stop();
        mixedBarVisualizerPanel.release()

        audioPlayer3.stop();
        waveVisualizerPanel.release()
    }
}