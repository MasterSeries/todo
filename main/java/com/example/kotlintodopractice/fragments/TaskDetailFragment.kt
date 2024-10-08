package com.example.kotlintodopractice.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlintodopractice.R
import com.example.kotlintodopractice.databinding.FragmentTaskDetailBinding
import com.example.kotlintodopractice.utils.adapter.TaskAdapter
import com.example.kotlintodopractice.utils.model.ToDoData
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TaskDetailFragment : Fragment() {
    private lateinit var binding: FragmentTaskDetailBinding
    private val database = Firebase.database.reference.child("Tasks")
    private var taskId: String? = null
    private var task: String? = null
    private lateinit var taskAdapter: TaskAdapter
    private val subTaskList = mutableListOf<ToDoData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            taskId = it.getString("taskId")
            task = it.getString("task")
        }

        binding.taskTextView.text = task ?: "No task found"

        taskAdapter = TaskAdapter(subTaskList)
        binding.subTaskRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.subTaskRecyclerView.adapter = taskAdapter

        loadSubTasks()

        binding.addSubTaskBtn.setOnClickListener {
            val subTaskText = binding.subTaskEditText.text.toString()
            if (subTaskText.isNotEmpty()) {
                val subTask = ToDoData(taskId = taskId ?: "", task = subTaskText)
                database.child(taskId!!).child("subTasks").push().setValue(subTask)
                    .addOnCompleteListener { taskResult ->
                        if (taskResult.isSuccessful) {
                            Toast.makeText(context, "Sub-task added successfully", Toast.LENGTH_SHORT).show()
                            binding.subTaskEditText.text?.clear()
                            subTaskList.add(subTask)
                            taskAdapter.notifyItemInserted(subTaskList.size - 1)
                        } else {
                            Toast.makeText(context, "Failed to add sub-task", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Sub-task cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadSubTasks() {
        database.child(taskId!!).child("subTasks").get().addOnSuccessListener { dataSnapshot ->
            for (subTaskSnapshot in dataSnapshot.children) {
                val subTask = subTaskSnapshot.getValue(String::class.java)
                if (subTask != null) {
                    subTaskList.add(ToDoData(taskId = subTaskSnapshot.key ?: "", task = subTask))
                }
            }
            taskAdapter.notifyDataSetChanged()
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to load sub-tasks", Toast.LENGTH_SHORT).show()
        }
    }
}
