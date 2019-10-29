package com.stickynotes;

import android.text.Editable;
import android.text.TextUtils;
import android.util.Patterns;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class FormValidationHelper {

    public static boolean isEmpty(TextInputLayout editText) {
        try{
            Editable text = editText.getEditText().getText();
            return TextUtils.isEmpty(text);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static boolean isValidPhoneNumber(String input) {

        if(TextUtils.isDigitsOnly(input)) {
            return Pattern.compile("^[7-9][0-9]{9}$").matcher(input).matches();
        }

        return false;
    }

    public static boolean isValidEmail(String input) {
        return Patterns.EMAIL_ADDRESS.matcher(input).matches();
    }

    public static void setError(TextInputLayout editText, String message) {
        editText.setError(message);
    }

    public static void clearError(TextInputLayout editText) {
        editText.setError("");
    }

    public static String getTextFromEditText(TextInputLayout editText) {
        try {
            return editText.getEditText().getText().toString();
        }catch (NullPointerException e) {
            return "";
        }

    }
}
