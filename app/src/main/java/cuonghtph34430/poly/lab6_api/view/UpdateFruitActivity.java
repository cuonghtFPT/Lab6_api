package cuonghtph34430.poly.lab6_api.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import retrofit2.Call;
import retrofit2.Callback;

public class UpdateFruitActivity extends AppCompatActivity {
    ActivityUpdateFruitBinding binding;
    private HttpRequest httpRequest;
    private Fruit fruitToUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateFruitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        httpRequest = new HttpRequest();
        fruitToUpdate = getIntent().getParcelableExtra("FRUIT_TO_UPDATE");

        if (fruitToUpdate != null) {
            displayFruitInfo();
        } else {
            Toast.makeText(this, "Không có dữ liệu hoa quả để cập nhật", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    private void displayFruitInfo() {
        binding.edtName.setText(fruitToUpdate.getName());
        binding.edtQuantity.setText(String.valueOf(fruitToUpdate.getQuantity()));
        binding.edtPrice.setText(String.valueOf(fruitToUpdate.getPrice()));
        binding.edtStatus.setText(fruitToUpdate.getStatus());
        binding.edtDescription.setText(fruitToUpdate.getDescription());
    }

    private void updateFruit() {
        String name = binding.edtName.getText().toString().trim();
        String quantity = binding.edtQuantity.getText().toString().trim();
        String price = binding.edtPrice.getText().toString().trim();
        String status = binding.edtStatus.getText().toString().trim();
        String description = binding.edtDescription.getText().toString().trim();

        Fruit updatedFruit = new Fruit();
        updatedFruit.set_id(fruitToUpdate.get_id());
        updatedFruit.setName(name);
        updatedFruit.setQuantity(quantity);
        updatedFruit.setPrice(price);
        updatedFruit.setStatus(status);
        updatedFruit.setDescription(description);

        httpRequest.callAPI().updateFruits(fruitToUpdate.get_id(), updatedFruit).enqueue(updateFruitCallback);
    }

    private Callback<Response<Fruit>> updateFruitCallback = new Callback<Response<Fruit>>() {
        @Override
        public void onResponse(Call<Response<Fruit>> call, retrofit2.Response<Response<Fruit>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    Toast.makeText(UpdateFruitActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UpdateFruitActivity.this, "Cập nhật không thành công", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(UpdateFruitActivity.this, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<Response<Fruit>> call, Throwable t) {
            Toast.makeText(UpdateFruitActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
        }
    };
}
