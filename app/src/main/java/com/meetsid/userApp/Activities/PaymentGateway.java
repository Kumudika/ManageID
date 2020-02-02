package com.meetsid.userApp.Activities;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.meetsid.userApp.R;
import com.meetsid.userApp.Utils.Common;
import com.meetsid.userApp.Utils.ServerUtils.ConnectServer;
import com.meetsid.userApp.Utils.Verification;

import net.authorize.acceptsdk.AcceptSDKApiClient;
import net.authorize.acceptsdk.datamodel.common.Message;
import net.authorize.acceptsdk.datamodel.merchant.ClientKeyBasedMerchantAuthentication;
import net.authorize.acceptsdk.datamodel.transaction.CardData;
import net.authorize.acceptsdk.datamodel.transaction.EncryptTransactionObject;
import net.authorize.acceptsdk.datamodel.transaction.TransactionObject;
import net.authorize.acceptsdk.datamodel.transaction.TransactionType;
import net.authorize.acceptsdk.datamodel.transaction.callbacks.EncryptTransactionCallback;
import net.authorize.acceptsdk.datamodel.transaction.response.EncryptTransactionResponse;
import net.authorize.acceptsdk.datamodel.transaction.response.ErrorTransactionResponse;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentGateway extends AppCompatActivity {
    //    @BindView(R.id.credit_card_form)
//    CreditCardForm credit_card_form;
    @BindView(R.id.btnConfirm)
    Button btnConfirm;
    @BindView(R.id.backBtn)
    ImageButton backBtn;
    @BindView(R.id.cardNo)
    EditText cardNo;
    @BindView(R.id.month)
    EditText month;
    @BindView(R.id.year)
    EditText year;
    @BindView(R.id.cvv)
    EditText cvv;
    @BindView(R.id.amount)
    TextView amount;

    int keyDel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_payment_gateway);
        ButterKnife.bind(this);
        btnConfirm.setEnabled(true);
        String val = "$" + Common.amount;
        amount.setText(val);
        btnConfirm.setText(val);
        init();
