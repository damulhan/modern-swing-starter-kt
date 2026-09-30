package com.example.swingapp.view

import com.example.swingapp.service.CustomerService
import com.example.swingapp.viewmodel.CustomerViewModel
import com.example.swingapp.viewmodel.DashboardViewModel
import com.example.swingapp.viewmodel.bindToSwing
import com.formdev.flatlaf.FlatDarkLaf
import com.formdev.flatlaf.FlatLaf
import com.formdev.flatlaf.FlatLightLaf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.awt.*
import javax.swing.*

class MainFrame : JFrame() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val customerService = CustomerService()
    private val customerViewModel = CustomerViewModel(customerService)
    private val dashboardViewModel = DashboardViewModel(customerService)

    private val cardLayout = CardLayout()
    private val contentCards = JPanel(cardLayout)

    private lateinit var dashboardPanel: DashboardPanel
    private lateinit var customerPanel: CustomerPanel
    private lateinit var settingsPanel: SettingsPanel

    private val lblStatus = JLabel("Ready")
    private lateinit var btnNavDashboard: JButton
    private lateinit var btnNavCustomers: JButton
    private lateinit var btnNavSettings: JButton

    init {
        initUI()
        setupBindings()
    }

    private fun initUI() {
        title = "Swing Modern Desktop Sample (Kotlin & Coroutines)"
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(1100, 720)
        minimumSize = Dimension(860, 560)
        setLocationRelativeTo(null)

        val rootPanel = JPanel(BorderLayout())

        // 1. Header Bar
        rootPanel.add(createHeaderBar(), BorderLayout.NORTH)

        // 2. Center Work Area
        val centerArea = JPanel(BorderLayout())
        centerArea.add(createSidebar(), BorderLayout.WEST)

        dashboardPanel = DashboardPanel(dashboardViewModel)
        customerPanel = CustomerPanel(customerViewModel) {
            dashboardPanel.refreshData()
        }
        settingsPanel = SettingsPanel(this)

        contentCards.add(dashboardPanel, "Dashboard")
        contentCards.add(customerPanel, "Customers")
        contentCards.add(settingsPanel, "Settings")

        centerArea.add(contentCards, BorderLayout.CENTER)
        rootPanel.add(centerArea, BorderLayout.CENTER)

        // 3. Status Bar
        rootPanel.add(createStatusBar(), BorderLayout.SOUTH)

        contentPane = rootPanel
        switchView("Dashboard")
    }

    private fun setupBindings() {
        customerViewModel.statusMessage.bindToSwing(scope) { msg ->
            lblStatus.text = "상태: " + msg
        }
    }

    private fun createHeaderBar(): JPanel {
        val header = JPanel(BorderLayout(16, 0)).apply {
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
            )
        }

        val title = JLabel("Swing Modern Desktop Sample (Kotlin)").apply {
            font = font.deriveFont(Font.BOLD, 15f)
        }
        header.add(title, BorderLayout.WEST)

        val rightTools = JPanel(FlowLayout(FlowLayout.RIGHT, 6, 0)).apply {
            isOpaque = false
        }

        val btnTheme = JButton(if (FlatLaf.isLafDark()) "☀️" else "🌙").apply {
            toolTipText = "테마 간편 전환 (Light/Dark)"
            isFocusable = false
            addActionListener {
                val toDark = !FlatLaf.isLafDark()
                try {
                    if (toDark) {
                        UIManager.setLookAndFeel(FlatDarkLaf())
                        text = "☀️"
                    } else {
                        UIManager.setLookAndFeel(FlatLightLaf())
                        text = "🌙"
                    }
                    FlatLaf.updateUI()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }

        val btnGear = JButton("⚙").apply {
            toolTipText = "설정으로 이동"
            isFocusable = false
            addActionListener { switchView("Settings") }
        }

        rightTools.add(btnTheme)
        rightTools.add(btnGear)
        header.add(rightTools, BorderLayout.EAST)

        return header
    }

    private fun createSidebar(): JPanel {
        val sidebar = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            preferredSize = Dimension(200, 0)
            border = BorderFactory.createMatteBorder(0, 0, 0, 1, Color(226, 232, 240))
        }

        sidebar.add(Box.createVerticalStrut(14))

        btnNavDashboard = createNavButton("📊  Dashboard", "Dashboard")
        btnNavCustomers = createNavButton("👥  Customers", "Customers")
        btnNavSettings  = createNavButton("⚙️ Settings", "Settings")

        sidebar.add(btnNavDashboard)
        sidebar.add(Box.createVerticalStrut(4))
        sidebar.add(btnNavCustomers)
        sidebar.add(Box.createVerticalStrut(4))
        sidebar.add(btnNavSettings)
        sidebar.add(Box.createVerticalGlue())

        return sidebar
    }

    private fun createNavButton(text: String, targetCard: String): JButton {
        return JButton(text).apply {
            maximumSize = Dimension(180, 38)
            preferredSize = Dimension(180, 38)
            alignmentX = Component.CENTER_ALIGNMENT
            horizontalAlignment = SwingConstants.LEFT
            isFocusPainted = false
            putClientProperty("JButton.buttonType", "roundRect")
            addActionListener { switchView(targetCard) }
        }
    }

    private fun switchView(cardName: String) {
        cardLayout.show(contentCards, cardName)

        updateNavState(btnNavDashboard, cardName == "Dashboard")
        updateNavState(btnNavCustomers, cardName == "Customers")
        updateNavState(btnNavSettings, cardName == "Settings")

        if (cardName == "Dashboard") {
            dashboardPanel.refreshData()
        } else if (cardName == "Customers") {
            customerPanel.loadTableData()
        }

        lblStatus.text = "화면: " + cardName + " | Ready"
    }

    private fun updateNavState(btn: JButton, active: Boolean) {
        if (active) {
            btn.putClientProperty("FlatLaf.styleClass", "accent")
            btn.font = btn.font.deriveFont(Font.BOLD)
        } else {
            btn.putClientProperty("FlatLaf.styleClass", null)
            btn.font = btn.font.deriveFont(Font.PLAIN)
        }
        btn.repaint()
    }

    private fun createStatusBar(): JPanel {
        val bar = JPanel(BorderLayout(10, 0)).apply {
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)
            )
        }

        lblStatus.font = lblStatus.font.deriveFont(11.5f)
        lblStatus.foreground = Color(100, 116, 139)
        bar.add(lblStatus, BorderLayout.WEST)

        val jVer = System.getProperty("java.version")
        val lblJava = JLabel("Java " + jVer + " | Kotlin + Coroutines + FlatLaf").apply {
            font = font.deriveFont(11.5f)
            foreground = Color(100, 116, 139)
        }
        bar.add(lblJava, BorderLayout.EAST)

        return bar
    }
}