package com.example.csvfilterapp
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import model.User

class UserAdapter(private var users: List<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    // Holds views for one list item
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.nameText)
        val location: TextView = view.findViewById(R.id.locationText)
        val followers: TextView = view.findViewById(R.id.followersText)
    }

    // Creates a new row when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    // Number of items
    override fun getItemCount(): Int {
        return users.size
    }

    // Binds data to each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]

        holder.name.text = "${user.firstName} ${user.lastName}"
        holder.location.text = "${user.city}, ${user.country}"
        holder.followers.text = "Followers: ${user.followers}"
    }

    // Updates list when filtering
    fun updateData(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}
