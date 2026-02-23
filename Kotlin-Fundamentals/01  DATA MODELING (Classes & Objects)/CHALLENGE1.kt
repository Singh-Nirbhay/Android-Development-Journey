//🏗️ BUILD CHALLENGE #1
//Create a Banking Account System:
//
//Requirements:
//
//Create a data class BankAccount with:
//
//accountNumber: String
//holderName: String
//balance: Double (default 0.0)
//accountType: String (default "SAVINGS")
//Add an init block that:
//
//Validates accountNumber is exactly 10 digits
//Validates balance is not negative
//Validates holderName is not blank
//Create two accounts with same accountNumber and holderName but different balance
//
//Print whether they are equal (they should NOT be, because balance differs)
//Create a copy of an account with updated balance (+500.0)
//
//Print both original and copy
//Add a computed property isLowBalance that returns true if balance < 1

data class BankAccount(
    val accountNumber: String,
    val holderName: String,
    val balance: Double = 0.0,
    val accountType: String = "SAVINGS"
) {
    init {
        require(accountNumber.length == 10 && accountNumber.all { it.isDigit() }) {
            "Account number must be exactly 10 digits"
        }
        require(balance >= 0) { "Balance cannot be negative" }
        require(holderName.isNotBlank()) { "Holder name cannot be blank" }
    }

    val isLowBalance: Boolean
        get() = balance < 1000
}

fun main() {
    val account1 = BankAccount("1234567890", "Nirbhay")
    val account2 = BankAccount("1234567890", "Nirbhay", 10.0)

    println(account1 == account2)  // false (balance differs)

    val account3 = account2.copy(balance = account2.balance + 500)

    println(account2)  // Original unchanged
    println(account3)  // New with updated balance
    println(account3.isLowBalance)  // true (510 < 1000)
}