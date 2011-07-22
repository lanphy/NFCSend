package fr.NFCSend;

import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.util.Locale;

public class NFCSendActivity extends Activity {
    /** Called when the activity is first created. */
	private NfcAdapter mAdapter;
    private TextView mText;
    private NdefMessage mMessage;
    
    public static NdefRecord newTextRecord(String text, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length]; 
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }
    
    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        
        setContentView(R.layout.main);
        
        mText = (TextView) findViewById(R.id.text);
        if (mAdapter != null) {
            mText.setText("Push your NFC message :-)");
        } else {
            mText.setText("This phone is not NFC enabled.");
        }

        // Create an NDEF message with some sample text
        mMessage = new NdefMessage(
                new NdefRecord[] { newTextRecord("My NFC Message !!!!!!", Locale.ENGLISH, true)}); 
        
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) mAdapter.enableForegroundNdefPush(this, mMessage);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) mAdapter.disableForegroundNdefPush(this);
    }
    
}