package com.example.qrcodereader.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import com.example.qrcodereader.R;

//THIS DOES NOT WORK THIS SHOULD ALWAYS FAIL brokey
@RunWith(RobolectricTestRunner.class)
public class HomeFragmentTest {

    private HomeFragment homeFragment;

    @Mock
    private View mockView;

    @Mock
    private Button mockButton;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        homeFragment = new HomeFragment();
        when(mockView.findViewById(R.id.profile_button)).thenReturn(mockButton);
        when(mockView.findViewById(R.id.my_event_button)).thenReturn(mockButton);
        when(mockView.findViewById(R.id.admin_button)).thenReturn(mockButton);
    }

    @Test
    public void testOnCreateView() {
        View view = homeFragment.onCreateView(LayoutInflater.from(RuntimeEnvironment.application), null, null);
        assertNotNull(view);
    }

    @Test
    public void testOnDestroyView() {
        homeFragment.onDestroyView();
        assertNull(ReflectionHelpers.getField(homeFragment, "binding"));
    }
}
