package com.example.kotlintodopractice.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.kotlintodopractice.R
import com.example.kotlintodopractice.utils.model.ToDoData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ToDoDialogFragment : DialogFragment() {

    private var listener: OnDialogNextBtnClickListener? = null
    private var toDoData: ToDoData? = null
    private lateinit var mAuth: FirebaseAuth

    fun setListener(listener: OnDialogNextBtnClickListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "DialogFragment"
        @JvmStatic
        fun newInstance(taskId: String, task: String) =
            ToDoDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("taskId", taskId)
                    putString("task", task)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_todo, container, false) // Use your actual dialog layout file here
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance() // Initialize Firebase Auth

        val todoEt: TextInputEditText = view.findViewById(R.id.todoEt) // Get TextInputEditText
        val todoClose: ImageView = view.findViewById(R.id.todoClose) // Get close button
        val todoNextBtn: ImageView = view.findViewById(R.id.todoNextBtn) // Get next button
        val accountOptionsBt: TextView = view.findViewById(R.id.accountOptionsBt) // Get account options button

        if (arguments != null) {
            toDoData = ToDoData(arguments?.getString("taskId").toString(), arguments?.getString("task").toString())
            todoEt.setText(toDoData?.task)
        }

        todoClose.setOnClickListener {
            dismiss()
        }

        todoNextBtn.setOnClickListener {
            val todoTask = todoEt.text.toString()
            if (todoTask.isNotEmpty()) {
                if (toDoData == null) {
                    listener?.saveTask(todoTask, todoEt)
                } else {
                    toDoData!!.task = todoTask
                    listener?.updateTask(toDoData!!, todoEt)
                }
            }
        }

        // Account Options Button Click Listener
        accountOptionsBt.setOnClickListener {
            showAccountOptionsDialog()
        }
    }

    private fun showAccountOptionsDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_account_options)

        val viewAccountButton: TextView = dialog.findViewById(R.id.viewAccountBtn)
        val signOutButton: TextView = dialog.findViewById(R.id.signOutBtn)

        viewAccountButton.setOnClickListener {
            // Add functionality to view account details
            dialog.dismiss()
        }

        signOutButton.setOnClickListener {
            mAuth.signOut() // Sign out from Firebase
            dismiss() // Close dialog
            // Optionally navigate to the sign-in page
        }

        dialog.show()
    }

    interface OnDialogNextBtnClickListener {
        fun saveTask(todoTask: String, todoEdit: TextInputEditText)
        fun updateTask(toDoData: ToDoData, todoEdit: TextInputEditText)
    }
}
