package com.raju.railassistant;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class WebAppInterface {
    private final Context context;
    private final WebView webView;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech tts;
    private boolean ttsReady = false;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final SharedPreferences prefs;

    public WebAppInterface(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
        this.prefs = context.getSharedPreferences("RailAssistantPrefs", Context.MODE_PRIVATE);
        initTTS();
    }

    private void initTTS() {
        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true;
                tts.setLanguage(new Locale("hi", "IN"));
            }
        });
    }

    @JavascriptInterface
    public void requestMicPermission() {
        mainHandler.post(() -> {
            if (context instanceof Activity) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, 101);
            }
        });
    }

    @JavascriptInterface
    public void startListening() {
        startVoiceRecognition("hi");
    }

    @JavascriptInterface
    public void startListening(String languageCode) {
        startVoiceRecognition(languageCode);
    }

    @JavascriptInterface
    public void stopListening() {
        stopVoiceRecognition();
    }

    @JavascriptInterface
    public void startVoiceRecognition(String languageCode) {
        mainHandler.post(() -> {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                if (context instanceof Activity) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, 101);
                }
                callJs("if(window.onVoiceError)window.onVoiceError(9);if(window.onNativeSpeechError)window.onNativeSpeechError(9);");
                return;
            }

            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                Toast.makeText(context, "Speech Recognition is not available on this device", Toast.LENGTH_SHORT).show();
                callJs("if(window.onVoiceError)window.onVoiceError('not_available');if(window.onNativeSpeechError)window.onNativeSpeechError('not_available');");
                return;
            }

            if (speechRecognizer != null) {
                try {
                    speechRecognizer.cancel();
                    speechRecognizer.destroy();
                } catch (Exception ignored) {}
            }

            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            
            String lang = (languageCode != null && languageCode.equalsIgnoreCase("en")) ? "en-IN" : "hi-IN";
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, lang);
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    callJs("if(window.onVoiceReady)window.onVoiceReady();if(window.onNativeSpeechReady)window.onNativeSpeechReady();");
                }

                @Override
                public void onBeginningOfSpeech() {
                    callJs("if(window.onVoiceBegin)window.onVoiceBegin();if(window.onNativeSpeechBegin)window.onNativeSpeechBegin();");
                }

                @Override
                public void onRmsChanged(float rmsdB) {
                    callJs("if(window.onNativeAudioLevel)window.onNativeAudioLevel(" + rmsdB + ");");
                }

                @Override
                public void onBufferReceived(byte[] buffer) {}

                @Override
                public void onEndOfSpeech() {
                    callJs("if(window.onVoiceEnd)window.onVoiceEnd();if(window.onNativeSpeechEnd)window.onNativeSpeechEnd();");
                }

                @Override
                public void onError(int error) {
                    callJs("if(window.onVoiceError)window.onVoiceError(" + error + ");if(window.onNativeSpeechError)window.onNativeSpeechError(" + error + ");");
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        String recognizedText = matches.get(0).replace("\\", "\\\\").replace("'", "\\'").replace("\n", " ");
                        callJs("if(window.onVoiceResult)window.onVoiceResult('" + recognizedText + "');if(window.onNativeSpeechResult)window.onNativeSpeechResult('" + recognizedText + "');");
                    } else {
                        callJs("if(window.onVoiceError)window.onVoiceError('empty');if(window.onNativeSpeechError)window.onNativeSpeechError('empty');");
                    }
                }

                @Override
                public void onPartialResults(Bundle partialResults) {
                    ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        String partial = matches.get(0).replace("\\", "\\\\").replace("'", "\\'").replace("\n", " ");
                        callJs("if(window.onVoicePartialResult)window.onVoicePartialResult('" + partial + "');");
                    }
                }

                @Override
                public void onEvent(int eventType, Bundle params) {}
            });

            speechRecognizer.startListening(intent);
        });
    }

    @JavascriptInterface
    public void stopVoiceRecognition() {
        mainHandler.post(() -> {
            if (speechRecognizer != null) {
                try {
                    speechRecognizer.stopListening();
                } catch (Exception ignored) {}
            }
        });
    }

    @JavascriptInterface
    public void speak(String text, String languageCode) {
        mainHandler.post(() -> {
            if (tts != null && ttsReady && text != null && !text.trim().isEmpty()) {
                Locale loc = (languageCode != null && languageCode.equalsIgnoreCase("en")) 
                    ? Locale.ENGLISH : new Locale("hi", "IN");
                tts.setLanguage(loc);
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "RAIL_TTS");
            }
        });
    }

    @JavascriptInterface
    public void stopSpeaking() {
        mainHandler.post(() -> {
            if (tts != null) {
                try {
                    tts.stop();
                } catch (Exception ignored) {}
            }
        });
    }

    @JavascriptInterface
    public void showToast(String message) {
        mainHandler.post(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }

    @JavascriptInterface
    public void saveToken(String token) {
        prefs.edit().putString("railradar_token", token).apply();
    }

    @JavascriptInterface
    public String getToken() {
        return prefs.getString("railradar_token", "");
    }

    private void callJs(String script) {
        mainHandler.post(() -> {
            if (webView != null) {
                webView.evaluateJavascript("javascript:" + script, null);
            }
        });
    }

    public void destroy() {
        if (speechRecognizer != null) {
            try { speechRecognizer.destroy(); } catch (Exception ignored) {}
        }
        if (tts != null) {
            try {
                tts.stop();
                tts.shutdown();
            } catch (Exception ignored) {}
        }
    }
}
