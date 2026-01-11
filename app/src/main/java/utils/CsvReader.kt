package utils
import android.content.Context
import model.User
import java.io.BufferedReader
import java.io.InputStreamReader

object CsvReader {

    fun readUsers(context: Context, fileName: String): List<User> {
        val users = mutableListOf<User>()

        val input = context.assets.open(fileName)
        val reader = BufferedReader(InputStreamReader(input))
        reader.readLine() // skip header

        reader.forEachLine { line ->
            val row = line.split(",")

            if (row.size >= 10) {
                users.add(
                    User(
                        firstName = row[0],
                        lastName = row[1],
                        country = row[7],
                        city = row[9],
                        followers = row[5].toIntOrNull() ?: 0
                    )
                )
            }
        }
        return users
    }
}
