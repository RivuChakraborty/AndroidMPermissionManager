package com.rivuchk.mpermissionhandler;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by developer on 10/1/17.
 */

public final class PermissionManager {

    private final SharedPreferences permissionStatus;
    private android.app.Fragment supportfragment;
    private Fragment fragment;
    private Activity activity;

    private static final int PERMISION_REQUEST_CODE = 101;
    private static final int REQUEST_PERMISSION_SETTING = 104;

    private boolean isPermissionRequestRunning = false;
    private boolean sentToSettings = false;
    private PermissionRequestCallback permissionRequestCallback;

    ArrayList<String> permissions = new ArrayList<>();
    private String title = "Please Grant The Permissions";
    private String message = "Please grant the permissions, so that we can server you better";
    private String toastMessage = "Go to Permissions to Grant all Permissions";

    private PermissionManager(Activity activity) {
        this.activity = activity;
        this.fragment = null;
        permissionStatus = activity.getSharedPreferences("permissionStatus", activity.MODE_PRIVATE);
    }

    private PermissionManager(Fragment fragment) {
        this.activity = fragment.getActivity();
        this.fragment = fragment;
        this.supportfragment = null;
        permissionStatus = fragment.getActivity().getSharedPreferences("permissionStatus", activity.MODE_PRIVATE);
    }

    private PermissionManager(android.app.Fragment fragment) {
        this.activity = fragment.getActivity();
        this.fragment = null;
        this.supportfragment = fragment;
        permissionStatus = fragment.getActivity().getSharedPreferences("permissionStatus", activity.MODE_PRIVATE);
    }

    public static PermissionManager createInstanceFor(Fragment fragment) {
        return new PermissionManager(fragment);
    }

    public static PermissionManager createInstanceFor(android.app.Fragment fragment) {
        return new PermissionManager(fragment);
    }

    public static PermissionManager createInstanceFor(Activity activity) {
        return new PermissionManager(activity);
    }

    public PermissionManager clearPermissionsArray() {
        permissions.clear();
        isPermissionRequestRunning = false;
        return this;
    }

    public PermissionManager setAlertMessages(String title, String message, String toastMessage) {
        this.title = title;
        this.message = message;
        this.toastMessage = toastMessage;
        return this;
    }

    public PermissionManager addRequestPermission(final String permission) {

        permissions.add(permission);
        return this;
    }

    public PermissionManager addRequestPermissions(String[] permissionsArray) {
        permissions.addAll(Arrays.asList(permissionsArray));
        return this;
    }

    public PermissionManager setPermissionRequestCallback(PermissionRequestCallback permissionRequestCallback) {
        this.permissionRequestCallback = permissionRequestCallback;
        return this;
    }

