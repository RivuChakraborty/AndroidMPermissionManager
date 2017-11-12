package com.rivuchk.mpermissionhandler

import android.app.Activity

/**
 * Created by Rivu on 12-11-2017.
 */

fun Activity.createPermissionManagerInstance():PermissionManager = PermissionManager.createInstanceFor(this)
fun android.app.Fragment.createPermissionManagerInstance():PermissionManager = PermissionManager.createInstanceFor(this)
fun android.support.v4.app.Fragment.createPermissionManagerInstance():PermissionManager = PermissionManager.createInstanceFor(this)