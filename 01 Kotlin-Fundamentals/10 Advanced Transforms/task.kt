//🏗️ BUILD CHALLENGE #10
//Create an E-Commerce Analytics System:
//
//Step 1: Create data classes:

//Kotlin
//
//data class Order(
//    val id: String,
//    val customerId: String,
//    val status: String,  // "PENDING", "SHIPPED", "DELIVERED"
//    val items: List<OrderItem>
//)
//
//data class OrderItem(
//    val productName: String,
//    val price: Double,
//    val quantity: Int
//)
//Step 2: Create sample data:
//
//5 orders with different customers and statuses
//Each order has 2-3 items
//Mix of statuses
//Step 3: Using collection operations, calculate:
//
//flatMap: Get ALL items from all orders in one flat list
//groupBy: Group orders by status
//associateBy: Create a map for quick order lookup by ID
//partition: Split orders into delivered vs not-delivered
//fold: Calculate total revenue (sum of price * quantity for all items in all orders)
//Bonus: Find the most expensive single item across all orders
//Step 4: Print results with clear labels.
//
//







data class Order(
    val id: String,
    val customerId: String,
    val status: String,
    val items: List<OrderItem>
)

data class OrderItem(
    val productName: String,
    val price: Double,
    val quantity: Int
)

fun main() {
    val orders = listOf(
        Order(
            id = "O1",
            customerId = "C1",
            status = "DELIVERED",
            items = listOf(
                OrderItem("Laptop", 800.0, 1),
                OrderItem("Mouse", 20.0, 2)
            )
        ),
        Order(
            id = "O2",
            customerId = "C2",
            status = "PENDING",
            items = listOf(
                OrderItem("Phone", 600.0, 1),
                OrderItem("Charger", 25.0, 1)
            )
        ),
        Order(
            id = "O3",
            customerId = "C1",
            status = "SHIPPED",
            items = listOf(
                OrderItem("Headphones", 150.0, 1),
                OrderItem("Keyboard", 70.0, 1)
            )
        ),
        Order(
            id = "O4",
            customerId = "C3",
            status = "DELIVERED",
            items = listOf(
                OrderItem("Monitor", 300.0, 1),
                OrderItem("HDMI Cable", 15.0, 3)
            )
        ),
        Order(
            id = "O5",
            customerId = "C4",
            status = "PENDING",
            items = listOf(
                OrderItem("Tablet", 400.0, 1),
                OrderItem("Cover", 30.0, 1)
            )
        )
    )

    println("🛒 E-COMMERCE ANALYTICS\n")

    // 1️⃣ flatMap
    println("=== All Items (${orders.size} orders) ===")
    val allItems = orders.flatMap { it.items }
    allItems.forEach { item ->
        println("  ${item.productName} — ₹${item.price} x${item.quantity}")
    }

    // 2️⃣ groupBy
    println("\n=== Orders By Status ===")
    val groupedByStatus = orders.groupBy { it.status }
    groupedByStatus.forEach { (status, orderList) ->
        println("  $status: ${orderList.size} orders")
        orderList.forEach { order ->
            println("    - Order ${order.id} (Customer ${order.customerId})")
        }
    }

    // 3️⃣ associateBy
    val ordersById = orders.associateBy { it.id }
    println("\n=== Quick Lookup Demo ===")
    val orderO3 = ordersById["O3"]
    orderO3?.let {
        println("  Order O3:")
        println("    Customer: ${it.customerId}")
        println("    Status: ${it.status}")
        println("    Items: ${it.items.size}")
    }

    // 4️⃣ partition
    val (delivered, notDelivered) = orders.partition { it.status == "DELIVERED" }
    println("\n=== Delivery Status ===")
    println("  ✅ Delivered: ${delivered.size} orders")
    delivered.forEach { println("      ${it.id}") }
    println("  ⏳ Not Delivered: ${notDelivered.size} orders")
    notDelivered.forEach { println("      ${it.id} (${it.status})") }

    // 5️⃣ fold
    println("\n=== Revenue Calculation ===")
    val totalRevenue = orders.fold(0.0) { acc, order ->
        val orderTotal = order.items.sumOf { it.price * it.quantity }
        println("  Order ${order.id}: ₹$orderTotal")
        acc + orderTotal
    }
    println("─".repeat(30))
    println("  💰 Total Revenue: ₹$totalRevenue")

    // Bonus
    println("\n=== Most Expensive Item ===")
    val mostExpensiveItem = allItems.maxByOrNull { it.price }
    mostExpensiveItem?.let {
        println("  🏆 ${it.productName}")
        println("     Price: ₹${it.price}")
        println("     Total value: ₹${it.price * it.quantity}")
    }
}