package com.project.thamani.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.thamani.R;
import com.project.thamani.adapter.NotesAdapter;
import com.project.thamani.app.AppConfig;
import com.project.thamani.app.AppController;
import com.project.thamani.database.DatabaseHelper;
import com.project.thamani.helper.MyDividerItemDecoration;
import com.project.thamani.helper.RecyclerTouchListener;
import com.project.thamani.helper.SQLiteHandler;
import com.project.thamani.helper.SessionManager;
import com.project.thamani.model.Note;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

//    @BindView(R.id.btn_bottom_sheet)
//    Button btnBottomSheet;
    @BindView(R.id.proceed)
    Button proceed;
    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.vat)
    TextView vatt;
    @BindView(R.id.sub)
    TextView subb;
    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;

    BottomSheetBehavior sheetBehavior;

    private EditText code;
    private LinearLayout search,btn_scan;
    private NotesAdapter mAdapter;
    private List<Note> notesList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private LinearLayout noNotesView;
    private Double total_price,vat,sub;
    private String userid,barcode;

    private DatabaseHelper db;
    private SQLiteHandler user_db;
    private SessionManager session;

    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
// Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        noNotesView=(LinearLayout) findViewById(R.id.noNotesView);

        // SqLite database handler
        user_db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from SQLite
        HashMap<String, String> user = user_db.getUserDetails();

//        String name = user.get("name");
//        String email = user.get("email");
        userid=user.get("u_id");

        db = new DatabaseHelper(this);

        notesList.addAll(db.getAllNotes());



        code=(EditText) findViewById(R.id.barcode);
        search=(LinearLayout) findViewById(R.id.btn_send);
        btn_scan=(LinearLayout) findViewById(R.id.btn_scan);
        recyclerView = findViewById(R.id.recyler_view);

        code.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                   btn_scan.setVisibility(View.GONE);
                   search.setVisibility(View.VISIBLE);

                }else {
                    btn_scan.setVisibility(View.VISIBLE);
                    search.setVisibility(View.GONE);
                }
            }
        });
        code.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length()==0){
//                    btn_scan.setVisibility(View.VISIBLE);
//                    search.setVisibility(View.GONE);
                    code.clearFocus();
                    //code.getText()
                }else {
//                    btn_scan.setVisibility(View.GONE);
//                    search.setVisibility(View.VISIBLE);
                }

            }
        });


        mAdapter = new NotesAdapter(this, notesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 0));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyNotes();

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(MainActivity.this, PayActivity.class);
                intent.putExtra("total", total_price);
                startActivity(intent);

            }
        });
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);

            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchcode = code.getText().toString().trim();



                // Check for empty data in the form
                if (!searchcode.isEmpty() ) {
                    // login user
                    getItem(searchcode,userid);
                    code.getText().clear();

                    code.clearFocus();
                  //  code.clearComposingText();

                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the barcode!", Toast.LENGTH_LONG)
                            .show();
                    code.clearFocus();
//                    search.setFocusable(true);
//                    search.setFocusableInTouchMode(true);
                }
            }
        });


        barcode = getIntent().getStringExtra("code");
       // Toast.makeText(MainActivity.this,barcode,Toast.LENGTH_LONG).show();
        // close the activity in case of empty barcode
        if (!TextUtils.isEmpty(barcode)) {
            Toast.makeText(MainActivity.this,barcode,Toast.LENGTH_LONG).show();

            getItem(barcode,userid);

        }else{
          //  Toast.makeText(getApplicationContext(), "Barcode is empty, Scan again!", Toast.LENGTH_LONG).show();
        }

        //copute total
        addToPrice();
        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
//                        btnBottomSheet.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
//                        btnBottomSheet.setText("Expand Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    /**
     * manually opening / closing bottom sheet on button click
     */
//    @OnClick(R.id.btn_bottom_sheet)
//    public void toggleBottomSheet() {
//        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
//            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
////            btnBottomSheet.setText("Close sheet");
//        } else {
//            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
////            btnBottomSheet.setText("Expand sheet");
//        }
//    }

    /**
     * showing bottom sheet dialog
     */
//    @OnClick(R.id.select)
//    public void showBottomSheetDialog() {
//        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);
//
//        BottomSheetDialog dialog = new BottomSheetDialog(this);
//        dialog.setContentView(view);
//        dialog.show();
//    }

    /**
     * Inserting new note in db
     * and refreshing the list
     */
    private void createNote(String uuid,String item,String price,String gtin,String warehouse,String wid,String manufacturer,String mid,String gs1,String retailer) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(uuid,item,price,gtin,warehouse,wid,manufacturer,mid,gs1,retailer);

        // get the newly inserted note from db
        Note n = db.getNote(id);

        if (n != null) {
            // adding new note to array list at 0 position
            notesList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            addToPrice();

            toggleEmptyNotes();
        }
    }

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
//    private void updateNote(String items, int position) {
//        Note n = notesList.get(position);
//        // updating note text
//        n.setItems(items);
//
//        // updating note in db
//        db.updateNote(n);
//
//        // refreshing the list
//        notesList.set(position, n);
//        mAdapter.notifyItemChanged(position);
//
//        addToPrice();
//
//        toggleEmptyNotes();
//    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteNote(int position) {
        // deleting the note from db
        db.deleteNote(notesList.get(position));

        // removing the note from the list
        notesList.remove(position);
        mAdapter.notifyItemRemoved(position);
        addToPrice();

        toggleEmptyNotes();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Delete Item"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    deleteNote(position);

