package com.code.feutech.forge.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.code.feutech.forge.R;
import com.code.feutech.forge.config.TaskConfig;
import com.code.feutech.forge.items.Assign;
import com.code.feutech.forge.interfaces.OnLoadingListener;
import com.code.feutech.forge.utils.TaskCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AssignmentsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AssignmentsFragment extends Fragment implements OnLoadingListener {

    private OnFragmentInteractionListener mListener;
    private ArrayList<Assign> assignsList;
    private String URL;

    private View noDataContainer;
    private View listViewContainer;
    private View loadingContainer;
    private Button noDataBtnRefresh;
    private TextView noDataText;
    private ListView listView;
    private String requestId;

    public AssignmentsFragment() {
        // Required empty public constructor
        this.URL = TaskConfig.ASSIGNS_MY_URL;
        this.requestId = "assignments";
    }

    @SuppressLint("ValidFragment")
    public AssignmentsFragment(String URL, String requestId) {
        this.URL = URL;
        this.requestId = requestId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_assignments, container, false);

        // set components
        listViewContainer = view.findViewById(R.id.assignments_list_view_container);
        noDataContainer = view.findViewById(R.id.no_data_container);
        loadingContainer = view.findViewById(R.id.loading_container);
        noDataBtnRefresh = view.findViewById(R.id.no_data_btn_refresh);
        noDataText = view.findViewById(R.id.no_data_text);
        listView = view.findViewById(R.id.assignments_list_view);

        // set listeners
        noDataBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetch(view);
            }
        });

        // set instances
        // do not renew if already exists
        if (assignsList == null) {
            assignsList = new ArrayList<>();
        } else {
            assignsList.clear();
        }
        fetch(noDataBtnRefresh);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

    }

    // methods
    public String getAppTitle() {
        return requestId == "assignments" ? "Assignments" : "Reviews";
    }

    private void fetch(View view) {
        onLoading();
        TaskCreator.execute(view.getContext(), this.getActivity(), requestId, URL);
    }

    private void setListAdapter() {
        if (listView.getAdapter() == null) {
            ArrayAdapter<Assign> adapter = new Assign.AssignArrayAdapter(getContext(), android.R.layout.simple_list_item_1, assignsList);
            listView.setAdapter(adapter);
        } else {
            ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
        }
    }

    public void setData(JSONArray assigns) throws JSONException {
        // clear list
        assignsList.clear();
        // parse array
        for (int i = 0; i < assigns.length(); i++) {
            final JSONObject jsonAssign = assigns.getJSONObject(i);
            // set assign here
            final Assign assign = new Assign(jsonAssign);
            Log.d("tagx", jsonAssign.toString());
            assignsList.add(assign);
        }
        onHasData();
        setListAdapter();
    }


    // loading listener
    @Override
    public void onHasData() {
        listViewContainer.setVisibility(View.VISIBLE);
        loadingContainer.setVisibility(View.GONE);
        noDataContainer.setVisibility(View.GONE);
    }

    @Override
    public void onNoData() {
        listViewContainer.setVisibility(View.GONE);
        loadingContainer.setVisibility(View.GONE);
        noDataContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoData(String msg) {
        noDataText.setText(msg);
        onNoData();
    }

    @Override
    public void onNoData(int resid) {
        noDataText.setText(resid);
        onNoData();
    }

    @Override
    public void onLoading() {
        listViewContainer.setVisibility(View.GONE);
        loadingContainer.setVisibility(View.VISIBLE);
        noDataContainer.setVisibility(View.GONE);
    }
}
