package np.com.qpay.kycformapp.kyc.formfragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bruce.pickerview.popwindow.DatePickerPopWin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import np.com.qpay.kycformapp.CommonConstants;
import np.com.qpay.kycformapp.MainActivity;
import np.com.qpay.kycformapp.R;
import np.com.qpay.kycformapp.Utils;
import np.com.qpay.kycformapp.custumclasses.QPayProgressDialog;
import np.com.qpay.kycformapp.http.HttpUrlConnectionPost;
import np.com.qpay.kycformapp.kyc.amountpicker.ZonePickerPopWin;
import np.com.qpay.kycformapp.kyc.dto.AddressInfo;
import np.com.qpay.kycformapp.kyc.dto.KYCDetailsInfo;
import np.com.qpay.kycformapp.kyc.interfaces.OnBtnClickedListener;
import np.com.qpay.kycformapp.merchant_databases.QpayMerchantDatabase;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by qpay on 2/16/17.
 */

public class DocumentFormFragment extends Fragment {

    private static final int PICK_ID_FRONT_REQUEST_CAMERA = 111;
    private static final int PICK_ID_FRONT_REQUEST_SELECT_FILE = 112;
    private static final int PICK_ID_BACK_REQUEST_CAMERA = 211;
    private static final int PICK_ID_BACK_REQUEST_SELECT_FILE = 212;

    private int PICK_USER_REQUEST = 1;
    private int PICK_ID_FRONT_REQUEST = 2;
    private int PICK_ID_BACK_REQUEST = 3;

    private String userChoosenTask = "";

    private float mDensity;
    private QpayMerchantDatabase qpayMerchantDatabase;
    private String term_id;
    private String app_id;
    private double lat;
    private double lng;

