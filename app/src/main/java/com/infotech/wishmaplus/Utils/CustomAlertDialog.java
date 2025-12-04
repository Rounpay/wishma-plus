package com.infotech.wishmaplus.Utils;

import static cn.pedant.SweetAlert.SweetAlertDialog.ERROR_TYPE;
import static cn.pedant.SweetAlert.SweetAlertDialog.SUCCESS_TYPE;
import static cn.pedant.SweetAlert.SweetAlertDialog.WARNING_TYPE;

import android.app.Activity;
import android.content.Intent;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.infotech.wishmaplus.Activity.LoginActivity;
import com.infotech.wishmaplus.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CustomAlertDialog {

    boolean isScreenOpen;
    AlertDialog alertDialogLogout;
    private Activity context;
    private SweetAlertDialog alertDialog;

    public CustomAlertDialog(Activity context, boolean isScreenOpen) {
        try {
            this.context = context;
            this.isScreenOpen = isScreenOpen;
            alertDialog = new SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            /*alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                    TextView text = alertDialog.findViewById(cn.pedant.SweetAlert.R.id.content_text);
                    text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    text.setSingleLine(false);


                }
            });*/
       /* TextView text = (TextView) alertDialog.findViewById(R.id.content_text);
        text.setGravity(Gravity.CENTER);
        //text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        text.setSingleLine(false);
        text.setMaxLines(10);
        text.setLines(6);*/
        } catch (IllegalStateException ise) {

        } catch (Exception e) {

        }

    }

    public SweetAlertDialog returnDialog() {
        return alertDialog;
    }

    public void Failed(final String message) {
        if (isScreenOpen) {
            try {
                alertDialog.changeAlertType(ERROR_TYPE);
                if (message != null && !message.isEmpty() && message.length() > 1) {
                    alertDialog.setContentText(message);
                } else {
                    alertDialog.setContentText("Failed");
                }

                // alertDialog.setCustomImage(R.drawable.ic_error_red_24dp);
                alertDialog.show();
            } catch (WindowManager.BadTokenException bte) {

            } catch (IllegalStateException ise) {

            } catch (Exception e) {

            }
        }
    }
    public void NetworkError(String title, final String message) {
        try {
            alertDialog.changeAlertType(SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            alertDialog.setContentText(message);
            alertDialog.setTitleText(title);
            alertDialog.setCustomImage(R.drawable.ic_connection_lost_24dp);
            alertDialog.show();
        } catch (WindowManager.BadTokenException bte) {

        } catch (IllegalStateException ise) {

        } catch (IllegalArgumentException iae) {

        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {

        }

    }


    /*public void showMessage(final String title, final String message, int id, final int flag) {
        if (isScreenOpen) {
            try {
                if (title != null && !title.isEmpty()) {
                    alertDialog.setTitle(title);
                } else {
                    alertDialog.setTitle("Attention!");
                }
                if (message != null && !message.isEmpty() && message.length() > 1) {
                    alertDialog.setContentText(message);
                } else {
                    alertDialog.setContentText("Failed");
                }
                alertDialog.setCustomImage(id);
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        //1 For Update profile
                        if (flag == 1) {
                            context.startActivity(new Intent(context, UpdateProfileActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                        }
                        sweetAlertDialog.cancel();
                    }
                });
                alertDialog.show();
            } catch (WindowManager.BadTokenException bte) {

            } catch (IllegalStateException ise) {

            } catch (Exception e) {

            }
        }
    }*/
    public void showToUserMessage(final String title, final String message) {
        if (isScreenOpen) {
            try {
                if (title != null && !title.isEmpty()) {
                    alertDialog.setTitle(title);
                } else {
                    alertDialog.setTitle("Attention!");
                }
                if (message != null && !message.isEmpty() && message.length() > 1) {
                    alertDialog.setContentText(message);
                } else {
                    alertDialog.setContentText("Failed");
                }
                alertDialog.setCancelable(true);
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        sweetAlertDialog.cancel();
                    }
                });
                alertDialog.show();
            } catch (WindowManager.BadTokenException bte) {

            } catch (IllegalStateException ise) {

            } catch (Exception e) {

            }
        }
    }
    public void Successful(final String message) {
        if (isScreenOpen) {
            try {
                alertDialog.changeAlertType(SUCCESS_TYPE);
                if (message != null && !message.isEmpty() && message.length() > 1) {
                    alertDialog.setContentText(message);
                } else {
                    alertDialog.setContentText("Success");
                }
                // alertDialog.setCustomImage(R.drawable.ic_success);
                alertDialog.show();
            } catch (WindowManager.BadTokenException bte) {

            } catch (IllegalStateException ise) {

            } catch (Exception e) {

            }
        }
    }

    public void SuccessfulWithFinsh(boolean isCancelable, final String message, int typeId, String pageId) {
        if (isScreenOpen) {
            try {
                alertDialog.changeAlertType(SUCCESS_TYPE);
                alertDialog.setContentText(message);
                alertDialog.setCancelable(isCancelable);
                // alertDialog.setCustomImage(R.drawable.ic_success);
                alertDialog.setConfirmButton("Ok", sweetAlertDialog -> {
                    alertDialog.dismiss();
                    Intent intent =new Intent();
                    intent.putExtra("Type",typeId);
                    intent.putExtra("pageId",pageId);
                    context.setResult(Activity.RESULT_OK,intent);
                    context.finish();
                });
                alertDialog.show();
            } catch (WindowManager.BadTokenException bte) {

            } catch (IllegalStateException ise) {

            } catch (Exception e) {

            }
        }
    }

    public void SuccessfulWithFinsh(final String message, boolean isCancelable) {
        if (isScreenOpen) {
            try {
                alertDialog.changeAlertType(SUCCESS_TYPE);
                alertDialog.setContentText(message);
                alertDialog.setCancelable(isCancelable);
                // alertDialog.setCustomImage(R.drawable.ic_success);
                alertDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        alertDialog.dismiss();
                        ((Activity) context).finish();
                    }
                });
                alertDialog.show();
            } catch (WindowManager.BadTokenException bte) {

            } catch (IllegalStateException ise) {

            } catch (Exception e) {

            }
        }
    }

    public void SuccessfulWithCallBack(final String message, final Activity activity) {
        if (isScreenOpen) {
            try {
                alertDialog.changeAlertType(SUCCESS_TYPE);
                alertDialog.setContentText(message);
                // alertDialog.setCustomImage(R.drawable.ic_success);
                alertDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        activity.finish();
                    }
                });
                alertDialog.setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();

                    }
                });
                alertDialog.show();
            } catch (WindowManager.BadTokenException bte) {

            } catch (IllegalStateException ise) {

            } catch (Exception e) {

            }
        }
    }
    public void SuccessfulWithDismiss(final String message, final Activity activity) {
        if (isScreenOpen) {
            try {
                alertDialog.changeAlertType(SUCCESS_TYPE);
                alertDialog.setContentText(message);
                // alertDialog.setCustomImage(R.drawable.ic_success);
                alertDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                });
                alertDialog.show();
            } catch (WindowManager.BadTokenException bte) {

            } catch (IllegalStateException ise) {

            } catch (Exception e) {

            }
        }
    }


    public void Successfulok(final String message, final Activity activity) {
        if (isScreenOpen) {
            try {
                alertDialog.changeAlertType(SUCCESS_TYPE);
                alertDialog.setContentText(message);
                // alertDialog.setCustomImage(R.drawable.ic_success);
                alertDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        alertDialog.dismiss();
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                });
                alertDialog.show();

            } catch (WindowManager.BadTokenException bte) {

            } catch (IllegalStateException ise) {

            } catch (Exception e) {

            }
        }
    }

    public void SuccessfulWithTitle(final String title, final String message) {
        if (isScreenOpen) {
            try {
                alertDialog.changeAlertType(SUCCESS_TYPE);
                alertDialog.setTitle(title);
                alertDialog.setContentText(message);

                alertDialog.show();
            } catch (WindowManager.BadTokenException bte) {

            } catch (IllegalStateException ise) {

            } catch (Exception e) {

            }
        }
    }

    public void ErrorWithTitle(final String title, final String message) {
        if (isScreenOpen) {
            try {
                alertDialog.changeAlertType(ERROR_TYPE);
                alertDialog.setTitle(title);
                alertDialog.setContentText(message);
                alertDialog.show();
            } catch (WindowManager.BadTokenException bte) {

            } catch (IllegalStateException ise) {

            } catch (Exception e) {

            }
        }
    }


    public void Errorok(final String message, final Activity activity) {
        if (isScreenOpen) {
            try {
                alertDialog.changeAlertType(ERROR_TYPE);
                alertDialog.setContentText(message);
                // alertDialog.setCustomImage(R.drawable.ic_success);
                alertDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        alertDialog.dismiss();
                        activity.finish();
                    }
                });
                alertDialog.show();

            } catch (WindowManager.BadTokenException bte) {

            } catch (IllegalStateException ise) {

            } catch (Exception e) {

            }
        }
    }

    public void Warning(final String title, final String message) {
        if (isScreenOpen) {
            try {
                alertDialog.changeAlertType(WARNING_TYPE);
                alertDialog.setContentText(message);
                alertDialog.setTitle(title);
                // alertDialog.setCustomImage(R.drawable.ic_error_red_24dp);
                alertDialog.show();
            } catch (WindowManager.BadTokenException bte) {

            } catch (IllegalStateException ise) {

            } catch (Exception e) {

            }
        }
    }


    public interface CallBack{
        void onOkClick();
        void onCancelClick();
    }

    public void Warning(final String title, final String message, String okBtn,CallBack callBack) {
        if (isScreenOpen) {
            try {
                alertDialog.changeAlertType(WARNING_TYPE);
                alertDialog.setContentText(message);
                alertDialog.setTitle(title);
                // alertDialog.setCustomImage(R.drawable.ic_success);
                alertDialog.setCancelButtonBackgroundColor(R.color.grey_4);
                alertDialog.setConfirmButtonBackgroundColor(android.R.color.holo_green_dark);
                alertDialog.setConfirmButton(okBtn, sweetAlertDialog -> {
                    alertDialog.dismiss();
                    if(callBack!=null){
                        callBack.onOkClick();
                    }

                });
                alertDialog.setCancelButton("Cancel", sweetAlertDialog -> {
                    sweetAlertDialog.dismiss();
                    if(callBack!=null){
                        callBack.onCancelClick();
                    }
                });
                alertDialog.show();

            } catch (WindowManager.BadTokenException bte) {

            } catch (IllegalStateException ise) {

            } catch (Exception e) {

            }
        }
    }

    public void Warning(final String message) {
        if (isScreenOpen) {
            try {
                alertDialog.changeAlertType(WARNING_TYPE);
                alertDialog.setContentText(message);
                // alertDialog.setCustomImage(R.drawable.ic_error_red_24dp);
                alertDialog.show();
            } catch (WindowManager.BadTokenException bte) {

            } catch (IllegalStateException ise) {

            } catch (Exception e) {

            }
        }
    }


    public void Error(final String message) {
        if (isScreenOpen) {
            try {
                alertDialog.changeAlertType(ERROR_TYPE);
                if (message != null && !message.isEmpty() && message.length() > 1) {
                    alertDialog.setContentText(message);
                } else {
                    alertDialog.setContentText("Error");
                }
                // alertDialog.setCustomImage(R.drawable.ic_error_red_24dp);
                alertDialog.show();
            } catch (WindowManager.BadTokenException bte) {

            } catch (IllegalStateException ise) {

            } catch (Exception e) {

            }
        }
    }


    public void ErrorWithFinsh(final String message) {
        if (isScreenOpen) {
            try {
                alertDialog.changeAlertType(ERROR_TYPE);
                alertDialog.setContentText(message);
                // alertDialog.setCustomImage(R.drawable.ic_error_red_24dp);
                alertDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        alertDialog.dismiss();
                        ((Activity) context).finish();
                    }
                });
                alertDialog.show();
            } catch (WindowManager.BadTokenException bte) {

            } catch (IllegalStateException ise) {

            } catch (Exception e) {

            }
        }
    }


