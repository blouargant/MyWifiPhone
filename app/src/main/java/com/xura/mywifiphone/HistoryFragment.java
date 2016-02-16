package com.xura.mywifiphone;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class HistoryFragment extends Fragment {

    private Contacts contacts;
    //private OnFragmentInteractionListener mListener;

    public HistoryFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RecyclerView rv = (RecyclerView) inflater.inflate(
                R.layout.contact_list_recycler, container, false);

        if (contacts == null) {
            String contactDic = "";
            if (savedInstanceState != null) {
                contactDic = savedInstanceState.getString("contacts");
            }
            if (contactDic != "") {
                contacts = new Contacts(getActivity(), contactDic);
            } else {
                contacts = new Contacts(getActivity());
            }
        }
        setupRecyclerView(rv);
        return rv;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String contactDic = contacts.contactDic.toString();
        outState.putString("contacts", contactDic);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), contacts));
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<String> mValues;
        private Contacts contacts;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final ImageView mImageView;
            public final TextView mTextView;
            public final TextView mDateView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.history_call_icon);
                mTextView = (TextView) view.findViewById(android.R.id.text1);
                mDateView = (TextView) view.findViewById(R.id.callDate);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }

        public String getValueAt(int position) {
            return mValues.get(position);
        }

        public SimpleStringRecyclerViewAdapter(Context context, Contacts contacsRef) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            contacts = contacsRef;
            mValues = contacts.readLastContactedNames();

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.history_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            String contactName = mValues.get(position);
            holder.mBoundString = contactName;
            holder.mTextView.setText(contactName);
            JsonDic contactInfo = contacts.contactDic.getDic(contactName);
            holder.mDateView.setText(contactInfo.getString("last"));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    ContactInfo mContactInfo = new ContactInfo();
                    mContactInfo.setName(holder.mBoundString);
                    mContactInfo.setDefaultBackground(contacts.getDrawableBackground(holder.mBoundString));

                    Intent intent = new Intent(context, ContactDetailActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putParcelable(ContactDetailActivity.CONTACT_KEY, mContactInfo);
                    intent.putExtras(mBundle);
                    //intent.putExtra(ContactDetailActivity.EXTRA_NAME, holder.mBoundString);
                    //String backdrop = String.valueOf(contacts.getDrawableBackground(holder.mBoundString));
                    //intent.putExtra(ContactDetailActivity.BACKGROUND, backdrop);
                    context.startActivity(intent);
                }
            });
            contacts.setContactThumbnail(holder.mImageView, contactName);

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }









    /// Auto generated functions :
/*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
*/
}
