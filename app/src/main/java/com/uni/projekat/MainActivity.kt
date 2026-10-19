package com.uni.projekat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var store: OnlineStore
    private lateinit var customer: Customer
    private lateinit var cart: Cart
    private var currentDiscount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize store, sample products, customer (using named arguments), and cart
        store = OnlineStore("Tech & Home Store")
        store.addProduct(Product(1, "Laptop", "Tech", 750.00))
        store.addProduct(Product(2, "Mouse", "Tech", 25.00))
        store.addProduct(Product(3, "Keyboard", "Tech", 45.00))
        store.addProduct(Product(4, "Coffee Mug", "Home", 8.50))
        store.addProduct(Product(5, "Desk Lamp", "Home", 30.00))

        customer = Customer(id = 1, name = "Arta") // Named arguments
        cart = Cart(customer)

        // Bind XML views with clear, readable names
        val inputSearchQuery = findViewById<EditText>(R.id.inputSearchQuery)
        val buttonShowAll = findViewById<Button>(R.id.buttonShowAll)
        val buttonSearch = findViewById<Button>(R.id.buttonSearch)
        val buttonFilter = findViewById<Button>(R.id.buttonFilter)
        val textCatalog = findViewById<TextView>(R.id.textCatalog)

        val inputProductId = findViewById<EditText>(R.id.inputProductId)
        val inputQuantity = findViewById<EditText>(R.id.inputQuantity)
        val buttonAddToCart = findViewById<Button>(R.id.buttonAddToCart)
        val buttonRemoveFromCart = findViewById<Button>(R.id.buttonRemoveFromCart)

        val inputDiscount = findViewById<EditText>(R.id.inputDiscount)
        val buttonApplyDiscount = findViewById<Button>(R.id.buttonApplyDiscount)
        val textCart = findViewById<TextView>(R.id.textCart)
        val buttonConfirmOrder = findViewById<Button>(R.id.buttonConfirmOrder)

        val inputOrderId = findViewById<EditText>(R.id.inputOrderId)
        val buttonShipOrder = findViewById<Button>(R.id.buttonShipOrder)
        val buttonDeliverOrder = findViewById<Button>(R.id.buttonDeliverOrder)
        val buttonCancelOrder = findViewById<Button>(R.id.buttonCancelOrder)
        val buttonReport = findViewById<Button>(R.id.buttonReport)
        val textOutput = findViewById<TextView>(R.id.textOutput)

        fun displayProducts(products: List<Product>) {
            if (products.isEmpty()) {
                textCatalog.text = "No matching products found."
                return
            }
            textCatalog.text = products.joinToString("\n") {
                "${it.id}. ${it.name} (${it.category}) - ${"%.2f".format(it.price)} EUR"
            }
        }

        fun updateCartDisplay() {
            if (cart.isEmpty()) {
                textCart.text = "Cart is empty."
                return
            }
            val cartLines = cart.getItems().joinToString("\n") {
                "${it.product.name} x${it.quantity} = ${"%.2f".format(it.subtotal())} EUR"
            }
            val subtotalFormatted = "%.2f".format(cart.subtotal())
            val totalFormatted = "%.2f".format(cart.total(discountPercent = currentDiscount))
            textCart.text = "$cartLines\nSubtotal: $subtotalFormatted EUR | Total ($currentDiscount% off): $totalFormatted EUR"
        }

        // Initial screen load
        displayProducts(store.showCatalog())
        updateCartDisplay()

        // 1. Catalog Display, Search & Filter
        buttonShowAll.setOnClickListener {
            displayProducts(store.showCatalog())
            textOutput.text = "Showing all products."
        }

        buttonSearch.setOnClickListener {
            val query = inputSearchQuery.text.toString().trim()
            displayProducts(store.searchCatalog(query))
            textOutput.text = "Search results for: '$query'"
        }

        buttonFilter.setOnClickListener {
            val category = inputSearchQuery.text.toString().trim()
            displayProducts(store.filterByCategory(category))
            textOutput.text = "Filtered by category: '$category'"
        }

        // 2. Add & Remove from Cart
        buttonAddToCart.setOnClickListener {
            try {
                val productId = inputProductId.text.toString().toIntOrNull()
                    ?: throw IllegalArgumentException("Please enter a valid Product ID")
                val quantityText = inputQuantity.text.toString().trim()
                val product = store.findProduct(productId)

                if (quantityText.isEmpty()) {
                    cart.addItem(product) // Demonstrates default argument (quantity = 1)
                } else {
                    val quantityNumber = quantityText.toIntOrNull()
                        ?: throw IllegalArgumentException("Quantity must be a valid number")
                    cart.addItem(product = product, quantity = quantityNumber) // Named arguments
                }

                updateCartDisplay()
                textOutput.text = "Added '${product.name}' to cart."
            } catch (error: Exception) {
                textOutput.text = "Error: ${error.message}"
            }
        }

        buttonRemoveFromCart.setOnClickListener {
            try {
                val productId = inputProductId.text.toString().toIntOrNull()
                    ?: throw IllegalArgumentException("Please enter a valid Product ID to remove")
                cart.removeItem(productId)
                updateCartDisplay()
                textOutput.text = "Removed product #$productId from cart."
            } catch (error: Exception) {
                textOutput.text = "Error: ${error.message}"
            }
        }

        // 3. Discount & Total Calculation
        buttonApplyDiscount.setOnClickListener {
            try {
                val discountValue = inputDiscount.text.toString().toDoubleOrNull() ?: 0.0
                // Test validity using extension function before saving
                cart.total(discountPercent = discountValue)
                currentDiscount = discountValue
                updateCartDisplay()
                textOutput.text = "Applied $currentDiscount% discount."
            } catch (error: Exception) {
                textOutput.text = "Error: ${error.message}"
            }
        }

        // 4. Confirm Order
        buttonConfirmOrder.setOnClickListener {
            try {
                val order = store.confirmOrder(cart, discountPercent = currentDiscount)
                currentDiscount = 0.0
                inputDiscount.text.clear()
                updateCartDisplay()
                inputOrderId.setText(order.id.toString())
                textOutput.text = "Order #${order.id} confirmed! Status: ${order.status}. " +
                        "Total: ${"%.2f".format(order.total())} EUR"
            } catch (error: Exception) {
                textOutput.text = "Error: ${error.message}"
            }
        }

        // 5. Change Order Status
        fun changeOrderState(newStatus: OrderStatus) {
            try {
                val orderId = inputOrderId.text.toString().toIntOrNull()
                    ?: throw IllegalArgumentException("Enter a valid Order ID")
                val order = store.getOrder(orderId)
                order.changeStatus(newStatus)
                textOutput.text = "Order #${order.id} status updated to: ${order.status}"
            } catch (error: Exception) {
                textOutput.text = "Error: ${error.message}"
            }
        }

        buttonShipOrder.setOnClickListener { changeOrderState(OrderStatus.SHIPPED) }
        buttonDeliverOrder.setOnClickListener { changeOrderState(OrderStatus.DELIVERED) }
        buttonCancelOrder.setOnClickListener { changeOrderState(OrderStatus.CANCELLED) }

        // 6. Orders Report
        buttonReport.setOnClickListener {
            textOutput.text = store.generateReport()
        }
    }
}