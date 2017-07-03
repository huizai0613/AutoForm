package com.twiceyuan.autoform.sample;

import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.twiceyuan.autoform.FormItemEntity;
import com.twiceyuan.autoform.provider.FormItemValidator;

/**
 * Created by twiceYuan on 2017/7/3.
 *
 * 手机号码验证器
 */
public class PhoneValidator implements FormItemValidator<PhoneFormItemProvider> {

    @Override
    public void onValidateFailed(FormItemEntity entity, PhoneFormItemProvider provider) {
        EditText mEtContent = provider.getEtPhone();
        if (mEtContent.length() != 11) {
            mEtContent.requestFocus();
            new AlertDialog.Builder(mEtContent.getContext())
                    .setMessage("手机号码必须为 11 位纯数字")
                    .setPositiveButton("好的", null)
                    .show();
        }
    }

    @Override
    public boolean validate(Object result) {
        return result instanceof String && ((String) result).length() == 11;
    }
}