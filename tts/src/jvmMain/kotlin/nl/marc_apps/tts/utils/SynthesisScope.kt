package nl.marc_apps.tts.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

fun SynthesisScope(supervisorJob: Job): CoroutineScope {
    val dispatcher = Dispatchers.Default.limitedParallelism(1)
    return CoroutineScope(supervisorJob + dispatcher)
}
