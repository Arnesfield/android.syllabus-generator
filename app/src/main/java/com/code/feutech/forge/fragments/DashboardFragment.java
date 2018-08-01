package com.code.feutech.forge.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.code.feutech.forge.R;
import com.code.feutech.forge.SyllabusActivity;
import com.code.feutech.forge.interfaces.AppTitleActivityListener;
import com.code.feutech.forge.items.Syllabus;

import org.json.JSONException;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DashboardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class DashboardFragment extends Fragment implements AppTitleActivityListener {

    private Context context;
    private OnFragmentInteractionListener mListener;
    private ListView listView;
    private ArrayList<Syllabus> list;
    private View view;

    public DashboardFragment() {
        // Required empty public constructor
    }

    private void viewSyllabus(int index) {
        if (list == null) {
            return;
        }
        final Syllabus syllabus = list.get(index);

        final Intent intent = new Intent(getContext(), SyllabusActivity.class);
        intent.putExtra("syllabusId", syllabus.getId());
        intent.putExtra("syllabus", syllabus.getJSON());
        intent.putExtra("starDialog", true);
        startActivity(intent);
    }

    private void hasData(View view, boolean b) {
        final TextView tvNoData = view.findViewById(R.id.dashboard_no_data_text);
        final View main = view.findViewById(R.id.dashboard_main_container);

        tvNoData.setVisibility(b ? View.GONE : View.VISIBLE);
        main.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    private void setList(View view) throws JSONException {
        final Syllabus[] syllabi = mListener.getSyllabi();

        hasData(view, syllabi.length > 0);

        // create list here
        final HtmlTextView tvSubtitle = view.findViewById(R.id.dashboard_subtitle);
        listView = view.findViewById(R.id.dashboard_list_view);

        listView.setLongClickable(true);
        registerForContextMenu(listView);

        if (list == null) {
            list = new ArrayList<>(Arrays.asList(syllabi));
        } else {
            list.clear();
            list.addAll(Arrays.asList(syllabi));
        }

        tvSubtitle.setHtml("Total saved: <b>" + list.size() + "</b>");

        if (listView.getAdapter() == null) {
            listView.setAdapter(new Syllabus.SyllabusArrayAdapter(true, context, android.R.layout.simple_list_item_1, list));
        } else {
            ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
        }

        if (listView.getOnItemClickListener() == null) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    viewSyllabus(i);
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        try {
            setList(view);
        } catch (JSONException e) {
            Log.e("tagx", "Error: ", e);
            hasData(view, false);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // reset the list
        try {
            if (view != null) {
                setList(view);
            }
        } catch (JSONException e) {
            Log.e("tagx", "Error: ", e);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            this.context = context;
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
        mListener = null;
    }

    // context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.dashboard_list_view) {
            final MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_context_syllabus, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (view == null || list == null) {
            super.onContextItemSelected(item);
        }

        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_dasboard_view: {
                viewSyllabus(info.position);
                return true;
            }
            case R.id.menu_dasboard_unsave: {
                // show dialog here
                new AlertDialog.Builder(getContext())
                    .setTitle(R.string.dialog_title_unsave)
                    .setMessage(R.string.msg_dialog_unsave)
                    .setPositiveButton(R.string.dialog_unsave, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Syllabus.setSyllabusToggle(getContext(), list.get(info.position));
                            // show message
                            Snackbar.make(view, R.string.msg_unsaved_offline, Snackbar.LENGTH_LONG).show();
                            try {
                                setList(view);
                            } catch (JSONException e) {
                                Log.e("tagx", "Error: ", e);
                                hasData(view, false);
                            }
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
                return true;
            }
        }
        return super.onContextItemSelected(item);
    }

    // app title activity listener
    @Override
    public String getAppTitle(Resources resources) {
        return resources.getString(R.string.nav_dashboard);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        Syllabus[] getSyllabi() throws JSONException;
    }
}
