package me.linx.vchat.app.widget.loader

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_loader_dialog.view.*
import me.linx.vchat.app.R

class LoaderDialogFragment : DialogFragment() {
    private lateinit var rootView: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        // 禁用点击空白
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        // 禁用返回键
        dialog.setOnKeyListener { _, keyCode, _ ->
            keyCode == KeyEvent.KEYCODE_BACK
        }

        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_loader_dialog, container, false)
        rootView.cpv.post {
            rootView.cpv.startAnimation()
        }

        // 对话框内部的背景设为透明
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return rootView
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        rootView.cpv.post {
            rootView.cpv.stopAnimation()
        }
    }
}