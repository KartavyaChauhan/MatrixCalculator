package com.example.matrixcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.content.Intent

class MainActivity : AppCompatActivity() {

    external fun addMatrices(matrix1: FloatArray, matrix2: FloatArray, rows1: Int, cols1: Int, rows2: Int, cols2: Int): FloatArray
    external fun subtractMatrices(matrix1: FloatArray, matrix2: FloatArray, rows1: Int, cols1: Int, rows2: Int, cols2: Int): FloatArray
    external fun multiplyMatrices(matrix1: FloatArray, matrix2: FloatArray, rows1: Int, cols1: Int, rows2: Int, cols2: Int): FloatArray
    external fun divideMatrices(matrix1: FloatArray, matrix2: FloatArray, rows1: Int, cols1: Int, rows2: Int, cols2: Int): FloatArray

    companion object {
        init {
            System.loadLibrary("matrixcalculator")
        }
    }

    private lateinit var matrix1Dims: EditText
    private lateinit var matrix2Dims: EditText
    private lateinit var matrix1Input: EditText
    private lateinit var matrix2Input: EditText
    private lateinit var resultText: TextView

    private fun parseMatrix(input: String, rows: Int, cols: Int): FloatArray {
        val values = mutableListOf<Float>()
        val rowsInput = input.split(";")
        if (rowsInput.size != rows) {
            throw IllegalArgumentException("Expected $rows rows, got ${rowsInput.size}")
        }
        for (row in rowsInput) {
            val nums = row.split(",").map { it.trim() }
            if (nums.size != cols) {
                throw IllegalArgumentException("Expected $cols columns, got ${nums.size}")
            }
            nums.forEach {
                if (it.isNotEmpty()) values.add(it.toFloatOrNull() ?: throw NumberFormatException("Invalid number: $it"))
            }
        }
        if (values.size != rows * cols) {
            throw IllegalArgumentException("Matrix size mismatch: expected ${rows * cols} elements, got ${values.size}")
        }
        return values.toFloatArray()
    }

    private fun getDimensions(input: String): Pair<Int, Int> {
        val dims = input.trim().split(" ")
        if (dims.size != 2 || dims.any { it.isEmpty() }) {
            throw IllegalArgumentException("Enter dimensions as 'rows cols' (e.g., 2 2)")
        }
        val rows = dims[0].toIntOrNull() ?: throw IllegalArgumentException("Invalid row dimension")
        val cols = dims[1].toIntOrNull() ?: throw IllegalArgumentException("Invalid column dimension")
        if (rows <= 0 || cols <= 0) {
            throw IllegalArgumentException("Dimensions must be positive")
        }
        return Pair(rows, cols)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Remove the title bar
        setContentView(R.layout.activity_main)

        matrix1Dims = findViewById(R.id.matrix1_dims)
        matrix2Dims = findViewById(R.id.matrix2_dims)
        matrix1Input = findViewById(R.id.matrix1_input)
        matrix2Input = findViewById(R.id.matrix2_input)
        resultText = findViewById(R.id.resultText)

        findViewById<Button>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish() // Close this Activity
        }

        findViewById<Button>(R.id.addButton).setOnClickListener {
            performOperation("add")
        }
        findViewById<Button>(R.id.subtractButton).setOnClickListener {
            performOperation("subtract")
        }
        findViewById<Button>(R.id.multiplyButton).setOnClickListener {
            performOperation("multiply")
        }
        findViewById<Button>(R.id.divideButton).setOnClickListener {
            performOperation("divide")
        }
    }

    private fun performOperation(operation: String) {
        try {
            val dim1Str = matrix1Dims.text.toString()
            val dim2Str = matrix2Dims.text.toString()
            val matrix1Str = matrix1Input.text.toString()
            val matrix2Str = matrix2Input.text.toString()

            val (rows1, cols1) = if (dim1Str.isNotEmpty()) getDimensions(dim1Str) else throw IllegalArgumentException("Enter Matrix 1 dimensions")
            val (rows2, cols2) = if (dim2Str.isNotEmpty()) getDimensions(dim2Str) else throw IllegalArgumentException("Enter Matrix 2 dimensions")

            val matrix1 = parseMatrix(matrix1Str, rows1, cols1)
            val matrix2 = parseMatrix(matrix2Str, rows2, cols2)

            val result = when (operation) {
                "add" -> if (rows1 == rows2 && cols1 == cols2) addMatrices(matrix1, matrix2, rows1, cols1, rows2, cols2) else floatArrayOf()
                "subtract" -> if (rows1 == rows2 && cols1 == cols2) subtractMatrices(matrix1, matrix2, rows1, cols1, rows2, cols2) else floatArrayOf()
                "multiply" -> if (cols1 == rows2) multiplyMatrices(matrix1, matrix2, rows1, cols1, rows2, cols2) else floatArrayOf()
                "divide" -> if (cols1 == rows2 && rows2 == cols2) divideMatrices(matrix1, matrix2, rows1, cols1, rows2, cols2) else floatArrayOf()
                else -> floatArrayOf()
            }

            if (result.isEmpty()) {
                resultText.text = "Invalid matrix dimensions for $operation"
            } else {
                displayResult(result, if (operation == "multiply") rows1 else rows1, if (operation == "multiply") cols2 else cols1)
            }
        } catch (e: Exception) {
            resultText.text = "Error: ${e.message}"
        }
    }

    private fun displayResult(matrix: FloatArray, rows: Int, cols: Int) {
        val builder = StringBuilder()
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                builder.append(matrix[i * cols + j]).append(" ")
            }
            builder.append("\n")
        }
        resultText.text = builder.toString()
    }
}