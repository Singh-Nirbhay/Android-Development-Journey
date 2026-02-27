//🏗️ BUILD CHALLENGE #3


@JvmInline
value class ProductId(val value: String) {
    init {
        require(value.isNotBlank()) { "ProductId cannot be blank" }
    }
}

@JvmInline
value class OrderId(val value: String) {
    init {
        require(value.isNotBlank()) { "OrderId cannot be blank" }
    }
}

@JvmInline
value class CustomerId(val value: String) {
    init {
        require(value.isNotBlank()) { "CustomerId cannot be blank" }
    }
}

data class OrderItem(
    val productId: ProductId,
    val quantity: Int,
    val price: Double
) {
    init {
        require(quantity > 0) { "Quantity must be positive" }
        require(price >= 0) { "Price cannot be negative" }
    }

    val itemTotal: Double
        get() = quantity * price
}

data class Order(
    val id: OrderId,
    val customerId: CustomerId,
    val items: List<OrderItem>,
    val status: Order.Status
) {
    class Status(val name: String, val code: Int)

    companion object {
        val PENDING = Status("Pending", 0)
        val SHIPPED = Status("Shipped", 1)
        val DELIVERED = Status("Delivered", 2)
    }

    val totalPrice: Double
        get() = items.sumOf { it.itemTotal }
}

fun main() {
    val item1 = OrderItem(
        productId = ProductId("P001"),
        quantity = 2,
        price = 500.0
    )

    val item2 = OrderItem(
        productId = ProductId("P002"),
        quantity = 1,
        price = 1000.0
    )

    val order = Order(
        id = OrderId("O1001"),
        customerId = CustomerId("C001"),
        items = listOf(item1, item2),
        status = Order.PENDING
    )

    println(order)
    println("Total Price: ${order.totalPrice}")
}