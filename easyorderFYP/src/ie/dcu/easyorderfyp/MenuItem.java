package ie.dcu.easyorderfyp;

import android.os.Parcel;
import android.os.Parcelable;

public class MenuItem implements Parcelable{

	String itemIdentifier;
	String itemName;
	double price;
	
	public MenuItem(String a, String b, double c) {
		itemIdentifier = a;
		itemName = b;
		price = c;
	}
	
	MenuItem()
	{}
	
	public String getItemName() {
		return itemName;
	}
	
	public void setItemName(String item_name) {
		 this.itemName = item_name;
	}
	
	public String getItemIdentifier() {
		return itemIdentifier;
	}
	
	public void setId(String ID) {
		 this.itemIdentifier = ID;
	}
	
	public double getPrice() {
		return price;
	}
	
	public void setPrice(double Price) {
		 this.price = Price;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private MenuItem(Parcel in) {
        // This order must match the order in writeToParcel()
		itemIdentifier = in.readString();
        itemName = in.readString();
        price = in.readDouble();
        // Continue doing this for the rest of your member data
    }

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(itemIdentifier);
		dest.writeString(itemName);
		dest.writeDouble(price);
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
		
		if(!m.itemIdentifier.equals(itemIdentifier)) {
			return false;
		}
		return true;
	}
	
}