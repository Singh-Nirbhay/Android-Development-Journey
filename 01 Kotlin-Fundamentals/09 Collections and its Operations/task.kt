//🏗️ BUILD CHALLENGE #9
//Create a Student Grade Processor:
//
//Step 1: Create data class Student:
//
//    id: String
//name: String
//score: Int
//subject: String
//Step 2: Create a list of 8 students:
//
//Mix of different subjects (Math, Science, English)
//Mix of scores (some above 80, some below)
//Step 3: Using collection operations, find:
//
//All students who scored above 80
//Names of students who scored above 80 (just names)
//First student who failed (score < 40)
//Are ALL students passing (score >= 40)?
//Count of students in "Math" subject
//Students sorted by score (highest first)
//Top 3 students by score
//Unique subjects in the list
//Step 4: Print results with clear labels.

data class Student(
    val id: String,
    val name: String,
    val score: Int,
    val subject: String
)

fun main() {
    val students = listOf(
        Student("101", "Hinata", 90, "Math"),
        Student("102", "Kageyama", 75, "Science"),
        Student("103", "Naruto", 35, "English"),
        Student("104", "Sasuke", 82, "Math"),
        Student("105", "Sakura", 60, "Science"),
        Student("106", "Itachi", 95, "English"),
        Student("107", "Rock Lee", 38, "Math"),
        Student("108", "Gaara", 88, "Science")
    )

    // 1. Above 80
    println("=== Students Scored Above 80 ===")
    students.filter { it.score > 80 }
        .forEach { println("  ${it.name} — ${it.score} (${it.subject})") }

    // 2. Names above 80
    println("\n=== Names Above 80 ===")
    val namesAbove80 = students
        .filter { it.score > 80 }
        .map { it.name }
    println("  $namesAbove80")

    // 3. First failure
    println("\n=== First Failure ===")
    students.find { it.score < 40 }
        ?.let { println("  ${it.name} failed with ${it.score}") }
        ?: println("  No failures!")

    // 4. All passing?
    println("\n=== All Passing? ===")
    val allPassing = students.all { it.score >= 40 }
    println("  $allPassing")

    // 5. Math count
    println("\n=== Math Students ===")
    println("  Count: ${students.count { it.subject == "Math" }}")

    // 6 & 7. Sorted + Top 3
    println("\n=== Top 3 Students ===")
    students.sortedByDescending { it.score }
        .take(3)
        .forEachIndexed { index, student ->
            println("  ${index + 1}. ${student.name} — ${student.score}")
        }

    // 8. Unique subjects
    println("\n=== Subjects ===")
    println("  ${students.map { it.subject }.distinct()}")
}