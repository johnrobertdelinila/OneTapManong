package com.example.johnrobertdelinila.onetapmanong;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;

public class CheckListActivity extends AppCompatActivity implements VerticalStepperForm {

    public VerticalStepperFormLayout verticalStepperFormLayout;
    public int currentStepNumber = -1;
    public static final int PICK_IMAGE_REQUEST_CODE = 2006;
    public static int resultStepNumber = -1;
    public static final int FINISH_ACTIVITY = 3000;

    public Service service;
    private String[] mySteps;
    private String[] subTitles;
    private ArrayList<Boolean> isInput;
    private ArrayList<ArrayList<String>> answers;
    private ArrayList<Integer> viewTypes;
    private ArrayList<Adapter> adapters;


    private static final String randomStringInside = "xjXgGdHTpi";
    private static final String randomStringOutside = "nXMk8bjWv7";

    private ProgressDialog progressDialog;

    DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("Request");
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private ArrayList<String> downloadUrls;
    public ArrayList<Bitmap> final_images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        appBar();

        service = (Service) getIntent().getSerializableExtra("service");
        adapters = new ArrayList<>();

        TextView textView = findViewById(R.id.text_title);
        textView.setText(service.getServiceName());

        service.getTitle().add("Summary");
        service.getSubtitle().add("Confirm Request");

        mySteps = service.getTitle().toArray(new String[service.getTitle().size()]);
        subTitles = service.getSubtitle().toArray(new String[service.getSubtitle().size()]);
        isInput = service.getIsInput();
        viewTypes = service.getViewTypes();
        answers = service.getAnswers();

