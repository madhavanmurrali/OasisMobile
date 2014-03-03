package com.oasis.android;

import java.util.ArrayList;

import com.oasis.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * for representing the list of GTodo Lists
 *
 */
public class ListArrayAdapter extends ArrayAdapter<List> {
  private final Context context;
  private final ArrayList<List> values;

  public ListArrayAdapter(Context context, ArrayList<List> listItems) {
    super(context, R.layout.list_item, listItems);
    this.context = context;
    this.values = listItems;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    // for the view with the name from the list object
	LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    
	View rowView = inflater.inflate(R.layout.list_item, parent, false);
    TextView textView = (TextView) rowView.findViewById(R.id.listItemListLabel);
    TextView totalTextView = (TextView) rowView.findViewById(R.id.listItemNumberOfItemsLabel);
    
    
    List currentList = values.get(position);
    textView.setText( currentList.getName());
    if(currentList.getNumberOfItems() !=0){
    	totalTextView.setText("(" + currentList.getNumberOfItems() + ")");
    }else{
    	totalTextView.setText("");
    }
    
    return rowView;
  }
}
