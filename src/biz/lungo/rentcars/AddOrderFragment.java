package biz.lungo.rentcars;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class AddOrderFragment extends FrontFragment implements OnTouchListener, OnClickListener {	
	private static final int START = 0;
	private static final int END = 1;
	EditText fieldName, fieldDateStart, fieldDateEnd;
	Button buttonAdd;
	Spinner spinnerCars;
	private String result;
	DatePicker dp;
	ArrayAdapter<CharSequence> adapter;
	RadioGroup radioGroupVip;
	
	public AddOrderFragment(int color, int resource) {
		super(color, resource);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout rl = new RelativeLayout(getActivity());
		rl = (RelativeLayout) RelativeLayout.inflate(getActivity(), super.resource, null);
		rl.setBackgroundColor(super.mColorRes);
		spinnerCars = (Spinner) rl.findViewById(R.id.spinnerCars);
		fieldName = (EditText) rl.findViewById(R.id.editTextName);
		fieldDateStart = (EditText) rl.findViewById(R.id.editTextDateStart);
		fieldDateEnd = (EditText) rl.findViewById(R.id.editTextDateEnd);	
		radioGroupVip = (RadioGroup) rl.findViewById(R.id.radioGroup1);
		fieldDateStart.setClickable(true);
		fieldDateStart.setInputType(InputType.TYPE_NULL);
		fieldDateStart.setOnTouchListener(this);
		fieldDateEnd.setClickable(true);
		fieldDateEnd.setInputType(InputType.TYPE_NULL);
		fieldDateEnd.setOnTouchListener(this);
		buttonAdd = (Button) rl.findViewById(R.id.buttonAdd);
		buttonAdd.setOnClickListener(this);
		String array_cars[] = fillArrayCars();
		adapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_dropdown_item);
		adapter.addAll(array_cars);
		spinnerCars.setAdapter(adapter);
		return rl;
	}

	private String[] fillArrayCars() {
		String statementSelect = "SELECT * FROM cars;";
		Cursor c = MainActivity.db.rawQuery(statementSelect, null);
		int rowCount = c.getCount();
		String result[] = new String[rowCount];
		if(c.moveToFirst()){
			for (int i = 0; i < rowCount; i++){			
				result[i] = c.getString(1);
				c.moveToNext();
			}
		}		
		return result;
	}


	@Override
	public boolean onTouch(View v, MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_DOWN){
			switch (v.getId()){
			case R.id.editTextDateStart:
				showDateDialog(START, getResources().getString(R.string.calendar_start));
				break;
			case R.id.editTextDateEnd:
				showDateDialog(END, getResources().getString(R.string.calendar_end));
				break;
			}
		}
		return false;
	}


	private void showDateDialog(int state, String title) {
		final int curstate = state;
		dp = new DatePicker(getActivity());
		dp.setCalendarViewShown(false);	
		AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
		ab.setView(dp);
		ab.setCancelable(false);
		ab.setTitle(title);
		ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				result = ((dp.getDayOfMonth() < 10)?("0" + dp.getDayOfMonth()):dp.getDayOfMonth()) + "." 
						+ (((dp.getMonth() + 1) < 10)?("0" + (dp.getMonth() + 1)):(dp.getMonth() + 1)) + "." 
						+ dp.getYear();
				if (curstate == START)
					fieldDateStart.setText(result);
				else if (curstate == END)
					fieldDateEnd.setText(result);
			}
		});
		
		ab.setNegativeButton(android.R.string.cancel, null);
		ab.show();
	}


	@Override
	public void onClick(View v) {
		String carModel = spinnerCars.getSelectedItem().toString();
		Cursor cursor = MainActivity.db.rawQuery("SELECT * FROM cars WHERE _model = '" + carModel + "';", null);
		//String carLicensePlate = cursor.getString(2);
		String carLicensePlate = "FFF";
		String clientName = fieldName.getText().toString();
		long startDate = getDate(START);
		long endDate = getDate(END);
		int isVip = isVip();
		String queryAdd = "INSERT INTO orders(_car_model, _license_plate, _client_name, _start_date, _end_date, _is_vip) VALUES('" 
							+ spinnerCars.getSelectedItem().toString() + "', '" 
							+ carLicensePlate + "', '" 
							+ clientName + "', "
							+ startDate + ", "
							+ endDate + ", "
							+ isVip + ");";
		MainActivity.db.execSQL(queryAdd);
		getDBContent();
	}


	private void getDBContent() {
		String statementSelect = "SELECT * FROM orders;";
		StringBuilder sb = new StringBuilder();
		Cursor c = MainActivity.db.rawQuery(statementSelect, null);
		int columns = c.getColumnCount();
		if (c.moveToFirst()){
			while(!c.isAfterLast()){
				for (int i = 0; i < columns; i++){
					sb.append(c.getString(i) + " ");
				}
				sb.append("\n");
				c.moveToNext();
			}
		}
		Toast.makeText(getActivity(), sb.toString(), Toast.LENGTH_SHORT).show();		
	}


	private int isVip() {
		int result = 0;
		if (radioGroupVip.getCheckedRadioButtonId() == R.id.radio0){
			result = 1;
		}
		return result;
	}


	private long getDate(int i){
		SimpleDateFormat sdf = new SimpleDateFormat("dd.mm.yyyy");
		Date date = null;
		switch(i){
		case START:
			try {
				date = sdf.parse(fieldDateStart.getText().toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			break;
		case END:
			try {
				date = sdf.parse(fieldDateEnd.getText().toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			break;
		}		
		return date.getTime();
	}

}
