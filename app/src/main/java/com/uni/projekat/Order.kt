package com.uni.projekat

class Order(
    val id: Int,
    val customer: Customer,
    private val items: List<CartItem>,
    val discountPercent: Double = 0.0
) {
    var status: OrderStatus = OrderStatus.NEW
        private set

    init {
        require(id > 0) { "Order ID must be positive" }
        require(items.isNotEmpty()) { "Cannot create an order with an empty cart" }
        require(discountPercent in 0.0..100.0) { "Discount must be between 0 and 100%" }
    }

    fun getItems(): List<CartItem> {
        val items_copy = mutableListOf<CartItem>()
        for (cartItem in items) {
            items_copy.add(cartItem.copy())
        }
        return items_copy.toList()
    }

    fun subtotal(): Double = items.sumOf { it.subtotal() }

    fun total(): Double = subtotal().applyDiscount(discountPercent)

    fun changeStatus(next: OrderStatus) {
        val allowed = when (status) {
            OrderStatus.NEW -> setOf(OrderStatus.CONFIRMED, OrderStatus.CANCELLED)
            OrderStatus.CONFIRMED -> setOf(OrderStatus.SHIPPED, OrderStatus.CANCELLED)
            OrderStatus.SHIPPED -> setOf(OrderStatus.DELIVERED)
            OrderStatus.DELIVERED, OrderStatus.CANCELLED -> emptySet()
        }
        require(next in allowed) { "Transition from $status to $next is not allowed" }
        status = next
    }
}