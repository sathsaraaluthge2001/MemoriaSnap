package com.example.memoriasnap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterMemories extends RecyclerView.Adapter<AdapterMemories.HolderMemories>{
    DatabaseConnection dbConnection;
    private Context context;
    private ArrayList<ModelMemories>memoriesList;
    public AdapterMemories(Context context, ArrayList<ModelMemories> memoriesList) {
        this.context = context;
        this.memoriesList = memoriesList;
        dbConnection=new DatabaseConnection(context);
    }

    @NonNull
    @Override
    public HolderMemories onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.raw_record,parent,false);
        return new HolderMemories(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMemories holder, int position) {

         ModelMemories model=memoriesList.get(position);
         String title=model.getTitle();
         String date=model.getDate();
         String image=model.getImage();
         String description=model.getDescription();

         holder.imageView.setImageURI(Uri.parse(image));
         holder.txtTitleView.setText(title);
         holder.txtDateView.setText(date);

         holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 Intent intent=new Intent(context,MemoryDetail.class);
                 intent.putExtra("MEMORY_TITLE",title);
                 context.startActivity(intent);
             }
         });

         holder.moreBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 showMoreDialog(
                         ""+title,
                         ""+image,
                         ""+date,
                         ""+description
                 );
             }
         });

    }

    private void showMoreDialog(String title,String image,String date,String description){
        String[] options={"Delete"};
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    dbConnection.deleteData(title);
                    ((MemoryList)context).onResume();
                }
            }
        });
        //show the dialog
        builder.show();
    }

    @Override
    public int getItemCount() {
        return memoriesList.size();
    }

    class HolderMemories extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView txtTitleView,txtDateView;
        ImageButton moreBtn;

        public HolderMemories(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.imageId);
            txtTitleView=itemView.findViewById(R.id.txtTilteView);
            txtDateView=itemView.findViewById(R.id.txtDateView);
            moreBtn=itemView.findViewById(R.id.moreBtn);
        }
    }

}
