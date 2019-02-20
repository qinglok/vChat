package me.linx.vchat.app.widget.loader

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_loader_dialog.view.*
import me.linx.vchat.app.AppActivity
import me.linx.vchat.app.R

class LoaderDialogFragment : DialogFragment() {
    private lateinit var rootView: View
    private var onDismiss : () -> Unit = {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        // 禁用点击空白
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        // 禁用返回键
//        dialog.setOnKeyListener { _, keyCode, _ ->
//            keyCode == KeyEvent.KEYCODE_BACK
//        }

        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_loader_dialog, container, false)

        // 对话框内部的背景设为透明
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        rootView.cpv.startAnimation()

        return rootView
    }

    fun showWithOnDismiss(onDismiss : () -> Unit) {
        this.onDismiss = onDismiss

        AppActivity.instance?.let {
            super.show(it.supportFragmentManager, null)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss()
    }

}