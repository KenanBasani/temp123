package com.uni.projekat

class Cart(val customer: Customer) {
    private val items = mutableListOf<CartItem>()

    fun addItem(product: Product, quantity: Int = 1) {
        require(quantity > 0) { "Quantity must be positive" }
        val existing = items.find { it.product.id == product.id }
        if (existing == null) {
            items.add(CartItem(product, quantity))
        } else {
            existing.quantity = Math.addExact(existing.quantity, quantity)
        }
    }

    fun removeItem(productId: Int) {
        if (!items.removeAll { it.product.id == productId }) {
            throw NoSuchElementException("Product with ID $productId is not in the cart")
        }
    }

    fun getItems(): List<CartItem> {
        val items_copy = mutableListOf<CartItem>()
        for (cartItem in items) {
            items_copy.add(cartItem.copy())
        }
        return items_copy.toList()
    }
    fun subtotal(): Double = items.sumOf { it.subtotal() }

    fun total(discountPercent: Double = 0.0): Double {
        return subtotal().applyDiscount(discountPercent)
    }

    fun clear() {
        items.clear()
    }

    fun isEmpty(): Boolean = items.isEmpty()
}