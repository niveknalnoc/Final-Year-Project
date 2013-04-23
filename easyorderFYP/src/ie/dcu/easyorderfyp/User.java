package ie.dcu.easyorderfyp;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{

	int id;
	String username;
	String password;
	
	public User(int a, String b, String c) {
		id = a;
		username = b;
		password = c;
	}
	
	User()
	{}
	
	public String getUserName() {
		return username;
	}
	
	public void setUserName(String user_name) {
		 this.username = user_name;
	}
	
	public int getItemId() {
		return id;
	}
	
	public void setId(int ID) {
		 this.id = ID;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		 this.password = password;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private User(Parcel in) {
        // This order must match the order in writeToParcel()
		id = in.readInt();
        username = in.readString();
        password = in.readString();
        // Continue doing this for the rest of your member data
    }

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(id);
		dest.writeString(username);
		dest.writeString(password);
	}
	
	public static final Parcelable.Creator<User> CREATOR
    = new Parcelable.Creator<User>() 
   {
         public User createFromParcel(Parcel in) 
         {
             return new User(in);
         }

         public User[] newArray (int size) 
         {
             return new User[size];
         }
    };
    
    @Override
    public boolean equals(Object o) {
		User m = (User)o;
		
		if(!m.username.equals(username)) {
			return false;
		}
		
		if(!m.password.equals(password)) {
			return false;
		}
		return true;
	}
    
}