package com.caiyu.lib_base.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.caiyu.lib_base.constants.Constants;
import com.caiyu.lib_base.utils.FileUtils;
import com.caiyu.lib_base.view.LoadingDialog;
import com.caiyu.lib_base.view.LoadingView;
import com.samluys.statusbar.StatusBarUtils;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;


/**
 * @author luys
 * @describe
 * @date 2019/4/17
 * @email ltym_lys@126.com
 */
public abstract class BaseActivity<V extends ViewDataBinding, VM extends BaseViewModel> extends RxAppCompatActivity implements IBaseView {
    protected V binding;
    protected VM viewModel;
    private int viewModelId;
    private LoadingDialog tipDialog;
    public String tempPath;
    protected LoadingView.LoadingHelper mHelper;
    protected int mToolbarHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tempPath = FileUtils.getLocalCacheFilePath(Environment.DIRECTORY_PICTURES+ File.separator + Constants.TEMP);

        StatusBarUtils.transparencyBar(this, false);

        //页面接受的参数方法
        initParam();
        //初始化Databinding和ViewModel方法
        initViewDataBinding();
        //ViewModel与View的契约事件回调逻辑
        doViewLiveDataCallBack();
        //页面数据初始化方法
        initData();
        //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        FrameLayout rootView = findViewById(android.R.id.content);
        mHelper = LoadingView.getInstance()
                .wrap(rootView)
                .toobarHeight(mToolbarHeight)
                .withRetry(new Runnable() {
                    @Override
                    public void run() {
                        onLoadRetry();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除ViewModel生命周期感应
        getLifecycle().removeObserver(viewModel);
        if(binding != null){
            binding.unbind();
        }
    }

    /**
     * 注入绑定
     */
    private void initViewDataBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId());

        viewModelId = initVariableId();
        Class modelClass;
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
        } else {
            //如果没有指定泛型参数，则默认使用BaseViewModel
            modelClass = BaseViewModel.class;
        }
        viewModel = (VM) createViewModel(this, modelClass);
        //关联ViewModel
        binding.setVariable(viewModelId, viewModel);
        //让ViewModel拥有View的生命周期感应
        getLifecycle().addObserver(viewModel);
        //注入RxLifecycle生命周期
        viewModel.injectLifecycleProvider(this);
    }


    /**
     * 注册ViewModel与View的契约UI回调事件
     */
    private void doViewLiveDataCallBack() {

        //加载对话框显示
        viewModel.getLiveData().getShowDialogEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String title) {
                showDialog(title);
            }
        });
        //加载对话框消失
        viewModel.getLiveData().getDismissDialogEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                dismissDialog();
            }
        });

        //跳入新页面
        viewModel.getLiveData().getStartActivityEvent().observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(@Nullable Map<String, Object> params) {
                Class<?> clz = (Class<?>) params.get(BaseViewModel.ParameterField.CLASS);
                Bundle bundle = (Bundle) params.get(BaseViewModel.ParameterField.BUNDLE);
                startActivity(clz, bundle);
            }
        });
        //关闭界面
        viewModel.getLiveData().getFinishEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                finish();
            }
        });
        //关闭上一层
        viewModel.getLiveData().getOnBackPressedEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                onBackPressed();
            }
        });

        //跳入ContainerActivity
        viewModel.getLiveData().getStartContainerActivityEvent().observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(@Nullable Map<String, Object> params) {
                String canonicalName = (String) params.get(BaseViewModel.ParameterField.CANONICAL_NAME);
                Bundle bundle = (Bundle) params.get(BaseViewModel.ParameterField.BUNDLE);
                startContainerActivity(canonicalName, bundle);
            }
        });
    }

    public void showDialog(String title) {
        if (tipDialog == null) {
            tipDialog = new LoadingDialog(this);
        }

        tipDialog.showDialog(title);
    }

    public void dismissDialog() {
        if (tipDialog != null && tipDialog.isShowing()) {
            tipDialog.dismiss();
        }
    }


    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(this, clz));
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     */
    public void startContainerActivity(String canonicalName) {
        startContainerActivity(canonicalName, null);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    public void startContainerActivity(String canonicalName, Bundle bundle) {
        Intent intent = new Intent(this, ContainerActivity.class);
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName);
        if (bundle != null) {
            intent.putExtra(ContainerActivity.BUNDLE, bundle);
        }
        startActivity(intent);
    }


    /**
     * 初始化参数
     **/
    @Override
    public void initParam() {
        StatusBarUtils.StatusBarIconDark(this);
        setOrientation();
    }

    protected abstract void setOrientation();

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    public abstract int getLayoutId();

    /**
     * 初始化ViewModel的id
     *
     * @return BR的id
     */
    public abstract int initVariableId();

    @Override
    public void initData() {

    }

    @Override
    public void initViewObservable() {

    }

    /**
     * 创建ViewModel
     *
     * @param cls
     * @param <T>
     * @return
     */
    public <T extends ViewModel> T createViewModel(FragmentActivity activity, Class<T> cls) {
        return ViewModelProviders.of(activity).get(cls);
    }

    protected void onLoadRetry() {
        // override this method in subclass to do retry task
    }
}
