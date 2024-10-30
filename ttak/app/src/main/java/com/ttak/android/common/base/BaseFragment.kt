package com.ttak.android.common.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

abstract class BaseFragment<VM : BaseViewModel>(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    protected lateinit var viewModel: VM

    // ViewModel의 Class 타입을 반환하는 추상 메서드 (Class<VM> 반환)
    abstract fun getViewModelClass(): Class<VM>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel 초기화, getViewModelClass() 타입 명확히 지정
        viewModel = ViewModelProvider(this).get(getViewModelClass())

        // 로딩 상태 관찰
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        }

        // 에러 메시지 관찰
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                showError(it)
                viewModel.clearError() // 에러 초기화
            }
        }
    }

    open fun showLoading() {
        // ProgressBar 등 로딩 UI 표시
    }

    open fun hideLoading() {
        // ProgressBar 등 로딩 UI 숨김
    }

    open fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}