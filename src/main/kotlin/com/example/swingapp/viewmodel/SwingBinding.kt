package com.example.swingapp.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing

/**
 * Extension function to bind a Kotlin Flow / StateFlow to Swing UI components.
 * Guarantees collection on [Dispatchers.Swing] (EDT).
 */
fun <T> Flow<T>.bindToSwing(
    scope: CoroutineScope,
    onValue: (T) -> Unit
): Job {
    return scope.launch(Dispatchers.Swing) {
        collect { value ->
            onValue(value)
        }
    }
}
