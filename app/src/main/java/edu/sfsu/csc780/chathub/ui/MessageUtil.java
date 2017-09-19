package edu.sfsu.csc780.chathub.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.sfsu.csc780.chathub.ChatMessage;
import edu.sfsu.csc780.chathub.R;

/**
 * Created by bees on 9/19/17.
 */

public class MessageUtil
{
    private static final String LOG_TAG = MessageUtil.class.getSimpleName();
    public static final String MESSAGES_CHILD = "messages";
    private static DatabaseReference sFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

    private static MessageLoadListener sAdapterListener;
    private static FirebaseAuth sFirebaseAuth;

    public interface MessageLoadListener {public void onLoadComplete();}
    public static void send(ChatMessage chatMessage)
    {
        sFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(chatMessage);
    }
}

public static class MessageViewHolder extends RecyclerView.ViewHolder
{
    public TextView messageTextView;
    public TextView messengerTextView;
    public CircleImageView messengerImageView;

    public MessageViewHolder(View v)
    {
        super(v);
        messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
        messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
        messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
    }
}
