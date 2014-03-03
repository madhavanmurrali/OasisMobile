package com.oasis.android;

import java.util.ArrayList;

import com.oasis.android.R;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Custom adapter for Tasks ListView
 * 
 * @author Pujita
 *
 */
public class TaskListArrayAdapter extends ArrayAdapter<Task> {
  private final Context context;
  private final ArrayList<Task> values;
  private Task currentTask;
  private int listNumber;

  public TaskListArrayAdapter(Context context, ArrayList<Task> values, int listNumber) {
    super(context, R.layout.task_item, values);
    this.context = context;
    this.values = values;
    this.listNumber = listNumber;
    
  }

  @Override
  /**
   * Sets the right content in the for each task item
   */
  public View getView(int position, View convertView, ViewGroup parent) {
    
	LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    
	View rowView = inflater.inflate(R.layout.task_item, parent, false);
	
	// Get all required views from layout
	LinearLayout layout = (LinearLayout) rowView.findViewById(R.id.listItemLayout);
    TextView textView = (TextView) rowView.findViewById(R.id.listItemTaskLabel);
    TextView dateTextView = (TextView) rowView.findViewById(R.id.listItemDueDateLabel);
    ImageView imageView = (ImageView) rowView.findViewById(R.id.listItemIcon);
    CheckBox checkbox = (CheckBox) rowView.findViewById(R.id.listItemCheckBox);
    checkbox.setTag(position);
    layout.setTag(position);
    
    currentTask = values.get(position);
    textView.setText(currentTask.getName());
	dateTextView.setText(currentTask.getDueDateStr());
    
	
	
	
    // Set checkbox
	if (currentTask.isCompletedStatus()) {
    	checkbox.setChecked(true);
    } else {
    	checkbox.setChecked(false);
    }
    
    // Set icon
	if (currentTask.getPriority() == Task.PRIORITY_LOW) {
	    imageView.setImageResource(R.drawable.flag_yellow);
	} else if (currentTask.getPriority() == Task.PRIORITY_HIGH) {
	   	imageView.setImageResource(R.drawable.flag_red);
	}
		
    if (currentTask.isMissed()){
    	imageView.setImageResource(R.drawable.alerts_and_states_warning);
    }
    
    // Setting onClick and onLongClick listener for each list item to open the task description page;
    layout.setOnClickListener(new OnClickListener(){
	//	@Override
		public void onClick(View view) {
			int position = (Integer) view.getTag();
			SessionManager sm = ((Tasks) context).sm;
			sm.setCurrentTaskDetails(values.get(position), (Tasks)context);
			((Tasks) context).startActivity(new Intent(((Tasks) context), TaskDescription.class));		
		}
    	
    });
    
    layout.setOnLongClickListener(new OnLongClickListener(){
	//	@Override
		public boolean onLongClick(View view) {
			int position = (Integer) view.getTag();
			((Tasks) context).registerForContextMenu(view);
			((Tasks) context).openContextMenu(view);
			((Tasks) context).unregisterForContextMenu(view);
			((Tasks) context).setCurrentTaskIndex(position);
			((Tasks) context).setCurrentListNumber(listNumber);
			SessionManager sm = ((Tasks) context).sm;
			sm.setCurrentTaskDetails(values.get(position), (Tasks)context);
			return true;
		}   	
    });
    
    // Setting the onChange listener for the checkboxes
    checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    	
  //    @Override
      public void onCheckedChanged(CompoundButton buttonView,
          boolean isChecked) {
        if(isChecked){
        	int position = (Integer) buttonView.getTag();
        	((Tasks) context).checkTask(position);     	
        } else {
        	int position = (Integer) buttonView.getTag();
        	((Tasks) context).uncheckTask(position);
        }
      }
    });

    return rowView;
  }
}
