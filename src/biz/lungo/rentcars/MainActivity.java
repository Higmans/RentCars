package biz.lungo.rentcars;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends BaseActivity {

    private Fragment mContent;
    static SQLiteDatabase db;

    public MainActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        if (mContent == null)
            mContent = new AddOrderFragment(android.R.color.white, R.layout.add_order_frame);
        new Thread(new Runnable() {
			@Override
			public void run() {
				dbInit();	
			}
		}).run();
        
        setContentView(R.layout.content_frame);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, mContent)
                .commit();

        setBehindContentView(R.layout.menu_frame);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_frame, new MenuFragment())
                .commit();

        getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
    }

    protected void dbInit() {
    	db = openOrCreateDatabase("main.db", MODE_PRIVATE, null);
    	String queryCreateTableCars = "CREATE TABLE IF NOT EXISTS cars(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, _model TEXT, _license_plate TEXT);";
    	String queryCreateTableOrders = "CREATE TABLE IF NOT EXISTS orders(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, _car_model TEXT, _license_plate TEXT, _client_name TEXT, _start_date INTEGER, _end_date INTEGER, _is_vip INTEGER);";
    	db.execSQL(queryCreateTableCars);
    	db.execSQL(queryCreateTableOrders);
    	String carModels[] = getResources().getStringArray(R.array.cars);
    	String carLicensePlates[] = getResources().getStringArray(R.array.licensePlates);
    	for (int i = 0; i < carModels.length; i++){
    		String query = "INSERT INTO cars (_model, _license_plate) VALUES(" + "'" + carModels[i] + "', '" + carLicensePlates[i] + "');";
    		db.execSQL(query);
    	}
		//getDBcontent();
	}

	private void getDBcontent() {
		String statementSelect = "SELECT * FROM cars;";
		StringBuilder sb = new StringBuilder();
		Cursor c = db.rawQuery(statementSelect, null);
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
		Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
	}

	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "mContent", mContent);
    }

    public void switchContent(Fragment fragment) {
        mContent = fragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
        getSlidingMenu().showContent();
    }
    @Override
    protected void onDestroy() {
    	String dropQuery = "DROP TABLE IF EXISTS cars;";
    	db.execSQL(dropQuery);
    	String dropQuery1 = "DROP TABLE IF EXISTS orders;";
    	db.execSQL(dropQuery1);
    	super.onDestroy();
    }

}
