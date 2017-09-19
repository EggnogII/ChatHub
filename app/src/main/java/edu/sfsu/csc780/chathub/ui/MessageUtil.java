package edu.sfsu.csc780.chathub.ui;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.sfsu.csc780.chathub.ChatMessage;

/**
 * Created by bees on 9/19/17.
 */

public class MessageUtil {
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
