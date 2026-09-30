package com.example.swingapp

import com.example.swingapp.service.CustomerService
import com.example.swingapp.viewmodel.CustomerViewModel
import com.example.swingapp.viewmodel.DashboardViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ApplicationTest {

    @Test
    fun testCustomerServiceAndViewModel() {
        val service = CustomerService()
        assertTrue(service.getCustomerCount() > 0)
        assertTrue(service.getOrderCount() > 0)
        assertTrue(service.getTotalRevenue() > 0.0)

        val vm = CustomerViewModel(service)
        assertNotNull(vm.customerList.value)
    }

    @Test
    fun testDashboardViewModel() {
        val service = CustomerService()
        val dvm = DashboardViewModel(service)
        dvm.refresh()
        assertNotNull(dvm.state.value)
    }
}
