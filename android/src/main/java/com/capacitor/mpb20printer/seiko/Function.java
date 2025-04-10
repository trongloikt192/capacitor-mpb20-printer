package com.capacitor.mpb20printer.seiko;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.seikoinstruments.sdk.thermalprinter.BarcodeScannerListener;
import com.seikoinstruments.sdk.thermalprinter.CallbackFunctionListener;
import com.seikoinstruments.sdk.thermalprinter.PrinterException;
import com.seikoinstruments.sdk.thermalprinter.PrinterInfo;
import com.seikoinstruments.sdk.thermalprinter.PrinterListener;
import com.seikoinstruments.sdk.thermalprinter.PrinterManager;
import com.seikoinstruments.sdk.thermalprinter.SmartLabelManager;
import com.seikoinstruments.sdk.thermalprinter.printerenum.BarcodeSymbol;
import com.seikoinstruments.sdk.thermalprinter.printerenum.CharacterBold;
import com.seikoinstruments.sdk.thermalprinter.printerenum.CharacterFont;
import com.seikoinstruments.sdk.thermalprinter.printerenum.CharacterReverse;
import com.seikoinstruments.sdk.thermalprinter.printerenum.CharacterScale;
import com.seikoinstruments.sdk.thermalprinter.printerenum.CharacterUnderline;
import com.seikoinstruments.sdk.thermalprinter.printerenum.CuttingMethod;
import com.seikoinstruments.sdk.thermalprinter.printerenum.Direction;
import com.seikoinstruments.sdk.thermalprinter.printerenum.Dithering;
import com.seikoinstruments.sdk.thermalprinter.printerenum.DrawerNum;
import com.seikoinstruments.sdk.thermalprinter.printerenum.ErrorCorrection;
import com.seikoinstruments.sdk.thermalprinter.printerenum.HriPosition;
import com.seikoinstruments.sdk.thermalprinter.printerenum.ImageScale;
import com.seikoinstruments.sdk.thermalprinter.printerenum.LineStyle;
import com.seikoinstruments.sdk.thermalprinter.printerenum.ModuleSize;
import com.seikoinstruments.sdk.thermalprinter.printerenum.PrintAlignment;
import com.seikoinstruments.sdk.thermalprinter.printerenum.PulseWidth;
import com.seikoinstruments.sdk.thermalprinter.printerenum.QrModel;
import com.seikoinstruments.sdk.thermalprinter.printerenum.Rotate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * Class that calls SII Android SDK functions.
 */
public class Function {

    // Offline status(0x80000000) definition
    private final int STATUS_OFFLINE = -2147483648;

    // Offline status(0x00000000) definition (DPU-S printer)
    private final int STATUS_OFFLINE_DPU = 0;
    private PrinterManager mPrinterManager = null;
    private Context mContext = null;
    private boolean  mAllowCallback = false;
    private String mLanguage = "en";
    private String mFormat = "$%d.%d";
    private boolean mSecureConnection = true;

    // Constructor used for printer discovery (DeviceFragment)
    public Function(Context context){
        mContext = context;
        if(mPrinterManager == null){
            mPrinterManager = new PrinterManager(context);
        }
    }

    // Constructor used for other sample operations (MainFragment)
    public Function(Context context, String language ){
        mContext = context;
        mLanguage = language ;
        if(mPrinterManager == null){
            mPrinterManager = new PrinterManager(context);
        }
        if (mLanguage.equals("ja")) {
            mFormat = "¥%d";
        }
    }

