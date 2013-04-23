package ie.dcu.easyorderfyp;

import android.os.Parcel;
import android.os.Parcelable;

public class MenuItem implements Parcelable{

	String itemId;
	String itemName;
	double price;
	int available;
	
	public MenuItem(String a, String b, double c, int d) {
		itemId = a;
		itemName = b;
		price = c;
		available = d;
	}
	
	MenuItem()
	{}
	
	public String getItemName() {
		return itemName;
	}
	
	public void setItemName(String item_name) {
		 this.itemName = item_name;
	}
	
	public String getItemId() {
		return itemId;
	}
	
	public void setId(String ID) {
		 this.itemId = ID;
	}
	
	public double getPrice() {
		return price;
	}
	
	public void setPrice(double Price) {
		 this.price = Price;
	}
	
	public int getAvailable() {
		return available;
	}

	public void setAvailable(int Available) {
		 this.available = Available;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private MenuItem(Parcel in) {
        // This order must match the order in writeToParcel()
		itemId = in.readString();
        itemName = in.readString();
        price = in.readDouble();
        available = in.readInt();
        // Continue doing this for the rest of your member data
    }

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(itemId);
		dest.writeString(itemName);
		dest.writeDouble(price);
		dest.writeInt(available);
	}
	
	public static final Parcelable.Creator<MenuItem> CREATOR
    = new Parcelable.Creator<MenuItem>() 
   {
         public MenuItem createFromParcel(Parcel in) 
         {
             return new MenuItem(in);
         }

         public MenuItem[] newArray (int size) 
         {
             return new MenuItem[size];
         }
    };
    
    @Override
    public boolean equals(Object o) {
		MenuItem m = (MenuItem)o;
		
		if(!m.itemId.equals(itemId)) {
			return false;
		}
		return true;
	}
	
}