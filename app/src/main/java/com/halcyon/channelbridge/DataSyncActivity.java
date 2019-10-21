package com.halcyon.channelbridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class DataSyncActivity extends Activity {

//
//	public class UploadInvoiceTask extends AsyncTask<String, Integer, Integer> {
//
//		private final Context context;
//
//		public UploadInvoiceTask(Context context) {
//			this.context = context;
//		}
//
//		@Override
//		protected void onPreExecute() {
//
//		}
//
//		protected void onProgressUpdate(Integer... progress) {
//
//		}
//
//		protected void onPostExecute(Integer returnCode) {
//
//
//		}
//
//		@Override
//		protected Integer doInBackground(String... params) {
//			// TODO Auto-generated method stub
//
//			int returnValue = 1;
//
//			Looper.prepare();
//
//			Log.w("Log", "param result : " + params[0]);
//
//			Log.w("Log", "loadProductRepStoreData result : starting ");
//
//			if (isOnline()) {
//
//				publishProgress(1);
//
//				Invoice invoiceObject = new Invoice(DataSyncActivity.this);
//				invoiceObject.openReadableDatabase();
//
//				List<String[]> invoice = invoiceObject
//						.getInvoicesByStatus("false");
//				invoiceObject.closeDatabase();
//
//				Log.w("Log", "invoice size :  " + invoice.size());
//
//				for (String[] invoicedProduct : invoice) {
//					Log.w("Log", "invoice :  " + invoicedProduct[0]);
//					Log.w("Log", "invoice :  " + invoicedProduct[1]);
//					Log.w("Log", "invoice :  " + invoicedProduct[2]);
//					Log.w("Log", "invoice :  " + invoicedProduct[3]);
//					Log.w("Log", "invoice :  " + invoicedProduct[4]);
//					Log.w("Log", "invoice :  " + invoicedProduct[5]);
//					Log.w("Log", "invoice :  " + invoicedProduct[6]);
//					Log.w("Log", "invoice :  " + invoicedProduct[7]);
//					Log.w("Log", "invoice :  " + invoicedProduct[8]);
//					Log.w("Log", "invoice :  " + invoicedProduct[9]);
//
//				}
//
//				for (String[] invoiceData : invoice) {
//
//					Log.w("Log", "invoice id :  " + invoiceData[0]);
//					Log.w("Log", "invoice date :  " + invoiceData[10]);
//
//					ArrayList<String[]> invoicedProductDetailList = new ArrayList<String[]>();
//
//					InvoicedProducts invoicedProductsObject = new InvoicedProducts(
//							DataSyncActivity.this);
//					invoicedProductsObject.openReadableDatabase();
//					List<String[]> invoicedProducts = invoicedProductsObject
//							.getInvoicedProductsByInvoiceId(invoiceData[0]);
//
//					invoicedProductsObject.closeDatabase();
//
//					Log.w("Log",
//							"invoicedProducts size :  "
//									+ invoicedProducts.size());
//
//					for (String[] invoicedProduct : invoicedProducts) {
//						Log.w("Log", "invoicedProduct :  " + invoicedProduct[0]);
//						Log.w("Log", "invoicedProduct :  " + invoicedProduct[1]);
//						Log.w("Log", "invoicedProduct :  " + invoicedProduct[2]);
//						Log.w("Log", "invoicedProduct :  " + invoicedProduct[3]);
//						Log.w("Log", "invoicedProduct :  " + invoicedProduct[4]);
//						Log.w("Log", "invoicedProduct :  " + invoicedProduct[5]);
//						Log.w("Log", "invoicedProduct :  " + invoicedProduct[6]);
//						Log.w("Log", "invoicedProduct :  " + invoicedProduct[7]);
//
//					}
//
//					for (String[] invoicedProduct : invoicedProducts) {
//
//						ProductRepStore productRepStore = new ProductRepStore(
//								DataSyncActivity.this);
//						productRepStore.openReadableDatabase();
//						String[] productRepStor = productRepStore
//								.getProductDetailsByProductBatch(invoicedProduct[3]);
//						productRepStore.closeDatabase();
//
//						Products product = new Products(
//								DataSyncActivity.this);
//						product.openReadableDatabase();
//						String[] productData = product
//								.getProductDetailsByProductCode(invoicedProduct[2]);
//						product.closeDatabase();
//
//						Itinerary itinerary = new Itinerary(DataSyncActivity.this);
//						itinerary.openReadableDatabase();
//						
//						String tempCust = itinerary.getItineraryStatus(invoiceData[1]);
//						itinerary.closeDatabase();
//						
//						String custNo = "";
//						
//						Itinerary itineraryTwo = new Itinerary(DataSyncActivity.this);
//						itineraryTwo.openReadableDatabase();
//						
//						if (tempCust.equals("true")) {
//							String[] itnDetails = itineraryTwo.getItineraryDetailsForTemporaryCustomer(invoiceData[1]);
//							custNo = ItineraryList.DEVICE_ID+"_"+itnDetails[7];// this is where yu have to change..!!
//						}else{
//							String[] itnDetails = itineraryTwo.getItineraryDetailsById(invoiceData[1]);
//							custNo = itnDetails[4];
//						}
//						
//
//						itineraryTwo.closeDatabase();
//
//						if (invoicedProduct[7] != ""
//								&& Integer.parseInt(invoicedProduct[7]) > 0) {
//
//							String[] invoiceDetails = new String[13];
//
//							int qty = Integer.parseInt(invoicedProduct[7]);
//							double purchasePrice = 0;
//							double selleingPrice = 0;
//							if (productData[12] != null
//									&& productData[12].length() > 0) {
//								purchasePrice = Double
//										.parseDouble(productData[12]);
//							}
//							if (productData[13] != null
//									&& productData[13].length() > 0) {
//								selleingPrice = Double
//										.parseDouble(productData[13]);
//							}
//
//							double profit = (selleingPrice * qty)
//									- (purchasePrice * qty);
//
//							Log.w("Log", "profit :  " + profit);
//
//							invoiceDetails[0] = invoicedProduct[2]; // Product
//																	// code
//							invoiceDetails[1] = invoicedProduct[1]; // Invoice
//																	// Id
//							invoiceDetails[2] = "N"; // Issue mode
//							invoiceDetails[3] = invoicedProduct[7]; // Normal
//																	// qty
//							invoiceDetails[4] = invoiceData[10]; // Invoice date
//							invoiceDetails[5] = invoiceData[2]; // Payment type
//							invoiceDetails[6] = productRepStor[5]; // Expire
//																	// date
//							invoiceDetails[7] = invoicedProduct[3]; // Batch no
//							invoiceDetails[8] = custNo; // Customer no
//							invoiceDetails[9] = String.valueOf(profit); // Profit
//							invoiceDetails[10] = productData[14]; // Unit price
//							invoiceDetails[11] = invoicedProduct[6]; // Discount
//							invoiceDetails[12] = invoicedProduct[0]; // Id
//
//							invoicedProductDetailList.add(invoiceDetails);
//
//						}
//
//						if (invoicedProduct[5] != ""
//								&& Integer.parseInt(invoicedProduct[5]) > 0) {
//
//							String[] invoiceDetails = new String[13];
//
//							invoiceDetails[0] = invoicedProduct[2]; // Product
//																	// code
//							invoiceDetails[1] = invoicedProduct[1]; // Invoice
//																	// Id
//							invoiceDetails[2] = "F"; // Issue mode
//							invoiceDetails[3] = invoicedProduct[5]; // Normal
//																	// qty
//							invoiceDetails[4] = invoiceData[10]; // Invoice date
//							invoiceDetails[5] = invoiceData[2]; // Payment type
//							invoiceDetails[6] = productRepStor[5]; // Expire
//																	// date
//							invoiceDetails[7] = invoicedProduct[3]; // Batch no
//							invoiceDetails[8] = custNo; // Customer no
//							invoiceDetails[9] = "0.00"; // Profit
//							invoiceDetails[10] = "0"; // Unit price
//							invoiceDetails[11] = invoicedProduct[6]; // Discount
//							invoiceDetails[12] = invoicedProduct[0]; // Id
//
//							invoicedProductDetailList.add(invoiceDetails);
//
//						}
//
//					}
//
//					Log.w("Log", "invoicedProductDetailList size :  "
//							+ invoicedProductDetailList.size());
//
//					publishProgress(2);
//					String responseArr = null;
//					while (responseArr == null) {
//						try {
//
//							WebService webService = new WebService();
//							responseArr = webService.uploadInvoiceDetails(
//									ItineraryList.DEVICE_ID,
//									ItineraryList.REP_ID,
//									invoicedProductDetailList);
//
//							Thread.sleep(10000);
//
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//					}
//
//					Log.w("Log",
//							"update data result : "
//									+ responseArr.contains("No Error"));
//					if (responseArr.contains("No Error")) {
//
//						Log.w("Log", "Update the iternarary status");
//
//						Invoice invoiceObj = new Invoice(
//								DataSyncActivity.this);
//						invoiceObj.openReadableDatabase();
//						invoiceObj.setInvoiceUpdatedStatus(invoiceData[0],
//								"true");
//						invoiceObj.closeDatabase();
//
//						returnValue = 2;
//
//					}
//
//					Log.w("Log", "loadProductRepStoreData result : "
//							+ responseArr);
//
//				}
//
//				if (invoice.size() < 1) {
//
//					returnValue = 4;
//				}
//
//			} else {
//
//				returnValue = 3;
//			}
//
//			return returnValue;
//
//		}
//
//	}
//
//	public class UploadNewCustomersTask extends
//			AsyncTask<String, Integer, Integer> {
//
//		private final Context context;
//
//		public UploadNewCustomersTask(Context context) {
//			this.context = context;
//		}
//
//		@Override
//		protected void onPreExecute() {
//
//
//		}
//
//		protected void onProgressUpdate(Integer... progress) {
//
//		}
//
//		protected void onPostExecute(Integer returnCode) {
//
//		}
//
//		@Override
//		protected Integer doInBackground(String... params) {
//			// TODO Auto-generated method stub
//
//			int returnValue = 1;
//
//			Looper.prepare();
//
//			Log.w("Log", "param result : " + params[0]);
//
//			Log.w("Log", "loadProductRepStoreData result : starting ");
//
//			if (isOnline()) {
//
//				publishProgress(1);
//
//				CustomersPendingApproval rtnProdObject = new CustomersPendingApproval(
//						DataSyncActivity.this);
//				rtnProdObject.openReadableDatabase();
//
//				List<String[]> rtnProducts = rtnProdObject
//						.getCustomersByUploadStatus("false");
//				rtnProdObject.closeDatabase();
//
//				Log.w("Log", "rtnProducts size :  " + rtnProducts.size());
//
//				ArrayList<String[]> invoicedProductDetailList = new ArrayList<String[]>();
//
//				for (String[] rtnProdData : rtnProducts) {
//
//					Log.w("Log", "rtnProducts id :  " + rtnProdData[0]);
//					// Log.w("Log", "rtnProducts date :  " + rtnProdData[10]);
//
//					String[] invoiceDetails = new String[20];
//
//					invoiceDetails[0] = ItineraryList.DEVICE_ID+"_"+rtnProdData[0];
//					invoiceDetails[1] = rtnProdData[1];
//					invoiceDetails[2] = rtnProdData[2];
//					invoiceDetails[3] = rtnProdData[3];
//					invoiceDetails[4] = rtnProdData[4];
//					invoiceDetails[5] = rtnProdData[5];
//					invoiceDetails[6] = rtnProdData[6];
//					invoiceDetails[7] = rtnProdData[7];
//					invoiceDetails[8] = rtnProdData[8];
//					invoiceDetails[9] = rtnProdData[9];
//					invoiceDetails[10] = rtnProdData[11];
//					invoiceDetails[11] = rtnProdData[12];
//					invoiceDetails[12] = rtnProdData[13];
//					invoiceDetails[13] = rtnProdData[15];
//					invoiceDetails[14] = rtnProdData[14];
//					invoiceDetails[15] = rtnProdData[16];
//					invoiceDetails[16] = rtnProdData[17];
//					invoiceDetails[17] = rtnProdData[18];
//					invoiceDetails[18] = rtnProdData[20];
//					invoiceDetails[19] = rtnProdData[21];
//
//					publishProgress(2);
//					String responseArr = null;
//					while (responseArr == null) {
//						try {
//
//							WebService webService = new WebService();
//							responseArr = webService.uploadNewCustomerDetails(
//									ItineraryList.DEVICE_ID,
//									ItineraryList.REP_ID, invoiceDetails);
//
//							Thread.sleep(10000);
//
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//					}
//
//					Log.w("Log", "update data result : " + responseArr);
//
//					Log.w("Log",
//							"update data result : "
//									+ responseArr.contains("Successfully"));
//					if (responseArr.contains("Successfully")) {
//
//						Log.w("Log", "Update the iternarary status");
//
//						CustomersPendingApproval rtnProdObj = new CustomersPendingApproval(
//								DataSyncActivity.this);
//						rtnProdObj.openReadableDatabase();
//						rtnProdObj.setCustomerUploadedStatus(rtnProdData[0],
//								"true");
//						rtnProdObj.closeDatabase();
//
//						returnValue = 2;
//
//					}
//
//					Log.w("Log", "loadProductRepStoreData result : "
//							+ responseArr);
//
//				}
//
//				Log.w("Log", "invoicedProductDetailList size :  "
//						+ invoicedProductDetailList.size());
//
//				if (rtnProducts.size() < 1) {
//
//					returnValue = 4;
//				}
//
//			} else {
//
//				returnValue = 3;
//			}
//
//			return returnValue;
//
//		}
//
//	}
//
//	public class UploadProductReturnsTask extends
//			AsyncTask<String, Integer, Integer> {
//
//		private final Context context;
//
//		public UploadProductReturnsTask(Context context) {
//			this.context = context;
//		}
//
//		@Override
//		protected void onPreExecute() {
//
//
//		}
//
//		protected void onProgressUpdate(Integer... progress) {
//
//		}
//
//		protected void onPostExecute(Integer returnCode) {
//
//		}
//
//		@Override
//		protected Integer doInBackground(String... params) {
//			// TODO Auto-generated method stub
//
//			int returnValue = 1;
//
//			Looper.prepare();
//
//			Log.w("Log", "param result : " + params[0]);
//
//			Log.w("Log", "loadProductRepStoreData result : starting ");
//
//			if (isOnline()) {
//
//				publishProgress(1);
//
//				ProductReturns rtnProdObject = new ProductReturns(
//						DataSyncActivity.this);
//				rtnProdObject.openReadableDatabase();
//
//				List<String[]> rtnProducts = rtnProdObject
//						.getProductReturnsByStatus("false");
//				rtnProdObject.closeDatabase();
//
//				Log.w("Log", "rtnProducts size :  " + rtnProducts.size());
//
//			
//
//				for (String[] rtnProdData : rtnProducts) {
//
//					Log.w("Log", "rtnProducts id :  " + rtnProdData[0]);
//					// Log.w("Log", "rtnProducts date :  " + rtnProdData[10]);
//
//					Products product = new Products(DataSyncActivity.this);
//					product.openReadableDatabase();
//					String[] productData = product
//							.getProductDetailsByProductCode(rtnProdData[1]);
//					product.closeDatabase();
//
//					ProductRepStore productRepStore = new ProductRepStore(
//							DataSyncActivity.this);
//					productRepStore.openReadableDatabase();
//					String[] productRepStor = productRepStore
//							.getProductDetailsByProductBatch(rtnProdData[2]);
//					productRepStore.closeDatabase();
//
//
//					ArrayList<String[]> returnedProductList = new ArrayList<String[]>();
//					
//					String[] invoiceDetails = new String[13];
//
//					invoiceDetails[0] = rtnProdData[1]; // Product
//															// code
//					invoiceDetails[1] = rtnProdData[3]; // Invoice
//															// Id
//					invoiceDetails[2] = "R"; // Issue mode
//					invoiceDetails[3] = rtnProdData[5]; // Normal
//															// qty
//					invoiceDetails[4] = rtnProdData[7]; // Rtn date
//					
//					Log.w("Log", "productRepStor[5] 3@@$@ :  " + productRepStor[5]);
//					
//					invoiceDetails[5] = productRepStor[5]; // expire date
//					
//					if (invoiceDetails[5] == null || invoiceDetails[5] == "") {
//						invoiceDetails[5] = "2015-01-01 10:13:59.790"; // expire date
//					}
//					
//					invoiceDetails[6] = rtnProdData[2]; // batch no
//															// date
//					invoiceDetails[7] = rtnProdData[8]; // Batch no
//					
//					invoiceDetails[8] = rtnProdData[10]; // Unit price
//					if (invoiceDetails[8] ==null || invoiceDetails[8] =="") {
//						invoiceDetails[8] = productData[14]; // Unit price
//					}
//					
//					invoiceDetails[9] = rtnProdData[0]; // Id
//					returnedProductList.add(invoiceDetails);
//					
//					if (rtnProdData[6] != null ||Integer.parseInt(rtnProdData[6]) > 0) {
//						
//						String[] invoiceDetailsFree = new String[13];
//
//						invoiceDetailsFree[0] = rtnProdData[1]; // Product
//																// code
//						invoiceDetailsFree[1] = rtnProdData[3]; // Invoice
//																// Id
//						invoiceDetailsFree[2] = "RF"; // Issue mode
//						invoiceDetailsFree[3] = rtnProdData[6]; // Free qty
//						invoiceDetailsFree[4] = rtnProdData[7]; // Rtn date
//						
//						Log.w("Log", "productRepStor[5] 3@@$@ :  " + productRepStor[5]);
//						
//						invoiceDetailsFree[5] = productRepStor[5]; // expire date
//						
//						if (invoiceDetailsFree[5] == null || invoiceDetailsFree[5] == "") {
//							invoiceDetailsFree[5] = "2015-01-01 10:13:59.790"; // expire date
//						}
//						
//						invoiceDetailsFree[6] = rtnProdData[2]; // batch no
//																// date
//						invoiceDetailsFree[7] = rtnProdData[8]; // cust no
//						
//						invoiceDetailsFree[8] = rtnProdData[10]; // Unit price
//						if (invoiceDetailsFree[8] ==null || invoiceDetailsFree[8] =="") {
//							invoiceDetailsFree[8] = productData[14]; // Unit price
//						}
//						
//						invoiceDetailsFree[9] = rtnProdData[0]; // Id
//						returnedProductList.add(invoiceDetailsFree);
//					}
//
//					
//					
//					publishProgress(2);
//					String responseArr = null;
//					while (responseArr == null) {
//						try {
//
//							WebService webService = new WebService();
//							responseArr = webService
//									.uploadProductReturnsDetails(
//											ItineraryList.DEVICE_ID,
//											ItineraryList.REP_ID,
//											returnedProductList);
//
//							Thread.sleep(10000);
//
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//					}
//
//					Log.w("Log",
//							"update data result : "
//									+ responseArr.contains("No Error"));
//					if (responseArr.contains("No Error")) {
//
//						Log.w("Log", "Update the iternarary status");
//
//						ProductReturns rtnProdObj = new ProductReturns(
//								DataSyncActivity.this);
//						rtnProdObj.openReadableDatabase();
//						rtnProdObj.setRtnProductsUploadedStatus(rtnProdData[0],
//								"true");
//						rtnProdObj.closeDatabase();
//
//						returnValue = 2;
//
//					}
//
//					Log.w("Log", "loadProductRepStoreData result : "
//							+ responseArr);
//
//				}
//
//				if (rtnProducts.size() < 1) {
//
//					returnValue = 4;
//				}
//
//			} else {
//
//				returnValue = 3;
//			}
//
//			return returnValue;
//
//		}
//
//	}
//
//	public class DownloadProductRepStoreTask extends
//			AsyncTask<String, Integer, Integer> {
//
//		private final Context context;
//
//		public DownloadProductRepStoreTask(Context context) {
//			this.context = context;
//		}
//
//		@Override
//		protected void onPreExecute() {
//
//
//		}
//
//		protected void onProgressUpdate(Integer... progress) {
//
//		}
//
//		protected void onPostExecute(Integer returnCode) {
//
//		}
//
//		@Override
//		protected Integer doInBackground(String... params) {
//			// TODO Auto-generated method stub
//
//			int returnValue = 1;
//
//			Looper.prepare();
//
//			Log.w("Log", "param result : " + params[0]);
//
//			Log.w("Log", "loadProductRepStoreData result : starting ");
//
//			if (isOnline()) {
//
//				publishProgress(1);
//
//				Sequence sequenceObject = new Sequence(
//						DataSyncActivity.this);
//				sequenceObject.openReadableDatabase();
//
//				String lastRowId = sequenceObject
//						.getLastRowId("productRepStore");
//				sequenceObject.closeDatabase();
//
//				Log.w("Log", "lastRowId:  " + lastRowId);
//
//				int maxRowID = 0;
//
//				if (lastRowId != "") {
//
//					ProductRepStore repStoreObject = new ProductRepStore(
//							DataSyncActivity.this);
//					repStoreObject.openReadableDatabase();
//
//					String lastProductId = repStoreObject
//							.getProductIdByRowId(lastRowId);
//					repStoreObject.closeDatabase();
//					Log.w("Log", "lastProductId:  " + lastProductId);
//
//					if (lastProductId != "") {
//						maxRowID = Integer.parseInt(lastProductId);
//					}
//
//				}
//
//				ArrayList<String[]> repStoreDataResponse = null;
//				while (repStoreDataResponse == null) {
//					try {
//
//						WebService webService = new WebService();
//						repStoreDataResponse = webService
//								.getProductRepStoreList(
//										ItineraryList.DEVICE_ID,
//										ItineraryList.REP_ID, maxRowID);
//
//						Thread.sleep(100);
//
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//
//				if (repStoreDataResponse.size() > 0) {
//
//					ProductRepStore productRepStore = new ProductRepStore(
//							DataSyncActivity.this);
//
//					productRepStore.openWritableDatabase();
//					String timeStamp = new SimpleDateFormat(
//							"yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
//
//					for (int i = 0; i < repStoreDataResponse.size(); i++) {
//
//						String[] custDetails = repStoreDataResponse.get(i);
//
//						Long result = productRepStore.insertProductRepStore(
//								custDetails[0], custDetails[2], custDetails[5],
//								custDetails[3], custDetails[4], timeStamp);
//
//						if (result == -1) {
//							returnValue = 7;
//							break;
//						}
//
//						returnValue = 5;
//					}
//
//					productRepStore.closeDatabase();
//
//				} else {
//
//					returnValue = 6;
//
//				}
//
//			} else {
//
//				returnValue = 3;
//			}
//
//			return returnValue;
//
//		}
//
//	}
//
//	public class UploadShelfQtyTask extends
//	AsyncTask<String, Integer, Integer> {
//
//private final Context context;
//
//public UploadShelfQtyTask(Context context) {
//	this.context = context;
//}
//
//@Override
//protected void onPreExecute() {
//
//}
//
//protected void onProgressUpdate(Integer... progress) {
//
//}
//
//protected void onPostExecute(Integer returnCode) {
//
//
//}
//
//@Override
//protected Integer doInBackground(String... params) {
//	// TODO Auto-generated method stub
//
//	int returnValue = 1;
//
//	Looper.prepare();
//
//	Log.w("Log", "param result : " + params[0]);
//
//	Log.w("Log", "loadProductRepStoreData result : starting ");
//
//	if (isOnline()) {
//
//		publishProgress(1);
//
//		ShelfQuantity rtnProdObject = new ShelfQuantity(
//				DataSyncActivity.this);
//		rtnProdObject.openReadableDatabase();
//
//		List<String[]> rtnProducts = rtnProdObject
//				.getShelfQuantitiesByStatus("false");
//		rtnProdObject.closeDatabase();
//
//		Log.w("Log", "rtnProducts size :  " + rtnProducts.size());
//
//		ArrayList<String[]> invoicedProductDetailList = new ArrayList<String[]>();
//
//		for (String[] invoicedProduct : rtnProducts) {
//			Log.w("Log", "rtnProducts :  " + invoicedProduct[0]);
//			Log.w("Log", "rtnProducts :  " + invoicedProduct[1]);
//			Log.w("Log", "rtnProducts :  " + invoicedProduct[2]);
//			Log.w("Log", "rtnProducts :  " + invoicedProduct[3]);
//			Log.w("Log", "rtnProducts :  " + invoicedProduct[4]);
//			Log.w("Log", "rtnProducts :  " + invoicedProduct[5]);
//			Log.w("Log", "rtnProducts :  " + invoicedProduct[6]);
//			Log.w("Log", "rtnProducts :  " + invoicedProduct[7]);
//			Log.w("Log", "rtnProducts :  " + invoicedProduct[8]);
//
//		}
//
//		for (String[] rtnProdData : rtnProducts) {
//
//			Log.w("Log", "rtnProducts id :  " + rtnProdData[0]);
//			
//			String[] invoiceDetails = new String[13];
//
//			invoiceDetails[0] = ItineraryList.REP_ID; // rep id
//													
//			invoiceDetails[1] = rtnProdData[1]; // Invoice no
//			invoiceDetails[2] =  rtnProdData[2]; // Invoice date
//			
//			invoiceDetails[3] = rtnProdData[3]; // customer id
//			
//			invoiceDetails[4] = rtnProdData[4]; // item code
//			invoiceDetails[5] = rtnProdData[6]; // item code
//			invoiceDetails[6] = rtnProdData[5]; // item code
//
//
//			publishProgress(2);
//			String responseArr = null;
//			while (responseArr == null) {
//				try {
//
//					WebService webService = new WebService();
//					responseArr = webService
//							.uploadShelfQuantityDetails(
//									ItineraryList.DEVICE_ID,
//									ItineraryList.REP_ID,
//									invoiceDetails);
//
//					Thread.sleep(10000);
//
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//			}
//
//			Log.w("Log",
//					"update data result : "
//							+ responseArr.contains("Record Inserted Successfully"));
//			if (responseArr.contains("Record Inserted Successfully")) {
//
//				Log.w("Log", "Update the iternarary status");
//
//				ShelfQuantity rtnProdObj = new ShelfQuantity(
//						DataSyncActivity.this);
//				rtnProdObj.openReadableDatabase();
//				rtnProdObj.setShelfQtyUploadedStatus(rtnProdData[0],
//						"true");
//				rtnProdObj.closeDatabase();
//
//				returnValue = 2;
//
//			}
//
//			Log.w("Log", "loadProductRepStoreData result : "
//					+ responseArr);
//
//		}
//
//		Log.w("Log", "invoicedProductDetailList size :  "
//				+ invoicedProductDetailList.size());
//
//		if (rtnProducts.size() < 1) {
//
//			returnValue = 4;
//		}
//
//	} else {
//
//		returnValue = 3;
//	}
//
//	return returnValue;
//
//}
//
//}
//	
//	public class UploadCustomerImageTask extends
//	AsyncTask<String, Integer, Integer> {
//
//private final Context context;
//
//public UploadCustomerImageTask(Context context) {
//	this.context = context;
//}
//
//@Override
//protected void onPreExecute() {
//
//
//}
//
//protected void onProgressUpdate(Integer... progress) {
//
//}
//
//protected void onPostExecute(Integer returnCode) {
//
//
//}
//
//@Override
//protected Integer doInBackground(String... params) {
//	// TODO Auto-generated method stub
//
//	int returnValue = 1;
//
//	Looper.prepare();
//
//	Log.w("Log", "param result : " + params[0]);
//
//	Log.w("Log", "loadProductRepStoreData result : starting ");
//
//	if (isOnline()) {
//
//		publishProgress(1);
//
//		ImageGallery rtnProdObject = new ImageGallery(
//				DataSyncActivity.this);
//		rtnProdObject.openReadableDatabase();
//
//		List<String[]> rtnProducts = rtnProdObject
//				.getImagesByStatus("false");
//		rtnProdObject.closeDatabase();
//
//		Log.w("Log", "rtnProducts sized :  " + rtnProducts.size());
//
//		if (rtnProducts.size()<1) {
//			returnValue = 4;
//			
//		}else{
//		
//		for (String[] rtnProdData : rtnProducts) {
//		
//		Log.w("Log", "SimpleFTP ???" );
//
//		
//			Log.w("Log", "rtnProducts id :  " + rtnProdData[0]);
//			
//						
//			   FTPClient con = new FTPClient();
//			   try
//			   {
//				   con.connect(DataSyncActivity.this.getString(R.string.ftp_host), Integer.parseInt(DataSyncActivity.this.getString(R.string.ftp_port)));
//				   if (con.login(DataSyncActivity.this.getString(R.string.ftp_username), DataSyncActivity.this.getString(R.string.ftp_password))) {
//			    	   
//
//			           con.enterLocalPassiveMode(); 
//			           con.setFileType(FTP.BINARY_FILE_TYPE);
//			           
//
//			           String str = Environment.getExternalStorageDirectory() + File.separator
//								+ "DCIM" + File.separator + "Channel_Bridge_Images"
//								+ File.separator + rtnProdData[3];
//
//			           
//			           FileInputStream srcFileStream = new FileInputStream(str);  
//			     
//			        boolean status = con.storeFile(rtnProdData[3], srcFileStream);  
//      
//			        srcFileStream.close();  
//			        
//			        if (status) {
//
//						Log.w("Log", "Update the iternarary status");
//
//						ShelfQuantity rtnProdObj = new ShelfQuantity(
//								DataSyncActivity.this);
//						rtnProdObj.openReadableDatabase();
//						rtnProdObj.setShelfQtyUploadedStatus(rtnProdData[0],
//								"true");
//						rtnProdObj.closeDatabase();
//
//						returnValue = 2;
//
//					}
//
////			        con.stor(str);
//			       }
//			   }
//			   catch (Exception e)
//			   {
//			       e.printStackTrace();
//			       returnValue = 4;
//			   }
//
//
//		}
//		}
//
//
//	} else {
//
//		returnValue = 3;
//	}
//
//	return returnValue;
//
//}
//
//}
//	
//	public class DownloadItineraryTask extends
//	AsyncTask<String, Integer, Integer> {
//
//private final Context context;
//
//public DownloadItineraryTask(Context context) {
//	this.context = context;
//}
//
//@Override
//protected void onPreExecute() {
//
//
//}
//
//protected void onProgressUpdate(Integer... progress) {
//
//}
//
//protected void onPostExecute(Integer returnCode) {
//
//
//}
//
//@Override
//protected Integer doInBackground(String... params) {
//	// TODO Auto-generated method stub
//
//	int returnValue = 1;
//
//	Looper.prepare();
//
//	Log.w("Log", "param result : " + params[0]);
//
//	Log.w("Log", "DownloadItineraryTask result : starting ");
//
//	if (isOnline()) {
//
//		publishProgress(1);
//
//		Sequence sequenceObject = new Sequence(
//				DataSyncActivity.this);
//		sequenceObject.openReadableDatabase();
//
//		String lastRowId = sequenceObject
//				.getLastRowId("itinerary");
//		sequenceObject.closeDatabase();
//
//		Log.w("Log", "lastRowId:  " + lastRowId);
//
//		String maxRowID = "0";
//
//		if (lastRowId != "") {
//
//			Itinerary itinerary = new Itinerary(DataSyncActivity.this);
//			itinerary.openReadableDatabase();
//
//			String itineraryId = itinerary
//					.getLastUpdatedItineraryId();
//			itinerary.closeDatabase();
//			Log.w("Log", "lastProductId:  " + itineraryId);
//
//			if (itineraryId != "") {
//				maxRowID = itineraryId;
//			}
//
//		}
//
//		ArrayList<String[]> repStoreDataResponse = null;
//		while (repStoreDataResponse == null) {
//			try {
//				
//				WebService webService = new WebService();
//				repStoreDataResponse = webService.getItineraryListForRep(ItineraryList.REP_ID,ItineraryList.DEVICE_ID, maxRowID);
//
//				
//				
//				Thread.sleep(100);
//
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//		Log.w("Log", "repStoreDataResponse.size() :  " + repStoreDataResponse.size());
//		
//		
//		if (repStoreDataResponse.size() > 0) {
//
//			Itinerary itinerary = new Itinerary(DataSyncActivity.this);
//			itinerary.openWritableDatabase();
//
//			String timeStamp = new SimpleDateFormat(
//					"yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
//
//			for (int i = 0; i < repStoreDataResponse.size(); i++) {
//
//				String[] itnDetails = repStoreDataResponse.get(i);
//
//				Long result = itinerary.insertItinerary(itnDetails[8],itnDetails[0],
//						itnDetails[1], itnDetails[2], itnDetails[3], itnDetails[4],
//						itnDetails[5], itnDetails[6], itnDetails[7], timeStamp, "false", "false", "false");
//
//				if (result == -1) {
//					returnValue = 7;
//					break;
//				}
//
//				returnValue = 5;
//			}
//
//			itinerary.closeDatabase();
//
//		} else {
//
//			returnValue = 6;
//
//		}
//
//	} else {
//
//		returnValue = 3;
//	}
//
//	return returnValue;
//
//}
//
//}
//
//	public class DownloadProductsTask extends
//	AsyncTask<String, Integer, Integer> {
//
//private final Context context;
//
//public DownloadProductsTask(Context context) {
//	this.context = context;
//}
//
//@Override
//protected void onPreExecute() {
//
//
//}
//
//protected void onProgressUpdate(Integer... progress) {
//
//}
//
//protected void onPostExecute(Integer returnCode) {
//
//}
//
//@Override
//protected Integer doInBackground(String... params) {
//	// TODO Auto-generated method stub
//
//	int returnValue = 1;
//
//	Looper.prepare();
//
//	Log.w("Log", "param result : " + params[0]);
//
//	Log.w("Log", "loadProductRepStoreData result : starting ");
//
//	if (isOnline()) {
//
//		publishProgress(1);
//
//		Sequence sequenceObject = new Sequence(
//				DataSyncActivity.this);
//		sequenceObject.openReadableDatabase();
//
//		String lastRowId = sequenceObject
//				.getLastRowId("products");
//		sequenceObject.closeDatabase();
//
//		Log.w("Log", "lastRowId products :  " + lastRowId);
//
//		String maxRowID = "0";
//
//		if (lastRowId != "") {
//
//			Products prodObject = new Products(
//					DataSyncActivity.this);
//			prodObject.openReadableDatabase();
//
//			String lastProductId = prodObject
//					.getProductIdByRowId(lastRowId);
//			prodObject.closeDatabase();
//			Log.w("Log", "lastProductId:  " + lastProductId);
//
//			if (lastProductId != "") {
//				maxRowID = lastProductId;
//			}
//
//		}
//
//		ArrayList<String[]> repStoreDataResponse = null;
//		while (repStoreDataResponse == null) {
//			try {
//
//				WebService webService = new WebService();
//				repStoreDataResponse = webService
//						.getProductList(
//								ItineraryList.DEVICE_ID,
//								ItineraryList.REP_ID, maxRowID);
//
//				Thread.sleep(100);
//
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		Log.w("Log", "repStoreDataResponse.size() :  " + repStoreDataResponse.size());
//
//		if (repStoreDataResponse.size() > 0) {
//
//			Products products = new Products(DataSyncActivity.this);
//
//			products.openWritableDatabase();
//			String timeStamp = new SimpleDateFormat(
//					"yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
//
//			for (int i = 0; i < repStoreDataResponse.size(); i++) {
//
//				String[] custDetails = repStoreDataResponse.get(i);
//
//				Long result = products.insertProduct(custDetails[0],
//						custDetails[1], custDetails[2], custDetails[3],
//						custDetails[4], custDetails[5], custDetails[6],
//						custDetails[7], custDetails[8],"", custDetails[9],
//						custDetails[10], custDetails[11], custDetails[12],
//						custDetails[13], custDetails[14], custDetails[15],
//						custDetails[16],  timeStamp, custDetails[17]);
//
//				if (result == -1) {
//					returnValue = 7;
//					break;
//				}
//
//				returnValue = 5;
//			}
//
//			products.closeDatabase();
//
//		} else {
//
//			returnValue = 6;
//
//		}
//
//	} else {
//
//		returnValue = 3;
//	}
//
//	return returnValue;
//
//}
//
//}
//
//	public class DownloadCustomersTask extends
//	AsyncTask<String, Integer, Integer> {
//
//private final Context context;
//
//public DownloadCustomersTask(Context context) {
//	this.context = context;
//}
//
//@Override
//protected void onPreExecute() {
//
//}
//
//protected void onProgressUpdate(Integer... progress) {
//
//}
//
//protected void onPostExecute(Integer returnCode) {
//
//}
//
//@Override
//protected Integer doInBackground(String... params) {
//	// TODO Auto-generated method stub
//
//	int returnValue = 1;
//
//	Looper.prepare();
//
//	Log.w("Log", "param result : " + params[0]);
//
//	Log.w("Log", "loadProductRepStoreData result : starting ");
//
//	if (isOnline()) {
//
//		publishProgress(1);
//
//		Sequence sequenceObject = new Sequence(
//				DataSyncActivity.this);
//		sequenceObject.openReadableDatabase();
//
//		String lastRowId = sequenceObject
//				.getLastRowId("customers");
//		sequenceObject.closeDatabase();
//
//		Log.w("Log", "lastRowId products :  " + lastRowId);
//
//		String maxRowID = "0";
//
//		if (lastRowId != "") {
//
//			Customers customerObject = new Customers(
//					DataSyncActivity.this);
//			customerObject.openReadableDatabase();
//
//			String lastProductId = customerObject
//					.getPharmacyIdByRowId(lastRowId);
//			customerObject.closeDatabase();
//			Log.w("Log", "lastCustId:  " + lastProductId);
//
//			if (lastProductId != "") {
//				maxRowID = lastProductId;
//			}
//
//		}
//
//		ArrayList<String[]> repStoreDataResponse = null;
//		while (repStoreDataResponse == null) {
//			try {
//
//				WebService webService = new WebService();
//				repStoreDataResponse = webService
//						.getCustomerList(
//								ItineraryList.DEVICE_ID,
//								ItineraryList.REP_ID, maxRowID);
//
//				Thread.sleep(100);
//
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		Log.w("Log", "repStoreDataResponse.size() :  " + repStoreDataResponse.size());
//
//		if (repStoreDataResponse.size() > 0) {
//
//			Customers customers = new Customers(DataSyncActivity.this);
//
//			customers.openWritableDatabase();
//			String timeStamp = new SimpleDateFormat(
//					"yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
//
//			for (int i = 0; i < repStoreDataResponse.size(); i++) {
//
//				String[] custDetails = repStoreDataResponse.get(i);
//
//				Long result = customers.insertCustomer(custDetails[0], //pharmacyId
//						custDetails[1], //pharmacyCode,
//						custDetails[2], //dealerId, 
//						custDetails[3], //companyCode,
//						custDetails[4], //customerName,
//						custDetails[5], //address, 
//						custDetails[7], //area, 
//						custDetails[8], //town, 
//						custDetails[6], //district,
//						custDetails[9], //telephone, 
//						custDetails[10], //fax, 
//						custDetails[11], //email, 
//						custDetails[12], //customerStatus,	
//						custDetails[13], //creditLimit, 
//						"", //currentCredit, 
//						custDetails[14], //creditExpiryDate, 
//						custDetails[15], //creditDuration, 
//						custDetails[16], //vatNo, 
//						custDetails[17], //status, 
//						timeStamp, //timeStamp,
//						custDetails[28], //latitude, 
//						custDetails[29], //longitude, 
//						custDetails[20], //web, 
//						custDetails[21], //brNo, 
//						custDetails[22], //ownerContact, 
//						custDetails[24], //ownerWifeBday,
//						custDetails[23], //pharmacyRegNo, 
//						custDetails[25], //pharmacistName, 
//						custDetails[26], //purchasingOfficer, 
//						custDetails[27], //noStaff, 
//						custDetails[19], //customerCode
//						custDetails[30]		
//								);
//
//				if (result == -1) {
//					returnValue = 7;
//					break;
//				}
//
//				returnValue = 5;
//			}
//
//			customers.closeDatabase();
//
//		} else {
//
//			returnValue = 6;
//
//		}
//
//	} else {
//
//		returnValue = 3;
//	}
//
//	return returnValue;
//
//}
//
//}
//
//	

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {

            case 1:

                builder.setMessage("Unable to Upload data")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                Dialog alert = builder.create();
                return alert;

            case 2:

                builder.setMessage("Data uploaded successfully")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                Dialog alertTwo = builder.create();
                return alertTwo;

            case 3:

                builder.setMessage(
                        "There is no Internet Connectivity, Please check network connectivity.")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                Dialog alertThree = builder.create();
                return alertThree;

            case 4:

                builder.setMessage("Theres no data to upload")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                Dialog alertFour = builder.create();
                return alertFour;

            case 5:

                builder.setMessage("Data downloaded successfully")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                Dialog alertFive = builder.create();
                return alertFive;

            case 6:

                builder.setMessage("Theres no data to download")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                Dialog alertSix = builder.create();
                return alertSix;

            case 7:

                builder.setMessage("Unable to save data")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                Dialog alertSeven = builder.create();
                return alertSeven;

        }

        return null;
    }

    public boolean isOnline() {
        boolean flag = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            flag = true;
        }
        return flag;
    }


}
