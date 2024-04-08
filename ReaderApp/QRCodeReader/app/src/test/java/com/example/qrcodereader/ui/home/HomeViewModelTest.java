package com.example.qrcodereader.ui.home;

import static org.mockito.Mockito.verify;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
/**
 * Test class for HomeViewModel.
 */

public class HomeViewModelTest {

    /**
     * Rule to execute tasks synchronously for testing LiveData.
     */
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private HomeViewModel homeViewModel;

    /**
     * Mock observer to observe changes in LiveData.
     */
    @Mock
    Observer<String> observer;

    /**
     * Sets up the environment for testing by initializing mocks and the ViewModel.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        homeViewModel = new HomeViewModel();
        homeViewModel.getText().observeForever(observer);
    }
    /**
     * Tests the getText() method to ensure it emits the expected value.
     */

    @Test
    public void testGetText() {
        verify(observer).onChanged("This is home fragment");
    }
}
