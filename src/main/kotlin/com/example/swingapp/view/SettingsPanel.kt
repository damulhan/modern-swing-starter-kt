package com.example.swingapp.view

import com.formdev.flatlaf.FlatDarkLaf
import com.formdev.flatlaf.FlatLaf
import com.formdev.flatlaf.FlatLightLaf
import java.awt.*
import javax.swing.*

class SettingsPanel(
    private val mainFrame: JFrame
) : JPanel(BorderLayout()) {

    private val rbLight = JRadioButton("Light Theme (밝은 테마)", true)
    private val rbDark = JRadioButton("Dark Theme (어두운 테마)", false)

    init {
        isOpaque = false
        border = BorderFactory.createEmptyBorder(20, 24, 20, 24)

        initComponents()
    }

    private fun initComponents() {
        val contentPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
        }

        val titleLabel = JLabel("환경 설정 (Settings)").apply {
            font = font.deriveFont(Font.BOLD, 22f)
            alignmentX = Component.LEFT_ALIGNMENT
        }
        contentPanel.add(titleLabel)
        contentPanel.add(Box.createVerticalStrut(20))

        // Settings Card
        val card = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            )
            alignmentX = Component.LEFT_ALIGNMENT
        }

        val themeHeader = JLabel("테마 설정 (FlatLaf Theme)").apply {
            font = font.deriveFont(Font.BOLD, 15f)
        }
        card.add(themeHeader)
        card.add(Box.createVerticalStrut(12))

        val form = JPanel(GridLayout(2, 1, 0, 8)).apply {
            isOpaque = false
            alignmentX = Component.LEFT_ALIGNMENT
            maximumSize = Dimension(300, 60)
        }

        val group = ButtonGroup().apply {
            add(rbLight)
            add(rbDark)
        }

        val isDark = FlatLaf.isLafDark()
        rbDark.isSelected = isDark
        rbLight.isSelected = !isDark

        rbLight.addActionListener { changeTheme(false) }
        rbDark.addActionListener { changeTheme(true) }

        form.add(rbLight)
        form.add(rbDark)

        card.add(form)
        card.add(Box.createVerticalStrut(20))

        val infoLabel = JLabel("<html>FlatLaf의 런타임 Look & Feel 전환 기능을 사용하여<br>애플리케이션 재시작 없이 즉시 테마가 실시간 반영됩니다.</html>").apply {
            foreground = Color(100, 116, 139)
            alignmentX = Component.LEFT_ALIGNMENT
        }
        card.add(infoLabel)

        contentPanel.add(card)
        add(contentPanel, BorderLayout.CENTER)
    }

    private fun changeTheme(dark: Boolean) {
        SwingUtilities.invokeLater {
            try {
                if (dark) {
                    UIManager.setLookAndFeel(FlatDarkLaf())
                } else {
                    UIManager.setLookAndFeel(FlatLightLaf())
                }
                FlatLaf.updateUI()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}
