package com.example.ui.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack

object SoundPlayer {
    var soundVolume: Float = 0.8f
    var musicVolume: Float = 0.8f
    var isMuted: Boolean = false

    @Volatile
    private var isMusicRunning = false
    private var musicThread: Thread? = null

    fun playTone(frequency: Double, durationMs: Int, type: String = "decay", customVolume: Double = 0.4, isMusic: Boolean = false) {
        if (isMuted) return
        val currentVolume = if (isMusic) musicVolume else soundVolume
        if (currentVolume <= 0f) return
        Thread {
            try {
                val sampleRate = 8000
                val numSamples = durationMs * sampleRate / 1000
                val sample = DoubleArray(numSamples)
                val generatedSnd = ByteArray(2 * numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    
                    // Apply slight bubble pitch sweep for "pluck" style
                    val currentFreq = if (type == "pluck") {
                        frequency + (120.0 * (1.0 - i.toDouble() / numSamples))
                    } else {
                        frequency
                    }

                    val rawSine = Math.sin(2 * Math.PI * t * currentFreq)
                    
                    // Smooth volume envelopes (prevents harsh clicks or pops at the beginning/end)
                    val envelope = when (type) {
                        "pluck" -> Math.pow(1.0 - (i.toDouble() / numSamples), 2.5) // Exponential snappy pluck decay
                        "decay" -> 1.0 - (i.toDouble() / numSamples) // Warm linear decay
                        "soft" -> Math.sin(Math.PI * i.toDouble() / numSamples) // Bell curve fade in/out
                        else -> 1.0
                    }

                    sample[i] = rawSine * envelope * customVolume * currentVolume
                }

                var idx = 0
                for (dVal in sample) {
                    val valShort = (dVal * 32767).toInt().toShort()
                    generatedSnd[idx++] = (valShort.toInt() and 0x00ff).toByte()
                    generatedSnd[idx++] = ((valShort.toInt() and 0xff00) ushr 8).toByte()
                }

                val audioTrack = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    generatedSnd.size,
                    AudioTrack.MODE_STATIC
                )
                audioTrack.write(generatedSnd, 0, generatedSnd.size)
                audioTrack.play()
                Thread.sleep(durationMs.toLong() + 15)
                audioTrack.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun startMusicLoop() {
        if (isMusicRunning) return
        isMusicRunning = true
        musicThread = Thread {
            // A gentle, pleasant pentatonic farming theme arpeggio (C4, E4, G4, A4, G4, E4)
            val notes = doubleArrayOf(261.63, 329.63, 392.00, 440.00, 392.00, 329.63)
            var index = 0
            while (isMusicRunning) {
                if (musicVolume > 0f && !isMuted) {
                    val freq = notes[index]
                    playTone(freq, 700, "soft", 0.08, isMusic = true)
                    index = (index + 1) % notes.size
                    try {
                        Thread.sleep(2600)
                    } catch (e: InterruptedException) {
                        break
                    }
                } else {
                    try {
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                        break
                    }
                }
            }
        }.apply {
            priority = Thread.MIN_PRIORITY
            start()
        }
    }

    fun stopMusicLoop() {
        isMusicRunning = false
        musicThread?.interrupt()
        musicThread = null
    }

    // Soothing, low-frequency wooden organic click for buttons
    fun playMenuTap() {
        playTone(320.0, 45, "pluck", 0.35)
    }

    // High-quality gentle water-bubbly splash for vegetable selection
    fun playVeggieTap() {
        playTone(550.0, 75, "pluck", 0.45)
    }

    // Harmonious major-third double pluck match chord
    fun playMatch() {
        playTone(523.25, 120, "decay", 0.4) // C5
        Thread.sleep(60)
        playTone(659.25, 160, "decay", 0.4) // E5
    }

    // Dynamic, energetic chord for Combo Triggers
    fun playCombo(multiplier: Float) {
        // High energetic chord: pitch rises with multiplier!
        val basePitch = 587.33 + (multiplier * 40.0) // D5 + extra pitch
        playTone(basePitch, 150, "pluck", 0.4)
        Thread.sleep(50)
        playTone(basePitch * 1.25, 150, "decay", 0.45) // G5 or higher
    }

    // Very soft, non-intrusive muted thud
    fun playError() {
        playTone(130.0, 140, "pluck", 0.45)
    }

    // Delightful soft pentatonic arpeggio scale
    fun playVictory() {
        val notes = doubleArrayOf(523.25, 587.33, 659.25, 783.99, 880.00) // C5 D5 E5 G5 A5
        Thread {
            try {
                for (note in notes) {
                    playTone(note, 130, "decay", 0.35)
                    Thread.sleep(95)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    // Beautiful ascending progression for Level Advances
    fun playLevelUp() {
        val notes = doubleArrayOf(261.63, 329.63, 392.00, 523.25, 659.25, 783.99) // C4 E4 G4 C5 E5 G5
        Thread {
            try {
                for (note in notes) {
                    playTone(note, 150, "decay", 0.35)
                    Thread.sleep(110)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}
