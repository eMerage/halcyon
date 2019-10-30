package com.halcyon.channelbridgews;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.halcyon.Entity.CreditPeriod;
import com.halcyon.Entity.DealerSaleEntity;
import com.halcyon.Entity.ReturnHeaderEntity;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WebService {

    public final String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";



    //Live
//   public final String SOAP_ADDRESS = "http://halcyonsfa.emerge.lk:8082/CB_Halcyon_PVT/WebServices/WebServiceChannelBridge.asmx";

 // public final String SOAP_ADDRESS = "http://220.247.244.22:8082/Cb_PharmAce_Demo/WebServices/WebServiceChannelBridge.asmx";


  // public final String SOAP_ADDRESS = "http://halcyonsfa.emerge.lk:8082/eMerge/WebServices/WebServiceChannelBridge.asmx";






  public final String SOAP_ADDRESS = "http://halcyonsfa.emerge.lk:8082/CB_Halcyon_DEV/WebServices/WebServiceChannelBridge.asmx";



    int TimeOut=5000;//
    public String GetRepStatus(int id) {

        final String SOAP_ACTION = "http://tempuri.org/IsDeviceActive";
        final String OPERATION_NAME = "IsDeviceActive";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
        PropertyInfo pi = new PropertyInfo();

        pi.setName("RepID");
        pi.setValue(id);
        pi.setType(Integer.class);

        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response = null;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = envelope.getResponse();
        } catch (Exception exception) {
            response = exception.toString();
        }
        return response.toString();
    }

    public ArrayList<String> getRepForDevice(String deviceId,Context context) throws SocketException {

        ArrayList<String> repDetailList = null;

        final String SOAP_ACTION = "http://tempuri.org/xmlGetUser4Device";

        final String OPERATION_NAME = "xmlGetUser4Device";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        PropertyInfo pi = new PropertyInfo();

        Log.w("Log", "deviceId : " + deviceId);

        pi.setName("DeviceID");
        pi.setValue(deviceId);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                SoapObject repTable = (SoapObject) response.getProperty(0);

                if (repTable != null && repTable.getPropertyCount() > 0) {

                    SoapObject repDetails = (SoapObject) repTable
                            .getProperty(0);

                    repDetailList = new ArrayList<String>(
                            repDetails.getPropertyCount());

                    Log.w("Log", "repDetails object : " + repDetails.toString());

                    repDetailList.add(repDetails.getProperty("ID").toString());
                    repDetailList.add(repDetails.getProperty("DealerID")
                            .toString());
                    repDetailList.add(repDetails.getProperty("RepName")
                            .toString());
                    repDetailList.add(repDetails.getProperty("RepAddress")
                            .toString());
                    repDetailList.add(repDetails.getProperty("RepNId")
                            .toString());
                    repDetailList.add(repDetails.getProperty("RepHireDate")
                            .toString());
                    repDetailList.add(repDetails.getProperty("IsActive")
                            .toString());
                    repDetailList.add(repDetails.getProperty("RepType")
                            .toString());
                    repDetailList.add(repDetails.getProperty("DeviceID")
                            .toString());
                    repDetailList.add(repDetails.getProperty("Name")
                            .toString());
                    repDetailList.add(repDetails.getProperty("Town")
                            .toString());
                    repDetailList.add(repDetails.getProperty("PhoneNumber")
                            .toString());
                    repDetailList.add(repDetails.getProperty("CompID")
                            .toString());
                    repDetailList.add(repDetails.getProperty("IsStockTransfer")
                            .toString());
                    repDetailList.add(repDetails.getProperty("CompanyCode")
                            .toString());
                    repDetailList.add(repDetails.getProperty("WebApproval")
                            .toString());
                    repDetailList.add(repDetails.getProperty("InvoiceOption")
                            .toString());

                    repDetailList.add(repDetails.getProperty("IMEINo")
                            .toString());

                    repDetailList.add(repDetails.getProperty("CoordinatorsTPNumber")
                            .toString());


                    String a =repDetails.getProperty("IMEINo")
                            .toString();
/**
 * need to add
 */


                    SharedPreferences pref = context.getSharedPreferences("CBHesPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();

                    if(!repDetails.getProperty("WebApproval").toString().equals("") || repDetails.getProperty("WebApproval").toString() != null){
                        editor.putBoolean("WebApproval",Boolean.valueOf(repDetails.getProperty("WebApproval").toString()));
                    }
                    if(!repDetails.getProperty("InvoiceOption").toString().equals("") || repDetails.getProperty("InvoiceOption").toString() != null){
                        editor.putBoolean("InvoiceOption",Boolean.valueOf(repDetails.getProperty("InvoiceOption").toString()));
                    }
                    if(!repDetails.getProperty("ISOutStandingBlock").toString().equals("") || repDetails.getProperty("ISOutStandingBlock").toString() != null){
                        editor.putBoolean("ISOutStandingBlock",Boolean.valueOf(repDetails.getProperty("ISOutStandingBlock").toString()));
                    }
                    editor.commit();

                    Log.w("Log", "info : " + repDetailList.get(0));
                    Log.w("Log", "info : " + repDetailList.get(1));
                    Log.w("Log", "info : " + repDetailList.get(2));
                    Log.w("Log", "info : " + repDetailList.get(3));
                    Log.w("Log", "info : " + repDetailList.get(4));
                    Log.w("Log", "info : " + repDetailList.get(5));
                    Log.w("Log", "info : " + repDetailList.get(6));
                    Log.w("Log", "info : " + repDetailList.get(7));
                    Log.w("Log", "info : " + repDetailList.get(8));
                    Log.w("Log", "info : " + repDetailList.get(9));
                    Log.w("Log", "info : " + repDetailList.get(10));
                    Log.w("Log", "info rep telephone : " + repDetailList.get(11));
                    Log.w("CompID -->",repDetails.getProperty("CompID")
                            .toString());
                    Log.w("IsStockTransfer -->",repDetails.getProperty("IsStockTransfer")
                            .toString());
                    Log.w("CompanyCode  -->",repDetails.getProperty("CompanyCode")
                            .toString());
                    Log.w("Webapproval  -->",repDetails.getProperty("WebApproval")
                            .toString());
                    Log.w("InvoiceOption  -->",repDetails.getProperty("InvoiceOption")
                            .toString());
                } else {
                    repDetailList = new ArrayList<String>(0);
                    repDetailList.add("No Data");
                    Log.w("test123", "Error: 0 size zzz");
                }
            } else {
                repDetailList = new ArrayList<String>(0);
                repDetailList.add("No Data");
                Log.w("test123", "Error: 0 size xxx");
            }

        } catch (SocketException e) {
            System.out.println("SocketException :"+e);
            throw new SocketException(e.toString());

        } catch (Exception exception) {
            repDetailList = null;
            Log.w("test123", "Error: " + exception.toString());
        }

