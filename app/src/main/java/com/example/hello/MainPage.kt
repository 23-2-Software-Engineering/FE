package com.example.hello

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.hello.api.Utils
import com.example.hello.databinding.ActivityMainBinding
import java.util.*

class MainPage : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var utils: Utils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        utils = application as Utils

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.courseCreate.setOnClickListener{
            popUp()
        }

        binding.courseSearch.setOnClickListener{
            val intent = Intent(this, CourseView::class.java)

            startActivity(intent)
        }

        binding.postSearch.setOnClickListener {
            val intent = Intent(this, PostSearchActivity::class.java)

            startActivity(intent)
        }

        binding.myPosts.setOnClickListener{
            val intent = Intent(this, ReadMyPostList::class.java)

            startActivity(intent)
        }

        binding.moveToLogout.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)

            startActivity(intent)
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    fun popUp(){
        val dialogView = LayoutInflater.from(this).inflate(R.layout.data_input, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("정보 입력")
        var popup = builder.show()

        val startDateEdit = dialogView.findViewById<TextView>(R.id.startDate)
        val endDateEdit = dialogView.findViewById<TextView>(R.id.endDate)
        val done = dialogView.findViewById<Button>(R.id.done)

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        var startDate:Calendar = Calendar.getInstance().apply { set(year, month, day) }
        var endDate:Calendar = Calendar.getInstance().apply { set(year, month, day) }

        startDateEdit.setOnClickListener{
            DatePickerDialog(this, { _, year, month, day ->
                startDate = Calendar.getInstance().apply { set(year, month, day) }
                startDateEdit.text =
                    year.toString() + "/" + (month + 1).toString() + "/" + day.toString()
            }, year, month, day).apply {
                datePicker.minDate = System.currentTimeMillis()
            }.show()
        }
        endDateEdit.setOnClickListener{
            DatePickerDialog(this, { _, year, month, day ->
                endDate = Calendar.getInstance().apply { set(year, month, day) }
                endDateEdit.text =
                    year.toString() + "/" + (month + 1).toString() + "/" + day.toString()
            }, year, month, day).apply {
                datePicker.minDate = startDate.timeInMillis
            }.show()
        }

        done.setOnClickListener{
            val intent = Intent(this, Course::class.java)
            val bundle = Bundle()

            val loc:String = dialogView.findViewById<EditText>(R.id.locInput).text.toString()

            if(loc==""){
                Toast.makeText(this, "장소를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else{
                utils.setLoc(loc)
                bundle.putSerializable("startDate", startDate)
                bundle.putSerializable("endDate", endDate)
                intent.putExtras(bundle)

                popup.dismiss()

                startActivity(intent)
            }
        }
    }
}