    // Sample of device search
    // When searching for a wireless printer, please enable the Bluetooth and Wi-Fi functions on your Android device.
    // If using the display unit independently, add "DSP-A01" to the "Printer Model" and use "startDiscoveryDevice" and "getFoundDevice".
    // When using the display unit independently, connect it to the Android device via USB, and specify "Connection Type" as "USB" and "Printer Model" as "DSP-A01".
    public void searchDeviceSample(PrinterListener printerListener , int selectPort) {
        try {
            switch (selectPort) {
                case  PrinterManager.PRINTER_TYPE_BLUETOOTH:
                    mPrinterManager.startDiscoveryPrinter(printerListener);
                    break;
                case  PrinterManager.PRINTER_TYPE_USB:
                    // When using the printer, please use "startDiscoveryPrinter".
                    // When using the display unit independently via USB connection, please use "startDiscoveryDevice".
                    mPrinterManager.startDiscoveryPrinter(printerListener, PrinterManager.PRINTER_TYPE_USB);
                    //mPrinterManager.startDiscoveryDevice(printerListener, PrinterManager.PRINTER_TYPE_USB);
                    break;
                case  PrinterManager.PRINTER_TYPE_TCP:
                    mPrinterManager.startDiscoveryPrinter(printerListener, 1, 10000);
                    break;
                default:
                    break;
            }
        } catch (PrinterException ex) {
            // Ignore the error.
        }
    }

    public ArrayList<PrinterInfo> getFoundPrinter() {
        // When using the printer, please use "getFoundPrinter".
        // When using the display unit independently, please use "getFoundDevice".
        return mPrinterManager.getFoundPrinter();
        //return mPrinterManager.getFoundDevice();
    }

    // Sample of StatusCallback
    public void statusCallbackSample(CallbackFunctionListener listener) {
        try {
            mPrinterManager.setCallbackFunctionListener(listener);
        } catch (PrinterException ex) {
            // Ignore the error.
        }
    }

    // Sample of BarcodeScannerCallback
    public void barcodeScannerCallbackSample(BarcodeScannerListener listener) {
        try {
            mPrinterManager.setBarcodeScannerListener(listener);
        } catch (PrinterException ex) {
            // Ignore the error.
        }
    }

    // Apply settings to the SDK.
    public void applySettings(int socketKeepingTime, int sendTimeout, int receiveTimeout,
                              int codePage, int internationalChar, int secureConnection){
        mPrinterManager.setSocketKeepingTime(socketKeepingTime);
        mPrinterManager.setSendTimeout(sendTimeout);
        mPrinterManager.setReceiveTimeout(receiveTimeout);
        mPrinterManager.setCodePage(codePage);
        mPrinterManager.setInternationalCharacter(internationalChar);
        if (secureConnection == 0){
            mSecureConnection = true;
        } else {
            mSecureConnection = false;
        }
    }

    // Sample of Connect
    public String connectSample(int selectPort, int selectModel, String selectAddress) {
        try {
            if (selectPort == PrinterManager.PRINTER_TYPE_BLUETOOTH) {
                mPrinterManager.connect(selectModel, selectAddress, mSecureConnection);
            } else if (selectPort == PrinterManager.PRINTER_TYPE_USB) {
                mPrinterManager.connect(selectModel);
            } else {//TCP/IP
                mPrinterManager.connect(selectModel, selectAddress);
            }
            mAllowCallback = true;
            return "Connect Success";
        } catch (PrinterException ex) {
            mAllowCallback = false;
            return "Connect Error: " + ex.getErrorCode();
        }
    }

    // Sample of Disconnect
    public String disconnectSample() {
         try {
            statusCallbackSample(null);
            barcodeScannerCallbackSample(null);
            mPrinterManager.disconnect();
             return "Disconnect Success";
        } catch (PrinterException ex) {
             return "Disconnect Error: " + ex.getErrorCode();
        }
    }

