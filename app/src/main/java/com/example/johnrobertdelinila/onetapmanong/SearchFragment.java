package com.example.johnrobertdelinila.onetapmanong;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    public Button button_plumber, btn_electric;
    private Service service_plumber, service_electric;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        service_plumber = new Service();
        initServicePlumber(service_plumber);
        button_plumber = view.findViewById(R.id.btn_plumber);
        button_plumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("service", service_plumber);
                startActivity(intent);
            }
        });

        service_electric = new Service();
        initServiceElectric(service_electric);
        btn_electric = view.findViewById(R.id.btn_electric);
        btn_electric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("service", service_electric);
                startActivity(intent);
            }
        });

        return view;

    }

    private void initServiceElectric(Service service) {
        ArrayList<String> title = new ArrayList<>();
        title.add("What wiring work do you need?");
        title.add("What's the wiring?");
        title.add("Please describe the electrical work");
        title.add("When do you need it?");

        ArrayList<String> subTitle = new ArrayList<>();
        subTitle.add("");
        subTitle.add("");
        subTitle.add("Optional");
        subTitle.add("");

        ArrayList<Boolean> isInput = new ArrayList<>();
        isInput.add(false);
        isInput.add(true);
        isInput.add(false);
        isInput.add(false);

        ArrayList<Integer> viewTypes = new ArrayList<>();
        viewTypes.add(1);
        viewTypes.add(1);
        viewTypes.add(3);
        viewTypes.add(4);

        ArrayList<ArrayList<String>> answers = new ArrayList<>();

        ArrayList<String> firstTitle = new ArrayList<>();
        firstTitle.add("Install");
        firstTitle.add("Repair / Replace");
        firstTitle.add("Relocate / Move");
        firstTitle.add("Inspection");

        ArrayList<String> secondTitle = new ArrayList<>();
        secondTitle.add("Air conditioner / Water heater");
        secondTitle.add("Fan / Lightning");

        answers.add(firstTitle);
        answers.add(secondTitle);
        answers.add(null);
        answers.add(null);

        service.setServiceName("Electrical Wiring");
        service.setTitle(title);
        service.setSubtitle(subTitle);
        service.setViewTypes(viewTypes);
        service.setIsInput(isInput);
        service.setAnswers(answers);

    }

    private void initServicePlumber(Service service) {

        ArrayList<String> title = new ArrayList<>();
        title.add("What is the problem?");
        title.add("What fittings are affected?");
        title.add("Please describe the job in detail");
        title.add("When do you need it?");
        title.add("Attachments (Optional)");

        ArrayList<String> subtitle = new ArrayList<>();
        subtitle.add("");
        subtitle.add("");
        subtitle.add("Optional");
        subtitle.add("");
        subtitle.add("You may attach sample image of the design you like");

        ArrayList<Boolean> isInput = new ArrayList<>();
        isInput.add(true);
        isInput.add(true);
        isInput.add(false);
        isInput.add(false);
        isInput.add(false);

        ArrayList<Integer> viewTypes = new ArrayList<>();
        viewTypes.add(1);
        viewTypes.add(1);
        viewTypes.add(3);
        viewTypes.add(4);
        viewTypes.add(5);

        ArrayList<ArrayList<String>> answers = new ArrayList<>();

        // 1st title
        ArrayList<String> firstTitle = new ArrayList<>();
        firstTitle.add("Leaking / burst pipe");
        firstTitle.add("Clogged drain");
        firstTitle.add("Low water / no pressure");
        firstTitle.add("Fixture not flushing");
        firstTitle.add("I'm not sure");
        ArrayList<String> secondTitle = new ArrayList<>();
        secondTitle.add("Toilet bowl");
        secondTitle.add("Sink / basin");
        secondTitle.add("Shower head");
        secondTitle.add("Shower");
        secondTitle.add("Bathtub");
        secondTitle.add("Water pump");

        answers.add(firstTitle);
        answers.add(secondTitle);
        answers.add(null);
        answers.add(null);
        answers.add(null);

        service.setServiceName("Plumbing Repair");
        service.setTitle(title);
        service.setSubtitle(subtitle);
        service.setViewTypes(viewTypes);
        service.setIsInput(isInput);
        service.setAnswers(answers);

    }

}
