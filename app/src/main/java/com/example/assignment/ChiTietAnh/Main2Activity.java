package com.example.assignment.ChiTietAnh;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.renderscript.Script;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.assignment.MainActivity;
import com.example.assignment.Model.FlickrPhoto;
import com.example.assignment.Model.Photo;
import com.example.assignment.R;
import com.example.assignment.SaveImageHelper;
import com.example.assignment.adapter.ViewpagerAdapter;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class Main2Activity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1000;
    FloatingActionButton fab, fab1, fab2, fab3, fab4;
    public ViewPager viewPager;
    ViewpagerAdapter viewpagerAdapter;
    TextView tv1, tv2;
    boolean An_Hien = false;
    AlertDialog dialog;
    String link; // phục vụ cho việc tải ảnh
    public int position; // nhận position của ảnh đã click bên main
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    List<Photo> photos;


    // người dùng đã cấp quyền hay chưa
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Ðã được cho phép", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Quyền bị từ chối", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fab1);
        fab3 = findViewById(R.id.fab3);
        tv1 = findViewById(R.id.tv1);
        fab2 = findViewById(R.id.fab2);
        tv2 = findViewById(R.id.tv2);
        fab4 = findViewById(R.id.fab4);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        viewPager = findViewById(R.id.viewPager);
        Intent intent = this.getIntent();
        position = intent.getIntExtra("position", 0);
        getData();

        // xin quyền lưu vào bộ nhớ
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            }, PERMISSION_REQUEST_CODE);

        //
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (An_Hien == false) {
                    Hien();
                    An_Hien = true;
                } else {
                    An();
                    An_Hien = false;
                }
            }
        });

        //
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // xin quyền lưu trữ từ người dùng nếu trước đó họ chưa cấp quyền
                if (ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Main2Activity.this, "", Toast.LENGTH_SHORT).show();
                    requestPermissions(new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, PERMISSION_REQUEST_CODE);
                    return;
                } else {
                    dialog = new SpotsDialog.Builder().setContext(Main2Activity.this).build();
                    dialog.show();
                    dialog.setMessage("Downloading...");

                    String fileName = UUID.randomUUID().toString() + ".jpg";
                    Picasso.with(getBaseContext())
                            .load(photos.get(viewPager.getCurrentItem()).getUrlL())
                            .into(new SaveImageHelper(getBaseContext(), dialog,
                                    getApplicationContext().getContentResolver(),
                                    fileName, "Image description"));
                }

            }
        });

        //
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // xin quyền lưu trữ từ người dùng nếu trước đó họ chưa cấp quyền
                if (ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Main2Activity.this, "", Toast.LENGTH_SHORT).show();
                    requestPermissions(new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, PERMISSION_REQUEST_CODE);
                    return;
                } else {
                    dialog = new SpotsDialog.Builder().setContext(Main2Activity.this).build();
                    dialog.show();
                    dialog.setMessage("Downloading...");

                    String fileName2 = UUID.randomUUID().toString() + ".png";
                    Picasso.with(getBaseContext()).load(photos.get(viewPager.getCurrentItem()).getUrlL()).into(new SaveImageHelper(getBaseContext(),
                            dialog,
                            getApplicationContext().getContentResolver(),
                            fileName2, "Image description2"));

                }

            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(Main2Activity.this, "Share sucessfull", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(Main2Activity.this, "Share cancel", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(Main2Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                Picasso.with(getBaseContext())
                        .load(photos.get(viewPager.getCurrentItem()).getUrlL())
                        .into(target);
            }
        });
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                WallpaperManager wpm = WallpaperManager.getInstance(getApplicationContext());
                InputStream ins = null;
                try {
                    ins = new URL(photos.get(viewPager.getCurrentItem()).getUrlL()).openStream();
                    wpm.setStream(ins);
                    Toast.makeText(getApplicationContext(), "successfully set ", Toast.LENGTH_LONG).show();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if (shareDialog.canShow(SharePhotoContent.class)) {
                SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto).build();
                shareDialog.show(sharePhotoContent);
            }

        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    private void getData() {
        // thông báo trạng thái khi đợi dữ liệu trả về
        final ProgressDialog loading = new ProgressDialog(Main2Activity.this);
        loading.setMessage("Loading...");
        loading.show();
        //RequestQueue: nơi giữ các request trước khi gửi
        //tạo một RequestQueue bằng lệnh
        RequestQueue requestQueue =
                Volley.newRequestQueue(Main2Activity.this);
        //StringRequest: kế thừa từ Request, là class đại diện cho request trả về String
        // khai báo stringRepuest, phương thức POST
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://www.flickr.com/services/rest", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson(); //là một thư viện java giúp chuyển đổi qua lại giữa JSON và Java

                FlickrPhoto flickrPhoto =
                        gson.fromJson(response, FlickrPhoto.class);

                photos = flickrPhoto.getPhotos().getPhoto(); // lấy ảnh từ flick để cho vào list ảnh gắn lên view

                viewpagerAdapter = new ViewpagerAdapter(Main2Activity.this, photos); // gắn dữ liệu vào adapter
                link = photos.get(viewPager.getCurrentItem()).getUrlL(); // link ảnh phục vụ cho việc tải
                viewPager.setAdapter(viewpagerAdapter);
                viewPager.setCurrentItem(position, true);
                viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        // Gọi khi sự kiện scroll bắt đầu diễn ra và đi tới page đó
                    }

                    @Override
                    public void onPageSelected(int position) {
                        // Được gọi khi một page đã được chọn
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        // Được gọi khi trang thái scol thay đổi
                    }
                });
                viewpagerAdapter.notifyDataSetChanged();
                loading.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(Main2Activity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // lưu giữ các giá trị theo cặp key/value
                Map<String, String> params = new HashMap<>();
                params.put("api_key", "2e471db5eb8ebd63c3801fe8e9f4a825");
                params.put("user_id", "187022838@N07");
                params.put("extras", "views, media, path_alias, url_l, url_o");
                params.put("format", "json");
                params.put("method", "flickr.favorites.getList");
                params.put("nojsoncallback", "1");

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void An() {
        fab1.hide();
        fab2.hide();
        fab3.hide();
        fab4.hide();
        tv1.setVisibility(View.INVISIBLE);
        tv2.setVisibility(View.INVISIBLE);
    }

    private void Hien() {
        fab1.show();
        fab2.show();
        fab3.show();
        fab4.show();
        tv1.setVisibility(View.VISIBLE);
        tv2.setVisibility(View.VISIBLE);
    }

}
