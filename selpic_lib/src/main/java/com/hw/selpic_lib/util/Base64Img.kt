package com.hw.selpic_lib.util

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import java.io.*


/**
 * @author hewei(David)
 * @date 2020/9/9  5:54 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */

fun Bitmap2StrByBase64(bit: Bitmap): String? {
    val bos = ByteArrayOutputStream()
    bit.compress(Bitmap.CompressFormat.JPEG, 100, bos) //参数100表示不压缩
    val bytes = bos.toByteArray()
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}


fun File2BitmapUpload(context: Context, srcPath: String): Bitmap? {
    var bitmap: Bitmap? = null
    val newOpts = BitmapFactory.Options()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !srcPath.contains(context.packageName)) {
        val uri = getImageContentUri(context, srcPath)
        try {
            bitmap = getBitmapFormUri(context as Activity, uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    } else { // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts) // 此时返回bm为空
        newOpts.inJustDecodeBounds = false
        val w = newOpts.outWidth
        val h = newOpts.outHeight
        val hh = 1200f // 这里设置高度为800f
        val ww = 720f // 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        var be = 1 // be=1表示不缩放
        if (w > h && w > ww) { // 如果宽度大的话根据宽度固定大小缩放
            be = (newOpts.outWidth / ww).toInt()
        } else if (w < h && h > hh) { // 如果高度高的话根据宽度固定大小缩放
            be = (newOpts.outHeight / hh).toInt()
        }
        be = if (be <= 0) 1 else if (be <= 3) 2 else 3
        newOpts.inSampleSize = be // 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        Log.e("image", "img==$srcPath")
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts)
    }
    return bitmap // 压缩好比例大小后再进行质量压缩
    // return bitmap;
}

fun compressImage(image: Bitmap): Bitmap? {
    val baos = ByteArrayOutputStream()
    image.compress(Bitmap.CompressFormat.JPEG, 100, baos) // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
    var options = 100
    while (baos.toByteArray().size / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        if (options < 80) break
        baos.reset() // 重置baos即清空baos
        image.compress(Bitmap.CompressFormat.JPEG, options, baos) // 这里压缩options%，把压缩后的数据存放到baos中
        options -= 10 // 每次都减少10
    }
    val isBm =
        ByteArrayInputStream(baos.toByteArray()) // 把压缩后的数据baos存放到ByteArrayInputStream中
    return BitmapFactory.decodeStream(isBm, null, null)
}

//为了适配Android10，读取非应用内的图片不能直接访问源文件地址，需要转成uri加载图片
//------start--------
fun getImageContentUri(
    context: Context,
    path: String
): Uri? {
    val cursor = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(
            MediaStore.Images.Media._ID
        ), MediaStore.Images.Media.DATA + "=? ", arrayOf(path), null
    )
    return if (cursor != null && cursor.moveToFirst()) {
        val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
        val baseUri =
            Uri.parse("content://media/external/images/media")
        Uri.withAppendedPath(baseUri, "" + id)
    } else { // 如果图片不在手机的共享图片数据库，就先把它插入。
        if (File(path).exists()) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DATA, path)
            context.contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            null
        }
    }
}

