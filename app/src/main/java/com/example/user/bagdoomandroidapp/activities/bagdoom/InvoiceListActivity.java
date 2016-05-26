package com.example.user.bagdoomandroidapp.activities.bagdoom;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.bagdoomandroidapp.R;
import com.example.user.bagdoomandroidapp.activities.template.TemplateActivity;
import com.example.user.bagdoomandroidapp.adapters.RecyclerViewListAdapter;
import com.example.user.bagdoomandroidapp.data.constants.ApplicationConstants;
import com.example.user.bagdoomandroidapp.data.constants.JSONConstants;
import com.example.user.bagdoomandroidapp.datamodels.ITable;
import com.example.user.bagdoomandroidapp.datamodels.Invoice;
import com.example.user.bagdoomandroidapp.datamodels.OnRecyclerViewItemListener;
import com.example.user.bagdoomandroidapp.datamodels.User;
import com.example.user.bagdoomandroidapp.parser.JSONHandler;
import com.example.user.bagdoomandroidapp.utils.CustomTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class InvoiceListActivity extends TemplateActivity implements OnRecyclerViewItemListener{

    RecyclerView invoiceRecyclerView;
    List<Invoice> invoiceList ;
    SwipeRefreshLayout invoiceSwipeRefreshLayout;
    public static final String LOG ="InvoiceListActivity";
    @Override
    public void initView() {
        setContentView(R.layout.activity_invoice_list);
        templateToolbar = (Toolbar) findViewById(R.id.toolbar);
        invoiceSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_invoice_list);
        invoiceRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_invoices);
        invoiceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        subtitleText = (TextView) findViewById(R.id.text_subtitle);
        invoiceList = new ArrayList<Invoice>();
        subtitleText.setText("Invoices");
    }
    @Override
    public void loadData() {
        Invoice invoice = new Invoice();
        invoice.getNewWhereClause().addYESWhereClauseString(User.Variable.STRING_PHONE, ApplicationConstants.PHONE_NUMBER);
        List<ITable> iTableList = localDataBaseHelper.selectRows(invoice);
        Log.d(InvoiceListActivity.LOG+"&",iTableList.size()+"");
        for (ITable iTable: iTableList){
            Log.d(InvoiceListActivity.LOG, iTable.toString());
            invoiceList.add((Invoice) iTable);
        }
    }

    @Override
    public void initializeViewByData() {
        for (Invoice invoice:invoiceList){
            Log.d(InvoiceListActivity.LOG, invoice.toString());
        }
        invoiceRecyclerView.setAdapter(new RecyclerViewListAdapter(InvoiceListActivity.this,R.layout.card_invoice, invoiceList.size()));
        templateToolbar.setNavigationIcon(R.drawable.arrow_back_white_24x24);
        templateToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void listenView() {
        invoiceSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.button_background);
        invoiceSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                StringRequest dataRequest = new StringRequest(Request.Method.GET,
                        ApplicationConstants.PHP_INVOICE,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                invoiceSwipeRefreshLayout.setRefreshing(false);
                                JSONHandler jsonHandler = new JSONHandler();
                                List<ITable> iTables = new ArrayList<ITable>();
                                try {
                                    JSONObject responseJSJsonObject = new JSONObject(response);
                                    Log.d(InvoiceListActivity.LOG, response);
                                    // get selectedCategory json
                                    JSONArray categoryJSONArray = responseJSJsonObject.optJSONArray(JSONConstants.OBJECT_INVOICE);
                                    // get selectedCategory list from selectedCategory json
                                    List<ITable> categoryList = jsonHandler.getRowsFromJSONArray(categoryJSONArray, new Invoice());
                                    // insert selectedCategory into Local DB
                                    localDataBaseHelper.deleteRows(new Invoice());
                                    localDataBaseHelper.insertRowsFromServer(categoryList, new Invoice());
                                    // get local categories from local DB;
                                    Invoice invoice = new Invoice();
                                    invoice.getNewWhereClause().addYESWhereClauseString(User.Variable.STRING_PHONE, ApplicationConstants.PHONE_NUMBER);
                                    List<ITable> localInvoiceList = localDataBaseHelper.selectRows(invoice);
                                    invoiceList = new ArrayList<Invoice>();
                                    for (ITable iTable : localInvoiceList) {
                                        Log.d(InvoiceListActivity.LOG, iTable.toString());
                                        invoiceList.add((Invoice) iTable);
                                    }
                                    invoiceRecyclerView.setAdapter(new RecyclerViewListAdapter(InvoiceListActivity.this,R.layout.card_invoice, invoiceList.size()));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                volleyError.printStackTrace();
                            }
                        }
                );
                Volley.newRequestQueue(InvoiceListActivity.this).add(dataRequest);
            }
        });
        invoiceRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVertical = (invoiceRecyclerView == null || invoiceRecyclerView.getChildCount() == 0) ? 0 : invoiceRecyclerView.getChildAt(0).getTop();
                invoiceRecyclerView.setEnabled(topRowVertical >= 0);
            }
        });
    }

    @Override
    public void listenItem(View view, final int position) {
        TextView invoiceText = (TextView) view.findViewById(R.id.text_card_invoice_invoice_id);
        TextView totalPriceText = (TextView) view.findViewById(R.id.text_card_invoice_total_price);
        TextView invoiceStatusText = (TextView) view.findViewById(R.id.text_card_invoice_invoice_status);
        TextView invoiceTimeText = (TextView) view.findViewById(R.id.text_card_invoice_invoice_time);

        invoiceText.setText("INVOICE ID:"+invoiceList.get(position).invoiceID+"");
        totalPriceText.setText(invoiceList.get(position).totalPrice+"");
        if (invoiceList.get(position).invoiceStatus.equals("0")){
            invoiceStatusText.setText("Is Not Delivered");
        }
        else{
            invoiceStatusText.setText("Delivered");
        }

        Calendar localCalendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        localCalendar.add(Calendar.HOUR, 6);
        Log.d("Invoice Time", new CustomTime(localCalendar).toString());
        invoiceTimeText.setText(new CustomTime(invoiceList.get(position).invoiceTime).subtractTimeFrom(new CustomTime(localCalendar)));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationConstants.INVOICE_ID = invoiceList.get(position).invoiceID;
                Intent intent = new Intent(InvoiceListActivity.this, OrderListActivity.class);
                startActivity(intent);
            }
        });

    }
}
