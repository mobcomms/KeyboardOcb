package enliple.com;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.enliple.keyboard.common.KeyboardLogPrint;
import com.enliple.keyboard.mobonAD.MobonBannerType;
import com.enliple.keyboard.mobonAD.MobonBannerView;
import com.enliple.keyboard.mobonAD.MobonSimpleSDK;
import com.enliple.keyboard.mobonAD.iSimpleMobonBannerCallback;
import com.enliple.keyboard.ui.ckeyboard.IntroActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import enliple.com.ckeyboard.R;

public class MainActivity extends AppCompatActivity {
    int count = 0;
    MobonBannerView bannerView;
    LinearLayout layout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        startActivity(new Intent(MainActivity.this, IntroActivity.class));
//        finish();

        setContentView(R.layout.main);
        layout = findViewById(R.id.test);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadBanner();
            }
        });

        loadBanner();
    }

    private void initBannerView() {
        String bannerUnitId = "551812";
        //bannerUnitId = "40964";
        new MobonSimpleSDK(getApplicationContext(), "okaycashbag");
        bannerView = new MobonBannerView(getApplicationContext(), MobonBannerType.BANNER_CUSTOM).setBannerUnitId(bannerUnitId);
        bannerView.setAdListener(new iSimpleMobonBannerCallback() {

            @Override
            public void onCloseClicked() {

            }

            @Override
            public void onBannerLoaded(int leftBg, int rightBg) {

            }

            @Override
            public void onLoadedAdInfo(boolean result, String errorStr) {
                if (result) {
                    //visibleMobonADView();
                    if (bannerView != null) {
                        layout.setVisibility(View.VISIBLE);
                        layout.removeAllViews();
                        layout.addView(bannerView);
                    }
                } else {
                    layout.setVisibility(View.GONE);
                    bannerView.onDestroy();
                }
            }

            @Override
            public void onAdClicked() {
                KeyboardLogPrint.d("click ads");
            }
        });

    }

    public void loadBanner() {
        bannerView = null;
        initBannerView();
        if ( bannerView != null ) {
            bannerView.onDestroy();
            bannerView.loadCoupangAd();
        }
    }

    public void loadMediationBanner() {
        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            JSONObject object = new JSONObject();
            object.put("name", "adfitsdk");
            object.put("unitid", "DAN-Vx0wT8ZfwKuX4Mxx");
            object.put("mediaKey", "");
            array.put(object);
            // 차후 노출 순서에 따라 array에 put 한다.
            //array.put(new JSONObject("{\"name\":\"admixersdk\",\"unitid\":\"26117793\",\"mediaKey\":\"19239320\"}"));
            //array.put(new JSONObject("{\"name\":\"criteo\",\"unitid\":\"111111\",\"mediaKey\":\"\"}"));
            obj.put("list", array);
            bannerView = null;
            initBannerView();
            if ( bannerView != null ) {
                bannerView.onDestroy();
//                bannerView.loadMediationAd(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public Activity getActivity(Context context)
    {
        if (context == null)
        {
            return null;
        }
        else if (context instanceof ContextWrapper)
        {
            if (context instanceof Activity)
            {
                return (Activity) context;
            }
            else
            {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }

        return null;
    }
}