/*    public void sendReportDialog(final int type,String phoneNum, final DialogSingleCallBack mDialogSingleCallBack) {
        try {
            if (alertDialogSendReport != null && alertDialogSendReport.isShowing()) {
                return;
            }

           *//* type = 1 //Recharge Report
            type = 2 // Bank List
            type = 3 // Call Back Request*//*
            AlertDialog.Builder dialogBuilder;
            dialogBuilder = new AlertDialog.Builder(context);
            alertDialogSendReport = dialogBuilder.create();
            alertDialogSendReport.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = inflater.inflate(R.layout.dialog_send_report, null);
            alertDialogSendReport.setView(dialogView);

            final AppCompatEditText mobileEt = dialogView.findViewById(R.id.mobileEt);

            final AppCompatEditText emailEt = dialogView.findViewById(R.id.emailEt);
            TextView emailTitleTv= dialogView.findViewById(R.id.emailTitleTv);
            AppCompatTextView cancelBtn = dialogView.findViewById(R.id.cancelBtn);
            AppCompatTextView sendBtn = dialogView.findViewById(R.id.sendBtn);
            AppCompatImageView closeIv = dialogView.findViewById(R.id.closeIv);
            TextView titleTv = dialogView.findViewById(R.id.titleTv);
            if(phoneNum!=null ){
                mobileEt.setText(phoneNum);
            }
            if (type == 1) {
                titleTv.setText("Send Report");
            }
            if (type == 2) {
                titleTv.setText("Send Bank detail");
            }
            if (type == 3) {
                emailEt.setVisibility(View.GONE);
                emailTitleTv.setVisibility(View.GONE);
                titleTv.setText("Call Back Request");
            }

            closeIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialogSendReport.dismiss();
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialogSendReport.dismiss();
                }
            });

            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mobileEt.getText().toString().isEmpty()) {
                        mobileEt.setError("Please Enter Valid Mobile Number");
                        mobileEt.requestFocus();
                        return;
                    } else if (mobileEt.getText().toString().length() != 10) {
                        mobileEt.setError("Please Enter Valid Mobile Number");
                        mobileEt.requestFocus();
                        return;
                    } else if (type != 3&&emailEt.getText().toString().isEmpty()) {
                        emailEt.setError("Please Enter Valid Email Id");
                        emailEt.requestFocus();
                        return;
                    } else if (type != 3&&!emailEt.getText().toString().contains("@") || type != 3&&!emailEt.getText().toString().contains(".")) {
                        emailEt.setError("Please Enter Valid Email Id");
                        emailEt.requestFocus();
                        return;
                    }
                    if (mDialogSingleCallBack != null) {
                        alertDialogSendReport.dismiss();
                        mDialogSingleCallBack.onPositiveClick(mobileEt.getText().toString(), emailEt.getText().toString());
                    }
                }
            });


            alertDialogSendReport.show();

        } catch (IllegalStateException ise) {

        } catch (IllegalArgumentException iae) {

        } catch (Exception e) {

        }
    }*/

    public void dissmiss() {
        try {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        } catch (WindowManager.BadTokenException bte) {

        } catch (IllegalStateException ise) {

        } catch (Exception e) {

        }
    }


    public void Successfullogout(String message, Activity activity,PreferencesManager tokenManager) {
        if (isScreenOpen) {
            try {
                alertDialog.changeAlertType(WARNING_TYPE);
                alertDialog.setContentText(message);
                // alertDialog.setCustomImage(R.drawable.ic_success);
                alertDialog.setCancelButtonBackgroundColor(R.color.grey_4);
                alertDialog.setConfirmButtonBackgroundColor(android.R.color.holo_red_dark);
                alertDialog.setConfirmButton("Logout", sweetAlertDialog -> {
                    alertDialog.dismiss();
                    tokenManager.clear();
                    tokenManager.clearNonRemoval();
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                    activity.finishAffinity();
                });
                alertDialog.setCancelButton("Cancel", sweetAlertDialog -> sweetAlertDialog.dismiss());
                alertDialog.show();

            } catch (WindowManager.BadTokenException bte) {

            } catch (IllegalStateException ise) {

            } catch (Exception e) {

            }
        }
    }
}
