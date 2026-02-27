//🏗️ BUILD CHALLENGE #2 (Simplified)
//Create a Simple Bank System:
//
//Step 1: Create a data class Account with:
//
//accountNumber: String
//holderName: String
//balance: Double
//Step 2: Add companion object in Account with:
//
//A constant MIN_BALANCE = 500.0
//A function createEmpty(accountNumber: String, holderName: String) that returns an Account with balance = 0.0
//Step 3: Create a singleton object BankManager with:
//
//A private variable accounts (mutableListOf<Account>)
//fun addAccount(account: Account) — adds to list
//fun getAccountCount(): Int — returns how many accounts
//fun getTotalBalance(): Double — returns sum of all balances
//Step 4: In main():
//
//Create 2 accounts using the createEmpty factory
//Add both to BankManager
//Print account count
//Print total balance

object BankManager {
    private val accounts = mutableListOf<Account>()

    fun addAccount(account: Account) {
        accounts.add(account)
    }

    fun getAccountCount() = accounts.size

    fun getTotalBalance() = accounts.sumOf { it.balance }
}

data class Account(
    val accountNumber: String,
    val holderName: String,
    val balance: Double
) {
    init {
        require(accountNumber.length == 10 && accountNumber.all { it.isDigit() }) {
            "Account number must be exactly 10 digits"
        }
        require(holderName.isNotBlank()) { "Name cannot be blank" }
    }

    companion object {
        const val MIN_BALANCE = 500.0

        fun createEmpty(accountNumber: String, holderName: String) = Account(
            accountNumber = accountNumber,
            holderName = holderName,
            balance = 0.0
        )
    }
}

fun main() {
    val account1 = Account.createEmpty("1234567890", "Nirbhay")
    val account2 = Account.createEmpty("1234567120", "Dhanji")

    BankManager.addAccount(account1)
    BankManager.addAccount(account2)

    println(BankManager.getAccountCount())  // 2
    println(BankManager.getTotalBalance())  // 0.0
}