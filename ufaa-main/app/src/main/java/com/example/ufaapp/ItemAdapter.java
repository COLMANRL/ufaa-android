package com.example.ufaapp;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import com.example.ufaapp.model.Item;

import androidx.appcompat.app.WindowDecorActionBar;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Item> items;
    private int currentPage;
    private HashSet<Integer> selectedRows;
    public interface OnItemSelectionChangedListener {
        void onSelectionChanged(boolean isSelected, int globalPosition);
    }
    private OnItemSelectionChangedListener selectionListener;


    public ItemAdapter(List<Item> items,int currentPage, HashSet<Integer> selectedRows,OnItemSelectionChangedListener selectionListener) {
        this.items = items;
        this.currentPage = currentPage;
        this.selectedRows = selectedRows;
        this.selectionListener = selectionListener;
    }
    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_HEADER;
        return TYPE_ITEM;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }




    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            // Header view, no data to bind
            return;
        }

        int adjustedPosition = position - 1;
        Item item = items.get(adjustedPosition);
        int globalPosition = currentPage * 10 + adjustedPosition; // Adjust '10' if your page size is different
        holder.rowNumberTextView.setText(String.valueOf(globalPosition));


        holder.rowNumberTextView.setText(String.valueOf(adjustedPosition + 1));
        holder.nameTextView.setText(item.getName());
        holder.myidnumberTextView.setText(item.getMyidnumber());
        holder.holderTextView.setText(item.getHolder());
        holder.amountTextView.setText(item.getAmount());
        holder.boxTextView.setText(item.getBox());
        holder.statusTextView.setText(item.getStatus());

        // Note: Setting typeface each time in onBindViewHolder can be inefficient. Consider setting it elsewhere.
        holder.nameTextView.setTypeface(Typeface.DEFAULT_BOLD);
        holder.myidnumberTextView.setTypeface(Typeface.DEFAULT_BOLD);
        holder.holderTextView.setTypeface(Typeface.DEFAULT_BOLD);
        holder.amountTextView.setTypeface(Typeface.DEFAULT_BOLD);
        holder.boxTextView.setTypeface(Typeface.DEFAULT_BOLD);
        holder.statusTextView.setTypeface(Typeface.DEFAULT_BOLD);

        holder.checkBox.setChecked(item.isSelected());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //int globalPosition = currentPage * 10 + position - 1; // Adjust according to your paging logic
            item.setSelected(isChecked);
            selectionListener.onSelectionChanged(isChecked, globalPosition);
        });
    }

    @Override
    public int getItemCount() {
        return items.size() + 1; // +1 for the header
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox; // Will be null for header
        TextView nameTextView; // Will be null for header if not present in header_layout.xml
        TextView holderTextView; // Will be null for header if not present in header_layout.xml
        TextView amountTextView;
        TextView boxTextView;
        TextView myidnumberTextView;
        TextView statusTextView;
        TextView rowNumberTextView; // TextView for row number


        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            rowNumberTextView = itemView.findViewById(R.id.rowNumberTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            myidnumberTextView = itemView.findViewById(R.id.myidnumberTextView);
            holderTextView = itemView.findViewById(R.id.holderTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            boxTextView = itemView.findViewById(R.id.boxTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);

        }
    }

}

