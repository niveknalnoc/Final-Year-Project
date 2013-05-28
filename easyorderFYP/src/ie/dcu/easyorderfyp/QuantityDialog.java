package ie.dcu.easyorderfyp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.NumberPicker;

public class QuantityDialog extends DialogFragment {
	private QuanityChangeListener callback;

	public interface QuanityChangeListener {
		public void newQuantity(int index, int quan);
	}

	public static QuantityDialog newInstance(int currentQuan, int itemIndex) {
		QuantityDialog instance = new QuantityDialog();

		Bundle args = new Bundle();
		args.putInt("quantity", currentQuan);
		args.putInt("index", itemIndex);
		instance.setArguments(args);
		return instance;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the
			// host
			callback = (QuanityChangeListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement QuanityChangeListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int quantity = getArguments().getInt("quantity");
		final int index = getArguments().getInt("index");
		int MAX_ITEM = 30;

		final NumberPicker np = new NumberPicker(getActivity());
		np.setMinValue(0);
		np.setMaxValue(MAX_ITEM);
		np.setValue(quantity);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.quantity_dialog_title)
				.setView(np)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						callback.newQuantity(index, np.getValue());
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});

		return builder.create();
	}
}
