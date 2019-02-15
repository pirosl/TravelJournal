package com.lucianpiros.traveljournal.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.DataCache;
import com.lucianpiros.traveljournal.model.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.os.ConfigurationCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Calendar fragment. Displays a calendar, each date will have a note icon under it if there is any note
 * created within that particular day.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class CalendarFragment extends Fragment {

    @BindView(R.id.calendar)
    CalendarView calendarCV;
    @BindView(R.id.fragment_layout)
    ConstraintLayout fragmentLayout;

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

        View calendarView = inflater.inflate(R.layout.fragment_calendar, container, false);

        ButterKnife.bind(this, calendarView);

        new SetupCalendarTask().execute("");

        return calendarView;
    }

    /**
     * AsyncTask class used to fill the notes icon under dates within which notes were created
     *
     * @author Lucian Piros
     * @version 1.0
     */
    private class SetupCalendarTask extends AsyncTask<String, Void, String> {

        private List<EventDay> eventDays;

        protected SetupCalendarTask() {
            eventDays = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... params) {
            List<Note> notes = DataCache.getInstance().getNotesList();
            if(notes != null) {
                for (Note note : notes) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(note.getNoteCreationDate());
                    EventDay eventDay = new EventDay(calendar, R.drawable.ic_notes);

                    eventDays.add(eventDay);
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            // set events days
            calendarCV.setEvents(eventDays);
            calendarCV.setOnDayClickListener(new OnDayClickListener() {
                @Override
                public void onDayClick(EventDay eventDay) {
                    if(eventDays.contains(eventDay)) {

                    }
                    else {
                        Calendar calendar = eventDay.getCalendar();
                        SimpleDateFormat dateSF = new SimpleDateFormat(getString(R.string.note_dateformat), ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0));
                        String message = String.format(getResources().getString(R.string.noaddednot_ondate), dateSF.format(calendar.getTime()));

                        Snackbar.make(fragmentLayout, message, Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
