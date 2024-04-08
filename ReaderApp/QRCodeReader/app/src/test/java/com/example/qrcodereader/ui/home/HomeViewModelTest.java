package com.example.qrcodereader.ui.home;

import static org.mockito.Mockito.verify;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class HomeViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private HomeViewModel homeViewModel;

    @Mock
    Observer<String> observer;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        homeViewModel = new HomeViewModel();
        homeViewModel.getText().observeForever(observer);
    }

    @Test
    public void testGetText() {
        verify(observer).onChanged("This is home fragment");
    }
}
