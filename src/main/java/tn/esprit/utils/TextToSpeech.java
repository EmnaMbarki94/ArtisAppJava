package tn.esprit.utils;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class TextToSpeech {

    private static final String API_KEY = "261dcdb778f44e0e828d6500bfdbbdef";
    private static Clip clip = null;
    private static AudioInputStream audioStream = null;
    private static InputStream inputStream = null;

    public static void speak(String text, String languageCode) {
        try {
            String encodedText = java.net.URLEncoder.encode(text, "UTF-8");
            String requestUrl = "https://api.voicerss.org/?key=" + API_KEY +
                    "&hl=" + languageCode +
                    "&src=" + encodedText;

            HttpURLConnection conn = (HttpURLConnection) new URL(requestUrl).openConnection();
            conn.setRequestMethod("GET");

            inputStream  = conn.getInputStream();
            audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream));
            //BufferedInputStream bis = new BufferedInputStream(audioStream);
            //ais = AudioSystem.getAudioInputStream(bis);

            new Thread(() -> {
                try {

                    clip = AudioSystem.getClip();
                    clip.open(audioStream);
                    clip.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public static void pause(){
        if (clip != null) {
            clip.stop();
        }
    }

}