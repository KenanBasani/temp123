package com.uni.projekat

data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double
) {
    init {
        require(id > 0) { "Product ID has to be positive" }
        require(name.isNotBlank()) { "Product name cannot be blank" }
        require(category.isNotBlank()) { "Category cannot be blank" }
        require(price >= 0 && price.isFinite()) { "Price must be valid and positive" }
    }
}
fun Double.applyDiscount(discountPercent: Double = 0.0): Double {
    require(discountPercent.isFinite() && discountPercent in 0.0..100.0) {
        "Discount cant be below 0% and higher than 100%"
    }
    return this * (1 - discountPercent / 100.0)
}