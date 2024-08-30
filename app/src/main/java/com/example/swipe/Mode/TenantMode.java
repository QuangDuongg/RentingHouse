package com.example.swipe.Mode;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.swipe.R;

import java.util.HashMap;

public class TenantMode extends AppCompatActivity {

    private Button btnPersonalInfo, btnMessage;
    private EditText searchCity, searchDistrict;
    private String[] cities;
    private HashMap<String, String[]> districtsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_mode);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize buttons and search bars
        btnPersonalInfo = findViewById(R.id.btn_personal_info);
        btnMessage = findViewById(R.id.btn_message);
        searchCity = findViewById(R.id.search_city);
        searchDistrict = findViewById(R.id.search_district);

        // Prepare city and district data
        prepareDistrictData();

        // Set up listeners
        btnPersonalInfo.setOnClickListener(v -> {
            Toast.makeText(TenantMode.this, "Personal Info clicked", Toast.LENGTH_SHORT).show();
        });

        btnMessage.setOnClickListener(v -> {
            Toast.makeText(TenantMode.this, "Message clicked", Toast.LENGTH_SHORT).show();
        });

        searchCity.setOnClickListener(v -> {
            showCitySelectionDialog();
        });

        searchDistrict.setOnClickListener(v -> {
            String selectedCity = searchCity.getText().toString();
            if (selectedCity.isEmpty()) {
                Toast.makeText(TenantMode.this, "Please select a city first", Toast.LENGTH_SHORT).show();
            } else {
                showDistrictSelectionDialog(selectedCity);
            }
        });
    }

    private void prepareDistrictData() {
        districtsMap = new HashMap<>();

        // Danh sách các thành phố và quận/huyện tương ứng
        districtsMap.put("Hà Nội", new String[]{
                "Ba Đình", "Hoàn Kiếm", "Tây Hồ", "Cầu Giấy", "Đống Đa", "Hai Bà Trưng", "Thanh Xuân", "Hoàng Mai", "Long Biên", "Nam Từ Liêm", "Bắc Từ Liêm"
        });
        districtsMap.put("TP Hồ Chí Minh", new String[]{
                "Quận 1", "Quận 2", "Quận 3", "Quận 4", "Quận 5", "Quận 6", "Quận 7", "Quận 8", "Quận 9", "Quận 10", "Quận 11", "Quận 12", "Bình Tân", "Bình Thạnh", "Gò Vấp", "Phú Nhuận", "Tân Bình", "Tân Phú", "Thủ Đức"
        });
        districtsMap.put("Đà Nẵng", new String[]{
                "Hải Châu", "Thanh Khê", "Sơn Trà", "Ngũ Hành Sơn", "Liên Chiểu", "Cẩm Lệ", "Hòa Vang"
        });

        // Thêm dữ liệu cho tỉnh Quảng Ngãi
        districtsMap.put("Quảng Ngãi", new String[]{
                "TP Quảng Ngãi", "Bình Sơn", "Trà Bồng", "Sơn Tịnh", "Tư Nghĩa", "Nghĩa Hành", "Mộ Đức", "Đức Phổ",
                "Ba Tơ", "Minh Long", "Sơn Hà", "Sơn Tây", "Tây Trà", "Lý Sơn"
        });

        // Thêm dữ liệu cho tỉnh Quảng Nam
        districtsMap.put("Quảng Nam", new String[]{
                "TP Tam Kỳ", "TP Hội An", "Điện Bàn", "Đại Lộc", "Duy Xuyên", "Quế Sơn", "Nam Giang",
                "Phước Sơn", "Hiệp Đức", "Thăng Bình", "Tiên Phước", "Bắc Trà My", "Nam Trà My",
                "Núi Thành", "Phú Ninh", "Nông Sơn", "Đông Giang", "Tây Giang"
        });

        // Cập nhật danh sách thành phố từ districtsMap keys
        cities = districtsMap.keySet().toArray(new String[0]);
    }


    private void showCitySelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select City");
        builder.setItems(cities, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchCity.setText(cities[which]);
                searchDistrict.setText(""); // Clear district field when city changes
            }
        });
        builder.show();
    }

    private void showDistrictSelectionDialog(String city) {
        String[] districts = districtsMap.get(city);
        if (districts != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select District");
            builder.setItems(districts, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    searchDistrict.setText(districts[which]);
                }
            });
            builder.show();
        } else {
            Toast.makeText(this, "No districts available for selected city", Toast.LENGTH_SHORT).show();
        }
    }
}
