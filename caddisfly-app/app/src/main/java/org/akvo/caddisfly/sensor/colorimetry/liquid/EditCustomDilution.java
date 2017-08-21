/*
 * Copyright (C) Stichting Akvo (Akvo Foundation)
 *
 * This file is part of Akvo Caddisfly.
 *
 * Akvo Caddisfly is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Akvo Caddisfly is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Akvo Caddisfly. If not, see <http://www.gnu.org/licenses/>.
 */

package org.akvo.caddisfly.sensor.colorimetry.liquid;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import org.akvo.caddisfly.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditCustomDilution.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditCustomDilution#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditCustomDilution extends DialogFragment {

    private OnFragmentInteractionListener mListener;
    private EditText editDilutionFactor;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EditSensorIdentity.
     */
    public static EditCustomDilution newInstance() {
        EditCustomDilution fragment = new EditCustomDilution();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Context context = getActivity();

        LayoutInflater i = getActivity().getLayoutInflater();

        @SuppressLint("InflateParams")
        View view = i.inflate(R.layout.edit_custom_dilution, null);

        editDilutionFactor = view.findViewById(R.id.editDilutionFactor);

        editDilutionFactor.requestFocus();

        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.customDilution)
                .setPositiveButton(R.string.ok,
                        (dialog, whichButton) -> {
                            closeKeyboard(context, editDilutionFactor);
                            dismiss();
                        }
                )
                .setNegativeButton(R.string.cancel,
                        (dialog, whichButton) -> {
                            closeKeyboard(context, editDilutionFactor);
                            dismiss();
                        }
                );

        b.setView(view);
        return b.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        final Context context = getActivity();

        final AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (formEntryValid() && !editDilutionFactor.getText().toString().trim().isEmpty()) {
                        if (mListener != null) {
                            mListener.onFragmentInteraction(editDilutionFactor.getText().toString().trim());
                        }
                        closeKeyboard(context, editDilutionFactor);
                        dismiss();
                    }
                }

                private boolean formEntryValid() {
                    if (editDilutionFactor.getText().toString().trim().isEmpty()) {
                        editDilutionFactor.setError(getString(R.string.required));
                        return false;
                    }

                    return true;
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new IllegalArgumentException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Hides the keyboard
     *
     * @param input the EditText for which the keyboard is open
     */
    private void closeKeyboard(Context context, EditText input) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String value);
    }

}