//		Log.w("test123", "list size : " + repDetailList.size());

        return repDetailList;
    }

    public ArrayList<String[]> getCustomerListForRep(String deviceId, String repId) throws SocketException {

        ArrayList<String[]> repDetailList = null;

        final String SOAP_ACTION = "http://tempuri.org/xmlGetCustomerList";

        final String OPERATION_NAME = "xmlGetCustomerList";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);
        PropertyInfo pi = new PropertyInfo();

        Log.w("Log", "deviceId : " + deviceId);
        Log.w("Log", "repId : " + repId);
        Log.w("Log", "getCustomerListForRep ");

        pi.setName("DeviceID");
        pi.setValue(deviceId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo piTwo = new PropertyInfo();
        piTwo.setName("RepID");
        piTwo.setValue(repId);
        piTwo.setType(String.class);
        request.addProperty(piTwo);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);


        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                SoapObject custTable = (SoapObject) response.getProperty(0);

                if (custTable != null && custTable.getPropertyCount() > 0) {

                    repDetailList = new ArrayList<String[]>(
                            custTable.getPropertyCount());

                    for (int i = 0; i < custTable.getPropertyCount(); i++) {
                        String[] customerDetails = new String[36];
                        SoapObject custDetails = (SoapObject) custTable
                                .getProperty(i);

                        Log.w("Log",
                                "repDetails object : " + custDetails.toString());

                        if (custDetails.hasProperty("GLB_PharmacyID")) {
                            customerDetails[0] = custDetails.getProperty(
                                    "GLB_PharmacyID").toString();
                            customerDetails[1] = customerDetails[0];
                        }
                        if (custDetails.hasProperty("GLB_PharmacyID")) {
                            customerDetails[1] = custDetails.getProperty(
                                    "GLB_PharmacyID").toString();
                        }
                        if (custDetails.hasProperty("DealerID")) {
                            if (custDetails.getProperty(
                                    "DealerID").toString() != "anyType{}") {
                                customerDetails[2] = custDetails.getProperty(
                                        "DealerID").toString();
                            } else {
                                customerDetails[2] = "";
                            }

                        }

                        if (custDetails.hasProperty("CompanyCode")) {

                            if (custDetails.getProperty(
                                    "CompanyCode").toString() != "anyType{}") {
                                customerDetails[3] = custDetails.getProperty(
                                        "CompanyCode").toString();
                            } else {
                                customerDetails[3] = "";
                            }

                        }

                        if (custDetails.hasProperty("Name")) {

                            if (custDetails.getProperty("Name").toString() != "anyType{}") {
                                customerDetails[4] = custDetails.getProperty("Name").toString();
                            } else {
                                customerDetails[4] = "";
                            }

                        }
                        System.out.println("himmmmmmmmmmmmmmmmmm :"+custDetails.getProperty("Name").toString());

                        if (custDetails.hasProperty("Address")) {

                            if (custDetails.getProperty(
                                    "Address").toString() != "anyType{}") {
                                customerDetails[5] = custDetails.getProperty(
                                        "Address").toString();
                            } else {
                                customerDetails[5] = "";
                            }


                        }

                        if (custDetails.hasProperty("District")) {

                            if (custDetails.getProperty(
                                    "District").toString() != "anyType{}") {
                                customerDetails[6] = custDetails.getProperty(
                                        "District").toString();
                            } else {
                                customerDetails[6] = "";
                            }
                        }

                        if (custDetails.hasProperty("Area")) {

                            if (custDetails.getProperty(
                                    "Area").toString() != "anyType{}") {
                                customerDetails[7] = custDetails
                                        .getProperty("Area").toString();
                            } else {
                                customerDetails[7] = "";
                            }

                        }

                        if (custDetails.hasProperty("Town")) {

                            if (custDetails.getProperty(
                                    "Town").toString() != "anyType{}") {
                                customerDetails[8] = custDetails
                                        .getProperty("Town").toString();
                            } else {
                                customerDetails[8] = "";
                            }
                        }

                        if (custDetails.hasProperty("Telephone")) {

                            if (custDetails.getProperty(
                                    "Telephone").toString() != "anyType{}") {
                                customerDetails[9] = custDetails.getProperty(
                                        "Telephone").toString();
                            } else {
                                customerDetails[9] = "";
                            }
                        }

                        if (custDetails.hasProperty("Fax")) {

                            if (custDetails.getProperty(
                                    "Fax").toString() != "anyType{}") {
                                customerDetails[10] = custDetails
                                        .getProperty("Fax").toString();
                            } else {
                                customerDetails[10] = "";
                            }
                        }

                        if (custDetails.hasProperty("Email")) {

                            if (custDetails.getProperty(
                                    "Email").toString() != "anyType{}") {
                                customerDetails[11] = custDetails.getProperty(
                                        "Email").toString();
                            } else {
                                customerDetails[11] = "";
                            }
                        }

                        if (custDetails.hasProperty("CustomerStatus")) {

                            if (custDetails.getProperty(
                                    "CustomerStatus").toString() != "anyType{}") {
                                customerDetails[12] = custDetails.getProperty(
                                        "CustomerStatus").toString();
                            } else {
                                customerDetails[12] = "";
                            }
                        }

                        if (custDetails.hasProperty("CreditLimit")) {

                            if (custDetails.getProperty(
                                    "CreditLimit").toString() != "anyType{}") {
                                customerDetails[13] = custDetails.getProperty(
                                        "CreditLimit").toString();
                            } else {
                                customerDetails[13] = "";
                            }
                        }

                        if (custDetails.hasProperty("CreditExpiryDate")) {

                            if (custDetails.getProperty(
                                    "CreditExpiryDate").toString() != "anyType{}") {
                                customerDetails[14] = custDetails.getProperty(
                                        "CreditExpiryDate").toString();
                            } else {
                                customerDetails[14] = "";
                            }
                        }

                        if (custDetails.hasProperty("CreditDuration")) {

                            if (custDetails.getProperty(
                                    "CreditDuration").toString() != "anyType{}") {
                                customerDetails[15] = custDetails.getProperty(
                                        "CreditDuration").toString();
                            } else {
                                customerDetails[15] = "";
                            }
                        }

                        if (custDetails.hasProperty("VATNo")) {

                            if (custDetails.getProperty(
                                    "VATNo").toString() != "anyType{}") {
                                customerDetails[16] = custDetails.getProperty(
                                        "VATNo").toString();
                            } else {
                                customerDetails[16] = "";
                            }
                        }

                        if (custDetails.hasProperty("Status")) {

                            if (custDetails.getProperty(
                                    "Status").toString() != "anyType{}") {
                                customerDetails[17] = custDetails.getProperty(
                                        "Status").toString();
                            } else {
                                customerDetails[17] = "";
                            }
                        }

                        if (custDetails.hasProperty("temp")) {

                            if (custDetails.getProperty(
                                    "temp").toString() != "anyType{}") {
                                customerDetails[18] = custDetails.getProperty(
                                        "temp").toString();
                            } else {
                                customerDetails[18] = "";
                            }
                        }

                        if (custDetails.hasProperty("CustomerNo")) {

                            if (custDetails.getProperty(
                                    "CustomerNo").toString() != "anyType{}") {
                                customerDetails[19] = custDetails.getProperty(
                                        "CustomerNo").toString();
                            } else {
                                customerDetails[19] = "";
                            }
                        }

                        if (custDetails.hasProperty("Web")) {

                            if (custDetails.getProperty(
                                    "Web").toString() != "anyType{}") {
                                customerDetails[20] = custDetails
                                        .getProperty("Web").toString();
                            } else {
                                customerDetails[20] = "";
                            }
                        }

                        if (custDetails.hasProperty("BrNo")) {

                            if (custDetails.getProperty(
                                    "BrNo").toString() != "anyType{}") {
                                customerDetails[21] = custDetails.getProperty(
                                        "BrNo").toString();
                            } else {
                                customerDetails[21] = "";
                            }
                        }

                        if (custDetails.hasProperty("OwnerContactNo")) {

                            if (custDetails.getProperty(
                                    "OwnerContactNo").toString() != "anyType{}") {
                                customerDetails[22] = custDetails.getProperty(
                                        "OwnerContactNo").toString();
                            } else {
                                customerDetails[22] = "";
                            }
                        }

                        if (custDetails.hasProperty("PharmacyRegNo")) {

                            if (custDetails.getProperty(
                                    "PharmacyRegNo").toString() != "anyType{}") {
                                customerDetails[23] = custDetails.getProperty(
                                        "PharmacyRegNo").toString();
                            } else {
                                customerDetails[23] = "";
                            }
                        }

                        if (custDetails.hasProperty("OwnersWifesBDay")) {

                            if (custDetails.getProperty(
                                    "OwnersWifesBDay").toString() != "anyType{}") {
                                customerDetails[24] = custDetails.getProperty(
                                        "OwnersWifesBDay").toString();
                            } else {
                                customerDetails[24] = "";
                            }
                        }

                        if (custDetails.hasProperty("PharmacistName")) {

                            if (custDetails.getProperty(
                                    "PharmacistName").toString() != "anyType{}") {
                                customerDetails[25] = custDetails.getProperty(
                                        "PharmacistName").toString();
                            } else {
                                customerDetails[25] = "";
                            }
                        }

                        if (custDetails.hasProperty("PuchasingOfficer")) {

                            if (custDetails.getProperty(
                                    "PuchasingOfficer").toString() != "anyType{}") {
                                customerDetails[26] = custDetails.getProperty(
                                        "PuchasingOfficer").toString();
                            } else {
                                customerDetails[26] = "";
                            }
                        }

                        if (custDetails.hasProperty("NoOfStaff")) {

                            if (custDetails.getProperty(
                                    "NoOfStaff").toString() != "anyType{}") {
                                customerDetails[27] = custDetails.getProperty(
                                        "NoOfStaff").toString();
                            } else {
                                customerDetails[27] = "";
                            }
                        }

                        if (custDetails.hasProperty("Latitude")) {

                            if (custDetails.getProperty(
                                    "Latitude").toString() != "anyType{}") {
                                customerDetails[28] = custDetails.getProperty(
                                        "Latitude").toString();
                            } else {
                                customerDetails[28] = "";
                            }
                        }

                        if (custDetails.hasProperty("Longitude")) {

                            if (custDetails.getProperty(
                                    "Longitude").toString() != "anyType{}") {
                                customerDetails[29] = custDetails.getProperty(
                                        "Longitude").toString();
                            } else {
                                customerDetails[29] = "";
                            }
                        }

                        if (custDetails.hasProperty("JobID")) {

                            if (custDetails.getProperty(
                                    "JobID").toString() != "anyType{}") {
                                customerDetails[30] = custDetails.getProperty(
                                        "JobID").toString();
                            } else {
                                customerDetails[30] = "";
                            }
                        }

                        if (custDetails.hasProperty("PrimaryImageId")) {

                            if (custDetails.getProperty(
                                    "PrimaryImageId").toString() != "anyType{}") {
                                customerDetails[31] = custDetails.getProperty(
                                        "PrimaryImageId").toString();
                            } else {
                                customerDetails[31] = "";
                            }
                        }

                        if (custDetails.hasProperty("CusImage")) {

                            if (custDetails.getProperty(
                                    "CusImage").toString() != "anyType{}") {
                                customerDetails[32] = custDetails.getProperty(
                                        "CusImage").toString();
                            } else {
                                customerDetails[32] = "";
                            }
                        }
                        if (custDetails.hasProperty("IsInvoiceAllowed")) {//test

                            if (custDetails.getProperty("IsInvoiceAllowed").toString() != "anyType{}") {
                                customerDetails[33] = custDetails.getProperty("IsInvoiceAllowed").toString();
                            } else {
                                customerDetails[33] = "0";
                            }
                        }
                        if (custDetails.hasProperty("MaxInvoiceCount")) {//test

                            if (custDetails.getProperty("MaxInvoiceCount").toString() != "anyType{}") {
                                customerDetails[34] = custDetails.getProperty("MaxInvoiceCount").toString();
                            } else {
                                customerDetails[34] = "0";
                            }
                        }if (custDetails.hasProperty("CustomerBlocked")) {//test

                            if (custDetails.getProperty("CustomerBlocked").toString() != "anyType{}") {
                                customerDetails[35] = custDetails.getProperty("CustomerBlocked").toString();
                            } else {
                                customerDetails[35] = "0";
                            }
                        }


                        repDetailList.add(customerDetails);

                    }

                } else {
                    repDetailList = new ArrayList<String[]>(0);

                    Log.w("test123", "Error: 0 size zzz");
                }
            } else {
                repDetailList = new ArrayList<String[]>(0);
                Log.w("test123", "Error: 0 size xxx");
            }
        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            repDetailList = null;
            Log.w("test123", "Error: " + exception.toString());
        }



        return repDetailList;
    }

    public ArrayList<String[]> getItineraryListForRep(String repId, String deviceId, String maxRowId) throws SocketException {

        ArrayList<String[]> repDetailList = null;//testsas

        final String SOAP_ACTION = "http://tempuri.org/xmlItinarary";

        final String OPERATION_NAME = "xmlItinarary";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);
        PropertyInfo pi = new PropertyInfo();

        Log.w("Log", "repId : " + repId);

        pi.setName("RepID");
        pi.setValue(repId);
        pi.setType(String.class);
        //  pi.setType(Byte.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("DeviceID");
        pi2.setValue(deviceId);
        pi2.setType(String.class);
        request.addProperty(pi2);

        PropertyInfo pi3 = new PropertyInfo();
        pi3.setName("MaxRowID");
        pi3.setValue(maxRowId);
        pi3.setType(String.class);
        request.addProperty(pi3);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        Log.w("Log", "getItineraryListForRep : " + request.toString());

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS,TimeOut);
        SoapObject response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                SoapObject custTable = (SoapObject) response.getProperty(0);

                if (custTable != null && custTable.getPropertyCount() > 0) {

                    repDetailList = new ArrayList<String[]>(
                            custTable.getPropertyCount());

                    for (int i = 0; i < custTable.getPropertyCount(); i++) {
                        String[] customerDetails = new String[9];
                        SoapObject custDetails = (SoapObject) custTable
                                .getProperty(i);

                        Log.w("Log",
                                "repDetails object : " + custDetails.toString());

                        if (custDetails.hasProperty("Itn_ID")) {
                            customerDetails[0] = custDetails.getProperty(
                                    "Itn_ID").toString();
                        }
                        if (custDetails.hasProperty("Itn_Date")) {
                            customerDetails[1] = custDetails.getProperty(
                                    "Itn_Date").toString();
                        }

                        if (custDetails.hasProperty("Itn_VisitNo")) {
                            customerDetails[2] = custDetails.getProperty(
                                    "Itn_VisitNo").toString();
                        }

                        if (custDetails.hasProperty("GLB_PharmacyID")) {
                            customerDetails[3] = custDetails.getProperty(
                                    "GLB_PharmacyID").toString();
                        }

                        if (custDetails.hasProperty("GLB_PharmacyCode")) {
                            customerDetails[4] = custDetails.getProperty(
                                    "GLB_PharmacyCode").toString();
                        }

                        if (custDetails.hasProperty("Name")) {
                            customerDetails[5] = custDetails
                                    .getProperty("Name").toString();
                        }

                        if (custDetails.hasProperty("Target")) {
                            customerDetails[6] = custDetails.getProperty(
                                    "Target").toString();
                        }

                        if (custDetails.hasProperty("Itn_RepID")) {
                            customerDetails[7] = custDetails.getProperty(
                                    "Itn_RepID").toString();
                        }
                        if (custDetails.hasProperty("RowID")) {
                            customerDetails[8] = custDetails.getProperty(
                                    "RowID").toString();
                        }

                        repDetailList.add(customerDetails);

                        Log.w("Log", "info : " + customerDetails[0]);
                        Log.w("Log", "info : " + customerDetails[1]);
                        Log.w("Log", "info : " + customerDetails[2]);
                        Log.w("Log", "info : " + customerDetails[3]);
                        Log.w("Log", "info : " + customerDetails[4]);
                        Log.w("Log", "info : " + customerDetails[5]);
                        Log.w("Log", "info : " + customerDetails[6]);
                        Log.w("Log", "info : " + customerDetails[7]);
                        Log.w("Log", "info : " + customerDetails[8]);

                    }

                } else {
                    repDetailList = new ArrayList<String[]>(0);

                    Log.w("test123", "Error: 0 size zzz");
                }
            } else {
                repDetailList = new ArrayList<String[]>(0);
                Log.w("test123", "Error: 0 size xxx");
            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            repDetailList = null;
            Log.w("test123 getItineraryListForRep",
                    "Error: " + exception.toString());
        }

        return repDetailList;
    }

    public ArrayList<String[]> getProductListForRep(String deviceId, String repId) throws SocketException {

        ArrayList<String[]> prodDetailList = null;

        final String SOAP_ACTION = "http://tempuri.org/xmlGetProducts";

        final String OPERATION_NAME = "xmlGetProducts";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);
        PropertyInfo pi = new PropertyInfo();

        Log.w("Log", "deviceId : " + deviceId);
        Log.w("Log", "repId : " + repId);

        pi.setName("DeviceID");
        pi.setValue(deviceId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo piRep = new PropertyInfo();
        piRep.setName("RepID");
        piRep.setValue(repId);
        piRep.setType(String.class);
        request.addProperty(piRep);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        Log.w("Log", "request toString : " + request.toString());

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                SoapObject prodTable = (SoapObject) response.getProperty(0);

                if (prodTable != null && prodTable.getPropertyCount() > 0) {

                    prodDetailList = new ArrayList<String[]>(
                            prodTable.getPropertyCount());

                    for (int i = 0; i < prodTable.getPropertyCount(); i++) {
                        String[] prodDetails = new String[18];
                        SoapObject prductDetails = (SoapObject) prodTable
                                .getProperty(i);

                        Log.w("Log", "product Details object : "
                                + prductDetails.toString());

                        if (prductDetails.hasProperty("ID")) {
                            prodDetails[0] = prductDetails.getProperty("ID").toString();
                        }
                        if (prductDetails.hasProperty("BrandName")) {
                            prodDetails[1] = prductDetails.getProperty("BrandName").toString();
                        }
                        if (prductDetails.hasProperty("ItemCode")) {
                            prodDetails[2] = prductDetails.getProperty("ItemCode").toString();
                        }

                        if (prductDetails.hasProperty("UnitName")) {
                            prodDetails[3] = prductDetails.getProperty("UnitName").toString();
                        }


                        if (prductDetails.hasProperty("UnitSize")) {
                            prodDetails[4] = prductDetails.getProperty("UnitSize").toString();
                        }

                        if (prductDetails.hasProperty("GenericName")) {
                            prodDetails[5] = prductDetails.getProperty(
                                    "GenericName").toString();
                        }

                        if (prductDetails.hasProperty("ItemCategoryName")) {
                            prodDetails[6] = prductDetails.getProperty(
                                    "ItemCategoryName").toString();
                        }

                        if (prductDetails.hasProperty("Description")) {
                            prodDetails[7] = prductDetails.getProperty(
                                    "Description").toString();
                        }

                        if (prductDetails.hasProperty("IntroducedDate")) {
                            prodDetails[8] = prductDetails.getProperty(
                                    "IntroducedDate").toString();
                        }

                        if (prductDetails.hasProperty("CountryOfOrigin")) {
                            prodDetails[9] = prductDetails.getProperty(
                                    "CountryOfOrigin").toString();
                        }

                        if (prductDetails.hasProperty("Principle")) {
                            prodDetails[10] = prductDetails.getProperty(
                                    "Principle").toString();
                        }

                        if (prductDetails.hasProperty("PurchasingPrice")) {
                            prodDetails[11] = prductDetails.getProperty(
                                    "PurchasingPrice").toString();
                        }

                        if (prductDetails.hasProperty("SellingPrice")) {
                            prodDetails[12] = prductDetails.getProperty(
                                    "SellingPrice").toString();
                        }

                        if (prductDetails.hasProperty("RetailPrice")) {
                            prodDetails[13] = prductDetails.getProperty(
                                    "RetailPrice").toString();
                        }

                        if (prductDetails.hasProperty("Force")) {
                            prodDetails[14] = prductDetails
                                    .getProperty("Force").toString();
                        }

                        if (prductDetails.hasProperty("Inactive")) {
                            prodDetails[15] = prductDetails.getProperty(
                                    "Inactive").toString();
                        }

                        if (prductDetails.hasProperty("VAT")) {
                            prodDetails[16] = prductDetails.getProperty("VAT")
                                    .toString();
                        }

                        if (prductDetails.hasProperty("TT")) {
                            prodDetails[17] = prductDetails.getProperty("TT")
                                    .toString();
                        }

                        prodDetailList.add(prodDetails);

                        Log.w("Log", "info : " + prodDetails[0]);
                        Log.w("Log", "info : " + prodDetails[1]);
                        Log.w("Log", "info : " + prodDetails[2]);
                        Log.w("Log", "info : " + prodDetails[3]);
                        Log.w("Log", "info : " + prodDetails[4]);
                        Log.w("Log", "info : " + prodDetails[5]);
                        Log.w("Log", "info : " + prodDetails[6]);
                        Log.w("Log", "info : " + prodDetails[7]);
                        Log.w("Log", "info : " + prodDetails[8]);
                        Log.w("Log", "info : " + prodDetails[9]);
                        Log.w("Log", "info : " + prodDetails[10]);
                        Log.w("Log", "info : " + prodDetails[11]);
                        Log.w("Log", "info : " + prodDetails[12]);
                        Log.w("Log", "info : " + prodDetails[13]);
                        Log.w("Log", "info : " + prodDetails[14]);
                        Log.w("Log", "info : " + prodDetails[15]);
                        Log.w("Log", "info : " + prodDetails[16]);
                        Log.w("Log", "info : " + prodDetails[17]);

                    }

                } else {
                    prodDetailList = new ArrayList<String[]>(0);

                    Log.w("test123", "Error: 0 size zzz");
                }
            } else {
                prodDetailList = new ArrayList<String[]>(0);
                Log.w("test123", "Error: 0 size xxx");
            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            prodDetailList = null;
            Log.w("test123", "Error: " + exception.toString());
        }

        // Log.w("test123", "list size : " + prodDetailList.size());

        return prodDetailList;
    }

    public ArrayList<String[]> getProductRepStoreList(String deviceId, String repId, int maxRowID) throws SocketException {

        ArrayList<String[]> repStoreDetailList = null;

        final String SOAP_ACTION = "http://tempuri.org/xmlGetRepStoreData";

        final String OPERATION_NAME = "xmlGetRepStoreData";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);

        // need to remove below line its test purpose only

        Log.w("Log", "deviceId : " + deviceId);
        Log.w("Log", "repId : " + repId);
        Log.w("Log", "maxRowID : " + maxRowID);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("DeviceID");
        pi.setValue(deviceId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("RepID");
        pi2.setValue(repId);
        pi2.setType(String.class);
        request.addProperty(pi2);

        PropertyInfo pi3 = new PropertyInfo();
        pi3.setName("MaxRowID");
        pi3.setValue(maxRowID);
        pi3.setType(String.class);
        request.addProperty(pi3);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS,TimeOut);
        SoapObject response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                SoapObject repStoreTable = (SoapObject) response.getProperty(0);

                if (repStoreTable != null
                        && repStoreTable.getPropertyCount() > 0) {

                    repStoreDetailList = new ArrayList<String[]>(
                            repStoreTable.getPropertyCount());

                    for (int i = 0; i < repStoreTable.getPropertyCount(); i++) {
                        String[] prodDetails = new String[9];
                        SoapObject prductDetails = (SoapObject) repStoreTable
                                .getProperty(i);

                        Log.w("Log", "product Details object : "
                                + prductDetails.toString());

                        if (prductDetails.hasProperty("RowID")) {
                            prodDetails[0] = prductDetails.getProperty("RowID")
                                    .toString();
                        }
                        if (prductDetails.hasProperty("RepID")) {
                            prodDetails[1] = prductDetails.getProperty("RepID")
                                    .toString();
                        }
                        if (prductDetails.hasProperty("Code")) {
                            prodDetails[2] = prductDetails.getProperty("Code")
                                    .toString();
                        }

                        if (prductDetails.hasProperty("Qty")) {
                            prodDetails[3] = prductDetails.getProperty("Qty")
                                    .toString();
                        }

                        if (prductDetails.hasProperty("ExpireDate")) {
                            prodDetails[4] = prductDetails.getProperty(
                                    "ExpireDate").toString();
                        }

                        if (prductDetails.hasProperty("BatchNumber")) {
                            prodDetails[5] = prductDetails.getProperty(
                                    "BatchNumber").toString();
                        }

                        if (prductDetails.hasProperty("PurchasingPrice")) {
                            prodDetails[6] = prductDetails.getProperty(
                                    "PurchasingPrice").toString();
                        }
                        if (prductDetails.hasProperty("SalePrice")) {
                            prodDetails[7] = prductDetails.getProperty(
                                    "SalePrice").toString();
                        }
                        if (prductDetails.hasProperty("RetailPrice")) {
                            prodDetails[8] = prductDetails.getProperty(
                                    "RetailPrice").toString();
                        }
                        repStoreDetailList.add(prodDetails);

                        Log.w("Log", "info : " + prodDetails[0]);
                        Log.w("Log", "info : " + prodDetails[1]);
                        Log.w("Log", "info : -x->" + prodDetails[2]);
                        Log.w("Log", "info : " + prodDetails[3]);
                        Log.w("Log", "info : " + prodDetails[4]);
                        Log.w("Log", "info : " + prodDetails[5]);
                        Log.w("Log", "info p -x->: " + prodDetails[6]);
                        Log.w("Log", "info s -x->:  " + prodDetails[7]);
                        Log.w("Log", "info r -x->: " + prodDetails[8]);
                    }

                } else {
                    repStoreDetailList = new ArrayList<String[]>(0);

                    Log.w("test123", "Error repStoreDetailList: 0 size zzz");
                }
            } else {
                repStoreDetailList = new ArrayList<String[]>(0);
                Log.w("test123", "Error repStoreDetailList: 0 size xxx");
            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            repStoreDetailList = null;
            Log.w("test123",
                    "Error repStoreDetailList: " + exception.toString());
        }

        // Log.w("test123", "list size repStoreDetailList : " +
        // repStoreDetailList.size());

        return repStoreDetailList;
    }

    public String uploadInvoiceDetails(String deviceId, String repId, ArrayList<String[]> invoicedProductDetailList) throws SocketException {

        String repStoreDetailList = "";

        final String SOAP_ACTION = "http://tempuri.org/xmlSetInvoices";

        final String OPERATION_NAME = "xmlSetInvoices";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);

        Log.w("Log", "deviceId : " + deviceId);
        Log.w("Log", "repId : " + repId);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("DeviceID");
        pi.setValue(deviceId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("RepID");
        pi2.setValue(repId);
        pi2.setType(String.class);
        request.addProperty(pi2);

        Log.w("Log", "invoicedProductDetailList count : "
                + invoicedProductDetailList.size());

        SoapObject dataset = new SoapObject(WSDL_TARGET_NAMESPACE, "NewDataSet");

        for (String[] invoicedProduct : invoicedProductDetailList) {

            SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE, "Table");

            table.addProperty("Code", invoicedProduct[0]);
            table.addProperty("InvoiceNo", invoicedProduct[1]);
            table.addProperty("IssueMode", invoicedProduct[2]);
            table.addProperty("Qty", invoicedProduct[3]);
         //   table.addProperty("Date", invoicedProduct[4]);
            table.addProperty("PaymentType", invoicedProduct[5]);
            table.addProperty("ExpireDate", invoicedProduct[6]);
            table.addProperty("BatchNo", invoicedProduct[7]);
            table.addProperty("CustomerNo", invoicedProduct[8]);
            table.addProperty("Profit", invoicedProduct[9]);
            table.addProperty("UnitPrice", invoicedProduct[10]);
            table.addProperty("Discount", invoicedProduct[11]);
            table.addProperty("ID", invoicedProduct[12]);
            table.addProperty("Date", invoicedProduct[13]);
            table.addProperty("Lot", invoicedProduct[14]);
            table.addProperty("Lat", invoicedProduct[15]);
            table.addProperty("RequestedQty", invoicedProduct[16]);
            table.addProperty("EligibleFree", invoicedProduct[17]);

            System.out.println("request toString table :"+table);
            dataset.addSoapObject(table);
        }
        System.out.println("request toString dataset :"+dataset);

        SoapObject invoiceData = new SoapObject(WSDL_TARGET_NAMESPACE, "InvoiceData");
        invoiceData.addSoapObject(dataset);
        request.addSoapObject(invoiceData);
        Log.w("r> ", request.toString());
        Log.w("Log", "request toString : " + request.toString());
        System.out.println("request toString sout :"+request);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS,TimeOut);
        SoapObject response;
        try {

            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.bodyIn;

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                repStoreDetailList = response.getProperty(
                        "xmlSetInvoicesResult").toString();

            } else {
                repStoreDetailList = "error";
                Log.w("test123", "Error repStoreDetailList: 0 size xxx");
            }
        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            repStoreDetailList = "error";
            Log.w("test123",
                    "Error repStoreDetailList: " + exception.toString());

            Log.w("test123", "Error repStoreDetailList: " + exception);
        }

        Log.w("test123", "list size repStoreDetailList : " + repStoreDetailList);

        return repStoreDetailList;

    }

    public String uploadProductReturnsDetails(String deviceId, String repId, ArrayList<String[]> rtnProductList) throws SocketException {

        String repStoreDetailList = "";

        final String SOAP_ACTION = "http://tempuri.org/xmlSetReturns";

        final String OPERATION_NAME = "xmlSetReturns";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);

        Log.w("Log", "deviceId : " + deviceId);
        Log.w("Log", "repId : " + repId);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("DeviceID");
        pi.setValue(deviceId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("RepID");
        pi2.setValue(repId);
        pi2.setType(String.class);
        request.addProperty(pi2);

        SoapObject dataset = new SoapObject(WSDL_TARGET_NAMESPACE, "NewDataSet");

        for (String[] rtnProduct : rtnProductList) {

            Log.w("Log", "rtnProducts 0 : " + rtnProduct[0]);
            Log.w("Log", "rtnProducts 1 : " + rtnProduct[1]);
            Log.w("Log", "rtnProducts 2 : " + rtnProduct[2]);
            Log.w("Log", "rtnProducts 3 : " + rtnProduct[3]);
            Log.w("Log", "rtnProducts 4 : " + rtnProduct[4]);
            Log.w("Log", "rtnProducts 5 : " + rtnProduct[5]);
            Log.w("Log", "rtnProducts 6 : " + rtnProduct[6]);
            Log.w("Log", "rtnProducts 7 : " + rtnProduct[7]);
            Log.w("Log", "rtnProducts 8 : " + rtnProduct[8]);
            Log.w("Log", "rtnProducts 9 : " + rtnProduct[9]);
            Log.w("Log", "Latitude: " + rtnProduct[11]);
            Log.w("Log", "Longitude: " + rtnProduct[12]);
            Log.w("Log", "Flag: " + rtnProduct[13]);
            SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE, "Table");

            table.addProperty("Code", rtnProduct[0]);
            table.addProperty("InvoiceNo", rtnProduct[1]);
            table.addProperty("IssueMode", rtnProduct[2]);
            table.addProperty("Qty", rtnProduct[3]);
            table.addProperty("ReturnDate", rtnProduct[4]);
            table.addProperty("ExpireDate", rtnProduct[5]);
            table.addProperty("BatchNo", rtnProduct[6]);
            table.addProperty("CustomerNo", rtnProduct[7]);
            table.addProperty("UnitPrice", rtnProduct[8]);
            table.addProperty("ID", rtnProduct[9]);
            table.addProperty("Discount", rtnProduct[10]);
            table.addProperty("Latitude", rtnProduct[11]);
            table.addProperty("Longitude", rtnProduct[12]);
            table.addProperty("Flag", rtnProduct[13]);
            dataset.addSoapObject(table);
        }

        SoapObject invoiceData = new SoapObject(WSDL_TARGET_NAMESPACE,
                "ReturnData");
        invoiceData.addSoapObject(dataset);
        request.addSoapObject(invoiceData);

        Log.w("Log", "request toString : " + request.toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS,TimeOut);
        SoapObject response;
        try {

            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.bodyIn;

            Log.w("test123", "response : " + response.toString());

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                repStoreDetailList = response
                        .getProperty("xmlSetReturnsResult").toString();

            } else {
                repStoreDetailList = "error";
                Log.w("test123", "Error repStoreDetailList: 0 size xxx");
            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            repStoreDetailList = "error";
            Log.w("test123",
                    "Error repStoreDetailList: " + exception.toString());

            Log.w("test123", "Error repStoreDetailList: " + exception);
        }

        Log.w("test123", "list size repStoreDetailList : " + repStoreDetailList);

        return repStoreDetailList;

    }

    public String uploadNewCustomerDetails(String deviceId, String repId, String[] custDetails) throws SocketException {

        String repStoreDetailList = "";


        String NoofVisiter;
        if (custDetails[17].isEmpty() || custDetails[17] == "")
            NoofVisiter = "0";
        else
            NoofVisiter = custDetails[17];

        final String SOAP_ACTION = "http://tempuri.org/xmlSendNewCustomers";

        final String OPERATION_NAME = "xmlSendNewCustomers";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);

        Log.w("Log", "deviceId : " + deviceId);
        Log.w("Log", "repId : " + repId);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("DeviceID");
        pi.setValue(deviceId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("RepID");
        pi2.setValue(repId);
        pi2.setType(String.class);
        request.addProperty(pi2);

        SoapObject dataset = new SoapObject(WSDL_TARGET_NAMESPACE, "NewDataSet");

        Log.w("Log", "rtnProducts 0 : " + custDetails[0]);
        Log.w("Log", "rtnProducts 1 : " + custDetails[1]);
        Log.w("Log", "rtnProducts 2 : " + custDetails[2]);
        Log.w("Log", "rtnProducts 3 : " + custDetails[3]);
        Log.w("Log", "rtnProducts 4 : " + custDetails[4]);
        Log.w("Log", "rtnProducts 5 : " + custDetails[5]);
        Log.w("Log", "rtnProducts 6 : " + custDetails[6]);
        Log.w("Log", "rtnProducts 7 : " + custDetails[7]);
        Log.w("Log", "rtnProducts 8 : " + custDetails[8]);
        Log.w("Log", "rtnProducts 9 : " + custDetails[9]);

        SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE, "Table");

        table.addProperty("CustomerNo", custDetails[0]);
        table.addProperty("CustomerName", custDetails[1]);
        table.addProperty("Address", custDetails[2]);
        table.addProperty("Area", custDetails[3]);
        table.addProperty("Town", custDetails[4]);
        table.addProperty("District", custDetails[5]);
        table.addProperty("Telephone", custDetails[6]);
        table.addProperty("Fax", custDetails[7]);
        table.addProperty("Email", custDetails[8]);
        table.addProperty("Web", custDetails[9]);
        table.addProperty("BrNo", custDetails[10]);
        table.addProperty("IsActive", custDetails[11]);
        table.addProperty("OwnerContactNo", custDetails[12]);
        table.addProperty("PharmacyRegNo", custDetails[13]);
        table.addProperty("OwnersWifesBDay", custDetails[14]);
        table.addProperty("PharmacistName", custDetails[15]);
        table.addProperty("PuchasingOfficer", custDetails[16]);
        table.addProperty("NoOfStaff", NoofVisiter);
        table.addProperty("Latitude", custDetails[18]);
        table.addProperty("Longitude", custDetails[19]);
        table.addProperty("PrimaryImageId", custDetails[20]);
        table.addProperty("CustomerType", custDetails[21]);
        table.addProperty("CusImg", custDetails[23]);
        dataset.addSoapObject(table);

        SoapObject invoiceData = new SoapObject(WSDL_TARGET_NAMESPACE,
                "NewCustomer");
        invoiceData.addSoapObject(dataset);

        request.addSoapObject(invoiceData);

        Log.w("Log", "request toString : " + request.toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS,TimeOut);
        SoapObject response;
        try {

            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.bodyIn;

            Log.w("test123", "response : " + response.toString());

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                repStoreDetailList = response.getProperty(
                        "xmlSendNewCustomersResult").toString();

            } else {
                repStoreDetailList = "error";
                Log.w("test123", "Error repStoreDetailList: 0 size xxx");
            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            repStoreDetailList = "error";
            Log.w("test123",
                    "Error repStoreDetailList: " + exception.toString());

            Log.w("test123", "Error repStoreDetailList: " + exception);
        }

        Log.w("test123", "list size repStoreDetailList : " + repStoreDetailList);

        return repStoreDetailList;

    }


    public String uploadShelfQuantityDetails(String deviceId, String repId, String[] rtnProduct) throws SocketException {

        String repStoreDetailList = "";

        final String SOAP_ACTION = "http://tempuri.org/xmlSendShelfQuantity";

        final String OPERATION_NAME = "xmlSendShelfQuantity";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);

        Log.w("Log", "deviceId : " + deviceId);
        Log.w("Log", "repId : " + repId);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("DeviceID");
        pi.setValue(deviceId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("RepID");
        pi2.setValue(repId);
        pi2.setType(String.class);
        request.addProperty(pi2);

        SoapObject dataset = new SoapObject(WSDL_TARGET_NAMESPACE, "NewDataSet");

        Log.w("Log", "rtnProducts 0 : " + rtnProduct[0]);
        Log.w("Log", "rtnProducts 1 : " + rtnProduct[1]);
        Log.w("Log", "rtnProducts 2 : " + rtnProduct[2]);
        Log.w("Log", "rtnProducts 3 : " + rtnProduct[3]);
        Log.w("Log", "rtnProducts 4 : " + rtnProduct[4]);
        Log.w("Log", "rtnProducts 5 : " + rtnProduct[5]);
        Log.w("Log", "rtnProducts 6 : " + rtnProduct[6]);

        SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE, "Table");

        table.addProperty("RepId", Integer.parseInt(rtnProduct[0]));
        table.addProperty("InvoiceNo", rtnProduct[1]);
        table.addProperty("CustomerId", rtnProduct[3]);
        table.addProperty("AvailableStock", Integer.parseInt(rtnProduct[5]));
        table.addProperty("ItemCode", rtnProduct[4]);
        table.addProperty("InvoiceDate", rtnProduct[2].substring(0, 10));
        table.addProperty("BatchNumber", rtnProduct[6]);

        // table.addProperty("RepId", "2");
        // table.addProperty("InvoiceNo", "abc123");
        // table.addProperty("CustomerId", "2");
        // table.addProperty("AvailableStock", "10");
        // table.addProperty("ItemCode", "HDAFTA1");
        // table.addProperty("InvoiceDate", "2012-04-30");
        // table.addProperty("BatchNumber", "BMVM11016");

        // <Table>
        // <RepId>2</RepId>
        // <InvoiceNo>abc123</InvoiceNo>
        // <CustomerId>2</CustomerId>
        // <AvailableStock>10</AvailableStock>
        // <ItemCode>HDAFTA1</ItemCode>
        // <InvoiceDate>2012-04-30</InvoiceDate>
        // <BatchNumber>BMVM11016</BatchNumberumber>
        // </Table>

        dataset.addSoapObject(table);

        SoapObject invoiceData = new SoapObject(WSDL_TARGET_NAMESPACE,
                "ShelfQty"); // run this now im out
        invoiceData.addSoapObject(dataset);
        request.addSoapObject(invoiceData);

        Log.w("Log", "request toString : " + request.toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.w("request shelf -->", " " + request.toString());
        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS,TimeOut);
        SoapObject response;
        try {

            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.bodyIn;

            Log.w("test123", "response : " + response.toString());

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response of shelf --> : " + response.toString());

                repStoreDetailList = response.getProperty(
                        "xmlSendShelfQuantityResult").toString();

            } else {
                repStoreDetailList = "error";
                Log.w("test123", "Error repStoreDetailList: 0 size xxx");
            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            repStoreDetailList = "error";
            Log.w("test123",
                    "Error repStoreDetailList: " + exception.toString());

            Log.w("test123", "Error repStoreDetailList: " + exception);
        }

        Log.w("test123", "list size repStoreDetailList : " + repStoreDetailList);

        return repStoreDetailList;

    }

    public ArrayList<String[]> getProductList(String deviceId, String repId, String maxRowId) throws SocketException {

        ArrayList<String[]> prodDetailList = null;

        final String SOAP_ACTION = "http://tempuri.org/xmlGetPriceChanges";

        final String OPERATION_NAME = "xmlGetPriceChanges";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);

        Log.w("Log", "deviceId : " + deviceId);
        Log.w("Log", "repId : " + repId);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("RepID");
        pi.setValue(repId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("DeviceID");
        pi2.setValue(deviceId);
        pi2.setType(String.class);
        request.addProperty(pi2);

        PropertyInfo pi3 = new PropertyInfo();
        pi3.setName("MaxJobID");
        pi3.setValue(maxRowId);
        pi3.setType(String.class);
        request.addProperty(pi3);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        Log.w("Log", "request toString : " + request.toString());
        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS,TimeOut);

         SoapObject response = null;

        try {
            final SoapObject finalResponse = response;
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();


            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                SoapObject prodTable = (SoapObject) response.getProperty(0);

                if (prodTable != null && prodTable.getPropertyCount() > 0) {

                    prodDetailList = new ArrayList<String[]>(
                            prodTable.getPropertyCount());

                    for (int i = 0; i < prodTable.getPropertyCount(); i++) {
                        String[] prodDetails = new String[18];
                        SoapObject prductDetails = (SoapObject) prodTable
                                .getProperty(i);

                        Log.w("Log", "product Details object : "
                                + prductDetails.toString());

                        if (prductDetails.hasProperty("ItemIdentityCode")) {
                            prodDetails[0] = prductDetails.getProperty("ItemIdentityCode")
                                    .toString();
                        }
                        if (prductDetails.hasProperty("BrandName")) {
                            prodDetails[1] = prductDetails.getProperty(
                                    "BrandName").toString();
                        }
                        if (prductDetails.hasProperty("ItemCode")) {
                            prodDetails[2] = prductDetails.getProperty(
                                    "ItemCode").toString();
                        }

                        if (prductDetails.hasProperty("UnitName")) {
                            prodDetails[3] = prductDetails.getProperty(
                                    "UnitName").toString();
                        }

                        if (prductDetails.hasProperty("UnitSize")) {
                            prodDetails[4] = prductDetails.getProperty(
                                    "UnitSize").toString();
                        }

                        if (prductDetails.hasProperty("GenericName")) {
                            prodDetails[5] = prductDetails.getProperty(
                                    "GenericName").toString();
                        }

                        if (prductDetails.hasProperty("ItemCategoryName")) {
                            prodDetails[6] = prductDetails.getProperty(
                                    "ItemCategoryName").toString();
                        }

                        if (prductDetails.hasProperty("Description")) {
                            prodDetails[7] = prductDetails.getProperty(
                                    "Description").toString();
                        }

                        if (prductDetails.hasProperty("IntroducedDate")) {
                            prodDetails[8] = prductDetails.getProperty(
                                    "IntroducedDate").toString();
                        }


                        if (prductDetails.hasProperty("Principle")) {
                            prodDetails[9] = prductDetails.getProperty(
                                    "Principle").toString();
                        }

                        if (prductDetails.hasProperty("PurchasingPrice")) {
                            prodDetails[10] = prductDetails.getProperty(
                                    "PurchasingPrice").toString();
                        }

                        if (prductDetails.hasProperty("SellingPrice")) {
                            prodDetails[11] = prductDetails.getProperty(
                                    "SellingPrice").toString();
                        }

                        if (prductDetails.hasProperty("RetailPrice")) {
                            prodDetails[12] = prductDetails.getProperty(
                                    "RetailPrice").toString();
                        }

                        if (prductDetails.hasProperty("Force")) {
                            prodDetails[13] = prductDetails
                                    .getProperty("Force").toString();
                        }

                        if (prductDetails.hasProperty("Inactive")) {
                            prodDetails[14] = prductDetails.getProperty(
                                    "Inactive").toString();
                        }

                        if (prductDetails.hasProperty("VAT")) {
                            prodDetails[15] = prductDetails.getProperty("VAT")
                                    .toString();
                        }

                        if (prductDetails.hasProperty("TT")) {
                            prodDetails[16] = prductDetails.getProperty("TT")
                                    .toString();
                        }

                        if (prductDetails.hasProperty("ID")) {
                            prodDetails[17] = prductDetails.getProperty("ID")
                                    .toString();
                        }

                        prodDetailList.add(prodDetails);

                    }

                } else {
                    prodDetailList = new ArrayList<String[]>(0);

                    Log.w("test123", "Error: 0 size zzz");
                }
            } else {
                prodDetailList = new ArrayList<String[]>(0);
                Log.w("test123", "Error: 0 size xxx");
            }

        } catch (SocketException e) {
            prodDetailList =null;
        } catch (Exception exception) {
            prodDetailList = null;

        }

        // Log.w("test123", "list size : " + prodDetailList.size());

        return prodDetailList;
    }

    public ArrayList<String[]> getCustomerList(String deviceId, String repId, String maxRowId) throws SocketException {

        ArrayList<String[]> repDetailList = null;

        final String SOAP_ACTION = "http://tempuri.org/xmlGetCustomerUpdates";

        final String OPERATION_NAME = "xmlGetCustomerUpdates";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);

        Log.w("Log", "deviceId : " + deviceId);
        Log.w("Log", "repId : " + repId);
        Log.w("Log", "getCustomerListForRep ");

        PropertyInfo pi = new PropertyInfo();
        pi.setName("RepID");
        pi.setValue(repId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("DeviceID");
        pi2.setValue(deviceId);
        pi2.setType(String.class);
        request.addProperty(pi2);

        PropertyInfo pi3 = new PropertyInfo();
        pi3.setName("MaxID");
        pi3.setValue(maxRowId);
        pi3.setType(String.class);
        request.addProperty(pi3);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        Log.w("Log", "request toString : " + request.toString());

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS,TimeOut);
        SoapObject response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                SoapObject custTable = (SoapObject) response.getProperty(0);

                if (custTable != null && custTable.getPropertyCount() > 0) {

                    repDetailList = new ArrayList<String[]>(
                            custTable.getPropertyCount());

                    for (int i = 0; i < custTable.getPropertyCount(); i++) {
                        String[] customerDetails = new String[37];//test
                        SoapObject custDetails = (SoapObject) custTable
                                .getProperty(i);

                        Log.w("Log",
                                "repDetails object : " + custDetails.toString());

                        if (custDetails.hasProperty("GLB_PharmacyID")) {
                            customerDetails[0] = custDetails.getProperty(
                                    "GLB_PharmacyID").toString();
                            customerDetails[1] = customerDetails[0];
                        }
                        if (custDetails.hasProperty("GLB_PharmacyID")) {
                            customerDetails[1] = custDetails.getProperty(
                                    "GLB_PharmacyID").toString();
                        }
                        if (custDetails.hasProperty("DealerID")) {
                            if (custDetails.getProperty(
                                    "DealerID").toString() != "anyType{}") {
                                customerDetails[2] = custDetails.getProperty(
                                        "DealerID").toString();
                            } else {
                                customerDetails[2] = "";
                            }

                        }

                        if (custDetails.hasProperty("CompanyCode")) {

                            if (custDetails.getProperty(
                                    "CompanyCode").toString() != "anyType{}") {
                                customerDetails[3] = custDetails.getProperty(
                                        "CompanyCode").toString();
                            } else {
                                customerDetails[3] = "";
                            }

                        }

                        if (custDetails.hasProperty("Name")) {

                            if (custDetails.getProperty(
                                    "Name").toString() != "anyType{}") {
                                customerDetails[4] = custDetails
                                        .getProperty("Name").toString();
                            } else {
                                customerDetails[4] = "";
                            }

                        }

                        if (custDetails.hasProperty("Address")) {

                            if (custDetails.getProperty(
                                    "Address").toString() != "anyType{}") {
                                customerDetails[5] = custDetails.getProperty(
                                        "Address").toString();
                            } else {
                                customerDetails[5] = "";
                            }


                        }

                        if (custDetails.hasProperty("District")) {

                            if (custDetails.getProperty(
                                    "District").toString() != "anyType{}") {
                                customerDetails[6] = custDetails.getProperty(
                                        "District").toString();
                            } else {
                                customerDetails[6] = "";
                            }
                        }

                        if (custDetails.hasProperty("Area")) {

                            if (custDetails.getProperty(
                                    "Area").toString() != "anyType{}") {
                                customerDetails[7] = custDetails
                                        .getProperty("Area").toString();
                            } else {
                                customerDetails[7] = "";
                            }

                        }

                        if (custDetails.hasProperty("Town")) {

                            if (custDetails.getProperty(
                                    "Town").toString() != "anyType{}") {
                                customerDetails[8] = custDetails
                                        .getProperty("Town").toString();
                            } else {
                                customerDetails[8] = "";
                            }
                        }

                        if (custDetails.hasProperty("Telephone")) {

                            if (custDetails.getProperty(
                                    "Telephone").toString() != "anyType{}") {
                                customerDetails[9] = custDetails.getProperty(
                                        "Telephone").toString();
                            } else {
                                customerDetails[9] = "";
                            }
                        }

                        if (custDetails.hasProperty("Fax")) {

                            if (custDetails.getProperty(
                                    "Fax").toString() != "anyType{}") {
                                customerDetails[10] = custDetails
                                        .getProperty("Fax").toString();
                            } else {
                                customerDetails[10] = "";
                            }
                        }

                        if (custDetails.hasProperty("Email")) {

                            if (custDetails.getProperty(
                                    "Email").toString() != "anyType{}") {
                                customerDetails[11] = custDetails.getProperty(
                                        "Email").toString();
                            } else {
                                customerDetails[11] = "";
                            }
                        }

                        if (custDetails.hasProperty("CustomerStatus")) {

                            if (custDetails.getProperty(
                                    "CustomerStatus").toString() != "anyType{}") {
                                customerDetails[12] = custDetails.getProperty(
                                        "CustomerStatus").toString();
                            } else {
                                customerDetails[12] = "";
                            }
                        }

                        if (custDetails.hasProperty("CreditLimit")) {

                            if (custDetails.getProperty(
                                    "CreditLimit").toString() != "anyType{}") {
                                customerDetails[13] = custDetails.getProperty(
                                        "CreditLimit").toString();
                            } else {
                                customerDetails[13] = "";
                            }
                        }

                        if (custDetails.hasProperty("CreditExpiryDate")) {

                            if (custDetails.getProperty(
                                    "CreditExpiryDate").toString() != "anyType{}") {
                                customerDetails[14] = custDetails.getProperty(
                                        "CreditExpiryDate").toString();
                            } else {
                                customerDetails[14] = "";
                            }
                        }

                        if (custDetails.hasProperty("CreditDuration")) {

                            if (custDetails.getProperty(
                                    "CreditDuration").toString() != "anyType{}") {
                                customerDetails[15] = custDetails.getProperty(
                                        "CreditDuration").toString();
                            } else {
                                customerDetails[15] = "";
                            }
                        }

                        if (custDetails.hasProperty("VATNo")) {

                            if (custDetails.getProperty(
                                    "VATNo").toString() != "anyType{}") {
                                customerDetails[16] = custDetails.getProperty(
                                        "VATNo").toString();
                            } else {
                                customerDetails[16] = "";
                            }
                        }

                        if (custDetails.hasProperty("Status")) {

                            if (custDetails.getProperty(
                                    "Status").toString() != "anyType{}") {
                                customerDetails[17] = custDetails.getProperty(
                                        "Status").toString();
                            } else {
                                customerDetails[17] = "";
                            }
                        }

                        if (custDetails.hasProperty("temp")) {

                            if (custDetails.getProperty(
                                    "temp").toString() != "anyType{}") {
                                customerDetails[18] = custDetails.getProperty(
                                        "temp").toString();
                            } else {
                                customerDetails[18] = "";
                            }
                        }

                        if (custDetails.hasProperty("CustomerNo")) {

                            if (custDetails.getProperty(
                                    "CustomerNo").toString() != "anyType{}") {
                                customerDetails[19] = custDetails.getProperty(
                                        "CustomerNo").toString();
                            } else {
                                customerDetails[19] = "";
                            }
                        }

                        if (custDetails.hasProperty("Web")) {

                            if (custDetails.getProperty(
                                    "Web").toString() != "anyType{}") {
                                customerDetails[20] = custDetails
                                        .getProperty("Web").toString();
                            } else {
                                customerDetails[20] = "";
                            }
                        }

                        if (custDetails.hasProperty("BrNo")) {

                            if (custDetails.getProperty(
                                    "BrNo").toString() != "anyType{}") {
                                customerDetails[21] = custDetails.getProperty(
                                        "BrNo").toString();
                            } else {
                                customerDetails[21] = "";
                            }
                        }

                        if (custDetails.hasProperty("OwnerContactNo")) {

                            if (custDetails.getProperty(
                                    "OwnerContactNo").toString() != "anyType{}") {
                                customerDetails[22] = custDetails.getProperty(
                                        "OwnerContactNo").toString();
                            } else {
                                customerDetails[22] = "";
                            }
                        }

                        if (custDetails.hasProperty("PharmacyRegNo")) {

                            if (custDetails.getProperty(
                                    "PharmacyRegNo").toString() != "anyType{}") {
                                customerDetails[23] = custDetails.getProperty(
                                        "PharmacyRegNo").toString();
                            } else {
                                customerDetails[23] = "";
                            }
                        }

                        if (custDetails.hasProperty("OwnersWifesBDay")) {

                            if (custDetails.getProperty(
                                    "OwnersWifesBDay").toString() != "anyType{}") {
                                customerDetails[24] = custDetails.getProperty(
                                        "OwnersWifesBDay").toString();
                            } else {
                                customerDetails[24] = "";
                            }
                        }

                        if (custDetails.hasProperty("PharmacistName")) {

                            if (custDetails.getProperty(
                                    "PharmacistName").toString() != "anyType{}") {
                                customerDetails[25] = custDetails.getProperty(
                                        "PharmacistName").toString();
                            } else {
                                customerDetails[25] = "";
                            }
                        }

                        if (custDetails.hasProperty("PuchasingOfficer")) {

                            if (custDetails.getProperty(
                                    "PuchasingOfficer").toString() != "anyType{}") {
                                customerDetails[26] = custDetails.getProperty(
                                        "PuchasingOfficer").toString();
                            } else {
                                customerDetails[26] = "";
                            }
                        }

                        if (custDetails.hasProperty("NoOfStaff")) {

                            if (custDetails.getProperty(
                                    "NoOfStaff").toString() != "anyType{}") {
                                customerDetails[27] = custDetails.getProperty(
                                        "NoOfStaff").toString();
                            } else {
                                customerDetails[27] = "";
                            }
                        }

                        if (custDetails.hasProperty("Latitude")) {

                            if (custDetails.getProperty(
                                    "Latitude").toString() != "anyType{}") {
                                customerDetails[28] = custDetails.getProperty(
                                        "Latitude").toString();
                            } else {
                                customerDetails[28] = "";
                            }
                        }

                        if (custDetails.hasProperty("Longitude")) {

                            if (custDetails.getProperty(
                                    "Longitude").toString() != "anyType{}") {
                                customerDetails[29] = custDetails.getProperty(
                                        "Longitude").toString();
                            } else {
                                customerDetails[29] = "";
                            }
                        }

                        if (custDetails.hasProperty("JobID")) {

                            if (custDetails.getProperty(
                                    "JobID").toString() != "anyType{}") {
                                customerDetails[30] = custDetails.getProperty(
                                        "JobID").toString();
                            } else {
                                customerDetails[30] = "";
                            }
                        }

                        if (custDetails.hasProperty("PrimaryImageId")) {

                            if (custDetails.getProperty(
                                    "PrimaryImageId").toString() != "anyType{}") {
                                customerDetails[31] = custDetails.getProperty(
                                        "PrimaryImageId").toString();
                            } else {
                                customerDetails[31] = "";
                            }
                        }

                        if (custDetails.hasProperty("CusImage")) {

                            if (custDetails.getProperty(
                                    "CusImage").toString() != "anyType{}") {
                                customerDetails[32] = custDetails.getProperty(
                                        "CusImage").toString();
                            } else {
                                customerDetails[32] = "";
                            }
                        }
                        if (custDetails.hasProperty("CurrentCredit")) {//test

                            System.out.println("CurrentCredit ji :"+custDetails.getProperty(
                                    "CurrentCredit").toString());
                            if (custDetails.getProperty(
                                    "CurrentCredit").toString() != "anyType{}") {
                                customerDetails[33] = custDetails.getProperty(
                                        "CurrentCredit").toString();
                            } else {
                                customerDetails[33] = "";
                            }
                        }if (custDetails.hasProperty("IsInvoiceAllowed")) {//test
                            System.out.println("IsInvoiceAllowed ji :"+custDetails.getProperty(
                                    "IsInvoiceAllowed").toString());

                            if (custDetails.getProperty("IsInvoiceAllowed").toString() != "anyType{}") {
                                customerDetails[34] = custDetails.getProperty("IsInvoiceAllowed").toString();
                            } else {
                                customerDetails[34] = "0";
                            }
                        }


                        if (custDetails.hasProperty("MaxInvoiceCount")) {//test

                            if (custDetails.getProperty("MaxInvoiceCount").toString() != "anyType{}") {
                                customerDetails[35] = custDetails.getProperty("MaxInvoiceCount").toString();
                            } else {
                                customerDetails[35] = "0";
                            }
                        }if (custDetails.hasProperty("CustomerBlocked")) {//test

                            if (custDetails.getProperty("CustomerBlocked").toString() != "anyType{}") {
                                customerDetails[36] = custDetails.getProperty("CustomerBlocked").toString();
                            } else {
                                customerDetails[36] = "0";
                            }
                        }






                        repDetailList.add(customerDetails);

                    }

                } else {
                    repDetailList = new ArrayList<String[]>(0);

                    Log.w("test123", "Error: 0 size zzz");
                }
            } else {
                repDetailList = new ArrayList<String[]>(0);
                Log.w("test123", "Error: 0 size xxx");
            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            repDetailList = null;
            Log.w("test123", "Error: " + exception.toString());
        }