    public void startRequest() {
        boolean isPermissionNotAvailable = false;
        boolean allPermissionStatus = true;
        boolean shouldShowRationale = false;
        for (String permission : permissions) {
            isPermissionNotAvailable = isPermissionNotAvailable || ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED;
            allPermissionStatus = allPermissionStatus && permissionStatus.getBoolean(permission, false);
            shouldShowRationale = shouldShowRationale || ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        }

        if (isPermissionNotAvailable) {
            isPermissionRequestRunning = true;

            if (shouldShowRationale) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(title);
                builder.setMessage(message);
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if(null != fragment && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            fragment.requestPermissions(permissions.toArray(new String[]{}), PERMISION_REQUEST_CODE);
                        } else if(null != supportfragment && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            supportfragment.requestPermissions(permissions.toArray(new String[]{}), PERMISION_REQUEST_CODE);
                        } else {
                            ActivityCompat.requestPermissions(activity, permissions.toArray(new String[]{}), PERMISION_REQUEST_CODE);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if (permissionRequestCallback != null) {
                            permissionRequestCallback.onAllPermissionsDenied(permissions.toArray(new String[]{}));
                        }
                    }
                });
                builder.show();
            } else if (allPermissionStatus) {
                //Previously Permission Request was cancelled createInstanceFor 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera and Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        if(null != fragment){
                            fragment.startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        } else if(null != supportfragment) {
                            supportfragment.startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        } else {
                            activity.startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        }
                        Toast.makeText(activity, toastMessage, Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if (permissionRequestCallback != null) {
                            permissionRequestCallback.onAllPermissionsDenied(permissions.toArray(new String[]{}));
                        }
                    }
                });
                builder.show();
            } else {
                //just request the permission
                if(null != fragment && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    fragment.requestPermissions(permissions.toArray(new String[]{}), PERMISION_REQUEST_CODE);
                } else if(null != supportfragment && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    supportfragment.requestPermissions(permissions.toArray(new String[]{}), PERMISION_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(activity, permissions.toArray(new String[]{}), PERMISION_REQUEST_CODE);
                }

            }




            for (String permission : permissions) {
                SharedPreferences.Editor editor = permissionStatus.edit();
                editor.putBoolean(permission, true);
                editor.commit();
            }
        } else {
            //You already have the permission, just go ahead.
            if(permissionRequestCallback != null) {
                permissionRequestCallback.onAllPermissionsGranted(permissions.toArray(new String[]{}));
            }
        }

    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissionsRequested, @NonNull int[] grantResults) {
        if(requestCode == PERMISION_REQUEST_CODE && isPermissionRequestRunning){
            isPermissionRequestRunning = false;
            //check if all permissions are granted
            boolean allgranted = false;
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if(allgranted){
                permissionRequestCallback.onAllPermissionsGranted(permissions.toArray(new String[]{}));
                clearPermissionsArray();
            } else {
                boolean shouldShowRationale = false;
                final ArrayList<String> grantedPermissions = new ArrayList<>();
                for (String permission : permissions) {
                    if(ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED){
                        grantedPermissions.add(permission);
                    }
                    shouldShowRationale = shouldShowRationale || ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
                }
                if (shouldShowRationale) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(title);
                    builder.setMessage(message);
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            if(null != fragment && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                fragment.requestPermissions(permissions.toArray(new String[]{}), PERMISION_REQUEST_CODE);
                            } else if(null != supportfragment && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                supportfragment.requestPermissions(permissions.toArray(new String[]{}), PERMISION_REQUEST_CODE);
                            } else {
                                ActivityCompat.requestPermissions(activity, permissions.toArray(new String[]{}), PERMISION_REQUEST_CODE);
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            if (permissionRequestCallback != null) {
                                if(grantedPermissions.size()<=0) {
                                    permissionRequestCallback.onAllPermissionsDenied(permissions.toArray(new String[]{}));
                                } else {
                                    permissionRequestCallback.onPartialPermissionsGranted(grantedPermissions.toArray(new String[]{}));
                                }
                            }
                        }
                    });
                    builder.show();
                    isPermissionRequestRunning = true;
                } else {
                    clearPermissionsArray();
                    if (permissionRequestCallback != null) {
                        if(grantedPermissions.size()<=0) {
                            permissionRequestCallback.onAllPermissionsDenied(permissions.toArray(new String[]{}));
                        } else {
                            permissionRequestCallback.onPartialPermissionsGranted(grantedPermissions.toArray(new String[]{}));
                        }
                    }
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PERMISSION_SETTING && isPermissionRequestRunning) {
            isPermissionRequestRunning = false;

            boolean gotAll = true;
            ArrayList<String> grantedPermissions = new ArrayList<>();
            for (String permission : permissions) {
                gotAll = gotAll && ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
                if(ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED){
                    grantedPermissions.add(permission);
                }
            }

            if (permissionRequestCallback != null) {
                if(!gotAll) {
                    if (grantedPermissions.size() <= 0) {
                        permissionRequestCallback.onAllPermissionsDenied(permissions.toArray(new String[]{}));
                    } else {
                        permissionRequestCallback.onPartialPermissionsGranted(grantedPermissions.toArray(new String[]{}));
                    }
                } else {
                    permissionRequestCallback.onAllPermissionsGranted(permissions.toArray(new String[]{}));
                }
            }

            clearPermissionsArray();
        }
    }

    public interface PermissionRequestCallback {
        void onAllPermissionsGranted(String[] permissions);

        void onAllPermissionsDenied(String[] Permissions);

        void onPartialPermissionsGranted(String[] grantedPermissions);
    }

}
