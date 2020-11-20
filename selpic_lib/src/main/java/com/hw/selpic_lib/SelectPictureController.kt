package com.hw.selpic_lib

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.os.EnvironmentCompat
import androidx.fragment.app.Fragment
import com.hw.selpic_lib.activity.CropActivity
import com.hw.selpic_lib.util.YYYY_MM_DD
import com.hw.selpic_lib.util.getFormatDateString
import com.hw.selpic_lib.view.SelectPicturePopupWindow
import com.kevin.crop.UCrop
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author hewei(David)
 * @date 2020/9/9  2:57 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */

class SelectPictureController: SelectPicturePopupWindow.OnSelectedListener {

    override fun OnSelected(v: View?, position: Int) {
        when (position) {
            0 ->  // "拍照"按钮被点击了
                takePhoto()
            1 ->  // "从相册选择"按钮被点击了
                pickFromGallery()
        }
        mSelectPicturePopupWindow!!.dismissPopupWindow()
    }


    protected val REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101
    protected val REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102
    val GALLERY_REQUEST_CODE = 0 // 相册选图标记

    val CAMERA_REQUEST_CODE = 1 // 相机拍照标记

    val CUSTOM_CAMERA_REQUEST_CODE = 2 // 自定义相机拍照标记

    private val TAG = "MyFragment"
    /**
     * 图片选择的监听回调
     */
    var mOnPictureSelectedListener: OnPictureSelectedListener? = null
    // 拍照临时图片
    var mTempPhotoPath: String? = null

    //用于保存拍照图片的uri
    var mCameraUri: Uri? = null

    // 用于保存图片的文件路径，Android 10以下使用图片路径访问图片
    private var mCameraImagePath: String? = null

    // 剪切后图像文件
    var mDestinationUri: Uri? = null
    var w = 0
    var h = 0
    private var isCustomCamera = false

    // 是否是Android 10以上手机
    private val isAndroidQ =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    /**
     * 选择提示 PopupWindow
     */
    private var mSelectPicturePopupWindow: SelectPicturePopupWindow? = null
    private var mContext: Context? = null
    private var mFragment: Fragment? = null
    private val count = 0

    constructor(  mContext: Context?,
                  fragment: Fragment?
    ) {
        this.mContext = mContext
        mFragment = fragment
        initPopwindows()
    }
    constructor(mContext: Context?) {
        this.mContext = mContext
        initPopwindows()
    }



    private fun initPopwindows() {
        mDestinationUri =
            Uri.fromFile(File(mContext!!.cacheDir, "cropImage.jpeg"))
        mTempPhotoPath =
            Environment.getExternalStorageDirectory().absoluteFile.toString() + "/photo.jpeg"
        mSelectPicturePopupWindow = SelectPicturePopupWindow(mContext!!)
        mSelectPicturePopupWindow!!.setOnSelectedListener(this)
    }


    fun showPopWindows() {
        mSelectPicturePopupWindow!!.showPopupWindow((mContext as Activity?)!!)
    }

    fun showPopWindows(w: Int, h: Int) {
        this.w = w
        this.h = h
        mSelectPicturePopupWindow!!.showPopupWindow((mContext as Activity?)!!)
    }


    fun showPopWindows(isCustomCamera: Boolean) { //自定义拍照
        this.isCustomCamera = isCustomCamera
        mSelectPicturePopupWindow!!.showPopupWindow((mContext as Activity?)!!)
    }

    fun showPopWindows(w: Int, h: Int, isCustomCamera: Boolean) {
        this.isCustomCamera = isCustomCamera
        this.w = w
        this.h = h
        mSelectPicturePopupWindow!!.showPopupWindow((mContext as Activity?)!!)
    }


    private var idcardFlag = 0

    fun getIdcardFlag(): Int {
        return idcardFlag
    }

    fun setIdcardFlag(idcardFlag: Int) {
        this.idcardFlag = idcardFlag
    }


    /**
     * 提示用户去应用设置界面手动开启权限
     */
// 请求读写权限requestCode
    val PERMI_REQUEST_WRITE = 101
    // 请求相机requestCode
    val PERMI_REQUEST_CAMERA = 102

    // 请求相机&读写requestCode
    val PERMI_REQUEST_CAMERA_WRITE = 106


