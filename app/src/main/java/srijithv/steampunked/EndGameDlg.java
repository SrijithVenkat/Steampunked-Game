package edu.msu.srijithv.steampunked;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;
//
//  Display the end of game dialog box.
//
@SuppressWarnings("ALL")
public class EndGameDlg extends DialogFragment {
    @SuppressWarnings("FieldCanBeLocal")
    private final String ALERT_TITLE = "Alert_title";
    private final String ALERT_MESSSAGE = "Alert_message";

    private final String title;
    private final String message;
    /**
     * Save the id we are deleting in case we have to try again
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putString(ALERT_TITLE,  title);
        bundle.putString(ALERT_MESSSAGE, message);
    }

    public EndGameDlg(String title, String message) {
        this.title = title;
        this.message = message;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(Objects.requireNonNull(getActivity())); // R.style.CustomDialog);

        // Parameterize the builder
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton(android.R.string.ok, (dialogInterface, i) -> getActivity().finish());
        // Create the dialog box and show it
        AlertDialog alertDialog = builder.create();
        //alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            wlp.gravity = Gravity.RIGHT;
        } else {
            wlp.gravity = Gravity.BOTTOM;
        }
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        return alertDialog;
    }
}
