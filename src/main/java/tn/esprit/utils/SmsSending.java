package tn.esprit.utils;



import com.twilio.type.PhoneNumber;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

public class SmsSending {
    public static final String ACCOUNT_SID = "AC3c3e8fb234c6152eb0a2c93bd365820d";
    public static final String AUTH_TOKEN = "4168a099e2d65f48f2d4b8aa0600abbb";
    private static final String FROM_NUMBER = "+15076827270";



    public static void sendSms( String message) {
        Message sms = Message.creator(
                new PhoneNumber("+216 20460927"),  // To number
                new PhoneNumber(FROM_NUMBER),  // From Twilio number
                message
        ).create();
        System.out.println("Sent message SID: " + sms.getSid());
    }





}
