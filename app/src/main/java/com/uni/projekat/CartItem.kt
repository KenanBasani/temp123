package com.uni.projekat

data class CartItem(val product: Product, var quantity: Int = 1) {
    init {
        require(quantity > 0) { "Quantity has to be positive" }
    }

    fun subtotal(): Double = product.price * quantity
}