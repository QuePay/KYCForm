package np.com.qpay.kycformapp.kyc.formfragments;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import np.com.qpay.kycformapp.CommonConstants;
import np.com.qpay.kycformapp.R;
import np.com.qpay.kycformapp.custumclasses.QPayProgressDialog;
import np.com.qpay.kycformapp.http.HttpUrlConnectionPost;
import np.com.qpay.kycformapp.kyc.amountpicker.ZonePickerPopWin;
import np.com.qpay.kycformapp.kyc.dto.AddressInfo;
import np.com.qpay.kycformapp.kyc.dto.KYCDetailsInfo;
import np.com.qpay.kycformapp.kyc.interfaces.OnBtnClickedListener;
import np.com.qpay.kycformapp.merchant_databases.QpayMerchantDatabase;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by qpay on 2/16/17.
 */

public class ContactFormFragment extends Fragment {

    private QpayMerchantDatabase qpayMerchantDatabase;
    private List<AddressInfo> zoneAddress;
    private List<AddressInfo> districtList;
    private List<AddressInfo> VDCList;

    private String term_id;
    private String app_id;
    private float mDensity;

    private AddressInfo selectedZone;
    private AddressInfo selectedDistrict;
    private AddressInfo selectedVDC;

    private AddressInfo previousSelectedZone;
    private AddressInfo previousSelectedDistrict;

    private TextView zoneTextView;
    private TextView districtTextView;
    private TextView vdcTextView;
    private TextView toleNameTextView;

