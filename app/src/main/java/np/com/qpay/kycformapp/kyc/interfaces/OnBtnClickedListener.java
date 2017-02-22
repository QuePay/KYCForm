package np.com.qpay.kycformapp.kyc.interfaces;

import np.com.qpay.kycformapp.kyc.dto.KYCDetailsInfo;

/**
 * Created by qpay on 2/17/17.
 */

public interface OnBtnClickedListener {
    void onNextBtnClicked(KYCDetailsInfo kycDetailsInfo, int currentIndex);
    void onBackBtnClicked(KYCDetailsInfo kycDetailsInfo, int currentIndex);
    void onSubmitBtnClicked(KYCDetailsInfo kycDetailsInfo);
}
