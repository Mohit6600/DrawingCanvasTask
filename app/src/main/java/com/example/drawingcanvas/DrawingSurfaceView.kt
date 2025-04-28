package com.example.drawingcanvas

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.atan2

class DrawingSurfaceView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private lateinit var drawingBitmap: Bitmap
    private lateinit var drawingCanvas: Canvas

    private var paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }

    private var lastX = 0f
    private var lastY = 0f
    private var isDrawing = false

    private var imageBitmap: Bitmap? = null
    private val imageMatrix = Matrix()

    private var scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    private var scaleFactor = 1f
    private var translationX = 0f
    private var translationY = 0f
    private var previousDistance = 0f
    private var previousRotationAngle = 0f

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (!::drawingBitmap.isInitialized) {
            drawingBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            drawingCanvas = Canvas(drawingBitmap)
            drawingCanvas.drawColor(Color.WHITE)
        }
        drawCanvas()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    private fun drawCanvas() {
        val canvas = holder.lockCanvas()
        canvas?.let {
            it.drawColor(Color.WHITE)
            it.drawBitmap(drawingBitmap, 0f, 0f, null)

            imageBitmap?.let { bmp ->
                it.drawBitmap(bmp, imageMatrix, null)
            }

            holder.unlockCanvasAndPost(it)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)

        if (imageBitmap != null) {
            when (event.pointerCount) {
                1 -> {
                    when (event.action) {
                        MotionEvent.ACTION_MOVE -> {
                            val dx = event.x - lastX
                            val dy = event.y - lastY
                            imageMatrix.postTranslate(dx, dy)
                            translationX += dx
                            translationY += dy
                        }
                    }
                }
                2 -> {
                    if (event.actionMasked == MotionEvent.ACTION_MOVE) {
                        val dx = event.getX(1) - event.getX(0)
                        val dy = event.getY(1) - event.getY(0)
                        val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

                        if (previousDistance == 0f) {
                            previousDistance = distance
                        }

                        val scaleFactor = distance / previousDistance
                        this.scaleFactor *= scaleFactor

                        imageMatrix.postScale(scaleFactor, scaleFactor, width / 2f, height / 2f)

                        val angle = calculateRotationAngle(event)

                        if (Math.abs(angle - previousRotationAngle) > 5f) {
                            imageMatrix.postRotate(angle - previousRotationAngle, width / 2f, height / 2f)
                            previousRotationAngle = angle
                        }

                        previousDistance = distance
                    }
                }
            }
        } else {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = event.x
                    lastY = event.y
                    isDrawing = true
                }

                MotionEvent.ACTION_MOVE -> {
                    if (isDrawing) {
                        drawingCanvas.drawLine(lastX, lastY, event.x, event.y, paint)
                        lastX = event.x
                        lastY = event.y
                    }
                }

                MotionEvent.ACTION_UP -> {
                    isDrawing = false
                }
            }
        }

        lastX = event.x
        lastY = event.y

        drawCanvas()
        return true
    }

    private fun calculateRotationAngle(event: MotionEvent): Float {
        val dx = event.getX(1) - event.getX(0)
        val dy = event.getY(1) - event.getY(0)
        return Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
    }

    fun insertImage() {
        imageBitmap?.let { bmp ->
            drawingCanvas.drawBitmap(bmp, imageMatrix, null)
            imageBitmap = null

            post {
                drawCanvas()
            }
        }
    }

    fun setImageUri(imageUri: Uri) {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        imageBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
        imageMatrix.reset()
        drawCanvas()
    }

    fun clearCanvas() {
        drawingCanvas.drawColor(Color.WHITE)
        imageBitmap = null
        imageMatrix.reset()
        post {
            drawCanvas()
        }
    }


    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scale = detector.scaleFactor
            scaleFactor *= scale
            imageMatrix.postScale(scale, scale, detector.focusX, detector.focusY)
            return true
        }
    }
}
