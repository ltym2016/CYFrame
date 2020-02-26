package com.caiyu.cyframe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.caiyu.cyframe.api.ApiService;
import com.caiyu.lib_base.http.BaseResponse;
import com.caiyu.lib_base.http.RetrofitHelper;
import com.samluys.jutils.log.LogUtils;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        Observable<BaseResponse> api = RetrofitHelper.getApiService(ApiService.class).appInit();
//        RetrofitHelper.subscript(api, new Consumer<BaseResponse>() {
//            @Override
//            public void accept(BaseResponse baseResponse) throws Exception {
//                if (baseResponse != null) {
//                    LogUtils.e(baseResponse.toString());
//                }
//            }
//        });

        findViewById(R.id.btn_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable<BaseResponse> api = RetrofitHelper.getApiService(ApiService.class).appInit(1, 10);
                RetrofitHelper.subscript(api, new Consumer<BaseResponse>() {
                    @Override
                    public void accept(BaseResponse baseResponse) throws Exception {
                        if (baseResponse != null) {
                            LogUtils.e(baseResponse.toString());
                        }
                    }
                });
            }
        });
    }
}
