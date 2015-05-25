package app.in.com.testinapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import adapter.AdapterMaxPoints;
import util.IabHelper;
import util.IabResult;
import util.Inventory;
import util.Purchase;
import util.SkuDetails;

public class MainActivity extends Activity {
    private Context context;
    private String tag;

    int[] resId = { R.drawable.mp_01
            , R.drawable.mp_02, R.drawable.mp_03
            , R.drawable.mp_04, R.drawable.mp_05
            , R.drawable.mp_06};

    String[] list = { "100 Points", "165 Points", "345 Points"
            , "1,080 Points", "1,875 Points", "3,899 Points"};

    String[] bath = { "2.99", "4.99", "9.99"
            , "29.99", "49.99", "99.99"};
    private IabHelper mHelper;
    private final String base64PublicKey = "<public key>";
    private boolean isSetup;

    // ProductID
    private final String productID = "android.test.purchased";	// Test Product ID by Google

    // View
    private Button btnQuery, btnPurchase, btnConsume;

    // Purchase
    private Purchase purchaseOwned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Context
        context = getApplicationContext();
        ListView mListView = (ListView)findViewById(R.id.list_view);
        AdapterMaxPoints  adapterMaxPoints = new AdapterMaxPoints(getApplication(),list,resId,bath);
        final View headerView = getLayoutInflater().inflate(R.layout.item_header_maxpoint, mListView, false);
        mListView.addHeaderView(headerView);
        mListView.setAdapter(adapterMaxPoints);
        // log tag
        tag = "in_app_billing_ex2";

        // Helper
        mHelper = new IabHelper(context, base64PublicKey);
//		mHelper.enableDebugLogging(true);
        mHelper.enableDebugLogging(true, tag);

        // Assign View
//        btnQuery    = (Button) findViewById(R.id.btnQuery);
//        btnPurchase = (Button) findViewById(R.id.btnPurchase);
//        btnConsume  = (Button) findViewById(R.id.btnConsume);

        try {
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                @Override
                public void onIabSetupFinished(IabResult result) {
                    boolean blnSuccess = result.isSuccess();
                    boolean blnFail = result.isFailure();

                    isSetup = blnSuccess;

                    Toast.makeText(context, "mHelper.startSetup() - blnSuccess return " + String.valueOf(blnSuccess), Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "mHelper.startSetup() - blnFail return " + String.valueOf(blnFail), Toast.LENGTH_SHORT).show();
                    Log.i(tag, "mHelper.startSetup() ...");
                    Log.i(tag, "	- blnSuccess return " + String.valueOf(blnSuccess));
                    Log.i(tag, "	- blnFail return " + String.valueOf(blnFail));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

            isSetup = false;
            Toast.makeText(context, "mHelper.startSetup() - fail!", Toast.LENGTH_SHORT).show();
            Log.w(tag, "mHelper.startSetup() - fail!");
        }
        mListView.setAdapter(adapterMaxPoints);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplication(),"Check",Toast.LENGTH_SHORT).show();
                if (!isSetup) return;
                mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
                    @Override
                    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                        boolean blnSuccess = result.isSuccess();
                        boolean blnFail = result.isFailure();

                        Toast.makeText(context, "mHelper.queryInventoryAsync() - blnSuccess return " + String.valueOf(blnSuccess), Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, "mHelper.queryInventoryAsync() - blnFail return " + String.valueOf(blnFail), Toast.LENGTH_SHORT).show();
                        Log.i(tag, "mHelper.queryInventoryAsync() ...");
                        Log.i(tag, "	- blnSuccess return " + String.valueOf(blnSuccess));
                        Log.i(tag, "	- blnFail return " + String.valueOf(blnFail));

                        if (!blnSuccess) return;

                        Log.i(tag, "	- inv.hasPurchase()   = " + inv.hasPurchase(productID));
                        Log.i(tag, "	- inv.getPurchase()   = " + inv.getPurchase(productID));
                        Log.i(tag, "	- inv.hasDetails()    = " + inv.hasDetails(productID));
                        Log.i(tag, "	- inv.getSkuDetails() = " + inv.getSkuDetails(productID));

                        if (!inv.hasPurchase(productID)) return;

                        purchaseOwned = inv.getPurchase(productID);

