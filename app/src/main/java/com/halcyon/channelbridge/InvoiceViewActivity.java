package com.halcyon.channelbridge;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.halcyon.channelbridgedb.Customers;
import com.halcyon.channelbridgedb.CustomersPendingApproval;
import com.halcyon.channelbridgedb.Invoice;
import com.halcyon.channelbridgedb.InvoicedProducts;
import com.halcyon.channelbridgedb.Itinerary;
import com.halcyon.channelbridgedb.ProductReturns;
import com.halcyon.channelbridgedb.Products;
import com.halcyon.channelbridgedb.Reps;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class InvoiceViewActivity extends Activity {

    String rowId, pharmacyId, invoiceId, customerName, custAddress;
    List<String[]> invoicedProducts = new ArrayList<String[]>();
    ArrayList<String> invoiceData = new ArrayList<String>();

    TextView tViewCustomerName, tViewInvoiceNumber, tViewAddress, tViewDate,
            tViewTotalItems, tViewTotalAmount, tViewCash, tViewCheque,
            tViewCredit, tViewRemain, tViewMarketReturn, tViewDiscount,
            tViewNeedToPay;
    TableLayout tblInvoicedItems;
    Button btnDone, btnPrint, btnCancel;


    private BaseFont bfBold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_view);
        tblInvoicedItems = (TableLayout) findViewById(R.id.tlInvoice);

        tViewCustomerName = (TextView) findViewById(R.id.tvCustomerName);
        tViewInvoiceNumber = (TextView) findViewById(R.id.tvInvoiceNumber);
        tViewAddress = (TextView) findViewById(R.id.tvAddress);
        tViewDate = (TextView) findViewById(R.id.tvDate);
        tViewTotalItems = (TextView) findViewById(R.id.tvTotalQuantity);
        tViewTotalAmount = (TextView) findViewById(R.id.tvTotal);
        tViewCash = (TextView) findViewById(R.id.tvCash);
        tViewCredit = (TextView) findViewById(R.id.tvCredit);
        tViewCheque = (TextView) findViewById(R.id.tvCheque);
        tViewRemain = (TextView) findViewById(R.id.tvRemain);
        tViewMarketReturn = (TextView) findViewById(R.id.tvMarketReturn);
        tViewNeedToPay = (TextView) findViewById(R.id.tvNeedToPay);
        tViewDiscount = (TextView) findViewById(R.id.tvDiscount);

        btnDone = (Button) findViewById(R.id.bDone);
        btnPrint = (Button) findViewById(R.id.bPrint);
        btnCancel = (Button) findViewById(R.id.bCancel);
        String repName = getRepName();


        if (savedInstanceState != null) {
            setBundleData(savedInstanceState);
        } else {
            getDataFromPreviousActivity();
        }


        getAllInvoicedProducts();
        setInitialData(invoicedProducts, invoiceData);
        populateInvoiceTable(invoicedProducts);

        btnPrint.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                String repName = getRepName();
                //generatePDF(customerName, custAddress, repName);
             printFunction(customerName, custAddress, repName);

            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent lastInvoice = new Intent(InvoiceViewActivity.this,
                        LastInvoiceActivity.class);
                finish();
                Bundle extras = new Bundle();
                extras.putString("Id", rowId);
                extras.putString("PharmacyId", pharmacyId);

                lastInvoice.putExtras(extras);
                startActivity(lastInvoice);
            }
        });

    }

    protected void printFunction(String custName, String address, String repName) {
        // TODO Auto-generated method stub
        try {


            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            boolean prePrintInvoiceFormatEnabled = sharedPreferences.getBoolean("cbPrefPrePrintInvoice", true);
            String repId = sharedPreferences.getString("RepId", "-1");

            if (prePrintInvoiceFormatEnabled) {


                // boolean flag = true;
                int count = 48;
                int spaceCount = 8;

                Invoice invoice = new Invoice(this);
                invoice.openReadableDatabase();
                ArrayList<String> invoiceD = invoice.getInvoiceDetailsByInvoiceId(invoiceId);
                invoice.closeDatabase();

                ArrayList<String[]> returnedProductList = new ArrayList<String[]>();


                Customers cus = new Customers(InvoiceViewActivity.this);
                cus.openReadableDatabase();
                String phone =cus.getCustomerPhoneByPharmacyId(pharmacyId);
                if(phone.equals("0")){
                    CustomersPendingApproval pen = new CustomersPendingApproval(InvoiceViewActivity.this);
                    pen.openReadableDatabase();
                    phone=pen.getCustomerPhoneByPharmacyId(pharmacyId);

                }else {

                }


                if (invoiceId != "") {

                    ProductReturns productReturns = new ProductReturns(InvoiceViewActivity.this);
                    productReturns.openReadableDatabase();
                    returnedProductList = productReturns.getReturnDetailsByInvoiceId(invoiceId);
                    productReturns.closeDatabase();

                }

                Reps reps = new Reps(this);
                reps.openReadableDatabase();
                ArrayList<String> delearDetails = reps.getRepDetailsForPrinting(repId);
                reps.closeDatabase();

                String dealerName = delearDetails.get(1).trim();
                String dealerCity = delearDetails.get(2).trim();
                String dealerTel = delearDetails.get(3).trim();


                String add[] =dealerCity.split("@");
                String addres[] =add[0].split(",");

                String telfax=add[1];
                String delerefax = null;
                System.out.println("dddddddddddddd : "+add[1]);

                String delereContactDetails[] =telfax.split("$");
                String delereNumber =delereContactDetails[0].trim();

                try {
                     delerefax =delereContactDetails[1].trim();
                }catch (Exception ex){
                    delereNumber=delereNumber.substring(0,(delereNumber.length()-1));
                    delerefax="";
                }
             //

                System.out.println("dddddddddddddd 255454 : "+delereContactDetails[0]);
                String invoiceValue = invoiceD.get(3);// IMPORTANT
                String returns = invoiceD.get(7);// IMPORTANT
                String teram = invoiceD.get(2);

                if (teram.equals("CQR")) {
                    teram = "Cheque";
                } else {
                    teram = "Cash";
                }


                String date = invoiceD.get(11).substring(0, 10);
                String printDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date());

                int customerNameRemain = 0;
                int addressRemain = 0;


                if (custName.length() > 24) {
                    custName = custName.substring(0, 25);
                } else {
                    customerNameRemain = 25 - custName.length();
                }
                customerNameRemain = customerNameRemain + 1;
                for (int i = 0; i <= customerNameRemain; i++) {
                    custName = custName + " ";
                }


                String repTelNo = "";
                for (int i = 0; i <= 6; i++) {
                    repTelNo = repTelNo + " ";
                }
                repTelNo = repTelNo + dealerTel;

                int custnameLength = custName.length();
                int addressLength = address.length();
                String cusMargin = "";
                String addressMargin = "";
                String nameMargin = "";

                String delerAdrresMarginone = "";
                String delerAdrresMargintwo = "";

                String delerPhoneMargin = "";
                String delerFaxMargin = "";

                String finalAddress=addres[1].trim()+","+addres[2].trim();

                for (int i = 0; i < 75 - delereNumber.length(); i++) {
                    delerPhoneMargin = delerPhoneMargin + " ";
                }

               /* for (int i = 0; i < 74 - delerefax.length(); i++) {
                    delerFaxMargin = delerFaxMargin + " ";
                }*/


                for (int i = 0; i < 75 - finalAddress.length(); i++) {
                    delerAdrresMargintwo = delerAdrresMargintwo + " ";
                }

                for (int i = 0; i < 75 - addres[0].length(); i++) {
                    delerAdrresMarginone = delerAdrresMarginone + " ";
                }

                for (int i = 0; i < 75 - dealerName.length(); i++) {
                    nameMargin = nameMargin + " ";
                }

                for (int i = 0; i < 60 - custnameLength; i++) {
                    cusMargin = cusMargin + " ";
                }
                for (int i = 0; i < 60 - addressLength; i++) {
                    addressMargin = addressMargin + " ";
                }

                if (invoiceId.length() == 1) {
                    invoiceId = "00" + invoiceId;
                } else if (invoiceId.length() == 2) {
                    invoiceId = "0" + invoiceId;
                } else {
                    invoiceId = invoiceId;
                }


                String po = "";

                String headerData = "\n";
                headerData = headerData + "                                      INVOICE \n";
                headerData = headerData + nameMargin+                                                     dealerName+"\n";
                headerData = headerData + delerAdrresMarginone+                                            addres[0]+"\n";
                headerData = headerData + delerAdrresMargintwo+                                            finalAddress+"\n";
                headerData = headerData + delerPhoneMargin+                                               delereNumber+"\n";
                headerData = headerData + delerFaxMargin+                                                delerefax+"\n";
                headerData = headerData + "CUSTOMER ADDRESS                                            Invoice No:" + invoiceId + "\n";
                headerData = headerData + custName + cusMargin + "Date:" + date + "\n";
                headerData = headerData + address + addressMargin + "P.O     :" + po + "\n";
                headerData = headerData + "TEL : " + phone+ "\n";
                headerData = headerData + "                                                            Tearm  :" + teram + "\n";
                headerData = headerData + "\n";
                headerData = headerData + "No Description               Batch  Expiry R.Pri W/Pric  Qty Fre Dis  Ammount  \n";
                headerData = headerData + "--------------------------------------------------------------------------------";
                headerData = headerData + "\n";

                String printData = "";
                int totalQty = 0;
                double totalProdsValue = 0;


                List<String[]> freeProducts = new ArrayList<String[]>();
                int itemcount = 1;
                for (String[] invoicedProduct : invoicedProducts) {

                    if (count == 48) {
                        if (printData.length() > 1) {

                            int k = spaceCount;

                            for (int i = 0; i <= k; i++) {
                                printData = printData + "\n";
                            }

                        }

                        printData = printData + headerData;
                        count = 0;
                    }


                    Products products = new Products(this);
                    products.openReadableDatabase();
                    String[] productdetail = products.getProductDetailsById(invoicedProduct[2]);
                    products.closeDatabase();

                    String productCode = productdetail[2];// IMPORTANT
                    String productDescription = productdetail[8];// IMPORTANT
                    String batch = invoicedProduct[3];// IMPORTANT


                    String expiry = invoicedProduct[11];

                    String discount = invoicedProduct[6];// IMPORTANT
                    String unitPrice = invoicedProduct[9];// IMPORTANT
                    String normal = invoicedProduct[7];// IMPORTANT
                    String free = invoicedProduct[5];// IMPORTANT
                    String retailsPrice = productdetail[14];
                    if (free != "" && Integer.parseInt(free) > 0) {
                        freeProducts.add(invoicedProduct);
                    }

                    totalQty = totalQty + Integer.parseInt(normal);// IMPORTANT

                    int qty = Integer.parseInt(normal);// IMPORTANT

                    double prodValue = (Integer.parseInt(normal) * Double.parseDouble(unitPrice)) * ((100 - Double.parseDouble(discount)) / 100);

                    totalProdsValue = totalProdsValue + prodValue;
                    String value = (String.format("%.2f", prodValue));
                    String quantityString = String.valueOf(qty);
                    String unitPriceString = String.valueOf(unitPrice);
                    String valueString = String.valueOf(value);

                    int quantityRemain = 0;
                    int unitPriceRemain = 0;
                    int valueRemain = 0;

                    productDescription = productDescription.trim();
                    quantityString = quantityString.trim();
                    unitPriceString = unitPriceString.trim();
                    valueString = valueString.trim();


                    if (quantityString.length() > 7) {
                        quantityString = quantityString.substring(0, 7);
                    }
                    if (unitPriceString.length() > 9) {
                        unitPriceString = unitPriceString.substring(0, 9);
                    }
                    if (valueString.length() > 11) {
                        valueString = valueString.substring(0, 11);
                    }

                    if (productDescription.length() > 44) {
                        productDescription = productDescription.substring(0, 44);
                    }

                    if (quantityString.length() < 7) {
                        quantityRemain = 6 - quantityString.length();
                    }

                    for (int i = 0; i <= quantityRemain; i++) {
                        quantityString = " " + quantityString;
                    }

                    if (unitPriceString.length() < 9) {
                        unitPriceRemain = 8 - unitPriceString.length();
                    }

                    for (int i = 0; i <= unitPriceRemain; i++) {
                        unitPriceString = " " + unitPriceString;
                    }

                    if (valueString.length() < 11) {
                        valueRemain = 10 - valueString.length();
                    }

                    for (int i = 0; i <= valueRemain; i++) {
                        valueString = " " + valueString;
                    }

                    //Himanshu

                    String itemno;
                    if (String.valueOf(itemcount).length() == 1) {
                        itemno = "0" + String.valueOf(itemcount);
                    } else {
                        itemno = String.valueOf(itemcount);
                    }

                    if (batch.length() == 1) {
                        batch = batch.substring(0, 1) + "     ";
                    } else if (batch.length() == 2) {
                        batch = batch.substring(0, 2) + "    ";
                    } else if (batch.length() == 3) {
                        batch = batch.substring(0, 3) + "   ";
                    } else if (batch.length() == 4) {
                        batch = batch.substring(0, 4) + "  ";
                    } else if (batch.length() == 5) {
                        batch = batch.substring(0, 5) + " ";
                    } else {
                        batch = batch.substring(0, 6);
                    }

                    String quantity;
                    if (String.valueOf(qty).length() == 1) {
                        quantity = "  " + String.valueOf(qty);
                    } else if (String.valueOf(qty).length() == 2) {
                        quantity = " " + String.valueOf(qty);
                    } else {
                        quantity = String.valueOf(qty);
                    }

                    if (productDescription.length() == 26) {

                    } else if (productDescription.length() > 26) {
                        productDescription = productDescription.substring(0, 26);
                    } else {
                        int prodetails = 26 - productDescription.length();
                        String prodetailsspace = "";
                        for (int i = 0; i < prodetails; i++) {
                            prodetailsspace = prodetailsspace + " ";
                        }
                        productDescription = productDescription + prodetailsspace;
                    }


                    String newRetailprice = retailsPrice.trim();
                    if (newRetailprice.length() == 1) {
                        newRetailprice = "      " + newRetailprice;
                    } else if (retailsPrice.trim().length() == 2) {
                        newRetailprice = "     " + newRetailprice;
                    } else if (retailsPrice.trim().length() == 3) {
                        newRetailprice = "    " + newRetailprice;
                    } else if (retailsPrice.trim().length() == 4) {
                        newRetailprice = "   " + newRetailprice;
                    } else if (retailsPrice.trim().length() == 5) {
                        newRetailprice = "  " + newRetailprice;
                    } else if (retailsPrice.trim().length() == 6) {
                        newRetailprice = " " + newRetailprice;
                    } else {
                        newRetailprice = newRetailprice.substring(0, 7);
                    }
                    String newunitprice = unitPriceString.trim();
                    if (newunitprice.length() == 1) {
                        newunitprice = "      " + newunitprice;
                    } else if (newunitprice.trim().length() == 2) {
                        newunitprice = "     " + newunitprice;
                    } else if (newunitprice.trim().length() == 3) {
                        newunitprice = "    " + newunitprice;
                    } else if (newunitprice.trim().length() == 4) {
                        newunitprice = "   " + newunitprice;
                    } else if (newunitprice.trim().length() == 5) {
                        newunitprice = "  " + newunitprice;
                    } else if (newunitprice.trim().length() == 6) {
                        newunitprice = " " + newunitprice;
                    } else {
                        newunitprice = newunitprice.substring(0, 7);
                    }


                    if (free.length() == 1) {
                        free = "  " + free;
                    } else if (free.length() == 2) {
                        free = " " + free;
                    } else if (free.length() == 3) {
                        free = free;
                    }


                    // printData = printData + itemno + " " + productDescription + " " + batch + " " + expiry.substring(2, 10) + " " + newRetailprice+ " " + newunitprice + " " + quantity + " "+free+" "+ discount + " " + valueString.trim() + "" + "\n";
                    printData = printData + itemno + " " + productDescription + "             " + newRetailprice + " " + newunitprice + " " + quantity + " " + free + " " + discount + "" + valueString + "" + "\n";

                    itemcount++;

                    count = count + 2;
                    Log.w("COUNT", count + "lines");

                }//


                double discountedPrice = (Float.parseFloat(invoiceValue) / 100)
                        * Double.parseDouble(invoiceD.get(8));


                double totalDiscountedValue = (discountedPrice + Float
                        .parseFloat(returns));


                double needToPay = Float.parseFloat(invoiceValue)
                        - totalDiscountedValue;
                String needToPayString = String.format("%.2f", needToPay);

                Log.w("IG3", "needToPay 332 " + needToPay);

                String totalQt = String.valueOf(totalQty);
                String invoiceVal = String.format("%.2f", totalProdsValue);

                if (invoiceVal.length() > 9) {
                    invoiceVal = invoiceVal.substring(0, 9);
                }

                int invoiceValRemainRemain = 0;
                if (invoiceVal.length() < 11) {
                    invoiceValRemainRemain = 11 - invoiceVal.length();
                }

                int quantityRemain = 0;

                if (totalQt.length() < 7) {
                    quantityRemain = 6 - totalQt.length();
                }

                for (int i = 0; i <= quantityRemain; i++) {
                    totalQt = " " + totalQt;
                }

                invoiceValRemainRemain = invoiceValRemainRemain + 10;

                for (int i = 0; i <= invoiceValRemainRemain; i++) {
                    invoiceVal = " " + invoiceVal;
                }

                if (count < 45) {
                    printData = printData + "\n";
                    printData = printData + "--------------------------------------------------------------------------------\n";
                    printData = printData + "Total                                                " + totalQt + "          " + invoiceVal.trim() + "\n";

                    count = count + 3;
                } else {

                    int k = 48 - count;
                    k = k + spaceCount;

                    for (int i = 0; i <= k; i++) {
                        printData = printData + "\n";
                    }

                    printData = printData + headerData;
                    count = 0;

                    printData = printData + "\n";
                    printData = printData + "--------------------------------------------------------------------------------\n";
                    printData = printData + "Total                                                " + totalQt + "          " + invoiceVal.trim() + "\n";


                    count = count + 3;
                }

                if (returnedProductList.size() > 0) {

                    if (count < 45) {

                        printData = printData + "\n Returned Products\n";

                        printData = printData + "------------------------------------------------------------------------------------";
                        printData = printData + "\n";

                        count = count + 3;

                    } else {

                        int k = 48 - count;
                        k = k + spaceCount;

                        for (int i = 0; i <= k; i++) {
                            printData = printData + "\n";
                        }

                        printData = printData + headerData;
                        count = 0;

                        printData = printData + "\n Returned Products\n";

                        printData = printData + "-------------------------------------------------------------------------------------";
                        printData = printData + "\n";

                        count = count + 3;

                    }

                    for (String[] selectedProduct : returnedProductList) {

                        if (count == 48) {
                            if (printData.length() > 1) {

                                int k = spaceCount;

                                for (int i = 0; i <= k; i++) {
                                    printData = printData + "\n";
                                }
                            }

                            printData = printData + headerData;
                            count = 0;
                        }

                        int quantityReturnRemain = 0;
                        int priceReturnRemain = 0;
                        int valueReturnRemain = 0;

                        int normal = 0, free = 0;
                        if (selectedProduct[4] != "") {
                            normal = Integer.parseInt(selectedProduct[4]);
                        }

                        if (selectedProduct[5] != "") {
                            free = Integer.parseInt(selectedProduct[5]);
                        }

                        double price = 0;
                        if (selectedProduct[8] != "") {
                            price = Double.parseDouble(selectedProduct[8]);
                        }

                        double discountVal = 0;
                        if (selectedProduct[10] != "") {
                            discountVal = Double.parseDouble(selectedProduct[10]);
                        }

                        String quantityReturnString = String.valueOf(normal + free);
                        String priceReturnString = String.valueOf(price);

                        double prodDiscountValue = 0;
                        if (discountVal > 0) {
                            prodDiscountValue = (normal * price) / 100
                                    * discountVal;
                        }

                        String valueReturnString = String.format("%.2f",
                                (normal * price) - prodDiscountValue);

                        if (quantityReturnString.length() < 7) {
                            quantityReturnRemain = 6 - quantityReturnString
                                    .length();
                        }
                        if (priceReturnString.length() < 9) {
                            priceReturnRemain = 8 - priceReturnString.length();
                        }
                        if (valueReturnString.length() < 10) {
                            valueReturnRemain = 10 - valueReturnString.length();
                        }

                        for (int i = 0; i <= quantityReturnRemain; i++) {
                            quantityReturnString = " " + quantityReturnString;
                        }
                        for (int i = 0; i <= priceReturnRemain; i++) {
                            priceReturnString = " " + priceReturnString;
                        }
                        for (int i = 0; i <= valueReturnRemain; i++) {
                            valueReturnString = " " + valueReturnString;
                        }

                        printData = printData + selectedProduct[9] + "\n";
                        printData = printData + "              " + (quantityReturnString) + " " + priceReturnString + " " + (valueReturnString) + "\n";

                        count = count + 2;
                        Log.w("COUNT", count + "lines");

                    }

                    printData = printData + "---------------------------------------------------------------------------------------------------------";
                    printData = printData + "\n";
                    count++;

                }


                printData = printData + "\n";
                count++;

                String footerData = "";

                double discountValue = 0;

                if (invoiceD.get(8) != ""
                        && Double.parseDouble(invoiceD.get(8)) > 0) {
                    double invoiceTotalVal = Double.parseDouble(invoiceD.get(3));
                    double invoiceDiscount = Double.parseDouble(invoiceD.get(8));
                    discountValue = (invoiceTotalVal / 100) * invoiceDiscount;
                }

                String craditduration = invoiceD.get(13);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Calendar c = Calendar.getInstance(); // Get Calendar Instance
                c.setTime(sdf.parse(date));
                c.add(Calendar.DATE, Integer.parseInt(craditduration));  // add 45 days
                sdf = new SimpleDateFormat("dd/MM/yyyy");

                Date resultdate = new Date(c.getTimeInMillis());   // Get new time
                String calculateDuration = sdf.format(resultdate);


                if ((count + 17) < 48) {

                    footerData = footerData + "--------------------------------------------------------------------------------\n";
                    footerData = footerData + "\n";
                    footerData = footerData + "PAYMENT BY 'ACCOUNT PAYEE' CHEQUES DRAWN IN FAVOUR OF "+dealerName+"   \n";
                    footerData = footerData + "Short dated products must be returned before 4 months of expiary date\n";
                    footerData = footerData + "THIS INVOICE IS DUE FOR SETTLEMENT ON OR BEFORE : " + calculateDuration + " \n\n";
                    footerData = footerData + "                                                      Received Correct Products\n";
                    footerData = footerData + "                                                           & Quantities\n\n";
                    footerData = footerData + "                                                      --------------------------\n";
                    footerData = footerData + "Printed by :" + getRepName() + " Tel : " + repTelNo.trim() + "\n";
                    footerData = footerData + "Software Provided By Mobitel (Pvt) Ltd - +94(0)712755777.\n";

                    printData = printData + footerData;

                } else {
                    footerData = footerData + "--------------------------------------------------------------------------------\n";
                    footerData = footerData + "\n";
                    footerData = footerData + "PAYMENT BY 'ACCOUNT PAYEE' CHEQUES DRAWN IN FAVOUR OF "+dealerName+"   \n";
                    footerData = footerData + "THIS INVOICE IS DUE FOR SETTLEMENT ON OR BEFORE\n\n";
                    footerData = footerData + "                                                      Received Correct Products\n";
                    footerData = footerData + "                                                           & Quantities\n\n";
                    footerData = footerData + "                                                      --------------------------\n";
                    footerData = footerData + "Printed by :" + dealerName + "\n";
                    footerData = footerData + "Software Provided By Mobitel (Pvt) Ltd - +94(0)712755777.\n";

                    int k = 48 - count;
                    k = k + spaceCount;

                    for (int i = 0; i <= k; i++) {
                        printData = printData + "\n";
                    }


                    printData = printData + footerData;

                }

                Bundle bundleToView = new Bundle();
                bundleToView.putString("PrintData", printData);

                // Print invoice

                Intent activityIntent = new Intent(getApplicationContext(), PrintUtility.class);
                activityIntent.putExtras(bundleToView);
                startActivityForResult(activityIntent, 0);


            } else {


                // boolean flag = true;
                int count = 14;
//				int spaceCount = 8;

                Invoice invoice = new Invoice(this);
                invoice.openReadableDatabase();
                ArrayList<String> invoiceD = invoice
                        .getInvoiceDetailsByInvoiceId(invoiceId);
                invoice.closeDatabase();

                ArrayList<String[]> returnedProductList = new ArrayList<String[]>();

                if (invoiceId != "") {

                    ProductReturns productReturns = new ProductReturns(
                            InvoiceViewActivity.this);
                    productReturns.openReadableDatabase();
                    returnedProductList = productReturns
                            .getReturnDetailsByInvoiceId(invoiceId);
                    productReturns.closeDatabase();

                }

                Reps reps = new Reps(this);
                reps.openReadableDatabase();
                ArrayList<String> delearDetails = reps
                        .getRepDetailsForPrinting(repId);
                reps.closeDatabase();

                String dealerName = delearDetails.get(1).trim();
                String dealerCity = delearDetails.get(2).trim();
                String dealerTel = delearDetails.get(3).trim();

                if (dealerName.length() > 18) {
                    dealerName = dealerName.substring(0, 18);
                }

                if (dealerCity.length() > 18) {
                    dealerCity = dealerCity.substring(0, 18);
                }

                String invoiceValue = invoiceD.get(3);// IMPORTANT
                String returns = invoiceD.get(7);// IMPORTANT

                Log.w("IG3", "invoiceD.get(11) 332 " + invoiceD.get(11));

                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss.SSS");

                // Date dateObj = dateFormat.parse(invoiceD.get(11));

                // String date = new SimpleDateFormat("yyyy-MM-dd").format(dateObj);
                String date = invoiceD.get(11).substring(0, 10);
                // String time =new SimpleDateFormat("hh:mm:ss a").format(dateObj);
                String printDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a")
                        .format(new Date());

                int customerNameRemain = 0;
                int addressRemain = 0;

                if (custName.length() > 24) {
                    custName = custName.substring(0, 25);
                } else {
                    customerNameRemain = 25 - custName.length();
                }
                customerNameRemain = customerNameRemain + 1;
                for (int i = 0; i <= customerNameRemain; i++) {
                    custName = custName + " ";
                }

                if (address.length() > 24) {
                    address = address.substring(0, 25);
                } else {
                    addressRemain = 25 - address.length();
                }
                addressRemain = addressRemain + 1;
                for (int i = 0; i <= addressRemain; i++) {
                    address = address + " ";
                }

                String headerData = "";
                headerData = headerData + dealerName + "\n";
                headerData = headerData + dealerCity + "\n";
                headerData = headerData + "Tel: " + dealerTel + "\n";
                headerData = headerData + "\n\n";


                headerData = headerData + "Invoice To\n";
                headerData = headerData + custName + "Invoice No: " + invoiceId + "\n";
                headerData = headerData + address + "Date :" + date + "\n";
                headerData = headerData + "\n\n";

                String printData = "";
                int totalQty = 0;
                double totalProdsValue = 0;

                int invoicePageCount = 1;

                printData = printData + headerData;

                printData = printData + "Description       Qty     Price       Value\n";
                printData = printData
                        + "--------------------------------------------";
                printData = printData + "\n";

                List<String[]> freeProducts = new ArrayList<String[]>();

                for (String[] invoicedProduct : invoicedProducts) {

                    if (count == 60) {

                        printData = printData + "\nPage " + invoicePageCount + "\n\n\n\n\n";
                        invoicePageCount++;
                        count = 0;
                    }

                    Products products = new Products(this);
                    products.openReadableDatabase();
                    String[] productdetail = products
                            .getProductDetailsById(invoicedProduct[2]);
                    products.closeDatabase();

                    String productCode = productdetail[2];// IMPORTANT
                    String productDescription = productdetail[8];// IMPORTANT
                    String batch = invoicedProduct[3];// IMPORTANT


                    String expiry = invoicedProduct[11];

                    String discount = invoicedProduct[6];// IMPORTANT
                    String unitPrice = invoicedProduct[9];// IMPORTANT
                    String normal = invoicedProduct[7];// IMPORTANT
                    String free = invoicedProduct[5];// IMPORTANT

                    if (free != "" && Integer.parseInt(free) > 0) {
                        freeProducts.add(invoicedProduct);
                    }

                    totalQty = totalQty + Integer.parseInt(normal);// IMPORTANT

                    int qty = Integer.parseInt(normal);// IMPORTANT

                    double prodValue = (Integer.parseInt(normal) * Double
                            .parseDouble(unitPrice))
                            * ((100 - Double.parseDouble(discount)) / 100);

                    totalProdsValue = totalProdsValue + prodValue;

                    String value = (String.format("%.2f", prodValue));
                    String quantityString = String.valueOf(qty);
                    String unitPriceString = String.valueOf(unitPrice);
                    String valueString = String.valueOf(value);

                    int quantityRemain = 0;
                    int unitPriceRemain = 0;
                    int valueRemain = 0;

                    productDescription = productDescription.trim();
                    quantityString = quantityString.trim();
                    unitPriceString = unitPriceString.trim();
                    valueString = valueString.trim();

                    Log.w("SIZE",
                            "quantityString size : " + quantityString.length());
                    Log.w("SIZE",
                            "unitPriceString size : " + unitPriceString.length());
                    Log.w("SIZE", "valueString size : " + valueString.length());

                    if (quantityString.length() > 7) {
                        quantityString = quantityString.substring(0, 7);
                    }
                    if (unitPriceString.length() > 9) {
                        unitPriceString = unitPriceString.substring(0, 9);
                    }
                    if (valueString.length() > 11) {
                        valueString = valueString.substring(0, 11);
                    }

                    if (productDescription.length() > 44) {
                        productDescription = productDescription.substring(0, 44);
                    }

                    if (quantityString.length() < 7) {
                        quantityRemain = 6 - quantityString.length();
                    }

                    for (int i = 0; i <= quantityRemain; i++) {
                        quantityString = " " + quantityString;
                    }

                    if (unitPriceString.length() < 9) {
                        unitPriceRemain = 8 - unitPriceString.length();
                    }

                    for (int i = 0; i <= unitPriceRemain; i++) {
                        unitPriceString = " " + unitPriceString;
                    }

                    if (valueString.length() < 11) {
                        valueRemain = 10 - valueString.length();
                    }

                    for (int i = 0; i <= valueRemain; i++) {
                        valueString = " " + valueString;
                    }

                    printData = printData + productDescription + "\n";
                    printData = printData + "              " + quantityString + " "
                            + unitPriceString + " " + valueString + "\n";

                    count = count + 2;
                    Log.w("COUNT", count + "lines");

                }

                // Log.w("IG3", "discount 332 " +
                // Integer.parseInt(invoiceD.get(8)));

                double discountedPrice = (Float.parseFloat(invoiceValue) / 100)
                        * Double.parseDouble(invoiceD.get(8));

                Log.w("IG3", "discountedPrice 332 " + discountedPrice);

                double totalDiscountedValue = (discountedPrice + Float
                        .parseFloat(returns));

                Log.w("IG3", "totalDiscountedValue 332 " + totalDiscountedValue);

                Log.w("IG3", "total 332 " + count);

                double needToPay = Float.parseFloat(invoiceValue)
                        - totalDiscountedValue;
                String needToPayString = String.format("%.2f", needToPay);

                Log.w("IG3", "needToPay 332 " + needToPay);

                String totalQt = String.valueOf(totalQty);
                String invoiceVal = String.format("%.2f", totalProdsValue);

                if (invoiceVal.length() > 9) {
                    invoiceVal = invoiceVal.substring(0, 9);
                }

                int invoiceValRemainRemain = 0;
                if (invoiceVal.length() < 11) {
                    invoiceValRemainRemain = 11 - invoiceVal.length();
                }

                int quantityRemain = 0;

                if (totalQt.length() < 7) {
                    quantityRemain = 6 - totalQt.length();
                }

                for (int i = 0; i <= quantityRemain; i++) {
                    totalQt = " " + totalQt;
                }

                invoiceValRemainRemain = invoiceValRemainRemain + 10;

                for (int i = 0; i <= invoiceValRemainRemain; i++) {
                    invoiceVal = " " + invoiceVal;
                }

                if (count < 57) {
                    printData = printData
                            + "--------------------------------------------\n";
                    printData = printData + "Total        " + " " + totalQt
                            + invoiceVal + "\n";
                    printData = printData
                            + "--------------------------------------------\n";

                    count = count + 3;
                } else {

                    int k = 60 - count;
                    //k = k + spaceCount;

                    for (int i = 0; i <= k; i++) {
                        printData = printData + "\n";
                    }

                    printData = printData + "\nPage " + invoicePageCount + "\n\n\n\n\n";
                    invoicePageCount++;
                    count = 0;


                    printData = printData
                            + "--------------------------------------------\n";
                    printData = printData + "Total        " + " " + totalQt
                            + invoiceVal + "\n";
                    printData = printData
                            + "--------------------------------------------\n";
                    count = count + 3;
                }

                if (returnedProductList.size() > 0) {

                    if (count < 57) {

                        printData = printData + "\n Returned Products\n";

                        printData = printData
                                + "--------------------------------------------";
                        printData = printData + "\n";

                        count = count + 3;

                    } else {

                        int k = 60 - count;
                        //k = k + spaceCount;

                        for (int i = 0; i <= k; i++) {
                            printData = printData + "\n";
                        }

                        printData = printData + "\nPage " + invoicePageCount + "\n\n\n\n\n";
                        invoicePageCount++;
                        count = 0;

                        printData = printData + "\n Returned Products\n";

                        printData = printData
                                + "--------------------------------------------";
                        printData = printData + "\n";

                        count = count + 3;

                    }

                    for (String[] selectedProduct : returnedProductList) {

                        if (count == 60) {
                            printData = printData + "\nPage " + invoicePageCount + "\n\n\n\n\n";
                            invoicePageCount++;
                            count = 0;
                        }

                        int quantityReturnRemain = 0;
                        int priceReturnRemain = 0;
                        int valueReturnRemain = 0;

                        int normal = 0, free = 0;
                        if (selectedProduct[4] != "") {
                            normal = Integer.parseInt(selectedProduct[4]);
                        }

                        if (selectedProduct[5] != "") {
                            free = Integer.parseInt(selectedProduct[5]);
                        }

                        double price = 0;
                        if (selectedProduct[8] != "") {
                            price = Double.parseDouble(selectedProduct[8]);
                        }

                        double discountVal = 0;
                        if (selectedProduct[10] != "") {
                            discountVal = Double.parseDouble(selectedProduct[10]);
                        }

                        String quantityReturnString = String.valueOf(normal + free);
                        String priceReturnString = String.valueOf(price);

                        double prodDiscountValue = 0;
                        if (discountVal > 0) {
                            prodDiscountValue = (normal * price) / 100
                                    * discountVal;
                        }

                        String valueReturnString = String.format("%.2f",
                                (normal * price) - prodDiscountValue);

                        if (quantityReturnString.length() < 7) {
                            quantityReturnRemain = 6 - quantityReturnString
                                    .length();
                        }
                        if (priceReturnString.length() < 9) {
                            priceReturnRemain = 8 - priceReturnString.length();
                        }
                        if (valueReturnString.length() < 10) {
                            valueReturnRemain = 10 - valueReturnString.length();
                        }

                        for (int i = 0; i <= quantityReturnRemain; i++) {
                            quantityReturnString = " " + quantityReturnString;
                        }
                        for (int i = 0; i <= priceReturnRemain; i++) {
                            priceReturnString = " " + priceReturnString;
                        }
                        for (int i = 0; i <= valueReturnRemain; i++) {
                            valueReturnString = " " + valueReturnString;
                        }

                        printData = printData + selectedProduct[9] + "\n";
                        printData = printData + "              "
                                + (quantityReturnString) + " " + priceReturnString
                                + " " + (valueReturnString) + "\n";

                        count = count + 2;
                        Log.w("COUNT", count + "lines");

                    }

                    printData = printData
                            + "--------------------------------------------";
                    printData = printData + "\n";
                    count++;

                }

                if (freeProducts.size() > 0) {

                    if (count < 57) {

                        printData = printData
                                + "\n Free Issues or Special Discount\n";

                        printData = printData
                                + "--------------------------------------------";
                        printData = printData + "\n";

                        count = count + 3;

                    } else {

                        int k = 60 - count;
//						k = k + spaceCount;

                        for (int i = 0; i <= k; i++) {
                            printData = printData + "\n";
                        }

                        printData = printData + "\nPage " + invoicePageCount + "\n\n\n\n\n";
                        invoicePageCount++;
                        count = 0;

                        printData = printData
                                + "\n Free Issues or Special Discount\n";

                        printData = printData
                                + "--------------------------------------------";
                        printData = printData + "\n";

                        count = count + 3;

                    }

                    for (String[] invoicedProduct : freeProducts) {

                        if (count == 60) {
                            printData = printData + "\nPage " + invoicePageCount + "\n\n\n\n\n";
                            invoicePageCount++;
                            count = 0;
                        }

                        Products products = new Products(this);
                        products.openReadableDatabase();
                        String[] productdetail = products
                                .getProductDetailsById(invoicedProduct[2]);
                        products.closeDatabase();

                        String productCode = productdetail[2];// IMPORTANT
                        String productDescription = productdetail[8];// IMPORTANT
                        String batch = invoicedProduct[3];// IMPORTANT

                        String expiry = invoicedProduct[11];

                        String discount = invoicedProduct[6];// IMPORTANT
                        String unitPrice = invoicedProduct[9];// IMPORTANT
                        String normal = invoicedProduct[7];// IMPORTANT
                        String free = invoicedProduct[5];// IMPORTANT

                        int qty = Integer.parseInt(free);// IMPORTANT

                        String quantityString = String.valueOf(qty);
                        String unitPriceString = "0";
                        String valueString = "0";

                        int qtyRemain = 0;
                        int unitPriceRemain = 0;
                        int valueRemain = 0;

                        productDescription = productDescription.trim();
                        quantityString = quantityString.trim();
                        unitPriceString = unitPriceString.trim();
                        valueString = valueString.trim();

                        Log.w("SIZE",
                                "quantityString size : " + quantityString.length());
                        Log.w("SIZE",
                                "unitPriceString size : "
                                        + unitPriceString.length());
                        Log.w("SIZE", "valueString size : " + valueString.length());

                        if (quantityString.length() > 7) {
                            quantityString = quantityString.substring(0, 7);
                        }
                        if (unitPriceString.length() > 9) {
                            unitPriceString = unitPriceString.substring(0, 9);
                        }
                        if (valueString.length() > 11) {
                            valueString = valueString.substring(0, 11);
                        }

                        if (productDescription.length() > 44) {
                            productDescription = productDescription
                                    .substring(0, 44);
                        }

                        if (quantityString.length() < 7) {
                            qtyRemain = 6 - quantityString.length();
                        }

                        for (int i = 0; i <= qtyRemain; i++) {
                            quantityString = " " + quantityString;
                        }

                        if (unitPriceString.length() < 9) {
                            unitPriceRemain = 8 - unitPriceString.length();
                        }

                        for (int i = 0; i <= unitPriceRemain; i++) {
                            unitPriceString = " " + unitPriceString;
                        }

                        if (valueString.length() < 11) {
                            valueRemain = 10 - valueString.length();
                        }

                        for (int i = 0; i <= valueRemain; i++) {
                            valueString = " " + valueString;
                        }

                        printData = printData + productDescription + "\n";
                        printData = printData + "              " + quantityString
                                + " " + unitPriceString + " " + valueString + "\n";

                        count = count + 2;
                        Log.w("COUNT", count + "lines");

                    }

                    printData = printData
                            + "--------------------------------------------";
                    printData = printData + "\n";
                    count++;

                }

                printData = printData + "\n";
                count++;

                String footerData = "";

                double discountValue = 0;

                if (invoiceD.get(8) != ""
                        && Double.parseDouble(invoiceD.get(8)) > 0) {
                    double invoiceTotalVal = Double.parseDouble(invoiceD.get(3));
                    double invoiceDiscount = Double.parseDouble(invoiceD.get(8));
                    discountValue = (invoiceTotalVal / 100) * invoiceDiscount;
                }

                if ((count + 17) < 60) {

                    footerData = footerData + "Gross Value : " + invoiceD.get(3)
                            + "\n";
                    footerData = footerData + "Discount    : " + invoiceD.get(8)
                            + "%  (" + String.format("%.2f", discountValue) + ")\n";
                    footerData = footerData + "Return Value: " + returns + "\n";
                    footerData = footerData + "Free/Special: 0\n";
                    footerData = footerData + "Need to pay : " + needToPayString
                            + "\n";

                    footerData = footerData + "\n\n";
                    footerData = footerData + "-----------------------------\n";
                    footerData = footerData + "  Customer Signature & Seal\n\n";

                    footerData = footerData + "Print Date & Time : "
                            + printDateTime + "\n\n";

                    footerData = footerData
                            + "Software By eMerge Solutions - 0115 960 960\n";

                    printData = printData + footerData;

                } else {

                    footerData = footerData + "Gross Value : " + invoiceD.get(3)
                            + "\n";
                    footerData = footerData + "Discount    : " + invoiceD.get(8)
                            + "%  (" + String.format("%.2f", discountValue) + ")\n";
                    footerData = footerData + "Return Value: " + returns + "\n";
                    footerData = footerData + "Free/Special: 0\n";
                    footerData = footerData + "Need to pay : " + needToPayString
                            + "\n";

                    footerData = footerData + "\n\n";
                    footerData = footerData + "-----------------------------\n";
                    footerData = footerData + "  Customer Signature & Seal\n\n";

                    footerData = footerData + "Print Date & Time : "
                            + printDateTime + "\n\n";

                    footerData = footerData
                            + "Software By eMerge Solutions - 0115 960 960\n";

                    int k = 60 - count;
//					k = k + spaceCount;

                    for (int i = 0; i <= k; i++) {
                        printData = printData + "\n";
                    }

                    printData = printData + "\nPage " + invoicePageCount + "\n\n\n\n\n";
                    invoicePageCount++;


//					printData = printData + headerData;
                    printData = printData + footerData;

                }

                Bundle bundleToView = new Bundle();
                bundleToView.putString("PrintData", printData);
                System.out.println(printData);

                // Print invoice

                Intent activityIntent = new Intent(getApplicationContext(),
                        PrintUtility.class);
                activityIntent.putExtras(bundleToView);
                startActivityForResult(activityIntent, 0);


            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void getAllInvoicedProducts() {
        // TODO Auto-generated method stub
        InvoicedProducts invoicedProductsObject = new InvoicedProducts(this);
        invoicedProductsObject.openReadableDatabase();
        invoicedProducts = invoicedProductsObject.getInvoicedProductsByInvoiceId(invoiceId);
        invoicedProductsObject.closeDatabase();

        Invoice invoice = new Invoice(this);
        invoice.openReadableDatabase();
        invoiceData = invoice.getInvoiceDetailsByInvoiceId(invoiceId);
        invoice.closeDatabase();

    }

    private void populateInvoiceTable(List<String[]> invProducts) {
        // TODO Auto-generated method stub
        TableRow tr;
        tblInvoicedItems.setShrinkAllColumns(true);

        try {

            int count = 1;
            for (String[] invoicedProduct : invoicedProducts) {
                Log.w("called", "inside populate for");

                tr = new TableRow(this);
                tr.setId(1000 + count);
                tr.setPadding(4, 4, 4, 4);
                tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                        LayoutParams.WRAP_CONTENT));

                if (count % 2 != 0) {
                    tr.setBackgroundColor(Color.DKGRAY);

                }

                Products products = new Products(this);
                products.openReadableDatabase();
                String[] productdetail = products
                        .getProductDetailsById(invoicedProduct[2]);
                products.closeDatabase();

                TextView tvProductDescription = new TextView(this);
                tvProductDescription.setId(200 + count);
                tvProductDescription.setText(productdetail[8]);
                tvProductDescription.setGravity(Gravity.LEFT);
                tvProductDescription.setPadding(3, 3, 3, 3);
                tvProductDescription.setTextColor(Color.WHITE);
                tr.addView(tvProductDescription);

                TextView tvPrice = new TextView(this);
                tvPrice.setId(200 + count);
                tvPrice.setText(String.valueOf(invoicedProduct[9]));
                tvPrice.setGravity(Gravity.LEFT);
                tvPrice.setPadding(3, 3, 3, 3);
                tvPrice.setTextColor(Color.WHITE);
                tr.addView(tvPrice);

                TextView tvNormal = new TextView(this);
                tvNormal.setId(200 + count);
                tvNormal.setText(String.valueOf(invoicedProduct[7]));
                tvNormal.setGravity(Gravity.LEFT);
                tvNormal.setPadding(3, 3, 3, 3);
                tvNormal.setTextColor(Color.WHITE);
                tr.addView(tvNormal);

                TextView tvFree = new TextView(this);
                tvFree.setId(200 + count);
                tvFree.setText(String.valueOf(invoicedProduct[5]));
                tvFree.setGravity(Gravity.LEFT);
                tvFree.setPadding(3, 3, 3, 3);
                tvFree.setTextColor(Color.WHITE);
                tr.addView(tvFree);

                TextView tvDiscount = new TextView(this);
                tvDiscount.setId(200 + count);
                tvDiscount.setText(String.valueOf(invoicedProduct[6]));
                tvDiscount.setGravity(Gravity.LEFT);
                tvDiscount.setPadding(3, 3, 3, 3);
                tvDiscount.setTextColor(Color.WHITE);
                tr.addView(tvDiscount);

                TextView tvQuantity = new TextView(this);
                tvQuantity.setId(200 + count);
                tvQuantity.setText(String.valueOf(Integer
                        .parseInt(invoicedProduct[5])
                        + Integer.parseInt(invoicedProduct[7])));
                tvQuantity.setGravity(Gravity.LEFT);
                tvQuantity.setPadding(3, 3, 3, 3);
                tvQuantity.setTextColor(Color.WHITE);
                tr.addView(tvQuantity);

                TextView tvTotal = new TextView(this);
                tvTotal.setId(200 + count);


                double tempPrice = Double.parseDouble(String.valueOf(Integer
                        .parseInt(invoicedProduct[7])
                        * Double.parseDouble(invoicedProduct[9])));

                double discount = 0;

                if (invoicedProduct[6] != null || invoicedProduct[6] != "") {
                    discount = (tempPrice / 100) * Double.parseDouble(invoicedProduct[6]);
                }

                tvTotal.setText(String.format("%.2f", tempPrice - discount));
                tvTotal.setGravity(Gravity.LEFT);
                tvTotal.setPadding(3, 3, 3, 3);
                tvTotal.setTextColor(Color.WHITE);
                tr.addView(tvTotal);

                count++;

                tblInvoicedItems.addView(tr, new TableLayout.LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            }
        } catch (Exception e) {
            Log.w("pop Table error", e.toString());
        }
    }

    private void getDataFromPreviousActivity() {
        // TODO Auto-generated method stub
        Bundle extras = getIntent().getExtras();
        rowId = extras.getString("Id");
        pharmacyId = extras.getString("PharmacyId");
        invoiceId = extras.getString("InvoiceNumber");
    }

    private void setInitialData(List<String[]> invProds,
                                ArrayList<String> invData) {
        // TODO Auto-generated method stub

        Itinerary itinerary = new Itinerary(this);
        itinerary.openReadableDatabase();
        String status = itinerary.getItineraryStatus(rowId);
        itinerary.closeDatabase();

        String systemDate = DateFormat.getDateInstance().format(new Date());

        if (status.contentEquals("true")) {
            itinerary.openReadableDatabase();
            String[] itnDetails = itinerary
                    .getItineraryDetailsForTemporaryCustomer(rowId);
            itinerary.closeDatabase();
            String address = itnDetails[2] + ", " + itnDetails[3] + ", "
                    + itnDetails[4] + ", " + itnDetails[5];

            customerName = itnDetails[0];
            tViewCustomerName.setText(itnDetails[0]);
            tViewAddress.setText(address);
            custAddress = address;

        } else {
            Customers customers = new Customers(this);
            customers.openReadableDatabase();
            String[] customerDetails = customers
                    .getCustomerDetailsByPharmacyId(pharmacyId);
            customers.closeDatabase();

            customerName = customerDetails[5];
            tViewCustomerName.setText(customerDetails[5]);
            tViewAddress.setText(customerDetails[6]);
            custAddress = customerDetails[6];

        }
        tViewInvoiceNumber.setText(invoiceId);
        tViewDate.setText(systemDate);
        tViewTotalAmount.setText(invData.get(3));
        double total = 0;
        for (String[] invoicedproduct : invProds) {
            total = total + Double.parseDouble(invoicedproduct[5])
                    + Double.parseDouble(invoicedproduct[7]);
        }
        String tempTotal = String.valueOf(total);
        int d = tempTotal.indexOf(".");

        tViewTotalItems.setText(tempTotal.substring(0, d));
        tViewCash.setText(invData.get(4));
        tViewCredit.setText(invData.get(5));
        tViewCheque.setText(invData.get(6));
        tViewMarketReturn.setText(invData.get(7));
        tViewDiscount.setText(invData.get(8));
        tViewRemain.setText("");

        double needToPay = 0;
        needToPay = Double.parseDouble(invData.get(3))
                - (Double.parseDouble(invData.get(7)) + (Double
                .parseDouble(invData.get(3)) * ((Double
                .parseDouble(invData.get(8))) / 100)));

        tViewNeedToPay.setText(String.format("%.2f", needToPay));

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            Intent lastInvoice = new Intent(InvoiceViewActivity.this,
                    LastInvoiceActivity.class);
            finish();
            Bundle extras = new Bundle();
            extras.putString("Id", rowId);
            extras.putString("PharmacyId", pharmacyId);

            lastInvoice.putExtras(extras);
            startActivity(lastInvoice);
        }
        return super.onKeyDown(keyCode, event);
    }

    private String getRepName() {
        // TODO Auto-generated method stub

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        String repId = sharedPreferences.getString("RepId", "-1");

        Reps repsObject = new Reps(this);
        repsObject.openReadableDatabase();
        String repName = repsObject.getRepNameByRepId(repId);
        repsObject.closeDatabase();
        return repName;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub

        super.onSaveInstanceState(outState);

        outState.putSerializable("invoiceData", invoiceData);
        outState.putString("rowId", rowId);
        outState.putString("pharmacyId", pharmacyId);
        outState.putString("invoiceId", invoiceId);
        outState.putString("customerName", customerName);
        outState.putString("custAddress", custAddress);

    }

    @SuppressWarnings("unchecked")
    private void setBundleData(Bundle bundlData) {

        invoiceData = (ArrayList<String>) bundlData.getSerializable("invoiceData");
        rowId = bundlData.getString("rowId");
        pharmacyId = bundlData.getString("pharmacyId");
        invoiceId = bundlData.getString("invoiceId");
        customerName = bundlData.getString("customerName");
        custAddress = bundlData.getString("custAddress");

    }

    /*//Himanshu
    private void generatePDF(String custName, String address, String repName) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String repId = sharedPreferences.getString("RepId", "-1");


        Document document = new Document();
        Invoice invoice = new Invoice(this);
        invoice.openReadableDatabase();
        ArrayList<String> invoiceD = invoice.getInvoiceDetailsByInvoiceId(invoiceId);
        invoice.closeDatabase();


        Reps reps = new Reps(this);
        reps.openReadableDatabase();
        ArrayList<String> repDetails = reps.getRepDetailsForPrinting(repId);
        reps.closeDatabase();

        String date = invoiceD.get(11).substring(0, 10);
        String teram = invoiceD.get(2);

        if (teram.equals("CQR")) {
            teram = "Cheque";
        } else {
            teram = "Cash";
        }

        try {
            File file = new File(new File("/sdcard/DCIM/Channel_Bridge_Images/"), "Sample.pdf");
          //  PdfWriter.getInstance(document, new FileOutputStream(file));

            PdfWriter docWriter = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            Font chapterFont = FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD);
            Font paragraphFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
            Font tablefont = FontFactory.getFont(FontFactory.HELVETICA,8, Font.NORMAL);
            Font footerfont = FontFactory.getFont(FontFactory.HELVETICA,9, Font.NORMAL);


            Chunk c = new Chunk("INVOICE", chapterFont);

            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

         *//*   InputStream inputStream = getAssets().open("indoscanlogo.png");
            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image companyLogo = Image.getInstance(stream.toByteArray());
            companyLogo.setAlignment(Element.ALIGN_RIGHT);
            companyLogo.scalePercent(25);
            document.add(companyLogo);*//*

            Chunk cAdd1 = new Chunk("441/2A 2nd Ln", paragraphFont);
            Chunk cAdd2 = new Chunk("Kotte Road,Rajagiriya", paragraphFont);
            Chunk cAdd3 = new Chunk("Tel : +94 11 2 886 034", paragraphFont);
            Chunk cAdd4 = new Chunk("Fax : +94 11 2 886 035", paragraphFont);
            Paragraph pAdd1 = new Paragraph(cAdd1);
            Paragraph pAdd2 = new Paragraph(cAdd2);
            Paragraph pAdd3 = new Paragraph(cAdd3);
            Paragraph pAdd4 = new Paragraph(cAdd4);

            pAdd1.setAlignment(Element.ALIGN_RIGHT);
            pAdd2.setAlignment(Element.ALIGN_RIGHT);
            pAdd3.setAlignment(Element.ALIGN_RIGHT);
            pAdd4.setAlignment(Element.ALIGN_RIGHT);

            document.add(pAdd1);
            document.add(pAdd2);
            document.add(pAdd3);
            document.add(pAdd4);

            document.add(new Paragraph("\n"));




           *//* PdfPTable table1 = new PdfPTable(2);
            table1.setWidthPercentage(100);
            insertCell(table1, "CUSTOMER ADDRESS", Element.ALIGN_LEFT, 1, footerfont);
            insertCell(table1, "Invoice No : " + invoiceId, Element.ALIGN_RIGHT, 1, paragraphFont);

            document.add(table1);

            PdfPTable table2 = new PdfPTable(2);
            table2.setWidthPercentage(100);
            insertCell(table2, custName, Element.ALIGN_LEFT, 1, footerfont);
            insertCell(table2, "Date : " + date, Element.ALIGN_RIGHT, 1, paragraphFont);
            document.add(table2);

            PdfPTable table3 = new PdfPTable(2);
            table3.setWidthPercentage(100);
            insertCell(table3, address, Element.ALIGN_LEFT, 1, footerfont);
            insertCell(table3, "Po : MO12565", Element.ALIGN_RIGHT, 1, paragraphFont);
            document.add(table3);


            PdfPTable table4 = new PdfPTable(1);
            table4.setWidthPercentage(100);
            insertCell(table4, "Tearm  : " + teram, Element.ALIGN_RIGHT, 1, paragraphFont);
            document.add(table4);


            document.add(new Paragraph("\n"));
            float[] columnWidths = {1f,6f,2f,2f,2f,2f,2f,2f,1f,2f};

            PdfPTable table = new PdfPTable(columnWidths);
            table.setWidthPercentage(100);

            PdfPCell cell = new PdfPCell(new Phrase("No",tablefont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Description",tablefont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Batch",tablefont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Expiry",tablefont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("R.Pri",tablefont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("W/Pric",tablefont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Qty",tablefont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Free",tablefont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Dis",tablefont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Ammount",tablefont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            table.setHeaderRows(1);


            int itemcount = 1;
            int totalQty = 0;
            double totalProdsValue = 0;
            for (String[] invoicedProduct : invoicedProducts) {

                Products products = new Products(this);
                products.openReadableDatabase();
                String[] productdetail = products.getProductDetailsById(invoicedProduct[2]);

                ProductRepStore productRepStore = new ProductRepStore(this);
                productRepStore.openReadableDatabase();



                String expiry = productRepStore.getExpiryByProductCodeAndBatch(productdetail[2], invoicedProduct[3]);
                String productDescription = productdetail[8].trim();
                String batch = invoicedProduct[3].trim();
                String retailsPrice = productdetail[14].trim();
                String unitPrice = invoicedProduct[9].trim();// IMPORTANT
                String qty = invoicedProduct[7].trim();
                String free = invoicedProduct[5].trim();
                String discount = invoicedProduct[6];

                double prodValue = (Integer.parseInt(qty) * Double.parseDouble(unitPrice)) * ((100 - Double.parseDouble(discount)) / 100);
                String ammount = (String.format("%.2f", prodValue));
                totalQty = totalQty + Integer.parseInt(qty);


                totalProdsValue = totalProdsValue + prodValue;

                productRepStore.closeDatabase();
                products.closeDatabase();

                String itemno;
                if (String.valueOf(itemcount).length() == 1) {
                    itemno = "0" + String.valueOf(itemcount);
                } else {
                    itemno = String.valueOf(itemcount);
                }

                //dis
                if (productDescription.length() == 26) {

                } else if (productDescription.length() > 26) {
                    productDescription = productDescription.substring(0, 26);
                } else {
                    int prodetails = 26 - productDescription.length();
                    String prodetailsspace = "";
                    for (int i = 0; i < prodetails; i++) {
                        prodetailsspace = prodetailsspace + " ";
                    }
                    productDescription = productDescription + prodetailsspace;
                }

                insertCell(table,String.valueOf(itemno), Element.ALIGN_MIDDLE, 1, tablefont);
                insertCell(table, productDescription, Element.ALIGN_MIDDLE, 1, tablefont);
                insertCell(table, batch, Element.ALIGN_MIDDLE, 1, tablefont);
                insertCell(table, expiry.substring(2, 10), Element.ALIGN_MIDDLE, 1, tablefont);
                insertCell(table, retailsPrice, Element.ALIGN_MIDDLE, 1, tablefont);
                insertCell(table, unitPrice, Element.ALIGN_MIDDLE, 1, tablefont);
                insertCell(table, qty, Element.ALIGN_MIDDLE, 1, tablefont);
                insertCell(table, free, Element.ALIGN_MIDDLE, 1, tablefont);
                insertCell(table, discount, Element.ALIGN_MIDDLE, 1, tablefont);
                insertCell(table, ammount, Element.ALIGN_MIDDLE, 1, tablefont);


                itemcount++;
            }

            document.add(table);


            String totalQt = String.valueOf(totalQty);
            String invoiceVal = String.format("%.2f", totalProdsValue);

            float[] columnWidthsfortotale = {10f,3.3f,1.3f};
            PdfPTable tableTotale = new PdfPTable(columnWidthsfortotale);
            tableTotale.setWidthPercentage(100);

            PdfPCell cellTotel = new PdfPCell(new Phrase("TOTAL",tablefont));
            tableTotale.addCell(cellTotel);

            cellTotel = new PdfPCell(new Phrase(totalQt,tablefont));
            tableTotale.addCell(cellTotel);

            cellTotel = new PdfPCell(new Phrase(invoiceVal,tablefont));
            tableTotale.addCell(cellTotel);

            document.add(tableTotale);


            document.add(new Paragraph("\n\n\n\n\n\n"));


            String craditduration = invoiceD.get(13);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Calendar calen = Calendar.getInstance();
            calen.setTime(sdf.parse(date));
            calen.add(Calendar.DATE, Integer.parseInt(craditduration));
            sdf = new SimpleDateFormat("dd/MM/yyyy");

            Date resultdate = new Date(calen.getTimeInMillis());
            String calculateDuration = sdf.format(resultdate);

            Chunk cfoot1 = new Chunk("PAYMENT BY 'ACCOUNT PAYEE' CHEQUES DRAWN IN FAVOUR OF INDOSCAN [PVT] LIMITED", footerfont);
            Chunk cfoot2 = new Chunk("Short dated products must be returned befor 3 months of expiary date", footerfont);
            Chunk cfoot3 = new Chunk("THIS INVOICE IS DUE FOR SETTLEMENT ON OR BEFORE : "+calculateDuration+"", footerfont);
            Chunk cfoot4 = new Chunk("Received Correct Products & Quantities", footerfont);
            Chunk cfoot5 = new Chunk("................................................", footerfont);
            Chunk cfoot6 = new Chunk("Printed by :" + getRepName() + " Tel : "+repDetails.get(3).trim()+"", footerfont);




            Paragraph foot1 = new Paragraph(cfoot1);
            Paragraph foot2 = new Paragraph(cfoot2);
            Paragraph foot3 = new Paragraph(cfoot3);
            Paragraph foot4 = new Paragraph(cfoot4);
            Paragraph foot5 = new Paragraph(cfoot5);
            Paragraph foot6 = new Paragraph(cfoot6);


            foot4.setAlignment(Element.ALIGN_RIGHT);
            foot5.setAlignment(Element.ALIGN_RIGHT);


            document.add(foot1);
            document.add(foot2);
            document.add(foot3);
            document.add(new Paragraph("\n\n"));
            document.add(foot4);
            document.add(foot5);
            document.add(new Paragraph("\n\n"));
            document.add(foot6);

*//*




        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.close();


    }*/

    private void insertCell(PdfPTable table, String text, int align, int colspan, Font font){


        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        cell.setHorizontalAlignment(align);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(colspan);
        if(text.trim().equalsIgnoreCase("")){
            cell.setMinimumHeight(10f);
        }
        table.addCell(cell);

    }


}
