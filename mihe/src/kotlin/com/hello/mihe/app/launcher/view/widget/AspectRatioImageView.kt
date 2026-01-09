package com.hello.mihe.app.launcher.view.widget

import android.content.Context
import android.util.AttributeSet
import com.android.launcher3.R

class AspectRatioImageView : androidx.appcompat.widget.AppCompatImageView {

  private var imageRatioWidth: Float = 1f
  private var imageRatioHeight: Float = 1f

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    initRatio(context, attrs)
  }

  constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int
  ) : super(context, attrs, defStyleAttr) {
    initRatio(context, attrs)
  }

  private fun initRatio(context: Context, attrs: AttributeSet) {
    val ta = context.obtainStyledAttributes(attrs, R.styleable.mihe_AspectRatioImageView)
    imageRatioWidth = ta.getFloat(R.styleable.mihe_AspectRatioImageView_mihe_ratio_width, 1f)
    imageRatioHeight = ta.getFloat(R.styleable.mihe_AspectRatioImageView_mihe_ratio_height, 1f)
    ta.recycle()
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val measuredHeightTmp = measuredWidth * imageRatioHeight / imageRatioWidth
    setMeasuredDimension(measuredWidth, measuredHeightTmp.toInt())
  }

  fun setImageRatio(ratioWidth: Float, ratioHeight: Float) {
    this.imageRatioHeight = ratioHeight
    this.imageRatioWidth = ratioWidth
    requestLayout()
  }
}