//                    showNoteDialog(true, notesList.get(position), position);
                } else {
                }
            }
        });
        builder.show();
    }

    /**
     * function to verify login details in mysql db
     * */
    private void getItem(final String barcode,String retailer) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Searching Barcode...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_SEARCH+retailer+"/"+barcode, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
//                code.setText("Enter item barcode");
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject data = jObj.getJSONObject("data");
//                    boolean error = jObj.getBoolean("error");
//
//                    // Check for error node in json
                    if (data != null) {
                        // user successfully logged in
                        // Create login session
//                        session.setLogin(true);

                        // Now store the user in SQLite
//                        String uid = jObj.getString("uid");
//                        String id = jObj.getString("id");



                        String uuid = data.getString("item id");
                        String item = data.getString("item name");
                        String price = data.getString("price");
                        String manufacturer = data.getString("manufacturer");
                        String mid = data.getString("mid");
                        String warehouse = data.getString("warehouse");
                        String wid = data.getString("wid");
                        String gs1 = data.getString("gs1");
                        String retailer = data.getString("retailer");
                        String gtin = data.getString("GTIN");

                        createNote(uuid, item, price,gtin,warehouse,wid,manufacturer,mid,gs1,retailer);

                        addToPrice();

                        toggleEmptyNotes();
                        // Inserting row in users table
//                        db.addItems(uuid, item, price,gtin,items);

                        // Add main activity
//                        Intent intent = new Intent(LoginActivity.this,
//                                MainActivity.class);
//                        startActivity(intent);
//                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = "Product does not exist";
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {



        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
//    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position) {
//        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
//        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);
//
//        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
//        alertDialogBuilderUserInput.setView(view);
//
//        final EditText inputNote = view.findViewById(R.id.items);
//        TextView dialogTitle = view.findViewById(R.id.dialog_title);
//        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));
//
//        if (shouldUpdate && note != null) {
//            inputNote.setText(note.getItems());
//        }
//        alertDialogBuilderUserInput
//                .setCancelable(false)
//                .setPositiveButton(shouldUpdate ? "update" : "Add", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialogBox, int id) {
//
//                    }
//                })
//                .setNegativeButton("cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialogBox, int id) {
//                                dialogBox.cancel();
//                            }
//                        });
//
//        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
//        alertDialog.show();
//
//        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Show toast message when no text is entered
//                if (TextUtils.isEmpty(inputNote.getText().toString())) {
//                    Toast.makeText(MainActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();
//                    return;
//                } else {
//                    alertDialog.dismiss();
//                }
//
//                // check if user updating note
//                if (shouldUpdate && note != null) {
//                    // update note by it's id
//                    updateNote(inputNote.getText().toString(), position);
//                } else {
//                    // create new note
////                    createNote(inputNote.getText().toString());
//                }
//            }
//        });
//    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
//    private void showAddDialog(final boolean shouldUpdate, final Note note, final int position,final String uuid,final String item,final String price,final String gtin,final String warehouse,final String wid,final String manufacturer,final String mid,final String gs1,final String retailer) {
//        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
//        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);
//
//        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
//        alertDialogBuilderUserInput.setView(view);
//
////        final EditText inputNote = view.findViewById(R.id.items);
//        TextView dialogTitle = view.findViewById(R.id.dialog_title);
//        TextView name2 = view.findViewById(R.id.name);
//        TextView price2 = view.findViewById(R.id.price);
//
//        name2.setText("Product: "+ item);
//        price2.setText("Price: Ksh."+ price);



//                        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

//        if (shouldUpdate && note != null) {
//            inputNote.setText(note.getItems());
//        }
//        alertDialogBuilderUserInput
//                .setCancelable(false)
//                .setPositiveButton(shouldUpdate ? "update" : "add item", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialogBox, int id) {
//
//                    }
//                })
//                .setNegativeButton("cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialogBox, int id) {
//                                dialogBox.cancel();
//                            }
//                        });
//
//        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
//        alertDialog.show();
//
//        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Show toast message when no text is entered
////                if (TextUtils.isEmpty(inputNote.getText().toString())) {
////                    Toast.makeText(MainActivity.this, "Enter Quantity!", Toast.LENGTH_SHORT).show();
////                    return;
////                } else {
////                    alertDialog.dismiss();
////                }
//
//                // check if user updating note
//                if (shouldUpdate && note != null) {
//                    // update note by it's id
////                    updateNote(inputNote.getText().toString(), position);
//                } else {
//                    // create new note
//                    alertDialog.dismiss();
//                }
//            }
//        });
//    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);
        db.deleteItems();
        user_db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotes() {
        // you can check notesList.size() > 0

        if (db.getNotesCount() > 0) {
            noNotesView.setVisibility(View.GONE);
            proceed.setEnabled(true);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
            proceed.setEnabled(false);

        }
    }
    private void addToPrice(){

        total_price=db.getTotalPrice();

        total.setText("Ksh."+total_price.toString());
        //compute vat
        vat=total_price*0.16;
        vatt.setText(String.format("Ksh. %.2f",vat));

        sub=total_price-vat;
        subb.setText("Ksh."+sub.toString());
    }
    /**
     * showing bottom sheet dialog fragment
     * same layout is used in both dialog and dialog fragment
     */
//    @OnClick(R.id.btn_bottom_sheet_dialog_fragment)
//    public void showBottomSheetDialogFragment() {
//        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
//        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sale, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else
        if (id == R.id.action_logout) {
            logoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
