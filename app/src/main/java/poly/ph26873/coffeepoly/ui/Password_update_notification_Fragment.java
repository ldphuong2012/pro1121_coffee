package poly.ph26873.coffeepoly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.activities.MainActivity;


public class Password_update_notification_Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password_update_notification_, container, false);
    }

    private Button btn_back_to_home;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_back_to_home = view.findViewById(R.id.btn_back_to_home);
        btn_back_to_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).replaceFragmemt(((MainActivity) getActivity()).fragment);
                ((MainActivity) getActivity()).showToolBar(((MainActivity) getActivity()).tt);
                ((MainActivity) getActivity()).hieuUngChecked(((MainActivity) getActivity()).idMain);
                ((MainActivity) getActivity()).IDmenu = ((MainActivity) getActivity()).idMain;
            }
        });
    }
}