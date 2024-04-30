package com.example.game

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import java.util.*

class GameView(context: Context) : View(context) {
    private val background: Bitmap
    private val ground: Bitmap
    private val tank: Bitmap
    private val rectBackground: Rect
    private val rectGround: Rect
    private val handler: Handler
    private val runnable: Runnable
    private val textPaint = Paint()
    private val healthPaint = Paint()
    private val TEXT_SIZE = 120f
    var points = 0
    var life = 3
    private val random: Random
    var tankX = 0f
    var tankY = 0f
    private var oldX = 0f
    private var oldtankX = 0f
    private val spikes: ArrayList<Spike>
    private val explosions: ArrayList<Explosion>

    private val UPDATE_MILLIS = 30

    companion object {
        var dWidth = 0
        var dHeight = 0
    }

    init {
//        this.context = context
        background = BitmapFactory.decodeResource(resources, R.drawable.background)
        ground = BitmapFactory.decodeResource(resources, R.drawable.ground)
        tank = BitmapFactory.decodeResource(resources, R.drawable.tank)
        val display = (context as Activity).windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        dWidth = size.x
        dHeight = size.y
        rectBackground = Rect(0, 0, dWidth, dHeight)
        rectGround = Rect(0, dHeight - ground.height, dWidth, dHeight)
        handler = Handler()
        runnable = Runnable { invalidate() }
        textPaint.color = Color.rgb(255, 165, 0)
        textPaint.textSize = TEXT_SIZE
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.typeface = ResourcesCompat.getFont(context, R.font.kenney_blocks)
        healthPaint.color = Color.GREEN
        random = Random()
        tankX = (dWidth / 2 - tank.width / 2).toFloat()
        tankY = (dHeight - ground.height - tank.height).toFloat()
        spikes = ArrayList()
        explosions = ArrayList()
        for (i in 0..2) {
            val spike = Spike(context)
            spikes.add(spike)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(background, null, rectBackground, null)
        canvas.drawBitmap(ground, null, rectGround, null)
        canvas.drawBitmap(tank, tankX, tankY, null)
        for (i in spikes.indices) {
            canvas.drawBitmap(spikes[i].getSpike(spikes[i].spikeFrame)!!, spikes[i].spikeX.toFloat(), spikes[i].spikeY.toFloat(), null)
            spikes[i].spikeFrame++
            if (spikes[i].spikeFrame > 2) {
                spikes[i].spikeFrame = 0
            }
            spikes[i].spikeY += spikes[i].spikeVelocity
            if (spikes[i].spikeY + spikes[i].getSpikeHeight() >= dHeight - ground.height) {
                points += 10
                val explosion = Explosion(context)
                explosion.explosionX = spikes[i].spikeX
                explosion.explosionY = spikes[i].spikeY
                explosions.add(explosion)
                spikes[i].resetPosition()
            }
        }
        for (i in spikes.indices) {
            if (spikes[i].spikeX + spikes[i].getSpikeWidth() >= tankX
                && spikes[i].spikeX <= tankX + tank.width
                && spikes[i].spikeY + spikes[i].getSpikeWidth() >= tankY
                && spikes[i].spikeY + spikes[i].getSpikeWidth() <= tankY + tank.height
            ) {
                life--
                spikes[i].resetPosition()
                if (life == 0) {
                    val intent = Intent(context, GameOver::class.java)
                    intent.putExtra("points", points)
                    context.startActivity(intent)
                    (context as Activity).finish()
                }
            }
        }
        for (i in explosions.indices) {
            canvas.drawBitmap(explosions[i].getExplosion(explosions[i].explosionFrame)!!, explosions[i].explosionX.toFloat(), explosions[i].explosionY.toFloat(), null)
            explosions[i].explosionFrame++
            if (explosions[i].explosionFrame > 3) {
                explosions.removeAt(i)
            }
        }
        if (life == 2) {
            healthPaint.color = Color.YELLOW
        } else if (life == 1) {
            healthPaint.color = Color.RED
        }
        canvas.drawRect((dWidth - 200).toFloat(), 30f, (dWidth - 200 + 60 * life).toFloat(), 80f, healthPaint)
        canvas.drawText("" + points, 20f, TEXT_SIZE, textPaint)
        handler.postDelayed(runnable, UPDATE_MILLIS.toLong())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y
        if (touchY >= tankY) {
            val action = event.action
            if (action == MotionEvent.ACTION_DOWN) {
                oldX = event.x
                oldtankX = tankX
            }
            if (action == MotionEvent.ACTION_MOVE) {
                val shift = oldX - touchX
                val newtankX = oldtankX - shift
                tankX = when {
                    newtankX <= 0 -> 0f
                    newtankX >= dWidth - tank.width -> (dWidth - tank.width).toFloat()
                    else -> newtankX
                }
            }
        }
        return true
    }
}
