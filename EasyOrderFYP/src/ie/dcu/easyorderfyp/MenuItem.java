package ie.dcu.easyorderfyp;

public class MenuItem {

	String id;
	String itemName;
	double price;
	int available;
	
	public MenuItem(String a, String b, double c, int d) {
		id = a;
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
	
	public String getId() {
		return id;
	}
	
	public void setId(String ID) {
		 this.id = ID;
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
	
}