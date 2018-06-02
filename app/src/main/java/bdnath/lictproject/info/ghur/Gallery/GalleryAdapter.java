package bdnath.lictproject.info.ghur.Gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;

import bdnath.lictproject.info.ghur.FireBasePojoClass.GalleryHandeler;
import bdnath.lictproject.info.ghur.R;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    private List<GalleryHandeler>urls;
    private Context context;

    public GalleryAdapter(Context context,List<GalleryHandeler> urls) {
        this.urls = urls;
        this.context=context;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.gallery_grid,parent,false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        //Toast.makeText(context,urls.get(position).getUrl(),Toast.LENGTH_LONG).show();

        File path=new File(urls.get(position).getLocalUrl());
        Log.d("Path: ",path.getAbsolutePath());
        if (path.exists()){
            Picasso.get().load(path)
                    .resize(300, 300).onlyScaleDown() // the image will only be resized if it's bigger than 6000x2000 pixels.
                    .into(holder.imageView);
            /*Picasso.get().load(Uri.parse(urls.get(position).getUrl()))
                    .resize(300, 300).onlyScaleDown() // the image will only be resized if it's bigger than 6000x2000 pixels.
                    .into(holder.imageView);*/
        }else {
            Picasso.get().load(Uri.parse(urls.get(position).getUrl()))
                    .resize(300, 300).onlyScaleDown() // the image will only be resized if it's bigger than 6000x2000 pixels.
                    .into(holder.imageView);
            Toast.makeText(context,"Path not found in storage!!!",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public GalleryViewHolder(final View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.gallery_imgView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    LayoutInflater inflater= LayoutInflater.from(context);
                    LinearLayout root= (LinearLayout) inflater.inflate(R.layout.full_image_view,null);
                    builder.setView(root);
                    ImageView img=root.findViewById(R.id.full_imgView);
                    File path=new File(urls.get(position).getLocalUrl());
                    Log.d("Path: ",path.toString());
                    if (path.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(path.getAbsolutePath());
                        img.setImageBitmap(bitmap);
                    }
                    builder.setNegativeButton("Ok",null);
                    builder.show();
                }
            });
        }
    }

}