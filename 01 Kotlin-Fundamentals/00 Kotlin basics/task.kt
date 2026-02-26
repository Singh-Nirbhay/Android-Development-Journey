//🏗️ MEGA BUILD CHALLENGE — ALL CONCEPTS
//Create a Complete User Management System that tests ALL fundamentals:
//
//Kotlin

// ===================
// PART A: Constants & Config
// ===================
// 1. Create const val for MAX_NAME_LENGTH (50), MIN_AGE (13), MAX_AGE (120)
// 2. Create const val for DEFAULT_COUNTRY ("India")

// ===================
// PART B: Data Class with Nullables
// ===================
// 3. Create data class User:
//    - id: String
//    - name: String
//    - email: String?
//    - age: Int
//    - country: String (default DEFAULT_COUNTRY)

// ===================
// PART C: Extension Functions
// ===================
// 4. Create extension function String.isValidEmail() -> Boolean
//    (contains "@" and ".")
//
// 5. Create extension function Int.isValidAge() -> Boolean
//    (between MIN_AGE and MAX_AGE)
//
// 6. Create extension function User.getDisplayName() -> String
//    (If email exists, return "name (email)", else just "name")

// ===================
// PART D: Nullable Handling
// ===================
// 7. Create function processEmail(email: String?) -> String
//    - Use safe call and Elvis to return email uppercase or "NO EMAIL"
//
// 8. Create function sendWelcome(user: User)
//    - Use ?.let to send email only if email exists
//    - Print "Sending email to <email>" or "No email to send"

// ===================
// PART E: lateinit & lazy
// ===================
// 9. Create class UserManager:
//    - lateinit var currentUser: User
//    - lazy val analytics: Analytics (print "Analytics initialized" when created)
//    - fun hasCurrentUser(): Boolean (check if initialized)

// ===================
// PART F: Higher-Order Function
// ===================
// 10. Create function processUsers:
//     - Takes list of users
//     - Takes filter: (User) -> Boolean
//     - Takes transform: (User) -> String
//     - Returns List<String> of transformed filtered users

// ===================
// PART G: Scope Functions
// ===================
// 11. Create function createUser(name: String, email: String?, age: Int) -> User?
//     - Use takeIf to validate name length
//     - Use apply to configure user
//     - Use also to log creation
//     - Return null if invalid

// ===================
// PART H: Inline + Reified
// ===================
// 12. Create inline function <reified T> List<Any>.filterByType(): List<T>
//     - Returns only items of type T

// ===================
// PART I: Test Everything in main()
// ===================
// 13. Create 3 users (one with null email)
// 14. Use processUsers with custom filter and transform
// 15. Test UserManager with lateinit and lazy
// 16. Test filterByType with mixed list
// 17. Use scope functions in chain
//This single challenge tests:
//
//✅ val, var, const val
//✅ Type inference
//✅ String templates
//✅ Nullable types
//✅ Safe calls, Elvis, ?.let
//✅ lateinit, lazy
//✅ Extension functions
//✅ Higher-order functions
//✅ Lambdas
//✅ Scope functions
//✅ inline + reified


// Configuration stuff - keeping limits for user data
const val MAX_NAME_LENGTH = 50
const val MIN_AGE = 13
const val MAX_AGE = 120
const val DEFAULT_COUNTRY = "India"

// Simple user model
data class User(
    val id: String,
    val name: String,
    val email: String?,
    val age: Int,
    val country: String = DEFAULT_COUNTRY
)

// Adding some useful functions to String for email validation
fun String.isValidEmail(): Boolean {
    // Basic email check - just needs @ and .
    return this.contains("@") && this.contains(".")
}

// Check if age is in valid range
fun Int.isValidAge(): Boolean {
    return this >= MIN_AGE && this <= MAX_AGE
}

// Get a nice display name with email if available
fun User.getDisplayName(): String {
    if (email != null) {
        return "$name ($email)"
    }
    return name
}

// Process email - return uppercase or default message
fun processEmail(email: String?): String {
    if (email != null) {
        return email.uppercase()
    }
    return "NO EMAIL"
}

// Send welcome email only if user has an email
fun sendWelcome(user: User) {
    if (user.email != null) {
        println("Sending email to ${user.email}")
    } else {
        println("No email to send")
    }
}

// Simple analytics class
class Analytics {
    init {
        println("Analytics initialized")
    }

    fun track(event: String) {
        println("Tracked: $event")
    }
}

// User manager to handle current user and analytics
class UserManager {
    lateinit var currentUser: User

