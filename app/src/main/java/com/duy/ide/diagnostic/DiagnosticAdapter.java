package com.duy.ide.diagnostic;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.ide.common.blame.Message;
import com.android.ide.common.blame.SourceFile;
import com.android.ide.common.blame.SourceFilePosition;
import com.android.ide.common.blame.SourcePosition;
import com.duy.ide.R;
import com.duy.ide.themefont.fonts.FontManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duy on 19/07/2017.
 */

public class DiagnosticAdapter extends RecyclerView.Adapter<DiagnosticAdapter.ErrorHolder> {

    private List<Message> mDiagnostics = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflater;
    private OnItemClickListener listener;

    public DiagnosticAdapter(Context context, @Nullable List<Message> diagnostics) {
        this.mContext = context;
        if (diagnostics != null) {
            this.mDiagnostics = diagnostics;
        }
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public ErrorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_compile_msg, parent, false);
        return new ErrorHolder(view);
    }

    @Override
    public void onBindViewHolder(ErrorHolder holder, int position) {
        final Message message = mDiagnostics.get(position);

        SourceFilePosition sourceFilePosition = message.getSourceFilePositions().get(0);

        //line:col
        SourcePosition sourcePosition = sourceFilePosition.getPosition();
        holder.txtPosition.setText(String.valueOf(sourcePosition.getStartLine()));
        if (sourcePosition.getStartColumn() >= 0) {
            holder.txtPosition.append(":" + sourcePosition.getStartColumn());
        }

        SourceFile file = sourceFilePosition.getFile();
        if (file != null) {
            holder.txtFile.setText(file.getSourceFile().getName());
        } else {
            holder.txtFile.setText("");
        }

        switch (message.getKind()) {
            case ERROR:
                holder.icon.setImageResource(R.drawable.ic_error_red);
                break;
            case WARNING:
                holder.icon.setImageResource(R.drawable.ic_warning_yellow);
                break;
            default:
                holder.icon.setImageResource(R.drawable.ic_warning_yellow);
                break;
        }
        holder.txtMessage.setTypeface(FontManager.getFontFromAsset(mContext, "Roboto-Light.ttf"));
        holder.txtMessage.setText(message.getText());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) listener.onClick(message);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDiagnostics.size();
    }

    @UiThread
    public void clear() {
        mDiagnostics.clear();
        notifyDataSetChanged();
    }

    @UiThread
    public void addAll(List<Message> diagnostics) {
        this.mDiagnostics.addAll(diagnostics);
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public interface OnItemClickListener {
        void onClick(Message diagnostic);
    }

    public static class ErrorHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView txtMessage, txtPosition, txtFile;
        View root;

        public ErrorHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.img_icon);
            txtMessage = itemView.findViewById(R.id.txt_message);
            txtPosition = itemView.findViewById(R.id.txt_postion);
            txtFile = itemView.findViewById(R.id.txt_file);
            root = itemView.findViewById(R.id.container);
        }
    }
}