// 通过uri加载图片
fun getBitmapFromUri(context: Context, uri: Uri?): Bitmap? {
    try {
        val parcelFileDescriptor =
            context.contentResolver.openFileDescriptor(uri!!, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}


/**
 * 通过uri获取图片并进行压缩
 *
 * @param uri
 */
@Throws(IOException::class)
fun getBitmapFormUri(ac: Activity, uri: Uri?): Bitmap? {
    var input = ac.contentResolver.openInputStream(uri!!)
    val onlyBoundsOptions = BitmapFactory.Options()
    onlyBoundsOptions.inJustDecodeBounds = true
    onlyBoundsOptions.inDither = true //optional
    onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888 //optional
    BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
    input!!.close()
    val originalWidth = onlyBoundsOptions.outWidth
    val originalHeight = onlyBoundsOptions.outHeight
    if (originalWidth == -1 || originalHeight == -1) return null
    //图片分辨率以480x800为标准
    val hh = 1200f //这里设置高度为800f
    val ww = 720f //这里设置宽度为480f
    //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
    var be = 1 //be=1表示不缩放
    if (originalWidth > originalHeight && originalWidth > ww) { //如果宽度大的话根据宽度固定大小缩放
        be = (originalWidth / ww).toInt()
    } else if (originalWidth < originalHeight && originalHeight > hh) { //如果高度高的话根据宽度固定大小缩放
        be = (originalHeight / hh).toInt()
    }
    be = if (be <= 0) 1 else if (be <= 3) 2 else 3
    //比例压缩
    val bitmapOptions = BitmapFactory.Options()
    bitmapOptions.inSampleSize = be //设置缩放比例
    bitmapOptions.inDither = true //optional
    bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888 //optional
    input = ac.contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions)
    input!!.close()
    return bitmap //再进行质量压缩
}
//---------------end-----------


//     * @Title: GetImageStrFromUrl
//     * @param imgURL 网络资源位置
//     * @return Base64字符串
//     */
//    public static String GetImageStrFromUrl(String imgURL) {
//        byte[] data = null;
//        try {
//            // 创建URL
//            URL url = new URL(imgURL);
//            // 创建链接
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setConnectTimeout(5 * 1000);
//            InputStream inStream = conn.getInputStream();
//            data = new byte[inStream.available()];
//            inStream.read(data);
//            inStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // 对字节数组Base64编码
//        BASE64Encoder encoder = new BASE64Encoder();
//        // 返回Base64编码过的字节数组字符串
//        return encoder.encode(data);
//    }
//
//    /**
//     * @Title: GetImageStrFromPath
//     * @param imgPath
//     * @return
//     */
//    public static String GetImageStrFromPath(String imgPath) {
//        InputStream in = null;
//        byte[] data = null;
//        // 读取图片字节数组
//        try {
//            in = new FileInputStream(imgPath);
//            data = new byte[in.available()];
//            in.read(data);
//            in.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // 对字节数组Base64编码
//        BASE64Encoder encoder = new BASE64Encoder();
//        // 返回Base64编码过的字节数组字符串
//        return encoder.encode(data);
//    }

//---------------end-----------
//     * @Title: GetImageStrFromUrl
//     * @param imgURL 网络资源位置
//     * @return Base64字符串
//     */
//    public static String GetImageStrFromUrl(String imgURL) {
//        byte[] data = null;
//        try {
//            // 创建URL
//            URL url = new URL(imgURL);
//            // 创建链接
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setConnectTimeout(5 * 1000);
//            InputStream inStream = conn.getInputStream();
//            data = new byte[inStream.available()];
//            inStream.read(data);
//            inStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // 对字节数组Base64编码
//        BASE64Encoder encoder = new BASE64Encoder();
//        // 返回Base64编码过的字节数组字符串
//        return encoder.encode(data);
//    }
//
//    /**
//     * @Title: GetImageStrFromPath
//     * @param imgPath
//     * @return
//     */
//    public static String GetImageStrFromPath(String imgPath) {
//        InputStream in = null;
//        byte[] data = null;
//        // 读取图片字节数组
//        try {
//            in = new FileInputStream(imgPath);
//            data = new byte[in.available()];
//            in.read(data);
//            in.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // 对字节数组Base64编码
//        BASE64Encoder encoder = new BASE64Encoder();
//        // 返回Base64编码过的字节数组字符串
//        return encoder.encode(data);
//    }
/**
 * 根据地址获得数据的字节流
 *
 * @param strUrl 本地连接地址
 * @return
 */
fun getImageFromLocalByUrl(strUrl: String?): ByteArray? {
    try {
        val imageFile = File(strUrl)
        val inStream: InputStream = FileInputStream(imageFile)
        return readInputStream(inStream)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

/**
 * 从输入流中获取数据
 *
 * @param inStream 输入流
 * @return
 * @throws IOException
 * @throws Exception
 */
@Throws(IOException::class)
private fun readInputStream(inStream: InputStream): ByteArray {
    val outStream = ByteArrayOutputStream()
    val buffer = ByteArray(10240)
    var len = 0
    while (inStream.read(buffer).also { len = it } != -1) {
        outStream.write(buffer, 0, len)
    }
    inStream.close()
    return outStream.toByteArray()
}

//    public static byte[] setFileToByteCompress(String fileUrl) {
//        Bitmap bit = File2BitmapUpload(fileUrl);
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        bit.compress(Bitmap.CompressFormat.JPEG, 100, bos);//参数100表示不压缩
//        byte[] bytes = bos.toByteArray();
//        return bytes;
//    }

//    public static byte[] setFileToByteCompress(String fileUrl) {
//        Bitmap bit = File2BitmapUpload(fileUrl);
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        bit.compress(Bitmap.CompressFormat.JPEG, 100, bos);//参数100表示不压缩
//        byte[] bytes = bos.toByteArray();
//        return bytes;
//    }
@Throws(Exception::class)
fun setFileToByteCompress(
    context: Context,
    fileUrl: String
): ByteArray? {
    val bit = File2BitmapUpload(context, fileUrl) ?: return null
    val bos = ByteArrayOutputStream()
    bit.compress(Bitmap.CompressFormat.JPEG, 90, bos) //参数100表示不压缩
    return bos.toByteArray()
}