    // Sample of printing in standard mode
    public String standardModePrintSample() {
        String receiptSample;
        int codePage;
        int internationalCharacter;
        String qrData;

        // Holds current character settings.
        int currentCodePage = mPrinterManager.getCodePage();
        int currentInternationalCharacter = mPrinterManager.getInternationalCharacter();

        // Change the print content to match the language of the device.
        // for Japanese
        if (mLanguage.equals("ja")) {
            receiptSample =
                    "--------------------------------\n" +
                            "ｸﾞﾘﾙﾁｷﾝﾌﾞﾚｽﾄ              ¥1,850\n" +
                            "ｻｰﾛｲﾝｽﾃｰｷ                 ¥3,200\n" +
                            "ﾛｰｽﾄﾗﾑ                    ¥2,000\n" +
                            "ｻﾗﾀﾞ                      ¥1,000\n" +
                            "ｸｯｷｰ                        ¥350\n" +
                            "ｸｯｷｰ                        ¥350\n" +
                            "ｱｲｽｸﾘｰﾑ                     ¥500\n" +
                            "ﾁｭｳｶｿﾊﾞ                   ¥1,500\n" +
                            "ｽｷﾔｷ                      ¥3,000\n" +
                            "ｻﾝﾄﾞｲｯﾁ                   ¥1,000\n" +
                            "ﾋﾟｻﾞ                      ¥2,000\n" +
                            "ｺｳﾁｬ                        ¥350\n" +
                            "ｺｰﾋｰ                        ¥350\n\n" +
                            "--------------------------------\n" +
                            "       小計　            ¥17,450\n" +
                            "       消費税               ¥873\n" +
                            "       合計　            ¥18,323\n\n" +
                            "ご利用ありがとうございます\n" +
                            "またのご利用をお待ちしております\n\n";

            // Character set for Japanese
            codePage = PrinterManager.CODE_PAGE_KATAKANA ;
            internationalCharacter = PrinterManager.COUNTRY_JAPAN ;
            // 2-Byte Character Set example
            qrData = "QR サンプル";
        }
        // for English
        else{
            receiptSample =
                    "--------------------------------\n" +
                            "GRILLED CHICKEN BREAST   $ 18.50\n" +
                            "SIRLOIN STEAK            $ 32.00\n" +
                            "ROAST LAMB               $ 20.00\n" +
                            "SALAD                    $ 10.00\n" +
                            "COKE                     $  3.50\n" +
                            "COKE                     $  3.50\n" +
                            "ICE CREAM                $  5.00\n" +
                            "CHINESE NOODLE           $ 15.00\n" +
                            "SUKIYAKI                 $ 30.00\n" +
                            "SANDWICH                 $ 10.00\n" +
                            "PIZZA                    $ 20.00\n" +
                            "TEA                      $  3.50\n" +
                            "COFFEE                   $  3.50\n\n" +
                            "--------------------------------\n" +
                            "        SUBTOTAL        $ 174.50\n" +
                            "        SALES TAX       $   8.73\n" +
                            "        TOTAL           $ 183.23\n\n" +
                            "  Thank you and see you again!  \n";

            // Character set for English
            codePage = PrinterManager.CODE_PAGE_1252;
            internationalCharacter = PrinterManager.COUNTRY_USA;
            // 1-Byte Character Set example
            qrData = "QR Sample";
        }

        // Get the current time
        Date currentDate = new Date();

        // Generate a string in the specified format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = "\nNo.00001     " + dateFormat.format(currentDate) + "\n";

        // Barcode data
        String barcodeData = "012345678901";

        // Generate a buffer to store execution response requests
        int[] printerResponse = new int[1];
        printerResponse[0] = 0x02;//（00h~0Fh）

        try {
            // Check offline
            if(!isOnline()){
                return "StandardModePrintSample Error (OFFLINE)";
            }

            // Character set
            mPrinterManager.setCodePage(codePage);
            mPrinterManager.setInternationalCharacter(internationalCharacter);

            // Distinguish processing based on the printer model
            if(mPrinterManager.getPrinterModel() != PrinterManager.PRINTER_MODEL_DPU_S245
                    && mPrinterManager.getPrinterModel() != PrinterManager.PRINTER_MODEL_DPU_S445){
                // Print Image
                mPrinterManager.sendDataFile(getSampleFilePath("SampleLogo.jpg"), PrintAlignment.ALIGNMENT_CENTER, Dithering.DITHERING_ERRORDIFFUSION);
                // Print Text
                mPrinterManager.sendTextEx(formattedDate, CharacterBold.BOLD_CANCEL, CharacterUnderline.UNDERLINE_CANCEL, CharacterReverse.REVERSE_CANCEL, CharacterFont.FONT_A, CharacterScale.VERTICAL_1_HORIZONTAL_1, PrintAlignment.ALIGNMENT_CENTER);
                mPrinterManager.sendTextEx(receiptSample, CharacterBold.BOLD_CANCEL, CharacterUnderline.UNDERLINE_CANCEL, CharacterReverse.REVERSE_CANCEL, CharacterFont.FONT_A, CharacterScale.VERTICAL_1_HORIZONTAL_1, PrintAlignment.ALIGNMENT_CENTER);
                // Print QR
                mPrinterManager.printQRcode(qrData, ErrorCorrection.QR_ERROR_CORRECTION_M, ModuleSize.QR_MODULE_SIZE_4, PrintAlignment.ALIGNMENT_CENTER, QrModel.QR_MODEL_2);
                // Specify the barcode for EAN13
                mPrinterManager.printBarcode(BarcodeSymbol.BARCODE_SYMBOL_EAN13, barcodeData, ModuleSize.BARCODE_MODULE_WIDTH_3, 80, HriPosition.HRI_POSITION_BELOW, CharacterFont.FONT_A, PrintAlignment.ALIGNMENT_CENTER);
                // Partial cut with pre-feed
                // If using printer does not have cutter, it will only feed paper
                mPrinterManager.cutPaper(CuttingMethod.CUT_PARTIAL);
                // Retrieve execution response requests
                mPrinterManager.getPrinterResponse(PrinterManager.PRINTER_RESPONSE_REQUEST, printerResponse);
                // Confirm print finished
                if (printerResponse[0] != 0x82) { //0x80+0x02
                    return "StandardModePrintSample Error";
                }
            }else{
                // Print Image
                mPrinterManager.sendDataFile(getSampleFilePath("SampleLogo.jpg"), Dithering.DITHERING_ERRORDIFFUSION);
                // Print Text
                mPrinterManager.sendTextEx(formattedDate, CharacterBold.BOLD_CANCEL, CharacterUnderline.UNDERLINE_CANCEL, CharacterFont.FONT_A, CharacterScale.VERTICAL_1_HORIZONTAL_1);
                mPrinterManager.sendTextEx(receiptSample, CharacterBold.BOLD_CANCEL, CharacterUnderline.UNDERLINE_CANCEL, CharacterFont.FONT_A, CharacterScale.VERTICAL_1_HORIZONTAL_1);
                // Print QR
                mPrinterManager.printQRcode(qrData, ErrorCorrection.QR_ERROR_CORRECTION_M, ModuleSize.QR_MODULE_SIZE_4, PrintAlignment.ALIGNMENT_LEFT , QrModel.QR_MODEL_2);
                // Specify the barcode for EAN13
                mPrinterManager.printBarcode(BarcodeSymbol.BARCODE_SYMBOL_EAN13, barcodeData, ModuleSize.BARCODE_MODULE_WIDTH_3, 80, HriPosition.HRI_POSITION_BELOW, CharacterFont.FONT_A, PrintAlignment.ALIGNMENT_LEFT);
                // Retrieve execution response requests
                mPrinterManager.getPrinterResponse(PrinterManager.PRINTER_RESPONSE_REQUEST, printerResponse);
                // Confirm print finished
                if (printerResponse[0] != 0x52) { //0x50+0x02
                    return "StandardModePrintSample Error";
                }
            }

            //// Specify the barcode for CODE93 (Data:12345)
            //// How to binary input.
            //// 1. Input any data from 00H to 2EH. Multiple data can be input
            //// 2. Input 2FH or more lastly as the stop code
            //byte[] data = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x2F };
            //mPrinterManager.printBarcode(BarcodeSymbol.BARCODE_SYMBOL_CODE93, data, ModuleSize.BARCODE_MODULE_WIDTH_3, 80, HriPosition.HRI_POSITION_BELOW, CharacterFont.FONT_A, PrintAlignment.ALIGNMENT_CENTER);

            // Finally, revert to user-specified character settings.
            mPrinterManager.setCodePage(currentCodePage);
            mPrinterManager.setInternationalCharacter(currentInternationalCharacter);

            return "StandardModePrintSample Success";
        }
        catch (PrinterException ex)
        {
            return "StandardModePrintSample Error : " + ex.getErrorCode();
        }
    }

