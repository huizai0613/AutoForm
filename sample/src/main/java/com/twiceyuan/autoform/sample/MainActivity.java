package com.twiceyuan.autoform.sample;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.twiceyuan.autoform.FormItemEntity;
import com.twiceyuan.autoform.FormManager;
import com.twiceyuan.autoform.provider.Validator;
import com.twiceyuan.autoform.sample.bean.Category;
import com.twiceyuan.autoform.sample.form.SelectorLayoutProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FrameLayout     mFormContainer;
    private AppCompatButton mBtnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        DemoForm form = new DemoForm();
        form.address = "默认地址";

        final FormManager<DemoForm> formManager = FormManager
                .build(DemoForm.class)
                .initData(form)
                .inject(mFormContainer);

        appendSelectorLogic(formManager);

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mockSubmit(formManager);
            }
        });
    }

    /**
     * 追加选择器的逻辑
     *
     * @param formManager 选择器所在的表单管理器
     */
    private void appendSelectorLogic(FormManager formManager) {
        final SelectorLayoutProvider selector = (SelectorLayoutProvider) formManager.findLayoutProviderByKey("type");

        // 异步请求分类并配置
        asyncFetchCategories(new Callback<List<Category>>() {
            @Override
            public void call(final List<Category> categories) {
                List<String> categoriesTitle = new ArrayList<>();
                for (Category category : categories) {
                    categoriesTitle.add(category.name);
                }
                // 默认提示
                categoriesTitle.add(0, "请选择分类");
                selector.setData(categoriesTitle);
                selector.setPositionValueMapper(new SelectorLayoutProvider.PositionValueMapper() {
                    @Override
                    public Object map(int position) {
                        // 返回值的映射
                        return position == 0 ? null : categories.get(position - 1).id;
                    }
                });
            }
        });

        // 配置动态验证器
        FormItemEntity type = formManager.findFormEntityByKey("type");
        type.validator = new Validator<SelectorLayoutProvider>() {
            @Override
            public void onValidateFailed(FormItemEntity entity, final SelectorLayoutProvider provider) {
                provider.getSpSelector().requestFocus();
                Toast.makeText(MainActivity.this, "请选择分类", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void validate(Object result, ValidateCallback callback) {
                callback.handleResult(selector.getSpSelector().getSelectedItemPosition() != 0);
            }
        };
    }

    private void asyncFetchCategories(Callback<List<Category>> categoriesCallback) {
        List<Category> categories = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Category category = new Category();
            category.id = String.valueOf(i);
            category.name = "分类" + (i + 1);
            categories.add(category);
        }

        categoriesCallback.call(categories);
    }

    private void mockSubmit(final FormManager manager) {
        manager.validate(new Validator.ValidateCallback() {
            @Override
            public void handleResult(boolean validateResult) {
                if (validateResult) {
                    Map result = manager.getResult();

                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage(String.valueOf(result))
                            .setPositiveButton("确定", null)
                            .show();
                }
            }
        });
    }

    private void initView() {
        mFormContainer = findViewById(R.id.form);
        mBtnSubmit = findViewById(R.id.btn_submit);
    }

    interface Callback<T> {
        void call(T t);
    }
}
