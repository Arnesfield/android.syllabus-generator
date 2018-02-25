package com.example.code.forge;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SyllabiFragment extends Fragment {
    private ArrayAdapter<String> arrayAdapter;
    private List<String> syllabusArrayList;
    private String[] subjectTitles = {"ITNTWRK3","ITWPROMAN","ITWSOFTEN","ITWSPEC4","ITWSPEC6","PSYCH"};

    public SyllabiFragment() {
        // Required empty public constructor
    }
    private ListView syllabusList_ListView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragmentsyllabusArrayList = new ArrayList<>();
        View syllabiView = inflater.inflate(R.layout.fragment_syllabi, container, false);
        syllabusList_ListView = syllabiView.findViewById(R.id.syllabusList_ListView);
        syllabusArrayList = new ArrayList<>();

        for (int i = 0; i < subjectTitles.length; i++ ){
            syllabusArrayList.add(subjectTitles[i]);
        }
        arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, syllabusArrayList);
        syllabusList_ListView.setAdapter(arrayAdapter);
        return syllabiView;
    }

}