//        credit_card_form.setOnCardValidCallback(new CardValidCallback() {
//            @Override
//            public void cardValid(com.devmarvel.creditcardentry.library.CreditCard creditCard) {
//                btnConfirm.setEnabled(true);
//                cardNo = creditCard.getCardNumber();
//                cvv = creditCard.getSecurityCode();
//                month = creditCard.getExpMonth().toString();
//                year = creditCard.getExpYear().toString();
//            }
//        });
//        credit_card_form.focusCreditCard();
    }

    private void init() {
        cardNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String msg = Verification.isValidCard(cardNo.getText().toString());
                    if (msg != null) {
                        cardNo.setError(msg);
                    } else {
                        cardNo.setError(null);
                    }
                }
            }
        });
        month.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    int mm = Integer.parseInt(month.getText().toString());
                    if (mm > 12 || mm < 1) {
                        month.setError("Invalid Month");
                    } else {
                        month.setError(null);
                    }
                }
            }
        });
    }

    @OnClick(R.id.backBtn)
    public void goBack() {
        this.finish();
        super.onBackPressed();
    }

    @OnClick(R.id.btnConfirm)
    public void onConfirm() {
        Context context = this;
        AcceptSDKApiClient apiClient = new AcceptSDKApiClient.Builder(this,
                AcceptSDKApiClient.Environment.SANDBOX)
                .connectionTimeout(5000) // optional connection time out in milliseconds
                .build();
        EncryptTransactionObject transactionObject = TransactionObject.
                createTransactionObject(TransactionType.SDK_TRANSACTION_ENCRYPTION)// type of transaction object
                .cardData(prepareCardDataField()) // card data to be encrypted
                .merchantAuthentication(ClientKeyBasedMerchantAuthentication.
                        createMerchantAuthentication("8a2jC5Ej9By", "4sa82haPCPBY9np78MsBBqd9d3F94Ey5BGDVfuydb4hC74mpr9x68VW58W2AFEbn")) //Merchant authentication
                .build();
        apiClient.getTokenWithRequest(transactionObject, new EncryptTransactionCallback() {
                    @Override
                    public void onEncryptionFinished(EncryptTransactionResponse response) {
                        if (response.getResponseMessages().getResultCode().equalsIgnoreCase("Ok")) {
                            startPayment(response.getDataValue());
                        }
                        Log.i("Card", response.toString());
                    }

                    @Override
                    public void onErrorReceived(ErrorTransactionResponse errorResponse) {
                        Log.i("Card", errorResponse.toString());
                        Message error = errorResponse.getFirstErrorMessage();
                        if (error.getMessageCode().equals("E_WC_05"))
                            cardNo.setError("Invalid Card Number");
//                        Toast.makeText(context,
//                                error.getMessageText(),
//                                Toast.LENGTH_LONG)
//                                .show();
                    }
                }
        );
//        if(credit_card_form.isCreditCardValid()) {
//
//        }else {
//            Toast.makeText(PaymentGateway.this, "Enter Valid Card Details",Toast.LENGTH_SHORT).show();
//        }
    }

    public void startPayment(String token) {
        HashMap<String, String> param = new HashMap<>();
        param.put("accessToken", token);
        ConnectServer.connect().addPayment(param, this);
//        final Context context = this;
//        RequestAPI.jsonRequestHandle("register/update_payment/", context, param, new ResponseListner() {
//            @Override
//            public void onError(VolleyError msg) {
//                Log.e("Payment error: ", msg.toString());
//            }
//
//            @Override
//            public void onResponse(Object response) {
//                try {
//                    if(((JSONObject) response).get("code").toString().equalsIgnoreCase("0")) {
//                        TastyToast.makeText(getApplicationContext(), "Payment completed !", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
//                        Intent intent = new Intent(context, MainActivity.class);
//                        startActivity(intent);
//                    }
//                }catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//                Log.i("Sign up response: ", response.toString());
//            }
//        });
    }

    public CardData prepareCardDataField() {
        CardData cardData = new CardData.Builder(cardNo.getText().toString(),
                month.getText().toString(), // MM
                year.getText().toString()) // YYYY
                .cvvCode(cvv.getText().toString()) // Optional
//                .zipCode(ZIP_CODE)// Optional
//                .cardHolderName(CARD_HOLDER_NAME)// Optional
                .build();
        return cardData;
    }
}

class FourDigitCardFormatWatcher implements TextWatcher {
    private static final char SPACE_CHAR = ' ';
    private static final String SPACE_STRING = String.valueOf(SPACE_CHAR);
    private static final int GROUPSIZE = 4;
    private final String regexp = "^(\\d{4}\\s)*\\d{0,4}(?<!\\s)$";
    private boolean isUpdating = false;

    private final EditText editText;

    FourDigitCardFormatWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String originalString = s.toString();

        // Check if we are already updating, to avoid infinite loop.
        // Also check if the string is already in a valid format.
        if (isUpdating || originalString.matches(regexp)) {
            return;
        }

        // Set flag to indicate that we are updating the Editable.
        isUpdating = true;

        // Loop through the string again and add whitespaces in the correct positions
        for (int i = 0; ((i + 1) * GROUPSIZE + i) < s.length(); i++) {
            s.insert((i + 1) * GROUPSIZE + i, SPACE_STRING);
        }

        // Finally check that the cursor is not placed before a whitespace.
        // This will happen if, for example, the user deleted the digit '5' in
        // the string: "1234 567".
        // If it is, move it back one step; otherwise it will be impossible to delete
        // further numbers.
        int cursorPos = editText.getSelectionStart();
        if (cursorPos > 0 && s.charAt(cursorPos - 1) == SPACE_CHAR) {
            editText.setSelection(cursorPos - 1);
        }

        isUpdating = false;
    }

}
