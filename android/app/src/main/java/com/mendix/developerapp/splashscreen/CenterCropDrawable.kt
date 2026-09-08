package com.mendix.developerapp.splashscreen

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import org.xmlpull.v1.XmlPullParser

/**
 * Draws its `android:src` bitmap scaled uniformly to cover the bounds and centered, the
 * equivalent of [android.widget.ImageView.ScaleType.CENTER_CROP].
 *
 * A plain `<bitmap>` cannot do this: its gravity either scales both axes independently
 * (`fill`, which distorts the image) or does not scale at all (`center`, which leaves
 * bars on screens larger than the bitmap). We need it for the splash window background,
 * where the logo is baked into the artwork and must keep its aspect ratio on every screen.
 *
 * Inflated by class name from `res/drawable/bootsplash.xml`, so it needs a public no-arg
 * constructor and a ProGuard keep rule.
 */
class CenterCropDrawable : Drawable() {

    private val paint = Paint(Paint.FILTER_BITMAP_FLAG)
    private val matrix = Matrix()
    private var bitmap: Bitmap? = null

    override fun inflate(
        r: Resources,
        parser: XmlPullParser,
        attrs: AttributeSet,
        theme: Resources.Theme?
    ) {
        super.inflate(r, parser, attrs, theme)

        val attributes = intArrayOf(android.R.attr.src)
        val a = theme?.obtainStyledAttributes(attrs, attributes, 0, 0)
            ?: r.obtainAttributes(attrs, attributes)

        val srcId = try {
            a.getResourceId(0, 0)
        } finally {
            a.recycle()
        }

        require(srcId != 0) { "CenterCropDrawable requires an android:src attribute" }

        // Decode at the authored pixel size instead of letting the framework upscale for
        // the device density: the artwork is only shipped up to xhdpi, and we do our own
        // scaling anyway, so this is both cheaper and sharper.
        val options = BitmapFactory.Options().apply { inScaled = false }
        bitmap = BitmapFactory.decodeResource(r, srcId, options)

        updateMatrix(bounds)
    }

    override fun onBoundsChange(bounds: Rect) {
        updateMatrix(bounds)
    }

    private fun updateMatrix(bounds: Rect) {
        val bitmap = bitmap ?: return
        if (bounds.isEmpty || bitmap.width == 0 || bitmap.height == 0) return

        val scale = maxOf(
            bounds.width() / bitmap.width.toFloat(),
            bounds.height() / bitmap.height.toFloat()
        )

        matrix.setScale(scale, scale)
        matrix.postTranslate(
            bounds.left + (bounds.width() - bitmap.width * scale) / 2f,
            bounds.top + (bounds.height() - bitmap.height * scale) / 2f
        )
    }

    override fun draw(canvas: Canvas) {
        bitmap?.let { canvas.drawBitmap(it, matrix, paint) }
    }

    override fun setAlpha(alpha: Int) {
        if (paint.alpha != alpha) {
            paint.alpha = alpha
            invalidateSelf()
        }
    }

    override fun getAlpha(): Int = paint.alpha

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getColorFilter(): ColorFilter? = paint.colorFilter

    // Covering the bounds with an opaque bitmap lets the framework skip drawing whatever
    // is underneath; without a bitmap we draw nothing and must not claim to be opaque.
    override fun getOpacity(): Int = when {
        bitmap == null || paint.alpha < 255 -> PixelFormat.TRANSLUCENT
        bitmap?.hasAlpha() == true -> PixelFormat.TRANSLUCENT
        else -> PixelFormat.OPAQUE
    }
}
