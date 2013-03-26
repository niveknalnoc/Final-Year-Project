package ie.dcu.easyorderfyp;

import android.os.Parcel;
import android.os.Parcelable;

public class Order implements Parcelable{

	int tableNum;
	String itemId;
	String itemName;
	String itemPrice;
	int quantity;
	double price;
	
	public Order(int a, String b, String c, int d, double e) {
		tableNum = a;
		itemId = b;
		itemName = c;
		quantity = d;
		price = e;
	}
	
	Order()
	{}
	
	public int getTableNum() {
		return tableNum;
	}
	
	public void setTableNum(int tableNum) {
		 this.tableNum = tableNum;
	}
	
	public String itemId() {
		return itemId;
	}
	
	public void setItemId(String itemId) {
		 this.itemId = itemId;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public void setItemName(String itemName) {
		 this.itemName = itemName;
	}
	
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		 this.quantity = quantity;
	}
	
	public double getPrice() {
		return price;
	}
	
	public void setItemPrice(double price) {
		 this.price = price;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private Order(Parcel in) {
        // This order must match the order in writeToParcel()
		tableNum = in.readInt();
        itemId = in.readString();
        itemName = in.readString();
        quantity = in.readInt();
        price = in.readDouble();
        // Continue doing this for the rest of your member data
    }

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(tableNum);
		dest.writeString(itemId);
		dest.writeString(itemName);
		dest.writeInt(quantity);
		dest.writeDouble(price);
	}
	
	public static final Parcelable.Creator<Order> CREATOR
    = new Parcelable.Creator<Order>() 
   {
         public Order createFromParcel(Parcel in) 
         {
             return new Order(in);
         }

         public Order[] newArray (int size) 
         {
             return new Order[size];
         }
    };
	
}