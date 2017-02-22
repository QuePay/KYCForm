package np.com.qpay.kycformapp;

/**
 * Created by qpay on 2/17/17.
 */

public class CommonConstants {


    public static final String SAGAR_BASE_URL = "https://testportal.qpaysolutions.net/api/";
    public static final String BASE_API_MERCHANT = "https://node.qpaysolutions.net/QPay.svc/";

    public static final String GET_ZONE = CommonConstants.BASE_API_MERCHANT + "getzone";
    public static final String GET_DISTRICT = CommonConstants.BASE_API_MERCHANT + "GetDistrict";
    public static final String GET_VDC_MUNICIPALITY = CommonConstants.BASE_API_MERCHANT + "GetVdcMunicipality";
    public static final String GET_ALL_DISTRICT = CommonConstants.BASE_API_MERCHANT + "GetAllDistrict";
    public static final String POST_KYC_INFO = CommonConstants.BASE_API_MERCHANT + "PostKycInfo";

}
