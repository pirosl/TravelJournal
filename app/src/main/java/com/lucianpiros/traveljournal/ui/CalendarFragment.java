package com.lucianpiros.traveljournal.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.lucianpiros.traveljournal.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CalendarFragment extends Fragment {

    @BindView(R.id.calendar)
    CalendarView calendarCV;

    private List<EventDay> eventDays = new ArrayList<>();

    public CalendarFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //this.layoutInflater = inflater;
        //this.viewGroup = container;

        View calendarView = inflater.inflate(R.layout.fragment_calendar, container, false);

        ButterKnife.bind(this, calendarView);

        Calendar calendar = new GregorianCalendar(2019,01,21);
        //calendarCV.setDate(calendar);
        EventDay eventDay = new EventDay(calendar, R.drawable.ic_notes);
        eventDays.add(eventDay);
        calendarCV.setEvents(eventDays);

        // Retrieve recipe idx passed as parameter from Main Activity
        /*Bundle bundle = getArguments();

        noteIdx = NOTSELECTED_IDX;
        if(bundle != null)
            noteIdx = bundle.getInt(getResources()
                    .getString(R.string.noteactivity_extra_param));
*/
        return calendarView;
    }
}
