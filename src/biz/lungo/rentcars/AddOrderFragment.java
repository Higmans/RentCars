package biz.lungo.rentcars;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;

public class AddOrderFragment extends FrontFragment implements OnTouchListener, OnClickListener {	
	private static final int START = 0;
	private static final int END = 1;
	EditText fieldName, fieldDateStart, fieldDateEnd;
	Button buttonAdd;
	Spinner spinnerCars;
	private String result;
	DatePicker dp;
	ArrayAdapter<CharSequence> adapter;
	
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
		fieldDateStart.setClickable(true);
		fieldDateStart.setInputType(InputType.TYPE_NULL);
		fieldDateStart.setOnTouchListener(this);
		fieldDateEnd.setClickable(true);
		fieldDateEnd.setInputType(InputType.TYPE_NULL);
		fieldDateEnd.setOnTouchListener(this);
		buttonAdd = (Button) rl.findViewById(R.id.buttonAdd);
		buttonAdd.setOnClickListener(this);
		adapter = ArrayAdapter.createFromResource(getActivity(), R.array.cars, android.R.layout.simple_spinner_dropdown_item);
		spinnerCars.setAdapter(adapter);
		return rl;
	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_DOWN){
			switch (v.getId()){
			case R.id.editTextDateStart:
				showDateDialog(START, "Выберите дату начала аренды");
				break;
			case R.id.editTextDateEnd:
				showDateDialog(END, "Выберите дату окончания аренды");
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
		String selectedCarModel = MainActivity.db.execSQL(sql)
		String queryAdd = "INSERT INTO orders(_car_model, _license_plate, _client_name, _start_date, _end_date, _is_vip) VALUES('" + spinnerCars.getSelectedItemPosition();
	}

}
