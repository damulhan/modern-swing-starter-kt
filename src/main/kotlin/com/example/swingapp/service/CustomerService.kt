package com.example.swingapp.service

import com.example.swingapp.model.Customer
import com.example.swingapp.model.Order
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicLong

class CustomerService {
    private val customers = mutableListOf<Customer>()
    private val orders = mutableListOf<Order>()
    private val idGenerator = AtomicLong(1)

    init {
        initSampleData()
    }

    private fun initSampleData() {
        save(Customer(name = "John Doe", email = "john.doe@example.com", phone = "010-1234-5678", isActive = true))
        save(Customer(name = "Alice Smith", email = "alice.smith@example.com", phone = "010-2345-6789", isActive = true))
        save(Customer(name = "Bob Johnson", email = "bob.j@example.com", phone = "010-3456-7890", isActive = false))
        save(Customer(name = "Emma Watson", email = "emma.w@example.com", phone = "010-4567-8901", isActive = true))
        save(Customer(name = "Michael Brown", email = "mbrown@example.com", phone = "010-5678-9012", isActive = true))
        save(Customer(name = "Sarah Davis", email = "sarah.d@example.com", phone = "010-6789-0123", isActive = false))
        save(Customer(name = "James Wilson", email = "jwilson@example.com", phone = "010-7890-1234", isActive = true))

        orders.add(Order("ORD-1001", "John Doe", "Completed", 1250.00, LocalDate.now().minusDays(1)))
        orders.add(Order("ORD-1002", "Alice Smith", "Processing", 340.50, LocalDate.now().minusDays(2)))
        orders.add(Order("ORD-1003", "Emma Watson", "Completed", 4520.00, LocalDate.now().minusDays(3)))
        orders.add(Order("ORD-1004", "Michael Brown", "Shipped", 890.00, LocalDate.now().minusDays(4)))
        orders.add(Order("ORD-1005", "James Wilson", "Completed", 2150.00, LocalDate.now().minusDays(5)))
    }

    @Synchronized
    fun findAll(): List<Customer> = customers.map { it.copy() }

    @Synchronized
    fun search(keyword: String?): List<Customer> {
        if (keyword.isNullOrBlank()) {
            return findAll()
        }
        val kw = keyword.trim().lowercase()
        return customers
            .filter {
                it.name.lowercase().contains(kw) ||
                it.email.lowercase().contains(kw) ||
                it.phone.contains(kw)
            }
            .map { it.copy() }
    }

    @Synchronized
    fun save(customer: Customer): Customer {
        return if (customer.id == null) {
            val newCustomer = customer.copy(id = idGenerator.getAndIncrement())
            customers.add(newCustomer)
            newCustomer.copy()
        } else {
            val index = customers.indexOfFirst { it.id == customer.id }
            if (index != -1) {
                customers[index] = customer.copy()
                customer.copy()
            } else {
                customers.add(customer.copy())
                customer.copy()
            }
        }
    }

    @Synchronized
    fun delete(id: Long): Boolean {
        return customers.removeAll { it.id == id }
    }

    @Synchronized
    fun getRecentOrders(): List<Order> = ArrayList(orders)

    @Synchronized
    fun getCustomerCount(): Int = customers.size

    @Synchronized
    fun getOrderCount(): Int = orders.size

    @Synchronized
    fun getTotalRevenue(): Double = orders.sumOf { it.amount }
}
