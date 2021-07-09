package tzdawa.app.mwakalonga.dawa;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindxmlviews();
        permissionconfigure();
    }

    private void bindxmlviews() {
        String build_version = "BUILD_VERSION: " + BuildConfig.VERSION_NAME;
        TextView txt_appversion = findViewById(R.id.txt_appversion);
        txt_appversion.setText(build_version);

        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void homepage() {
        startActivity(new Intent(MainActivity.this, tzdawa.app.mwakalonga.dawa.home.MainActivity.class));
    }

    private void permissionconfigure() {
        Dexter.withActivity(MainActivity.this).withPermissions(
                Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.WAKE_LOCK,
                Manifest.permission.CHANGE_WIFI_MULTICAST_STATE
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                //  progressBar.setVisibility(View.VISIBLE);
                if (report.areAllPermissionsGranted()) {
                    //  progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(() -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        homepage();
                        finish();
                    }, 5000);
                }
                if (report.isAnyPermissionPermanentlyDenied()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    a_showsettingsdialog();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(error -> Toast.makeText(MainActivity.this, "Permission problem occured", Toast.LENGTH_SHORT).show()).onSameThread().check();

    }


    private void a_showsettingsdialog() {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle("Permissions failure")
                .setMessage(("You need to grant " + getString(R.string.app_name) + " permissions to work properly. You can grant them in app settings."))

                .addButton("Open Settings", -1, -1,
                        CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                            //Toast.makeText(MainActivity.this, "Upgrade tapped", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            a_opensettings();
                        });
        builder.show();
    }

    private void a_opensettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}