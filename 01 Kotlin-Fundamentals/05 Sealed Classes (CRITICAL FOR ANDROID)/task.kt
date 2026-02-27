//🏗️ BUILD CHALLENGE #5
//Create a Login Screen State System:
//
//Step 1: Create a sealed class LoginState with:
//
//object Idle — Initial state, nothing happening
//object Loading — Login in progress
//data class Success — Login successful, contains userId: String and userName: String
//data class Error — Login failed, contains message: String
//Step 2: Create a sealed class LoginEvent with:
//
//data class OnEmailChanged — contains email: String
//data class OnPasswordChanged — contains password: String
//object OnLoginClicked — user clicked login button
//object OnForgotPasswordClicked — user clicked forgot password
//Step 3: Create a class LoginViewModel with:
//
//Private _state variable of type LoginState (start with Idle)
//Public state getter
//Function handleEvent(event: LoginEvent) that:
//When OnLoginClicked → sets state to Loading, then (simulate) sets to Success with dummy data
//When OnForgotPasswordClicked → prints "Navigate to forgot password"
//Other events → just print what changed
//Step 4: Create a function renderState(state: LoginState) that:
//
//Uses when to print appropriate message for each state
//For Success → print "Welcome, {userName}!"
//For Error → print "Error: {message}"
//For Loading → print "Please wait..."
//For Idle → print "Please enter credentials"
//Step 5: In main():
//
//Create LoginViewModel
//Render initial state
//Handle OnEmailChanged event
//Handle OnLoginClicked event
//Render final state


// ----------------------------
// STEP 1 — LoginState
// ----------------------------
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val userId: String, val userName: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

// ----------------------------
// STEP 2 — LoginEvent
// ----------------------------
sealed class LoginEvent {
    data class OnEmailChanged(val email: String) : LoginEvent()
    data class OnPasswordChanged(val password: String) : LoginEvent()
    object OnLoginClicked : LoginEvent()
    object OnForgotPasswordClicked : LoginEvent()
}

// ----------------------------
// STEP 3 — LoginViewModel
// ----------------------------
class LoginViewModel {

    private var _state: LoginState = LoginState.Idle
    val state: LoginState get() = _state

    // Form data
    private var email: String = ""
    private var password: String = ""

    fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChanged -> {
                email = event.email
                println("📧 Email: $email")
            }

            is LoginEvent.OnPasswordChanged -> {
                password = event.password
                println("🔒 Password updated")
            }

            LoginEvent.OnLoginClicked -> {
                println("\n🔄 Login started...")
                _state = LoginState.Loading

                // Simulate validation
                when {
                    email.isBlank() -> {
                        _state = LoginState.Error("Email cannot be empty")
                    }
                    password.isBlank() -> {
                        _state = LoginState.Error("Password cannot be empty")
                    }
                    password.length < 6 -> {
                        _state = LoginState.Error("Password must be at least 6 characters")
                    }
                    else -> {
                        // Simulate successful login
                        _state = LoginState.Success(
                            userId = "U${System.currentTimeMillis() % 1000}",
                            userName = email.substringBefore("@")
                        )
                    }
                }
            }

            LoginEvent.OnForgotPasswordClicked -> {
                println("🔗 Navigate to forgot password screen")
            }
        }
    }
}

// ----------------------------
// STEP 4 — renderState
// ----------------------------
fun renderState(state: LoginState) {
    println("\n━━━━━━━━━━━━━━━━━━━━━━━━━")
    when (state) {
        LoginState.Idle -> {
            println("📝 Please enter credentials")
        }

        LoginState.Loading -> {
            println("⏳ Please wait...")
        }

        is LoginState.Success -> {
            println("✅ Welcome, ${state.userName}!")
            println("   User ID: ${state.userId}")
        }

        is LoginState.Error -> {
            println("❌ Error: ${state.message}")
        }
    }
    println("━━━━━━━━━━━━━━━━━━━━━━━━━\n")
}

// ----------------------------
// STEP 5 — main()
// ----------------------------
fun main() {
    val viewModel = LoginViewModel()

    println("=== LOGIN SCREEN ===\n")

    // Initial state
    renderState(viewModel.state)

    // User interaction flow
    viewModel.handleEvent(LoginEvent.OnEmailChanged("nirbhay@example.com"))
    viewModel.handleEvent(LoginEvent.OnPasswordChanged("pass123"))

    // Try login
    viewModel.handleEvent(LoginEvent.OnLoginClicked)
    renderState(viewModel.state)

    // Try another scenario
    println("\n=== SCENARIO 2: Empty Password ===\n")
    val viewModel2 = LoginViewModel()
    viewModel2.handleEvent(LoginEvent.OnEmailChanged("test@example.com"))
    viewModel2.handleEvent(LoginEvent.OnLoginClicked)
    renderState(viewModel2.state)
}