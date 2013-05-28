package ie.dcu.easyorderfyp;

import android.os.Parcel;
import android.os.Parcelable;

public class Order implements Parcelable {

	int table_num;
	String item_id;
	String item_name;
	int item_quantity;
	double item_price;
	String date;

	public Order(int a, String b, String c, int d, double e, String f) {
		table_num = a;
		item_id = b;
		item_name = c;
		item_quantity = d;
		item_price = e;
		date = f;
	}

	Order() {
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getItemName() {
		return item_name;
	}

	public void setItemName(String item_name) {
		this.item_name = item_name;
	}

	public String getItemId() {
		return item_id;
	}

	public void setId(String id) {
		this.item_id = id;
	}

	public int getQuantity() {
		return item_quantity;
	}

	public void setQuantity(int quantity) {
		this.item_quantity = quantity;
	}

	public double getPrice() {
		return item_price;
	}

	public void setPrice(double price) {
		this.item_price = price;
	}

	public int getTableNum() {
		return table_num;
	}

	public void setTableNum(int table_num) {
		this.table_num = table_num;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	private Order(Parcel in) {
		// This order must match the order in writeToParcel()
		table_num = in.readInt();
		item_id = in.readString();
		item_name = in.readString();
		item_quantity = in.readInt();
		item_price = in.readDouble();
		// Continue doing this for the rest of your member data
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(table_num);
		dest.writeString(item_id);
		dest.writeString(item_name);
		dest.writeInt(item_quantity);
		dest.writeDouble(item_price);
		dest.writeString(date);
	}

	public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
		public Order createFromParcel(Parcel in) {
			return new Order(in);
		}

		public Order[] newArray(int size) {
			return new Order[size];
		}
	};

	@Override
	public boolean equals(Object o) {
		Order m = (Order) o;

		if (!m.item_name.equals(item_name)) {
			return false;
		}

		if (!m.date.equals(date)) {
			return false;
		}

		if (!m.item_id.equals(item_id)) {
			return false;
		}
		return true;
	}

}