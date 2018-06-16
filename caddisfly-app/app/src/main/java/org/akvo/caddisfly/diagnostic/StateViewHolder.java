package org.akvo.caddisfly.diagnostic;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.akvo.caddisfly.R;

public class StateViewHolder extends RecyclerView.ViewHolder {

    TextView value;
    TextView rgb;
    TextView hsv;
    TextView swatch;

    public StateViewHolder(View itemView) {
        super(itemView);

        swatch = itemView.findViewById(R.id.textSwatch);
        value = itemView.findViewById(R.id.textValue);
        rgb = itemView.findViewById(R.id.textRgb);
        hsv = itemView.findViewById(R.id.textHsv);
    }
}