package com.example.swingapp.view

import com.example.swingapp.model.Customer
import com.example.swingapp.viewmodel.CustomerViewModel
import com.example.swingapp.viewmodel.bindToSwing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.awt.*
import javax.swing.*
import javax.swing.table.DefaultTableModel

class CustomerPanel(
    private val viewModel: CustomerViewModel,
    private val onDataChangedCallback: (() -> Unit)? = null
) : JPanel(BorderLayout(0, 16)) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val txtSearch = JTextField(18)
    private val tableModel: DefaultTableModel
    private val customerTable: JTable

    private val txtName = JTextField()
    private val txtEmail = JTextField()
    private val txtPhone = JTextField()
    private val chkActive = JCheckBox("활성 고객 (Active)")

    private var isUpdatingFromModel = false

    init {
        isOpaque = false
        border = BorderFactory.createEmptyBorder(20, 24, 20, 24)

        // Top Toolbar
        val topBar = JPanel(BorderLayout(16, 0)).apply {
            isOpaque = false
        }

        val titleLabel = JLabel("고객 관리 (Customer Management)").apply {
            font = font.deriveFont(Font.BOLD, 22f)
        }
        topBar.add(titleLabel, BorderLayout.WEST)

        val searchPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 8, 0)).apply {
            isOpaque = false
        }

        txtSearch.apply {
            putClientProperty("JTextField.placeholderText", "이름, 이메일, 전화번호 검색...")
            addActionListener { performSearch() }
        }

        val btnSearch = JButton("검색").apply {
            addActionListener { performSearch() }
        }

        val btnNew = JButton("+ 신규 등록").apply {
            putClientProperty("FlatLaf.styleClass", "accent")
            addActionListener {
                customerTable.clearSelection()
                viewModel.createNewCustomer()
                txtName.requestFocusInWindow()
            }
        }

        searchPanel.add(txtSearch)
        searchPanel.add(btnSearch)
        searchPanel.add(btnNew)
        topBar.add(searchPanel, BorderLayout.EAST)

        add(topBar, BorderLayout.NORTH)

        // Split pane: Left table & Right form
        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT).apply {
            resizeWeight = 0.58
            dividerSize = 8
            border = null
        }

        // Left Table
        val leftPanel = JPanel(BorderLayout()).apply { isOpaque = false }
        val cols = arrayOf("ID", "이름 (Name)", "이메일 (Email)", "전화번호 (Phone)", "상태 (Status)")
        tableModel = object : DefaultTableModel(cols, 0) {
            override fun isCellEditable(row: Int, column: Int): Boolean = false
        }

        customerTable = JTable(tableModel).apply {
            rowHeight = 32
            autoCreateRowSorter = true
            selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
            selectionModel.addListSelectionListener { e ->
                if (!e.valueIsAdjusting) {
                    onTableRowSelected()
                }
            }
        }

        val tableScroll = JScrollPane(customerTable).apply {
            border = BorderFactory.createLineBorder(Color(226, 232, 240))
        }
        leftPanel.add(tableScroll, BorderLayout.CENTER)
        splitPane.leftComponent = leftPanel

        // Right Edit Form using GridBagLayout
        val rightCard = JPanel(BorderLayout()).apply {
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            )
        }

        val formTitle = JLabel("고객 정보 편집 (Kotlin Flow & Coroutines)").apply {
            font = font.deriveFont(Font.BOLD, 15f)
            border = BorderFactory.createEmptyBorder(0, 0, 16, 0)
        }
        rightCard.add(formTitle, BorderLayout.NORTH)

        val formPanel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(6, 4, 6, 4)
        }

        txtName.putClientProperty("JTextField.placeholderText", "고객 이름 입력")
        txtEmail.putClientProperty("JTextField.placeholderText", "example@domain.com")
        txtPhone.putClientProperty("JTextField.placeholderText", "010-0000-0000")

        fun addRow(row: Int, labelText: String, comp: JComponent) {
            gbc.gridx = 0
            gbc.gridy = row
            gbc.weightx = 0.0
            val lbl = JLabel(labelText)
            lbl.horizontalAlignment = SwingConstants.RIGHT
            formPanel.add(lbl, gbc)

            gbc.gridx = 1
            gbc.gridy = row
            gbc.weightx = 1.0
            formPanel.add(comp, gbc)
        }

        addRow(0, "이름 (Name):", txtName)
        addRow(1, "이메일 (Email):", txtEmail)
        addRow(2, "전화번호 (Phone):", txtPhone)
        addRow(3, "상태 (Status):", chkActive)

        // Buttons
        val btnPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 8, 0))
        val btnDelete = JButton("삭제 (Delete)").apply {
            addActionListener { onDelete() }
        }
        val btnCancel = JButton("취소 (Cancel)").apply {
            addActionListener { onCancel() }
        }
        val btnSave = JButton("저장 (Save)").apply {
            putClientProperty("FlatLaf.styleClass", "accent")
            addActionListener { onSave() }
        }

        btnPanel.add(btnDelete)
        btnPanel.add(btnCancel)
        btnPanel.add(btnSave)

        gbc.gridx = 1
        gbc.gridy = 4
        gbc.weightx = 1.0
        gbc.insets = Insets(16, 4, 6, 4)
        formPanel.add(btnPanel, gbc)

        // Spacer to push everything up
        gbc.gridx = 0
        gbc.gridy = 5
        gbc.gridwidth = 2
        gbc.weighty = 1.0
        formPanel.add(JPanel().apply { isOpaque = false }, gbc)

        rightCard.add(formPanel, BorderLayout.CENTER)
        splitPane.rightComponent = rightCard

        add(splitPane, BorderLayout.CENTER)

        setupBindings()
    }

    private fun setupBindings() {
        // Bind customer list to table
        viewModel.customerList.bindToSwing(scope) { list ->
            val prevSelectedRow = customerTable.selectedRow
            var selectedId: Long? = null
            if (prevSelectedRow >= 0) {
                val modelRow = customerTable.convertRowIndexToModel(prevSelectedRow)
                selectedId = tableModel.getValueAt(modelRow, 0) as? Long
            }

            tableModel.rowCount = 0
            var newSelectRow = -1
            for (c in list) {
                val rowIdx = tableModel.rowCount
                if (c.id == selectedId) {
                    newSelectRow = rowIdx
                }
                tableModel.addRow(
                    arrayOf(
                        c.id,
                        c.name,
                        c.email,
                        c.phone,
                        if (c.isActive) "Active" else "Inactive"
                    )
                )
            }

            if (newSelectRow >= 0) {
                val viewRow = customerTable.convertRowIndexToView(newSelectRow)
                customerTable.setRowSelectionInterval(viewRow, viewRow)
            }
        }

        // Bind currentCustomer to form fields
        viewModel.currentCustomer.bindToSwing(scope) { customer ->
            isUpdatingFromModel = true
            try {
                txtName.text = customer.name
                txtEmail.text = customer.email
                txtPhone.text = customer.phone
                chkActive.isSelected = customer.isActive
            } finally {
                isUpdatingFromModel = false
            }
        }
    }

    private fun onTableRowSelected() {
        if (isUpdatingFromModel) return
        val selectedRow = customerTable.selectedRow
        if (selectedRow >= 0) {
            val modelRow = customerTable.convertRowIndexToModel(selectedRow)
            val id = tableModel.getValueAt(modelRow, 0) as? Long
            val customer = viewModel.customerList.value.find { it.id == id }
            viewModel.selectCustomer(customer)
        }
    }

    private fun onSave() {
        val name = txtName.text.trim()
        val email = txtEmail.text.trim()
        val phone = txtPhone.text.trim()
        val isActive = chkActive.isSelected

        val err = viewModel.validate(name)
        if (err != null) {
            JOptionPane.showMessageDialog(this, err, "입력 확인", JOptionPane.WARNING_MESSAGE)
            txtName.requestFocusInWindow()
            return
        }

        viewModel.save(name, email, phone, isActive) {
            onDataChangedCallback?.invoke()
            JOptionPane.showMessageDialog(this, "성공적으로 저장되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE)
        }
    }

    private fun onDelete() {
        val cur = viewModel.currentCustomer.value
        if (cur.id == null) {
            JOptionPane.showMessageDialog(this, "삭제할 고객을 목록에서 선택하세요.", "알림", JOptionPane.INFORMATION_MESSAGE)
            return
        }

        val opt = JOptionPane.showConfirmDialog(
            this,
            "선택한 고객(" + cur.name + ")을 삭제하시겠습니까?",
            "삭제 확인",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        )

        if (opt == JOptionPane.YES_OPTION) {
            viewModel.deleteCurrent {
                onDataChangedCallback?.invoke()
            }
        }
    }

    private fun onCancel() {
        onTableRowSelected()
    }

    private fun performSearch() {
        viewModel.loadCustomers(txtSearch.text)
    }

    fun loadTableData() {
        viewModel.loadCustomers(txtSearch.text)
    }
}