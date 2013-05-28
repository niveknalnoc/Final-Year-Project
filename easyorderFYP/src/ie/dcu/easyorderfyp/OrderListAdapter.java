package ie.dcu.easyorderfyp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class OrderListAdapter extends ArrayAdapter<MenuItem> {

	private Context context;
	private int layoutResourceId;
	private List<MenuItem> data = null;
	private List<Integer> quantities = null;

	/**
	 * Constructor for a brand new order, calls the main constructor with a new
	 * list
	 * 
	 * @param context
	 * @param layoutResourceId
	 */
	public OrderListAdapter(Context context, int layoutResourceId) {
		this(context, layoutResourceId, new ArrayList<MenuItem>(),
				new ArrayList<Integer>());
	}

	/**
	 * Constructor for any order, built from a List of OrderItems and quantities
	 * 
	 * @param context
	 * @param layoutResourceId
	 * @param data
	 */
	public OrderListAdapter(Context context, int layoutResourceId,
			List<MenuItem> data, List<Integer> quantities) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;

		this.data = data;
		this.quantities = quantities;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		OrderItemHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new OrderItemHolder();
			holder.name = (TextView) row
					.findViewById(R.id.order_item_list_name);
			holder.quantity = (Button) row
					.findViewById(R.id.order_item_list_quantity);
			holder.price = (TextView) row
					.findViewById(R.id.order_item_list_price);

			row.setTag(holder);
		} else {
			holder = (OrderItemHolder) row.getTag();
		}

		MenuItem item = data.get(position);
		holder.name.setText(item.getItemName());
		holder.quantity.setText("" + quantities.get(position) + " x");
		Double price = item.getPrice();
		String priceToString = String.valueOf(price);
		holder.price.setText(priceToString);

		// set up quantity button
		final int tempPosition = position;
		final Context tempContext = context;
		
		holder.quantity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				FragmentActivity tempActivity = (FragmentActivity) tempContext;
				QuantityDialog.newInstance(quantities.get(tempPosition),
						tempPosition)
						.show(tempActivity.getSupportFragmentManager(),
								"QUAN_DIALOG");
				
			}
		});

		return row;
	}

	/**
	 * Called to add an item to the list
	 * 
	 * @param item
	 */
	public void addOrderItem(MenuItem item) {
		quantities.add(Integer.valueOf(1));
		data.add(item);
		this.notifyDataSetChanged();
	}

	/**
	 * Called to change a quantity
	 * 
	 * @param index
	 * @param quan
	 */
	public void updateQuantity(int index, int quan) {
		if (quan == 0) {
			// remove item from order
			quantities.remove(index);
			data.remove(index);
		} else {
			// else update with new quantity
			quantities.set(index, quan);
		}
		this.notifyDataSetChanged();
	}

	/**
	 * Converts the order into a string passable to the WebAPI
	 * 
	 * @return
	 */
	public String getOrderString() throws Exception {
		String order = "";

		if (data.size() == 0) {
			throw new Exception();
		}

		for (int i = 0; i < data.size(); i++) {
			order += data.get(i).getItemIdentifier();
			order += ":";
			order += data.get(i).getItemName();
			order += ":";
			order += quantities.get(i);
			order += ":";
			order += data.get(i).getPrice();
			order += ",";
		}

		return order.substring(0, order.length() - 1);
	}

	private static class OrderItemHolder {
		TextView name;
		Button quantity;
		TextView price;
	}
}
