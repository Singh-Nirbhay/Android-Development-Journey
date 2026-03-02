//🏗️ BUILD CHALLENGE #3A (Part 1)
//Let's pause here and practice what you've learned so far!
//
//Create a Simple Fragment Navigation App:
//
//Requirements:
//Create 2 Fragments:
//
//HomeFragment — Shows "Home Screen" text and a button "Go to Profile"
//ProfileFragment — Shows "Profile Screen" text and displays a user name
//Create MainActivity:
//
//Loads HomeFragment initially
//Has a container (FrameLayout) for fragments
//In HomeFragment:
//
//Button click → Navigate to ProfileFragment
//Use replace() with addToBackStack()
//Pass user name "Nirbhay" as argument
//In ProfileFragment:
//
//Display the received user name
//Use factory method pattern (newInstance())
//Add lifecycle logging:
//
//Override onCreate, onCreateView, onViewCreated, onDestroyView in both Fragments
//Log with tag "FragmentLifecycle"
//Test:
//
//Open app → Should show HomeFragment
//Click button → Should show ProfileFragment with name
//Press back → Should return to HomeFragment
//Press back again → App should close
//You can use:
//
//Traditional XML layouts OR
//Compose inside Fragments (ComposeView)

