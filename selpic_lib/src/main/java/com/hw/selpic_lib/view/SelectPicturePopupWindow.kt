package com.hw.selpic_lib.view

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.RelativeLayout
import com.hw.selpic_lib.R


/**
 * @author hewei(David)
 * @date 2020/9/9  2:59 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */

class SelectPicturePopupWindow(context: Context, isDarkMode: Boolean) :
    PopupWindow(context), View.OnClickListener {
    private val takePhotoBtn: Button
    private val pickPictureBtn: Button
    private val cancelBtn: Button
    private val mMenuView: View
    private var popupWindow: PopupWindow? = null
    private var mOnSelectedListener: OnSelectedListener? = null
    private val rl_pop_root: RelativeLayout
    /**
     * 把一个View控件添加到PopupWindow上并且显示
     *
     * @param activity
     */
    fun showPopupWindow(activity: Activity) {
        dismissPopupWindow()
        if (activity.isFinishing) return
        popupWindow = PopupWindow(
            mMenuView,  // 添加到popupWindow
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        // ☆ 注意： 必须要设置背景，播放动画有一个前提 就是窗体必须有背景
        popupWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow!!.showAtLocation(
            activity.window.decorView,
            Gravity.CENTER or Gravity.BOTTOM,
            0,
            0
        )
        //        popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);   // 设置窗口显示的动画效果
        popupWindow!!.isFocusable = true // 点击其他地方隐藏键盘 popupWindow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            popupWindow!!.update()
        }
    }

    /**
     * 移除PopupWindow
     */
    fun dismissPopupWindow() {
        if (popupWindow != null && popupWindow!!.isShowing) {
            popupWindow!!.dismiss()
            popupWindow = null
        }
    }

    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.picture_selector_take_photo_btn) {
            if (null != mOnSelectedListener) {
                mOnSelectedListener!!.OnSelected(v, 0)
            }
        } else if (i == R.id.picture_selector_pick_picture_btn) {
            if (null != mOnSelectedListener) {
                mOnSelectedListener!!.OnSelected(v, 1)
            }
        } else if (i == R.id.picture_selector_cancel_btn) {
            if (null != mOnSelectedListener) {
                mOnSelectedListener!!.OnSelected(v, 2)
            }
        } else if (i == R.id.rl_pop_root) {
            dismissPopupWindow()
        }
    }

    /**
     * 设置选择监听
     *
     * @param l
     */
    fun setOnSelectedListener(l: OnSelectedListener?) {
        mOnSelectedListener = l
    }

    /**
     * 选择监听接口
     */
    interface OnSelectedListener {
        fun OnSelected(v: View?, position: Int)
    }

    init {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mMenuView = if(isDarkMode)
            inflater.inflate(R.layout.layout_picture_selector_dark, null)
        else inflater.inflate(R.layout.layout_picture_selector, null)
        takePhotoBtn =
            mMenuView.findViewById(R.id.picture_selector_take_photo_btn)
        pickPictureBtn =
            mMenuView.findViewById(R.id.picture_selector_pick_picture_btn)
        cancelBtn = mMenuView.findViewById(R.id.picture_selector_cancel_btn)
        rl_pop_root = mMenuView.findViewById(R.id.rl_pop_root)
        // 设置按钮监听
        takePhotoBtn.setOnClickListener(this)
        pickPictureBtn.setOnClickListener(this)
        cancelBtn.setOnClickListener(this)
        rl_pop_root.setOnClickListener(this)
    }
}