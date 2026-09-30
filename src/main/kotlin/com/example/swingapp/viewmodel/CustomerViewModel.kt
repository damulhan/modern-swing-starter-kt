package com.example.swingapp.viewmodel

import com.example.swingapp.model.Customer
import com.example.swingapp.service.CustomerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomerViewModel(
    private val customerService: CustomerService,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    private val _customerList = MutableStateFlow<List<Customer>>(emptyList())
    val customerList: StateFlow<List<Customer>> = _customerList.asStateFlow()

    private val _currentCustomer = MutableStateFlow(Customer())
    val currentCustomer: StateFlow<Customer> = _currentCustomer.asStateFlow()

    private val _statusMessage = MutableStateFlow("Ready")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    init {
        loadCustomers()
    }

    fun loadCustomers(keyword: String? = null) {
        scope.launch(Dispatchers.IO) {
            val list = customerService.search(keyword)
            _customerList.value = list
            _statusMessage.value = "고객 목록 " + list.size + "건 로드 완료"
        }
    }

    fun selectCustomer(customer: Customer?) {
        _currentCustomer.value = customer?.copy() ?: Customer()
    }

    fun createNewCustomer() {
        _currentCustomer.value = Customer(name = "", email = "", phone = "", isActive = true)
        _statusMessage.value = "새 고객 입력 모드"
    }

    fun updateCurrentDraft(name: String, email: String, phone: String, isActive: Boolean) {
        val cur = _currentCustomer.value
        _currentCustomer.value = cur.copy(name = name, email = email, phone = phone, isActive = isActive)
    }

    fun validate(name: String): String? {
        if (name.isBlank()) {
            return "고객 이름을 입력해 주세요."
        }
        return null
    }

    fun save(name: String, email: String, phone: String, isActive: Boolean, onSuccess: () -> Unit) {
        val validationErr = validate(name)
        if (validationErr != null) {
            _statusMessage.value = validationErr
            return
        }

        scope.launch(Dispatchers.IO) {
            val toSave = _currentCustomer.value.copy(
                name = name.trim(),
                email = email.trim(),
                phone = phone.trim(),
                isActive = isActive
            )
            val saved = customerService.save(toSave)
            _currentCustomer.value = saved
            val refreshed = customerService.findAll()
            _customerList.value = refreshed
            _statusMessage.value = "'" + saved.name + "' 저장 성공"
            launch(Dispatchers.Main.immediate) {
                onSuccess()
            }
        }
    }

    fun deleteCurrent(onSuccess: () -> Unit) {
        val cur = _currentCustomer.value
        val id = cur.id ?: return

        scope.launch(Dispatchers.IO) {
            val deleted = customerService.delete(id)
            if (deleted) {
                _currentCustomer.value = Customer()
                _customerList.value = customerService.findAll()
                _statusMessage.value = "'" + cur.name + "' 삭제 완료"
                launch(Dispatchers.Main.immediate) {
                    onSuccess()
                }
            }
        }
    }
}