                        Log.i(tag, "	- inv.getPurchase() ...");
                        Log.i(tag, "		.getDeveloperPayload() = " + purchaseOwned.getDeveloperPayload());
                        Log.i(tag, "		.getItemType()         = " + purchaseOwned.getItemType());
                        Log.i(tag, "		.getOrderId()          = " + purchaseOwned.getOrderId());
                        Log.i(tag, "		.getOriginalJson()     = " + purchaseOwned.getOriginalJson());
                        Log.i(tag, "		.getPackageName()      = " + purchaseOwned.getPackageName());
                        Log.i(tag, "		.getPurchaseState()    = " + String.valueOf(purchaseOwned.getPurchaseState()));
                        Log.i(tag, "		.getPurchaseTime()     = " + String.valueOf(purchaseOwned.getPurchaseTime()));
                        Log.i(tag, "		.getSignature()        = " + purchaseOwned.getSignature());
                        Log.i(tag, "		.getSku()              = " + purchaseOwned.getSku());
                        Log.i(tag, "		.getToken()            = " + purchaseOwned.getToken());

                        if (!inv.hasDetails(productID)) return;

                        SkuDetails skuDetails = inv.getSkuDetails(productID);
                        Log.i(tag, "	- inv.getSkuDetails() ...");
                        Log.i(tag, "		.getDescription() = " + skuDetails.getDescription());
                        Log.i(tag, "		.getPrice()       = " + skuDetails.getPrice());
                        Log.i(tag, "		.getSku()         = " + skuDetails.getSku());
                        Log.i(tag, "		.getTitle()       = " + skuDetails.getTitle());
                        Log.i(tag, "		.getType()        = " + skuDetails.getType());
                    }
                });
            }
        });
