package com.example.nicolaebogdan.whatsapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private View RequestsFragmentView;
    private RecyclerView myRequestList;

    private DatabaseReference ChatRequestsRef,UsersRef;
    private FirebaseAuth mAuth;
    private DatabaseReference ContactsRef;

    private String currentUserID;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RequestsFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestsRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        myRequestList = RequestsFragmentView.findViewById(R.id.chat_request_list);
        myRequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        return RequestsFragmentView;
    }


    @Override
    public void onStart() {
        super.onStart();

        final FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ChatRequestsRef.child(currentUserID),Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,RequestsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull Contacts model) {

                holder.itemView.findViewById(R.id.request_accept_button).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.request_cancel_button).setVisibility(View.VISIBLE);


                final String listUserID = getRef(position).getKey();

                DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            String type = dataSnapshot.getValue().toString();

                            if(type.equals("received")){
                                UsersRef.child(listUserID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild("image")){


                                            final String requestsProfileImage = dataSnapshot.child("image").getValue().toString();


                                            Picasso.get().load(requestsProfileImage).placeholder(R.drawable.profile_image).into(holder.profileImage);

                                        }

                                        final String requestsUserName = dataSnapshot.child("name").getValue().toString();
                                        //final String requestsUserStatus = dataSnapshot.child("status").getValue().toString();


                                        holder.userName.setText(requestsUserName);
                                        holder.userStatus.setText("wants to connect with you!");


                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                CharSequence options[] = new CharSequence[]{

                                                        "Accept",
                                                        "Cancel"

                                                 };

                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle(requestsUserName + "  Chat Requests");

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int i) {
                                                        if(i == 0){

                                                           ContactsRef.child(currentUserID).child(listUserID).child("Contact")
                                                                   .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                               @Override
                                                               public void onComplete(@NonNull Task<Void> task) {

                                                                   if(task.isSuccessful()){

                                                                       ContactsRef.child(listUserID).child(currentUserID).child("Contact")
                                                                               .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                           @Override
                                                                           public void onComplete(@NonNull Task<Void> task) {

                                                                               if(task.isSuccessful()){

                                                                                   ChatRequestsRef.child(currentUserID).child(listUserID)
                                                                                           .removeValue()
                                                                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                               @Override
                                                                                               public void onComplete(@NonNull Task<Void> task) {

                                                                                                   if(task.isSuccessful()){

                                                                                                       ChatRequestsRef.child(listUserID).child(currentUserID)
                                                                                                               .removeValue()
                                                                                                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                   @Override
                                                                                                                   public void onComplete(@NonNull Task<Void> task) {

                                                                                                                       if(task.isSuccessful()){
                                                                                                                           Toast.makeText(getContext(),"Contact Saved!",Toast.LENGTH_SHORT).show();
                                                                                                                       }

                                                                                                                   }
                                                                                                               });

                                                                                                   }

                                                                                               }
                                                                                           });

                                                                               }

                                                                           }
                                                                       });

                                                                   }

                                                               }
                                                           });

                                                        }
                                                        if(i == 1){

                                                            ChatRequestsRef.child(currentUserID).child(listUserID)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if(task.isSuccessful()){

                                                                                ChatRequestsRef.child(listUserID).child(currentUserID)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                if(task.isSuccessful()){
                                                                                                    Toast.makeText(getContext(),"Contact Deleted!",Toast.LENGTH_SHORT).show();
                                                                                                }

                                                                                            }
                                                                                        });

                                                                            }

                                                                        }
                                                                    });

                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                RequestsViewHolder holder = new RequestsViewHolder(view);
                return holder;

            }
        };

        myRequestList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class RequestsViewHolder extends RecyclerView.ViewHolder{

        TextView userName,userStatus;
        CircleImageView profileImage;
        Button AcceptButton,CancelButton;

        public RequestsViewHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profine_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            AcceptButton = itemView.findViewById(R.id.request_accept_button);
            CancelButton = itemView.findViewById(R.id.request_cancel_button);

        }
    }
}
