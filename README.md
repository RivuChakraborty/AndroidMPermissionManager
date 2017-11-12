# Android Runtime Permission Manager

## A backward compatible, callback driven, easy to use plugin to manage Runtime Permission (also compatible with pre-marshmallow static permission model).

With **Android 6.0 Marshmallow (API 23)**, a new runtime permission model has been introduced. According to this model users are not prompted for permission at the time of installing the app, rather developers need to check for and request permission at runtime (i.e. before performing any action that may require the particular permission)

I coded this plugin in a callback driven manner, i.e. it has callbacks for when user grants/denies/partially grants (grants one or few of the multiple permissions requested) the permissions. As already mentioned this plugin is backward compatible, so it’ll automatically call the callback method for permissions granted (if it is granted by the system, in the same flow as it was in pre-marshmallow) in pre-marshmallow devices.

Follow the below How to Use guide to integrate this plugin with your project and forget all permission headaches 🙂

Declare gradle dependancy.


    dependencies {
        implementation ‘com.rivuchk.mpermissionhandler:permissionmanager:1.0’
    }


Declare a Global variable in your `Activity` / `Fragment` class

    private PermissionManager permissionManager;
	
Initialise it inside `onCreate()` method of your `Activity` / `Fragment`.

    permissionManager = PermissionManager.createInstanceFor(this);
    Override onActivityResult and onRequestPermissionsResult methods like below

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
          permissionManager.onRequestPermissionResult(requestCode,permissions,grantResults);
        } catch (Exception e) {
          e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
       try {
          permissionManager.onActivityResult(requestCode,resultCode,data);
       } catch (Exception e) {
          e.printStackTrace();
       }
    }
    
Declare the required permissions in your manifest (replace the `READ_PHONE_STATE` permission with your required one)

    <uses-permission android:name=“android.permission.READ_PHONE_STATE” />

Then request permission inside your activity or fragment as below

    permissionManager.addRequestPermission(Manifest.permission.READ_PHONE_STATE)// add required permission to the request list
    //set Messages for alert and toast
      .setAlertMessages(“Permission Request Alert Title”,“Permission Request Explanation”,“Toast Message asking user to go to settings (called only when user previously denied permission with Never ask again)”)
    //set Request Callback
      .setPermissionRequestCallback(new PermissionManager.PermissionRequestCallback() {
          @Override
          public void onAllPermissionsGranted(String[] permissions) {
              //Proceed with your permission
          }

          @Override
          public void onAllPermissionsDenied(String[] Permissions) {
              //All Permissions denied, do job accordingly
          }

          @Override
          public void onPartialPermissionsGranted(String[] grantedPermissions) {
              //few of the permissions are granted, String[] grantedPermissions contains the granted permissions
          }
      })
      .startRequest(); //start permission request

You can also add a permissions array to the list with `addRequestPermissions(String[] permissionsArray)` method

This plugin now also includes **Kotlin Extension Functions**. Create instance of `PermissionManager` as with just `createPermissionManagerInstance` inside any Activity and/or Fragment class.

Enjoy Independancy from Runtime Permission Request Headache and continue with your project �. Yes Its that simple.

For more information visit [rivuchk.com](http://www.rivuchk.com)

License

Copyright (c) 2017 Rivu Chakraborty.

Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007(the “License”);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  https://www.gnu.org/licenses/gpl-3.0.en.html

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an “AS IS” BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.# AndroidMPermissionManager
This is a plugin which will help android developers to integrate M Permission callback to the project easily with just few lines of code.