    // Sample of printing in page mode
    public String pageModePrintSample() {
        try {
            // Check offline
            if(!isOnline()){
                return "PageModePrintSample Error (OFFLINE)";
            }

            // Holds current character settings.
            int currentCodePage = mPrinterManager.getCodePage();
            int currentInternationalCharacter = mPrinterManager.getInternationalCharacter();

            // Character set for English
            mPrinterManager.setCodePage(PrinterManager.CODE_PAGE_1252);
            mPrinterManager.setInternationalCharacter(PrinterManager.COUNTRY_USA);

            //// Character set for Japanese
            //mPrinterManager.setCodePage(PrinterManager.CODE_PAGE_KATAKANA);
            //mPrinterManager.setInternationalCharacter(PrinterManager.COUNTRY_JAPAN);

            // Start page mode
            mPrinterManager.enterPageMode();

            // Specify the print area in page mode
            // The number of printable dots in one dot line has to be set to 355 dots for this sample code.
            mPrinterManager.setPageModeArea(0, 0, 355, 576);

            // Specify the rectangle and the rule
            mPrinterManager.printPageModeRectangle(0, 0, 344, 575, LineStyle.LINESTYLE_THIN);
            mPrinterManager.printPageModeRectangle(7, 7, 336, 567, LineStyle.LINESTYLE_THIN);
            mPrinterManager.printPageModeLine(11, 404, 334, 404, LineStyle.LINESTYLE_THIN);

            // Specify the print direction in page mode
            mPrinterManager.setPageModeDirection(Direction.DIRECTION_TOP_TO_BOTTOM);

            // Specify the text
            mPrinterManager.printPageModeText(21, 47, "No.123456789");
            mPrinterManager.printPageModeText(212, 340, "Date 2023-01-01");

            // Specify the image file
            String imagePath = getSampleFilePath("TicketImage.jpg");
            mPrinterManager.printPageModeImageFile(10, 222, imagePath, Dithering.DITHERING_DISABLE);

            // Specify the print area in page mode
            mPrinterManager.setPageModeArea(0, 404, 345, 163);

            // Specify the direction of the print
            mPrinterManager.setPageModeDirection(Direction.DIRECTION_LEFT_TO_RIGHT);

            // Specify the barcode for CODE128
            mPrinterManager.printPageModeBarcode(
                    20,
                    132,
                    BarcodeSymbol.BARCODE_SYMBOL_CODE128,
                    new byte[]{0x67, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x68},
                    ModuleSize.BARCODE_MODULE_WIDTH_2,
                    80,
                    HriPosition.HRI_POSITION_ABOVE,
                    CharacterFont.FONT_A
            );

            //// Specify the barcode for CODE128 (Data:123456789)
            //// How to input using the CODE128 Code Set table
            //// 1. Input the start code of 67H(Code Set A), 68H(Code Set B), or 69H(Code Set C) shown in the table of CODE128 Code Set
            //// 2. Input any data from 00H to 66H. Multiple data can be input
            //// 3. Input 67H or more lastly as the stop code
            ////byte[] data = new byte[] { 0x68, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x67 };

            //// How to input using the CODE128 Special Code Set table
            //// 1. Input the start code (START A, START B, or START C) of the code set to be selected shown in the table of CODE128 Special Code
            //// 2. Input the data in the respective formats
            ////       Code Set A : Data from 00H to 5FH can be input
            ////       Code Set B : Data from 20H to 7FH can be input
            ////       Code Set C : Data from 00H(00) to 63H(99) can be input
            //byte[] data = new byte[] { 0x7B, 0x42, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39 };
            //mPrinterManager.printPageModeBarcode(10, 132, BarcodeSymbol.BARCODE_SYMBOL_CODE128, data, ModuleSize.BARCODE_MODULE_WIDTH_2, 80, HriPosition.HRI_POSITION_ABOVE, CharacterFont.FONT_A);

            // Print the page mode and partial cut with pre-feed
            // If using printer does not have cutter, it will only feed paper
            mPrinterManager.printPageMode(CuttingMethod.CUT_PARTIAL);
            // Confirm print finished
            int[] printerResponse = new int[1];
            mPrinterManager.getPrinterResponse(PrinterManager.PRINTER_RESPONSE_REQUEST, printerResponse);
            if (printerResponse[0] != 0x80)
            {
                return "PageModePrintSample Error";
            }

            // Finally, revert to user-specified character settings.
            mPrinterManager.setCodePage(currentCodePage);
            mPrinterManager.setInternationalCharacter(currentInternationalCharacter);

            return "PageModePrintSample Success";
        } catch (PrinterException ex) {
            return "PageModePrintSample Error: " + ex.getErrorCode();
        } finally {
            try {
                // Exit page mode
                mPrinterManager.exitPageMode();
            }catch(PrinterException ex){
                // Ignore the error.
            }
        }
    }

