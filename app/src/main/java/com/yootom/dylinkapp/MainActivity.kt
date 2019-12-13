package com.yootom.dylinkapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.yootom.dylinkapp.Utils.DynamicLinkHelper
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by yootom on 2019-12-12.
 */

class MainActivity : AppCompatActivity() {
    private val TAG: String = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Start, onCrete")
        setContentView(R.layout.activity_main)

        btn_share_dynamic_link.setOnClickListener {
            if (edit_code.text.toString().toByteArray().isNotEmpty()) {
                DynamicLinkHelper(this).createDynamicLink(
                    img_barcode,
                    txt_url,
                    edit_code.text.toString()
                )
            }
            it.hideKeyboard()
        }

        btn_share_invite.setOnClickListener {
            if (edit_code.text.toString().toByteArray().isNotEmpty()) {
                DynamicLinkHelper(this).shareDynamicLink(edit_code.text.toString())
            }
            it.hideKeyboard()
        }
        DynamicLinkHelper(this).handleDeepLink()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Start, onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "Start, onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Start, onDestroy")
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }


}
