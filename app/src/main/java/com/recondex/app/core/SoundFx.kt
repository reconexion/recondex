package com.recondex.app.core

import android.media.AudioManager
import android.media.ToneGenerator

/**
 * Feedback sonoro simple con ToneGenerator: no requiere archivos de audio
 * (no hay forma de descargar assets de sonido sin internet en este sandbox),
 * no requiere permisos extra y funciona 100% offline.
 */
object SoundFx {
    private val tg: ToneGenerator by lazy {
        ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME)
    }

    /** Click corto para taps de tarjetas/botones. */
    fun tap() = runCatching { tg.startTone(ToneGenerator.TONE_PROP_BEEP, 60) }

    /** Doble beep ascendente para "herramienta completada / +XP". */
    fun success() = runCatching { tg.startTone(ToneGenerator.TONE_PROP_ACK, 180) }

    /** Beep grave para errores / resultados fallidos. */
    fun error() = runCatching { tg.startTone(ToneGenerator.TONE_PROP_NACK, 150) }

    /** Tono sutil para "escaneo en progreso" (se puede llamar en loop corto). */
    fun ping() = runCatching { tg.startTone(ToneGenerator.TONE_SUP_PIP, 40) }
}