//		Log.w("test123", "list size : " + repDetailList.size());

        return repDetailList;
    }


    public ArrayList<String[]> Download_DEL_Outstanding(String deviceId, String repId) throws SocketException{

        ArrayList<String[]> repStoreDetailList = null;

        final String SOAP_ACTION = "http://tempuri.org/xmlUploadOutstanding";

        final String OPERATION_NAME = "xmlUploadOutstanding";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);

        // need to remove below line its test purpose only

        Log.w("Log", "deviceId : " + deviceId);
        Log.w("Log", "repId : " + repId);
        // Log.w("Log", "maxRowID : " + maxRowID);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("RepID");
        pi.setValue(repId);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                SoapObject repStoreTable = (SoapObject) response.getProperty(0);

                if (repStoreTable != null
                        && repStoreTable.getPropertyCount() > 0) {

                    repStoreDetailList = new ArrayList<String[]>(
                            repStoreTable.getPropertyCount());

                    for (int i = 0; i < repStoreTable.getPropertyCount(); i++) {
                        String[] prodDetails = new String[15];
                        SoapObject prductDetails = (SoapObject) repStoreTable
                                .getProperty(i);

                        Log.w("Log", "product Details object : "
                                + prductDetails.toString());

                        if (prductDetails.hasProperty("RowID")) {
                            prodDetails[0] = prductDetails.getProperty("RowID")
                                    .toString();
                        }
                        if (prductDetails.hasProperty("DealerCode")) {
                            prodDetails[1] = prductDetails.getProperty("DealerCode")
                                    .toString();
                        }
                        if (prductDetails.hasProperty("DealerName")) {
                            prodDetails[2] = prductDetails.getProperty("DealerName")
                                    .toString();
                        }

                        if (prductDetails.hasProperty("SalesRepID")) {
                            prodDetails[3] = prductDetails.getProperty("SalesRepID")
                                    .toString();
                        }


                        if (prductDetails.hasProperty("SalesRep")) {
                            prodDetails[4] = prductDetails.getProperty(
                                    "SalesRep").toString();
                        }

                        if (prductDetails.hasProperty("CustomerNo")) {
                            prodDetails[5] = prductDetails.getProperty(
                                    "CustomerNo").toString();
                        }

                        if (prductDetails.hasProperty("CustomerName")) {
                            prodDetails[6] = prductDetails.getProperty(
                                    "CustomerName").toString();
                        }
                        if (prductDetails.hasProperty("InvoiceNo")) {
                            prodDetails[7] = prductDetails.getProperty(
                                    "InvoiceNo").toString();
                        }

                        if (prductDetails.hasProperty("InvoiceDate")) {
                            prodDetails[8] = prductDetails.getProperty(
                                    "InvoiceDate").toString();
                        }
                        if (prductDetails.hasProperty("TotalAmount")) {
                            prodDetails[9] = prductDetails.getProperty(
                                    "TotalAmount").toString();
                        }
                        if (prductDetails.hasProperty("CreditAmount")) {
                            prodDetails[10] = prductDetails.getProperty(
                                    "CreditAmount").toString();
                        }

                        if (prductDetails.hasProperty("CreditDuration")) {
                            prodDetails[11] = prductDetails.getProperty(
                                    "CreditDuration").toString();
                        }
                        if (prductDetails.hasProperty("NewRepID")) {
                            prodDetails[12] = prductDetails.getProperty(
                                    "NewRepID").toString();
                        }

                        if (prductDetails.hasProperty("NewRepName")) {
                            prodDetails[13] = prductDetails.getProperty(
                                    "NewRepName").toString();
                        }
                        if (prductDetails.hasProperty("JobNo")) {
                            prodDetails[14] = prductDetails.getProperty(
                                    "JobNo").toString();
                        }


                        repStoreDetailList.add(prodDetails);

                        Log.w("Log", "info : " + prodDetails[0]);
                        Log.w("Log", "info : " + prodDetails[1]);
                        Log.w("Log", "info : " + prodDetails[2]);
                        Log.w("Log", "info : " + prodDetails[3]);
                        Log.w("Log", "info : " + prodDetails[4]);
                        Log.w("Log", "info : " + prodDetails[5]);

                    }

                } else {
                    repStoreDetailList = new ArrayList<String[]>(0);

                    Log.w("test123", "Error repStoreDetailList: 0 size zzz");
                }
            } else {
                repStoreDetailList = new ArrayList<String[]>(0);
                Log.w("test123", "Error repStoreDetailList: 0 size xxx");
            }

        } catch (SocketException e) {
            try {
                throw new SocketException(e.toString());
            } catch (SocketException e1) {
                e1.printStackTrace();
            }
        } catch (Exception exception) {
            repStoreDetailList = null;
            Log.w("test123",
                    "Error repStoreDetailList: " + exception.toString());
        }

        // Log.w("test123", "list size repStoreDetailList : " +
        // repStoreDetailList.size());

        return repStoreDetailList;

    }


    public String uploadCollectionNoteTask(String deviceId, String repId, String[] custDetails) throws SocketException {

        String repStoreDetailList = "";

        final String SOAP_ACTION = "http://tempuri.org/xmlSetCollectionNote";

        final String OPERATION_NAME = "xmlSetCollectionNote";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);

        Log.w("Log", "deviceId : " + deviceId);
        Log.w("Log", "repId : " + repId);

      /*  PropertyInfo pi = new PropertyInfo();
        pi.setName("DeviceID");
        pi.setValue(deviceId);
        pi.setType(String.class);
        request.addProperty(pi);*/

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("RepID");
        pi2.setValue(repId);
        pi2.setType(String.class);
        request.addProperty(pi2);

        SoapObject dataset = new SoapObject(WSDL_TARGET_NAMESPACE, "NewDataSet");

        Log.w("Log", "rtnProducts 0 : " + custDetails[0]);
        Log.w("Log", "rtnProducts 1 : " + custDetails[1]);
        Log.w("Log", "rtnProducts 2 : " + custDetails[2]);
        Log.w("Log", "rtnProducts 3 : " + custDetails[3]);
        Log.w("Log", "rtnProducts 4 : " + custDetails[4]);
        Log.w("Log", "rtnProducts 5 : " + custDetails[5]);
        Log.w("Log", "rtnProducts 6 : " + custDetails[6]);
        Log.w("Log", "rtnProducts 7 : " + custDetails[7]);
        Log.w("Log", "rtnProducts 8 : " + custDetails[8]);
        Log.w("Log", "rtnProducts 9 : " + custDetails[9]);


        SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE, "Table");
        table.addProperty("KEY_ROW_ID", custDetails[0]);
        table.addProperty("COLLECTION_NOTE_NO", custDetails[1]);
        table.addProperty("REP_NO", custDetails[2]);
        table.addProperty("CUSTOMER_NAME", custDetails[3]);
        table.addProperty("CURRENT_OUTSTANDING", custDetails[4]);
        table.addProperty("INVOICE_NO", custDetails[5]);
        table.addProperty("CREDIT_AMOUNT", custDetails[6]);
        table.addProperty("PAYMENT_TYPE", custDetails[7]);
        table.addProperty("CASH_AMOUNT", custDetails[8]);
        table.addProperty("CHEQUE_AMOUNT", custDetails[9]);
        table.addProperty("CHEQUE_NUMBER", custDetails[10]);
        table.addProperty("BANK_NAME", custDetails[11]);
        table.addProperty("BRANCH", custDetails[12]);
        table.addProperty("COLLECT_DATE", custDetails[13]);
        table.addProperty("REALIZE_DATE", custDetails[14]);
        table.addProperty("PAYMENTTYPE_CODE", custDetails[16]);
        table.addProperty("BRANCH_CODE", custDetails[17]);
        table.addProperty("CUSTOMER_CODE", custDetails[18]);
        table.addProperty("TYPE", custDetails[19]);
        table.addProperty("CHEQUE_IMAGE", custDetails[15]);


        dataset.addSoapObject(table);

        SoapObject invoiceData = new SoapObject(WSDL_TARGET_NAMESPACE,
                "CollectionNote");
        invoiceData.addSoapObject(dataset);
        request.addSoapObject(invoiceData);

        Log.w("Log", "request toString : " + request.toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        Log.i("-coll->",request.toString());
        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;
        try {

            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.bodyIn;

            Log.w("test123", "response : " + response.toString());

            if (response != null && response.getPropertyCount() > 0) {
                Log.w("Log", "response Str : " + response.toString());

                repStoreDetailList = response.getProperty("xmlSetCollectionNoteResult").toString();

            } else {
                repStoreDetailList = "error";
                Log.w("test123", "Error repStoreDetailList: 0 size xxx");
            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {

            Log.w("test123", "Error repStoreDetailList: " + exception);
        }

        return repStoreDetailList;

    }


    public ArrayList<String[]> Download_Payment_Type(String deviceId, String repId)
            throws SocketException{

        ArrayList<String[]> repStoreDetailList = null;

        final String SOAP_ACTION = "http://tempuri.org/xmlGetPaymentType";

        final String OPERATION_NAME = "xmlGetPaymentType";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);

        // need to remove below line its test purpose only

        Log.w("Log", "deviceId : " + deviceId);
        Log.w("Log", "repId : " + repId);
        // Log.w("Log", "maxRowID : " + maxRowID);

     /*   PropertyInfo pi = new PropertyInfo();
        pi.setName("RepID");
        pi.setValue(repId);
        pi.setType(String.class);
        request.addProperty(pi);*/

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                SoapObject repStoreTable = (SoapObject) response.getProperty(0);

                if (repStoreTable != null
                        && repStoreTable.getPropertyCount() > 0) {

                    repStoreDetailList = new ArrayList<String[]>(
                            repStoreTable.getPropertyCount());


                    for (int i = 0; i < repStoreTable.getPropertyCount(); i++) {
                        String[] prodDetails = new String[4];
                        SoapObject prductDetails = (SoapObject) repStoreTable
                                .getProperty(i);

                        Log.w("Log", "product Details object : "
                                + prductDetails.toString());

                        if (prductDetails.hasProperty("TypeID")) {
                            prodDetails[0] = prductDetails.getProperty("TypeID")
                                    .toString();
                        }
                        if (prductDetails.hasProperty("PaymentType")) {
                            prodDetails[1] = prductDetails.getProperty("PaymentType")
                                    .toString();
                        }
                        if (prductDetails.hasProperty("IsActive")) {
                            prodDetails[2] = prductDetails.getProperty("IsActive")
                                    .toString();
                        }

                        if (prductDetails.hasProperty("ModifyDate")) {
                            prodDetails[3] = prductDetails.getProperty("ModifyDate")
                                    .toString();
                        }


                        repStoreDetailList.add(prodDetails);

                        Log.w("Log", "info : " + prodDetails[0]);
                        Log.w("Log", "info : " + prodDetails[1]);
                        Log.w("Log", "info : " + prodDetails[2]);
                        Log.w("Log", "info : " + prodDetails[3]);


                    }

                } else {
                    repStoreDetailList = new ArrayList<String[]>(0);

                    Log.w("test123", "Error repStoreDetailList: 0 size zzz");
                }
            } else {
                repStoreDetailList = new ArrayList<String[]>(0);
                Log.w("test123", "Error repStoreDetailList: 0 size xxx");
            }

        } catch (SocketException e) {
            try {
                throw new SocketException(e.toString());
            } catch (SocketException e1) {
                e1.printStackTrace();
            }
        } catch (Exception exception) {
            repStoreDetailList = null;
            Log.w("test123",
                    "Error repStoreDetailList: " + exception.toString());
        }


        return repStoreDetailList;

    }


    public ArrayList<String[]> Download_Branch(String deviceId, String repId) throws SocketException{

        ArrayList<String[]> repStoreDetailList = null;

        final String SOAP_ACTION = "http://tempuri.org/xmlGetBranch";

        final String OPERATION_NAME = "xmlGetBranch";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);

        // need to remove below line its test purpose only

        Log.w("Log", "deviceId : " + deviceId);
        Log.w("Log", "repId : " + repId);
        // Log.w("Log", "maxRowID : " + maxRowID);

     /*   PropertyInfo pi = new PropertyInfo();
        pi.setName("RepID");
        pi.setValue(repId);
        pi.setType(String.class);
        request.addProperty(pi);*/

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                SoapObject repStoreTable = (SoapObject) response.getProperty(0);

                if (repStoreTable != null
                        && repStoreTable.getPropertyCount() > 0) {

                    repStoreDetailList = new ArrayList<String[]>(
                            repStoreTable.getPropertyCount());


                    for (int i = 0; i < repStoreTable.getPropertyCount(); i++) {
                        String[] prodDetails = new String[6];
                        SoapObject prductDetails = (SoapObject) repStoreTable
                                .getProperty(i);

                        Log.w("Log", "product Details object : "
                                + prductDetails.toString());


                        if (prductDetails.hasProperty("ID")) {
                            prodDetails[0] = prductDetails.getProperty("ID")
                                    .toString();
                        }
                        if (prductDetails.hasProperty("DistrictID")) {
                            prodDetails[1] = prductDetails.getProperty("DistrictID")
                                    .toString();
                        }
                        if (prductDetails.hasProperty("Town")) {
                            prodDetails[2] = prductDetails.getProperty("Town")
                                    .toString();
                        }
                        if (prductDetails.hasProperty("IsActive")) {
                            prodDetails[3] = prductDetails.getProperty("IsActive")
                                    .toString();
                        }

                        if (prductDetails.hasProperty("ModifyDate")) {
                            prodDetails[4] = prductDetails.getProperty("ModifyDate")
                                    .toString();
                        }
                        if (prductDetails.hasProperty("District")) {
                            prodDetails[5] = prductDetails.getProperty("District")
                                    .toString();
                        }


                        repStoreDetailList.add(prodDetails);

                        Log.w("Log", "info : " + prodDetails[0]);
                        Log.w("Log", "info : " + prodDetails[1]);
                        Log.w("Log", "info : " + prodDetails[2]);
                        Log.w("Log", "info : " + prodDetails[3]);


                    }

                } else {
                    repStoreDetailList = new ArrayList<String[]>(0);

                    Log.w("test123", "Error repStoreDetailList: 0 size zzz");
                }
            } else {
                repStoreDetailList = new ArrayList<String[]>(0);
                Log.w("test123", "Error repStoreDetailList: 0 size xxx");
            }

        } catch (SocketException e) {
            try {
                throw new SocketException(e.toString());
            } catch (SocketException e1) {
                e1.printStackTrace();
            }
        } catch (Exception exception) {
            repStoreDetailList = null;
            Log.w("test123",
                    "Error repStoreDetailList: " + exception.toString());
        }
        return repStoreDetailList;

    }


    public ArrayList<String[]> Download_Master_Banks(String deviceId, String repId)
            throws SocketException {

        ArrayList<String[]> repStoreDetailList = null;

        final String SOAP_ACTION = "http://tempuri.org/xmlGetBank";

        final String OPERATION_NAME = "xmlGetBank";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);

        // need to remove below line its test purpose only

        Log.w("Log", "deviceId : " + deviceId);
        Log.w("Log", "repId : " + repId);
        // Log.w("Log", "maxRowID : " + maxRowID);

     /*   PropertyInfo pi = new PropertyInfo();
        pi.setName("RepID");
        pi.setValue(repId);
        pi.setType(String.class);
        request.addProperty(pi);*/

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                SoapObject repStoreTable = (SoapObject) response.getProperty(0);

                if (repStoreTable != null && repStoreTable.getPropertyCount() > 0) {

                    repStoreDetailList = new ArrayList<String[]>(
                            repStoreTable.getPropertyCount());


                    for (int i = 0; i < repStoreTable.getPropertyCount(); i++) {
                        String[] prodDetails = new String[4];
                        SoapObject prductDetails = (SoapObject) repStoreTable
                                .getProperty(i);

                        Log.w("Log", "product Details object : "
                                + prductDetails.toString());


                        if (prductDetails.hasProperty("ID")) {
                            prodDetails[0] = prductDetails.getProperty("ID")
                                    .toString();
                        }
                        if (prductDetails.hasProperty("BankName")) {
                            prodDetails[1] = prductDetails.getProperty("BankName")
                                    .toString();
                        }
                        if (prductDetails.hasProperty("IsActive")) {
                            prodDetails[2] = prductDetails.getProperty("IsActive")
                                    .toString();
                        }

                        if (prductDetails.hasProperty("ModifyDate")) {
                            prodDetails[3] = prductDetails.getProperty("ModifyDate")
                                    .toString();
                        }


                        repStoreDetailList.add(prodDetails);

                        Log.w("Log", "info : " + prodDetails[0]);
                        Log.w("Log", "info : " + prodDetails[1]);
                        Log.w("Log", "info : " + prodDetails[2]);
                        Log.w("Log", "info : " + prodDetails[3]);


                    }

                } else {
                    repStoreDetailList = new ArrayList<String[]>(0);

                    Log.w("test123", "Error repStoreDetailList: 0 size zzz");
                }
            } else {
                repStoreDetailList = new ArrayList<String[]>(0);
                Log.w("test123", "Error repStoreDetailList: 0 size xxx");
            }

        } catch (SocketException e) {
            try {
                throw new SocketException(e.toString());
            } catch (SocketException e1) {
                e1.printStackTrace();
            }
        } catch (Exception exception) {
            repStoreDetailList = null;
            Log.w("test123",
                    "Error repStoreDetailList: " + exception.toString());
        }

        // Log.w("test123", "list size repStoreDetailList : " +
        // repStoreDetailList.size());

        return repStoreDetailList;

    }


    public String uploadInvoiceOutstandingDetails(String deviceId, String repId, String[] invoiceOutstanding) throws SocketException {

        String returnString = "";

        final String SOAP_ACTION = "http://tempuri.org/xmlSetOutstanding";

        final String OPERATION_NAME = "xmlSetOutstanding";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);

        Log.w("Log", "deviceId : " + deviceId);
        Log.w("Log", "repId : " + repId);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("DeviceID");
        pi.setValue(deviceId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("RepID");
        pi2.setValue(repId);
        pi2.setType(String.class);
        request.addProperty(pi2);

        SoapObject dataset = new SoapObject(WSDL_TARGET_NAMESPACE, "NewDataSet");


        SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE, "Table");

        table.addProperty("InvoiceNo", invoiceOutstanding[0]);
        table.addProperty("CustomerNo", invoiceOutstanding[1]);
        table.addProperty("Date", invoiceOutstanding[2]);
        table.addProperty("TotalAmount", invoiceOutstanding[3]);
        table.addProperty("CreditAmount", invoiceOutstanding[4]);
        table.addProperty("CreditDuration", invoiceOutstanding[5]);


        dataset.addSoapObject(table);

        SoapObject invoiceData = new SoapObject(WSDL_TARGET_NAMESPACE,
                "OutstandingData");
        invoiceData.addSoapObject(dataset);
        request.addSoapObject(invoiceData);

        Log.w("Log", "request toString : " + request.toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        Log.i("out -ou->",request.toString());
        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS,TimeOut);
        SoapObject response;
        try {

            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.bodyIn;

            Log.w("test123", "response : " + response.toString());

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                returnString = response.getProperty(
                        "xmlSetOutstandingResult").toString();

            } else {
                returnString = "error";
                Log.w("test123", "Error repStoreDetailList: 0 size xxx");
            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            returnString = "error";
            Log.w("test123",
                    "Error repStoreDetailList: " + exception.toString());

            Log.w("test123", "Error repStoreDetailList: " + exception);
        }

        Log.w("test123", "list size repStoreDetailList : " + returnString);

        return returnString;

    }

    public String uploadInvoiceChequeDetails(String deviceId, String repId, String[] invoiceCheque) throws SocketException {

        String returnString = "";

        final String SOAP_ACTION = "http://tempuri.org/xmlSetCheques";

        final String OPERATION_NAME = "xmlSetCheques";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);

        Log.w("Log", "deviceId : " + deviceId);
        Log.w("Log", "repId : " + repId);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("DeviceID");
        pi.setValue(deviceId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("RepID");
        pi2.setValue(repId);
        pi2.setType(String.class);
        request.addProperty(pi2);

        SoapObject dataset = new SoapObject(WSDL_TARGET_NAMESPACE, "NewDataSet");

        SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE, "Table");

        table.addProperty("ChequeNo", invoiceCheque[0]);
        table.addProperty("CustomerNo", invoiceCheque[1]);
        table.addProperty("CollectDate", invoiceCheque[2]);
        table.addProperty("ReleaseDate", invoiceCheque[3]);
        table.addProperty("ChequeAmount", invoiceCheque[4]);

        dataset.addSoapObject(table);

        SoapObject invoiceData = new SoapObject(WSDL_TARGET_NAMESPACE,
                "ChequeData");
        invoiceData.addSoapObject(dataset);
        request.addSoapObject(invoiceData);

        Log.w("Log", "request toString : " + request.toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;
        try {

            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.bodyIn;

            Log.w("test123", "response : " + response.toString());

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                returnString = response.getProperty(
                        "xmlSetChequesResult").toString();

            } else {
                returnString = "error";
                Log.w("test123", "Error repStoreDetailList: 0 size xxx");
            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            returnString = "error";
            Log.w("test123",
                    "Error repStoreDetailList: " + exception.toString());

            Log.w("test123", "Error repStoreDetailList: " + exception);
        }

        Log.w("test123", "list size repStoreDetailList : " + returnString);

        return returnString;

    }

    public ArrayList<String[]> getGetUnloadingStatus(String deviceId, String repId, String rowIds) throws SocketException {

        ArrayList<String[]> rowIdDetailList = null;

        final String SOAP_ACTION = "http://tempuri.org/xmlGetUnloadingStatus";

        final String OPERATION_NAME = "xmlGetUnloadingStatus";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);

        Log.w("Log", "deviceId : " + deviceId);
        Log.w("Log", "repId : " + repId);
        Log.w("Log", "getGetUnloadingStatus ");

        PropertyInfo pi = new PropertyInfo();
        pi.setName("RepID");
        pi.setValue(repId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("DeviceID");
        pi2.setValue(deviceId);
        pi2.setType(String.class);
        request.addProperty(pi2);

        PropertyInfo pi3 = new PropertyInfo();
        pi3.setName("RowIDs");
        pi3.setValue(rowIds);
        pi3.setType(String.class);
        request.addProperty(pi3);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        Log.w("Log", "request toString : " + request.toString());

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                SoapObject rowIdTable = (SoapObject) response.getProperty(0);

                if (rowIdTable != null && rowIdTable.getPropertyCount() > 0) {

                    rowIdDetailList = new ArrayList<String[]>(
                            rowIdTable.getPropertyCount());

                    for (int i = 0; i < rowIdTable.getPropertyCount(); i++) {
                        String[] unloadDetails = new String[6];
                        SoapObject custDetails = (SoapObject) rowIdTable
                                .getProperty(i);

                        Log.w("Log",
                                "repDetails object : " + custDetails.toString());

                        if (custDetails.hasProperty("ID")) {
                            unloadDetails[0] = custDetails.getProperty(
                                    "ID").toString();
                        }
                        if (custDetails.hasProperty("Code")) {
                            unloadDetails[1] = custDetails.getProperty(
                                    "Code").toString();
                        }
                        if (custDetails.hasProperty("UnloadQty")) {
                            if (custDetails.getProperty(
                                    "UnloadQty").toString() != "anyType{}") {
                                unloadDetails[2] = custDetails.getProperty(
                                        "UnloadQty").toString();
                            } else {
                                unloadDetails[2] = "";
                            }

                        }

                        if (custDetails.hasProperty("ExpireDate")) {

                            if (custDetails.getProperty(
                                    "ExpireDate").toString() != "anyType{}") {
                                unloadDetails[3] = custDetails.getProperty(
                                        "ExpireDate").toString();
                            } else {
                                unloadDetails[3] = "";
                            }

                        }

                        if (custDetails.hasProperty("BatchNo")) {

                            if (custDetails.getProperty(
                                    "BatchNo").toString() != "anyType{}") {
                                unloadDetails[4] = custDetails
                                        .getProperty("BatchNo").toString();
                            } else {
                                unloadDetails[4] = "";
                            }

                        }

                        if (custDetails.hasProperty("Status")) {

                            if (custDetails.getProperty(
                                    "Status").toString() != "anyType{}") {
                                unloadDetails[5] = custDetails.getProperty(
                                        "Status").toString();
                            } else {
                                unloadDetails[5] = "";
                            }


                        }


                        rowIdDetailList.add(unloadDetails);

                    }

                } else {
                    rowIdDetailList = new ArrayList<String[]>(0);

                    Log.w("test123", "Error: 0 size zzz");
                }
            } else {
                rowIdDetailList = new ArrayList<String[]>(0);
                Log.w("test123", "Error: 0 size xxx");
            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            rowIdDetailList = null;
            Log.w("test123", "Error: " + exception.toString());
        }

