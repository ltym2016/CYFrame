package com.caiyu.lib_photo.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caiyu.lib_photo.R;
import com.caiyu.lib_photo.adapter.FilePhotoSeeSelectedAdapter;
import com.caiyu.lib_photo.adapter.FilePhotoSeeSelectedRVAdapter;
import com.caiyu.lib_photo.view.MultiTouchViewPager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FilePhotoSeeSelectedActivity extends AppCompatActivity {

    RelativeLayout toolbar;
    RelativeLayout rl_finish;
    Button btn_commit;
    MultiTouchViewPager viewpager;
    TextView tv_current_select;
    RelativeLayout rel_select;
    CheckBox cb_seclect;

    RecyclerView recyclerView;
    LinearLayout ll_bottom;


    private static final String MAX_SIZE = "max_size";
    private int maxSize = 9;
    private int position = 0;
    private String dirpath;
    private List<String> infos;
    private FilePhotoSeeSelectedAdapter adapter;
    private FilePhotoSeeSelectedRVAdapter filePhotoSeeSelected_recyclerView_adapter;

    private List<String> mSelectImage;
    private int selectSize;
    private String nowfilePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_file_photo_see_selected);
        position = getIntent().getIntExtra("position", 0);
        selectSize = getIntent().getIntExtra("select_size", 0);
        infos = PhotoActivity.getImagePathInPhone();
        dirpath = getIntent().getStringExtra("dirpath");
        if (TextUtils.isEmpty(dirpath)) {
            dirpath = "";
        }
        mSelectImage = getIntent().getStringArrayListExtra("selectimage");
        maxSize = getIntent().getIntExtra(MAX_SIZE, 9);
        if (mSelectImage == null) {
            mSelectImage = new ArrayList<>();
        }
        initViews();
        setRecyclerView();
        setViewPager();
        setData(position);
    }

    /**
     * 设置recyclerview数据
     */
    private void setRecyclerView() {
        filePhotoSeeSelected_recyclerView_adapter = new FilePhotoSeeSelectedRVAdapter(this, mSelectImage);
        filePhotoSeeSelected_recyclerView_adapter.setOnItemClickListener(new FilePhotoSeeSelectedRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String path = mSelectImage.get(position);
                if (infos != null && !infos.isEmpty()) {
                    for (int i = 0; i < infos.size(); i++) {
                        if (path.contains(infos.get(i))) {
                            viewpager.setCurrentItem(i);
                        }
                    }
                }
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(filePhotoSeeSelected_recyclerView_adapter);

    }


    private void initViews() {

        toolbar = findViewById(R.id.toolbar);
        rl_finish = findViewById(R.id.rl_finish);
        btn_commit = findViewById(R.id.btn_commit);
        viewpager = findViewById(R.id.viewpager);
        tv_current_select = findViewById(R.id.tv_current_select);
        rel_select = findViewById(R.id.rel_select);
        cb_seclect = findViewById(R.id.cb_seclect);

        recyclerView = findViewById(R.id.recyclerView);
        ll_bottom = findViewById(R.id.ll_bottom);

        setCommitText(mSelectImage.size(), maxSize);
        rl_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("close", false);
                intent.putExtra("simage", (Serializable) mSelectImage);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        rel_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_seclect.isChecked()) {
                    cb_seclect.setChecked(false);
                    mSelectImage.remove(nowfilePath);
                } else {
                    if (mSelectImage.size() >= maxSize) {
                        Toast.makeText(FilePhotoSeeSelectedActivity.this, getResources().getString(R.string.add_image_tips, maxSize+""), Toast.LENGTH_SHORT).show();
                    } else {
                        cb_seclect.setChecked(true);
                        mSelectImage.add(nowfilePath);
                    }
                }
                setCommitText(mSelectImage.size(), maxSize);
                filePhotoSeeSelected_recyclerView_adapter.notifyDataSetChanged();
            }
        });
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.putExtra("close", true);
                intent.putExtra("simage", (Serializable) mSelectImage);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setData(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 设置完成按钮的文案
     *
     * @param chooseSize 选择的数量
     * @param maxSize    最大数量
     */
    private void setCommitText(int chooseSize, int maxSize) {
        btn_commit.setText("完成(" + (chooseSize+selectSize) + "/" + maxSize + ")");
    }

    private void setViewPager() {
        adapter = new FilePhotoSeeSelectedAdapter(this, FilePhotoSeeSelectedActivity.this, infos, dirpath);
        adapter.setOnItemClickListener(new FilePhotoSeeSelectedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (toolbar.getTranslationY() < 0) {
                    showTop();
                } else {
                    hideTop();
                }
            }
        });
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(position);
    }

    private void hideTop() {
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                toolbar.animate().translationY(-toolbar.getHeight()).setDuration(300).start();
            }
        });
        ll_bottom.post(new Runnable() {
            @Override
            public void run() {
                ll_bottom.animate().setDuration(300).alpha(0).start();
            }
        });

    }

    private void showTop() {
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                toolbar.setTranslationY(-toolbar.getHeight());
                toolbar.animate().translationY(0).setDuration(300).start();
            }
        });
        ll_bottom.post(new Runnable() {
            @Override
            public void run() {
                ll_bottom.animate().setDuration(300).alpha(1).start();
            }
        });
    }


    private void setData(final int position) {
        if (infos != null && !infos.isEmpty()) {
            if (dirpath.equals("allimgs")) {
                nowfilePath = "file://" + infos.get(position);
            } else {
                nowfilePath = "file://" + dirpath + "/" + infos.get(position);
            }
            tv_current_select.post(new Runnable() {
                @Override
                public void run() {
                    tv_current_select.setText((position + 1) + "/" + infos.size());
                }
            });
            if (mSelectImage.contains(nowfilePath)) {
                cb_seclect.setChecked(true);
            } else {
                cb_seclect.setChecked(false);
            }
            filePhotoSeeSelected_recyclerView_adapter.setNowLookPath(nowfilePath);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("close", false);
        intent.putExtra("simage", (Serializable) mSelectImage);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PhotoActivity.setImagePathInPhone(new ArrayList<String>());
    }
}
