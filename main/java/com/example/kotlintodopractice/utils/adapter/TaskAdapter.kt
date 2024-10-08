package com.example.kotlintodopractice.utils.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlintodopractice.databinding.EachTodoItemBinding
import com.example.kotlintodopractice.utils.model.ToDoData

class TaskAdapter(private val list: MutableList<ToDoData>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val TAG = "TaskAdapter"
    private var listener: TaskAdapterInterface? = null

    fun setListener(listener: TaskAdapterInterface) {
        this.listener = listener
    }

    class TaskViewHolder(val binding: EachTodoItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = EachTodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = list[position] // Get the current task
        with(holder) {
            binding.todoTask.text = currentTask.task // Set the task text
            Log.d(TAG, "onBindViewHolder: ${currentTask.task}") // Log for debugging

            // Handle click events
            binding.root.setOnClickListener {
                listener?.onItemClicked(currentTask)  // Notify listener of item click
            }

            binding.editTask.setOnClickListener {
                listener?.onEditItemClicked(currentTask, position) // Notify listener to edit item
            }

            binding.deleteTask.setOnClickListener {
                listener?.onDeleteItemClicked(currentTask, position) // Notify listener to delete item
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size // Return the number of items
    }

    interface TaskAdapterInterface {
        fun onItemClicked(toDoData: ToDoData) // Notify when an item is clicked
        fun onDeleteItemClicked(toDoData: ToDoData, position: Int) // Notify when an item is deleted
        fun onEditItemClicked(toDoData: ToDoData, position: Int) // Notify when an item is edited
    }

    // Optional: Method to add a new sub-task
    fun addSubTask(subTask: ToDoData) {
        list.add(subTask)
        notifyItemInserted(list.size - 1) // Notify the adapter about the new item
    }

    // Optional: Method to clear the list (if needed)
    fun clearTasks() {
        list.clear()
        notifyDataSetChanged() // Notify that the data set has changed
    }
}
