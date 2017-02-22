package np.com.qpay.kycformapp.kyc.formfragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bruce.pickerview.popwindow.DatePickerPopWin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import np.com.qpay.kycformapp.R;
import np.com.qpay.kycformapp.custumclasses.FontTextViewRegular;
import np.com.qpay.kycformapp.kyc.dto.KYCDetailsInfo;
import np.com.qpay.kycformapp.kyc.interfaces.OnBtnClickedListener;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by qpay on 2/16/17.
 */

public class PersonalHistoryFormFragment extends Fragment {

    private String[] spinnerValues = {"Male", "Female", "Others"};
    private GenderSpinnerAdapter genderSpinnerAdapter;
    private int genderPosition = 0;

    private float mDensity;

    private FontTextViewRegular dobTextView;
    private Spinner genderSpinner;
    private EditText fullNameTextView;
    private EditText fatherNameTextView;
    private EditText motherNameTextView;
    private EditText grandfatherNameTextView;
    private EditText occupation;

    private OnBtnClickedListener onBtnClickedListener;
    private KYCDetailsInfo kycDetailsInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.personal_history_form_layout, container, false);

        mDensity = getContext().getResources().getDisplayMetrics().density;

        // <editor-fold desc = "Date Of Birth Picker Configuration">
        dobTextView = (FontTextViewRegular) view.findViewById(R.id.dob_text_view);
        configureDateOfBirth();
        //</editor-fold>

        //<editor-fold desc="Gender Spinner Configuration">
        genderSpinner = (Spinner) view.findViewById(R.id.gender_spinner);
        genderSpinnerAdapter = new GenderSpinnerAdapter(getActivity(), R.layout.gender_spinner_layout, spinnerValues);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                genderPosition = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        genderSpinner.setAdapter(genderSpinnerAdapter);
        //</editor-fold>

        // <editor-fold desc="Edit Text Object Initialization">
        fullNameTextView = (EditText) view.findViewById(R.id.full_name_text_view);
        fatherNameTextView = (EditText) view.findViewById(R.id.father_name_text_view);
        motherNameTextView = (EditText) view.findViewById(R.id.mother_name_text_view);
        grandfatherNameTextView = (EditText) view.findViewById(R.id.grandfather_name_text_view);

        occupation = (EditText) view.findViewById(R.id.occupation_text_view);
        // </editor-fold>

        // <editor-fold desc="Next Button Configuration">
        Button nextBtn = (Button) view.findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillKycDetails();
                onBtnClickedListener.onNextBtnClicked(kycDetailsInfo, 1);
            }
        });
        // </editor-fold>

        setDataInForm(kycDetailsInfo);
        return view;
    }

    private void configureDateOfBirth() {
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
        dobTextView.setText(today);
        final int finalThisYearInt = thisYearInt;
        dobTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                String todayFormattedForPicker = "1990-01-01";
                String dateText;
                if(dobTextView.getText() != null) {
                    dateText = dobTextView.getText().toString();
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
                        dobTextView.setText(DOB);
                    }
                }).textConfirm("SET") //text of confirm button
                        .textCancel("CANCEL") //text of cancel button
                        .minYear(1900) //min year in loop
                        .maxYear(finalThisYearInt) // max year in loop
                        .showDayMonthYear(false) // shows like dd mm yyyy (default is false)
                        .dateChose(todayFormattedForPicker) // date chose when init popwindow
                        .build();
                pickerPopWin.showPopWin(getActivity());
            }
        });
    }

    public void setOnBtnClickedListener(OnBtnClickedListener onBtnClickedListener) {
        this.onBtnClickedListener = onBtnClickedListener;
    }

    // <editor-fold desc="Get Personal History Data">
    private void getPersonalHistoryData() {

    }
    // </editor-fold>

    // <editor-fold desc="Hides the soft keyboard">
    public void hideSoftKeyboard() {
        if(getActivity().getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
    // </editor-fold>

    private boolean fillKycDetails(){
        // FULL NAME
        if(fullNameTextView.getText() != null && !fullNameTextView.getText().toString().isEmpty()) {
            kycDetailsInfo.setFullName(fullNameTextView.getText().toString());
        }else {
            return false;
        }

        // FATHER NAME
        if(fatherNameTextView.getText() != null && !fatherNameTextView.getText().toString().isEmpty()) {
            kycDetailsInfo.setFatherName(fatherNameTextView.getText().toString());
        }else {
            return false;
        }

        // MOTHER NAME
        if(motherNameTextView.getText() != null && !motherNameTextView.getText().toString().isEmpty()) {
            kycDetailsInfo.setMotherName(motherNameTextView.getText().toString());
        }else {
            return false;
        }

        // GRANDFATHER NAME
        if(grandfatherNameTextView.getText() != null && !grandfatherNameTextView.getText().toString().isEmpty()) {
            kycDetailsInfo.setGrandFatherName(grandfatherNameTextView.getText().toString());
        }else {
            return false;
        }

        // Occupation
        if(occupation.getText() != null && !occupation.getText().toString().isEmpty()) {
            kycDetailsInfo.setOccupation(occupation.getText().toString());
        }else {
            return false;
        }

        kycDetailsInfo.setGender(genderPosition);

        kycDetailsInfo.setDateOfBirth(dobTextView.getText().toString());

        return true;
    }


    public void setData(KYCDetailsInfo kycDetailsInfo){
        this.kycDetailsInfo = kycDetailsInfo;
    }

    public void setDataInForm(KYCDetailsInfo kycDetailsInfo) {
        if(kycDetailsInfo != null) {
            fullNameTextView.setText(kycDetailsInfo.getFullName());
            fatherNameTextView.setText(kycDetailsInfo.getFatherName());
            motherNameTextView.setText(kycDetailsInfo.getMotherName());
            grandfatherNameTextView.setText(kycDetailsInfo.getGrandFatherName());
            occupation.setText(kycDetailsInfo.getOccupation());
            genderSpinner.setSelection(kycDetailsInfo.getGender());
            if(!kycDetailsInfo.getDateOfBirth().isEmpty()){
                dobTextView.setText(kycDetailsInfo.getDateOfBirth());
            }
        }
    }

    //<editor-fold desc="CLASS: GenderSpinnerAdapter">
    public class GenderSpinnerAdapter extends ArrayAdapter<String> {

        public GenderSpinnerAdapter(Context ctx, int txtViewResourceId, String[] objects) {
            super(ctx, txtViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            return getCustomView(position, cnvtView, prnt);
        }

        @Override
        public View getView(int pos, View cnvtView, ViewGroup prnt) {
            return getCustomView(pos, cnvtView, prnt);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View mySpinner = inflater.inflate(R.layout.gender_spinner_layout, parent, false);
            TextView main_text = (TextView) mySpinner.findViewById(R.id.text_main_seen);
            main_text.setText(spinnerValues[position]);
            return mySpinner;
        }
    }
    // </editor-fold>
}