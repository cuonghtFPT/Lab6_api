package cuonghtph34430.poly.lab6_api.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import java.util.ArrayList;

public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Fruit> list;
    private FruitClick fruitClick;

    public FruitAdapter(Context context, ArrayList<Fruit> list, FruitClick fruitClick) {
        this.context = context;
        this.list = list;
        this.fruitClick = fruitClick;
    }

    public interface FruitClick {
        void delete(Fruit fruit);

        void edit(Fruit fruit);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFruitBinding binding = ItemFruitBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Fruit fruit = list.get(position);
        holder.bind(fruit);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemFruitBinding binding;

        public ViewHolder(ItemFruitBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Fruit fruit) {
            binding.tvName.setText(fruit.getName());
            binding.tvPriceQuantity.setText("price: " + fruit.getPrice() + " - quantity: " + fruit.getQuantity());
            binding.tvDes.setText(fruit.getDescription());

            if (fruit.getImage() != null && fruit.getImage().size() > 0) {
                String url = fruit.getImage().get(0);
                String newUrl = url.replace("localhost", "10.0.2.2");
                Glide.with(context)
                        .load(newUrl)
                        .thumbnail(Glide.with(context).load(R.drawable.baseline_broken_image_24))
                        .into(binding.img);
                Log.d("321321", "onBindViewHolder: " + list.get(getAdapterPosition()).getImage().get(0));
            } else {
                Glide.with(context)
                        .load(R.drawable.baseline_broken_image_24)
                        .into(binding.img);
            }

            binding.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fruitClick.delete(fruit);
                }
            });

            binding.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    // Truyền fruit được chọn qua interface
                    fruitClick.edit(fruit);
                    // Chuyển sang UpdateFruitActivity
                    Intent intent = new Intent(context, UpdateFruitActivity.class);
                    // Truyền thông tin về Fruit cần cập nhật
                    intent.putExtra("FRUIT_ID", fruit.get_id());
                    context.startActivity(intent);
                }
            });
        }
    }
}