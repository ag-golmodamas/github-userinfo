
import User
import UserRepository
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

object CommandInput {
    private val reader = BufferedReader(InputStreamReader(System.`in`))
    // Return input int, null if invalid
    fun readInt(): Int? {
        val line = readLine()
        return try {
            line.toInt()
        } catch (e: NumberFormatException) {
            null
        }
    }
    // Read input string line
    fun readLine(): String {
        return reader.readLine() ?: ""
    }
}

class GitUserInfo(private val userRepository: UserRepository) {
    fun start() {
        println("---Github Command-Line user information---\n")
        var choice: String
        do {
            print("command- ")
            try {
                // Using our custom CommandInput utility instead of Scanner
                choice = CommandInput.readLine()?: ""
                when (choice) {
                    "help" -> help()
                    "userinfo" -> getUserInfo()
                    "storedusers" -> showStored()
                    "searchuser" -> searchUser()
                    "searchrep" -> searchRepo()
                    "exit" -> println("Exiting program...")
                    else -> println("Command not found. " +
                            "Type \"help\" to see available commands.\n")
                }
            } catch (e: Exception) {
                println("Error : ${e.message}")
                choice = "0"
            }
            println() // Empty line for better readability
        } while (choice != "5")
    }

    private fun help() {
        println()
        println("Command Guide -----------")
        println("userinfo    : Retrieve user information")
        println("storedusers : Display stored users")
        println("searchuser  : Search for a user")
        println("searchrep   : Search for a repository")
        println("exit        : exit the program")
        println("-----------------------")
    }

    private fun getUserInfo() {
        print("username: ")
        val username = CommandInput.readLine().trim()
        val user = userRepository.getUserInfo(username)
        if (user != null) {
            displayUserInfo(user)
        } else { println("ERROR: Failed to get info on $username")}
    }

    private fun showStored() {
        val users = userRepository.getAllCachedUsers()
        if (users.isEmpty()) {
            println("Nothing stored.")
            return
        }
        println("STORED USERS -------")
        users.forEach { user ->
            println("username: ${user.username}")
            println("followers: ${user.followers}")
            println("repos: ${user.repositories.size}")
            println("------------------")
        }
    }

    private fun searchUser() {
        print("username: ")
        val username = CommandInput.readLine().trim()
        val user = userRepository.findUserByUsername(username)
        if (user != null) {
            displayUserInfo(user)
        } else {
            println("Not found.")
        }
    }

    private fun searchRepo() {
        print("repository name: ")
        val repoName = CommandInput.readLine().trim()
        val result = userRepository.findRepositoryByName(repoName)
        if (result != null) {
            val (user, repo) = result
            println("\nREPOSITORY INFO --------")
            println("repository  : ${repo.name}")
            println("description : ${repo.description ?: "N/A"}")
            println("language    : ${repo.language ?: "N/A"}")
            println("link to repo -> ${repo.url}")
        } else {
            println("Not found.")
        }
    }

    private fun displayUserInfo(user: User) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val createdDate = try {
            dateFormat.parse(user.createdAt)
        } catch (e: Exception) {
            null
        }

        val formattedDate = if (createdDate != null) {
            SimpleDateFormat("MMMM dd, yyyy").format(createdDate)
        } else {
            user.createdAt
        }

        println("\nUSER INFO ---------")
        println("username     : ${user.username}")
        println("followers    : ${user.followers}")
        println("following    : ${user.following}")
        println("created on   : $formattedDate")

        if (user.repositories.isNotEmpty()) {
            println("\nPublic repositories:")
            user.repositories.forEachIndexed { index, repo ->
                println("|${index + 1}| ${repo.language ?: "N/A"} | ${repo.name}")
            }
        }
    }
} 