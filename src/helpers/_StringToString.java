package helpers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class _StringToString {
		private final StringProperty key;
		private final StringProperty val;
		private _StringToString(String key, String val)
		{
			this.key = new SimpleStringProperty(key);
			this.val = new SimpleStringProperty(val);
		}
		public void setKey(String value){key.set(value);}
		public String getKey(){return key.get();}
		public void setVal(String value){val.set(value);}
		public String getVal(){return val.get();}
}
