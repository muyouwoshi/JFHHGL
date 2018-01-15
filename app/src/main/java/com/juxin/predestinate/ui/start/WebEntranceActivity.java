package com.juxin.predestinate.ui.start;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.juxin.library.log.PSP;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.util.UIShow;

public class WebEntranceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webapp_activity);

        final String url = PSP.getInstance().getString("test_url", "http://");

        final EditText text = (EditText) findViewById(R.id.edit_url);
        text.setText(url);

        Button btn = (Button) findViewById(R.id.url_submit);

        final Context context = this;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String endURL = text.getText().toString();
                PSP.getInstance().put("test_url", endURL);
                UIShow.showWebActivity(context, endURL);
            }
        });
    }
}
