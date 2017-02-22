package np.com.qpay.kycformapp.kyc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import np.com.qpay.kycformapp.CommonConstants;
import np.com.qpay.kycformapp.FinishedActivity;
import np.com.qpay.kycformapp.R;
import np.com.qpay.kycformapp.Utils;
import np.com.qpay.kycformapp.custumclasses.CircularTextView;
import np.com.qpay.kycformapp.custumclasses.FontTextViewRegular;
import np.com.qpay.kycformapp.custumclasses.QPayProgressDialog;
import np.com.qpay.kycformapp.http.HttpUrlConnectionPost;
import np.com.qpay.kycformapp.kyc.dto.KYCDetailsInfo;
import np.com.qpay.kycformapp.kyc.formfragments.ContactFormFragment;
import np.com.qpay.kycformapp.kyc.formfragments.DocumentFormFragment;
import np.com.qpay.kycformapp.kyc.formfragments.PersonalHistoryFormFragment;
import np.com.qpay.kycformapp.kyc.interfaces.OnBtnClickedListener;
import np.com.qpay.kycformapp.merchant_databases.QpayMerchantDatabase;

/**
 * Created by qpay on 2/16/17.
 */

public class KycFormFragment extends Fragment implements OnBtnClickedListener {

    private CircularTextView firstFormIndex, secondFormIndex, thirdFormIndex;
    private FontTextViewRegular firstFormIndexTextView, secondFormIndexTextView, thirdFormIndexTextView;

    private int currentFormIndex = 1;
    private int previousFormIndex = 0;
    private String term_id;
    private String app_id;
    private double lat;
    private double lng;

    private KYCDetailsInfo kycDetailsInfo;
    private QpayMerchantDatabase qpayMerchantDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.kyc_form_frag, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // TODO: Change TermID and AppID value
        term_id = "ZRGe7xlTHyVqh5OWalXUxQ==";
        app_id = "2A2275920236713";
        lat = 0;
        lng = 0;

        qpayMerchantDatabase = new QpayMerchantDatabase(getActivity());
        if(qpayMerchantDatabase.isTableExists(QpayMerchantDatabase.ZONE_TABLE_NAME, true)){
            qpayMerchantDatabase.dropTable(QpayMerchantDatabase.ZONE_TABLE_NAME);
        }

        if(qpayMerchantDatabase.isTableExists(QpayMerchantDatabase.DISTRICT_TABLE_NAME, true)){
            qpayMerchantDatabase.dropTable(QpayMerchantDatabase.DISTRICT_TABLE_NAME);
        }

        if(qpayMerchantDatabase.isTableExists(QpayMerchantDatabase.VDC_TABLE_NAME, true)){
            qpayMerchantDatabase.dropTable(QpayMerchantDatabase.VDC_TABLE_NAME);
        }

        if(qpayMerchantDatabase.isTableExists(QpayMerchantDatabase.ALL_DISTRICT_TABLE_NAME, true)){
            qpayMerchantDatabase.dropTable(QpayMerchantDatabase.ALL_DISTRICT_TABLE_NAME);
        }

        kycDetailsInfo = new KYCDetailsInfo();

        firstFormIndex = (CircularTextView) view.findViewById(R.id.first_page_index);
        secondFormIndex = (CircularTextView) view.findViewById(R.id.second_page_index);
        thirdFormIndex = (CircularTextView) view.findViewById(R.id.third_page_index);

        firstFormIndexTextView = (FontTextViewRegular) view.findViewById(R.id.first_page_index_text_view);
        secondFormIndexTextView = (FontTextViewRegular) view.findViewById(R.id.second_page_index_text_view);
        thirdFormIndexTextView = (FontTextViewRegular) view.findViewById(R.id.third_page_index_text_view);

        firstFormIndex.setStrokeWidth(1);
        firstFormIndex.setSolidColor(getContext().getResources().getColor(R.color.active_tab_color));

        secondFormIndex.setStrokeWidth(1);
        secondFormIndex.setSolidColor(getContext().getResources().getColor(R.color.inactive_tab_color));

