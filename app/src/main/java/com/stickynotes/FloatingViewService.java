package com.stickynotes;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stickynotes.adapters.NoteAdapter;
import com.stickynotes.models.Note;

import java.util.ArrayList;
import java.util.List;

public class FloatingViewService extends Service {

    private WindowManager windowManager;
    private View floatingView;
    WindowManager.LayoutParams params;
    private View collapsedView;
    private View expandedView;
    private RecyclerView recyclerView;
    private NoteAdapter adapter;

    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        floatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_view, null);

        initComponents();
        setOnTouchListeners();
        initRecyclerView();
        fetchNotes();

        //Add the view to the window.
         params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        // SPECIFY THE VIEW POSITION
        params.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(floatingView, params);
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) floatingView.findViewById(R.id.notes_list);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );
        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        adapter.notifyDataSetChanged();
    }

    public void fetchNotes() {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        String documentPath = "/users/" + user.getUid() + "/notes/";
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(documentPath)
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    // Task is successful
                                    if (task.getResult() != null) {
                                        List<Note> notes = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            // LOOP THROUGH ALL THE DOCUMENT SNAPSHOTS AND CREATE NOTE OBJECTS
                                            Note note = (Note) document.toObject(Note.class);
                                            note.setId(document.getId());
                                            notes.add(note);
                                        }
                                        adapter.setNotes(notes);
//                                        if (notes.isEmpty()) {
//                                            noNotesOverlay.setVisibility(View.VISIBLE);
//                                            notesListRecyclerView.setVisibility(View.GONE);
//                                        }
                                    }
                                }
                            }
                        }
                );

    }

    private void initComponents() {

        //The root element of the collapsed view layout
        collapsedView = floatingView.findViewById(R.id.collapse_view);
        //The root element of the expanded view layout
        expandedView = floatingView.findViewById(R.id.expanded_container);

        // OUTER CLOSE BUTTON
        ImageView closeButtonCollapsed = (ImageView) floatingView.findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // CLOSE THE SERVICE AND REMOVE THE VIEW FROM THE WINDOW
                stopSelf();
            }
        });

        // INNER CLOSE BUTTON
        // THIS CLOSES THE EXPANDED VIEW AND SHOWS THE COLLAPSED VIEW
        ImageView closeButton = (ImageView) floatingView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) windowManager.removeView(floatingView);
    }

    private void setOnTouchListeners() {
        floatingView.findViewById(R.id.root_container).setOnTouchListener(
                new View.OnTouchListener() {

                    private int initialX;
                    private int initialY;
                    private float initialTouchX;
                    private float initialTouchY;

                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                initialX = params.x;
                                initialY = params.y;


                                //get the touch location
                                initialTouchX = motionEvent.getRawX();
                                initialTouchY = motionEvent.getRawY();
                                return true;
                            case MotionEvent.ACTION_UP:
                                int Xdiff = (int) (motionEvent.getRawX() - initialTouchX);
                                int Ydiff = (int) (motionEvent.getRawY() - initialTouchY);


                                //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                                //So that is click event.
                                if (Xdiff < 10 && Ydiff < 10) {
                                    if (isViewCollapsed()) {
                                        //When user clicks on the image view of the collapsed layout,
                                        //visibility of the collapsed layout will be changed to "View.GONE"
                                        //and expanded view will become visible.
                                        collapsedView.setVisibility(View.GONE);
                                        expandedView.setVisibility(View.VISIBLE);
                                    }
                                }
                                return true;
                            case MotionEvent.ACTION_MOVE:
                                //Calculate the X and Y coordinates of the view.
                                params.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                                params.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);


                                //Update the layout with new X & Y coordinate
                                windowManager.updateViewLayout(floatingView, params);
                                return true;
                        }
                        return false;
                    }
                }
        );
    }

    private boolean isViewCollapsed() {
        return floatingView == null || floatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

}