    private KYCDetailsInfo kycDetailsInfo;
    private OnBtnClickedListener onBtnClickedListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_form_layout, container, false);
        qpayMerchantDatabase = new QpayMerchantDatabase(getActivity());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mDensity = getContext().getResources().getDisplayMetrics().density;

        // TODO: Change TermID and AppID value
        term_id = "ZRGe7xlTHyVqh5OWalXUxQ==";
        app_id = "2A2275920236713";

        zoneTextView = (TextView) view.findViewById(R.id.zone_text_view);
        zoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                setZoneData();
            }
        });

        districtTextView = (TextView) view.findViewById(R.id.district_text_view);
        districtTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                if(selectedZone != null) {
                    setDistrictData();
                }else {
                    Toast.makeText(getActivity(), "Select Zone first.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        vdcTextView = (TextView) view.findViewById(R.id.vdc_text_view);
        vdcTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                if(selectedDistrict != null) {
                    setVDCData();
                }else {
                    Toast.makeText(getActivity(), "Select District first.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        toleNameTextView = (TextView) view.findViewById(R.id.tole_name_text_view);

        Button nextBtn = (Button) view.findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillKycDetails();
                onBtnClickedListener.onNextBtnClicked(kycDetailsInfo, 2);
            }
        });

        Button backBtn = (Button) view.findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBtnClickedListener.onBackBtnClicked(kycDetailsInfo, 2);
            }
        });

        if (qpayMerchantDatabase.isTableExists(QpayMerchantDatabase.ZONE_TABLE_NAME, true)){
            zoneAddress = qpayMerchantDatabase.selectAllZone();
        }else {
            new GetAllZoneTask().execute();
        }

        setDataForForm(kycDetailsInfo);


        return view;
    }

    public void hideSoftKeyboard() {
        if(getActivity().getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void getDistrictByZoneId(int zoneId){
        if (qpayMerchantDatabase.isTableExists(QpayMerchantDatabase.DISTRICT_TABLE_NAME, true)) {
            if(selectedZone != null && previousSelectedZone != null && zoneId == previousSelectedZone.getId()) {
                districtList = qpayMerchantDatabase.selectAllDistrict();
                return;
            }else {
                qpayMerchantDatabase.dropTable(QpayMerchantDatabase.DISTRICT_TABLE_NAME);
                qpayMerchantDatabase.dropTable(QpayMerchantDatabase.VDC_TABLE_NAME);
                districtTextView.setText("");
                selectedDistrict = null;
                vdcTextView.setText("");
                selectedVDC = null;
                VDCList = null;
                districtList = null;
            }
        }
        new GetDistrictTask(zoneId).execute();
    }

    private void getVDCByDistrictId(int districtId){
        if (qpayMerchantDatabase.isTableExists(QpayMerchantDatabase.VDC_TABLE_NAME, true)) {
            if(selectedDistrict != null && previousSelectedDistrict != null && districtId == previousSelectedDistrict.getId()) {
                VDCList = qpayMerchantDatabase.selectAllVDC();
                return;
            }else {
                qpayMerchantDatabase.dropTable(QpayMerchantDatabase.VDC_TABLE_NAME);
                vdcTextView.setText("");
                selectedVDC = null;
                VDCList = null;
            }
        }
        new GetVDCTask(districtId).execute();
    }

    public void setOnBtnClickedListener(OnBtnClickedListener onBtnClickedListener) {
        this.onBtnClickedListener = onBtnClickedListener;
    }

    public void setData(KYCDetailsInfo kycDetailsInfo){
        this.kycDetailsInfo = kycDetailsInfo;
    }

    public void setDataForForm(KYCDetailsInfo kycDetailsInfo) {
        if(kycDetailsInfo != null) {

            if(!kycDetailsInfo.getToleName().isEmpty()) {
                toleNameTextView.setText(kycDetailsInfo.getToleName());
            }

            if(kycDetailsInfo.getZoneId() != -1){
                selectedZone = qpayMerchantDatabase.selectZoneById(kycDetailsInfo.getZoneId());
                zoneTextView.setText(selectedZone.getName());
            }

            if(kycDetailsInfo.getDistrictId() != -1){
                selectedDistrict = qpayMerchantDatabase.selectDistrictById(kycDetailsInfo.getDistrictId());
                districtTextView.setText(selectedDistrict.getName());
                if (qpayMerchantDatabase.isTableExists(QpayMerchantDatabase.DISTRICT_TABLE_NAME, true)){
                    districtList = qpayMerchantDatabase.selectAllDistrict();
                }
            }

            if(kycDetailsInfo.getVdcId() != -1){
                selectedVDC = qpayMerchantDatabase.selectVDCById(kycDetailsInfo.getVdcId());
                vdcTextView.setText(selectedVDC.getName());
                if (qpayMerchantDatabase.isTableExists(QpayMerchantDatabase.VDC_TABLE_NAME, true)){
                    VDCList = qpayMerchantDatabase.selectAllVDC();
                }
            }
        }
    }

    public void fillKycDetails(){
        if(toleNameTextView.getText() != null && !toleNameTextView.getText().toString().isEmpty()) {
            kycDetailsInfo.setToleName(toleNameTextView.getText().toString());
        }
    }

    // <editor-fold desc="Set Zone Data in Picker">
    private void setZoneData(){
        if(zoneAddress != null){
            ZonePickerPopWin.Builder pickerPopWinBuilder = new ZonePickerPopWin.Builder(getActivity(), new ZonePickerPopWin.OnZonePickedListener() {
                @Override
                public void onZonePickCompleted(int month, String dateDesc) {
                    if(previousSelectedZone == null){
                        previousSelectedZone = selectedZone;
                    }
                    selectedZone = zoneAddress.get(month);
                    zoneTextView.setText(selectedZone.getName());
                    kycDetailsInfo.setZoneId(selectedZone.getId());
                    getDistrictByZoneId(selectedZone.getId());
                }
            }).textConfirm("SET") //text of confirm button
                    .textCancel("CANCEL") //text of cancel button
                    .btnTextSize((int) (10 * mDensity)) // button text size
                    .viewTextSize((int) (15 * mDensity)) // pick view text size
                    .colorCancel(Color.parseColor("#999999")) //color of cancel button
                    .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                    .setValues(zoneAddress);
            if(selectedZone != null){
                pickerPopWinBuilder.initialValue(selectedZone);
            }
            ZonePickerPopWin pickerPopWin = pickerPopWinBuilder.build();
            pickerPopWin.showPopWin(getActivity());
        }
    }
    // </editor-fold>

    // <editor-fold desc="Set District Data in Picker">
    private void setDistrictData() {
        if(districtList != null){
            ZonePickerPopWin.Builder pickerPopWinBuilder = new ZonePickerPopWin.Builder(getActivity(), new ZonePickerPopWin.OnZonePickedListener() {
                @Override
                public void onZonePickCompleted(int month, String dateDesc) {
                    if(previousSelectedDistrict != null){
                        previousSelectedDistrict = selectedDistrict;
                    }
                    selectedDistrict = districtList.get(month);
                    districtTextView.setText(selectedDistrict.getName());
                    kycDetailsInfo.setDistrictId(selectedDistrict.getId());
                    getVDCByDistrictId(selectedDistrict.getId());
                }
            }).textConfirm("SET") //text of confirm button
                    .textCancel("CANCEL") //text of cancel button
                    .btnTextSize((int) (10 * mDensity)) // button text size
                    .viewTextSize((int) (15 * mDensity)) // pick view text size
                    .colorCancel(Color.parseColor("#999999")) //color of cancel button
                    .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                    .setValues(districtList);

            if(selectedDistrict != null) {
                pickerPopWinBuilder.initialValue(selectedDistrict);
            }
            ZonePickerPopWin pickerPopWin = pickerPopWinBuilder.build();
            pickerPopWin.showPopWin(getActivity());
        }
    }
    // </editor-fold>

    // <editor-fold desc="Set VDC/Municipality Data in Picker">
    private void setVDCData(){
        if(VDCList != null){
            ZonePickerPopWin.Builder pickerPopWinBuilder = new ZonePickerPopWin.Builder(getActivity(), new ZonePickerPopWin.OnZonePickedListener() {
                @Override
                public void onZonePickCompleted(int month, String dateDesc) {
                    selectedVDC = VDCList.get(month);
                    vdcTextView.setText(selectedVDC.getName());
                    kycDetailsInfo.setVdcId(selectedVDC.getId());
                }
            }).textConfirm("SET") //text of confirm button
                    .textCancel("CANCEL") //text of cancel button
                    .btnTextSize((int) (10 * mDensity)) // button text size
                    .viewTextSize((int) (15 * mDensity)) // pick view text size
                    .colorCancel(Color.parseColor("#999999")) //color of cancel button
                    .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                    .setValues(VDCList);
            if(selectedVDC != null){
                pickerPopWinBuilder.initialValue(selectedVDC);
            }
            ZonePickerPopWin pickerPopWin = pickerPopWinBuilder.build();
            pickerPopWin.showPopWin(getActivity());
        }
    }
    // </editor-fold>

    // <editor-fold desc="CLASS: GetAllZoneTask">
    class GetAllZoneTask extends AsyncTask<String, String, String> {

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
                jsonObject.put("custId", term_id);
                jsonObject.put("appId", app_id);
                response = httpUrlConnectionPost.sendHTTPData(CommonConstants.GET_ZONE, jsonObject);
                return  response;
            }catch(JSONException je){

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            if(s != null) {
                Log.d("sangharsha", "onPostExecute: " + s);
                try{
                    JSONObject jsonObject = new JSONObject(s).getJSONObject("GetZoneResult");
                    if(jsonObject.getBoolean("success")){
                        if (!qpayMerchantDatabase.isTableExists(QpayMerchantDatabase.ZONE_TABLE_NAME, true)){
                            qpayMerchantDatabase.createZoneTable();
                        }else {
                            qpayMerchantDatabase.dropTable(QpayMerchantDatabase.ZONE_TABLE_NAME);
                            qpayMerchantDatabase.createZoneTable();
                        }
                        JSONArray jDataArr = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jDataArr.length(); i++){
                            JSONObject jData = jDataArr.getJSONObject(i);
                            AddressInfo addressInfo = new AddressInfo();
                            addressInfo.setId(jData.getInt("Id"));
                            addressInfo.setName(jData.getString("Zone"));
                            qpayMerchantDatabase.populateZoneTable(addressInfo);
                        }

                        zoneAddress = qpayMerchantDatabase.selectAllZone();
                    }
                }catch (JSONException je){

                }
            }else {

            }
        }
    }
    // </editor-fold>

    // <editor-fold desc="CLASS: GetDistrictTask">
    class GetDistrictTask extends AsyncTask<String, String, String> {

        private QPayProgressDialog dialog;
        private int zoneId;

        public GetDistrictTask(int zoneId) {
            this.zoneId = zoneId;
        }

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
                jsonObject.put("custId", term_id);
                jsonObject.put("appId", app_id);
                jsonObject.put("zoneId", zoneId);
                response = httpUrlConnectionPost.sendHTTPData(CommonConstants.GET_DISTRICT, jsonObject);
                return  response;
            }catch(JSONException je){

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            if(s != null) {
                try{
                    JSONObject jsonObject = new JSONObject(s).getJSONObject("GetDistrictResult");
                    if(jsonObject.getBoolean("success")){
                        districtList = new ArrayList<>();
                        if (!qpayMerchantDatabase.isTableExists(QpayMerchantDatabase.DISTRICT_TABLE_NAME, true)){
                            qpayMerchantDatabase.createDistrictTable();
                        }else {
                            qpayMerchantDatabase.dropTable(QpayMerchantDatabase.DISTRICT_TABLE_NAME);
                            qpayMerchantDatabase.createDistrictTable();
                        }
                        JSONArray jDataArr = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jDataArr.length(); i++){
                            JSONObject jData = jDataArr.getJSONObject(i);
                            AddressInfo addressInfo = new AddressInfo();
                            addressInfo.setId(jData.getInt("Id"));
                            addressInfo.setName(jData.getString("District"));
                            districtList.add(addressInfo);
                            qpayMerchantDatabase.populateDistrictTable(addressInfo);
                        }
                        Log.d("sangharsha", "onPostExecute: " + s);
                    }
                }catch (JSONException je){

                }
            }else {

            }
        }
    }
    // </editor-fold>

    // <editor-fold desc="CLASS: GetVDCTask">
    class GetVDCTask extends AsyncTask<String, String, String> {

        private QPayProgressDialog dialog;
        private int districtId;

        public GetVDCTask(int districtId) {
            this.districtId = districtId;
        }

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
                jsonObject.put("custId", term_id);
                jsonObject.put("appId", app_id);
                jsonObject.put("districtId", districtId);
                response = httpUrlConnectionPost.sendHTTPData(CommonConstants.GET_VDC_MUNICIPALITY, jsonObject);
                return  response;
            }catch(JSONException je){

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            if(s != null) {
                try{
                    JSONObject jsonObject = new JSONObject(s).getJSONObject("GetVdcMunicipalityResult");
                    if(jsonObject.getBoolean("success")){
                        if (!qpayMerchantDatabase.isTableExists(QpayMerchantDatabase.VDC_TABLE_NAME, true)){
                            qpayMerchantDatabase.createVDCTable();
                        }else {
                            qpayMerchantDatabase.dropTable(QpayMerchantDatabase.VDC_TABLE_NAME);
                            qpayMerchantDatabase.createVDCTable();
                        }
                        VDCList = new ArrayList<>();
                        JSONArray jDataArr = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jDataArr.length(); i++){
                            JSONObject jData = jDataArr.getJSONObject(i);
                            AddressInfo addressInfo = new AddressInfo();
                            addressInfo.setId(jData.getInt("Id"));
                            addressInfo.setName(jData.getString("VdcMunicipality").replace("Municipality",""));
                            VDCList.add(addressInfo);
                            qpayMerchantDatabase.populateVDCTable(addressInfo);
                        }
                        Log.d("sangharsha", "onPostExecute: " + s);
                    }
                }catch (JSONException je){

                }
            }else {

            }
        }
    }
    // </editor-fold>
}
