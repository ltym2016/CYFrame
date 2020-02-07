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
import com.caiyu.lib_photo.adapter.PreviewPhotoAdapter;
import com.caiyu.lib_photo.adapter.PreviewPhotoRVAdapter;
import com.caiyu.lib_photo.entity.SelectImageEntity;
import com.caiyu.lib_photo.view.MultiTouchViewPager;
import com.samluys.jutils.StringUtils;
import com.wangjing.recyclerview_drag.DragRecyclerView;
import com.wangjing.recyclerview_drag.touch.OnItemMoveListener;
import com.wangjing.recyclerview_drag.touch.OnItemStateChangedListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreviewPhotoActivity extends AppCompatActivity {

    RelativeLayout toolbar;
    RelativeLayout rl_finish;
    Button btn_commit;
    MultiTouchViewPager viewpager;
    TextView tv_current_select;
    RelativeLayout rel_select;
    CheckBox cb_seclect;
    DragRecyclerView recyclerView;
    LinearLayout ll_bottom;

    private PreviewPhotoAdapter adapter;
    private PreviewPhotoRVAdapter previewPhotoRVAdapter;

    private List<String> infos;
    private List<SelectImageEntity> mSelectImageEntites = new ArrayList<>();
    private int max_size;
    private int select_size;
    private int selectPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_photo);
        initViews();
        initListeners();
        setRecyclerView();
        setViewPager();
        setData(0);
    }

    /**
     * 初始化数据
     */
    private void initViews() {
         toolbar = findViewById(R.id.toolbar);
         rl_finish= findViewById(R.id.rl_finish);
         btn_commit= findViewById(R.id.btn_commit);
         viewpager= findViewById(R.id.viewpager);
         tv_current_select= findViewById(R.id.tv_current_select);
         rel_select= findViewById(R.id.rel_select);
         cb_seclect= findViewById(R.id.cb_seclect);
         recyclerView= findViewById(R.id.recyclerView);
         ll_bottom= findViewById(R.id.ll_bottom);



        try {
            infos = (List<String>) getIntent().getSerializableExtra("list");
            max_size = getIntent().getIntExtra("max_size", 0);
            select_size = getIntent().getIntExtra("select_size", 0);
            if (infos == null && infos.isEmpty()) {
                Toast.makeText(this, "图片不能为空哦", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        } catch (Exception e) {
            finish();
            return;
        }
        try {
            for (int i = 0; i < infos.size(); i++) {
                SelectImageEntity selectImageEntity = new SelectImageEntity();
                selectImageEntity.setChoose(true);
                selectImageEntity.setPath(infos.get(i));
                mSelectImageEntites.add(selectImageEntity);
            }
            tv_current_select.post(new Runnable() {
                @Override
                public void run() {
                    tv_current_select.setText("1/" + infos.size());
                }
            });
            setCommitText(mSelectImageEntites.size(), max_size);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setRecyclerView() {
        recyclerView.setLongPressDragEnabled(true);
        recyclerView.setOnItemMoveListener(new OnItemMoveListener() {
            @Override
            public boolean onItemMove(int fromPosition, int toPosition) {
                selectPosition = toPosition;
                // Item被拖拽时，交换数据，并更新adapter。
                Collections.swap(mSelectImageEntites, fromPosition, toPosition);
                previewPhotoRVAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onItemDismiss(int position) {

            }
        });
        recyclerView.setOnItemStateChangedListener(new OnItemStateChangedListener() {
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
                    // 状态：正在拖拽。
                } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
                    // 状态：滑动删除。
                } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
                    // 状态：手指松开。
                    adapter.notifyDataSetChanged();
                    viewpager.setCurrentItem(selectPosition);
                    setData(selectPosition);
                }
            }
        });

        previewPhotoRVAdapter = new PreviewPhotoRVAdapter(this, mSelectImageEntites);
        previewPhotoRVAdapter.setOnItemClickListener(new PreviewPhotoRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                viewpager.setCurrentItem(position);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(previewPhotoRVAdapter);
    }

    private void setViewPager() {
        adapter = new PreviewPhotoAdapter(this, PreviewPhotoActivity.this, mSelectImageEntites);
        adapter.setOnItemClickListener(new PreviewPhotoAdapter.OnItemClickListener() {
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
        viewpager.setCurrentItem(0);
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

    /**
     * 设置完成按钮的文案
     *
     * @param chooseSize 选择的数量
     * @param maxSize    最大数量
     */
    private void setCommitText(int chooseSize, int maxSize) {
        btn_commit.setText("完成(" + (chooseSize +select_size)+ "/" + maxSize + ")");
    }

    /**
     * 设置监听
     */
    private void initListeners() {
        rl_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        rel_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_seclect.isChecked()) {
                    cb_seclect.setChecked(false);
                    mSelectImageEntites.get(viewpager.getCurrentItem()).setChoose(false);
                } else {
                    cb_seclect.setChecked(true);
                    mSelectImageEntites.get(viewpager.getCurrentItem()).setChoose(true);
                }


                setCommitText(getSelectSize(mSelectImageEntites), max_size);
                previewPhotoRVAdapter.notifyDataSetChanged();
            }
        });
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    List<String> datas = formatChooseImage(mSelectImageEntites,
                            mSelectImageEntites.get(viewpager.getCurrentItem()).getPath());
                    intent.putExtra("list", (Serializable) datas);
                    intent.putExtra("should_commit", true);
                    PreviewPhotoActivity.this.setResult(RESULT_OK, intent);
                    PreviewPhotoActivity.this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    Intent intent = new Intent();
                    intent.putExtra("list", (Serializable) infos);
                    PreviewPhotoActivity.this.setResult(RESULT_OK, intent);
                    intent.putExtra("should_commit", true);
                    PreviewPhotoActivity.this.finish();
                }

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

    private List<String> formatChooseImage(List<SelectImageEntity> selectImageEntities, String defaultPath) {
        List<String> datas = new ArrayList<>();
        if (getSelectSize(selectImageEntities) <= 0) {
            if (!TextUtils.isEmpty(defaultPath)) {
                datas.add(defaultPath);
            }
        } else {
            for (int i = 0; i < selectImageEntities.size(); i++) {
                if (selectImageEntities.get(i).isChoose()) {
                    datas.add(selectImageEntities.get(i).getPath());
                }
            }
        }
        return datas;
    }

    private void setData(final int position) {
        if (mSelectImageEntites != null && !mSelectImageEntites.isEmpty()) {
            tv_current_select.post(new Runnable() {
                @Override
                public void run() {
                    tv_current_select.setText((position + 1) + "/" + mSelectImageEntites.size());
                }
            });
            if (mSelectImageEntites.get(position).isChoose()) {
                cb_seclect.setChecked(true);
            } else {
                cb_seclect.setChecked(false);
            }
            if (recyclerView.isComputingLayout()) {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        previewPhotoRVAdapter.setNowPosition(position);
                    }
                }, 100);
            } else {
                previewPhotoRVAdapter.setNowPosition(position);
            }
        }
    }


    private int getSelectSize(List<SelectImageEntity> selectImageEntities) {
        int size = 0;
        for (int i = 0; i < selectImageEntities.size(); i++) {
            if (selectImageEntities.get(i).isChoose()) {
                size++;
            }
        }
        return size;
    }

    @Override
    public void onBackPressed() {
        List<String> datas = formatChooseImage(mSelectImageEntites, "");
        Intent intent = new Intent();
        intent.putExtra("list", (Serializable) datas);
        intent.putExtra("should_commit", false);
        PreviewPhotoActivity.this.setResult(RESULT_OK, intent);
        finish();
    }
}
