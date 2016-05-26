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
import com.example.user.bagdoomandroidapp.datamodels.Product;
import com.example.user.bagdoomandroidapp.datamodels.RemoteOrder;
import com.example.user.bagdoomandroidapp.parser.JSONHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderListActivity extends TemplateActivity implements OnRecyclerViewItemListener{

    RecyclerView invoiceOrdersRecyclerView;
    List<RemoteOrder> orderList ;
    SwipeRefreshLayout invoiceSwipeRefreshLayout;
    @Override
    public void initView() {
        setContentView(R.layout.activity_order_list);
        templateToolbar = (Toolbar) findViewById(R.id.toolbar);
        invoiceSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_invoice_order_list);
        invoiceOrdersRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_invoice_orders);
        invoiceOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<RemoteOrder>();
        subtitleText = (TextView) findViewById(R.id.text_subtitle);
    }

    @Override
    public void loadData() {
        RemoteOrder remoteOrder = new RemoteOrder();
        remoteOrder.invoiceID = ApplicationConstants.INVOICE_ID;
        remoteOrder.getWhereClause().addYESWhereClauseString(Invoice.Variable.INT_INVOICE_ID,remoteOrder.invoiceID);
        List<ITable> iTableList = localDataBaseHelper.selectRows(remoteOrder);
        for (ITable iTable: iTableList){
            orderList.add((RemoteOrder) iTable);
        }

    }
    @Override
    public void initializeViewByData() {
        invoiceOrdersRecyclerView.setAdapter(new RecyclerViewListAdapter(OrderListActivity.this,R.layout.card_invoice_order, orderList.size()));
        subtitleText.setText("ORDERS - INVOICE ID "+ApplicationConstants.INVOICE_ID);
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
                        ApplicationConstants.PHP_ORDER,
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
                                    JSONArray orderJSONArray = responseJSJsonObject.optJSONArray(JSONConstants.OBJECT_ORDER);
                                    // get selectedCategory list from selectedCategory json
                                    List<ITable> orderITableList = jsonHandler.getRowsFromJSONArray(orderJSONArray, new RemoteOrder());
                                    // insert selectedCategory into Local DB
                                    localDataBaseHelper.deleteRows(new RemoteOrder());
                                    localDataBaseHelper.insertRowsFromServer(orderITableList, new RemoteOrder());
                                    // get local categories from local DB;
                                    RemoteOrder remoteOrder = new RemoteOrder();
                                    remoteOrder.invoiceID = ApplicationConstants.INVOICE_ID;
                                    remoteOrder.getNewWhereClause().addYESWhereClauseString(Invoice.Variable.INT_INVOICE_ID, remoteOrder.invoiceID);
                                    List<ITable> localInvoiceList = localDataBaseHelper.selectRows(remoteOrder);
                                    orderList = new ArrayList<RemoteOrder>();
                                    for (ITable iTable : localInvoiceList) {
                                        Log.d(InvoiceListActivity.LOG, iTable.toString());
                                        orderList.add((RemoteOrder) iTable);
                                    }
                                    Collections.reverse(orderList);
                                    invoiceOrdersRecyclerView.setAdapter(new RecyclerViewListAdapter(OrderListActivity.this,R.layout.card_invoice_order, orderList.size()));
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
                Volley.newRequestQueue(OrderListActivity.this).add(dataRequest);
            }
        });
        invoiceOrdersRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVertical = (invoiceOrdersRecyclerView == null || invoiceOrdersRecyclerView.getChildCount() == 0) ? 0 : invoiceOrdersRecyclerView.getChildAt(0).getTop();
                invoiceOrdersRecyclerView.setEnabled(topRowVertical >= 0);
            }
        });
    }

    @Override
    public void listenItem(View view, final int position) {

        TextView productText = (TextView) view.findViewById(R.id.text_card_invoice_order_product_name);
        TextView subTotalPriceText = (TextView) view.findViewById(R.id.text_card_invoice_order_sub_total_price);
        TextView quantityText = (TextView) view.findViewById(R.id.text_card_invoice_order_quantity);
        TextView orderStatusText = (TextView) view.findViewById(R.id.text_card_invoice_order_status);
        Product product = new Product();
        product.product_id = orderList.get(position).productID;
        product.getNewWhereClause().addYESWhereClauseString(Product.Variable.INT_PRODUCT_ID, product.product_id);
        product = (Product) localDataBaseHelper.selectRow(product);
        productText.setText(product.product_name);
        subTotalPriceText.setText("Unit Price "+orderList.get(position).unit_price+"");
        quantityText.setText("Quantity "+orderList.get(position).quantity+"");
        if (orderList.get(position).orderStatus.equals("0")){
            orderStatusText.setText("Is not Delivered");
        }else{
            orderStatusText.setText("Delivered");
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationConstants.PRODUCT_ID =orderList.get(position).productID;
                Intent intent = new Intent(OrderListActivity.this, ProductDetailsActivity.class);
                startActivity(intent);
            }
        });
    }
}
