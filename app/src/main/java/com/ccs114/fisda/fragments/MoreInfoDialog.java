package com.ccs114.fisda.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import com.ccs114.fisda.R;

public class MoreInfoDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        TextView textView = new TextView(getContext());
        final SpannableString iucnLink = new SpannableString(Html.fromHtml((String) getText(R.string.iucn_red_list_info)));
        Linkify.addLinks(iucnLink, Linkify.ALL);

        textView.setText(iucnLink);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.titleDialog)
                .setMessage(textView.getText())
                .setPositiveButton(R.string.close, (dialogInterface, i) -> {

                }
                ).setNeutralButton(R.string.visit, (dialogInterface, i) -> openBrowser());
        return builder.create();
    }

    private void openBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.iucnredlist.org/"));
        startActivity(intent);
    }

}