        thirdFormIndex.setStrokeWidth(1);
        thirdFormIndex.setSolidColor(getContext().getResources().getColor(R.color.inactive_tab_color));
        changeFragment();

//        firstFormIndex.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onFirstFormIndexSelected();
//            }
//        });
//
//        secondFormIndex.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onSecondFormIndexSelected();
//
//            }
//        });
//
//        thirdFormIndex.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onThirdFormIndexSelected();
//            }
//        });

        return view;
    }

    private void onThirdFormIndexSelected(boolean isBackBtnPressed) {
        int tempPrevFormIndex = previousFormIndex;

        if(currentFormIndex < previousFormIndex) {
            changeFragment();
        } else {
            if(!kycDetailsInfo.getToleName().isEmpty() && kycDetailsInfo.getVdcId() != -1 && kycDetailsInfo.getZoneId() != -1 && kycDetailsInfo.getDistrictId() != -1) {
                changeFragment();
            }else {
                Toast.makeText(getActivity(), "Fill All the Form", Toast.LENGTH_LONG).show();
                currentFormIndex = previousFormIndex;
                previousFormIndex = tempPrevFormIndex;
                return;
            }
        }
        firstFormIndex.setStrokeWidth(1);
        firstFormIndex.setSolidColor(getContext().getResources().getColor(R.color.inactive_tab_color));

        secondFormIndex.setStrokeWidth(1);
        secondFormIndex.setSolidColor(getContext().getResources().getColor(R.color.inactive_tab_color));

        thirdFormIndex.setStrokeWidth(1);
        thirdFormIndex.setSolidColor(getContext().getResources().getColor(R.color.active_tab_color));

        firstFormIndexTextView.setTextColor(getContext().getResources().getColor(R.color.inactive_tab_text_color));
        secondFormIndexTextView.setTextColor(getContext().getResources().getColor(R.color.inactive_tab_text_color));
        thirdFormIndexTextView.setTextColor(getContext().getResources().getColor(R.color.active_tab_text_color));
    }

    private void onSecondFormIndexSelected(boolean isBackBtnPressed) {
        int tempPrevFormIndex = previousFormIndex;

        if(currentFormIndex < previousFormIndex) {
            changeFragment();
        } else {
            if(!kycDetailsInfo.getFullName().isEmpty() && !kycDetailsInfo.getFatherName().isEmpty() && !kycDetailsInfo.getMotherName().isEmpty() && !kycDetailsInfo.getGrandFatherName().isEmpty() && !kycDetailsInfo.getOccupation().isEmpty()) {
                changeFragment();
            }else {
                Toast.makeText(getActivity(), "Fill All the Form", Toast.LENGTH_LONG).show();
                currentFormIndex = previousFormIndex;
                previousFormIndex = tempPrevFormIndex;
                return;
            }
        }
        firstFormIndex.setStrokeWidth(1);
        firstFormIndex.setSolidColor(getContext().getResources().getColor(R.color.inactive_tab_color));

        secondFormIndex.setStrokeWidth(1);
        secondFormIndex.setSolidColor(getContext().getResources().getColor(R.color.active_tab_color));

        thirdFormIndex.setStrokeWidth(1);
        thirdFormIndex.setSolidColor(getContext().getResources().getColor(R.color.inactive_tab_color));

        firstFormIndexTextView.setTextColor(getContext().getResources().getColor(R.color.inactive_tab_text_color));
        secondFormIndexTextView.setTextColor(getContext().getResources().getColor(R.color.active_tab_text_color));
        thirdFormIndexTextView.setTextColor(getContext().getResources().getColor(R.color.inactive_tab_text_color));
    }

    private void onFirstFormIndexSelected(boolean isBackBtnPressed) {
        firstFormIndex.setStrokeWidth(1);
        firstFormIndex.setSolidColor(getContext().getResources().getColor(R.color.active_tab_color));

        secondFormIndex.setStrokeWidth(1);
        secondFormIndex.setSolidColor(getContext().getResources().getColor(R.color.inactive_tab_color));

        thirdFormIndex.setStrokeWidth(1);
        thirdFormIndex.setSolidColor(getContext().getResources().getColor(R.color.inactive_tab_color));

        firstFormIndexTextView.setTextColor(getContext().getResources().getColor(R.color.active_tab_text_color));
        secondFormIndexTextView.setTextColor(getContext().getResources().getColor(R.color.inactive_tab_text_color));
        thirdFormIndexTextView.setTextColor(getContext().getResources().getColor(R.color.inactive_tab_text_color));
        if(currentFormIndex != previousFormIndex){
            changeFragment();
        }
    }

    private void changeFragment(){
        if(previousFormIndex == 0){
            // add Personal History Fragment
            addPersonalHistoryForm();
        } else if(currentFormIndex == 1){
            // add Personal History Fragment
            replaceToPersonalHistoryForm();
        } else if(currentFormIndex == 2){
            // add Contact Fragment
            replaceToContactForm();
        } else if(currentFormIndex == 3){
            // add Documents Fragment
            replaceToDocumentsForm();
        }
    }

    private void addPersonalHistoryForm() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        PersonalHistoryFormFragment personalHistoryFormFragment = new PersonalHistoryFormFragment();
        personalHistoryFormFragment.setOnBtnClickedListener(this);
        personalHistoryFormFragment.setData(kycDetailsInfo);
        Bundle bundle = new Bundle();
        personalHistoryFormFragment.setArguments(bundle);
        transaction.add(R.id.form_frag_container, personalHistoryFormFragment);
        transaction.commit();
    }

    private void replaceToPersonalHistoryForm() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        PersonalHistoryFormFragment personalHistoryFormFragment = new PersonalHistoryFormFragment();
        personalHistoryFormFragment.setData(kycDetailsInfo);
        personalHistoryFormFragment.setOnBtnClickedListener(this);
        Bundle bundle = new Bundle();
        personalHistoryFormFragment.setArguments(bundle);
        if(currentFormIndex > previousFormIndex){
            //enter from right
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left);
        }else {
            //enter from right
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right);
        }
        transaction.replace(R.id.form_frag_container, personalHistoryFormFragment);
        transaction.commit();
    }

    private void replaceToContactForm(){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        ContactFormFragment contactFormFragment = new ContactFormFragment();
        contactFormFragment.setData(kycDetailsInfo);
        contactFormFragment.setOnBtnClickedListener(this);
        Bundle bundle = new Bundle();
        contactFormFragment.setArguments(bundle);
        if(currentFormIndex > previousFormIndex){
            //enter from right
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left);
        }else {
            //enter from right
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right);
        }
        transaction.replace(R.id.form_frag_container, contactFormFragment);
        transaction.commit();
    }

    private void replaceToDocumentsForm(){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        DocumentFormFragment documentFormFragment = new DocumentFormFragment();
        documentFormFragment.setData(kycDetailsInfo);
        documentFormFragment.setOnBtnClickedListener(this);
        Bundle bundle = new Bundle();
        documentFormFragment.setArguments(bundle);
        if(currentFormIndex > previousFormIndex) {
            //enter from right
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left);
        }else {
            //enter from right
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right);
        }
        transaction.replace(R.id.form_frag_container, documentFormFragment);
        transaction.commit();
    }

    @Override
    public void onNextBtnClicked(KYCDetailsInfo kycDetailsInfo, int currentIndex) {
        this.kycDetailsInfo = kycDetailsInfo;
        previousFormIndex = currentFormIndex;
        currentFormIndex = currentIndex + 1;
        if(currentIndex + 1 == 1){
            onFirstFormIndexSelected(false);
        } else if(currentIndex + 1 == 2) {
            onSecondFormIndexSelected(false);
        } else if(currentIndex + 1 == 3){
            onThirdFormIndexSelected(false);
        }
    }

    @Override
    public void onBackBtnClicked(KYCDetailsInfo kycDetailsInfo, int currentIndex) {
        this.kycDetailsInfo = kycDetailsInfo;
        previousFormIndex = currentFormIndex;
        currentFormIndex = currentIndex - 1;
        if(currentIndex - 1 == 1){
            onFirstFormIndexSelected(true);
        } else if(currentIndex - 1 == 2) {
            onSecondFormIndexSelected(true);
        } else if(currentIndex - 1 == 3){
            onThirdFormIndexSelected(true);
        }
    }

    @Override
    public void onSubmitBtnClicked(KYCDetailsInfo kycDetailsInfo) {
        this.kycDetailsInfo = kycDetailsInfo;
        new KycFormSubmitTask().execute();

    }

    class KycFormSubmitTask extends AsyncTask<String, String, String> {

        private QPayProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new QPayProgressDialog(getContext());
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONObject jsonObject = setParams();
            Log.d("sangharsha", "doInBackground: " + jsonObject.toString());
            HttpUrlConnectionPost httpUrlConnectionPost = new HttpUrlConnectionPost();
            return httpUrlConnectionPost.sendHTTPData(CommonConstants.POST_KYC_INFO, jsonObject);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("sangharsha", "onPostExecute: " + s);
            dialog.dismiss();
            try{
                JSONObject jsonObject = new JSONObject(s).getJSONObject("PostKycInfoResult");
                if(jsonObject.getBoolean("success")) {
                    Intent intent = new Intent(getContext(), FinishedActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }else {
                    Toast.makeText(getContext(), "Because of some problem, we cannot submit KYC form. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }catch (JSONException je){

            }
        }

        private JSONObject setParams(){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("appId", app_id);
                jsonObject.put("custId", term_id);
                jsonObject.put("customerName", kycDetailsInfo.getFullName());
                jsonObject.put("dateOfBirth", kycDetailsInfo.getDateOfBirth());
                jsonObject.put("districtId", kycDetailsInfo.getDistrictId());
                jsonObject.put("fatherName", kycDetailsInfo.getFatherName());
                String gender;
                if(kycDetailsInfo.getGender() == 0){
                    gender= "Male";
                }else if (kycDetailsInfo.getGender() == 1){
                    gender= "Female";
                }else {
                    gender= "Others";
                }
                jsonObject.put("gender", gender);
                jsonObject.put("grandFatherName", kycDetailsInfo.getGrandFatherName());
                jsonObject.put("idIssuedDate", kycDetailsInfo.getIssuedDate());
                jsonObject.put("idIssuedFrom", kycDetailsInfo.getIdIssuedFrom());
                jsonObject.put("identification", kycDetailsInfo.getIdentificationNumber());
                jsonObject.put("lat", lat);
                jsonObject.put("lng", lng);
                jsonObject.put("motherName", kycDetailsInfo.getMotherName());
                jsonObject.put("occupation", kycDetailsInfo.getOccupation());
                jsonObject.put("tole", kycDetailsInfo.getToleName());
                jsonObject.put("typeOfId", "citizenship");
                jsonObject.put("vdcMunicipalityId", kycDetailsInfo.getVdcId());
                jsonObject.put("zoneId", kycDetailsInfo.getZoneId());
                if(kycDetailsInfo.getUserImageUri() != null){
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), kycDetailsInfo.getUserImageUri());
                        jsonObject.put("imgPhoto", Utils.bitmapToBase64(bitmap));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(kycDetailsInfo.getIdFrontImageUri() != null){
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), kycDetailsInfo.getIdFrontImageUri());
                        jsonObject.put("imgIdFront", Utils.bitmapToBase64(bitmap));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(kycDetailsInfo.getIdBackImageUri()!= null){
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), kycDetailsInfo.getIdFrontImageUri());
                        jsonObject.put("imgIdBack", Utils.bitmapToBase64(bitmap));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }catch (JSONException je) {
                je.printStackTrace();
            }
            return jsonObject;
        }
    }
}