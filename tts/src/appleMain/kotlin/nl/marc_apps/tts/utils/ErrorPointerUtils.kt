package nl.marc_apps.tts.utils

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.value
import platform.Foundation.NSError

object ErrorPointerUtils {
    @OptIn(ExperimentalForeignApi::class)
    fun <T> createErrorPointer(block: (ObjCObjectVar<NSError?>) -> T): T = memScoped {
        block(alloc<ObjCObjectVar<NSError?>>())
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun ObjCObjectVar<NSError?>.throwOnError() {
    if (value != null) {
        throw RuntimeException(value?.debugDescription)
    }
}
