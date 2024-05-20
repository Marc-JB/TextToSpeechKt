package nl.marc_apps.tts.utils

import android.os.Bundle

fun Bundle.toMap(): Map<String, Any?> = keySet().associateWith { get(it) }
