package com.example.coinkiller

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences

class MainActivity : AppCompatActivity(), GameClosing {
    private lateinit var coinLayout: LinearLayout
    private lateinit var startBtn: Button
    private lateinit var score: TextView
    private lateinit var mGameHome: GameHome
    private var selectedManType: Int = 0
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // Create SharedPreferences
        sharedPreferences = getSharedPreferences("TURBO_RACE", Context.MODE_PRIVATE)

        // Get start button id
        startBtn = findViewById(R.id.startBtn)

        // Get layout id
        coinLayout = findViewById(R.id.coinLayout)

        // Get score id
        score = findViewById(R.id.score)
        mGameHome = GameHome(this, this, selectedManType)

        // Create strat button work
        val highScore = sharedPreferences.getInt("high_score", 0)
        val scoreTextView: TextView = findViewById(R.id.score)
        scoreTextView.text = "High Score: $highScore"
        startBtn.setOnClickListener {

            startGame(selectedManType)
        }
    }

    // Handle man type selection
    fun selectManType(view: View) {
        findViewById<View>(R.id.manType1).alpha = 1.0f
        findViewById<View>(R.id.manType2).alpha = 1.0f
        findViewById<View>(R.id.manType3).alpha = 1.0f
        findViewById<View>(R.id.manType4).alpha = 1.0f


        view.alpha = 0.5f
        //Assign type number
        selectedManType = when (view.id) {
            R.id.manType1 -> 1
            R.id.manType2 -> 2
            R.id.manType3 -> 3
            R.id.manType4 -> 4
            else -> 0 // Default value
        }

        // enable start button visible
        startBtn.visibility = View.VISIBLE
    }


    // Create Start game function
    private fun startGame(manType: Int) {

        // Hide all human characters
        findViewById<View>(R.id.manType1).visibility = View.GONE
        findViewById<View>(R.id.manType2).visibility = View.GONE
        findViewById<View>(R.id.manType3).visibility = View.GONE
        findViewById<View>(R.id.manType4).visibility = View.GONE
        score.visibility = View.GONE

        // Create an new instance of GameView
        mGameHome = GameHome(this, this, manType)


        coinLayout.addView(mGameHome)

        // Hide start button
        startBtn.visibility = View.GONE
    }


    // create close game Function
    override fun closeGame(mScore: Int) {
        score.text = "Score : $mScore"
        coinLayout.removeView(mGameHome)

        // Start button and score display
        startBtn.visibility = View.VISIBLE
        score.visibility = View.VISIBLE
    }
}