    fun takePhoto() { // 检查权限是否已经获取
        val checkCameraPermission = ActivityCompat.checkSelfPermission(
            mContext!!,
            Manifest.permission.CAMERA
        )
        val checkWritePermission = ActivityCompat.checkSelfPermission(
            mContext!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
        if (checkCameraPermission != PackageManager.PERMISSION_GRANTED && checkWritePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                (mContext as Activity?)!!.requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), PERMI_REQUEST_CAMERA_WRITE
                )
            } else Toast.makeText(mContext, "请在应用管理中打开“相机,读写存储”访问权限！", Toast.LENGTH_LONG).show()
        } else if (checkCameraPermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                (mContext as Activity?)!!.requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    PERMI_REQUEST_CAMERA
                )
            } else Toast.makeText(mContext, "请在应用管理中打开“相机”访问权限！", Toast.LENGTH_LONG).show()
        } else if (checkWritePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                (mContext as Activity?)!!.requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMI_REQUEST_WRITE
                )
            } else Toast.makeText(mContext, "请在应用管理中打开“读写存储”访问权限！", Toast.LENGTH_LONG).show()
        } else {
            if (mSelectPicturePopupWindow != null) mSelectPicturePopupWindow!!.dismissPopupWindow()
            if (!isCustomCamera) {
                val takeIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                // 判断是否有相机
                if (takeIntent.resolveActivity(mContext!!.packageManager) != null) {
                    var photoFile: File? = null
                    var photoUri: Uri? = null
                    if (isAndroidQ) { // 适配android 10
                        photoUri = createImageUri()
                    } else {
                        try {
                            photoFile = createImageFile()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        if (photoFile != null) {
                            mCameraImagePath = photoFile.absolutePath
                            photoUri =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                                    FileProvider.getUriForFile(
                                        mContext!!,
                                        mContext!!.applicationContext.packageName + ".fileProvider",
                                        photoFile
                                    )
                                } else {
                                    Uri.fromFile(photoFile)
                                }
                        }
                    }
                    mCameraUri = photoUri
                    if (photoUri != null) {
                        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                        takeIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        //                        mFragment.getActivity().startActivityForResult(takeIntent, CAMERA_REQUEST_CODE);
                        if (null != mFragment) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                mFragment!!.startActivityForResult(takeIntent, CAMERA_REQUEST_CODE)
                            }
                        } else (mContext as Activity?)!!.startActivityForResult(
                            takeIntent,
                            CAMERA_REQUEST_CODE
                        )
                    }
                }
            } else {
//                val takeIntent = Intent(mContext, CameraActivity::class.java)
//                takeIntent.putExtra("idcard_flag", getIdcardFlag())
//                (mContext as Activity?)!!.startActivityForResult(
//                    takeIntent,
//                    CUSTOM_CAMERA_REQUEST_CODE
//                )
            }
        }
    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     */
    private fun createImageUri(): Uri? {
        val status = Environment.getExternalStorageState()
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        return if (status == Environment.MEDIA_MOUNTED) {
            mContext!!.contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())
        } else {
            mContext!!.contentResolver
                .insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, ContentValues())
        }
    }

    /**
     * 创建保存图片的文件
     */
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val imageName =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(Date())
        val storageDir =
            mContext!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (!storageDir!!.exists()) {
            storageDir.mkdir()
        }
        val tempFile = File(storageDir, imageName)
        return if (Environment.MEDIA_MOUNTED != EnvironmentCompat.getStorageState(
                tempFile
            )
        ) {
            null
        } else tempFile
    }

    fun pickFromGallery() { // 检查权限是否已经获取
        val checkWritePermission = ActivityCompat.checkSelfPermission(
            mContext!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
        if (checkWritePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                (mContext as Activity?)!!.requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMI_REQUEST_WRITE
                )
            } else Toast.makeText(mContext, "请在应用管理中打开“读写存储”访问权限！", Toast.LENGTH_LONG).show()
        } else {
            mSelectPicturePopupWindow!!.dismissPopupWindow()
            val pickIntent = Intent(Intent.ACTION_PICK, null)
            // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            if (null != mFragment) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mFragment!!.startActivityForResult(pickIntent, GALLERY_REQUEST_CODE)
                }
            } else (mContext as Activity?)!!.startActivityForResult(
                pickIntent,
                GALLERY_REQUEST_CODE
            )
        }
    }



    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    fun startCropActivity(uri: Uri?) {
        if (null != mFragment) UCrop.of(uri!!, mDestinationUri!!)
            .withAspectRatio(432f, 432f)
            .withMaxResultSize(432, 432)
            .withTargetActivity(CropActivity::class.java)
            .start(mContext!!, mFragment!!)
        else UCrop.of(uri!!, mDestinationUri!!)
            .withAspectRatio(432f, 432f)
            .withMaxResultSize(432, 432)
            .withTargetActivity(CropActivity::class.java)
            .start((mContext as Activity?)!!)
    }

    fun startCropActivity(uri: Uri?, w: Int, h: Int) {
        if (null != mFragment) UCrop.of(uri!!, mDestinationUri!!)
            .withAspectRatio(w.toFloat(), h.toFloat())
            .withMaxResultSize(w, h)
            .withTargetActivity(CropActivity::class.java)
            .start(mContext!!, mFragment!!)
        else UCrop.of(uri!!, mDestinationUri!!)
            .withAspectRatio(w.toFloat(), h.toFloat())
            .withMaxResultSize(w, h)
            .withTargetActivity(CropActivity::class.java)
            .start((mContext as Activity?)!!)
    }


    /**
     * 处理自定义拍照成功的返回值
     *
     * @param fileName
     */
    fun handlePhotoResult(fileName: String?) { //        deleteTempPhotoFile();
        Log.e("image", "fileName==$fileName")
        if (null != fileName) {
        } else {
            Toast.makeText(mContext, "无法剪切选择图片", Toast.LENGTH_SHORT).show()
            //            ScToast.getInstance(mContext).showToast( "无法剪切选择图片");
        }
    }

    /**
     * 处理剪切成功的返回值
     *
     * @param result
     */
    fun handleCropResult(result: Intent?, fileName: String?) {
        deleteTempPhotoFile()
        val resultUri = UCrop.getOutput(result!!)
        Log.e("image", "resultUri==$resultUri")
        if (null != resultUri) {
            val filePath = resultUri.encodedPath
            val imagePath = Uri.decode(filePath)
            Log.e("image", "img==$imagePath")
            mOnPictureSelectedListener?.onPictureSelected(imagePath)
        } else {
            Toast.makeText(mContext, "无法剪切选择图片", Toast.LENGTH_SHORT).show()
            //            ScToast.getInstance(mContext).showToast( "无法剪切选择图片");
        }
    }

    /**
     * 处理剪切失败的返回值
     *
     * @param result
     */
    fun handleCropError(result: Intent?) {
        deleteTempPhotoFile()
        val cropError = UCrop.getError(result!!)
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError)
            Toast.makeText(mContext, cropError.message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(mContext, "无法剪切选择图片,请重试！", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 删除拍照临时文件
     */
    fun deleteTempPhotoFile() {
        if (mTempPhotoPath != null) {
            val tempFile = File(mTempPhotoPath)
            if (tempFile.exists() && tempFile.isFile) {
                tempFile.delete()
            }
        }
        if (mCameraImagePath != null) {
            val tempFile2 = File(mCameraImagePath)
            if (tempFile2.exists() && tempFile2.isFile) {
                tempFile2.delete()
            }
        }
    }

    /**
     * 图片选择的回调接口
     */
    interface OnPictureSelectedListener {
        /**
         * 图片选择的监听回调
         *
         * @param url
         */
        fun onPictureSelected(url: String?, filePath: String?)
        fun onPictureSelected(filePath: String?)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
//                CUSTOM_CAMERA_REQUEST_CODE ->
                CAMERA_REQUEST_CODE ->
                    if (isAndroidQ) {
                        if (w > 0 && h > 0) startCropActivity(mCameraUri, w, h)
                        else startCropActivity(mCameraUri)
                    } else {
                        val temp = File(mCameraImagePath)
                        if (w > 0 && h > 0) startCropActivity(Uri.fromFile(temp), w, h
                        ) else startCropActivity(Uri.fromFile(temp))
                    }
                GALLERY_REQUEST_CODE ->
                    if (w > 0 && h > 0) startCropActivity(data?.data, w, h)
                    else startCropActivity(data?.data)
                UCrop.REQUEST_CROP -> handleCropResult(data, getFormatDateString(YYYY_MM_DD) + "/" + System.currentTimeMillis()  + ".jpg")
                UCrop.RESULT_ERROR -> handleCropError(data)
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data)
        }
    }
}