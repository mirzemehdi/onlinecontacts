package Data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.firebaseusers.Interface.ILoadMore;
import com.firebase.firebaseusers.MessagesActivity;
import com.firebase.firebaseusers.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import User.User;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> usersList;
    private Context context;
    private final int VIEW_TYPE_LOADING=0,VIEW_TYPE_ITEM=1;
    private ILoadMore loadMore;
    private boolean isLoading;
    int totalItemCount,lastVisibleItem;

    public Adapter(RecyclerView recyclerView,List<User> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;

        final LinearLayoutManager layoutManager=(LinearLayoutManager)recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount=layoutManager.getChildCount();
                lastVisibleItem=layoutManager.findLastVisibleItemPosition();
                Log.d("OnLoadMore",String.valueOf(totalItemCount)+" and index: "+String.valueOf(layoutManager.getItemCount()));
                if (!isLoading&&totalItemCount<=lastVisibleItem+5){
                    Log.d("OnLoadMore","Scrolled");

                    if (loadMore!=null)
                    loadMore.onLoadMore();
                }
                isLoading=true;

            }
        });


    }


    @Override
    public int getItemViewType(int position) {
        return usersList.get(position)==null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==VIEW_TYPE_ITEM){
            View view=LayoutInflater.from(context).inflate(R.layout.user_row,parent,false);
            return new ItemViewHolder(view,context);
        }
        else if(viewType==VIEW_TYPE_LOADING){
            View view=LayoutInflater.from(context).inflate(R.layout.item_progress,parent,false);
            return new LoadingViewHolder(view);

        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            if (holder instanceof ItemViewHolder){

                ItemViewHolder viewHolder=(ItemViewHolder)holder;
                User user=usersList.get(position);
                viewHolder.nameSurname.setText(user.getName()+" "+user.getSurname());
                Picasso.get().load(user.getImageUri()).into(viewHolder.profileImage);
            }

            else if (holder instanceof LoadingViewHolder){

                LoadingViewHolder viewHolder=(LoadingViewHolder)holder;
                viewHolder.progressBar.setIndeterminate(true);
            }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar=(ProgressBar)itemView.findViewById(R.id.loadProgressBar);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView profileImage;
        private TextView nameSurname;

        public ItemViewHolder(View itemView,Context ctx) {
            super(itemView);
            context=ctx;

            profileImage=(ImageView)itemView.findViewById(R.id.profileImage);
            nameSurname=(TextView) itemView.findViewById(R.id.nameSurname);


            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageClicked();
                }


            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent passMessageActivity=new Intent(context, MessagesActivity.class);
                    String userId=usersList.get(getAdapterPosition()).getUserId();
                    String imageUri=usersList.get(getAdapterPosition()).getImageUri();
                    passMessageActivity.putExtra("selectedUserId",userId);

                    passMessageActivity.putExtra("selectedUserImageUri",imageUri);
                    context.startActivity(passMessageActivity);
                }
            });



        }


        private void imageClicked() {
            AlertDialog.Builder builder=new AlertDialog.Builder(context);
            View view=LayoutInflater.from(context).inflate(R.layout.alerd_dialog,null);
            ImageView alertImage=view.findViewById(R.id.alertImage);
            Uri imageURI= Uri.parse(usersList.get(getAdapterPosition()).getImageUri()) ;
            Log.d("ImageUri",usersList.get(getAdapterPosition()).getImageUri());
            Picasso.get().load(imageURI).into(alertImage);
            builder.setView(view);

            AlertDialog alertDialog=builder.create();
            alertDialog.show();
        }
    }




}
