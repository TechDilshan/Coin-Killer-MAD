package com.example.coinkiller

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import android.os.CountDownTimer
import android.media.MediaPlayer

class GameHome(var c: Context, var gameClosing: GameClosing, var manType: Int) : View(c) {
    private var myPaint: Paint? = null
    private var speed = 1
    private var time = 0
    private var score = 0
    private var myManPosition = 0
    private val otherCoins = ArrayList<HashMap<String, Any>>()

    var viewWidth = 0
    var viewHeight = 0
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var roadBitmap: Bitmap
    private lateinit var manDrawable: Drawable
    private lateinit var countdownTimer: CountDownTimer
    private var countdownSeconds = 20
    private var mediaPlayer: MediaPlayer? = null

    init {
        myPaint = Paint()  // Initialize Paint

        // Get SharedPreferences data
        sharedPreferences = c.getSharedPreferences("TURBO_RACE", Context.MODE_PRIVATE)

        // Create MediaPlayer and get music file
        mediaPlayer = MediaPlayer.create(context, R.raw.music)
        mediaPlayer?.isLooping = true

        // Get selected man type id and set
        manDrawable = when (manType) {
            1 -> resources.getDrawable(R.drawable.man1back, null)
            2 -> resources.getDrawable(R.drawable.man2back, null)
            3 -> resources.getDrawable(R.drawable.man3back, null)
            4 -> resources.getDrawable(R.drawable.man4back, null)
            else -> resources.getDrawable(R.drawable.man1back, null) // Default
        }

        roadBitmap = BitmapFactory.decodeResource(resources, R.drawable.coinroad)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        viewWidth = this.measuredWidth
        viewHeight = this.measuredHeight
        startMusic()
        if (!::countdownTimer.isInitialized || countdownTimer == null) {
            startCountdownTimer()
        }

        val highScore = sharedPreferences.getInt("high_score", 0)

        canvas.drawBitmap(roadBitmap, null, Rect(0, 0, viewWidth, viewHeight), null)

        // Generate coin and agest speed
        if (time % 700 < 10 + speed) {
            val map = HashMap<String, Any>()
            map["lane"] = (0..2).random()
            map["startTime"] = time
            otherCoins.add(map)
        }

        time = time + 10 + speed
        val manWidth = viewWidth / 5
        val manHeight = manWidth + 10
        myPaint!!.style = Paint.Style.FILL

        val newManHeight = manHeight + 200
        manDrawable.setBounds(
            myManPosition * viewWidth / 3 + viewWidth / 15 + 25,
            viewHeight - 2 - newManHeight,
            myManPosition * viewWidth / 3 + viewWidth / 15 + manWidth - 25,
            viewHeight - 2
        )
        manDrawable.draw(canvas!!)
        myPaint!!.color = Color.GREEN

        for (i in otherCoins.indices) {
            try {
                val manX = otherCoins[i]["lane"] as Int * viewWidth / 3 + viewWidth / 15
                var manY = time - otherCoins[i]["startTime"] as Int
                var d2 = resources.getDrawable(R.drawable.coin, null)

                val coinWidthIncrease = 50
                d2.setBounds(
                    manX + 25 - (coinWidthIncrease / 2),
                    manY - manHeight,
                    manX + manWidth - 25 + (coinWidthIncrease / 2),
                    manY
                )


                d2.draw(canvas)


                if (otherCoins[i]["lane"] as Int == myManPosition && manY > viewHeight - 2 - manHeight && manY < viewHeight - 2) {

                    score++

                    otherCoins.removeAt(i)
                }

                if (manY > viewHeight + manHeight) {
                    otherCoins.removeAt(i)
                    speed = 1 + Math.abs(score / 8)

                    //Update High Score
                    if (score > highScore) {
                        with(sharedPreferences.edit()) {
                            putInt("high_score", score)
                            apply()
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }



        val textBgPaint = Paint().apply {
            color = Color.BLACK
        }
        val textBgRect = Rect(0, 10, 300, 180)
        canvas.drawRect(textBgRect, textBgPaint)

        myPaint!!.color = Color.WHITE
        myPaint!!.textSize = 60f
        canvas.drawText("Time: $countdownSeconds", 20f, 150f, myPaint!!)

        val textBgRect2 = Rect(280, 10, 1000, 180)
        canvas.drawRect(textBgRect2, textBgPaint)

        canvas.drawText("Score : $score", 300f, 150f, myPaint!!)
        canvas.drawText("High Score : $highScore", 80f, 50f, myPaint!!)


        invalidate()
    }

    // start the countdown timer
    private fun startCountdownTimer() {
        countdownTimer = object : CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownSeconds = (millisUntilFinished / 1000).toInt()
            }

            // When countdown finished, close the game
            override fun onFinish() {
                gameClosing.closeGame(score)
            }
        }
        countdownTimer.start()
    }

    fun startMusic() {
        mediaPlayer?.start()
    }

    // music stop function
    fun stopMusic() {
        mediaPlayer?.stop()
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    //Create moving part
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                val x1 = event.x
                if (x1 < viewWidth / 2) {
                    if (myManPosition > 0) {
                        myManPosition--
                    }
                }
                if (x1 > viewWidth / 2) {
                    if (myManPosition < 2) {
                        myManPosition++
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return true
    }
}