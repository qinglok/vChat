package me.linx.vchat.app.ec.sign

import android.os.Bundle
import androidx.lifecycle.*
import kotlinx.android.synthetic.main.fragment_sign.view.*
import me.linx.vchat.app.R
import me.linx.vchat.app.common.base.BaseFragment

class SignParentFragment : BaseFragment() {
    private lateinit var toolbarViewMode: ToolbarViewMode

    override fun setLayout() = R.layout.fragment_sign

    override fun onBindView(savedInstanceState: Bundle?) {
        toolbarViewMode = ViewModelProviders.of(this).get(ToolbarViewMode::class.java)

        toolbarViewMode.currentTitle.observe(this, Observer<String> { title ->
            // Update the UI, in this case, a TextView.
            rootView.toolbar.title = title
        })

        loadRootFragment(R.id.fl_container, SignInFragment())
    }

    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        super.onEnterAnimationEnd(savedInstanceState)
        rootView.toolbar.setNavigationOnClickListener {
            if (topChildFragment is SignUpFragment)
                (topChildFragment as SignUpFragment).pop()
            else {
                hideSoftInput()
                _mActivity.finish()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        hideSoftInput()
    }
}

internal class ToolbarViewMode : ViewModel() {
    // Create a LiveData with a String
    val currentTitle: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}