//		Log.w("test123", "list size : " + repDetailList.size());

        return rowIdDetailList;
    }

    public String SetUnloadingDetails(String deviceId, String repId, String[] unloadDetails) throws SocketException {

        String returnString = "";

        final String SOAP_ACTION = "http://tempuri.org/xmlSetUnloading";

        final String OPERATION_NAME = "xmlSetUnloading";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);

        Log.w("Log", "deviceId : SetUnloadingDetails " + deviceId);
        Log.w("Log", "deviceId : " + deviceId);
        Log.w("Log", "repId : " + repId);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("DeviceID");
        pi.setValue(deviceId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("RepID");
        pi2.setValue(repId);
        pi2.setType(String.class);
        request.addProperty(pi2);

        SoapObject dataset = new SoapObject(WSDL_TARGET_NAMESPACE, "NewDataSet");

        SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE, "Table");

        table.addProperty("ID", unloadDetails[0]);
        table.addProperty("Code", unloadDetails[1]);
        table.addProperty("UnloadQty", unloadDetails[2]);
        table.addProperty("ExpireDate", unloadDetails[3]);
        table.addProperty("BatchNo", unloadDetails[4]);

        dataset.addSoapObject(table);

        SoapObject invoiceData = new SoapObject(WSDL_TARGET_NAMESPACE,
                "UnloadingData");
        invoiceData.addSoapObject(dataset);
        request.addSoapObject(invoiceData);

        Log.w("Log", "request toString : " + request.toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS,TimeOut);
        SoapObject response;
        try {

            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.bodyIn;

            Log.w("test123", "response : " + response.toString());

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

				/*returnString = response.getProperty(
                        "xmlSetUnloadingResult").toString();*/
                returnString = response.getProperty(
                        "xmlSetUnloadingResult").toString();

            } else {
                returnString = "error";
                Log.w("test123", "Error repStoreDetailList: 0 size xxx");
            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            returnString = "error";
            Log.w("test123",
                    "Error repStoreDetailList: " + exception.toString());

            Log.w("test123", "Error repStoreDetailList: " + exception);
        }

        Log.w("test123", "list size repStoreDetailList : " + returnString);

        return returnString;

    }

    public String uploadAttendence(String[] GPSDetails) throws SocketException {

        String returnString = "";

        final String SOAP_ACTION = "http://tempuri.org/SaveRepAttendence";

        final String OPERATION_NAME = "SaveRepAttendence";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);


      /*  PropertyInfo pi = new PropertyInfo();
        pi.setName("DeviceID");
        pi.setValue(deviceId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("RepID");
        pi2.setValue(repId);
        pi2.setType(String.class);
        request.addProperty(pi2);*/

        SoapObject dataset = new SoapObject(WSDL_TARGET_NAMESPACE, "NewDataSet");


        SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE, "Table");
        table.addProperty("TabSideID", GPSDetails[0]);
        table.addProperty("DeviceID", GPSDetails[1]);
        table.addProperty("Latitude", GPSDetails[3]);
        table.addProperty("Longitude", GPSDetails[4]);
        table.addProperty("Comments", GPSDetails[6]);
        table.addProperty("InOuttime", GPSDetails[2]);
        table.addProperty("Location", GPSDetails[5]);
        table.addProperty("GetDate", GPSDetails[8]);
        table.addProperty("Flag", GPSDetails[7]);
        dataset.addSoapObject(table);

        SoapObject GPSData = new SoapObject(WSDL_TARGET_NAMESPACE,
                "attendence");
        GPSData.addSoapObject(dataset);
        request.addSoapObject(GPSData);

        Log.w("Log", "request toString :         " + request.toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;
        try {

            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.bodyIn;

            Log.w("test123", "response : " + response.toString());

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                returnString = response.getProperty(
                        "SaveRepAttendenceResult").toString();

            } else {
                returnString = "error";
                Log.w("test123", "Error repStoreDetailList: 0 size xxx");
            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            returnString = "error";
            Log.w("test123",
                    "Error GPSDetailList: " + exception.toString());

            Log.w("test123", "Error GPSDetailList: " + exception);
        }

        Log.w("test123", "list size GPSDetailList : " + returnString);

        return returnString;

    }

    public String uploadGPS(String[] GPSDetails) throws SocketException {

        String returnString = "";

        final String SOAP_ACTION = "http://tempuri.org/SaveGPSLocation";

        final String OPERATION_NAME = "SaveGPSLocation";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);


      /*  PropertyInfo pi = new PropertyInfo();
        pi.setName("DeviceID");
        pi.setValue(deviceId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("RepID");
        pi2.setValue(repId);
        pi2.setType(String.class);
        request.addProperty(pi2);*/

        SoapObject dataset = new SoapObject(WSDL_TARGET_NAMESPACE, "NewDataSet");


        SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE, "Table");
        table.addProperty("TabRowID", GPSDetails[0]);
        table.addProperty("DeviceID", GPSDetails[1]);
        table.addProperty("Latitude", GPSDetails[2]);
        table.addProperty("Longitude", GPSDetails[3]);
        table.addProperty("GatDate", GPSDetails[4]);


        dataset.addSoapObject(table);

        SoapObject GPSData = new SoapObject(WSDL_TARGET_NAMESPACE,
                "gpslocation");
        GPSData.addSoapObject(dataset);
        request.addSoapObject(GPSData);

        Log.w("Log", "request toString :         " + request.toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;
        try {

            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.bodyIn;

            Log.w("test123", "response : " + response.toString());

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                returnString = response.getProperty(
                        "SaveGPSLocationResult").toString();

            } else {
                returnString = "error";
                Log.w("test123", "Error repStoreDetailList: 0 size xxx");
            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            returnString = "error";
            Log.w("test123",
                    "Error GPSDetailList: " + exception.toString());

            Log.w("test123", "Error GPSDetailList: " + exception);
        }

        Log.w("test123", "list size GPSDetailList : " + returnString);

        return returnString;

    }

    public String uploadExpirewarning(String deviceId, String repId,
                                      String[] custDetails) throws SocketException {

        String repStoreDetailList = "";

        final String SOAP_ACTION = "http://tempuri.org/XmlUploadxpireWarnnig";
        final String OPERATION_NAME = "XmlUploadxpireWarnnig";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);

        Log.w("Log", "deviceId : " + deviceId);
        Log.w("Log", "repId : " + repId);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("DeviceID");
        pi.setValue(deviceId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("RepID");
        pi2.setValue(repId);
        pi2.setType(String.class);
        request.addProperty(pi2);

        SoapObject dataset = new SoapObject(WSDL_TARGET_NAMESPACE, "NewDataSet");

        //   for (String[] custDetails : invoicedProductDetailList) {

        SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE, "Table");

        Log.w("Log", "rtnProducts 0 : " + custDetails[0]);
        Log.w("Log", "rtnProducts 1 : " + custDetails[1]);
        Log.w("Log", "rtnProducts 2 : " + custDetails[2]);
        Log.w("Log", "rtnProducts 3 : " + custDetails[3]);
        Log.w("Log", "rtnProducts 4 : " + custDetails[4]);
        Log.w("Log", "rtnProducts 5 : " + custDetails[5]);
        Log.w("Log", "rtnProducts 6 : " + custDetails[6]);
        Log.w("Log", "rtnProducts 7 : " + custDetails[7]);
        Log.w("Log", "rtnProducts 8 : " + custDetails[8]);
        Log.w("Log", "rtnProducts 9 : " + custDetails[9]);


        table.addProperty("PharmacyID", custDetails[1]);
        table.addProperty("DealerID", custDetails[2]);
        table.addProperty("RepID", custDetails[3]);
        table.addProperty("CheckDate", custDetails[4]);
        table.addProperty("ProductCode", custDetails[5]);
        table.addProperty("ShelfStock", custDetails[6]);
        table.addProperty("ExpireDate", custDetails[7]);
        table.addProperty("BatchCode", custDetails[8]);
        table.addProperty("ContactNo", custDetails[9]);
        dataset.addSoapObject(table);


        //  }


        SoapObject invoiceData = new SoapObject(WSDL_TARGET_NAMESPACE, "UploadXpireWarnnig");
        invoiceData.addSoapObject(dataset);
        request.addSoapObject(invoiceData);

        Log.w("Log", "request toString : " + request.toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;
        try {

            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.bodyIn;

            Log.w("test123", "response : " + response.toString());
            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());
                // repStoreDetailList = response.toString();
                repStoreDetailList = response.getProperty("XmlUploadxpireWarnnigResult").toString();

            } else {

                repStoreDetailList = "error";
                Log.w("test123", "Error repStoreDetailList: 0 size xxx");
            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            repStoreDetailList = "error";
            Log.w("test123",
                    "Error repStoreDetailList: " + exception.toString());

            Log.w("test123", "Error repStoreDetailList: " + exception);
        }

        Log.w("test123", "list size repStoreDetailList : " + repStoreDetailList);

        return repStoreDetailList;

    }


    public String uploadRemarks(String[] remarksDetails) throws SocketException {

        String returnString = "";

        final String SOAP_ACTION = "http://tempuri.org/AddCustomerRemarks";

        final String OPERATION_NAME = "AddCustomerRemarks";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);


      /*  PropertyInfo pi = new PropertyInfo();
        pi.setName("DeviceID");
        pi.setValue(deviceId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("RepID");
        pi2.setValue(repId);
        pi2.setType(String.class);
        request.addProperty(pi2);*/

        SoapObject dataset = new SoapObject(WSDL_TARGET_NAMESPACE, "NewDataSet");


        SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE, "Table");
        table.addProperty("Row_ID", remarksDetails[0]);
        table.addProperty("RepID", remarksDetails[6]);
        table.addProperty("Itinerary_ID", remarksDetails[1]);
        table.addProperty("Itinerary_Date", remarksDetails[4]);
        table.addProperty("Remark", remarksDetails[2]);
        table.addProperty("CustomerID", remarksDetails[5]);
        table.addProperty("RemarkType", remarksDetails[7]);
        table.addProperty("TIMESTAMP", remarksDetails[3]);
        table.addProperty("ITINERARY_ID_SERVER", remarksDetails[9]);
        table.addProperty("GatDate", remarksDetails[9]);
        table.addProperty("Longititude",remarksDetails[10]);
        table.addProperty("Latitude",remarksDetails[11]);


        dataset.addSoapObject(table);

        SoapObject remarksData = new SoapObject(WSDL_TARGET_NAMESPACE, "CustomerRemarks");
        remarksData.addSoapObject(dataset);
        request.addSoapObject(remarksData);

        Log.w("Log", "request remarks toString :----->         " + request.toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;
        try {

            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.bodyIn;

            Log.w("remarks ------>", "response : " + response.toString());

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                returnString = response.getProperty(
                        "AddCustomerRemarksResult").toString();

            } else {
                returnString = "ERROR";

            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            returnString = "error";
            Log.w("test123",
                    "Error RemarksDetailList: ------> " + exception.toString());

            Log.w("test123", "Error RemarksDetailList:   ------->" + exception);
        }

        Log.w("test123", "list size RemarksDetailList : ---------> " + returnString);

        return returnString;

    }

    public ArrayList<String[]> GetCustomerImage(String repid, String pharmacyId) throws SocketException {


        ArrayList<String[]> repDetailList = new ArrayList<>();

        final String SOAP_ACTION = "http://tempuri.org/XmlGetCustomerImage";
        final String OPERATION_NAME = "XmlGetCustomerImage";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);

        Log.w("Log", "deviceId : " + pharmacyId);
        Log.w("Log", "repId : " + repid);


        PropertyInfo pi = new PropertyInfo();
        pi.setName("repId");//repId
        pi.setValue(repid);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("pharmacyId");//pharmacyId
        pi2.setValue(pharmacyId);
        pi2.setType(String.class);
        request.addProperty(pi2);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        Log.w("Log", "request toString : " + request.toString());

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                SoapObject custTable = (SoapObject) response.getProperty(0);

                if (custTable != null && custTable.getPropertyCount() > 0) {

                  /*  repDetailList = new ArrayList<String[]>(
                            custTable.getPropertyCount());*/

                    for (int i = 0; i < custTable.getPropertyCount(); i++) {
                        String[] customerDetails = new String[2];//test
                        SoapObject custDetails = (SoapObject) custTable
                                .getProperty(i);

                        Log.w("Log",
                                "repDetails object : " + custDetails.toString());

                        if (custDetails.hasProperty("CusImage")) {

                            if (custDetails.getProperty(
                                    "CusImage").toString() != "anyType{}") {
                                customerDetails[0] = custDetails.getProperty(
                                        "CusImage").toString();
                            } else {
                                customerDetails[0] = "";
                            }
                        }
                        if (custDetails.hasProperty("ImageID")) {

                            if (custDetails.getProperty(
                                    "ImageID").toString() != "anyType{}") {
                                customerDetails[1] = custDetails.getProperty(
                                        "ImageID").toString();
                            } else {
                                customerDetails[1] = "";
                            }
                        }
                        repDetailList.add(customerDetails);

                    }

                } else {
                    repDetailList = new ArrayList<String[]>(0);

                    Log.w("test123", "Error: 0 size zzz");
                }
            } else {
                repDetailList = new ArrayList<String[]>(0);
                Log.w("test123", "Error: 0 size xxx");
            }

        } catch (SocketException e) {
            try {
                throw new SocketException(e.toString());
            } catch (SocketException e1) {
                e1.printStackTrace();
            }
        } catch (Exception exception) {
            repDetailList = null;
            Log.w("test123", "Error: " + exception.toString());
        }


        return repDetailList;
    }




    public ArrayList<CreditPeriod> getCreditPeriods(String deviceId,int repId) throws SocketException {

        ArrayList<CreditPeriod> creditDetailList = new ArrayList<>();

        final String SOAP_ACTION = "http://tempuri.org/GetCreditPeriod";

        final String OPERATION_NAME = "GetCreditPeriod";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);
        PropertyInfo pi = new PropertyInfo();

        Log.w("Log", "deviceId : " + deviceId);
        PropertyInfo pi1 = new PropertyInfo();
        pi1.setName("DeviceID");
        pi1.setValue(deviceId);

        pi.setValue(repId);
        pi.setName("RepID");

        request.addProperty(pi1);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        Log.w("Log", "cr request : " + request.toString());
        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS,TimeOut);
        SoapObject response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();
            Log.w("Log", "response count : " + response.getPropertyCount());

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());


