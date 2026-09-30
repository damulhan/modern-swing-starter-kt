package com.example.swingapp.viewmodel

import com.example.swingapp.model.Order
import com.example.swingapp.service.CustomerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardState(
    val customerCount: Int = 0,
    val orderCount: Int = 0,
    val totalRevenue: Double = 0.0,
    val recentOrders: List<Order> = emptyList()
)

class DashboardViewModel(
    private val customerService: CustomerService,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        scope.launch(Dispatchers.IO) {
            val customerCount = customerService.getCustomerCount()
            val orderCount = customerService.getOrderCount()
            val totalRevenue = customerService.getTotalRevenue()
            val recentOrders = customerService.getRecentOrders()

            _state.value = DashboardState(
                customerCount = customerCount,
                orderCount = orderCount,
                totalRevenue = totalRevenue,
                recentOrders = recentOrders
            )
        }
    }
}