    private ImageButton userPhotoImageView;
    private ImageButton identificationPhotoFrontImageView;
    private ImageButton identificationPhotoBackImageView;
    private TextView issuedDateTextView;
    private TextView identificationNumberTextView;
    private KYCDetailsInfo kycDetailsInfo;
    private OnBtnClickedListener onBtnClickedListener;
    private TextView issuedFromTextView;
    private List<AddressInfo> allDistrictList;
    private AddressInfo selectedDistrict;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.document_form_layout, container, false);

        qpayMerchantDatabase = new QpayMerchantDatabase(getActivity());

        // TODO: Change TermID and AppID value
        term_id = "ZRGe7xlTHyVqh5OWalXUxQ==";
        app_id = "2A2275920236713";
        lat = 0;
        lng = 0;

        new GetAllDistrictTask().execute();

        mDensity = getActivity().getResources().getDisplayMetrics().density;
        userPhotoImageView = (ImageButton) view.findViewById(R.id.user_photo_image_view);
        identificationPhotoFrontImageView = (ImageButton) view.findViewById(R.id.id_photo_front_image_view);
        identificationPhotoBackImageView = (ImageButton) view.findViewById(R.id.id_photo_back_image_view);

        identificationNumberTextView = (TextView) view.findViewById(R.id.identification_number_text_view);

        issuedDateTextView = (TextView) view.findViewById(R.id.issued_date_text_view);

        issuedFromTextView = (TextView) view.findViewById(R.id.issued_from_text_view);

        issuedFromTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(allDistrictList != null) {
                    setDistrictData();
                }
            }
        });


        final SimpleDateFormat inputSDF = new SimpleDateFormat("yyyy-MM-dd");
        final SimpleDateFormat outputSDF = new SimpleDateFormat("yyyy/MM/dd");

        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.YEAR, -1);
        final Date date = cal.getTime();
        final String today = outputSDF.format(date);
        String thisYear = new SimpleDateFormat("yyyy").format(date);
        int thisYearInt;
        try {
            thisYearInt = Integer.parseInt(thisYear);
        }catch (NumberFormatException nfe){
            thisYearInt = 1990;
            Log.d("KYC", "onCreateView: " + nfe.getMessage());
        }
        issuedDateTextView.setText(today);
        final int finalThisYearInt = thisYearInt;

        issuedDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                String todayFormattedForPicker = "1990-01-01";
                String dateText;
                if(issuedDateTextView.getText() != null) {
                    dateText = issuedDateTextView.getText().toString();
                }else {
                    dateText = today;
                }
                try {
                    todayFormattedForPicker = inputSDF.format(outputSDF.parse(dateText));
                }catch (ParseException pe){

                }
                DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(getActivity(), new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                        Toast.makeText(getActivity(), dateDesc, Toast.LENGTH_SHORT).show();
                        String DOB = today;
                        try {
                            DOB = outputSDF.format(inputSDF.parse(dateDesc));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        issuedDateTextView.setText(DOB);
                    }
                }).textConfirm("SET") //text of confirm button
                        .textCancel("CANCEL") //text of cancel button
                        .btnTextSize((int) (10 * mDensity)) // button text size
                        .viewTextSize((int) (35 * mDensity)) // pick view text size
                        .colorCancel(Color.parseColor("#999999")) //color of cancel button
                        .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                        .minYear(1900) //min year in loop
                        .maxYear(finalThisYearInt) // max year in loop
                        .showDayMonthYear(false) // shows like dd mm yyyy (default is false)
                        .dateChose(todayFormattedForPicker) // date chose when init popwindow
                        .build();
                pickerPopWin.showPopWin(getActivity());
            }
        });

        Button submitButton = (Button) view.findViewById(R.id.submit_btn);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillKycDetails();
                if(checkIfFormCompleted()) {
                    onBtnClickedListener.onSubmitBtnClicked(kycDetailsInfo);
                }else {
                    Toast.makeText(getActivity(), "Fill Form Completely!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button backButton = (Button) view.findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillKycDetails();
                onBtnClickedListener.onBackBtnClicked(kycDetailsInfo, 3);
            }
        });

        userPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(view);
            }
        });

        identificationPhotoFrontImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(view);
            }
        });

        identificationPhotoBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(view);
            }
        });

        setDataInForm(kycDetailsInfo);
        return view;
    }

    private boolean checkIfFormCompleted() {
        if(kycDetailsInfo.getFullName().isEmpty() || kycDetailsInfo.getFatherName().isEmpty() || kycDetailsInfo.getFullName().isEmpty() || kycDetailsInfo.getMotherName().isEmpty() || kycDetailsInfo.getGrandFatherName().isEmpty()
                || kycDetailsInfo.getDateOfBirth().isEmpty() || kycDetailsInfo.getOccupation().isEmpty() || kycDetailsInfo.getToleName().isEmpty()
                || kycDetailsInfo.getZoneId() == -1 || kycDetailsInfo.getDistrictId() == -1 || kycDetailsInfo.getVdcId() == -1
                || kycDetailsInfo.getIdentificationNumber().isEmpty() || kycDetailsInfo.getIdIssuedFrom() == -1 || kycDetailsInfo.getIssuedDate().isEmpty()
                || kycDetailsInfo.getUserImageUri() == null || kycDetailsInfo.getIdFrontImageUri() == null || kycDetailsInfo.getIdBackImageUri() == null){
            return false;
        }

        return true;
    }

    // <editor-fold desc="Hides the soft keyboard">
    public void hideSoftKeyboard() {
        if(getActivity().getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
    // </editor-fold>

    // <editor-fold desc="Set District Data in Picker">
    private void setDistrictData() {
        ZonePickerPopWin.Builder pickerPopWinBuilder = new ZonePickerPopWin.Builder(getActivity(), new ZonePickerPopWin.OnZonePickedListener() {
            @Override
            public void onZonePickCompleted(int month, String dateDesc) {
                selectedDistrict = allDistrictList.get(month);
                issuedFromTextView.setText(selectedDistrict.getName());
                kycDetailsInfo.setIdIssuedFrom(selectedDistrict.getId());
            }
        }).textConfirm("SET") //text of confirm button
                .textCancel("CANCEL") //text of cancel button
                .btnTextSize((int) (10 * mDensity)) // button text size
                .viewTextSize((int) (25 * mDensity)) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setValues(allDistrictList);

        if(selectedDistrict != null) {
            pickerPopWinBuilder.initialValue(selectedDistrict);
        }
        ZonePickerPopWin pickerPopWin = pickerPopWinBuilder.build();
        pickerPopWin.showPopWin(getActivity());
    }
    // </editor-fold>

    private void fillKycDetails() {
        if (issuedDateTextView.getText() != null && !issuedDateTextView.getText().toString().isEmpty()) {
            kycDetailsInfo.setIssuedDate(issuedDateTextView.getText().toString());
        }

        if (identificationNumberTextView.getText() != null && !identificationNumberTextView.getText().toString().isEmpty()) {
            kycDetailsInfo.setIdentificationNumber(identificationNumberTextView.getText().toString());
        }
    }

    public void setOnBtnClickedListener(OnBtnClickedListener onBtnClickedListener) {
        this.onBtnClickedListener = onBtnClickedListener;
    }

    private void selectImage(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        int id = view.getId();
        switch (id) {
            case R.id.user_photo_image_view:
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_USER_REQUEST);
                break;
            case R.id.id_photo_front_image_view:
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_ID_FRONT_REQUEST);
                selectImage(PICK_ID_FRONT_REQUEST);
                break;
            case R.id.id_photo_back_image_view:
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_ID_BACK_REQUEST);
                selectImage(PICK_ID_BACK_REQUEST);
                break;
        }
    }

    private void selectImage(final int viewDefination) {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utils.checkPermission(getActivity());
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent(viewDefination);
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent(viewDefination);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent(int viewDefination)
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(viewDefination == PICK_ID_BACK_REQUEST) {
            startActivityForResult(intent, PICK_ID_BACK_REQUEST_CAMERA);
        }else if(viewDefination == PICK_ID_FRONT_REQUEST){
            startActivityForResult(intent, PICK_ID_FRONT_REQUEST_CAMERA);
        }
    }

    private void galleryIntent(int viewDefination)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        if(viewDefination == PICK_ID_BACK_REQUEST) {
            startActivityForResult(Intent.createChooser(intent, "Select File"),PICK_ID_BACK_REQUEST_SELECT_FILE);
        }else if(viewDefination == PICK_ID_FRONT_REQUEST){
            startActivityForResult(Intent.createChooser(intent, "Select File"),PICK_ID_FRONT_REQUEST_SELECT_FILE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utils.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent(requestCode);
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent(requestCode);
                } else {
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_ID_FRONT_REQUEST_SELECT_FILE || requestCode == PICK_ID_BACK_REQUEST_SELECT_FILE || requestCode == PICK_USER_REQUEST)
                onSelectFromGalleryResult(requestCode,resultCode, data);
            else if (requestCode == PICK_ID_FRONT_REQUEST_CAMERA || requestCode == PICK_ID_BACK_REQUEST_CAMERA)
                onCaptureImageResult(requestCode,resultCode, data);
        }
    }

    private void onSelectFromGalleryResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_USER_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                if(bitmap != null) {
                    kycDetailsInfo.setUserImageUri(uri);
                    userPhotoImageView.setBackground(null);
                    userPhotoImageView.setImageBitmap(Utils.changeBitmapToThumbnail(getActivity(), bitmap, (int) (64 * mDensity)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_ID_FRONT_REQUEST_SELECT_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                if(bitmap != null) {
                    kycDetailsInfo.setIdFrontImageUri(uri);
                    identificationPhotoFrontImageView.setBackground(null);
                    identificationPhotoFrontImageView.setImageBitmap(Utils.changeBitmapToThumbnail(getActivity(), bitmap, (int) (64 * mDensity)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_ID_BACK_REQUEST_SELECT_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                if(bitmap != null) {
                    kycDetailsInfo.setIdBackImageUri(uri);
                    identificationPhotoBackImageView.setBackground(null);
                    identificationPhotoBackImageView.setImageBitmap(Utils.changeBitmapToThumbnail(getActivity(), bitmap, (int) (64 * mDensity)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onCaptureImageResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_USER_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if(bitmap != null) {
                    kycDetailsInfo.setUserImageUri(uri);
                    userPhotoImageView.setBackground(null);
                    userPhotoImageView.setImageBitmap(Utils.changeBitmapToThumbnail(getActivity(), bitmap, (int) (64 * mDensity)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_ID_FRONT_REQUEST_CAMERA && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if(bitmap != null) {
                    kycDetailsInfo.setIdFrontImageUri(uri);
                    identificationPhotoFrontImageView.setBackground(null);
                    identificationPhotoFrontImageView.setImageBitmap(Utils.changeBitmapToThumbnail(getActivity(), bitmap, (int) (64 * mDensity)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_ID_BACK_REQUEST_CAMERA && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if(bitmap != null) {
                    kycDetailsInfo.setIdBackImageUri(uri);
                    identificationPhotoBackImageView.setBackground(null);
                    identificationPhotoBackImageView.setImageBitmap(Utils.changeBitmapToThumbnail(getActivity(), bitmap, (int) (64 * mDensity)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setData(KYCDetailsInfo kycDetailsInfo){
        this.kycDetailsInfo = kycDetailsInfo;
    }

    public void setDataInForm(KYCDetailsInfo kycDetailsInfo) {
        if(kycDetailsInfo != null) {
            if(!kycDetailsInfo.getIdentificationNumber().isEmpty()) {
                identificationNumberTextView.setText(kycDetailsInfo.getIdentificationNumber());
            }
            if(!kycDetailsInfo.getIssuedDate().isEmpty()) {
                issuedDateTextView.setText(kycDetailsInfo.getIssuedDate());
            }
            if(kycDetailsInfo.getUserImageUri() != null){
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), kycDetailsInfo.getUserImageUri());
                    userPhotoImageView.setImageBitmap(Utils.changeBitmapToThumbnail(getActivity(), bitmap, (int) (64 * mDensity)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(kycDetailsInfo.getIdFrontImageUri() != null){
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), kycDetailsInfo.getIdFrontImageUri());
                    identificationPhotoFrontImageView.setImageBitmap(Utils.changeBitmapToThumbnail(getActivity(), bitmap, (int) (64 * mDensity)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(kycDetailsInfo.getIdBackImageUri()!= null){
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), kycDetailsInfo.getIdFrontImageUri());
                    identificationPhotoBackImageView.setImageBitmap(Utils.changeBitmapToThumbnail(getActivity(), bitmap, (int) (64 * mDensity)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(kycDetailsInfo.getIdIssuedFrom() != -1){
                selectedDistrict = qpayMerchantDatabase.selectAllDistrictById(kycDetailsInfo.getIdIssuedFrom());
                issuedFromTextView.setText(selectedDistrict.getName());
                if (qpayMerchantDatabase.isTableExists(QpayMerchantDatabase.ALL_DISTRICT_TABLE_NAME, true)){
                    allDistrictList = qpayMerchantDatabase.selectAllDistrict();
                }
            }
        }
    }

    // <editor-fold desc="CLASS: GetAllDistrictTask">
    class GetAllDistrictTask extends AsyncTask<String, String, String> {

        private QPayProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new QPayProgressDialog(getActivity());
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String response = "";
            try {
                HttpUrlConnectionPost httpUrlConnectionPost = new HttpUrlConnectionPost();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("appId", app_id);
                jsonObject.put("custId", term_id);
                jsonObject.put("lat", lat);
                jsonObject.put("lng", lng);

                response = httpUrlConnectionPost.sendHTTPData(CommonConstants.GET_ALL_DISTRICT, jsonObject);
            }catch (JSONException je){
                je.printStackTrace();
            }
            return  response;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            if(s != null) {
                Log.d("sangharsha", "onPostExecute: " + s);
                try{
                    JSONObject jsonObject = new JSONObject(s).getJSONObject("GetAllDistrictResult");
                    if(jsonObject.getBoolean("success")){
                        if (!qpayMerchantDatabase.isTableExists(QpayMerchantDatabase.ALL_DISTRICT_TABLE_NAME, true)){
                            qpayMerchantDatabase.createAllDistrictTable();
                        }else {
                            qpayMerchantDatabase.dropTable(QpayMerchantDatabase.ALL_DISTRICT_TABLE_NAME);
                            qpayMerchantDatabase.createAllDistrictTable();
                        }
                        JSONArray jDataArr = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jDataArr.length(); i++){
                            JSONObject jData = jDataArr.getJSONObject(i);
                            AddressInfo addressInfo = new AddressInfo();
                            addressInfo.setId(jData.getInt("Id"));
                            addressInfo.setName(jData.getString("District"));
                            qpayMerchantDatabase.populateAllDistrictTable(addressInfo);
                        }

                        allDistrictList = qpayMerchantDatabase.selectAllAllDistrict();
                    }
                }catch (JSONException je){

                }
            }else {

            }
        }
    }
    // </editor-fold>

}
