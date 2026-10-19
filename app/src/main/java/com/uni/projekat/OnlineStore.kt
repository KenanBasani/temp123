package com.uni.projekat

class OnlineStore(val name: String) {
    private val catalog = mutableListOf<Product>()
    private val orders = mutableListOf<Order>()
    private var nextOrderId = 101

    fun addProduct(product: Product) {
        require(catalog.none { it.id == product.id }) { "Product ID already exists" }
        catalog.add(product)
    }

    fun showCatalog(): List<Product> = catalog.toList()

    fun searchCatalog(query: String): List<Product> =
        catalog.filter { it.name.contains(query, ignoreCase = true) }

    fun filterByCategory(category: String): List<Product> =
        catalog.filter { it.category.equals(category, ignoreCase = true) }

    fun findProduct(id: Int): Product =
        catalog.find { it.id == id }
            ?: throw NoSuchElementException("Product with ID $id does not exist in catalog")

    fun confirmOrder(cart: Cart, discountPercent: Double = 0.0): Order {
        check(!cart.isEmpty()) { "Cart is empty! Add products before confirming." }
        val order = Order(
            id = nextOrderId++,
            customer = cart.customer,
            items = cart.getItems(),
            discountPercent = discountPercent
        )
        order.changeStatus(OrderStatus.CONFIRMED)
        orders.add(order)
        cart.clear()
        return order
    }

    fun getOrder(orderId: Int): Order =
        orders.find { it.id == orderId }
            ?: throw NoSuchElementException("Order #$orderId not found")

    fun generateReport(): String {
        val sb = StringBuilder()
        sb.append("=== ORDERS REPORT: $name ===\n")
        if (orders.isEmpty()) {
            sb.append("No orders placed yet.\n")
            return sb.toString()
        }
        for (order in orders) {
            sb.append(
                "Order #${order.id} | ${order.customer.name} | ${order.status} | " +
                        "Total: ${"%.2f".format(order.total())} EUR (Discount: ${order.discountPercent}%)\n"
            )
        }
        sb.append("\nTotal orders: ${orders.size}\n")
        val deliveredRevenue = orders
            .filter { it.status == OrderStatus.DELIVERED }
            .sumOf { it.total() }
        sb.append("Revenue from DELIVERED orders: ${"%.2f".format(deliveredRevenue)} EUR\n")
        return sb.toString()
    }
}