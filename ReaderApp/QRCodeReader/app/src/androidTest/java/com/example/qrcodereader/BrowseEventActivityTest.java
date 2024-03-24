package com.example.qrcodereader;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;

import org.junit.runner.RunWith;



@RunWith(AndroidJUnit4.class)
public class BrowseEventActivityTest {

    @Rule
    public GrantPermissionRule grantLocationPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);



}
