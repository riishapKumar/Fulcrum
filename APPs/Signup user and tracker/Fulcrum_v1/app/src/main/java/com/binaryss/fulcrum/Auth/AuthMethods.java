package com.binaryss.fulcrum.Auth;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.binaryss.fulcrum.MainActivity;
import com.binaryss.fulcrum.Utils.GlobalVariables;
import com.binaryss.fulcrum.Utils.LoadingDialog;
import com.binaryss.fulcrum.Utils.MyCustomMethods;
import com.binaryss.fulcrum.Utils.SharedHelper;
import java.util.ArrayList;
import java.util.Objects;


public class AuthMethods {
    private static final String TAG = "AuthMethods";

    public static void registerUser(Activity activity, String name, String email, String password) {
        SharedHelper sharedHelper = new SharedHelper(activity);
        LoadingDialog loadingDialog = new LoadingDialog(activity);
        ArrayList<AuthUserAttribute> attributes = new ArrayList<>();
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.email(), email));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.name(), name));
        loadingDialog.show();
        Amplify.Auth.signUp(
                email,
                password,
                AuthSignUpOptions.builder().userAttributes(attributes).build(),
                result -> {
                    try {
                        loadingDialog.dismiss();
                        Log.i(TAG, "Result: " + result.isSignUpComplete() + "\n" + result + result.getNextStep().getSignUpStep());
                        sharedHelper.putKey(GlobalVariables.SIGNUP_EMAIL, email);
                        sharedHelper.putKey(GlobalVariables.SIGNUP_PASSWORD, password);
                        Intent intent = new Intent(activity, ConfirmSignUpActivity.class);
                        intent.putExtra("name", email);
                        intent.putExtra("sendEmail", "false");
                        activity.startActivity(intent);
                    }catch (Exception ex){
                        Log.i(TAG, "Sign up failed Exception: " + ex.getMessage());

                    }

                },
                error -> {
                    loadingDialog.dismiss();
                    Log.d(TAG, "Sign up failed " + error.getMessage());
                    if (Objects.equals(error.getMessage(), "Username already exists in the system.")) {
                        MyCustomMethods.showSimpleToast(activity, "Email Already exist.Please try another!");
                    } else {
                        MyCustomMethods.showSimpleToast(activity, error.getMessage());
                    }
                }
        );
    }

    public static void confirmSignUp(Activity activity, String email, String code) {
        LoadingDialog loadingDialog = new LoadingDialog(activity);
        SharedHelper sharedHelper = new SharedHelper(activity);
        loadingDialog.show();
        Amplify.Auth.confirmSignUp(email,
                code,
                result -> {
                    Log.i(TAG, result.toString());
                    loadingDialog.dismiss();
                    MyCustomMethods.showSimpleToast(activity, "Account Created Successfully");
                    sharedHelper.putKey(GlobalVariables.ACTIVITY_NAME, "SIGNUP_CONFIRM");
                    Intent intent = new Intent(activity, Login.class);
                    activity.startActivity(intent);
                },
                error -> {
                    loadingDialog.dismiss();
                    Log.e(TAG, error.toString());
                    MyCustomMethods.showSimpleToast(activity, error.getMessage());
                }
        );
    }

    public static void signIn(Activity activity, String email, String password) {
        SharedHelper sharedHelper = new SharedHelper(activity);

        LoadingDialog loadingDialog = new LoadingDialog(activity);
        UserSessionManager userSessionManager = new UserSessionManager(activity);
        loadingDialog.show();
        Amplify.Auth.signIn(email,
                password,
                result -> {
                    Log.i(TAG, "signIn result " +  result.toString());
                    loadingDialog.dismiss();
                    userSessionManager.setLoggedIn(true);
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                    activity.finishAffinity();
                },
                error -> {
                    loadingDialog.dismiss();
                    Log.e(TAG, "signIn error " + error.toString());
                    if(error.toString().contains("UserNotFoundException")){
                        MyCustomMethods.showSimpleToast(activity, "User does not exist");

                    }else if(error.toString().contains("UserNotConfirmedException")){
                        MyCustomMethods.showSimpleToast(activity, "User not confirmed");
                        sharedHelper.putKey(GlobalVariables.SIGNUP_EMAIL, email);
                        sharedHelper.putKey(GlobalVariables.SIGNUP_PASSWORD, password);
                        Intent intent = new Intent(activity, ConfirmSignUpActivity.class);
                        intent.putExtra("name", email);
                        intent.putExtra("sendEmail", "true");
                        activity.startActivity(intent);
                    }else {
                        MyCustomMethods.showSimpleToast(activity, "Please Check Your Email or Password");
                    }
                }
        );
    }

    public static void forgotPassword(Activity activity, String email) {
        LoadingDialog loadingDialog = new LoadingDialog(activity);
        loadingDialog.show();
        Amplify.Auth.resetPassword(email,
                result -> {
                    Log.i(TAG, "forgotPassword result: " + result.toString());
                    loadingDialog.dismiss();
                    MyCustomMethods.showSimpleToast(activity, "Code Send To Your Email Address.");
                },
                error -> {
                    loadingDialog.dismiss();
                    Log.e(TAG, "forgotPassword error: " + error.toString());
                    Log.e(TAG, "forgotPassword 1 error: " + error.getLocalizedMessage());
                    Log.e(TAG, "forgotPassword 2 error: " + error.getMessage());

                    MyCustomMethods.showSimpleToast(activity,  error.getLocalizedMessage());
                }
        );
    }

    public static void confirmResetPassword(Activity activity, String password, String code) {
        LoadingDialog loadingDialog = new LoadingDialog(activity);
        loadingDialog.show();
        Amplify.Auth.confirmResetPassword(password,
                code,
                () -> {
                    Log.i(TAG, "confirmResetPassword Success: ");
                    loadingDialog.dismiss();
                    MyCustomMethods.showSimpleToast(activity, "Successfully Changed Password");
                    Intent intent = new Intent(activity, Login.class);
                    activity.startActivity(intent);
                },
                error -> {
                    loadingDialog.dismiss();
                    Log.e(TAG, "confirmResetPassword Error: " + error.toString());
                    MyCustomMethods.showSimpleToast(activity, error.toString());
                }
        );
    }

    public static void updatePassword(Activity activity, String oldPassword, String newPassword) {
        LoadingDialog loadingDialog = new LoadingDialog(activity);
        loadingDialog.show();
        Amplify.Auth.updatePassword(oldPassword, newPassword,
                () -> {
                    loadingDialog.dismiss();
                    MyCustomMethods.showSimpleToast(activity, "Successfully Changed Password");
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                },
                error -> {
                    loadingDialog.dismiss();
                    Log.e(TAG, "updatePassword Error: " + error.toString());
                    MyCustomMethods.showSimpleToast(activity, error.getMessage());
                }
        );
    }

    public static void resendCode(Activity activity) {
        SharedHelper sharedHelper = new SharedHelper(activity);

        if(!sharedHelper.getKey(GlobalVariables.SIGNUP_EMAIL).equals("")){
            LoadingDialog loadingDialog = new LoadingDialog(activity);
            loadingDialog.show();
            Amplify.Auth.resendSignUpCode(sharedHelper.getKey(GlobalVariables.SIGNUP_EMAIL),
                    result -> {
                        loadingDialog.dismiss();
                        MyCustomMethods.showSimpleToast(activity, "Code Send To Your Email Address.");
                    },
                    error -> {
                        loadingDialog.dismiss();
                        Log.e(TAG, "resendCode Error: " + error.toString());
                        MyCustomMethods.showSimpleToast(activity, error.toString());
                    }
            );
        }else{
            MyCustomMethods.showSimpleToast(activity, "Something went wrong, try again later");
        }


    }

    public static void signOut(Activity activity) {
        LoadingDialog loadingDialog = new LoadingDialog(activity);
        UserSessionManager userSessionManager = new UserSessionManager(activity);
        SharedHelper sharedHelper = new SharedHelper(activity);
        loadingDialog.show();
        Amplify.Auth.signOut(
                () -> {
                    loadingDialog.dismiss();
                    sharedHelper.clearStoreInfoData();
                    MyCustomMethods.showSimpleToast(activity, "Successfully SignOut");
                    userSessionManager.setLoggedIn(false);
                    userSessionManager.clearSessionData();
                    Intent intent = new Intent(activity, Login.class);
                    activity.startActivity(intent);
                },
                error -> {
                    loadingDialog.dismiss();
                    Log.e(TAG, error.toString());
                    MyCustomMethods.showSimpleToast(activity, error.toString());
                }
        );
    }

    public static void updateUserAttribute(Activity activity, String id, String name, String email, String accountType) {
        LoadingDialog loadingDialog = new LoadingDialog(activity);
        ArrayList<AuthUserAttribute> attributes = new ArrayList<>();
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.email(), email));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.profile(), accountType));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.name(), name));
        loadingDialog.show();
        Amplify.Auth.updateUserAttributes(
                attributes,
                result -> {
                    MyCustomMethods.showSimpleToast(activity, "Successfully Updated");
                },
                error -> {
                    loadingDialog.dismiss();
                    Log.e(TAG, "Sign up failed", error);
                    MyCustomMethods.showSimpleToast(activity, error.getMessage());
                }
        );
    }
}