        isInput.add(false);
        viewTypes.add(6);
        answers.add(null);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait..");

        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        int colorPrimaryDark = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
        int colorAccent = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);

        verticalStepperFormLayout = findViewById(R.id.vertical_stepper_form);
        VerticalStepperFormLayout.Builder.newInstance(verticalStepperFormLayout, mySteps, this, this)
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .displayBottomNavigation(false)
                .showVerticalLineWhenStepsAreCollapsed(false)
                .stepsSubtitles(subTitles)
                .init();

        downloadUrls = new ArrayList<>();

    }

    @Override
    public View createStepContentView(int stepNumber) {
        View view;
        view = createView(viewTypes.get(stepNumber), answers.get(stepNumber), isInput.get(stepNumber), stepNumber);
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private View createView(int viewType, ArrayList<String> answers, Boolean isInput, final int stepNumber) {

        if (viewType == 6) {
            LayoutInflater inflater = LayoutInflater.from(getBaseContext());
            adapters.add(new Adapter(CheckListActivity.this, false, false, answers, isInput, false, stepNumber));
            LinearLayout view = (LinearLayout) inflater.inflate(R.layout.layout_summary, null, false);
            return view;
        }else {
            Boolean isTextField = null;
            Boolean isCheckBox = null;
            Boolean isaAttachment = null;
            if (viewType == 1) {
                isCheckBox = true;
                isTextField = null;
            }else if (viewType == 2) {
                isCheckBox = false;
                isTextField = null;
            }else  if (viewType == 3) {
                answers = null;
                isTextField = true;
            }else if (viewType == 4) {
                answers = null;
                isTextField = false;
            }else if (viewType == 5) {
                answers = null;
                isaAttachment = true;
            }

            Adapter adapter = new Adapter(CheckListActivity.this, isTextField, isCheckBox, answers, isInput, isaAttachment, stepNumber);
            adapters.add(adapter);

            // STARTS HERE
            LayoutInflater inflater = LayoutInflater.from(getBaseContext());
            final LinearLayout view;

            if (isTextField != null && !isTextField) {
                view = (LinearLayout) inflater.inflate(R.layout.layout_listview_fordate, null, false);
            }else if (answers != null && answers.size() <= 4){
                view = (LinearLayout) inflater.inflate(R.layout.layout_listview_small, null, false);
            } else {
                view = (LinearLayout) inflater.inflate(R.layout.layout_listview, null, false);
            }
            ListView listView = view.findViewById(R.id.listView);
            listView.setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);

            if (isTextField == null) {
                listView.setOnTouchListener(new View.OnTouchListener() {
                    // Setting on Touch Listener for handling the touch inside ScrollView
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Disallow the touch request for parent scroll on touch of child view
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        adapters.get(stepNumber).changeItemChecked(position);
                    }
                });
            }

            listView.setAdapter(adapters.get(stepNumber));
            return view;
        }

    }

    @Override
    public void onStepOpening(int stepNumber) {
        saveCurrentStepNumber(stepNumber);
        checkIfOptional();
        verticalStepperFormLayout.setActiveStepAsCompleted();
    }

    private void saveCurrentStepNumber(int stepNumber) {
        if (stepNumber < Arrays.asList(subTitles).size()) {
            currentStepNumber = stepNumber;
        }
    }

    private void checkIfOptional() {
        if (currentStepNumber < Arrays.asList(subTitles).size()) {
            if (Arrays.asList(subTitles).get(currentStepNumber).equals("")) {
                // IF REQUIRED
                if (adapters.get(currentStepNumber).getSelectedData().size() > 0) {
                    verticalStepperFormLayout.setActiveStepAsCompleted();
                }else {
                    verticalStepperFormLayout.setActiveStepAsUncompleted(null);
                }
            }else {
                // IF OPTIONAL
                verticalStepperFormLayout.setActiveStepAsCompleted();
            }
            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (Arrays.asList(subTitles).get(currentStepNumber).equals("")) {
                        // IF REQUIRED
                        if (adapters.get(currentStepNumber).getSelectedData().size() > 0) {
                            verticalStepperFormLayout.setActiveStepAsCompleted();
                        }else {
                            verticalStepperFormLayout.setActiveStepAsUncompleted(null);
                        }
                    }else {
                        // IF OPTIONAL
                        verticalStepperFormLayout.setActiveStepAsCompleted();
                    }
                    handler.postDelayed(this, 400);
                }
            };
            handler.postDelayed(runnable, 400);
        }
    }

    @Override
    public void sendData() {

        boolean continue_method = true;
        for (int i = 0; i < adapters.size(); i++) {
            if (adapters.get(i).getSelectedData().size() <= 0 && Arrays.asList(subTitles).get(i).equals("")) {
                verticalStepperFormLayout.setStepAsUncompleted(i, "required");
                continue_method = false;
                break;
            }
        }
        if (continue_method) {
            // Insert data to database

            StringBuilder serviceAnswers = new StringBuilder();
            ArrayList<String> final_answers = new ArrayList<>();
            final_images = new ArrayList<>();

            for (int i = 0; i < Arrays.asList(mySteps).size(); i++) {
                if (i != Arrays.asList(mySteps).size() - 1) {
                    if (viewTypes.get(i) == 5) {
                        ArrayList<Bitmap> images = adapters.get(i).getSelectedImages();
                        for (int y = 0; y < images.size(); y++) {
                            if (images.get(y) != null) {
                                final_images.add(images.get(y));
                            }
                        }
                        final_answers.add(String.valueOf(images));
                    }else {
                        if (i != Arrays.asList(mySteps).size() - 2) {
                            serviceAnswers.append(concatAnswers(adapters.get(i).getSelectedData()) + randomStringOutside);
                        }else {
                            serviceAnswers.append(concatAnswers(adapters.get(i).getSelectedData()));
                        }

                        final_answers.add(String.valueOf(adapters.get(i).getSelectedData()));
                    }
                }

            }

            Log.e("SUMMARY ", String.valueOf(final_answers));
            Log.e("FINAL ANSWER ", serviceAnswers.toString());
            Log.e("FINAL QUESTIONS ", concatQuestions(mySteps));
            Log.e("IMAGES", String.valueOf(final_images));

            // Firebase Database

            Request request = new Request();
            request.setAnswers(serviceAnswers.toString());
            request.setQuestions(concatQuestions(mySteps));
            request.setServiceName(service.getServiceName());
            request.setImages("none");
            request.setLatitude(service.getLatitude());
            request.setLongtitude(service.getLongtitude());
            request.setLocationName(service.getLocationName());

            progressDialog.show();
            final String futureKey = requestRef.child("iyottayo").push().getKey();
            requestRef.child(service.getServiceName()).child(futureKey).setValue(request)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // UPLOAD IMAGE IF THERE IS IMAGE
                            if (final_images.size() != 0) {
                                for (int i = 0; i < final_images.size(); i++) {
                                    uploadImage(final_images.get(i), i, futureKey);
                                }
                            }else {
                                finishActivityOnSuccess();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(CheckListActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void finishActivityOnSuccess() {
        progressDialog.dismiss();
        Intent intent = new Intent(CheckListActivity.this, MapsActivity.class);
        intent.putExtra("iyot", "iyot");
        setResult(RESULT_OK, intent);
        Toast.makeText(CheckListActivity.this, "Sucessfully saved in database!!!", Toast.LENGTH_LONG).show();
        finish();
    }

    private void uploadImage(Bitmap bitmap, final int count, final String futureKey) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference imageUpload = firebaseStorage.getReference().child("images").child(randomName() + ".jpg");
        imageUpload.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUrls.add(taskSnapshot.getDownloadUrl().toString());
                        if (count == (downloadUrls.size() - 1)) {
                            Log.e("URL SIZE", String.valueOf(downloadUrls.size()));
                            uploadImageUrlsToFirebase(futureKey);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ERROR UPLOADING", e.getMessage());
                    }
                });
    }

    private void uploadImageUrlsToFirebase(String futurekey) {
        final String newDownloadUrlStr = concatUrls(downloadUrls);
        HashMap<String, Object> childImages = new HashMap<>();
        childImages.put("images", newDownloadUrlStr);
        requestRef.child(service.getServiceName()).child(futurekey).updateChildren(childImages)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("URLS", newDownloadUrlStr);
                        finishActivityOnSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.e("UPLOADING DATABASE", e.getMessage());
                    }
                });
    }

    private String concatUrls(ArrayList<String> url) {
        StringBuilder output_questions = new StringBuilder();

        for (int i = 0; i < url.size(); i++) {
            if (i != url.size() - 1) {
                output_questions.append(url.get(i) + randomStringOutside);
            }else {
                output_questions.append(url.get(i));
            }
        }

        return output_questions.toString();
    }

    private String randomName() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    private String concatAnswers(ArrayList<String> data) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < data.size(); i++) {
            if (i != data.size() - 1) {
                builder.append(data.get(i) + randomStringInside);
            }else {
                builder.append(data.get(i));
            }
        }
        return builder.toString();
    }

    private String concatQuestions(String[] questions) {
        StringBuilder output_questions = new StringBuilder();
        for (int i = 0; i < Arrays.asList(questions).size(); i++) {
            if (i != Arrays.asList(questions).size() - 1) {
                if (i != Arrays.asList(questions).size() - 2) {
                    output_questions.append(questions[i] + randomStringOutside);
                }else {
                    output_questions.append(questions[i]);
                }
            }
        }
        return output_questions.toString();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri filePath = data.getData();
            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(CheckListActivity.this.getContentResolver(), filePath);
//                selectedImages.add(image);
                adapters.get(resultStepNumber).insertBitmap(image);
            }catch (Exception e) {

                e.printStackTrace();
                Log.e("IMAGE ERROR", e.getMessage());
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }

    private void appBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
//        final Drawable upArrow;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
//        }
//        else {
//            upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//        }
        upArrow.setColorFilter(getResources().getColor(R.color.backArrow), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        CheckListActivity.this.setTitle("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
