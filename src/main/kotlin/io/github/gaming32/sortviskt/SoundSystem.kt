package io.github.gaming32.sortviskt

import javax.sound.midi.MidiChannel
import javax.sound.midi.MidiSystem
import kotlin.concurrent.withLock
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class SoundSystem(private val window: MainWindow) : AutoCloseable {
    companion object {
        private const val PITCH_MIN = 25.0
        private const val PITCH_MAX = 105.0
        private const val SOUND_MUL = 1.0
        private const val REVERB = 91
    }

    private val synth = MidiSystem.getSynthesizer().also { synth ->
        synth.open()
        synth.loadInstrument(synth.availableInstruments[0])
    }
    var instrument = 0
    private var loadedInstrument = 0
    private val channels = Array(15, ::refreshChannel)

    fun tick() {
        val list = window.list
        if (instrument != loadedInstrument) {
            loadedInstrument = instrument
            synth.loadInstrument(synth.availableInstruments[instrument])
            channels.indices.forEach(::refreshChannel)
        }
        channels.forEach(MidiChannel::allNotesOff)
        if (!window.playSound) return
        var channel = 0
        list.marksLock.withLock {
            for (mark in list.marks) {
                if (channel == 9) {
                    channel++
                }
                if (channel >= 15) break
                val pitch =
                    list.internal[min(max(mark, 0), list.size - 1)] /
                        list.size.toDouble() *
                        (PITCH_MAX - PITCH_MIN) +
                        PITCH_MIN
                val pitchMajor = pitch.toInt()
                val pitchMinor = ((pitch - pitchMajor) * 8192).toInt()
                var vel = ((PITCH_MAX - pitchMajor).pow(2) * 15.0.pow(-0.25) * 64 * SOUND_MUL).toInt() / 2
                if (SOUND_MUL >= 1 && vel < 256) {
                    vel *= vel
                }
                channels[channel].noteOn(pitchMajor, vel)
                channels[channel].pitchBend = pitchMinor
                channels[channel].controlChange(REVERB, 10)
                channel++
            }
        }
    }

    override fun close() {
        synth.close()
    }

    private fun refreshChannel(index: Int): MidiChannel {
        val channel = synth.channels[index]
        channel.programChange(synth.availableInstruments[instrument].patch.program)
        channel.channelPressure = 1
        return channel
    }
}
