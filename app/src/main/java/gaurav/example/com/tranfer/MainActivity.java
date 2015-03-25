package gaurav.example.com.tranfer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;

import android.support.v7.app.ActionBarActivity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends ActionBarActivity {


    TextView textInfo;
    TextView textSearchedEndpoint;
    ToggleButton tg1,tg2,tg3,tg4,tg5,tg6,tg7,tg8,tg9,tg10;
    TextView textDeviceName;
    TextView textStatus;

    private static final int targetVendorID = 2341; //Arduino Uno
    private static final int targetProductID = 42; //Arduino Uno, not 0067
    UsbDevice deviceFound = null;
    UsbInterface usbInterfaceFound = null;
    UsbEndpoint endpointIn = null;
    UsbEndpoint endpointOut = null;

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    PendingIntent mPermissionIntent;

    UsbInterface usbInterface;
    UsbDeviceConnection usbDeviceConnection;

    EditText textOut;
    Button buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textStatus = (TextView) findViewById(R.id.textstatus);

        textDeviceName = (TextView) findViewById(R.id.textdevicename);
        textInfo = (TextView) findViewById(R.id.info);
        textSearchedEndpoint = (TextView) findViewById(R.id.searchedendpoint);
        tg1=(ToggleButton)findViewById(R.id.tg1);
        tg2=(ToggleButton)findViewById(R.id.tg2);
        tg3=(ToggleButton)findViewById(R.id.tg3);
        tg4=(ToggleButton)findViewById(R.id.tg4);
        tg5=(ToggleButton)findViewById(R.id.tg5);
        tg6=(ToggleButton)findViewById(R.id.tg6);
        tg7=(ToggleButton)findViewById(R.id.tg7);
        tg8=(ToggleButton)findViewById(R.id.tg8);
        tg9=(ToggleButton)findViewById(R.id.tg9);
        tg10=(ToggleButton)findViewById(R.id.tg10);



        // register the broadcast receiver
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);

        registerReceiver(mUsbDeviceReceiver, new IntentFilter(
                UsbManager.ACTION_USB_DEVICE_ATTACHED));
        registerReceiver(mUsbDeviceReceiver, new IntentFilter(
                UsbManager.ACTION_USB_DEVICE_DETACHED));

        connectUsb();

        textOut = (EditText)findViewById(R.id.textout);
        buttonSend = (Button)findViewById(R.id.send);
        buttonSend.setOnClickListener(buttonSendOnClickListener);
    }

    OnClickListener buttonSendOnClickListener =
            new OnClickListener(){

                @Override
                public void onClick(View v) {

                    if(deviceFound != null){

                        int arr[]=new int[30];

                        if(tg1.isChecked()){
                            arr[0]=255;
                            arr[1]=0;
                            arr[2]=0;
                        }
                        if(tg2.isChecked()==true){
                            arr[3]=255;
                            arr[4]=0;
                            arr[5]=0;
                        }
                        if(tg3.isChecked()==true){
                            arr[6]=255;
                            arr[7]=0;
                            arr[8]=0;
                        }
                        if(tg1.isChecked()==true){
                            arr[9]=255;
                            arr[10]=0;
                            arr[11]=0;
                        }
                        if(tg1.isChecked()==true){
                            arr[12]=255;
                            arr[13]=0;
                            arr[14]=0;
                        }
                        if(tg1.isChecked()==true){
                            arr[15]=255;
                            arr[16]=0;
                            arr[17]=0;
                        }
                        if(tg1.isChecked()==true){
                            arr[18]=255;
                            arr[19]=0;
                            arr[20]=0;
                        }
                        if(tg1.isChecked()==true){
                            arr[21]=255;
                            arr[22]=0;
                            arr[23]=0;
                        }
                        if(tg1.isChecked()==true){
                            arr[24]=255;
                            arr[25]=0;
                            arr[26]=0;
                        }
                        if(tg1.isChecked()==true){
                            arr[27]=255;
                            arr[28]=0;
                            arr[29]=0;
                        }

                        //String tOut = textOut.getText().toString();
                        //byte[] bytesOut = tOut.getBytes(); //convert String to byte[]
                        ByteBuffer byteBuffer = ByteBuffer.allocate(arr.length * 4);
                        IntBuffer intBuffer = byteBuffer.asIntBuffer();
                        intBuffer.put(arr);
                        byte[] bytesOut=byteBuffer.array();
                        int usbResult = usbDeviceConnection.bulkTransfer(
                                endpointOut, bytesOut, bytesOut.length, 0);

                    }else{
                        Toast.makeText(MainActivity.this,
                                "deviceFound == null",
                                Toast.LENGTH_LONG).show();
                    }


                }};

    @Override
    protected void onDestroy() {
        releaseUsb();
        unregisterReceiver(mUsbReceiver);
        unregisterReceiver(mUsbDeviceReceiver);
        super.onDestroy();
    }

    private void connectUsb() {

        Toast.makeText(MainActivity.this, "connectUsb()", Toast.LENGTH_LONG)
                .show();
        textStatus.setText("connectUsb()");

        searchEndPoint();

        if (usbInterfaceFound != null) {
            setupUsbComm();
        }

    }

    private void releaseUsb() {

        Toast.makeText(MainActivity.this, "releaseUsb()", Toast.LENGTH_LONG)
                .show();
        textStatus.setText("releaseUsb()");

        if (usbDeviceConnection != null) {
            if (usbInterface != null) {
                usbDeviceConnection.releaseInterface(usbInterface);
                usbInterface = null;
            }
            usbDeviceConnection.close();
            usbDeviceConnection = null;
        }

        deviceFound = null;
        usbInterfaceFound = null;
        endpointIn = null;
        endpointOut = null;
    }

    private void searchEndPoint() {

        textInfo.setText("");
        textSearchedEndpoint.setText("");

        usbInterfaceFound = null;
        endpointOut = null;
        endpointIn = null;

        // Search device for targetVendorID and targetProductID
        if (deviceFound == null) {
            UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
            HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();

                if (device.getVendorId() == targetVendorID) {
                    if (device.getProductId() == targetProductID) {
                        deviceFound = device;
                    }
                }
            }
        }

        if (deviceFound == null) {
            Toast.makeText(MainActivity.this, "device not found",
                    Toast.LENGTH_LONG).show();
            textStatus.setText("device not found");
        } else {
            String s = deviceFound.toString() + "\n" + "DeviceID: "
                    + deviceFound.getDeviceId() + "\n" + "DeviceName: "
                    + deviceFound.getDeviceName() + "\n" + "DeviceClass: "
                    + deviceFound.getDeviceClass() + "\n" + "DeviceSubClass: "
                    + deviceFound.getDeviceSubclass() + "\n" + "VendorID: "
                    + deviceFound.getVendorId() + "\n" + "ProductID: "
                    + deviceFound.getProductId() + "\n" + "InterfaceCount: "
                    + deviceFound.getInterfaceCount();
            textInfo.setText(s);

            // Search for UsbInterface with Endpoint of USB_ENDPOINT_XFER_BULK,
            // and direction USB_DIR_OUT and USB_DIR_IN

            for (int i = 0; i < deviceFound.getInterfaceCount(); i++) {
                UsbInterface usbif = deviceFound.getInterface(i);

                UsbEndpoint tOut = null;
                UsbEndpoint tIn = null;

                int tEndpointCnt = usbif.getEndpointCount();
                if (tEndpointCnt >= 2) {
                    for (int j = 0; j < tEndpointCnt; j++) {
                        if (usbif.getEndpoint(j).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                            if (usbif.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_OUT) {
                                tOut = usbif.getEndpoint(j);
                            } else if (usbif.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_IN) {
                                tIn = usbif.getEndpoint(j);
                            }
                        }
                    }

                    if (tOut != null && tIn != null) {
                        // This interface have both USB_DIR_OUT
                        // and USB_DIR_IN of USB_ENDPOINT_XFER_BULK
                        usbInterfaceFound = usbif;
                        endpointOut = tOut;
                        endpointIn = tIn;
                    }
                }

            }

            if (usbInterfaceFound == null) {
                textSearchedEndpoint.setText("No suitable interface found!");
            } else {
                textSearchedEndpoint.setText("UsbInterface found: "
                        + usbInterfaceFound.toString() + "\n\n"
                        + "Endpoint OUT: " + endpointOut.toString() + "\n\n"
                        + "Endpoint IN: " + endpointIn.toString());
            }
        }
    }

    private boolean setupUsbComm() {

        // for more info, search SET_LINE_CODING and
        // SET_CONTROL_LINE_STATE in the document:
        // "Universal Serial Bus Class Definitions for Communication Devices"
        // at http://adf.ly/dppFt
        final int RQSID_SET_LINE_CODING = 0x20;
        final int RQSID_SET_CONTROL_LINE_STATE = 0x22;

        boolean success = false;

        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        Boolean permitToRead = manager.hasPermission(deviceFound);

        if (permitToRead) {
            usbDeviceConnection = manager.openDevice(deviceFound);
            if (usbDeviceConnection != null) {
                usbDeviceConnection.claimInterface(usbInterfaceFound, true);

                int usbResult;
                usbResult = usbDeviceConnection.controlTransfer(0x21, // requestType
                        RQSID_SET_CONTROL_LINE_STATE, // SET_CONTROL_LINE_STATE
                        0, // value
                        0, // index
                        null, // buffer
                        0, // length
                        0); // timeout

                /*Toast.makeText(
                        MainActivity.this,
                        "controlTransfer(SET_CONTROL_LINE_STATE): " + usbResult,
                        Toast.LENGTH_LONG).show();*/

                // baud rate = 9600
                // 8 data bit
                // 1 stop bit
                byte[] encodingSetting = new byte[] { (byte) 0x80, 0x25, 0x00,
                        0x00, 0x00, 0x00, 0x08 };
                usbResult = usbDeviceConnection.controlTransfer(0x21, // requestType
                        RQSID_SET_LINE_CODING, // SET_LINE_CODING
                        0, // value
                        0, // index
                        encodingSetting, // buffer
                        7, // length
                        0); // timeout
               /* Toast.makeText(MainActivity.this,
                        "controlTransfer(RQSID_SET_LINE_CODING): " + usbResult,
                        Toast.LENGTH_LONG).show();*/

    /*
    byte[] bytesHello = new byte[] { (byte) 'H', 'e', 'l', 'l',
      'o', ' ', 'f', 'r', 'o', 'm', ' ', 'A', 'n', 'd', 'r',
      'o', 'i', 'd' };
    usbResult = usbDeviceConnection.bulkTransfer(endpointOut,
      bytesHello, bytesHello.length, 0);
    Toast.makeText(MainActivity.this, "bulkTransfer: " + usbResult,
      Toast.LENGTH_LONG).show();
    */
            }

        } else {
            manager.requestPermission(deviceFound, mPermissionIntent);
            Toast.makeText(MainActivity.this, "Permission: " + permitToRead,
                    Toast.LENGTH_LONG).show();
            textStatus.setText("Permission: " + permitToRead);
        }

        return success;
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {

                Toast.makeText(MainActivity.this, "ACTION_USB_PERMISSION",
                        Toast.LENGTH_LONG).show();
                textStatus.setText("ACTION_USB_PERMISSION");

                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            connectUsb();
                        }
                    } else {
                        Toast.makeText(MainActivity.this,
                                "permission denied for device " + device,
                                Toast.LENGTH_LONG).show();
                        textStatus.setText("permission denied for device "
                                + device);
                    }
                }
            }
        }
    };

    private final BroadcastReceiver mUsbDeviceReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {

                deviceFound = (UsbDevice) intent
                        .getParcelableExtra(UsbManager.EXTRA_DEVICE);
               /* Toast.makeText(
                        MainActivity.this,
                        "ACTION_USB_DEVICE_ATTACHED: \n"
                                + deviceFound.toString(), Toast.LENGTH_LONG)
                        .show();*/
                textStatus.setText("ACTION_USB_DEVICE_ATTACHED: \n"
                        + deviceFound.toString());

                connectUsb();

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {

                UsbDevice device = (UsbDevice) intent
                        .getParcelableExtra(UsbManager.EXTRA_DEVICE);

                /*Toast.makeText(MainActivity.this,
                        "ACTION_USB_DEVICE_DETACHED: \n" + device.toString(),
                        Toast.LENGTH_LONG).show();*/
                textStatus.setText("ACTION_USB_DEVICE_DETACHED: \n"
                        + device.toString());

                if (device != null) {
                    if (device == deviceFound) {
                        releaseUsb();
                    }else{
                        Toast.makeText(MainActivity.this,
                                "device == deviceFound, no call releaseUsb()\n" +
                                        device.toString() + "\n" +
                                        deviceFound.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this,
                            "device == null, no call releaseUsb()", Toast.LENGTH_LONG).show();
                }

                textInfo.setText("");
            }
        }

    };

}