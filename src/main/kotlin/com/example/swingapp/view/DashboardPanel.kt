package com.example.swingapp.view

import com.example.swingapp.viewmodel.DashboardViewModel
import com.example.swingapp.viewmodel.bindToSwing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Font
import java.awt.GridLayout
import java.text.NumberFormat
import java.util.Locale
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

class DashboardPanel(
    private val viewModel: DashboardViewModel
) : JPanel(BorderLayout()) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val lblCustomersVal = JLabel("0")
    private val lblOrdersVal = JLabel("0")
    private val lblRevenueVal = JLabel("")

    private val columnNames = arrayOf("주문번호 (ID)", "고객명 (Customer)", "상태 (Status)", "결제금액 (Amount)", "주문일자 (Date)")
    private val ordersTableModel = object : DefaultTableModel(columnNames, 0) {
        override fun isCellEditable(row: Int, column: Int): Boolean = false
    }
    private val ordersTable = JTable(ordersTableModel)

    init {
        isOpaque = false
        border = BorderFactory.createEmptyBorder(20, 24, 20, 24)

        initComponents()
        setupBindings()
    }

    private fun initComponents() {
        val contentPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
        }

        // Title
        val titleLabel = JLabel("대시보드 (Dashboard)").apply {
            font = font.deriveFont(Font.BOLD, 22f)
            alignmentX = Component.LEFT_ALIGNMENT
        }
        contentPanel.add(titleLabel)
        contentPanel.add(Box.createVerticalStrut(16))

        // Metric Cards Panel
        val cardsPanel = JPanel(GridLayout(1, 3, 16, 0)).apply {
            isOpaque = false
            alignmentX = Component.LEFT_ALIGNMENT
            maximumSize = java.awt.Dimension(Int.MAX_VALUE, 90)
        }

        cardsPanel.add(createMetricCard("총 고객수 (Users)", lblCustomersVal, "👥", Color(14, 165, 233)))
        cardsPanel.add(createMetricCard("총 주문수 (Orders)", lblOrdersVal, "📦", Color(16, 185, 129)))
        cardsPanel.add(createMetricCard("총 매출액 (Revenue)", lblRevenueVal, "💳", Color(245, 158, 11)))

        contentPanel.add(cardsPanel)
        contentPanel.add(Box.createVerticalStrut(28))

        // Section Title: Recent Orders
        val ordersTitle = JLabel("최근 주문 현황 (Recent Orders)").apply {
            font = font.deriveFont(Font.BOLD, 16f)
            alignmentX = Component.LEFT_ALIGNMENT
        }
        contentPanel.add(ordersTitle)
        contentPanel.add(Box.createVerticalStrut(10))

        // Orders Table setup
        ordersTable.apply {
            rowHeight = 32
            showHorizontalLines = true
            showVerticalLines = false
            autoCreateRowSorter = true
            tableHeader.reorderingAllowed = false
            tableHeader.font = tableHeader.font.deriveFont(Font.BOLD)
        }

        val centerRenderer = DefaultTableCellRenderer().apply {
            horizontalAlignment = JLabel.CENTER
        }
        ordersTable.columnModel.getColumn(0).cellRenderer = centerRenderer
        ordersTable.columnModel.getColumn(2).cellRenderer = centerRenderer
        ordersTable.columnModel.getColumn(4).cellRenderer = centerRenderer

        val scrollPane = JScrollPane(ordersTable).apply {
            alignmentX = Component.LEFT_ALIGNMENT
            border = BorderFactory.createLineBorder(Color(226, 232, 240))
        }
        contentPanel.add(scrollPane)

        add(contentPanel, BorderLayout.CENTER)
    }

    private fun createMetricCard(title: String, valueLabel: JLabel, icon: String, accentColor: Color): JPanel {
        return JPanel(BorderLayout(10, 10)).apply {
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
            )
            putClientProperty("FlatLaf.styleClass", "card")

            val titleLbl = JLabel(title).apply {
                font = font.deriveFont(Font.PLAIN, 12f)
                foreground = Color(100, 116, 139)
            }

            valueLabel.font = valueLabel.font.deriveFont(Font.BOLD, 22f)

            val iconLbl = JLabel(icon).apply {
                font = font.deriveFont(24f)
            }

            val textPanel = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                isOpaque = false
                add(titleLbl)
                add(Box.createVerticalStrut(4))
                add(valueLabel)
            }

            add(textPanel, BorderLayout.CENTER)
            add(iconLbl, BorderLayout.EAST)
        }
    }

    private fun setupBindings() {
        val curFormat = NumberFormat.getCurrencyInstance(Locale.US)

        viewModel.state.bindToSwing(scope) { state ->
            lblCustomersVal.text = String.format("%,d", state.customerCount)
            lblOrdersVal.text = String.format("%,d", state.orderCount)
            lblRevenueVal.text = curFormat.format(state.totalRevenue)

            ordersTableModel.rowCount = 0
            for (order in state.recentOrders) {
                ordersTableModel.addRow(
                    arrayOf(
                        order.id,
                        order.customerName,
                        order.status,
                        curFormat.format(order.amount),
                        order.date.toString()
                    )
                )
            }
        }
    }

    fun refreshData() {
        viewModel.refresh()
    }
}
