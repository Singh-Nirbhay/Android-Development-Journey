//🏗️ BUILD CHALLENGE #4
//Create a Payment System:
//
//Step 1: Create an interface PaymentMethod:
//
//fun processPayment(amount: Double): Boolean
//fun getMethodName(): String
//Step 2: Create an abstract class BasePayment:
//
//    Property: val transactionId: String (generated in constructor using System.currentTimeMillis())
//Abstract property: val fee: Double
//Regular function: fun calculateTotal(amount: Double): Double (returns amount + fee)
//Step 3: Create class CreditCardPayment:
//
//    Implements PaymentMethod
//Extends BasePayment
//Has property: cardNumber: String
//fee = 2.5
//processPayment prints "Processing credit card payment of $amount" and returns true
//getMethodName returns "Credit Card"
//Step 4: Create class UpiPayment:
//
//    Implements PaymentMethod
//Extends BasePayment
//Has property: upiId: String
//fee = 0.0 (no fee for UPI)
//processPayment prints "Processing UPI payment of $amount" and returns true
//getMethodName returns "UPI"
//Step 5: In main():
//
//Create one CreditCardPayment and one UpiPayment
//Process payment of 1000.0 for each
//Print the total (amount + fee) for each

interface PaymentMethod {
    fun processPayment(amount: Double): Boolean
    fun getMethodName(): String
}

abstract class BasePayment : PaymentMethod {
    val transactionId: String = System.currentTimeMillis().toString()

    abstract val fee: Double

    fun calculateTotal(amount: Double) = amount + fee
}

class CreditCardPayment(val cardNumber: String) : BasePayment() {

    init {
        require(cardNumber.length == 16 && cardNumber.all { it.isDigit() }) {
            "Card number must be 16 digits"
        }
    }

    override val fee = 2.5

    override fun processPayment(amount: Double): Boolean {
        val total = calculateTotal(amount)
        println("Processing ${getMethodName()} payment of ₹$amount")
        println("Fee: ₹$fee | Total: ₹$total")
        println("Transaction ID: $transactionId")
        return true
    }

    override fun getMethodName() = "Credit Card"
}

class UpiPayment(val upiId: String) : BasePayment() {

    init {
        require(upiId.contains("@")) { "Invalid UPI ID format" }
    }

    override val fee = 0.0

    override fun processPayment(amount: Double): Boolean {
        val total = calculateTotal(amount)
        println("Processing ${getMethodName()} payment of ₹$amount")
        println("Fee: ₹$fee | Total: ₹$total")
        println("Transaction ID: $transactionId")
        return true
    }

    override fun getMethodName() = "UPI"
}

fun main() {
    val creditCardPayment = CreditCardPayment("1234567890123456")
    val upiPayment = UpiPayment("nirbhay@paytm")

    println("=== Credit Card ===")
    creditCardPayment.processPayment(1000.0)

    println()

    println("=== UPI ===")
    upiPayment.processPayment(1000.0)
}