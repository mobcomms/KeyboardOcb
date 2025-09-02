package com.enliple.keyboard.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enliple.keyboard.R;
import com.enliple.keyboard.activity.SettingSelectKeyboardActivity;
import com.enliple.keyboard.activity.SharedPreference;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;

import java.util.ArrayList;

/**
 * Created by shoppul-pc1 on 2017-03-08.
 */

public class KeyboardSelectAdapter extends RecyclerView.Adapter<KeyboardSelectAdapter.ViewHolder> {
    private ArrayList<KeyboardSelectData> mKbdData = new ArrayList<KeyboardSelectData>();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int mSelectItem = 0;
    private Context mContext = null;
    private SettingSelectKeyboardActivity mActivity;
    private int iWidth, iHeight;
    private boolean isFromStepLayer = true;

    // data is passed into the constructor
    public KeyboardSelectAdapter(Context context, ArrayList<KeyboardSelectData> imgUrl, int position, int width, Activity activity) {
        this.mInflater = LayoutInflater.from(context);
        this.mKbdData = imgUrl;
        this.mContext = context;
        this.mActivity = (SettingSelectKeyboardActivity) activity;
        isFromStepLayer = false;
        if (position > 0)
            this.mSelectItem = position;

        int unitWidth = width - Common.convertDpToPx(context, 80);
        iWidth = (int)(unitWidth / 2);
        iHeight = (int)((iWidth * 87) / 140);
        /*
        iWidth = SharedPreference.getInt(context, Key.KEY_KEYBOARD_SELECT_WIDTH_P);
        iHeight = SharedPreference.getInt(context, Key.KEY_KEYBOARD_SELECT_HEIGHT_P);
        if ( iWidth < 0 && iHeight < 0 ) {
            int unitWidth = width - Common.convertDpToPx(context, 80);
            iWidth = (int)(unitWidth / 2);
            iHeight = (int)((iWidth * 87) / 140);
            SharedPreference.setInt(context, Key.KEY_KEYBOARD_SELECT_WIDTH_P, iWidth);
            SharedPreference.setInt(context, Key.KEY_KEYBOARD_SELECT_HEIGHT_P, iHeight);
        }*/
    }

    // data is passed into the constructor
    public KeyboardSelectAdapter(Context context, ArrayList<KeyboardSelectData> imgUrl, int position, int width) {
        this.mInflater = LayoutInflater.from(context);
        this.mKbdData = imgUrl;
        this.mContext = context;
        if (position > 0)
            this.mSelectItem = position;

        int unitWidth = width - Common.convertDpToPx(context, 80);
        iWidth = (int)(unitWidth / 2);
        iHeight = (int)((iWidth * 87) / 140);
        /*
        iWidth = SharedPreference.getInt(context, Key.KEY_KEYBOARD_SELECT_WIDTH_P);
        iHeight = SharedPreference.getInt(context, Key.KEY_KEYBOARD_SELECT_HEIGHT_P);
        if ( iWidth < 0 && iHeight < 0 ) {
            int unitWidth = width - Common.convertDpToPx(context, 80);
            iWidth = (int)(unitWidth / 2);
            iHeight = (int)((iWidth * 87) / 140);
            SharedPreference.setInt(context, Key.KEY_KEYBOARD_SELECT_WIDTH_P, iWidth);
            SharedPreference.setInt(context, Key.KEY_KEYBOARD_SELECT_HEIGHT_P, iHeight);
        }
         */
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.aikbd_keyboard_cell_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //String animal = mbgImgUrl[position];
        //holder.myTextView.setText(animal);

        holder.mTitleTv.setText(mKbdData.get(position).kbd_title);
        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) holder.iContainer.getLayoutParams();
        param.width = iWidth;
        param.height = iHeight;
        holder.iContainer.setLayoutParams(param);

        RelativeLayout.LayoutParams iParam = (RelativeLayout.LayoutParams) holder.mBGIv.getLayoutParams();
        iParam.width = iWidth;
        iParam.height = iHeight;
        holder.mBGIv.setLayoutParams(iParam);

        LogPrint.d("iWidth :: " + iWidth + " , iHeight :: " + iHeight);

        if ( mKbdData.get(position).kbd_resourceId > 0 )
            holder.mBGIv.setBackgroundResource(mKbdData.get(position).kbd_resourceId);

        /**
        RelativeLayout.LayoutParams bParam = (RelativeLayout.LayoutParams) holder.mBackgrpund.getLayoutParams();
        bParam.width = iWidth;
        bParam.height = iHeight;
        holder.mBackgrpund.setLayoutParams(bParam);
**/
        if ( !TextUtils.isEmpty(holder.mTitleTv.getText().toString()) ) {
            if (mSelectItem == position) {
                holder.mBackgrpund.setVisibility(View.VISIBLE);
            } else {
                holder.mBackgrpund.setVisibility(View.GONE);
            }
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !isFromStepLayer ) {
                    if (mActivity.getKeyboardVibility() != View.VISIBLE ) {
                        if ( !TextUtils.isEmpty(holder.mTitleTv.getText().toString()) ) {
                            notifyItemChanged(mSelectItem);
                            mSelectItem = position;
                            notifyItemChanged(mSelectItem);
                            if (mClickListener != null) {
                                mClickListener.onItemClick(view, position);
                            }
                        }
                    } else {
                        mActivity.goneKeyboard();
                    }
                } else {
                    if ( !TextUtils.isEmpty(holder.mTitleTv.getText().toString()) ) {
                        notifyItemChanged(mSelectItem);
                        mSelectItem = position;
                        notifyItemChanged(mSelectItem);
                        if (mClickListener != null) {
                            mClickListener.onItemClick(view, position);
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mKbdData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
//        public ImageView mCheckIv;
        public ImageView mBGIv;
        public TextView mTitleTv;
        public RelativeLayout mBackgrpund;
        public RelativeLayout iContainer;
        public ViewHolder(View itemView) {
            super(itemView);
//            mCheckIv = (ImageView) itemView.findViewById(R.id.kbd_sel);
            mBGIv = (ImageView) itemView.findViewById(R.id.kbd_bg);
            mTitleTv = (TextView) itemView.findViewById(R.id.kbd_title);
            mBackgrpund = (RelativeLayout) itemView.findViewById(R.id.background);
            iContainer = (RelativeLayout) itemView.findViewById(R.id.iContainer);
        }
/**
        @Override
        public void onClick(View view) {
            if (mActivity.getKeyboardVibility() != View.VISIBLE ) {
                if ( !TextUtils.isEmpty(mTitleTv.getText().toString()) ) {
                    notifyItemChanged(mSelectItem);
                    mSelectItem = getLayoutPosition();
                    notifyItemChanged(mSelectItem);
                    if (mClickListener != null) {
                        mClickListener.onItemClick(view, getAdapterPosition());
                    }
                }

            } else {
                mActivity.goneKeyboard();
            }
        }**/
    }

    // convenience aikbd_method for getting data at click position
    public KeyboardSelectData getItem(int id) {
        return mKbdData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this aikbd_method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
