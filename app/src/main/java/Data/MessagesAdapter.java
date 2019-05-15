package Data;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.firebaseusers.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import User.Message;
import User.User;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private List<Message>messageList;
    private Context context;
    private String userId;
    private String otherUserId;
    private String otherUserImageUri;
    private Uri currentUserImageUri;

    public MessagesAdapter(List<Message> messageList, Context context, String userId, String otherUserId, String otherUserImageUri) {
        this.messageList = messageList;
        this.context = context;
        this.userId = userId;
        this.otherUserId = otherUserId;
        this.otherUserImageUri = otherUserImageUri;
        Log.d("CurrentUser",userId);
    }








    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row,parent,false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {
        Message message=messageList.get(position);






        if(isMe(message.getUserId())){


            holder.messageContainer.setGravity(Gravity.RIGHT);

            holder.messageTextView.setBackgroundResource(R.drawable.text_border);



        }

        else{
            holder.messageContainer.setGravity(Gravity.LEFT);
            holder.messageTextView.setBackgroundResource(R.drawable.text_border_forother);



        }

//        Picasso.get().load(Uri.parse(otherUserImageUri)).into(holder.otherUserImage);

        holder.messageTextView.setText(message.getMessage());




    }

    private Uri getCurrentUserImage() {

        DatabaseReference myRef=FirebaseDatabase.getInstance().getReference("Users").child(userId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User currentUser=dataSnapshot.getValue(User.class);
                 currentUserImageUri= Uri.parse(currentUser.getImageUri());
                Log.d("cuurentUserImage",currentUserImageUri.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
      //  currentUserImageUri=Uri.parse("https://firebasestorage.googleapis.com/v0/b/fir-contacts-ddc64.appspot.com/o/ProfilePictures%2Fcropped635234239511201465.jpg?alt=media&token=2f188364-6aed-433b-8fcb-0b268ad5eba0");
      return currentUserImageUri;
    }


    private boolean isMe(String id) {

        Log.d("userID",id);
        Log.d("userID","UserID: "+userId);
       return id.equals(userId);

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView messageTextView;
        private RelativeLayout messageContainer;
        public ViewHolder(View itemView) {
            super(itemView);


            messageTextView=(TextView)itemView.findViewById(R.id.messageTextview);
            messageContainer=(RelativeLayout)itemView.findViewById(R.id.messageContainerRow);
        }
    }
}
