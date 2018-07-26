package com.code.feutech.forge.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.code.feutech.forge.CourseInfoActivity;
import com.code.feutech.forge.R;
import com.code.feutech.forge.config.TaskConfig;
import com.code.feutech.forge.items.Course;
import com.code.feutech.forge.utils.OnLoadingListener;
import com.code.feutech.forge.utils.TaskCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CoursesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CoursesFragment extends Fragment implements OnLoadingListener {

    private OnFragmentInteractionListener mListener;
    private ArrayList<Course> coursesList;
    private String URL;

    private View noDataContainer;
    private View listViewContainer;
    private View loadingContainer;
    private TextView noDataText;
    private ListView listView;
    private String requestId;

    public CoursesFragment() {
        // Required empty public constructor
        this.URL = TaskConfig.COURSES_URL;
        this.requestId = "courses";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_courses, container, false);

        // set components
        listViewContainer = view.findViewById(R.id.courses_list_view_container);
        noDataContainer = view.findViewById(R.id.no_data_container);
        loadingContainer = view.findViewById(R.id.loading_container);
        noDataText = view.findViewById(R.id.no_data_text);
        listView = view.findViewById(R.id.courses_list_view);
        Button noDataBtnRefresh = view.findViewById(R.id.no_data_btn_refresh);

        // set listeners
        noDataBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetch(view);
            }
        });

        // listView listeners
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // using the courseList, pass that data
                final Course course = CoursesFragment.this.coursesList.get(i);

                Intent intent = new Intent(CoursesFragment.this.getContext(), CourseInfoActivity.class);
                intent.putExtra("courseId", course.getId());
                startActivity(intent);
            }
        });

        // set instances
        // do not renew if already exists
        if (coursesList == null) {
            coursesList = new ArrayList<>();
        } else {
            coursesList.clear();
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
        return "Courses";
    }

    private void fetch(View view) {
        onLoading();
        TaskCreator.execute(view.getContext(), this.getActivity(), requestId, URL);
    }

    private void setListAdapter() {
        if (listView.getAdapter() == null) {
            ArrayAdapter<Course> adapter = new Course.CourseArrayAdapter(getContext(), android.R.layout.simple_list_item_1, coursesList);
            listView.setAdapter(adapter);
        } else {
            ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
        }
    }

    public void setData(JSONArray courses) throws JSONException {
        // clear list
        coursesList.clear();
        // parse array
        for (int i = 0; i < courses.length(); i++) {
            final JSONObject jsonCourse = courses.getJSONObject(i);
            // set course here
            final Course course = new Course(jsonCourse);
            Log.d("tagx", jsonCourse.toString());
            coursesList.add(course);
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