    // Sample of printing smart label
    public String smartLabelSample() {
        try {
            // Check offline
            if(!isOnline()){
                return "SmartLabelSample Error (OFFLINE)";
            }

            // Create smart label instance.
            SmartLabelManager smartLabelManager = new SmartLabelManager(mContext);

            // Specify a label file(*.sl) to print or replace data.
            // Sample.sl is defined following objects.
            //  * Text    Object (ID = 1) : "Jane Smith"
            //  * Image   Object (ID = 1) : woman.jpg
            //  * Barcode Object (ID = 1) : "0123456" (Code128)
            // Label Type of Sample.sl is User Paper[ Printer:MP-B21L, PaperType:Receipt, PositionDetection:None, PaperWidth58mm, PaperLength:300mm ].
            String labelFilePath = getSampleFilePath("Sample.sl");
            smartLabelManager.selectSmartLabelFile(labelFilePath);

            // Print Smart Label image
            mPrinterManager.printSmartLabelImageData(smartLabelManager);

            // Replace the text data with "John Smith".
            smartLabelManager.replaceSmartLabelTextData(1, "John Smith");

            // Replace the image data with "man.jpg".
            // Get image data for replacement
            String imageFilePath = getSampleFilePath("man.jpg");
            Bitmap imageObject =
                    BitmapFactory.decodeStream(new BufferedInputStream( new FileInputStream(new File(imageFilePath))));
            smartLabelManager.replaceSmartLabelImageData(1, imageObject);

            // Replace the barcode data with "ABCDEFG".
            smartLabelManager.replaceSmartLabelBarcodeData(1, "ABCDEFG");

            // Print Smart Label image
            mPrinterManager.printSmartLabelImageData(smartLabelManager);

            // Full cut with pre-feed.
            mPrinterManager.cutPaper(CuttingMethod.CUT_FULL);

            // Confirm print finished
            int[] printerResponse = new int[1];
            mPrinterManager.getPrinterResponse(PrinterManager.PRINTER_RESPONSE_REQUEST, printerResponse);
            if (printerResponse[0] != 0x80)
            {
                return "SmartLabelSample Error";
            }
            return "SmartLabelSample Success";
        } catch (PrinterException ex) {
            return "SmartLabelSample Error: " + ex.getErrorCode();
        } catch (FileNotFoundException e) {
            return "SmartLabelSample Error: ";
        }
    }

