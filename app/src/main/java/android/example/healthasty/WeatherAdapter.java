package android.example.healthasty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherModel> weatherModel;

    public WeatherAdapter(Context context,ArrayList<WeatherModel> weatherModel) {
        this.context = context;
        this.weatherModel = weatherModel;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_row_weather,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherModel model = weatherModel.get(position);
        holder.textView2.setText(model.getTemperature() + "℃");
        holder.textView3.setText(model.getWindSpeed().concat(" Km/h"));

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa" );
        try {
            Date t = input.parse(model.getTime());
            holder.textView1.setText(output.format(t));
        }
        catch (Exception e){
            e.printStackTrace();
        }

        Picasso.get().load("http:".concat(model.getIcon())).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return weatherModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView1,textView2,textView3;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.single_time);
            textView2 = itemView.findViewById(R.id.single_temp);
            textView3 = itemView.findViewById(R.id.wind);
            imageView = itemView.findViewById(R.id.single_image);

        }
    }
}
