//🏗️ BUILD CHALLENGE #12 (FINAL CHALLENGE!)
//Create a Complete Login System with Error Handling:
//
//Step 1: Create data classes:
//
//Kotlin
//
//data class User(val id: String, val name: String, val email: String)
//data class LoginRequest(val email: String, val password: String)
//Step 2: Create sealed class LoginState:
//
//    Idle
//Loading
//Success(user: User)
//Error(message: String)
//Step 3: Create AuthRepository:
//
//Function login(request: LoginRequest): Result<User> using runCatching
//Simulate validation:
//If email is blank → throw IllegalArgumentException("Email required")
//If password length < 6 → throw IllegalArgumentException("Password too short")
//If email is "fail@test.com" → throw Exception("Invalid credentials")
//Otherwise → return success with User
//Step 4: Create LoginViewModel:
//
//Has _state (MutableStateFlow) and state (StateFlow)
//Function login(email: String, password: String):
//Set Loading
//Call repository
//Use fold to handle success/failure
//Map specific exceptions to user-friendly messages
//Step 5: In main():
//
//Test 4 scenarios:
//Empty email
//Short password
//Invalid credentials (fail@test.com)
//Successful login (any valid email/password)
//Print state after each scenario


// =====================
// Data Classes
// =====================
data class User(val id: String, val name: String, val email: String)

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val user: User) : UiState()
    data class Error(val message: String) : UiState()
}

// =====================
// Repository
// =====================
class UserRepository(private val api: ApiService) {

    suspend fun getUser(id: String): Result<User> = runCatching {
        api.fetchUser(id)
    }.onFailure { error ->
        Logger.error("Failed to fetch user $id", error)
    }

    suspend fun updateUser(user: User): Result<User> = runCatching {
        api.updateUser(user)
    }.onFailure { error ->
        Logger.error("Failed to update user ${user.id}", error)
    }
}

// =====================
// ViewModel
// =====================
class ProfileViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading

            repository.getUser(userId).fold(
                onSuccess = { user ->
                    _state.value = UiState.Success(user)
                },
                onFailure = { error ->
                    _state.value = UiState.Error(
                        when (error) {
                            is IOException -> "No internet connection"
                            is HttpException -> "Server error (${error.code()})"
                            else -> error.message ?: "Something went wrong"
                        }
                    )
                }
            )
        }
    }

    fun updateProfile(name: String, email: String) {
        val currentUser = (state.value as? UiState.Success)?.user ?: return
        val updatedUser = currentUser.copy(name = name, email = email)

        viewModelScope.launch {
            _state.value = UiState.Loading

            repository.updateUser(updatedUser)
                .onSuccess { user ->
                    _state.value = UiState.Success(user)
                }
                .onFailure { error ->
                    _state.value = UiState.Error(error.message ?: "Update failed")
                }
        }
    }
}

// =====================
// UI (Composable)
// =====================
@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val state by viewModel.state.collectAsState()

    when (val currentState = state) {
        is UiState.Idle -> {
            // Initial state
        }
        is UiState.Loading -> {
            CircularProgressIndicator()
        }
        is UiState.Success -> {
            ProfileContent(
                user = currentState.user,
                onUpdateClick = { name, email ->
                    viewModel.updateProfile(name, email)
                }
            )
        }
        is UiState.Error -> {
            ErrorView(
                message = currentState.message,
                onRetryClick = { viewModel.loadProfile(userId) }
            )
        }
    }
}