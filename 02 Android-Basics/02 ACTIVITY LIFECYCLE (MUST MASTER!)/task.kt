class MainActivity : ComponentActivity() {
    companion object {
        const val COUNTER_KEY = "counter_key"
    }

    private var counter by mutableStateOf(0)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LifecycleDemo", "Method Name -> onCreate , time -> ${System.currentTimeMillis()}")
        if (savedInstanceState != null) {
            counter = savedInstanceState.getInt(COUNTER_KEY, 0)
        }
        counter++
        Log.d("LifecycleDemo", "Counter Value -> $counter")

        enableEdgeToEdge()
        setContent {
            AndroidDevJourneyTheme {

                MainScreen(counter, onIncrement = {
                    counter++
                    Log.d("LifecycleDemo", "Counter Value -> $counter")

                })
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("LifecycleDemo", "Method Name -> onStart , time -> ${System.currentTimeMillis()}")
    }

    override fun onResume() {
        super.onResume()
        Log.d(
            "LifecycleDemo",
            "Method Name -> onResume , counter value onResume -> $counter , time -> ${System.currentTimeMillis()}"
        )

    }

    override fun onPause() {
        super.onPause()
        Log.d("LifecycleDemo", "Method Name -> onPause , time -> ${System.currentTimeMillis()}")
    }

    override fun onStop() {
        super.onStop()
        Log.d("LifecycleDemo", "Method Name -> onStop , time -> ${System.currentTimeMillis()}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LifecycleDemo", "Method Name -> onDestroy , time -> ${System.currentTimeMillis()}")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("LifecycleDemo", "Method Name -> onRestart , time -> ${System.currentTimeMillis()}")

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(
            "LifecycleDemo",
            "Method Name -> onSaveInstanceState,counterValue-> $counter , time -> ${System.currentTimeMillis()}"
        )
        outState.putInt(COUNTER_KEY, counter)
    }


}

@Composable
fun MainScreen(counter: Int, onIncrement: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(text = "You have pushed the button this many times $counter")
        Button(onClick = { onIncrement() }) {
            Text(text = "Increment")
        }
    }
}

