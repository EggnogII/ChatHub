package edu.sfsu.csc780.chathub.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URI;
import java.util.Calendar;
import java.util.IllegalFormatCodePointException;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.sfsu.csc780.chathub.model.ChatMessage;
import edu.sfsu.csc780.chathub.R;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by bees on 9/19/17.
 */

public class MessageUtil
{
    private static final String LOG_TAG = MessageUtil.class.getSimpleName();
    public static final String MESSAGES_CHILD = "messages";
    private static FirebaseStorage sStorage= FirebaseStorage.getInstance();
    private static DatabaseReference sFirebaseDatabaseReference =
            FirebaseDatabase.getInstance().getReference();

    private static MessageLoadListener sAdapterListener;
    private static FirebaseAuth sFirebaseAuth;

    public interface MessageLoadListener {public void onLoadComplete();}

    public static void send(ChatMessage chatMessage)
    {
        sFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(chatMessage);
    }

    public static StorageReference getImageStorageReference(FirebaseUser user, Uri uri){
        //Create a blob storage reference path : bucke/userId/timeMs/filename
        long nowMs = Calendar.getInstance().getTimeInMillis();

        return sStorage.getReference().child(user.getUid() + "/" + nowMs + "/" + uri
            .getLastPathSegment());
    }

    //Add a static method to make RecyclerViewAdapter

    public static FirebaseRecyclerAdapter getFirebaseAdapter(final Activity activity,
                                                             MessageLoadListener listener,
                                                             final LinearLayoutManager linearManager,
                                                            final RecyclerView recyclerView)
    {
        sAdapterListener = listener;

        final FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<ChatMessage,
                MessageViewHolder>(
                    ChatMessage.class,
                    R.layout.item_message,
                    MessageViewHolder.class,
                    sFirebaseDatabaseReference.child(MESSAGES_CHILD))
                {
                    @Override
                    protected void populateViewHolder(final MessageViewHolder viewHolder,
                                                    ChatMessage chatMessage, int position)
                    {
                        //more TODO
                        sAdapterListener.onLoadComplete();

                        viewHolder.messageTextView.setText(chatMessage.getText());
                        viewHolder.messengerTextView.setText(chatMessage.getName());

                        if (chatMessage.getPhotoUrl() == null)
                        {
                            viewHolder.messengerImageView
                                    .setImageDrawable(ContextCompat
                                    .getDrawable(activity,
                                            R.drawable.ic_account_circle_black_36dp));
                        }

                       else
                        {
                            SimpleTarget target = new SimpleTarget<Bitmap>()
                            {
                                @Override
                                public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation)
                                {
                                    viewHolder.messengerImageView.setImageBitmap(bitmap);
                                }
                            };

                        Glide.with(activity)
                                .load(chatMessage
                                .getPhotoUrl())
                                .asBitmap()
                                .into(target);
                        }

                        if (chatMessage.getImageUrl() != null)
                        {
                            //Set View visabilities for image message
                            viewHolder.messageImageView.setVisibility(View.VISIBLE);
                            viewHolder.messageTextView.setVisibility(View.GONE);
                            //Load Image for message

                        }
                        else
                        {
                         //set view visibilities for text message
                            viewHolder.messageImageView.setVisibility(View.GONE);
                            viewHolder.messageTextView.setVisibility(View.VISIBLE);
                        }

                        //load image for message
                        try{
                            final StorageReference gsReference =
                                    sStorage.getReferenceFromUrl(chatMessage.getImageUrl());

                            gsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(activity)
                                            .load(uri)
                                            .into(viewHolder.messageImageView);
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Could not load image for message", e);
                                }
                            });
                        }
                        catch (IllegalFormatCodePointException e){
                            viewHolder.messageTextView.setText("Error loading image");
                            Log.e(LOG_TAG, e.getMessage() + " : "+ chatMessage.getImageUrl());
                        }

                    } //End populateViewHolder
                }; //End Firebase Recycler Adapter creation

            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
            {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount)
                {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int messageCount = adapter.getItemCount();
                    int lastVisiblePosition = linearManager.findLastCompletelyVisibleItemPosition();

                    if (lastVisiblePosition == -1 ||
                        (positionStart >= (messageCount -1) &&
                                lastVisiblePosition == (positionStart -1)))
                    {
                        recyclerView.scrollToPosition(positionStart);
                    }
                }
            });

        return adapter;
    }
    //End Recycler

    public static class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView messageTextView;
        public TextView messengerTextView;
        public CircleImageView messengerImageView;
        public ImageView messageImageView;

        public MessageViewHolder(View v)
        {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
        }

    }

}


