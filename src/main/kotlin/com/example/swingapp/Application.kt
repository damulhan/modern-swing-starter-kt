package com.example.swingapp

import com.example.swingapp.view.MainFrame
import com.formdev.flatlaf.FlatLightLaf
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main() {
    System.setProperty("sun.java2d.uiScale.enabled", "true")

    try {
        FlatLightLaf.setup()
        UIManager.put("Button.arc", 6)
        UIManager.put("Component.arc", 6)
        UIManager.put("ProgressBar.arc", 6)
        UIManager.put("TextComponent.arc", 6)
    } catch (e: Exception) {
        System.err.println("Failed to initialize FlatLaf Look and Feel: " + e.message)
    }

    SwingUtilities.invokeLater {
        val frame = MainFrame()
        frame.isVisible = true
    }
}