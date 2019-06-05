package com.venmo.android.pin

import android.view.View
import android.widget.Toast

import com.venmo.android.pin.view.PinputView.OnCommitListener

internal class ConfirmPinViewController<T : PinFragmentImplement>(
    f: T,
    v: View,
    private val mTruthString: String
) : BaseViewController<T>(f, v) {

    internal override fun initUI() {
        val confirm = String.format(
            mContext.getString(R.string.confirm_n_digit_pin),
            mPinputView.pinLen
        )
        mHeaderText.text = confirm
    }

    internal override fun provideListener(): OnCommitListener {
        return OnCommitListener { view, submission ->
            if (submission == mTruthString) {
                handleSave(submission)
            } else {
                Toast.makeText(
                    mContext, mContext.getString(R.string.pin_mismatch),
                    Toast.LENGTH_SHORT
                ).show()
                resetToCreate()
                view.showErrorAndClear()
            }
        }
    }

    private fun handleSave(submission: String) {
        val saver = config.pinSaver
        if (saver is AsyncSaver) {
            getOutAndInAnim(mPinputView, mProgressBar).start()
            mHeaderText.setText(R.string.saving_pin)
            runAsync {
                try {
                    config.pinSaver.save(submission)
                    postToMain { onSaveComplete() }
                } catch (e: Exception) {
                    generalErrorAsync(mPinFragment.getString(R.string.async_save_error))
                }
            }
        } else {
            saver.save(submission)
            onSaveComplete()
        }
    }

    private fun onSaveComplete() {
        mPinputView.text.clear()
        mPinFragment.notifyCreated()
    }

    private fun resetToCreate() {
        mPinFragment.setDisplayType(PinDisplayType.CREATE)
        mPinFragment.setViewController(CreatePinViewController(mPinFragment, mRootView))
    }

}
