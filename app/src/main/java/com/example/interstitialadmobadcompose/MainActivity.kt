package com.example.interstitialadmobadcompose

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.interstitialadmobadcompose.ui.theme.InterstitialAdMobAdComposeTheme
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this)

        setContent {
            InterstitialAdMobAdComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Main(this)
                }
            }
        }
    }
}

@Composable
fun Main(context: Context, modifier: Modifier = Modifier) {
    Column (
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var mInterstitialAd: InterstitialAd? = null
        val btnText = remember { mutableStateOf("Loading interstitial Ad") }
        val btnEnable = remember { mutableStateOf(false) }

        fun loadInterstitialAd(context: Context) {
            InterstitialAd.load(context, "ca-app-pub-3940256099942544/1033173712", AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        mInterstitialAd = interstitialAd
                        btnText.value = "Show interstitial Ad"
                        btnEnable.value = true
                    }
                }
            )
        }

        fun showInterstitialAd(context: Context, onAdDismissed: () -> Unit) {
            if (mInterstitialAd != null) {
                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdFailedToShowFullScreenContent(e: AdError) {
                        mInterstitialAd = null
                    }

                    override fun onAdDismissedFullScreenContent() {
                        mInterstitialAd = null

                        loadInterstitialAd(context)
                        onAdDismissed()

                        btnText.value = "Loading interstitial Ad"
                        btnEnable.value = false
                    }
                }
                mInterstitialAd?.show(context as Activity)
            }
        }

        loadInterstitialAd(context)
        val coroutineScope = rememberCoroutineScope()
        Button(
            enabled = btnEnable.value,
            onClick = {
                coroutineScope.launch {
                    showInterstitialAd(context) {
                        Toast.makeText(context, "Interstitial Ad Shown!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        ) {
            Text(text = btnText.value)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    InterstitialAdMobAdComposeTheme {
        Main(LocalContext.current)
    }
}