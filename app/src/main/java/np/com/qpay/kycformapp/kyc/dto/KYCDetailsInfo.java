package np.com.qpay.kycformapp.kyc.dto;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by qpay on 2/17/17.
 */

public class KYCDetailsInfo {
    //START: Personal Details
    private String fullName = "";
    private String fatherName = "";
    private String motherName = "";
    private String grandFatherName = "";
    private String dateOfBirth = "";
    private int gender = 0;
    private String occupation = "";

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getGrandFatherName() {
        return grandFatherName;
    }

    public void setGrandFatherName(String grandFatherName) {
        this.grandFatherName = grandFatherName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
    //END: Personal Details

    //START: Contacts
    private int zoneId = -1;
    private int districtId = -1;
    private int vdcId = -1;
    private String toleName = "";

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public int getVdcId() {
        return vdcId;
    }

    public void setVdcId(int vdcId) {
        this.vdcId = vdcId;
    }

    public String getToleName() {
        return toleName;
    }

    public void setToleName(String toleName) {
        this.toleName = toleName;
    }
    //END: Contacts

    //START: Documents
    private String identificationNumber = "";
    private String issuedDate = "";
    private int idIssuedFrom = -1;

    private Uri userImageUri;
    private Uri idFrontImageUri;
    private Uri idBackImageUri;

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public int getIdIssuedFrom() {
        return idIssuedFrom;
    }

    public void setIdIssuedFrom(int idIssuedFrom) {
        this.idIssuedFrom = idIssuedFrom;
    }

    public Uri getUserImageUri() {
        return userImageUri;
    }

    public void setUserImageUri(Uri userImageUri) {
        this.userImageUri = userImageUri;
    }

    public Uri getIdFrontImageUri() {
        return idFrontImageUri;
    }

    public void setIdFrontImageUri(Uri idFrontImageUri) {
        this.idFrontImageUri = idFrontImageUri;
    }

    public Uri getIdBackImageUri() {
        return idBackImageUri;
    }

    public void setIdBackImageUri(Uri idBackImageUri) {
        this.idBackImageUri = idBackImageUri;
    }
    // END: Documents
}