//                SoapObject dataset = (SoapObject) response.getProperty(0);
//                Log.w("Log", "Data set : " + dataset.toString());
                SoapObject creditTable = (SoapObject) response.getProperty(0);
                Log.w("Log", "creditTable : " + creditTable.toString());

                if (creditTable != null && creditTable.getPropertyCount() > 0) {

                    CreditPeriod creditPeriod = null;
                    for (int i = 0; i < creditTable.getPropertyCount(); i++) {
                        SoapObject creditDetails = (SoapObject) creditTable
                                .getProperty(i);
                        creditPeriod = new CreditPeriod();
                        Log.w("Log", "credit Details object : "
                                + creditDetails.toString());
                        creditPeriod.setRowID(Integer.parseInt(creditDetails.getProperty("Id").toString()));
                        creditPeriod.setPeriod(Integer.parseInt(creditDetails.getProperty("CreditPeriod").toString()));
                        creditPeriod.setIsActive(Boolean.valueOf(creditDetails.getProperty("IsActive").toString()));
                        creditPeriod.setCompId(Integer.parseInt(creditDetails.getProperty("CompID").toString()));
                        creditDetailList.add(creditPeriod);
                    }



                } else {
                    creditDetailList = new ArrayList<>();
                    //repDetailList.add("No Data");
                    Log.w("test123", "Error: 0 size zzz");
                }
            } else {
                creditDetailList = new ArrayList<>();
                //repDetailList.add("No Data");
                Log.w("test123", "Error: 0 size xxx");
            }

        }catch (IOException e) {
            creditDetailList = null;
            Log.e("IO exception ->",e.toString());
        }catch (XmlPullParserException e) {
            creditDetailList = null;
            Log.e("XmlPullParserException ->",e.toString());
        }catch (Exception ex){
            creditDetailList = null;
        }


        return creditDetailList;
    }


    public void uploadInvoiceHeader(String deviceId,String repId,String dealerId,List<String[]> headerList){
        Log.i("uploadInvoice -->","CALL");
        String responseString = "";

        final String SOAP_ACTION = "http://tempuri.org/SetInvoicesHeder";
        final String OPERATION_NAME = "SetInvoicesHeder";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("DeviceID");
        pi.setValue(deviceId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("RepID");
        pi2.setValue(repId);
        pi2.setType(String.class);
        request.addProperty(pi2);

        SoapObject dataSet = new SoapObject(WSDL_TARGET_NAMESPACE,"NewDataSet");


        for(String[] headerItem:headerList){
            SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE,"Table");

            table.addProperty("TabRowID",headerItem[0]);
            table.addProperty("DealerID",dealerId);
            table.addProperty("InvoiceNo",headerItem[0]);
            table.addProperty("GrandQty",headerItem[18]);

            table.addProperty("GrandDiscount","0.0000");


            table.addProperty("GrandTotal",headerItem[3]);
            table.addProperty("MarketreturnValue",headerItem[7]);
           // table.addProperty("IsReturn",headerItem[12]);
            table.addProperty("PaidAmount",headerItem[4]);
            table.addProperty("CreditAmount",headerItem[5]);
            table.addProperty("ChequeAmount",headerItem[6]);
            table.addProperty("CreditDuration",headerItem[13]);
            table.addProperty("IsReturned",headerItem[12]);
            table.addProperty("PaymentType",headerItem[2]);
            table.addProperty("CustomerNo",headerItem[19]);
            table.addProperty("Longititude",headerItem[16]);
            table.addProperty("Latitude",headerItem[15]);
            table.addProperty("UserName","");
            table.addProperty("InvoiceStartTime",headerItem[17]);//headerItem[17]
            table.addProperty("InvoiceEndTime",headerItem[11]);//headerItem[11]
            table.addProperty("TransferdTimeStamp", (new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date())));
            table.addProperty("InvoiceDate",headerItem[11]);//headerItem[11]
            table.addProperty("DiscountPrecentage",headerItem[8]);

            table.addProperty("DetailsRowCount",headerItem[20]);
            Log.i("send -->",table.toString());
            dataSet.addSoapObject(table);
        }

        SoapObject invoiceData = new SoapObject(WSDL_TARGET_NAMESPACE,
                "InvoiceData");
        invoiceData.addSoapObject(dataSet);
        request.addSoapObject(invoiceData);
        Log.i("send request-->",request.toString());
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response = null;
        try {

            httpTransport.call(SOAP_ACTION, envelope);

            if (envelope.bodyIn instanceof SoapFault)
            {
                final SoapFault sf = (SoapFault) envelope.bodyIn;
                responseString = sf.toString();
                Log.i("fault->",responseString);

            }else if(envelope.bodyIn instanceof SoapObject){
                response = (SoapObject)envelope.bodyIn;
            }

            if (response != null && response.getPropertyCount() > 0) {
                Log.i("Response -->", response.toString());
                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                responseString = response.getProperty(
                        "SetInvoicesHederResult").toString();
                Log.i("Response -->", "done");
            } else {
                responseString = "error";
                Log.w("test123", "no response");
            }
        }catch (IOException e) {
            Log.e("IO exception ->",e.toString());
        }catch (XmlPullParserException e) {
            Log.e("XmlPullParserException ->",e.toString());
        }catch (Exception e) {
            Log.e("Exception ->", e.toString());
        }

        Log.w("RESPO", " " + responseString);


    }



    public String uploadReturnHeader(String repId,String deviceId,ReturnHeaderEntity headerEntity){

        final String SOAP_ACTION = "http://tempuri.org/SetReturnInvoicesHeder";
        final String OPERATION_NAME = "SetReturnInvoicesHeder";
        String returnString = "";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);

        PropertyInfo pi1 = new PropertyInfo();
        pi1.setName("DeviceID");
        pi1.setValue(deviceId);
        pi1.setType(String.class);
        request.addProperty(pi1);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("RepID");
        pi2.setValue(repId);
        pi2.setType(String.class);
        request.addProperty(pi2);

        SoapObject dataSet = new SoapObject(WSDL_TARGET_NAMESPACE,"NewDataSet");

       // for(ReturnHeaderEntity headerEntity:headerEntities){
            SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE,"Table");
            table.addProperty("TabRowID",headerEntity.getId());
            table.addProperty("InvoiceNo",headerEntity.getInvoiceNumber());
            table.addProperty("InvoiceDate",headerEntity.getEndTime());
            table.addProperty("GrandTotal",headerEntity.getTotalAmount());
            table.addProperty("GrandQty",headerEntity.getTotalQuantity());
            table.addProperty("GrandDiscount",headerEntity.getDiscountAmount());
            table.addProperty("InvoiceStartTime",headerEntity.getStartTime());
            table.addProperty("InvoiceEndTime",headerEntity.getEndTime());
            table.addProperty("Longititude",headerEntity.getLongitude());
            table.addProperty("Latitude",headerEntity.getLatitude());
            table.addProperty("CustomerNo",headerEntity.getCutomerNo());
            table.addProperty("ReturnInvoiceNumber",headerEntity.getReturnInvoiceNumber());
            table.addProperty("DetailsRowCount",headerEntity.getDetailsCount());
            dataSet.addSoapObject(table);

      //  }

        SoapObject invoice = new SoapObject(WSDL_TARGET_NAMESPACE,"InvoiceData");
        invoice.addSoapObject(dataSet);
        request.addSoapObject(invoice);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransportSE = new HttpTransportSE(SOAP_ADDRESS,TimeOut);
        SoapObject response;
        Log.i("Return request -Rr->",request.toString());

        try{
            httpTransportSE.call(SOAP_ACTION,envelope);
            response = (SoapObject)envelope.bodyIn;

            if(response != null && response.getPropertyCount() > 0){
                returnString = response.getProperty(
                        "SetReturnInvoicesHederResult").toString();
                Log.i("Return no -rN->",returnString);
            }
        }catch (IOException e){
            Log.e("IO exception",e.toString());
        }catch (XmlPullParserException e){
            Log.e("xml exception",e.toString());
        }catch (Exception e){
            Log.e("xml exception",e.toString());
        }

        return  returnString;
    }


    public ArrayList<DealerSaleEntity> getDealerSalesFromServer(String deviceId,String repId){

        ArrayList<DealerSaleEntity> salesArray = new ArrayList<>();
        final String SOAP_ACTION = "http://tempuri.org/xmlUploadInvoice";

        final String OPERATION_NAME = "xmlUploadInvoice";
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);

        SimpleDateFormat fromServer = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");


        PropertyInfo p1 = new PropertyInfo();
        p1.setName("DeviceID");
        p1.setValue(deviceId);
        p1.setType(String.class);
        request.addProperty(p1);

        PropertyInfo p2 = new PropertyInfo();
        p2.setName("RepID");
        p2.setValue(repId);
        p2.setType(String.class);
        request.addProperty(p2);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransportSE = new HttpTransportSE(SOAP_ADDRESS,TimeOut);
        SoapObject response;


        try {
            httpTransportSE.call(SOAP_ACTION, envelope);
            response = (SoapObject)envelope.getResponse();

            if(response != null && response.getPropertyCount() > 0){
                Log.w("Log", "response count : -D->" + response.getPropertyCount());
                Log.w("Log", "response Str -D->: " + response.toString());
                SoapObject dealerTable = (SoapObject) response.getProperty(0);
                Log.w("Log", "dealerTable  -D->: " + dealerTable.toString());

                if(dealerTable != null && dealerTable.getPropertyCount() > 0){

                    DealerSaleEntity dealerSaleEntity = null;
                    for (int i = 0; i < dealerTable.getPropertyCount(); i++) {
                        SoapObject dealerDetails = (SoapObject) dealerTable
                                .getProperty(i);
                        dealerSaleEntity = new DealerSaleEntity();
                        Log.w("Log", "dealer Details object : "
                                + dealerDetails.toString());

                        dealerSaleEntity.setDealerId(dealerDetails.getProperty("DealerID").toString());
                        dealerSaleEntity.setItemId(dealerDetails.getProperty("ItemIdentityCode").toString());
                        dealerSaleEntity.setpPrice(dealerDetails.getProperty("PurchasingPrice").toString());
                        dealerSaleEntity.setsPrice(dealerDetails.getProperty("SellingPrice").toString());
                        dealerSaleEntity.setrPrice(dealerDetails.getProperty("RetailPrice").toString());
                        dealerSaleEntity.setDicountMethod(dealerDetails.getProperty("DiscountMethod").toString());
                        dealerSaleEntity.setDiscountRate(dealerDetails.getProperty("DiscountRate").toString());
                        dealerSaleEntity.setFreeIssues(dealerDetails.getProperty("FreeIssues").toString());
                        dealerSaleEntity.setInvoiceNo(dealerDetails.getProperty("InvoiceNo").toString());
                        dealerSaleEntity.setIssueMode(dealerDetails.getProperty("IssueMode").toString());
                        dealerSaleEntity.setQty(Integer.parseInt(dealerDetails.getProperty("Qty").toString()));
                        dealerSaleEntity.setUnitPrice(dealerDetails.getProperty("UnitPrice").toString());
                        dealerSaleEntity.setDiscount(dealerDetails.getPrimitiveProperty("Discount").toString());
                        dealerSaleEntity.setRepID(dealerDetails.getProperty("SalesRepID").toString());
                        dealerSaleEntity.setRepName(dealerDetails.getProperty("SalesRep").toString());
                        dealerSaleEntity.setCustNo(dealerDetails.getProperty("CustomerNo").toString());

                        String date = dealerDetails.getProperty("InvoiceDate").toString();
                        String []splitArray = date.split("T");
                        String invoiceDate = myFormat.format(fromServer.parse(splitArray[0]));
                        dealerSaleEntity.setInvoiceDate(invoiceDate);

                        dealerSaleEntity.setPaymentType(dealerDetails.getProperty("PaymentType").toString());
                        dealerSaleEntity.setTotal(dealerDetails.getProperty("Total").toString());
                        dealerSaleEntity.setExpiry(dealerDetails.getProperty("ExpireDate").toString());
                        dealerSaleEntity.setBatch(dealerDetails.getProperty("BatchNumber").toString());
                        dealerSaleEntity.setRefNo(dealerDetails.getProperty("RefNo").toString());
                        salesArray.add(dealerSaleEntity);
                    }
                }

            }
        }catch (IOException e) {
            salesArray =null;
            Log.e("IO exception ->",e.toString());
        }catch (XmlPullParserException e){
            salesArray =null;
            Log.e("pull parse exception ->",e.toString());
        }catch (ParseException e){
            salesArray =null;
            Log.e("parse exception ->",e.toString());
        }catch (Exception e){
            salesArray =null;
        }

        return salesArray;
    }

    public String uploadMacAddress(String repId,String deviceId,String MacId){

        final String SOAP_ACTION = "http://tempuri.org/xmlSetRepMacAddress";
        final String OPERATION_NAME = "xmlSetRepMacAddress";
        String returnString = "";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);

        PropertyInfo pi1 = new PropertyInfo();
        pi1.setName("DeviceID");
        pi1.setValue(deviceId);
        pi1.setType(String.class);
        request.addProperty(pi1);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("RepID");
        pi2.setValue(repId);
        pi2.setType(String.class);
        request.addProperty(pi2);

        SoapObject dataSet = new SoapObject(WSDL_TARGET_NAMESPACE,"NewDataSet");

        // for(ReturnHeaderEntity headerEntity:headerEntities){
        SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE,"Table");
        table.addProperty("MacAddress", MacId);
        table.addProperty("TabMacAddress","no mac");

        dataSet.addSoapObject(table);

        //  }

        SoapObject invoice = new SoapObject(WSDL_TARGET_NAMESPACE,"RepMacAddress");
        invoice.addSoapObject(dataSet);
        request.addSoapObject(invoice);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransportSE = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response = null;
        Log.i("Return request -Mac->",request.toString());

        try{
            httpTransportSE.call(SOAP_ACTION,envelope);


            if (envelope.bodyIn instanceof SoapFault)
            {
                final SoapFault sf = (SoapFault) envelope.bodyIn;
                returnString = sf.toString();
                Log.i("fault->",returnString);

            }else if(envelope.bodyIn instanceof SoapObject){
                response = (SoapObject)envelope.bodyIn;
            }

            if(response != null && response.getPropertyCount() > 0){
                returnString = response.getProperty(
                        "xmlSetRepMacAddressResult").toString();

                Log.i("Return no -mac->", returnString);
            }
        }catch (IOException e){
            Log.e("IO exception",e.toString());
        }catch (XmlPullParserException e){
            Log.e("xml exception",e.toString());
        }

        return  returnString;
    }



    public String uploadUserCredentials(String repId,String deviceId,String userNmae,String pwd,double longititude,double latitude,String timeStamp){

        final String SOAP_ACTION = "http://tempuri.org/xmlSetRepUserAccountDetails";
        final String OPERATION_NAME = "xmlSetRepUserAccountDetails";
        String returnString = "";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);

        PropertyInfo pi1 = new PropertyInfo();
        pi1.setName("DeviceID");
        pi1.setValue(deviceId);
        pi1.setType(String.class);
        request.addProperty(pi1);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("RepID");
        pi2.setValue(repId);
        pi2.setType(String.class);
        request.addProperty(pi2);

        SoapObject dataSet = new SoapObject(WSDL_TARGET_NAMESPACE,"NewDataSet");

        // for(ReturnHeaderEntity headerEntity:headerEntities){
        SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE,"Table");
        table.addProperty("UserName",userNmae);
        table.addProperty("Password",pwd);
        table.addProperty("Longititude",Double.toString(longititude));
        table.addProperty("Latitude",Double.toString(latitude));
        table.addProperty("TabLoginTime",timeStamp);
        table.addProperty("TabMacAddress","-");


        dataSet.addSoapObject(table);

        //  }

        SoapObject invoice = new SoapObject(WSDL_TARGET_NAMESPACE,"RepUserAccountDetails");
        invoice.addSoapObject(dataSet);
        request.addSoapObject(invoice);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransportSE = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response = null;
        Log.i("Return request -crd->",request.toString());

        try{
            httpTransportSE.call(SOAP_ACTION,envelope);


            if (envelope.bodyIn instanceof SoapFault)
            {
                final SoapFault sf = (SoapFault) envelope.bodyIn;
                returnString = sf.toString();
                Log.i("fault->",returnString);

            }else if(envelope.bodyIn instanceof SoapObject){
                response = (SoapObject)envelope.bodyIn;
            }

            if(response != null && response.getPropertyCount() > 0){
                returnString = response.getProperty(
                        "xmlSetRepUserAccountDetailsResult").toString();

                Log.i(" -crd res->",returnString);
            }
        }catch (IOException e){
            Log.e("IO exception",e.toString());
        }catch (XmlPullParserException e){
            Log.e("xml exception",e.toString());
        }

        return  returnString;
    }

    //Service by Himanshu
    public ArrayList<String[]> getDiscountStructures(String repid) {

        ArrayList<String[]> DetailList = null;
        final String SOAP_ACTION = "http://tempuri.org/xmlDiscountStructures";
        final String OPERATION_NAME = "xmlDiscountStructures";
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("RepID");
        pi.setValue(repid);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS,TimeOut);
        SoapObject response = null;

        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();
            if (response != null && response.getPropertyCount() > 0) {
                SoapObject desicountTable = (SoapObject) response.getProperty(0);
                if (desicountTable != null && desicountTable.getPropertyCount() > 0) {
                    DetailList = new ArrayList<String[]>(desicountTable.getPropertyCount());
                    for (int i = 0; i < desicountTable.getPropertyCount(); i++) {
                        String[] Details = new String[10];//test
                        SoapObject discountDetails = (SoapObject) desicountTable.getProperty(i);

                        if (discountDetails.hasProperty("RepID")) {
                            Details[0] = discountDetails.getProperty("RepID").toString();
                        }
                        if (discountDetails.hasProperty("ServerID")) {
                            Details[1] = discountDetails.getProperty("ServerID").toString();
                        }
                        if (discountDetails.hasProperty("Type")) {
                            Details[2] = discountDetails.getProperty("Type").toString();
                        }
                        if (discountDetails.hasProperty("Principle")) {
                            Details[3] = discountDetails.getProperty("Principle").toString();
                        }
                        if (discountDetails.hasProperty("ItemCode")) {
                            Details[4] = discountDetails.getProperty("ItemCode").toString();
                        }if (discountDetails.hasProperty("Description")) {
                            Details[5] = discountDetails.getProperty("Description").toString();
                        }if (discountDetails.hasProperty("NQty")) {
                            Details[6] = discountDetails.getProperty("NQty").toString();
                        }if (discountDetails.hasProperty("FQty")) {
                            Details[7] = discountDetails.getProperty("FQty").toString();
                        }
                        if (discountDetails.hasProperty("IsActive")) {
                            if (discountDetails.getProperty("IsActive").toString().equals("true")) {
                                Details[8] = "1";
                            } else {
                                Details[8] = "0";
                            }
                        }
                        DetailList.add(Details);

                    }



                } else {
                    DetailList = new ArrayList<String[]>(0);
                }
            } else {
                DetailList = new ArrayList<String[]>(0);
            }

        } catch (SocketException e) {
            DetailList=null;
            // throw new SocketException(e.toString());
        } catch (Exception exception) {
            DetailList = null;
            Log.w("test123", "Error: " + exception.toString());
        }
        return DetailList;

    }

    //Service by Himanshu
    public ArrayList<String[]> getApprvedList(String repId) {

        ArrayList<String[]> apprvDetailList = null;
        final String SOAP_ACTION = "http://tempuri.org/xmlApprovedPersonDetails";
        final String OPERATION_NAME = "xmlApprovedPersonDetails";
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("RepID");
        pi.setValue(repId);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response = null;

        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();
            if (response != null && response.getPropertyCount() > 0) {
                SoapObject custTable = (SoapObject) response.getProperty(0);
                if (custTable != null && custTable.getPropertyCount() > 0) {
                    apprvDetailList = new ArrayList<String[]>(custTable.getPropertyCount());


                    for (int i = 0; i < custTable.getPropertyCount(); i++) {
                        String[] approveDetails = new String[5];//test
                        SoapObject custDetails = (SoapObject) custTable.getProperty(i);
                        if (custDetails.hasProperty("ServerID")) {
                            approveDetails[0] = custDetails.getProperty("ServerID").toString();
                        }
                        if (custDetails.hasProperty("ApprovedPerson")) {
                            approveDetails[1] = custDetails.getProperty("ApprovedPerson").toString();
                        }
                        if (custDetails.hasProperty("EmailID")) {
                            approveDetails[2] = custDetails.getProperty("EmailID").toString();
                        }
                        if (custDetails.hasProperty("ContactNo")) {
                            approveDetails[3] = custDetails.getProperty("ContactNo").toString();
                        }
                        if (custDetails.hasProperty("IsActive")) {
                            if (custDetails.getProperty("IsActive").toString().equals("true")) {
                                approveDetails[4] = "1";
                            } else {
                                approveDetails[4] = "0";
                            }
                        }
                        apprvDetailList.add(approveDetails);

                    }



                } else {
                    apprvDetailList = new ArrayList<String[]>(0);
                }
            } else {
                apprvDetailList = new ArrayList<String[]>(0);
            }

        } catch (SocketException e) {
            apprvDetailList=null;
            // throw new SocketException(e.toString());
        } catch (Exception exception) {
            apprvDetailList = null;
            Log.w("test123", "Error: " + exception.toString());
        }
        return apprvDetailList;

    }

    public String uploadApprovalDetails(String[] Details, String repid) throws SocketException {

        String returnString = "";

        final String SOAP_ACTION = "http://tempuri.org/xmlcustomerblockinfo";

        final String OPERATION_NAME = "xmlcustomerblockinfo";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);


        SoapObject dataset = new SoapObject(WSDL_TARGET_NAMESPACE, "NewDataSet");


        SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE, "Table");
        table.addProperty("Row_ID", Details[0]);
        table.addProperty("RepID", repid);
        table.addProperty("Customer_No", Details[1]);
        table.addProperty("Date", Details[2]);
        table.addProperty("Code", Details[3]);
        table.addProperty("Reason", Details[4]);
        table.addProperty("Comment", Details[5]);
        table.addProperty("Is_Access", Details[6]);
        table.addProperty("Access_Date", Details[7]);
        table.addProperty("Approval_Person", Details[8]);
        table.addProperty("Sent_Date", Details[9]);


        dataset.addSoapObject(table);

        SoapObject remarksData = new SoapObject(WSDL_TARGET_NAMESPACE, "customerblockinfo");
        remarksData.addSoapObject(dataset);
        request.addSoapObject(remarksData);
        System.out.println("mdk :" + remarksData);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.bodyIn;
            if (response != null && response.getPropertyCount() > 0) {
                returnString = response.getProperty("xmlcustomerblockinfoResult").toString();

            } else {
                returnString = "ERROR";

            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            returnString = "error";

        }


        return returnString;

    }


    public String lastInvoiceNumber(String repid) {

        String DetailList = null;
        final String SOAP_ACTION = "http://tempuri.org/LastInvoiceNumber";
        final String OPERATION_NAME = "LastInvoiceNumber";
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("RepID");
        pi.setValue(repid);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response = null;

        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();
            if (response != null && response.getPropertyCount() > 0) {
                SoapObject cheqTable = (SoapObject) response.getProperty(0);

                if (cheqTable != null && cheqTable.getPropertyCount() > 0) {

                    SoapObject discountDetails = (SoapObject) cheqTable.getProperty(0);
                    if (discountDetails.hasProperty("MaxInvoiceID")) {
                        DetailList = discountDetails.getProperty("MaxInvoiceID").toString();
                    }


                } else {
                    DetailList = "0";
                }
            } else {
                DetailList = response.toString();
            }

        } catch (SocketException e) {
            DetailList=null;
            // throw new SocketException(e.toString());
        } catch (Exception exception) {
            DetailList = null;
            Log.w("test123", "Error: " + exception.toString());
        }
        return DetailList;

    }

    public ArrayList<String> checkTransferAudit(String repId) throws SocketException {

        ArrayList<String> invoicesDetailList = null;
        final String SOAP_ACTION = "http://tempuri.org/xmlTransferAudit";
        final String OPERATION_NAME = "xmlTransferAudit";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
        PropertyInfo pi = new PropertyInfo();

        pi.setName("RepID");
        pi.setValue(repId);
        pi.setType(Integer.class);
        request.addProperty(pi);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response ;

        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.bodyIn;

            if (response != null && response.getPropertyCount() > 0) {
                SoapObject repTable = (SoapObject) response.getProperty(0);
                if (repTable != null && repTable.getPropertyCount() > 0) {
                    invoicesDetailList = new ArrayList<String>(repTable.getPropertyCount());
                    System.out.println("transfer result"+response);
                    for (int a = 0; a < repTable.getPropertyCount(); a++) {
                        SoapObject repDetails = (SoapObject) repTable.getProperty(a);
                        System.out.println("transfer result getProperty " + repDetails);
                        SoapObject repDetailsSecon = (SoapObject) repDetails.getProperty(a);
                        invoicesDetailList.add(repDetailsSecon.getProperty("InvoiceNo").toString());
                    }


                } else {
                    invoicesDetailList = null;
                }
            } else {
                invoicesDetailList = null;

            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            invoicesDetailList = null;
        }


        return invoicesDetailList;
    }
    public String checkInvoiceCount(String repId) throws SocketException {

        String invoicesCount = null;
        final String SOAP_ACTION = "http://tempuri.org/xmlTransferAuditCurrentDatecount";
        final String OPERATION_NAME = "xmlTransferAuditCurrentDatecount";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
        PropertyInfo pi = new PropertyInfo();

        pi.setName("RepID");
        pi.setValue(repId);
        pi.setType(Integer.class);
        request.addProperty(pi);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;

        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();

            if (response != null && response.getPropertyCount() > 0) {
                SoapObject repTable = (SoapObject) response.getProperty(0);
                if (repTable != null && repTable.getPropertyCount() > 0) {
                    SoapObject repDetails = (SoapObject) repTable.getProperty(0);
                    invoicesCount=repDetails.getProperty("InvoiceCount").toString();

                } else {
                    invoicesCount = null;
                }
            } else {
                invoicesCount = null;

            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            invoicesCount = null;
        }


        return invoicesCount;
    }
    public ArrayList<String> getCheckInvoiceId(String repId,ArrayList<String>rowId) throws SocketException {

        ArrayList<String> invoicesDetailList = null;
        final String SOAP_ACTION = "http://tempuri.org/xmlTransferAuditInvoiceDetails";
        final String OPERATION_NAME = "xmlTransferAuditInvoiceDetails";




        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("RepID");
        pi.setValue(repId);
        pi.setType(Integer.class);
        request.addProperty(pi);


        SoapObject dataset = new SoapObject(WSDL_TARGET_NAMESPACE, "NewDataSet");


        for(int a=0;a<rowId.size();a++){
            SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE, "Table");
            table.addProperty("InvoiceID", rowId.get(a));
            dataset.addSoapObject(table);
        }



        SoapObject invoiceData = new SoapObject(WSDL_TARGET_NAMESPACE, "InvoiceDetails");
        invoiceData.addSoapObject(dataset);
        request.addSoapObject(invoiceData);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;


        try {

            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject)envelope.bodyIn;


            if (response != null && response.getPropertyCount() > 0) {
                SoapObject repTable = (SoapObject) response.getProperty(0);

                if (repTable != null && repTable.getPropertyCount() > 0) {
                    invoicesDetailList = new ArrayList<String>(repTable.getPropertyCount());

                    for (int a = 0; a < repTable.getPropertyCount(); a++) {
                        SoapObject repDetails = (SoapObject) repTable.getProperty(a);
                        SoapObject repTablesecond = (SoapObject) repDetails.getProperty(0);
                        invoicesDetailList.add(repTablesecond.getProperty("InvoiceID").toString());
                    }


                } else {
                    invoicesDetailList = null;
                }


            } else {

            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {

        }


        return invoicesDetailList;
    }

    public ArrayList<String[]> checkTransferAuditIternery(String repId,String count) throws SocketException {

        ArrayList<String[]> invoicesDetailList = null;
        final String SOAP_ACTION = "http://tempuri.org/xmlTransferAuditIternery";
        final String OPERATION_NAME = "xmlTransferAuditIternery";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
        PropertyInfo pi = new PropertyInfo();
        PropertyInfo p2 = new PropertyInfo();

        pi.setName("RepID");
        pi.setValue(repId);
        pi.setType(Integer.class);
        request.addProperty(pi);

        p2.setName("Count");
        p2.setValue(count);
        p2.setType(Integer.class);
        request.addProperty(p2);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response ;

        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.bodyIn;

            if (response != null && response.getPropertyCount() > 0) {
                SoapObject repTable = (SoapObject) response.getProperty(0);
                if (repTable != null && repTable.getPropertyCount() > 0) {
                    invoicesDetailList = new ArrayList<String[]>(repTable.getPropertyCount());

                    for (int a = 0; a < repTable.getPropertyCount(); a++) {
                        String[] customerDetails = new String[9];
                        SoapObject repDetails = (SoapObject) repTable.getProperty(a);
                        SoapObject repDetailsSecon = (SoapObject) repDetails.getProperty(a);

                        if (repDetailsSecon.hasProperty("RowID")) {
                            customerDetails[0] = repDetailsSecon.getProperty("RowID").toString();

                            if (repDetailsSecon.hasProperty("Itn_ID")) {
                                customerDetails[1] = repDetailsSecon.getProperty(
                                        "Itn_ID").toString();
                            }
                            if (repDetailsSecon.hasProperty("Itn_RepID")) {
                                customerDetails[2] = repDetailsSecon.getProperty(
                                        "Itn_RepID").toString();
                            }

                            if (repDetailsSecon.hasProperty("Itn_Date")) {
                                customerDetails[3] = repDetailsSecon.getProperty(
                                        "Itn_Date").toString();
                            }

                            if (repDetailsSecon.hasProperty("GLB_PharmacyID")) {
                                customerDetails[4] = repDetailsSecon.getProperty(
                                        "GLB_PharmacyID").toString();
                            }

                            if (repDetailsSecon.hasProperty("CompanyCode")) {
                                customerDetails[5] = repDetailsSecon.getProperty(
                                        "CompanyCode").toString();
                            }

                            if (repDetailsSecon.hasProperty("Name")) {
                                customerDetails[6] = repDetailsSecon
                                        .getProperty("Name").toString();
                            }

                            if (repDetailsSecon.hasProperty("Target")) {
                                customerDetails[7] = repDetailsSecon.getProperty(
                                        "Target").toString();
                            }

                            if (repDetailsSecon.hasProperty("Itn_SendForApproval")) {
                                customerDetails[8] = repDetailsSecon.getProperty(
                                        "Itn_SendForApproval").toString();
                            }
                            if (repDetailsSecon.hasProperty("Address")) {
                                customerDetails[9] = repDetailsSecon.getProperty(
                                        "Address").toString();
                            }

                            invoicesDetailList.add(customerDetails);
                        }else {
                            invoicesDetailList = null;

                        }

                    }


                } else {
                    invoicesDetailList = null;
                }
            } else {
                invoicesDetailList = null;

            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            invoicesDetailList = null;
        }


        return invoicesDetailList;
    }
    public String lastRetuernInvoiceNumber(String repid) {

        String DetailList = null;
        final String SOAP_ACTION = "http://tempuri.org/LastReturnInvoiceNumber";
        final String OPERATION_NAME = "LastReturnInvoiceNumber";
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("RepID");
        pi.setValue(repid);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response = null;

        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();

            if (response != null && response.getPropertyCount() > 0) {
                SoapObject cheqTable = (SoapObject) response.getProperty(0);

                if (cheqTable != null && cheqTable.getPropertyCount() > 0) {

                    SoapObject discountDetails = (SoapObject) cheqTable.getProperty(0);
                    if (discountDetails.hasProperty("MaxInvoiceID")) {
                        DetailList = discountDetails.getProperty("MaxInvoiceID").toString();
                    }


                } else {
                    DetailList = "0";
                }
            } else {
                DetailList = response.toString();
            }



        } catch (SocketException e) {
            DetailList=null;
            // throw new SocketException(e.toString());
        } catch (Exception exception) {
            DetailList = null;
            Log.w("test123 fdf", "Errordd: " + exception.toString());
        }
        return DetailList;

    }

    public ArrayList<String> checkTransferAuditReuturn(String repId) throws SocketException {

        ArrayList<String> invoicesDetailList = null;
        final String SOAP_ACTION = "http://tempuri.org/xmlTransferReturnAudit";
        final String OPERATION_NAME = "xmlTransferReturnAudit";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
        PropertyInfo pi = new PropertyInfo();

        pi.setName("RepID");
        pi.setValue(repId);
        pi.setType(Integer.class);
        request.addProperty(pi);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response ;

        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.bodyIn;

            if (response != null && response.getPropertyCount() > 0) {
                SoapObject repTable = (SoapObject) response.getProperty(0);
                if (repTable != null && repTable.getPropertyCount() > 0) {
                    invoicesDetailList = new ArrayList<String>(repTable.getPropertyCount());
                    System.out.println("transfer result"+response);
                    for (int a = 0; a < repTable.getPropertyCount(); a++) {
                        SoapObject repDetails = (SoapObject) repTable.getProperty(a);
                        System.out.println("transfer result getProperty " + repDetails);
                        SoapObject repDetailsSecon = (SoapObject) repDetails.getProperty(a);
                        invoicesDetailList.add(repDetailsSecon.getProperty("InvoiceNo").toString());
                    }


                } else {
                    invoicesDetailList = null;
                }
            } else {
                invoicesDetailList = null;

            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            invoicesDetailList = null;
        }


        return invoicesDetailList;
    }

    public String checkInvoiceCountReturn(String repId) throws SocketException {

        String invoicesCount = null;
        final String SOAP_ACTION = "http://tempuri.org/xmlTransferReuturnAuditCurrentDatecount";
        final String OPERATION_NAME = "xmlTransferReuturnAuditCurrentDatecount";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
        PropertyInfo pi = new PropertyInfo();

        pi.setName("RepID");
        pi.setValue(repId);
        pi.setType(Integer.class);
        request.addProperty(pi);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;

        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();

            if (response != null && response.getPropertyCount() > 0) {
                SoapObject repTable = (SoapObject) response.getProperty(0);
                if (repTable != null && repTable.getPropertyCount() > 0) {
                    SoapObject repDetails = (SoapObject) repTable.getProperty(0);
                    invoicesCount=repDetails.getProperty("InvoiceCount").toString();

                } else {
                    invoicesCount = null;
                }
            } else {
                invoicesCount = null;

            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            invoicesCount = null;
        }


        return invoicesCount;
    }
    public ArrayList<String> getCheckInvoiceIdReturn(String repId,ArrayList<String>rowId) throws SocketException {

        ArrayList<String> invoicesDetailList = null;
        final String SOAP_ACTION = "http://tempuri.org/xmlTransferReturnAuditInvoiceDetails";
        final String OPERATION_NAME = "xmlTransferReturnAuditInvoiceDetails";




        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("RepID");
        pi.setValue(repId);
        pi.setType(Integer.class);
        request.addProperty(pi);


        SoapObject dataset = new SoapObject(WSDL_TARGET_NAMESPACE, "NewDataSet");


        for(int a=0;a<rowId.size();a++){
            SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE, "Table");
            table.addProperty("InvoiceID", rowId.get(a));
            dataset.addSoapObject(table);
        }



        SoapObject invoiceData = new SoapObject(WSDL_TARGET_NAMESPACE, "InvoiceDetails");
        invoiceData.addSoapObject(dataset);
        request.addSoapObject(invoiceData);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;


        try {

            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject)envelope.bodyIn;


            if (response != null && response.getPropertyCount() > 0) {
                SoapObject repTable = (SoapObject) response.getProperty(0);

                if (repTable != null && repTable.getPropertyCount() > 0) {
                    invoicesDetailList = new ArrayList<String>(repTable.getPropertyCount());

                    for (int a = 0; a < repTable.getPropertyCount(); a++) {
                        SoapObject repDetails = (SoapObject) repTable.getProperty(a);
                        SoapObject repTablesecond = (SoapObject) repDetails.getProperty(0);
                        invoicesDetailList.add(repTablesecond.getProperty("InvoiceID").toString());
                    }


                } else {
                    invoicesDetailList = null;
                }


            } else {

            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {

        }


        return invoicesDetailList;
    }


    //aduit customers
    public int AuditCustomerCount(String repId,String deviceId) throws SocketException {

        int customerCount = 0;
        final String SOAP_ACTION = "http://tempuri.org/xmlAuditCustomerCount";
        final String OPERATION_NAME = "xmlAuditCustomerCount";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
        PropertyInfo pi1 = new PropertyInfo();


        pi1.setName("DeviceID");
        pi1.setValue(deviceId);
        pi1.setType(String.class);
        request.addProperty(pi1);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("RepID");
        pi2.setValue(repId);
        pi2.setType(String.class);
        request.addProperty(pi2);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;

        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();

            if (response != null && response.getPropertyCount() > 0) {
                SoapObject repTable = (SoapObject) response.getProperty(0);
                if (repTable != null && repTable.getPropertyCount() > 0) {
                    SoapObject repDetails = (SoapObject) repTable.getProperty(0);
                    String cusCount=repDetails.getProperty("CustomerCount").toString();

                    try {
                        customerCount =Integer.parseInt(cusCount);
                    }catch (NumberFormatException numex){
                        customerCount = 0;
                    }catch (Exception ex){
                        customerCount = 0;
                    }

                } else {
                    customerCount = 0;
                }
            } else {
                customerCount = 0;

            }

        } catch (SocketException e) {
            customerCount = 0;
            throw new SocketException(e.toString());

        } catch (Exception exception) {
            customerCount = 0;
        }

        return customerCount;
    }


    public ArrayList<String[]> GetAuditCustomerList(String deviceId, String repId,ArrayList<String>rowId) throws SocketException {

        ArrayList<String[]> repDetailList = null;

        final String SOAP_ACTION = "http://tempuri.org/xmlAuditCustomerList";

        final String OPERATION_NAME = "xmlAuditCustomerList";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);



        PropertyInfo pi = new PropertyInfo();
        pi.setName("RepID");
        pi.setValue(repId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("DeviceID");
        pi2.setValue(deviceId);
        pi2.setType(String.class);
        request.addProperty(pi2);



        /*SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        Log.w("Log", "request toString : " + request.toString());

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS,TimeOut);
        SoapObject response;*/

        System.out.println("ddddddddddddddd    :"+rowId.size());



        SoapObject dataset = new SoapObject(WSDL_TARGET_NAMESPACE, "NewDataSet");

        if(rowId.size()==0){
            SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE, "Table");
            table.addProperty("CustomerID",null);
            dataset.addSoapObject(table);
        }else {
            for(int a=0;a<rowId.size();a++){
                SoapObject table = new SoapObject(WSDL_TARGET_NAMESPACE, "Table");
                table.addProperty("CustomerID", rowId.get(a));
                dataset.addSoapObject(table);
            }
        }


        SoapObject invoiceData = new SoapObject(WSDL_TARGET_NAMESPACE, "CustomerList");
        invoiceData.addSoapObject(dataset);

        request.addSoapObject(invoiceData);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        System.out.println("qqqqqqqqqqqqqq dddd :"+request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        SoapObject response;

        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();

            if (response != null && response.getPropertyCount() > 0) {

                Log.w("Log", "response count : " + response.getPropertyCount());
                Log.w("Log", "response Str : " + response.toString());

                SoapObject custTable = (SoapObject) response.getProperty(0);

                if (custTable != null && custTable.getPropertyCount() > 0) {

                    repDetailList = new ArrayList<String[]>(custTable.getPropertyCount());

                    for (int i = 0; i < custTable.getPropertyCount(); i++) {
                        String[] customerDetails = new String[37];//test
                        SoapObject custDetails = (SoapObject) custTable
                                .getProperty(i);

                        Log.w("Log",
                                "repDetails object : " + custDetails.toString());

                        if (custDetails.hasProperty("GLB_PharmacyID")) {
                            customerDetails[0] = custDetails.getProperty(
                                    "GLB_PharmacyID").toString();
                            customerDetails[1] = customerDetails[0];
                        }
                        if (custDetails.hasProperty("GLB_PharmacyID")) {
                            customerDetails[1] = custDetails.getProperty(
                                    "GLB_PharmacyID").toString();
                        }
                        if (custDetails.hasProperty("DealerID")) {
                            if (custDetails.getProperty(
                                    "DealerID").toString() != "anyType{}") {
                                customerDetails[2] = custDetails.getProperty(
                                        "DealerID").toString();
                            } else {
                                customerDetails[2] = "";
                            }

                        }

                        if (custDetails.hasProperty("CompanyCode")) {

                            if (custDetails.getProperty(
                                    "CompanyCode").toString() != "anyType{}") {
                                customerDetails[3] = custDetails.getProperty(
                                        "CompanyCode").toString();
                            } else {
                                customerDetails[3] = "";
                            }

                        }
                        if (custDetails.hasProperty("Name")) {

                            if (custDetails.getProperty(
                                    "Name").toString() != "anyType{}") {
                                customerDetails[4] = custDetails
                                        .getProperty("Name").toString();
                            } else {
                                customerDetails[4] = "";
                            }

                        }

                        if (custDetails.hasProperty("Address")) {

                            if (custDetails.getProperty(
                                    "Address").toString() != "anyType{}") {
                                customerDetails[5] = custDetails.getProperty(
                                        "Address").toString();
                            } else {
                                customerDetails[5] = "";
                            }


                        }

                        if (custDetails.hasProperty("District")) {

                            if (custDetails.getProperty(
                                    "District").toString() != "anyType{}") {
                                customerDetails[6] = custDetails.getProperty(
                                        "District").toString();
                            } else {
                                customerDetails[6] = "";
                            }
                        }

                        if (custDetails.hasProperty("Area")) {

                            if (custDetails.getProperty(
                                    "Area").toString() != "anyType{}") {
                                customerDetails[7] = custDetails
                                        .getProperty("Area").toString();
                            } else {
                                customerDetails[7] = "";
                            }

                        }

                        if (custDetails.hasProperty("Town")) {

                            if (custDetails.getProperty(
                                    "Town").toString() != "anyType{}") {
                                customerDetails[8] = custDetails
                                        .getProperty("Town").toString();
                            } else {
                                customerDetails[8] = "";
                            }
                        }

                        if (custDetails.hasProperty("Telephone")) {

                            if (custDetails.getProperty(
                                    "Telephone").toString() != "anyType{}") {
                                customerDetails[9] = custDetails.getProperty(
                                        "Telephone").toString();
                            } else {
                                customerDetails[9] = "";
                            }
                        }

                        if (custDetails.hasProperty("Fax")) {

                            if (custDetails.getProperty(
                                    "Fax").toString() != "anyType{}") {
                                customerDetails[10] = custDetails
                                        .getProperty("Fax").toString();
                            } else {
                                customerDetails[10] = "";
                            }
                        }

                        if (custDetails.hasProperty("Email")) {

                            if (custDetails.getProperty(
                                    "Email").toString() != "anyType{}") {
                                customerDetails[11] = custDetails.getProperty(
                                        "Email").toString();
                            } else {
                                customerDetails[11] = "";
                            }
                        }

                        if (custDetails.hasProperty("CustomerStatus")) {

                            if (custDetails.getProperty(
                                    "CustomerStatus").toString() != "anyType{}") {
                                customerDetails[12] = custDetails.getProperty(
                                        "CustomerStatus").toString();
                            } else {
                                customerDetails[12] = "";
                            }
                        }

                        if (custDetails.hasProperty("CreditLimit")) {

                            if (custDetails.getProperty(
                                    "CreditLimit").toString() != "anyType{}") {
                                customerDetails[13] = custDetails.getProperty(
                                        "CreditLimit").toString();
                            } else {
                                customerDetails[13] = "";
                            }
                        }

                        if (custDetails.hasProperty("CreditExpiryDate")) {

                            if (custDetails.getProperty(
                                    "CreditExpiryDate").toString() != "anyType{}") {
                                customerDetails[14] = custDetails.getProperty(
                                        "CreditExpiryDate").toString();
                            } else {
                                customerDetails[14] = "";
                            }
                        }

                        if (custDetails.hasProperty("CreditDuration")) {

                            if (custDetails.getProperty(
                                    "CreditDuration").toString() != "anyType{}") {
                                customerDetails[15] = custDetails.getProperty(
                                        "CreditDuration").toString();
                            } else {
                                customerDetails[15] = "";
                            }
                        }

                        if (custDetails.hasProperty("VATNo")) {

                            if (custDetails.getProperty(
                                    "VATNo").toString() != "anyType{}") {
                                customerDetails[16] = custDetails.getProperty(
                                        "VATNo").toString();
                            } else {
                                customerDetails[16] = "";
                            }
                        }

                        if (custDetails.hasProperty("Status")) {

                            if (custDetails.getProperty(
                                    "Status").toString() != "anyType{}") {
                                customerDetails[17] = custDetails.getProperty(
                                        "Status").toString();
                            } else {
                                customerDetails[17] = "";
                            }
                        }

                        if (custDetails.hasProperty("temp")) {

                            if (custDetails.getProperty(
                                    "temp").toString() != "anyType{}") {
                                customerDetails[18] = custDetails.getProperty(
                                        "temp").toString();
                            } else {
                                customerDetails[18] = "";
                            }
                        }

                        if (custDetails.hasProperty("CustomerNo")) {

                            if (custDetails.getProperty(
                                    "CustomerNo").toString() != "anyType{}") {
                                customerDetails[19] = custDetails.getProperty(
                                        "CustomerNo").toString();
                            } else {
                                customerDetails[19] = "";
                            }
                        }

                        if (custDetails.hasProperty("Web")) {

                            if (custDetails.getProperty(
                                    "Web").toString() != "anyType{}") {
                                customerDetails[20] = custDetails
                                        .getProperty("Web").toString();
                            } else {
                                customerDetails[20] = "";
                            }
                        }

                        if (custDetails.hasProperty("BrNo")) {

                            if (custDetails.getProperty(
                                    "BrNo").toString() != "anyType{}") {
                                customerDetails[21] = custDetails.getProperty(
                                        "BrNo").toString();
                            } else {
                                customerDetails[21] = "";
                            }
                        }

                        if (custDetails.hasProperty("OwnerContactNo")) {

                            if (custDetails.getProperty(
                                    "OwnerContactNo").toString() != "anyType{}") {
                                customerDetails[22] = custDetails.getProperty(
                                        "OwnerContactNo").toString();
                            } else {
                                customerDetails[22] = "";
                            }
                        }

                        if (custDetails.hasProperty("PharmacyRegNo")) {

                            if (custDetails.getProperty(
                                    "PharmacyRegNo").toString() != "anyType{}") {
                                customerDetails[23] = custDetails.getProperty(
                                        "PharmacyRegNo").toString();
                            } else {
                                customerDetails[23] = "";
                            }
                        }

                        if (custDetails.hasProperty("OwnersWifesBDay")) {

                            if (custDetails.getProperty(
                                    "OwnersWifesBDay").toString() != "anyType{}") {
                                customerDetails[24] = custDetails.getProperty(
                                        "OwnersWifesBDay").toString();
                            } else {
                                customerDetails[24] = "";
                            }
                        }

                        if (custDetails.hasProperty("PharmacistName")) {

                            if (custDetails.getProperty(
                                    "PharmacistName").toString() != "anyType{}") {
                                customerDetails[25] = custDetails.getProperty(
                                        "PharmacistName").toString();
                            } else {
                                customerDetails[25] = "";
                            }
                        }

                        if (custDetails.hasProperty("PuchasingOfficer")) {

                            if (custDetails.getProperty(
                                    "PuchasingOfficer").toString() != "anyType{}") {
                                customerDetails[26] = custDetails.getProperty(
                                        "PuchasingOfficer").toString();
                            } else {
                                customerDetails[26] = "";
                            }
                        }

                        if (custDetails.hasProperty("NoOfStaff")) {

                            if (custDetails.getProperty(
                                    "NoOfStaff").toString() != "anyType{}") {
                                customerDetails[27] = custDetails.getProperty(
                                        "NoOfStaff").toString();
                            } else {
                                customerDetails[27] = "";
                            }
                        }

                        if (custDetails.hasProperty("Latitude")) {

                            if (custDetails.getProperty(
                                    "Latitude").toString() != "anyType{}") {
                                customerDetails[28] = custDetails.getProperty(
                                        "Latitude").toString();
                            } else {
                                customerDetails[28] = "";
                            }
                        }

                        if (custDetails.hasProperty("Longitude")) {

                            if (custDetails.getProperty(
                                    "Longitude").toString() != "anyType{}") {
                                customerDetails[29] = custDetails.getProperty(
                                        "Longitude").toString();
                            } else {
                                customerDetails[29] = "";
                            }
                        }

                        if (custDetails.hasProperty("JobID")) {

                            if (custDetails.getProperty(
                                    "JobID").toString() != "anyType{}") {
                                customerDetails[30] = custDetails.getProperty(
                                        "JobID").toString();
                            } else {
                                customerDetails[30] = "";
                            }
                        }

                        if (custDetails.hasProperty("PrimaryImageId")) {

                            if (custDetails.getProperty(
                                    "PrimaryImageId").toString() != "anyType{}") {
                                customerDetails[31] = custDetails.getProperty(
                                        "PrimaryImageId").toString();
                            } else {
                                customerDetails[31] = "";
                            }
                        }

                        if (custDetails.hasProperty("CusImage")) {
                            if (custDetails.getProperty(
                                    "CusImage").toString() != "anyType{}") {
                                customerDetails[32] = custDetails.getProperty(
                                        "CusImage").toString();
                            } else {
                                customerDetails[32] = "";
                            }
                        }
                        if (custDetails.hasProperty("CurrentCredit")) {//test

                            System.out.println("CurrentCredit ji :"+custDetails.getProperty(
                                    "CurrentCredit").toString());
                            if (custDetails.getProperty(
                                    "CurrentCredit").toString() != "anyType{}") {
                                customerDetails[33] = custDetails.getProperty(
                                        "CurrentCredit").toString();
                            } else {
                                customerDetails[33] = "";
                            }
                        }if (custDetails.hasProperty("IsInvoiceAllowed")) {//test
                            System.out.println("IsInvoiceAllowed ji :"+custDetails.getProperty(
                                    "IsInvoiceAllowed").toString());

                            if (custDetails.getProperty("IsInvoiceAllowed").toString() != "anyType{}") {
                                customerDetails[34] = custDetails.getProperty("IsInvoiceAllowed").toString();
                            } else {
                                customerDetails[34] = "0";
                            }
                        }


                        if (custDetails.hasProperty("MaxInvoiceCount")) {//test

                            if (custDetails.getProperty("MaxInvoiceCount").toString() != "anyType{}") {
                                customerDetails[35] = custDetails.getProperty("MaxInvoiceCount").toString();
                            } else {
                                customerDetails[35] = "0";
                            }
                        }if (custDetails.hasProperty("CustomerBlocked")) {//test

                            if (custDetails.getProperty("CustomerBlocked").toString() != "anyType{}") {
                                customerDetails[36] = custDetails.getProperty("CustomerBlocked").toString();
                            } else {
                                customerDetails[36] = "0";
                            }
                        }

                        repDetailList.add(customerDetails);

                    }

                } else {
                    repDetailList = new ArrayList<String[]>(0);

                    Log.w("test123", "Error: 0 size zzz");
                }
            } else {
                repDetailList = new ArrayList<String[]>(0);
                Log.w("test123", "Error: 0 size xxx");
            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            repDetailList = null;
            Log.w("test123", "Error: " + exception.toString());
        }

        return repDetailList;
    }


    public ArrayList<String[]> getProductTarget(String deviceId, String repId) throws SocketException {

        ArrayList<String[]> productTargetDetailList = null;
        final String SOAP_ACTION = "http://tempuri.org/xmlGetRepMonthlyTarget";
        final String OPERATION_NAME = "xmlGetRepMonthlyTarget";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("DeviceID");
        pi.setValue(deviceId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("RepID");
        pi2.setValue(repId);
        pi2.setType(String.class);
        request.addProperty(pi2);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS,TimeOut);
        SoapObject response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();
            if (response != null && response.getPropertyCount() > 0) {
                SoapObject repStoreTable = (SoapObject) response.getProperty(0);

                if (repStoreTable != null && repStoreTable.getPropertyCount() > 0) {
                    productTargetDetailList = new ArrayList<String[]>(repStoreTable.getPropertyCount());
                    for (int i = 0; i < repStoreTable.getPropertyCount(); i++) {
                        String[] prodDetails = new String[9];
                        SoapObject prductDetails = (SoapObject) repStoreTable.getProperty(i);

                        if (prductDetails.hasProperty("Principle")) {
                            prodDetails[0] = prductDetails.getProperty("Principle").toString();
                        }
                        if (prductDetails.hasProperty("ItemCode")) {
                            prodDetails[1] = prductDetails.getProperty("ItemCode").toString();
                        }
                        if (prductDetails.hasProperty("TargetQty")) {
                            prodDetails[2] = prductDetails.getProperty("TargetQty").toString();
                        }

                        if (prductDetails.hasProperty("AchieveQty")) {
                            prodDetails[3] = prductDetails.getProperty("AchieveQty").toString();
                        }

                        if (prductDetails.hasProperty("Product")) {
                            prodDetails[4] = prductDetails.getProperty("Product").toString();
                        }

                        productTargetDetailList.add(prodDetails);


                    }

                } else {
                    productTargetDetailList = new ArrayList<String[]>(0);

                    Log.w("test123", "Error repStoreDetailList: 0 size zzz");
                }
            } else {
                productTargetDetailList = new ArrayList<String[]>(0);
                Log.w("test123", "Error repStoreDetailList: 0 size xxx");
            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            productTargetDetailList = null;
            Log.w("test123", "Error repStoreDetailList: " + exception.toString());
        }



        return productTargetDetailList;
    }



    public ArrayList<String[]> getInvoiceDelivery(String deviceId, String repId) throws SocketException {

        ArrayList<String[]> productTargetDetailList = null;
        final String SOAP_ACTION = "http://tempuri.org/xmlGetAllDeliveryStatus";
        final String OPERATION_NAME = "xmlGetAllDeliveryStatus";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("DeviceID");
        pi.setValue(deviceId);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("RepID");
        pi2.setValue(repId);
        pi2.setType(String.class);
        request.addProperty(pi2);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS,TimeOut);
        SoapObject response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = (SoapObject) envelope.getResponse();
            if (response != null && response.getPropertyCount() > 0) {
                SoapObject repStoreTable = (SoapObject) response.getProperty(0);

                if (repStoreTable != null && repStoreTable.getPropertyCount() > 0) {
                    productTargetDetailList = new ArrayList<String[]>(repStoreTable.getPropertyCount());
                    for (int i = 0; i < repStoreTable.getPropertyCount(); i++) {
                        String[] prodDetails = new String[7];
                        SoapObject prductDetails = (SoapObject) repStoreTable.getProperty(i);

                        if (prductDetails.hasProperty("InvoiceNo")) {
                            prodDetails[0] = prductDetails.getProperty("InvoiceNo").toString();
                        }
                        if (prductDetails.hasProperty("InvoiceDate")) {
                            prodDetails[1] = prductDetails.getProperty("InvoiceDate").toString();
                        }
                        if (prductDetails.hasProperty("CustomerNo")) {
                            prodDetails[2] = prductDetails.getProperty("CustomerNo").toString();
                        }

                        if (prductDetails.hasProperty("CustomerName")) {
                            prodDetails[3] = prductDetails.getProperty("CustomerName").toString();
                        }

                        if (prductDetails.hasProperty("Value")) {
                            prodDetails[4] = prductDetails.getProperty("Value").toString();
                        }

                        if (prductDetails.hasProperty("DeliveryStatus")) {
                            prodDetails[5] = prductDetails.getProperty("DeliveryStatus").toString();
                        }

                        if (prductDetails.hasProperty("Reason")) {
                            prodDetails[6] = prductDetails.getProperty("Reason").toString();
                        }


                        productTargetDetailList.add(prodDetails);


                    }

                } else {
                    productTargetDetailList = new ArrayList<String[]>(0);

                    Log.w("test123", "Error repStoreDetailList: 0 size zzz");
                }
            } else {
                productTargetDetailList = new ArrayList<String[]>(0);
                Log.w("test123", "Error repStoreDetailList: 0 size xxx");
            }

        } catch (SocketException e) {
            throw new SocketException(e.toString());
        } catch (Exception exception) {
            productTargetDetailList = null;
            Log.w("test123", "Error repStoreDetailList: " + exception.toString());
        }



        return productTargetDetailList;
    }

}
