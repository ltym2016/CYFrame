package com.caiyu.lib_base.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


/**
 * @author luys
 * @describe 通用弹窗Dialog
 * @date 2019/6/4
 * @email samluys@foxmail.com
 */
public class CommonDialog extends Dialog {

	private Context mContext;
	private View mRootView;
	private boolean cancelTouchOut;
	private boolean cancelAble;
	private EditText inputContent;


	public CommonDialog(Builder builder) {
		super(builder.context);
		mRootView = builder.view;
		cancelTouchOut = builder.cancelTouchOut;
		mContext = builder.context;
		cancelAble=builder.cancelAble;
		inputContent = builder.inputContent;
	}

	public CommonDialog(Builder builder, int themeResId) {
		super(builder.context, themeResId);
		mRootView = builder.view;
		cancelTouchOut = builder.cancelTouchOut;
		mContext = builder.context;
		cancelAble= builder.cancelAble;
		inputContent = builder.inputContent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(mRootView);
		setCanceledOnTouchOutside(cancelTouchOut);
		setCancelable(cancelAble);//设置点击dialog外部无效，不关闭dialog
	}

	public EditText getEditText() {
		return inputContent;
	}

	public static class Builder {

		private Context context;
		private View view;
		private boolean cancelTouchOut;
		private boolean cancelAble;
		private int resStyle;
		private EditText inputContent;

		public Builder(Context context) {
			this.context = context;
		}


		/**
		 * 设置布局文件
		 * @param resview
		 * @return
		 */
		public Builder view(int resview) {
			view = LayoutInflater.from(context).inflate(resview, null);
			return this;
		}

		/**
		 * 设置样式
		 * @param style
		 * @return
		 */
		public Builder setStyle(int style) {
			this.resStyle = style;
			return this;
		}

		/**
		 * 设置是否可以点击其他地方消失
		 * @param cancelTouchOut
		 * @return
		 */
		public Builder setCancelTouchout(boolean cancelTouchOut) {
			this.cancelTouchOut = cancelTouchOut;
			return this;
		}

		/**
		 * 设置点击返回键是否消失
		 * @param cancel
		 * @return
		 */
		public Builder setCancelAble(boolean cancel) {
			this.cancelAble = cancel;
			return this;
		}


		/**
		 * 设置标题
		 * @param resTitle
		 * @param title
		 * @return
		 */
		public Builder setTitle(int resTitle, String title) {
			TextView tvTitle = view.findViewById(resTitle);
			tvTitle.setText(title);
			return this;
		}

		/**
		 * 设置内容
		 * @param resMessage
		 * @param message
		 * @return
		 */
		public Builder setContent(int resMessage, String message) {
			TextView tvContent = view.findViewById(resMessage);
			if(TextUtils.isEmpty(message)) {
				tvContent.setVisibility(View.GONE);
			} else {
				tvContent.setVisibility(View.VISIBLE);
			}
			tvContent.setText(message);
			tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
			return this;
		}

		/**
		 * 设置输入框内容
		 * @param resMessage
		 * @param message
		 * @return
		 */
		public Builder setInputContent(int resMessage, String message, String hint) {
			inputContent = view.findViewById(resMessage);
			if(TextUtils.isEmpty(message) && TextUtils.isEmpty(hint)) {
				inputContent.setVisibility(View.GONE);
			} else {
				inputContent.setVisibility(View.VISIBLE);
				inputContent.setHint(hint);
				inputContent.setText(message);
			}
			return this;
		}


		/**
		 * 设置取消按钮
		 * @param resCancel
		 * @param text
		 * @param listener
		 * @return
		 */
		public Builder setCancel(int resCancel, String text, View.OnClickListener listener) {
			TextView cancel = view.findViewById(resCancel);
			if (TextUtils.isEmpty(text)) {
				cancel.setVisibility(View.GONE);
			}
			cancel.setText(text);
			cancel.setOnClickListener(listener);
			return this;
		}

		/**
		 * 设置确认按钮
		 * @param resConfirm
		 * @param text
		 * @param listener
		 * @return
		 */
		public Builder setConfirm(int resConfirm, String text, View.OnClickListener listener) {
			TextView confirm = view.findViewById(resConfirm);
			if (TextUtils.isEmpty(text)) {
				confirm.setVisibility(View.GONE);
			}
			confirm.setText(text);
			confirm.setOnClickListener(listener);
			return this;
		}


		public CommonDialog build() {
			if (resStyle != 0) {
				return  new CommonDialog(this,resStyle);
			} else {
				return new CommonDialog(this);
			}
		}
	}

}
