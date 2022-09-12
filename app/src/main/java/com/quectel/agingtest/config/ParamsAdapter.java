package com.quectel.agingtest.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quectel.agingtest.R;
import com.quectel.agingtest.common.CasesManager;
import com.quectel.agingtest.common.ItemParams;
import com.quectel.agingtest.common.Utils;

import java.util.List;


public class ParamsAdapter extends RecyclerView.Adapter<ParamsAdapter.ConfigViewHolder> {

    private List<ItemParams> datas;
    private Context mContext;

    public ParamsAdapter(Context context, List<ItemParams> params) {
        this.datas = params;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ConfigViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_config, parent, false);
        return new ConfigViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ConfigViewHolder holder, int position) {
        ItemParams params = datas.get(position);
        holder.tvName.setText(params.name);
        String val = CasesManager.getInstance(this.mContext).getParam(params.key);
        holder.etValue.setHint(params.desc);
        holder.etValue.setText(val);
        holder.btnSave.setOnClickListener(view -> {
            onSave(params.key, holder.etValue.getText().toString());
        });
        holder.btnSave.setTag(position);
    }

    private void onSave(String key, String val) {
        CasesManager.getInstance(this.mContext).saveParam(key, val);
        Toast.makeText(this.mContext, "保存成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return datas != null ? datas.size() : 0;
    }

    public class ConfigViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public EditText etValue;
        public Button btnSave;

        public ConfigViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            etValue = itemView.findViewById(R.id.et_value);
            btnSave = itemView.findViewById(R.id.btn_save);
        }
    }
}