//        btnQuery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isSetup) return;
//
//                mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
//                    @Override
//                    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
//                        boolean blnSuccess = result.isSuccess();
//                        boolean blnFail = result.isFailure();
//
//                        Toast.makeText(context, "mHelper.queryInventoryAsync() - blnSuccess return " + String.valueOf(blnSuccess), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(context, "mHelper.queryInventoryAsync() - blnFail return " + String.valueOf(blnFail), Toast.LENGTH_SHORT).show();
//                        Log.i(tag, "mHelper.queryInventoryAsync() ...");
//                        Log.i(tag, "	- blnSuccess return " + String.valueOf(blnSuccess));
//                        Log.i(tag, "	- blnFail return " + String.valueOf(blnFail));
//
//                        if (!blnSuccess) return;
//
//                        Log.i(tag, "	- inv.hasPurchase()   = " + inv.hasPurchase(productID));
//                        Log.i(tag, "	- inv.getPurchase()   = " + inv.getPurchase(productID));
//                        Log.i(tag, "	- inv.hasDetails()    = " + inv.hasDetails(productID));
//                        Log.i(tag, "	- inv.getSkuDetails() = " + inv.getSkuDetails(productID));
//
//                        if (!inv.hasPurchase(productID)) return;
//
//                        purchaseOwned = inv.getPurchase(productID);
//
//                        Log.i(tag, "	- inv.getPurchase() ...");
//                        Log.i(tag, "		.getDeveloperPayload() = " + purchaseOwned.getDeveloperPayload());
//                        Log.i(tag, "		.getItemType()         = " + purchaseOwned.getItemType());
//                        Log.i(tag, "		.getOrderId()          = " + purchaseOwned.getOrderId());
//                        Log.i(tag, "		.getOriginalJson()     = " + purchaseOwned.getOriginalJson());
//                        Log.i(tag, "		.getPackageName()      = " + purchaseOwned.getPackageName());
//                        Log.i(tag, "		.getPurchaseState()    = " + String.valueOf(purchaseOwned.getPurchaseState()));
//                        Log.i(tag, "		.getPurchaseTime()     = " + String.valueOf(purchaseOwned.getPurchaseTime()));
//                        Log.i(tag, "		.getSignature()        = " + purchaseOwned.getSignature());
//                        Log.i(tag, "		.getSku()              = " + purchaseOwned.getSku());
//                        Log.i(tag, "		.getToken()            = " + purchaseOwned.getToken());
//
//                        if (!inv.hasDetails(productID)) return;
//
//                        SkuDetails skuDetails = inv.getSkuDetails(productID);
//                        Log.i(tag, "	- inv.getSkuDetails() ...");
//                        Log.i(tag, "		.getDescription() = " + skuDetails.getDescription());
//                        Log.i(tag, "		.getPrice()       = " + skuDetails.getPrice());
//                        Log.i(tag, "		.getSku()         = " + skuDetails.getSku());
//                        Log.i(tag, "		.getTitle()       = " + skuDetails.getTitle());
//                        Log.i(tag, "		.getType()        = " + skuDetails.getType());
//                    }
//                });
//            }
//        });
//
//        btnPurchase.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isSetup) return;
//
//                mHelper.launchPurchaseFlow(MainActivity.this, productID, 1001, new IabHelper.OnIabPurchaseFinishedListener() {
//                    @Override
//                    public void onIabPurchaseFinished(IabResult result, Purchase info) {
//                        boolean blnSuccess = result.isSuccess();
//                        boolean blnFail = result.isFailure();
//
//                        Toast.makeText(context, "mHelper.launchPurchaseFlow() - blnSuccess return " + String.valueOf(blnSuccess), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(context, "mHelper.launchPurchaseFlow() - blnFail return " + String.valueOf(blnFail), Toast.LENGTH_SHORT).show();
//                        Log.i(tag, "mHelper.launchPurchaseFlow() ...");
//                        Log.i(tag, "	- blnSuccess return " + String.valueOf(blnSuccess));
//                        Log.i(tag, "	- blnFail return " + String.valueOf(blnFail));
//
//                        if (!blnSuccess) return;
//
//                        purchaseOwned = info;
//
//                        Log.i(tag, "	- info ...");
//                        Log.i(tag, "		.getDeveloperPayload() = " + info.getDeveloperPayload());
//                        Log.i(tag, "		.getItemType()         = " + info.getItemType());
//                        Log.i(tag, "		.getOrderId()          = " + info.getOrderId());
//                        Log.i(tag, "		.getOriginalJson()     = " + info.getOriginalJson());
//                        Log.i(tag, "		.getPackageName()      = " + info.getPackageName());
//                        Log.i(tag, "		.getPurchaseState()    = " + String.valueOf(info.getPurchaseState()));
//                        Log.i(tag, "		.getPurchaseTime()     = " + String.valueOf(info.getPurchaseTime()));
//                        Log.i(tag, "		.getSignature()        = " + info.getSignature());
//                        Log.i(tag, "		.getSku()              = " + info.getSku());
//                        Log.i(tag, "		.getToken()            = " + info.getToken());
//                    }
//                }, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
//            }
//        });
//
//        btnConsume.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isSetup) return;
//                if (purchaseOwned == null) return;
//
//                mHelper.consumeAsync(purchaseOwned, new IabHelper.OnConsumeFinishedListener() {
//                    @Override
//                    public void onConsumeFinished(Purchase purchase, IabResult result) {
//                        boolean blnSuccess = result.isSuccess();
//                        boolean blnFail = result.isFailure();
//
//                        Toast.makeText(context, "mHelper.consumeAsync() - blnSuccess return " + String.valueOf(blnSuccess), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(context, "mHelper.consumeAsync() - blnFail return " + String.valueOf(blnFail), Toast.LENGTH_SHORT).show();
//                        Log.i(tag, "mHelper.consumeAsync() ...");
//                        Log.i(tag, "	- blnSuccess return " + String.valueOf(blnSuccess));
//                        Log.i(tag, "	- blnFail return " + String.valueOf(blnFail));
//
//                        if (!blnSuccess) return;
//
//                        purchaseOwned = null;
//
//                        Log.i(tag, "	- purchase ...");
//                        Log.i(tag, "		.getDeveloperPayload() = " + purchase.getDeveloperPayload());
//                        Log.i(tag, "		.getItemType()         = " + purchase.getItemType());
//                        Log.i(tag, "		.getOrderId()          = " + purchase.getOrderId());
//                        Log.i(tag, "		.getOriginalJson()     = " + purchase.getOriginalJson());
//                        Log.i(tag, "		.getPackageName()      = " + purchase.getPackageName());
//                        Log.i(tag, "		.getPurchaseState()    = " + String.valueOf(purchase.getPurchaseState()));
//                        Log.i(tag, "		.getPurchaseTime()     = " + String.valueOf(purchase.getPurchaseTime()));
//                        Log.i(tag, "		.getSignature()        = " + purchase.getSignature());
//                        Log.i(tag, "		.getSku()              = " + purchase.getSku());
//                        Log.i(tag, "		.getToken()            = " + purchase.getToken());
//                    }
//                });
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isSetup) {
            boolean blnResult = mHelper.handleActivityResult(requestCode, resultCode, data);

            Toast.makeText(context, "onActivityResult() - mHelper.handleActivityResult() = " + blnResult, Toast.LENGTH_SHORT).show();
            Log.i(tag, "onActivityResult() - mHelper.handleActivityResult() = " + blnResult);

            if (blnResult) return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (isSetup) mHelper.dispose();
        mHelper = null;

        super.onDestroy();
    }
}