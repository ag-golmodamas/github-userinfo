
import GitHubClient
import Repository
import User

class UserRepository {
    private val cachedUsers = mutableMapOf<String, User>()
    private val gitHubService = GitHubClient.service
    
    fun getUserInfo(username: String): User? {
        // Check if user is already cached
        if (cachedUsers.containsKey(username)) {
            println("Retrieved user $username from cache.")
            return cachedUsers[username]
        }
        
        try {
            // Get user info
            val userResponse = gitHubService.getUser(username).execute()
            if (!userResponse.isSuccessful) {
                println("Error: ${userResponse.code()} - ${userResponse.message()}")
                return null
            }
            
            val user = userResponse.body() ?: return null
            
            // Get user repositories
            val reposResponse = gitHubService.getUserRepositories(username).execute()
            if (reposResponse.isSuccessful) {
                val repos = reposResponse.body() ?: emptyList()
                user.repositories = repos
            }
            
            // Cache the user
            cachedUsers[username] = user
            return user
        } catch (e: Exception) {
            println("Error retrieving user info: ${e.message}")
            return null
        }
    }
    
    fun getAllCachedUsers(): List<User> {
        return cachedUsers.values.toList()
    }
    
    fun findUserByUsername(username: String): User? {
        return cachedUsers[username]
    }
    
    fun findRepositoryByName(repoName: String): Pair<User, Repository>? {
        for (user in cachedUsers.values) {
            val repo = user.repositories.find { it.name.equals(repoName, ignoreCase = true) }
            if (repo != null) {
                return Pair(user, repo)
            }
        }
        return null
    }
} 