package com.infotech.wishmaplus.Utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.infotech.wishmaplus.Activity.TermPrivacyActivity;
import com.infotech.wishmaplus.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author pradeep.arige
 */
public enum Utility {
    INSTANCE;


    public String getAmountFormat(String amount) {
        StringBuilder strBind = new StringBuilder(amount);
        strBind.append(".00");
        return strBind.toString();
    }


    public String getAmountFormawitdot(String amount) {
        Pattern regex = Pattern.compile("[.]");
        Matcher matcher = regex.matcher(amount);
        if (matcher.find()) {
            // Do something
            return amount;
        } else {
            return getAmountFormat(amount);
        }

    }

    public double convertStringToDouble(String data) {
        try {
            NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
            double amountcheck = nf.parse(data).doubleValue();
            return amountcheck;
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static String getRupeeAmount(double value) {
        String rupeeAmount = "0.0";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
        rupeeAmount = formatter.format(value);
        return rupeeAmount;

    }

    public static String chckStringNull(String value) {
        String value1 = "";
        try {
            if (value != null &&
                    !value.equalsIgnoreCase(null) &&
                    !value.equalsIgnoreCase("null") &&
                    !value.equalsIgnoreCase("")) {
                value1 = value.trim();
            } else {
                value1 = "";
            }
        } catch (Exception e) {
            value1 = "";
        }
        return value1;
    }

    public String formattedAmountWithRupees(Object amt) {

        String value="";
        if(amt instanceof Double){
            value = String.valueOf(amt);
        }
        if (value != null && !value.isEmpty()) {
            if (value.contains(".")) {
                String postfixValue = value.substring(value.indexOf("."));
                if (postfixValue.equalsIgnoreCase(".0")) {
                    return "\u20B9 " + value.replace(".0", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".00")) {
                    return "\u20B9 " + value.replace(".00", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".000")) {
                    return "\u20B9 " + value.replace(".000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".0000")) {
                    return "\u20B9 " + value.replace(".0000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".00000")) {
                    return "\u20B9 " + value.replace(".00000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".000000")) {
                    return "\u20B9 " + value.replace(".000000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".0000000")) {
                    return "\u20B9 " + value.replace(".0000000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".00000000")) {
                    return "\u20B9 " + value.replace(".00000000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".000000000")) {
                    return "\u20B9 " + value.replace(".000000000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".0000000000")) {
                    return "\u20B9 " + value.replace(".0000000000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".00000000000")) {
                    return "\u20B9 " + value.replace(".00000000000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".000000000000")) {
                    return "\u20B9 " + value.replace(".000000000000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".0000000000000")) {
                    return "\u20B9 " + value.replace(".0000000000000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".00000000000000")) {
                    return "\u20B9 " + value.replace(".00000000000000", "").trim();
                }  else if (postfixValue.equalsIgnoreCase(".000000000000000")) {
                    return "\u20B9 " + value.replace(".000000000000000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".0000000000000000")) {
                    return "\u20B9 " + value.replace(".0000000000000000", "").trim();
                } else {
                    try {
                        return "\u20B9 " + String.format("%.2f", Double.parseDouble(value.trim()));
                    } catch (NumberFormatException nfe) {
                        return "\u20B9 " + value.trim();
                    }
                }
            } else {
                return "\u20B9 " + value.trim();
            }

        } else {
            return "\u20B9 0";
        }
    }

    public String formatedAmountWithOutRupees(String value) {

        if (value != null && !value.isEmpty()) {
            if (value.contains(".")) {
                String postfixValue = value.substring(value.indexOf("."));
                if (postfixValue.equalsIgnoreCase(".0")) {
                    return value.replace(".0", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".00")) {
                    return value.replace(".00", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".000")) {
                    return value.replace(".000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".0000")) {
                    return value.replace(".0000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".00000")) {
                    return value.replace(".00000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".000000")) {
                    return value.replace(".000000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".0000000")) {
                    return value.replace(".0000000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".00000000")) {
                    return value.replace(".00000000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".000000000")) {
                    return value.replace(".000000000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".0000000000")) {
                    return value.replace(".0000000000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".00000000000")) {
                    return value.replace(".00000000000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".000000000000")) {
                    return value.replace(".000000000000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".0000000000000")) {
                    return value.replace(".0000000000000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".00000000000000")) {
                    return value.replace(".00000000000000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".000000000000000")) {
                    return value.replace(".000000000000000", "").trim();
                } else if (postfixValue.equalsIgnoreCase(".0000000000000000")) {
                    return value.replace(".0000000000000000", "").trim();
                } else {
                    try {
                        return String.format("%.2f", Double.parseDouble(value.trim()));
                    } catch (NumberFormatException nfe) {
                        return value.trim();
                    }
                }
            } else {
                return value.trim();
            }

        } else {
            return "0";
        }
    }

    public String formatedOnlyDate(String dateStr) {
        if (dateStr != null && !dateStr.isEmpty()) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM");
            try {
                Date date = inputFormat.parse(dateStr);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return dateStr;
            }
        }
        return "";
    }

    public String formatedDate(String dateStr) {
        if (dateStr != null && !dateStr.isEmpty()) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss:SSS aa");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
            try {
                Date date = inputFormat.parse(dateStr);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return dateStr;
            }

        }
        return "";
    }

    public String formatedDateMonthYear(String dateStr) {
        if (dateStr != null && !dateStr.isEmpty()) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM yyyy");
            try {
                Date date = inputFormat.parse(dateStr);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return dateStr;
            }

        }
        return "";
    }

    public String formatedDateWithT(String dateStr) {
        if (dateStr != null && !dateStr.isEmpty()) {

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.sss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
            try {
                Date date = inputFormat.parse(dateStr.replace("T", " "));
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return dateStr.replace("T", " ");
            }
        }
        return "";
    }

    public String formatedDate3(String dateStr) {
        if (dateStr != null && !dateStr.isEmpty()) {

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
            try {
                Date date = inputFormat.parse(dateStr);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return dateStr.replace("T", " ");
            }
        }
        return "";
    }

    public String formatedDate2(String dateStr) {
        if (dateStr != null && !dateStr.isEmpty()) {

            SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
            try {
                Date date = inputFormat.parse(dateStr);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return dateStr;
            }
        }
        return "";
    }

    public String formatedDateOfSlash(String dateStr) {
        if (dateStr != null && !dateStr.isEmpty()) {

            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
            try {
                Date date = inputFormat.parse(dateStr);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return dateStr;
            }
        }
        return "";
    }

    public String formatedDateOfDash(String dateStr) {
        if (dateStr != null && !dateStr.isEmpty()) {

            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                Date date = inputFormat.parse(dateStr);
                return outputFormat.format(date);
            } catch (ParseException e) {
                return dateStr;
            }


        }
        return "";
    }

    public String formatedDateTimeOfSlash(String dateStr) {
        if (dateStr != null && !dateStr.isEmpty()) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
            try {
                Date date = inputFormat.parse(dateStr);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return dateStr;
            }
        }
        return "";
    }

    public String formatedDateTimeOfDash(String dateStr) {
        if (dateStr != null && !dateStr.isEmpty()) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
            try {
                Date date = inputFormat.parse(dateStr);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return dateStr;
            }
        }
        return "";
    }

    public String formatedDateYMD(String dateStr) {
        if (dateStr != null && !dateStr.isEmpty()) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
            try {
                Date date = inputFormat.parse(dateStr);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return dateStr;
            }
        }
        return "";
    }

    public boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    protected void makeLinkClickable(final Context context, SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(span.getURL()))
                            .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                } catch (ActivityNotFoundException anfe) {

                }
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    public void setTextViewHTML(Context context, TextView text, String html) {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(context, strBuilder, span);
        }
        text.setText(strBuilder);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public boolean isValidString(String str) {
        if (str != null) {
            str = str.trim();
            if (str.length() > 0) {
                return true;
            }
        }

        return false;
    }

    public boolean isSpecialCharacter(String s) {

        if (s != null && !s.isEmpty()) {
            Pattern p = Pattern.compile("[^a-zA-Z ]");
            Matcher m = p.matcher(s);
            boolean b = m.find();

            if (b) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }

    }

    public InputFilter getEditTextCharacterFilter() {
        return (source, start, end, dest, dstart, dend) -> {

            boolean keepOriginal = true;
            StringBuilder sb = new StringBuilder(end - start);
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                if (isCharAllowed(c)) // put your condition here
                    sb.append(c);
                else
                    keepOriginal = false;
            }
            if (keepOriginal)
                return null;
            else {
                if (source instanceof Spanned) {
                    SpannableString sp = new SpannableString(sb);
                    TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                    return sp;
                } else {
                    return sb;
                }
            }
        };
    }

    private boolean isCharAllowed(char c) {
        Pattern ps = Pattern.compile("^[a-zA-Z ]+$");
        Matcher ms = ps.matcher(String.valueOf(c));
        return ms.matches();
    }

    public void openWhatsapp(Activity mContext, String smsNumber) {

        try {
            Intent sendIntent = new Intent(Intent.ACTION_MAIN);
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            // sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello");
            sendIntent.putExtra("jid", "91" + smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
            sendIntent.setPackage("com.whatsapp");
            mContext.startActivity(sendIntent);
        } catch (Exception e) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setPackage("com.whatsapp");
                intent.setData(Uri.parse(String.format("https://api.whatsapp.com/send?phone=%s", "91" + smsNumber)));
                if (mContext.getPackageManager().resolveActivity(intent, 0) != null) {
                    mContext.startActivity(intent);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://api.whatsapp.com/send?phone=%s", "91" + smsNumber)));
                    mContext.startActivity(intent);
                }
            } catch (Exception ex) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://api.whatsapp.com/send?phone=%s", "91" + smsNumber)));
                mContext.startActivity(intent);

            }
        }

    }

    public byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis = new FileInputStream(f);

        try {

            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }


    public void setClipboard(Activity mActivity, String text, String type) {

        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(text);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Share Link", text);
                clipboard.setPrimaryClip(clip);
            }
            Toast.makeText(mActivity, type + " Copied to clipboard", Toast.LENGTH_LONG).show();
        } catch (Exception e) {

        }
    }


    public void shareit(Activity mActivity, LinearLayout shareView, boolean isWhatsapp, boolean isDownload) {
       /* Bitmap myBitmap = Bitmap.createBitmap(shareView.getWidth(), shareView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(myBitmap);
        shareView.layout(0, 0, shareView.getWidth(), shareView.getHeight());
        shareView.draw(c);
        saveBitmap(mActivity, myBitmap, isWhatsapp, isDownload);*/
        shareView.setDrawingCacheEnabled(true);
        shareView.buildDrawingCache();
        Bitmap myBitmap = shareView.getDrawingCache();
        saveBitmap(mActivity, myBitmap, isWhatsapp, isDownload);
    }

    private void saveBitmap(Activity mActivity, Bitmap bitmap, boolean isWhatsapp, boolean isDownload) {
        if (Build.VERSION.SDK_INT >= 30) {
            ContentValues values = contentValues();
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + mActivity.getResources().getString(R.string.app_name));
            values.put(MediaStore.Images.Media.IS_PENDING, true);

            Uri uri = mActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try {
                    saveImageToStream(bitmap, mActivity.getContentResolver().openOutputStream(uri));
                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    mActivity.getContentResolver().update(uri, values, null, null);
                    if (isDownload) {
                        Toast.makeText(mActivity, "Successfully Download", Toast.LENGTH_SHORT).show();
                        MediaScannerConnection.scanFile(mActivity, new String[]{uri.getPath()}, new String[]{"image/png"}, null);
                    } else {
                        if (isWhatsapp) {
                            openWhatsapp(mActivity, uri);
                        } else {
                            sendMail(mActivity, uri);
                        }
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        } else {
            File directory = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/" +
                    mActivity.getResources().getString(R.string.app_name));

            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = System.currentTimeMillis() + ".png";
            File file = new File(directory, fileName);
            try {
                saveImageToStream(bitmap, new FileOutputStream(file));
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                Uri pathUri = mActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (isDownload) {
                    Toast.makeText(mActivity, "Successfully Download", Toast.LENGTH_SHORT).show();
                    MediaScannerConnection.scanFile(mActivity, new String[]{file.getPath()}, new String[]{"image/png"}, null);
                } else {
                    if (isWhatsapp) {
                        openWhatsapp(mActivity, pathUri);
                    } else {
                        sendMail(mActivity, pathUri);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private ContentValues contentValues() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        }
        return values;
    }

    private void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void openWhatsapp(Activity mActivity, Uri myUri) {

        try {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "AEPS Receipt");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Receipt");
            sendIntent.setType("image/png");
            //Uri myUri = Uri.parse("file://" + path);
            sendIntent.putExtra(Intent.EXTRA_STREAM, myUri);
            sendIntent.setPackage("com.whatsapp");
            mActivity.startActivity(sendIntent);
        } catch (ActivityNotFoundException e) {

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.whatsapp"));
            mActivity.startActivity(intent);


        }

    }

    private void sendMail(Activity mActivity, Uri myUri) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                "AEPS Receipt");
        emailIntent.putExtra(Intent.EXTRA_TEXT,
                "Receipt");
        emailIntent.setType("image/png");
        // Uri myUri = Uri.parse("file://" + path);
        emailIntent.putExtra(Intent.EXTRA_STREAM, myUri);
        mActivity.startActivity(Intent.createChooser(emailIntent, "Share via..."));
    }

   public long getFileDuration(Activity activity,String path) {

        try {
           /* MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            long timeInMillis = mediaPlayer.getDuration();
            mediaPlayer.stop();
            mediaPlayer.release();*/
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(activity, Uri.parse(path));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMillis = Long.parseLong(time);
            retriever.release();
           return timeInMillis;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    public Long getFileDurationLess42(Activity activity,String path) {

        try {
           /* MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            long timeInMillis = mediaPlayer.getDuration();
            mediaPlayer.stop();
            mediaPlayer.release();*/
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(activity, Uri.parse(path));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMillis = Long.parseLong(time);
            retriever.release();
           return timeInMillis-42;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void setTerm_Privacy(Activity mActivity, TextView termAndPrivacyTxt, int fromTerm, int toTerm,
                                int fromPrivacy, int toPrivacy) {
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(termAndPrivacyTxt.getText().toString());
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity,R.color.colorPrimary)), fromTerm, toTerm, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View v) {
                /*termAndPrivacyTxt.setChecked(termAndPrivacyTxt.isChecked());*/
                mActivity.startActivity(new Intent(mActivity, TermPrivacyActivity.class)
                        .putExtra("URL", ApplicationConstant.INSTANCE.term_condition_url)
                        .putExtra("Title", "Term & Condition"));

            }
        }, fromTerm, toTerm, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity,R.color.colorPrimary)), fromPrivacy,
                toPrivacy, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View v) {

                /*termAndPrivacyTxt.setChecked(termAndPrivacyTxt.isChecked());*/
                mActivity.startActivity(new Intent(mActivity, TermPrivacyActivity.class)
                        .putExtra("URL", ApplicationConstant.INSTANCE.privacy_policy_url)
                        .putExtra("Title", "Privacy Policy"));
            }
        }, fromPrivacy, toPrivacy, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        termAndPrivacyTxt.setText(spannable);

        termAndPrivacyTxt.setMovementMethod(LinkMovementMethod.getInstance());
    }

  /*  public void setTerm_Privacy(Activity mActivity, TextView termAndPrivacyTxt, int fromTerm, int toTerm,
                                int fromPrivacy, int toPrivacy) {
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(termAndPrivacyTxt.getText().toString());
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity,R.color.colorPrimary)), fromTerm, toTerm, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View v) {
                *//*termAndPrivacyTxt.setChecked(termAndPrivacyTxt.isChecked());*//*
                mActivity.startActivity(new Intent(mActivity, TermPrivacyActivity.class)
                        .putExtra("URL", ApplicationConstant.INSTANCE.term_condition_url)
                        .putExtra("Title", "Term & Condition"));

            }
        }, fromTerm, toTerm, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity,R.color.colorPrimary)), fromPrivacy,
                toPrivacy, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View v) {

                *//*termAndPrivacyTxt.setChecked(termAndPrivacyTxt.isChecked());*//*
                mActivity.startActivity(new Intent(mActivity, TermPrivacyActivity.class)
                        .putExtra("URL", ApplicationConstant.INSTANCE.privacy_policy_url)
                        .putExtra("Title", "Privacy Policy"));
            }
        }, fromPrivacy, toPrivacy, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        termAndPrivacyTxt.setText(spannable);

        termAndPrivacyTxt.setMovementMethod(LinkMovementMethod.getInstance());
    }*/
}
