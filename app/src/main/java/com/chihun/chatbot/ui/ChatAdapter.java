package com.chihun.chatbot.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chihun.chatbot.R;
import com.chihun.chatbot.data.model.ChatMessage;
import com.chihun.chatbot.data.model.MessageType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private static final int VIEW_TYPE_USER = 0;
    private static final int VIEW_TYPE_BOT = 1;
    private static final int VIEW_TYPE_TEMI = 2;

    private List<ChatMessage> messageList;

    public ChatAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messageList = messages;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messageList != null ? messageList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messageList.get(position);
        if (message.isUser()) {
            return VIEW_TYPE_USER;
        } else {
            return message.getType() == MessageType.COMMAND ? VIEW_TYPE_TEMI : VIEW_TYPE_BOT;
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_USER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_user, parent, false);
        } else if (viewType == VIEW_TYPE_TEMI) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_temi, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_bot, parent, false);
        }
        return new ChatViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        holder.messageText.setText(message.getText());

        // ✅ SimpleDateFormat 사용하여 날짜 포맷 적용
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(message.getTimestamp());
            String formattedTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
            holder.timestamp.setText(formattedTime);
        } catch (ParseException e) {
            Log.e("ChatAdapter", "시간 파싱 오류: " + message.getTimestamp(), e);
            holder.timestamp.setText("");
        }

        if (getItemViewType(position) == VIEW_TYPE_TEMI && holder.temiIcon != null) {
            holder.temiIcon.setVisibility(View.VISIBLE);
        }
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timestamp;
        ImageView temiIcon;

        public ChatViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timestamp = itemView.findViewById(R.id.timestamp);

            if (viewType == VIEW_TYPE_TEMI) {
                temiIcon = itemView.findViewById(R.id.temiIcon);
            }
        }
    }
}
