package com.uni.projekat

data class Customer(val id: Int, val name: String) {
    init {
        require(id > 0) { "Customer ID must be positive" }
        require(name.isNotBlank()) { "Customer name cant be blank" }
    }
}