package com.yootom.dylinkapp.Utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.createChooser
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

/**
 * Created by yootom on 2019-12-12.
 */

class DynamicLinkHelper(private val activity: Activity) {

    private val TAG: String = DynamicLinkHelper::class.java.simpleName

    private val DY_LINK_DOMAIN: String = "https://dylink.yootom.com/"
    private val DY_LINK_SEGMENT_PATH: String = "wtach/"
    private val DY_LINK_URL_PREFIX: String = "https://ytom.page.link"

    //Create Dynamic Link
    //QR Code + Url(DeepLink)
    fun createDynamicLink(img: ImageView, txt: TextView, code: String) {
        Log.d(TAG, "Start, createDynamicLink")
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(getCreatedCodeDeepLink(code))
            .setDomainUriPrefix(DY_LINK_URL_PREFIX) //동적 링크 도메인, Firebase console 에서 주어짐
            // Open links with this app on Android
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder(activity.packageName).build()) //Package Name
            .buildShortDynamicLink() //이 함수를 사용해서 긴 url이 아닌 단축 url을 생성할 수 있음
            .addOnSuccessListener { result: ShortDynamicLink ->
                // Short link created
                val shortLink: String = result.shortLink.toString()
                // Preview link  created
                val flowchartLink: String = result.previewLink.toString()

                Log.d(TAG, "Created, Short link : $shortLink, Preview link : $flowchartLink")
                createQRCode(img, shortLink)
                txt.text = shortLink
            }
            .addOnFailureListener { e: java.lang.Exception ->
                Log.d(TAG, "Failed, Dynamic Link : ${e.printStackTrace()}")
            }
    }

    //Share DeepLink (Invite)
    fun shareDynamicLink(str: String) {
        Log.d(TAG, "Start, shareDynamicLink")
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(getCreatedCodeDeepLink(str))
            .setDomainUriPrefix(DY_LINK_URL_PREFIX)
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder(activity.packageName).build())
            .buildShortDynamicLink()
            .addOnSuccessListener { result: ShortDynamicLink ->
                // Short link created
                val shortLink: String = result.shortLink.toString()
                // Preview link  created
                val flowchartLink: String = result.previewLink.toString()
                Log.d(TAG, "Created, Short link : $shortLink, Preview link : $flowchartLink")

                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shortLink)
                    type = "text/plain"
                }

                activity.startActivity(createChooser(sendIntent, "Share"))

            }
    }

    //Get DeepLink
    fun handleDeepLink() {
        Log.d(TAG, "Start, handleDeepLink")
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(activity.intent)
            .addOnSuccessListener(activity) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                //has not DeepLink Data
                @Suppress("SENSELESS_COMPARISON")
                if (pendingDynamicLinkData == null) {
                    Log.d(TAG, "No have dynamic link")
                    return@addOnSuccessListener
                }
                //has DeepLik Data
                val deepLink: Uri? = pendingDynamicLinkData.link
                Log.d(TAG, "deepLink: $deepLink")

                //Get Path
                val segment: String? = deepLink?.lastPathSegment
                segment?.let { showReactiveDialog(it) }
            }
            //Failed Get DeepLink Data
            .addOnFailureListener(activity) { e: java.lang.Exception ->
                Log.d(TAG, "getDynamicLink:onFailure", e)
            }
    }


    //Create Deep Link
    private fun getCreatedCodeDeepLink(code: String): Uri {
        //_https://dylink.yootom.com/wtach/code
        return Uri.parse(DY_LINK_DOMAIN + DY_LINK_SEGMENT_PATH + code)
    }

    //Create QR Code
    private fun createQRCode(canvas: ImageView, text: String) {
        Log.d(TAG, "Start, createQRCode")
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix =
                multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            canvas.setImageBitmap(bitmap)
            Log.d(TAG, "Created, QR Code.")
        } catch (e: Exception) {
            Log.d(TAG, "Failed, QR Code : ${e.printStackTrace()}")
        }
    }

    //Show Dialog
    private fun showReactiveDialog(code: String) {
        Log.d(TAG, "Start, showReactiveDialog")
        AlertDialog.Builder(activity)
            .setMessage("Receive promotion code: $code")
            .setPositiveButton("Confirm", null)
            .create().show()
    }
}