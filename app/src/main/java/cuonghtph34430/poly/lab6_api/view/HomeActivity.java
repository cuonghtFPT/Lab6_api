package cuonghtph34430.poly.lab6_api.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeActivity extends AppCompatActivity implements FruitAdapter.FruitClick {
    ActivityHomeBinding binding;
    private HttpRequest httpRequest;
    private SharedPreferences sharedPreferences;
    private String token;
    private FruitAdapter adapter;
    private static final int REQUEST_CODE_PICK_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        httpRequest = new HttpRequest();
        sharedPreferences = getSharedPreferences("INFO", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        httpRequest.callAPI().getListFruit("Bearer " + token).enqueue(getListFruitResponse);
        setupListeners();
    }

    private void setupListeners() {
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AddFruitActivity.class));
            }
        });

    }



    Callback<Response<ArrayList<Fruit>>> getListFruitResponse = new Callback<Response<ArrayList<Fruit>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Fruit>>> call, retrofit2.Response<Response<ArrayList<Fruit>>> response) {
            if (response.isSuccessful() && response.body().getStatus() == 200) {
                ArrayList<Fruit> ds = response.body().getData();
                showFruits(ds);
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Fruit>>> call, Throwable t) {
            Toast.makeText(HomeActivity.this, "Failed to fetch fruits", Toast.LENGTH_SHORT).show();
        }
    };

    private void showFruits(ArrayList<Fruit> ds) {
        adapter = new FruitAdapter(this, ds, this);
        binding.rcvFruit.setAdapter(adapter);
    }

    @Override
    public void delete(Fruit fruit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this fruit?");

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFruit(fruit.get_id());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void edit(Fruit fruit) {
        // Hiển thị dialog cập nhật trái cây
        showUpdateFruitDialog(fruit);
    }

    private void showUpdateFruitDialog(Fruit fruit) {
        // Tạo view dialog từ layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_update_fruit, null);

        // Khởi tạo AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Update Fruit")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Lấy giá trị mới từ các trường nhập liệu
                        EditText edtName = dialogView.findViewById(R.id.edt_name);
                        EditText edtQuantity = dialogView.findViewById(R.id.edt_quantity);
                        EditText edtPrice = dialogView.findViewById(R.id.edt_price);
                        EditText edtDescription = dialogView.findViewById(R.id.edt_description);

                        String newName = edtName.getText().toString().trim();
                        String newQuantity = edtQuantity.getText().toString().trim();
                        String newPrice = edtPrice.getText().toString().trim();
                        String newDescription = edtDescription.getText().toString().trim();

                        // Tạo đối tượng Fruit mới với các giá trị cập nhật
                        Fruit updatedFruit = new Fruit(fruit.get_id(), newName, newQuantity, newPrice, newDescription);

                        // Gọi phương thức để cập nhật trái cây
                        updateFruit(updatedFruit);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // Hiển thị AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Thiết lập sự kiện click cho ImageView để chọn ảnh
        ImageView imageView = dialogView.findViewById(R.id.avatar);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi intent để mở hộp thoại chọn ảnh từ bộ nhớ trong thiết bị
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
            }
        });
    }
    private void updateFruit(Fruit fruit) {
        // Gọi API hoặc phương thức cập nhật trái cây với dữ liệu mới
        // Ví dụ:
        httpRequest.callAPI().updateFruits(fruit.get_id(), fruit).enqueue(new Callback<Response<Fruit>>() {
            @Override
            public void onResponse(Call<Response<Fruit>> call, retrofit2.Response<Response<Fruit>> response) {
                if (response.isSuccessful() && response.body().getStatus() == 200) {
                    Toast.makeText(HomeActivity.this, "Fruit updated successfully", Toast.LENGTH_SHORT).show();
                    // Cập nhật lại danh sách trái cây sau khi cập nhật thành công
                    httpRequest.callAPI().getListFruit("Bearer " + token).enqueue(getListFruitResponse);
                } else {
                    Toast.makeText(HomeActivity.this, "Failed to update fruit", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<Fruit>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Failed to update fruit", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void deleteFruit(String fruitId) {
        httpRequest.callAPI().deleteFruit(fruitId).enqueue(new Callback<Response<Fruit>>() {
            @Override
            public void onResponse(Call<Response<Fruit>> call, retrofit2.Response<Response<Fruit>> response) {
                if (response.isSuccessful() && response.body().getStatus() == 200) {
                    Toast.makeText(HomeActivity.this, "Fruit deleted successfully", Toast.LENGTH_SHORT).show();
                    httpRequest.callAPI().getListFruit("Bearer " + token).enqueue(getListFruitResponse);
                } else {
                    Toast.makeText(HomeActivity.this, "Failed to delete fruit", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<Fruit>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Failed to delete fruit", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        httpRequest.callAPI().getListFruit("Bearer " + token).enqueue(getListFruitResponse);
    }
}