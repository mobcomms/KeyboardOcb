package com.enliple.keyboard.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.enliple.keyboard.Cipher.E_Cipher;
import com.enliple.keyboard.R;
import com.enliple.keyboard.activity.SharedPreference;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.common.SearchModel;
import com.enliple.keyboard.common.ShoppingCommonModel;
import com.enliple.keyboard.imageloader.ImageLoader;
import com.enliple.keyboard.imageloader.ImageUtils;
import com.enliple.keyboard.ui.common.Key;
import com.rake.android.rkmetrics.RakeAPI;
import com.skplanet.pdp.sentinel.shuttle.OCBLogSentinelShuttle;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OCBShoppingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ONE_HOUR = 60 * 60 * 1000;
    private static final int ONE_MIN = 60 * 1000;
    private static final int ONE_SEC = 1000;
    private static final String TYPE_ORABANG = "recommend_orabang";
    private static final String TYPE_TIMEDEAL = "recommend_one_item_time";
    private Context context;
    private int totalCount;
    private String headerTitle;
    private String searchWord;
    private ArrayList<ShoppingCommonModel> items = new ArrayList<>();
    private CountDownTimer olabangTimer, timedealTimer;
    RakeAPI rake;
    public OCBShoppingAdapter(Context context, RakeAPI rk) {
        this.context = context;
        rake = rk;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.aikbd_shopping_list_item, parent, false);
        return new ShoppingViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        bindShoppingHolder((ShoppingViewHolder) holder, position);
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        } else {
            return 0;
        }
    }

    public void stopTimer() {
        if ( olabangTimer != null )
            olabangTimer.cancel();
        if ( timedealTimer != null )
            timedealTimer.cancel();
    }

    private void bindShoppingHolder(ShoppingViewHolder holder, int position) {
        ShoppingCommonModel item = items.get(position);
        int tPosition = items.size() - 1;
        if ( position == 0 ) {
            holder.headerLayer.setVisibility(View.VISIBLE);
            holder.divider.setVisibility(View.VISIBLE);
            holder.txtMore.setVisibility(View.GONE);
            String strTotal = "총 (" + items.size() + ")";
            holder.txtTotal.setText(strTotal);
        } else if ( position == items.size() - 1 ) {
            holder.headerLayer.setVisibility(View.GONE);
            if ( totalCount > items.size() ) {
                holder.txtMore.setVisibility(View.VISIBLE);
                holder.divider.setVisibility(View.VISIBLE);
                int moreCnt = totalCount - items.size();
                String strMore = "+" + moreCnt + "건 더보기";
                SpannableString content = new SpannableString(strMore);
                content.setSpan(new UnderlineSpan(), 0, strMore.length(), 0);
                holder.txtMore.setText(content);
            } else {
                holder.divider.setVisibility(View.GONE);
                holder.txtMore.setVisibility(View.GONE);
            }
        } else {
            holder.txtMore.setVisibility(View.GONE);
            holder.headerLayer.setVisibility(View.GONE);
            holder.divider.setVisibility(View.VISIBLE);
        }

        if (TYPE_ORABANG.equals(item.getType())) {
            holder.o_itemLayer.setVisibility(View.VISIBLE);
            holder.s_itemLayer.setVisibility(View.GONE);
            holder.t_itemLayer.setVisibility(View.GONE);

            long currentTime = System.currentTimeMillis();
            long startTime = item.getStartDate();
            long endTime = item.getEndDate();
            if ( currentTime >= startTime && currentTime < endTime ) {
                holder.live_layer.setVisibility(View.VISIBLE);
                holder.status_kor.setVisibility(View.VISIBLE);

                long st_time = endTime - currentTime;
                olabangTimer = new CountDownTimer(st_time, 1000) {
                    public void onTick(long millisUntilFinished) {
                        int hour = (int)(millisUntilFinished / ONE_HOUR);

                        long forMin = millisUntilFinished - (hour * ONE_HOUR);
                        int minute = (int)(forMin / ONE_MIN);

                        long forSec = millisUntilFinished - (hour * ONE_HOUR) - (minute * ONE_MIN);
                        int sec = (int)(forSec / ONE_SEC);
                        String sHour = "";
                        String sMin = "";
                        String sSec = "";
                        if ( hour >=10 ) sHour = "" + hour;
                        else sHour = "0" + hour;

                        if ( minute >= 10 ) sMin = "" + minute;
                        else sMin = "0" + minute;

                        if ( sec >= 10 ) sSec = "" + sec;
                        else sSec = "0" + sec;

                        holder.o_time.setText(sHour + ":" + sMin + ":" + sSec);
                    }
                    public void onFinish() {
                    }
                };
                olabangTimer.start();
            } else if ( startTime > currentTime ) {
                holder.live_layer.setVisibility(View.GONE);
                holder.status_kor.setVisibility(View.GONE);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date time = new Date();
                String today = format.format(time);
                String forDate = "";
                if ( today.equals(getTodayDate()) ) {
                    forDate = "오늘";
                } else if ( today.equals(getTomorrowDate()) ) {
                    forDate = "내일";
                }

                Date startDate = new Date(startTime);
                SimpleDateFormat dFormat = new SimpleDateFormat("a HH");
                String dTime = dFormat.format(startDate).toLowerCase();
                if ( dTime.contains("am") ) {
                    dTime.replaceAll("am", "오전");
                } else {
                    dTime.replaceAll("pm", "오후");
                }
                String str = forDate + " " + dTime;
                holder.o_time.setText(str);
            }
            holder.o_title.setText(item.getTitle());
        } else if ( TYPE_TIMEDEAL.equals(item.getType())) {
            holder.o_itemLayer.setVisibility(View.GONE);
            holder.s_itemLayer.setVisibility(View.GONE);
            holder.t_itemLayer.setVisibility(View.VISIBLE);
            try {
                ImageLoader.with(context)
                        .from(item.getImageUrl())
                        .noMemoryCache()
                        .noStorageCache()
                        .transform(ImageUtils.cropCenter())
                        .load(holder.t_image);
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.t_title.setText(item.getTitle());

            int iPrice = (int)item.getPrice();
            int iOriginalPrice = (int)item.getOriginalPrice();
            holder.t_price.setText(Common.putComma("" + iPrice));
            if ( iOriginalPrice <= 0 ) {
                holder.t_originPrice.setVisibility(View.INVISIBLE);
                holder.t_originPriceWon.setVisibility(View.INVISIBLE);
                holder.t_c_line.setVisibility(View.INVISIBLE);
            } else {
                holder.t_originPrice.setVisibility(View.VISIBLE);
                holder.t_originPriceWon.setVisibility(View.VISIBLE);
                holder.t_originPrice.setText(Common.putComma("" + iOriginalPrice));
                holder.t_c_line.setVisibility(View.VISIBLE);
            }
            holder.headerTitle.setText(headerTitle);
            holder.t_pointLayer.setVisibility(View.GONE);
            if ( !TextUtils.isEmpty(item.getSaveText()) ) {
                String point = item.getSaveText();
                point = point.toUpperCase();
                point = point.replaceAll("P", " ");
                String[] pointArr = point.split(" ");
                if ( pointArr != null && pointArr.length > 0 ) {
                    holder.t_pointLayer.setVisibility(View.VISIBLE);
                    String strPoint = pointArr[0];
                    holder.t_point.setText(strPoint);
                }
            }
            holder.t_itemLayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if ( !TextUtils.isEmpty(item.getLinkUrl()) ) {
                            setRake("/keyboard/search/results", "tap.product");
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getLinkUrl()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            long currentTime = System.currentTimeMillis();
            long endTime = item.getEndDate();
            long st_time = endTime - currentTime;
            timedealTimer = new CountDownTimer(st_time, 1000) {
                public void onTick(long millisUntilFinished) {
                    int hour = (int)(millisUntilFinished / ONE_HOUR);

                    long forMin = millisUntilFinished - (hour * ONE_HOUR);
                    int minute = (int)(forMin / ONE_MIN);

                    long forSec = millisUntilFinished - (hour * ONE_HOUR) - (minute * ONE_MIN);
                    int sec = (int)(forSec / ONE_SEC);
                    String sHour = "";
                    String sMin = "";
                    String sSec = "";
                    if ( hour >=10 ) sHour = "" + hour;
                    else sHour = "0" + hour;

                    if ( minute >= 10 ) sMin = "" + minute;
                    else sMin = "0" + minute;

                    if ( sec >= 10 ) sSec = "" + sec;
                    else sSec = "0" + sec;

                    holder.t_time.setText(sHour + ":" + sMin + ":" + sSec);
                }
                public void onFinish() {
                }
            };
            timedealTimer.start();
        } else if ( "homeShopping".equals(item.getMainType()) ) {
            holder.o_itemLayer.setVisibility(View.GONE);
            holder.s_itemLayer.setVisibility(View.VISIBLE);
            holder.t_itemLayer.setVisibility(View.GONE);
            try {
                ImageLoader.with(context).from(item.getImageUrl()).transform(ImageUtils.cropCenter()).load(holder.s_image);
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.s_title.setText(item.getTitle());

            int iPrice = (int)item.getPrice();
            int iOriginalPrice = (int)item.getOriginalPrice();
            holder.s_price.setText(Common.putComma("" + iPrice));
            if ( iOriginalPrice <= 0 ) {
                holder.s_originPrice.setVisibility(View.INVISIBLE);
                holder.s_originPriceWon.setVisibility(View.INVISIBLE);
                holder.s_c_line.setVisibility(View.INVISIBLE);
            } else {
                holder.s_originPrice.setVisibility(View.VISIBLE);
                holder.s_originPriceWon.setVisibility(View.VISIBLE);
                holder.s_originPrice.setText(Common.putComma("" + iOriginalPrice));
                holder.s_c_line.setVisibility(View.VISIBLE);
            }
            holder.headerTitle.setText(headerTitle);
            holder.s_pointLayer.setVisibility(View.GONE);
            if ( !TextUtils.isEmpty(item.getSaveText()) ) {
                String point = item.getSaveText();
                point = point.toUpperCase();
                if( point != null && !TextUtils.isEmpty(point) ) {
                    if ( point.contains("적립") ) {
                        point = point.replaceAll("적립", "");
                    }
                    if ( point != null && !TextUtils.isEmpty(point) ) {
                        holder.s_pointLayer.setVisibility(View.VISIBLE);
                        String strPoint = point;
                        holder.s_point.setText(strPoint);
                    }

                }
            }
            holder.s_itemLayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if ( !TextUtils.isEmpty(item.getLinkUrl()) ) {
                            setRake("/keyboard/search/results", "tap.product");
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getLinkUrl()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            holder.o_itemLayer.setVisibility(View.GONE);
            holder.s_itemLayer.setVisibility(View.VISIBLE);
            holder.t_itemLayer.setVisibility(View.GONE);
            try {
                ImageLoader.with(context).from(item.getImageUrl()).transform(ImageUtils.cropCenter()).load(holder.s_image);
            } catch(Exception e) {
                e.printStackTrace();
            }

            holder.s_title.setText(item.getTitle());

            int iPrice = (int)item.getPrice();
            int iOriginalPrice = (int)item.getOriginalPrice();
            holder.s_price.setText(Common.putComma("" + iPrice));
            if ( iOriginalPrice <= 0 ) {
                holder.s_originPrice.setVisibility(View.INVISIBLE);
                holder.s_originPriceWon.setVisibility(View.INVISIBLE);
                holder.s_c_line.setVisibility(View.INVISIBLE);
            } else {
                holder.s_originPrice.setVisibility(View.VISIBLE);
                holder.s_originPriceWon.setVisibility(View.VISIBLE);
                holder.s_originPrice.setText(Common.putComma("" + iOriginalPrice));
                holder.s_c_line.setVisibility(View.VISIBLE);
            }
            holder.headerTitle.setText(headerTitle);
            holder.s_pointLayer.setVisibility(View.GONE);
            if ( !TextUtils.isEmpty(item.getSaveText()) ) {
                String point = item.getSaveText();
                point = point.toUpperCase();
                point = point.replaceAll("P", " ");
                String[] pointArr = point.split(" ");
                if ( pointArr != null && pointArr.length > 0 ) {
                    holder.s_pointLayer.setVisibility(View.VISIBLE);
                    String strPoint = pointArr[0];
                    holder.s_point.setText(strPoint);
                }
            }
            holder.s_itemLayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if ( !TextUtils.isEmpty(item.getLinkUrl()) ) {
                            setRake("/keyboard/search/results", "tap.product");
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getLinkUrl()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        holder.txtMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력한 검색 키워드의 통합 검색 결과 화면으로 이동, 결과화면은 어디??
                try {
                    setRake("/keyboard/search/results", "bottom_tap.viewmore");
                    String encodedSearchWord = URLEncoder.encode(searchWord, "UTF-8");
                    String url = "ocbt://com.skmc.okcashbag.home_google/searchMain?keyword=" + encodedSearchWord;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setItems(ArrayList<ShoppingCommonModel> its, int totalCnt, String title, String sWord) {
        if ( items == null ) {
            items = new ArrayList<>();
        }

        items.addAll(its);
        this.totalCount = totalCnt;
        this.headerTitle = title;
        this.searchWord = sWord;
        notifyDataSetChanged();
    }

    public class ShoppingViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout headerLayer, s_pointLayer, t_pointLayer;
        public TextView txtTotal, s_title, s_price, s_originPrice, s_originPriceWon, s_point, t_title, t_price, t_originPrice, t_originPriceWon, t_point, t_time, txtMore, headerTitle, status_kor, o_time, o_title;
        public ImageView s_image, t_image, o_image;
        public View divider, t_c_line, s_c_line;
        public LinearLayout s_itemLayer, t_itemLayer, live_layer;
        public RelativeLayout o_itemLayer;
        public ShoppingViewHolder(Context context, View itemView) {
            super(itemView);

            headerLayer = itemView.findViewById(R.id.headerLayer);
            headerTitle = itemView.findViewById(R.id.headerTitle);
            divider = itemView.findViewById(R.id.divider);
            txtMore = itemView.findViewById(R.id.txtMore);
            txtTotal = itemView.findViewById(R.id.txtTotal);

            s_itemLayer = itemView.findViewById(R.id.s_itemLayer);
            s_pointLayer = itemView.findViewById(R.id.s_pointLayer);
            s_title = itemView.findViewById(R.id.s_title);
            s_price = itemView.findViewById(R.id.s_price);
            s_originPrice = itemView.findViewById(R.id.s_originPrice);
            s_originPriceWon = itemView.findViewById(R.id.s_originPriceWon);
            s_point = itemView.findViewById(R.id.s_point);
            s_image = itemView.findViewById(R.id.s_image);

            t_itemLayer = itemView.findViewById(R.id.t_itemLayer);
            t_time = itemView.findViewById(R.id.t_time);
            t_pointLayer = itemView.findViewById(R.id.t_pointLayer);
            t_title = itemView.findViewById(R.id.t_title);
            t_price = itemView.findViewById(R.id.t_price);
            t_originPrice = itemView.findViewById(R.id.t_originPrice);
            t_originPriceWon = itemView.findViewById(R.id.t_originPriceWon);
            t_point = itemView.findViewById(R.id.t_point);
            t_image = itemView.findViewById(R.id.t_image);

            status_kor = itemView.findViewById(R.id.status_kor);
            o_time = itemView.findViewById(R.id.o_time);
            o_title = itemView.findViewById(R.id.o_title);
            o_image = itemView.findViewById(R.id.o_image);
            live_layer = itemView.findViewById(R.id.live_layer);
            o_itemLayer = itemView.findViewById(R.id.o_itemLayer);

            t_c_line = itemView.findViewById(R.id.t_c_line);
            s_c_line = itemView.findViewById(R.id.s_c_line);
        }
    }

    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String date = sdf.format(cal.getTime());
        return date;
    }

    private String getTomorrowDate() {
        Calendar cal = Calendar.getInstance();
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        cal.add(cal.DATE, +1);
        String date = sdf.format(cal.getTime());
        return date;
    }

    private void setRake(String page_id, String action_id) {
        new Thread() {
            public void run() {
                String time = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
                OCBLogSentinelShuttle shuttle = new OCBLogSentinelShuttle();
                String track_id = SharedPreference.getString(context, Key.KEY_OCB_TRACK_ID);
                String device_id = SharedPreference.getString(context, Key.KEY_OCB_DEVICE_ID);
                try {
                    E_Cipher cp = E_Cipher.getInstance();
                    track_id = cp.Decode(context, track_id);
                    device_id = cp.Decode(context, device_id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String session_id = time + "_" + device_id;
                shuttle.page_id(page_id).action_id(action_id).session_id(session_id).mbr_id(track_id);
                rake.track(shuttle.toJSONObject());
            }
        }.start();
    }
 }
