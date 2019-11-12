package com.gfso.client.oauthclientapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.gfso.client.oauthclientapplication.fragment.CartFragment;
import com.gfso.client.oauthclientapplication.fragment.DashboardFragment;
import com.gfso.client.oauthclientapplication.fragment.HomeFragment;
import com.gfso.client.oauthclientapplication.fragment.MeFragment;
import com.gfso.client.oauthclientapplication.fragment.ShoppingFragment;
import com.gfso.client.oauthclientapplication.fragment.widget.CustomViewPager;
import com.gfso.client.oauthclientapplication.fragment.recycleview.CustomViewPagerAdapter;
import com.gfso.client.oauthclientapplication.util.BottomNavigationViewHelper;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {
    CustomViewPager viewPager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.navigation_category:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.navigation_shopping:
                    viewPager.setCurrentItem(2);
                    break;
                case R.id.navigation_cart:
                    viewPager.setCurrentItem(3);
                    break;
                case R.id.navigation_me:
                    viewPager.setCurrentItem(4);
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (CustomViewPager) findViewById(R.id.topfragment_container);
        CustomViewPagerAdapter adapter = new CustomViewPagerAdapter (MainActivity.this.getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "home");
        adapter.addFragment(new DashboardFragment(), "dashboard");
        adapter.addFragment(new ShoppingFragment(), "shopping");
        adapter.addFragment(new CartFragment(), "cart");
        adapter.addFragment(new MeFragment(), "me");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);
        //loading the default fragment
        viewPager.setCurrentItem(0);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomnavigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        trustAllCertificates();
    }

    private void trustAllCertificates() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                            return myTrustedAnchors;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception e) {
        }
    }
}
