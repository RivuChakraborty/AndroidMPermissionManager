package com.rivuchk.mpermissionhandler;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionManager = PermissionManager.createInstanceFor(this);

        Button btn1 = (Button) findViewById(R.id.btn1);
        Button btn2 = (Button) findViewById(R.id.btn2);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionManager.addRequestPermission(Manifest.permission.CAMERA)
                        .setPermissionRequestCallback(new PermissionManager.PermissionRequestCallback() {
                            @Override
                            public void onAllPermissionsGranted(String[] permissions) {
                                Toast.makeText(getBaseContext(),"Got Camera",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onAllPermissionsDenied(String[] Permissions) {
                                Toast.makeText(getBaseContext(),"Camera denied",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onPartialPermissionsGranted(String[] grantedPermissions) {
                                Toast.makeText(getBaseContext(),"Camera denied partial "+grantedPermissions[0],Toast.LENGTH_LONG).show();
                            }
                        }).startRequest();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionManager.addRequestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE})
                        .setPermissionRequestCallback(new PermissionManager.PermissionRequestCallback() {
                            @Override
                            public void onAllPermissionsGranted(String[] permissions) {
                                Toast.makeText(getBaseContext(),"Got All",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onAllPermissionsDenied(String[] Permissions) {
                                Toast.makeText(getBaseContext(),"All denied",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onPartialPermissionsGranted(String[] grantedPermissions) {
                                Toast.makeText(getBaseContext(),"denied partial "+grantedPermissions[0],Toast.LENGTH_LONG).show();
                            }
                        }).startRequest();
            }
        });
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * createInstanceFor the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionResult(requestCode,permissions,grantResults);
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        permissionManager.onActivityResult(requestCode,resultCode,data);
    }
}
