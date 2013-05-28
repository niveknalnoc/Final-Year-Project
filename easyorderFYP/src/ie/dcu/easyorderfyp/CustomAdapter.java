package ie.dcu.easyorderfyp;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {
	// STORE CONTEXT
	private LayoutInflater inflater;
	// STORE RESOURCE
	private int resource;
	// REF TO DATA
	private List<Order> data;

	/**
	 * Default constructor. Creates the new Adaptor object to provide a ListView
	 * with data.
	 * 
	 * @param context
	 * @param resource
	 * @param items
	 */
	public CustomAdapter(Context context, int resource, List<Order> items) {
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.resource = resource;
		this.data = items;
	}

	/**
	 * Return the size of the data set.
	 */
	public int getCount() {
		return this.data.size();
	}

	/**
	 * Return an object in the data set.
	 */
	public Object getItem(int position) {
		return this.data.get(position);
	}

	/**
	 * Return the position provided.
	 */
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Return a generated view for a position.
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;

		if (convertView == null) {
			view = this.inflater.inflate(resource, parent, false);
		} else {
			view = convertView;
		}

		// BIND TO VIEW OBJECT
		return this.bindData(view, position);
	}

	/**
	 * Bind the provided data to the view. This is the only method not required
	 * by base adapter.
	 */
	public View bindData(View view, int position) {
		if (this.data.get(position) == null) {
			return view;
		}

		// GET OBJECT
		Order item = this.data.get(position);

		TextView tv;
		View viewElement;

		viewElement = view.findViewById(R.id.name);
		tv = (TextView) viewElement;
		tv.setText(item.item_name);

		viewElement = view.findViewById(R.id.quantity);
		tv = (TextView) viewElement;
		String q = Integer.toString(item.item_quantity);
		tv.setText(q);

		viewElement = view.findViewById(R.id.price);
		tv = (TextView) viewElement;
		String p = Double.toString(item.item_price);
		tv.setText(p);

		return view;
	}
}
