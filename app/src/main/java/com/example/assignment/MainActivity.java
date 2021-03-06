package com.example.assignment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.assignment.ChiTietAnh.Main2Activity;
import com.example.assignment.Model.FlickrPhoto;
import com.example.assignment.Model.Photo;
import com.example.assignment.adapter.ImageViewAdapter;
import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    ImageViewAdapter imageViewAdapter;
    RecyclerView recyclerViewImage;
    SwipeRefreshLayout swipeRefreshLayout;
    private static final int NUM_COLUMNS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerViewImage = findViewById(R.id.recyclerview);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GetData();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        GetData();
        printKeyHash();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnPhongCanh:
                Intent intent = new Intent(MainActivity.this,PhongCanhActivity.class);
                startActivity(intent);
                return true;
            case R.id.btnGaiXinh:
                Intent intent2 = new Intent(MainActivity.this,GaiXinhActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.assignment",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash",Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void GetData() {
        swipeRefreshLayout.setRefreshing(true);
        //RequestQueue: nơi giữ các request trước khi gửi
        //tạo một RequestQueue bằng lệnh
        RequestQueue requestQueue =
                Volley.newRequestQueue(MainActivity.this);
        //StringRequest: kế thừa từ Request, là class đại diện cho request trả về String
        // khai báo stringRepuest, phương thức POST
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://www.flickr.com/services/rest", new Response.Listener<String>() { //Nơi bạn nhận dữ liệu trả về từ server khi request hoàn thành
            @Override
            public void onResponse(String response) {
                //là một thư viện java giúp chuyển đổi qua lại giữa JSON và Java
                Gson gson = new Gson();

                FlickrPhoto flickrPhoto =
                        gson.fromJson(response, FlickrPhoto.class);

                List<Photo> photos = flickrPhoto.getPhotos().getPhoto();

                // gọi interface bên adapter để bắt sự kiện chuyển màn hình và truyền position của item đã click sang màn hình main2
                imageViewAdapter = new ImageViewAdapter(getApplication(), (ArrayList<Photo>) photos,
                        new ImageViewAdapter.AdapterListener() {
                            @Override
                            public void OnClick(int position) {
                                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                                intent.putExtra("position", position);
                                startActivity(intent);
                            }
                        });
                // 1 dạng layout trong recyclerView giúp view hiển thị theo dạng lưới tùy theo kích thước của item
                StaggeredGridLayoutManager staggeredGridLayoutManager = new
                        StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
                recyclerViewImage.setLayoutManager(staggeredGridLayoutManager);
                recyclerViewImage.setAdapter(imageViewAdapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // nơi nhận các lỗi xảy ra khi request
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
        requestQueue.add(stringRequest); // thêm vào nơi giữ các request để gửi lên server
    }
}
