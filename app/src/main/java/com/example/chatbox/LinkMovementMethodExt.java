package com.example.chatbox;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import static com.example.chatbox.MainActivity.recyclerView;
import android.widget.TextView;
import android.widget.Toast;

public class LinkMovementMethodExt extends LinkMovementMethod {
    private static Context movementContext;
    private static LinkMovementMethod linkMovementMethod = new LinkMovementMethodExt();


    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
            if (link.length != 0) {
                String[] menu = new String[]{
                        "Call",
                        "Send Sms",
                        "Save Contact",
                        "Copy"
                };






                final String url = link[0].getURL();




                if (url.matches("^((https?|ftp|smtp):\\/\\/)?(www.)?[a-z0-9]+\\.[a-z]+(\\/[a-zA-Z0-9#]+\\/?)*$")) {
                    Log.d("Link", url);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    movementContext.startActivity(i);
//
                } else if (url.startsWith("tel")) {
                    Log.d("Link", url);


                    AlertDialog.Builder builder = new AlertDialog.Builder(movementContext);
                    builder.setTitle(url.replace("tel:", ""));
                    builder.setItems(menu, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i){
                                case 0:
                                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                    callIntent.setData(Uri.parse(url));
                                    movementContext.startActivity(callIntent);
                                    break;

                                case 1:
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setType("vnd.android-dir/mms-sms");
                                    intent.putExtra("address", url.replace("tel:", ""));
                                    movementContext.startActivity(intent);
                                    break;

                                case 2:
                                    Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                                    contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                                    contactIntent.putExtra(ContactsContract.Intents.Insert.PHONE, url.replace("tel:", ""));
                                    movementContext.startActivity(contactIntent);
                                    break;

                                case 3:
                                    ClipboardManager clipboard = (ClipboardManager) movementContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("label", url.replace("tel:",""));
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(movementContext, "Copied", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else if (url.startsWith("mailto")) {

                    String recipient = url.replace("mailto:", "");
                    String[] recipiens = recipient.split(",");

                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.putExtra(Intent.EXTRA_EMAIL, recipiens);

                    intent.setData(Uri.parse("mailto:" + recipient));
                    movementContext.startActivity(Intent.createChooser(intent, "Open with:"));
                }else if (url.matches("[#]+[A-Za-z0-9-_]+\\b")){
                        recyclerView.scrollToPosition(5);
                }
                return true;
            }
        }
        return super.onTouchEvent(widget, buffer, event);
    }

    public static MovementMethod getInstantce(Context c){
        movementContext = c;
        return linkMovementMethod;
    }
}
