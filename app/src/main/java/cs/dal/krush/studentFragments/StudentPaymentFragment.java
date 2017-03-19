package cs.dal.krush.studentFragments;


import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cs.dal.krush.R;
import cs.dal.krush.models.DBHelper;

/**
 * StudentPaymentFragment is used to process payments from student's credit cards and increments
 * the revenue of tutor based on the cost of the session.
 */
public class StudentPaymentFragment extends Fragment {

    private boolean isValid;
    private String creditCarNumber;
    private String cvvNumbers;
    private String expirationMonth;
    private DBHelper mydb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.student_payment, container, false);
        // TODO: 2017-03-17 Get student and tutor ID from bundle
        // int userId = Integer.parseInt(getArguments().getString("UserID"));
        // int tutorId = Integer.parseInt(getArguments().getString("TutorID"));

        //initialize database connection
        mydb = new DBHelper(getActivity().getApplicationContext());

        //fetch UI components
        final TextView pageHeader = (TextView) view.findViewById(R.id.paymentHeader);
        final TextView costLabel = (TextView) view.findViewById(R.id.costLabel);
        final TextView tutoringCost = (TextView) view.findViewById(R.id.tutoringCost);
        final TextView cardNumberLabel = (TextView) view.findViewById(R.id.cardNumberLabel);
        final TextView expDateLabel = (TextView) view.findViewById(R.id.expDateLabel);
        final TextView cvvLabel = (TextView) view.findViewById(R.id.cvvLabel);
        final TextView paymentMethodsLabel = (TextView) view.findViewById(R.id.paymentMethodsLabel);
        final EditText creditNumberInput = (EditText) view.findViewById(R.id.creditNumberInput);
        final EditText monthInput = (EditText) view.findViewById(R.id.monthInput);
        final EditText cvvNumberInput = (EditText) view.findViewById(R.id.cvvNumberInput);
        final Button submitPayment = (Button) view.findViewById(R.id.submitPayment);
        final ImageView visaLogo = (ImageView) view.findViewById(R.id.visaLogo);
        final ImageView masterCardLogo = (ImageView) view.findViewById(R.id.masterCardLogo);
        final ImageView amexLogo = (ImageView) view.findViewById(R.id.amexLogo);

        //fetch custom app font
        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/FredokaOne-Regular.ttf");

        //set logo font style
        pageHeader.setTypeface(typeFace);
        costLabel.setTypeface(typeFace);
        cardNumberLabel.setTypeface(typeFace);
        expDateLabel.setTypeface(typeFace);
        cvvLabel.setTypeface(typeFace);
        paymentMethodsLabel.setTypeface(typeFace);

        //set cost label
        // TODO: 2017-03-17 Get tutoring session cost from bundle 
        tutoringCost.setText("$" + 99.99);

        /**
         * text listener on credit card TextField to display the credit card type
         *
         * Source:
         * [9]D. J, "android on Text Change Listener", Stackoverflow.com, 2017. [Online].
         * Available: http://stackoverflow.com/questions/20824634/android-on-text-change-listener.
         * Accessed: 18- Mar- 2017
         */
        creditNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //on text change check the first character for card type
                if(s.length() >= 1) {
                    if(s.charAt(0) == '4') {
                        //visa card
                        removeFilter(visaLogo);
                        grayOut(masterCardLogo);
                        grayOut(amexLogo);
                    } else if(s.charAt(0) == '5') {
                        //mastercard card
                        removeFilter(masterCardLogo);
                        grayOut(visaLogo);
                        grayOut(amexLogo);
                    } else if(s.charAt(0) == '3') {
                        //amex card
                        removeFilter(amexLogo);
                        grayOut(visaLogo);
                        grayOut(masterCardLogo);
                    } else {
                        //invalid card
                        grayOut(amexLogo);
                        grayOut(visaLogo);
                        grayOut(masterCardLogo);
                        creditNumberInput.setError("Invalid credit card");
                    }
                } else {
                    //remove filter on card logos
                    removeFilter(amexLogo);
                    removeFilter(visaLogo);
                    removeFilter(masterCardLogo);
                }
            }
        });

        //submit payment
        submitPayment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isValid = true;
                creditCarNumber = creditNumberInput.getText().toString();
                expirationMonth = monthInput.getText().toString();
                cvvNumbers = cvvNumberInput.getText().toString();

                //validate inputs
                if(creditCarNumber.length() != 16) {
                    creditNumberInput.setError("Credit card number required!");
                    isValid = false;
                }

                if(expirationMonth.length() != 4) {
                    monthInput.setError("Expiration required!");
                    isValid = false;
                }

                if(cvvNumbers.length() != 3) {
                    cvvNumberInput.setError("CVV required!");
                    isValid = false;
                }

                //increment tutor's revenue and return to student home
                if(isValid) {
                    // TODO: 2017-03-18  Use tutorID and cost from bundle
                    mydb.tutor.incrementTutorRevenue(1, 99.99);

                    // TODO: 2017-03-18 Need intend - likely back to the student home
                }
            }
        });
        return view;
    }

    /**
     * Takes an ImageView and applies a 'grey' filter to it
     * @param imageView
     */
    private void grayOut(ImageView imageView) {
        // gray out image
        imageView.setColorFilter(Color.argb(150,200,200,200));
    }

    /**
     * Takes an ImageView and removes a filter
     * @param imageView
     */
    private void removeFilter(ImageView imageView) {
        // remove gray out on image
        imageView.setColorFilter(null);
    }
}