    // Sample of printing a file
    public String sendDataFileSample(String filePath) {
        try {
            // Check offline
            if(!isOnline()){
                return "SendDataFileSample Error (OFFLINE)";
            }
            mPrinterManager.setSendTimeout(5000) ;
            mPrinterManager.setReceiveTimeout(5000) ;
            // Specifies the print or data file.
            mPrinterManager.sendDataFile(filePath,PrintAlignment.ALIGNMENT_LEFT, Dithering.DITHERING_ERRORDIFFUSION);
            // Confirm print finished
            int[] printerResponse = new int[1];
            int response;
            mPrinterManager.getPrinterResponse(PrinterManager.PRINTER_RESPONSE_REQUEST, printerResponse);
            if(mPrinterManager.getPrinterModel() != PrinterManager.PRINTER_MODEL_DPU_S245
                    && mPrinterManager.getPrinterModel() != PrinterManager.PRINTER_MODEL_DPU_S445){
                response = 0x80;
            }else{
                response = 0x50;
            }
            if (printerResponse[0] != response)
            {
                return "SendDataFileSample Error";
            }
            return "SendDataFileSample Success";
        } catch (PrinterException ex) {
            return "SendDataFileSample Error: " + ex.getErrorCode();
        }
    }

    // Sample of printing a PDF file
    public String printPdfSample(String filePath) {
        try {
            // Check offline
            if(!isOnline()){
                return "PrintPdfSample Error (OFFLINE)";
            }
            // Specifies the print PDF file.
            // To print all pages of a PDF file, set “startIndex” to -1.
            mPrinterManager.printPDF(filePath,-1,-1, Rotate.ROTATE_NONE, ImageScale.IMAGE_SCALE_WIDTH_FIT,-1,Dithering.DITHERING_ERRORDIFFUSION,PrintAlignment.ALIGNMENT_CENTER);

            // To print any page of a PDF file
            // Example: To print pages 2 through 5, specify 2 for “startIndex” and 5 for “endIndex”.
            //mPrinterManager.printPDF(filePath,2,5, Rotate.ROTATE_NONE, ImageScale.IMAGE_SCALE_WIDTH_FIT,-1,Dithering.DITHERING_ERRORDIFFUSION,PrintAlignment.ALIGNMENT_CENTER);

            // Confirm print finished
            int[] printerResponse = new int[1];
            int response = 0x80;
            mPrinterManager.getPrinterResponse(PrinterManager.PRINTER_RESPONSE_REQUEST, printerResponse);
            if (printerResponse[0] != response)
            {
                return "PrintPdfSample Error";
            }
            return "PrintPdfSample Success";
        } catch (PrinterException ex) {
            return "PrintPdfSample Error: " + ex.getErrorCode();
        }
    }

