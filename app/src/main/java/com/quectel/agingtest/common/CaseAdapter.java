package com.quectel.agingtest.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quectel.agingtest.R;

import java.util.HashSet;
import java.util.List;


public class CaseAdapter extends RecyclerView.Adapter<CaseAdapter.CaseViewHolder> {

    private List<ItemCases> datas;
    private boolean isRunning = false;// 是否正在运行
    private final HashSet<String> casesSelected = new HashSet<>();// 选中case
    private String currentRunCases = "";
    private String notice = "";
    private Context mContext;

    public CaseAdapter(Context context, List<ItemCases> cases, boolean run, HashSet<String> selected) {
        this.mContext = context;
        this.datas = cases;
        this.isRunning = run;
        if (selected != null) {
            this.casesSelected.addAll(selected);
        }
    }

    @NonNull
    @Override
    public CaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_case, parent, false);
        return new CaseViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CaseViewHolder holder, int position) {
        ItemCases cases = datas.get(position);

        holder.setColor(getTextColor());
        holder.tvName.setText(cases.name);
        holder.tvDesc.setText(getCaseTypeText(cases.type, cases.time));
        holder.tvCheck.setChecked(isChecked(cases.id));
        holder.tvCheck.setClickable(!isRunning);

        holder.tvCheck.setOnCheckedChangeListener((compoundButton, b) -> {

            boolean res;
            if (b) {
                res = casesSelected.add(cases.id);
            } else res = casesSelected.remove(cases.id);
            System.out.println("b=" + b + ",res=" + res);
            try {
                if (res) {
                    notifyItemChanged(position);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        holder.tvStats.setText(isCasesRunning(cases.id) ? "进行中" + notice : "");
    }

    private String getCaseTypeText(int type, int time) {
        String desc = "";
        if (type == Utils.TYPE_NUMBER) {
            desc = time + "次";
        } else {
            int hour = time / 60;
            int m = time % 60;
            if (hour > 0) {
                desc = hour + "小时";
            }
            desc += m + "分钟";
        }
        return Utils.getCaseTypeName(type) + ":" + desc;
    }


    private boolean isChecked(String id) {
        return casesSelected.contains(id);
    }

    private boolean isCasesRunning(String id) {
        return isRunning && currentRunCases.equals(id);
    }

    private int getTextColor() {
        return mContext.getColor(isRunning ? R.color.text_abnormal : R.color.text_normal);
    }

    public HashSet<String> getCasesSelected() {
        return casesSelected;
    }

    @Override
    public int getItemCount() {
        return datas != null ? datas.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateStateRun(boolean run) {
        isRunning = run;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateSateCases(String runCases, String nt) {
        currentRunCases = runCases;
        notice = nt;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateReset() {
        isRunning = false;
        currentRunCases = "";
        casesSelected.clear();
        notifyDataSetChanged();
    }

    public class CaseViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout itemRoot;
        public TextView tvName;
        public TextView tvDesc;
        public CheckBox tvCheck;
        public TextView tvStats;

        public CaseViewHolder(@NonNull View itemView) {
            super(itemView);
            itemRoot = itemView.findViewById(R.id.ll_item_root);
            tvName = itemView.findViewById(R.id.tv_case_name);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvCheck = itemView.findViewById(R.id.tv_check);
            tvStats = itemView.findViewById(R.id.tv_status);
        }

        public void setColor(int resId) {
            tvName.setTextColor(resId);
            tvDesc.setTextColor(resId);
            tvStats.setTextColor(resId);
        }
    }
}
