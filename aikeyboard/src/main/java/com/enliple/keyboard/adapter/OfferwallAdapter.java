package com.enliple.keyboard.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enliple.keyboard.R;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.imgmodule.ImageModule;
import com.enliple.keyboard.imgmodule.load.MultiTransformation;
import com.enliple.keyboard.imgmodule.load.resource.bitmap.CenterCrop;
import com.enliple.keyboard.imgmodule.load.resource.bitmap.RoundedCorners;
import com.enliple.keyboard.imgmodule.request.RequestOptions;
import com.enliple.keyboard.models.OfferwallCategoryData;
import com.enliple.keyboard.models.OfferwallData;
import com.enliple.keyboard.ui.common.LogPrint;

import java.util.ArrayList;

public class OfferwallAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int TAB_ALL = 0;
    private static final int TAB_JOIN = 1;
    private Listener listener;
    public ArrayList<OfferwallData> items;
    public ArrayList<OfferwallCategoryData> categoryItems;
    public double total_user_point;
    private Context context;
    private MultiTransformation multiOption;
    private int selectedTab = TAB_ALL;

    public interface Listener {
        void onItemClicked(OfferwallData data);
        void onCategoryClilcked(OfferwallCategoryData data);
    }

    public OfferwallAdapter(Context context, ArrayList<OfferwallCategoryData> categoryItems, Listener listener) {
        this.context = context;
        this.listener = listener;
        this.categoryItems = categoryItems;
        multiOption = new MultiTransformation(
                new CenterCrop(),
                new RoundedCorners(Common.convertDpToPx(context, 15))
        );
    }

    public void setItems(ArrayList<OfferwallData> its, double tup) {
        items = new ArrayList<>();
        items.addAll(its);
        total_user_point = tup;
        LogPrint.d("items size :: " + items.size());
        notifyDataSetChanged();
    }

    public void addItems(ArrayList<OfferwallData> its, double tup) {
        total_user_point = tup;
        if ( items != null ) {
            items.addAll(its);
            notifyDataSetChanged();
        }
    }

    public void deleteJoinedMission(String joined_mission_id) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup a_viewGroup, int a_viewType) {
        View view = LayoutInflater.from(a_viewGroup.getContext()).inflate(a_viewType, a_viewGroup, false);

        final RecyclerView.ViewHolder viewHolder;
        if (a_viewType == OfferwallCategoryHeaderViewHolder.VIEW_TYPE) {
            viewHolder = new OfferwallCategoryHeaderViewHolder(view, categoryItems, new OfferwallCategoryHeaderViewHolder.Listener() {
                @Override
                public void onCategoryClicked(OfferwallCategoryData data) {
                    if ( listener != null ) {
                        listener.onCategoryClilcked(data);
                    }
                }
            });
        } else {
            viewHolder = new OfferwallItemViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder a_holder, int a_position) {
        LogPrint.d("onBindViewHolder a_position :: " + a_position);
        if (a_holder instanceof OfferwallCategoryHeaderViewHolder) {
            OfferwallCategoryHeaderViewHolder viewHolder = (OfferwallCategoryHeaderViewHolder) a_holder;
            viewHolder.txt_possible_point.setText(Common.putCommaWithoutDot(total_user_point + ""));
/**
            LogPrint.d("selectedTab :: " + selectedTab);
            if ( selectedTab == TAB_ALL ) {
                headerViewHolder.text_all.setTypeface(headerViewHolder.text_all.getTypeface(), Typeface.BOLD);
                headerViewHolder.text_all.setTextColor(Color.parseColor("#000000"));
                headerViewHolder.line_all.setVisibility(View.VISIBLE);
                headerViewHolder.text_join.setTypeface(headerViewHolder.text_join.getTypeface(), Typeface.NORMAL);
                headerViewHolder.text_join.setTextColor(Color.parseColor("#666666"));
                headerViewHolder.line_join.setVisibility(View.GONE);
            } else {
                headerViewHolder.text_join.setTypeface(headerViewHolder.text_join.getTypeface(), Typeface.BOLD);
                headerViewHolder.text_join.setTextColor(Color.parseColor("#000000"));
                headerViewHolder.line_join.setVisibility(View.VISIBLE);
                headerViewHolder.text_all.setTypeface(headerViewHolder.text_all.getTypeface(), Typeface.NORMAL);
                headerViewHolder.text_all.setTextColor(Color.parseColor("#666666"));
                headerViewHolder.line_all.setVisibility(View.GONE);
            }

            headerViewHolder.txt_possible_point.setText(Common.putCommaWithoutDot(total_user_point + ""));
            headerViewHolder.btn_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogPrint.d("all clicked :: " + selectedTab + " , a_position :: " + a_position);
                    if ( selectedTab != TAB_ALL ) {
                        if ( listener != null )
                            listener.onCategoryClilcked(TAB_ALL);
                        headerViewHolder.text_all.setTypeface(headerViewHolder.text_all.getTypeface(), Typeface.BOLD);
                        headerViewHolder.text_all.setTextColor(Color.parseColor("#000000"));
                        headerViewHolder.line_all.setVisibility(View.VISIBLE);
                        headerViewHolder.text_join.setTypeface(headerViewHolder.text_join.getTypeface(), Typeface.NORMAL);
                        headerViewHolder.text_join.setTextColor(Color.parseColor("#666666"));
                        headerViewHolder.line_join.setVisibility(View.GONE);
                        selectedTab = TAB_ALL;
                        notifyItemChanged(a_position);
                    }
                }
            });

            headerViewHolder.btn_join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogPrint.d("join clicked :: " + selectedTab + " , a_position :: " + a_position);
                    if ( selectedTab != TAB_JOIN ) {
                        if ( listener != null )
                            listener.onCategoryClilcked(TAB_JOIN);
                        headerViewHolder.text_join.setTypeface(headerViewHolder.text_join.getTypeface(), Typeface.BOLD);
                        headerViewHolder.text_join.setTextColor(Color.parseColor("#000000"));
                        headerViewHolder.line_join.setVisibility(View.VISIBLE);
                        headerViewHolder.text_all.setTypeface(headerViewHolder.text_all.getTypeface(), Typeface.NORMAL);
                        headerViewHolder.text_all.setTextColor(Color.parseColor("#666666"));
                        headerViewHolder.line_all.setVisibility(View.GONE);
                        selectedTab = TAB_JOIN;
                        notifyItemChanged(a_position);
                    }
                }
            });
 **/
        } else {
            final OfferwallData item = items.get(a_position - 1);
            if ( item != null ) {
                OfferwallItemViewHolder viewHolder = (OfferwallItemViewHolder) a_holder;
                String img_path = item.getThumb_img();
                try {
                    ImageModule.with(context)
                            .load(img_path)
                            .placeholder(R.drawable.aikbd_ppz_app_no_img)
                            .apply(RequestOptions.bitmapTransform(multiOption))
                            .into(viewHolder.offerwall_image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LogPrint.d("mission_class :: " + item.getMission_class());
                LogPrint.d("shop :: " + item.getAdver_name());
                LogPrint.d("keyword :: " + item.getKeyword());
                LogPrint.d("img :: " + item.getThumb_img());
                int iPos = a_position - 1;
                LogPrint.d("a_position - 1 :: " + iPos + " , items size :: " + items.size());
                LogPrint.d("****************************");
                viewHolder.offerwall_title.setText(item.getAdver_name());
                viewHolder.offerwall_desc.setText(item.getKeyword());
                try {
                    double dPoint = item.getUser_point();
                    int iPoint = (int)dPoint;
                    viewHolder.offerwall_point.setText(iPoint + "P");
                } catch (Exception e ) {
                    viewHolder.offerwall_point.setText(item.getUser_point() + "P");
                    e.printStackTrace();
                }

                String cnt = item.getDaily_participation_cnt() + "";
                String total = item.getDaily_participation() + "";
                String str = cnt + "/" + total;
                SpannableStringBuilder builder = new SpannableStringBuilder(str);
                builder.setSpan(new ForegroundColorSpan(Color.parseColor("#fe0955")), 0, cnt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder.offerwall_join_count.setText(builder);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( listener != null ) {
                            LogPrint.d("itemview click listener not null");
                            if ( item == null )
                                LogPrint.d("item null");
                            else
                                LogPrint.d("item not null");
                            listener.onItemClicked(item);
                        } else {
                            LogPrint.d("itemview click listener null");
                        }
                    }
                });
                if ( a_position == items.size() ) {
                    viewHolder.bot_layer.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.bot_layer.setVisibility(View.GONE);
                }
            }
        }
    }

    public void removeItems() {
        if ( items != null ) {
            LogPrint.d("items remove all");
            items.clear();
            items = new ArrayList<>();
        }
    }

    @Override
    public int getItemCount() {
        if ( items != null )
            return items.size() + 1;
        else
            return 1;
    }

    @Override
    public int getItemViewType(int a_position) {
        if (a_position == 0) {
            return OfferwallCategoryHeaderViewHolder.VIEW_TYPE;
        } else {
            return OfferwallItemViewHolder.VIEW_TYPE;
        }
    }
}