    // Sample of Open Drawer
    public String openDrawerSample() {
        try {
            // Check offline
            if(!isOnline()){
                return "OpenDrawerSample Error (OFFLINE)";
            }
            // Specify the drawer you want to open.
            mPrinterManager.openDrawer(DrawerNum.DRAWER_1, PulseWidth.ON_OFF_TIME_100);
            // Confirm print finished
            int[] printerResponse = new int[1];
            mPrinterManager.getPrinterResponse(PrinterManager.PRINTER_RESPONSE_REQUEST, printerResponse);
            if (printerResponse[0] != 0x80)
            {
                return "OpenDrawerSample Error";
            }
            return "OpenDrawerSample Success";
        } catch (PrinterException ex) {
            return "OpenDrawerSample Error: " + ex.getErrorCode();
        }
    }

    // Get sdk version
    public String getSdkVersion() {
        return mPrinterManager.getVersion() ;
    }

    // Sample using object selection and template showing #1
    public String beginPaymentSample(int total) {
        String strTotal;
        try {
            // Check offline
            if(!isOnline()){
                return "DisplaySample Error (OFFLINE)";
            }

            if (mLanguage.equals("ja")) {
                // Character set for Japanese
                mPrinterManager.setCodePage(PrinterManager.CODE_PAGE_KATAKANA);
                mPrinterManager.setInternationalCharacter(PrinterManager.COUNTRY_JAPAN);

                strTotal = String.format(mFormat, total);
                mPrinterManager.selectTemplate(120, 80);     // Select template (ID=120 : for List Up(Jp)) with Slide 80
                mPrinterManager.selectTemplateTextObject(0);   // Select Text area 0
                mPrinterManager.setTemplateTextData("合計");
            }else {
                // Character set for English
                mPrinterManager.setCodePage(PrinterManager.CODE_PAGE_1252);
                mPrinterManager.setInternationalCharacter(PrinterManager.COUNTRY_USA);

                strTotal = String.format(mFormat, total / 100, total % 100);
                mPrinterManager.selectTemplate(119, 80);     // Select template (ID=119 : for List Up(En)) with Slide 80
                mPrinterManager.selectTemplateTextObject(0);   // Select Text area 0
                mPrinterManager.setTemplateTextData("Total");
            }
            mPrinterManager.selectTemplateTextObject(1);   // Select Text area 1
            mPrinterManager.setTemplateTextData(strTotal);
            mPrinterManager.showTemplate(0);              // Display
            return ""; // Returns an empty string if no error occurs
        }
        catch (PrinterException ex)
        {
            return "DisplaySample Error : " + ex.getErrorCode();
        }
    }