    // Analytics is created only when first used
    val analytics: Analytics by lazy {
        Analytics()
    }

    fun hasCurrentUser(): Boolean {
        return ::currentUser.isInitialized
    }

    fun setCurrentUser(user: User) {
        currentUser = user
        // This will trigger analytics initialization if not already done
        analytics.track("User set: ${user.name}")
    }
}

// Filter and transform users - useful for processing lists
fun processUsers(
    users: List<User>,
    filterCondition: (User) -> Boolean,
    transformFunction: (User) -> String
): List<String> {
    val filtered = users.filter(filterCondition)
    val transformed = filtered.map(transformFunction)
    return transformed
}

// Create user with validation
fun createUser(name: String, email: String?, age: Int): User? {
    // Check if name is valid length
    if (name.length > MAX_NAME_LENGTH || name.isBlank()) {
        println("Invalid name: $name")
        return null
    }

    // Check age
    if (!age.isValidAge()) {
        println("Invalid age: $age")
        return null
    }

    // Validate email if provided
    val validEmail = if (email != null && email.isValidEmail()) {
        email
    } else {
        null
    }

    // Create the user
    val newUser = User(
        id = "U${System.currentTimeMillis() % 10000}",
        name = name,
        email = validEmail,
        age = age
    )

    println("Created user: ${newUser.getDisplayName()}")
    return newUser
}

// Filter list by type - using reified to keep type info at runtime
inline fun <reified T> List<Any>.filterByType(): List<T> {
    val result = mutableListOf<T>()
    for (item in this) {
        if (item is T) {
            result.add(item)
        }
    }
    return result
}

fun main() {
    println("=== User Management System ===\n")

    // Creating some test users
    val nirbhay = User("U001", "Nirbhay", "nirbhay@test.com", 21)
    val john = User("U002", "John", null, 25, "USA")  // no email
    val priya = User("U003", "Priya", "priya@example.in", 17)

    val allUsers = listOf(nirbhay, john, priya)

    println("Created users:")
    for (user in allUsers) {
        println("- ${user.getDisplayName()}")
    }

    // Testing email validation
    println("\nEmail validation tests:")
    val testEmails = listOf("valid@email.com", "invalid", "test@gmail.com")
    for (email in testEmails) {
        println("$email -> ${email.isValidEmail()}")
    }

    // Testing age validation
    println("\nAge validation tests:")
    val testAges = listOf(10, 18, 25, 150)
    for (age in testAges) {
        println("$age -> ${age.isValidAge()}")
    }

    // Testing email processing
    println("\nEmail processing:")
    println("With email: ${processEmail("test@example.com")}")
    println("Without email: ${processEmail(null)}")

    // Sending welcome emails
    println("\nSending welcome emails:")
    for (user in allUsers) {
        sendWelcome(user)
    }

    // Testing UserManager
    println("\nTesting UserManager:")
    val manager = UserManager()
    println("Has user? ${manager.hasCurrentUser()}")

    manager.setCurrentUser(nirbhay)
    println("Has user now? ${manager.hasCurrentUser()}")
    println("Current user: ${manager.currentUser.name}")

    // Testing processUsers
    println("\nFiltering adult users:")
    val adults = processUsers(
        allUsers,
        { user -> user.age >= 18 },
        { user -> user.name }
    )
    println("Adults: $adults")

    println("\nUsers with email:")
    val withEmail = processUsers(
        allUsers,
        { user -> user.email != null },
        { user -> "${user.name}: ${user.email}" }
    )
    println(withEmail)

    // Testing createUser function
    println("\nCreating new users:")
    val amit = createUser("Amit", "amit@test.com", 30)
    val invalid1 = createUser("", "test@test.com", 25)  // blank name
    val invalid2 = createUser("Test User", "bad-email", 20)  // bad email
    val invalid3 = createUser("Baby", "baby@test.com", 5)  // too young

    // Testing filterByType
    println("\nTesting type filtering:")
    val mixedList: List<Any> = listOf("hello", 42, "world", 3.14, 100, "kotlin")
    println("Original: $mixedList")

    val strings = mixedList.filterByType<String>()
    println("Strings: $strings")

    val numbers = mixedList.filterByType<Int>()
    println("Numbers: $numbers")

    // Chaining operations
    println("\nChaining example:")
    val result = nirbhay.email
        ?.takeIf { it.isValidEmail() }
        ?.substringAfter("@")
        ?: "no domain"
    println("Email domain: $result")

    println("\n=== Done! ===")
}