    // Sample using object selection and template showing #2
    public String registerItemSample(int subTotal, String itemName, int price) {
        String strSubTotal;
        String strPrice;
        if (mLanguage.equals("ja")) {
            strSubTotal = String.format(mFormat, subTotal);
            strPrice = String.format(mFormat, price);
        }else{
            strSubTotal = String.format(mFormat, subTotal / 100, subTotal % 100);
            strPrice = String.format(mFormat, price / 100, price % 100);
        }

        try
        {
            mPrinterManager.selectTemplateTextObject(1);      // Select Text area 1
            mPrinterManager.setTemplateTextData(strSubTotal);
            mPrinterManager.selectTemplateTextObject(6);      // Select Text area 6
            mPrinterManager.setTemplateTextData(itemName);
            mPrinterManager.selectTemplateTextObject(7);      // Select Text area 7
            mPrinterManager.setTemplateTextData(strPrice);
            mPrinterManager.showTemplate(0);                  // Display
            return ""; // Returns an empty string if no error occurs
        }
        catch (PrinterException ex)
        {
            return "DisplaySample Error : " + ex.getErrorCode();
        }
    }

    // Sample using object selection and template showing #3
    public String finishPaymentSample(int total, int paid, int change) {
        String strTotal ;//= String.format(mFormat, total / 100, total % 100);
        String strPaid ;//= String.format(mFormat, paid / 100, paid % 100);
        String strChange ;//= String.format(mFormat, change / 100, change % 100);

        try
        {
            if (mLanguage.equals("ja")) {
                strTotal = String.format(mFormat, total);
                strPaid = String.format(mFormat, paid);
                strChange = String.format(mFormat, change);
                mPrinterManager.selectTemplate(124, 83);    // Select template (ID=124 : for 3 lines(Jp)) with Slide 83
            }else{
                strTotal = String.format(mFormat, total / 100, total % 100);
                strPaid = String.format(mFormat, paid / 100, paid % 100);
                strChange = String.format(mFormat, change / 100, change % 100);
                mPrinterManager.selectTemplate(123, 82);    // Select template (ID=123 : for 3 lines(En)) with Slide 82
            }
            mPrinterManager.selectTemplateTextObject(3);    // Select Text area 3
            mPrinterManager.setTemplateTextData(strTotal);
            mPrinterManager.selectTemplateTextObject(4);    // Select Text area 4
            mPrinterManager.setTemplateTextData(strPaid);
            mPrinterManager.selectTemplateTextObject(5);    // Select Text area 5
            mPrinterManager.setTemplateTextData(strChange);

            mPrinterManager.showTemplate(0);               // Display
            return ""; // Returns an empty string if no error occurs
        }
        catch (PrinterException ex)
        {
            return "DisplaySample Error : " + ex.getErrorCode();
        }
    }

    // Sample of putting the display on standby
    public String standbySample() {
        try
        {
            mPrinterManager.enterStandbyMode();
            return ""; // Returns an empty string if no error occurs
        }
        catch (PrinterException ex)
        {
            return "DisplaySample Error : " + ex.getErrorCode();
        }
    }

    // Gets the path of the specified file
    private String getSampleFilePath(String fileName) {
        String path = Objects.requireNonNull(mContext.getExternalFilesDir(null)).getAbsolutePath();
        File file = new File(path, fileName);
        if (!file.exists()) {
            try {
                InputStream in = mContext.getAssets().open(fileName);
                FileOutputStream out = new FileOutputStream(file.getPath());
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return file.getPath();
    }

    // Get PrinterManager
    public PrinterManager getPrinterManager(){
        return mPrinterManager;
    }

    // Get SecureConnection Type
    public boolean getSecureConnection(){
        return mSecureConnection;
    }

    public boolean isAllowCallback(){
        return mAllowCallback;
    }

    private boolean isOnline(){
        // Check offline
        int[] status = new int[1];
        int offlineStatus;
        try
        {
            mPrinterManager.getStatus(status);
        }
        catch (PrinterException ex)
        {
            return false;
        }

        if(mPrinterManager.getPrinterModel() != PrinterManager.PRINTER_MODEL_DPU_S245
                && mPrinterManager.getPrinterModel() != PrinterManager.PRINTER_MODEL_DPU_S445){
            offlineStatus = STATUS_OFFLINE;
        }else{
            offlineStatus = STATUS_OFFLINE_DPU ;
        }

        if (status[0] == offlineStatus)
        {
            return false;
        }
        return true;
    }
}
